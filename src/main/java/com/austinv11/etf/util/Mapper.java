package com.austinv11.etf.util;

import com.austinv11.etf.ETFConfig;
import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.erlang.ErlangMap;
import com.austinv11.etf.parsing.ETFParser;
import com.austinv11.etf.writing.ETFWriter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This represents a mapper which will handle object serialization/deserialization.
 */
public class Mapper {
	
	private ETFConfig config;
	private static sun.misc.Unsafe UNSAFE;
	
	static {
		try {
			Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			UNSAFE = (sun.misc.Unsafe) theUnsafe.get(null);
		} catch (Throwable e) {
			UNSAFE = null; //Unsafe unavailable
		}
	}
	
	public Mapper(ETFConfig config) {
		this.config = config;
	}
	
	private List<PropertyManager> findProperties(Object instance, Class clazz) {
		List<PropertyManager> properties = new ArrayList<>();
		for (Field field : clazz.getFields()) {
			if (!Modifier.isTransient(field.getModifiers())) {
				properties.add(new PropertyManager(instance, field));
			}
		}
		return properties;
	}
	
	private <T> T createInstance(Class<T> clazz) {
		if (UNSAFE != null) { //Unsafe available, use it to instantiate the class
			try {
				return (T) UNSAFE.allocateInstance(clazz);
			} catch (InstantiationException e) {}
		}
		
		//Fallback to reflection
		try {
			return clazz.getConstructor().newInstance();
		} catch (Exception e) {
			throw new ETFException(e);
		}
	}
	
	public <T> byte[] writeToMap(T obj) {
		ETFWriter writer = config.createWriter();
		Map<String, Object> properties = findProperties(obj, obj.getClass())
				.stream().filter(o -> {
					System.out.println(o);
					return true;
				}).collect(Collectors.toMap(PropertyManager::getName, PropertyManager::getValue));
		return writer.writeMap(properties).toBytes();
	}
	
	public <T> byte[] write(T obj) {
		ETFWriter writer = config.createWriter();
		findProperties(obj, obj.getClass()).forEach(writer::write);
		return writer.toBytes();
	}
	
	public <T> T read(ErlangMap data, Class<T> clazz) {
		T instance = createInstance(clazz);
		List<PropertyManager> properties = findProperties(instance, clazz);
		for (PropertyManager property : properties) {
			if (data.containsKey(property.getName())) {
				property.setValue(data.get(property.getName()));
			}
		}
		return instance;
	}
	
	public <T> T read(byte[] data, Class<T> clazz) {
		ETFParser parser = config.createParser(data);
		T instance = createInstance(clazz);
		List<PropertyManager> properties = findProperties(instance, clazz);
		if (parser.peek() == TermTypes.MAP_EXT) {
			ErlangMap map = parser.nextMap();
			if (Map.class.isAssignableFrom(clazz)) { //User wants a map so lets give it to them
				return (T) map;
			} else {
				return read(map, clazz);
			}
		} else {
			for (PropertyManager property : properties) {
				if (parser.isFinished())
					break;
				
				property.setValue(parser.next());
			}
			return instance;
		}
	}
	
	public interface IPropertyAccessor {
		
		Object get();
	}
	
	public interface IPropertyMutator {
		
		void set(Object o);
	}
	
	public static class FieldAccessorAndMutator implements IPropertyAccessor, IPropertyMutator {
		
		private final Object object;
		private final Field field;
		
		public FieldAccessorAndMutator(Object object, Field field) {
			this.object = object;
			this.field = field;
			this.field.setAccessible(true);
		}
		
		@Override
		public Object get() {
			try {
				return field.get(object);
			} catch (IllegalAccessException e) {
				throw new ETFException(e);
			}
		}
		
		@Override
		public void set(Object o) {
			try {
				field.set(object, o);
			} catch (IllegalAccessException e) {
				throw new ETFException(e);
			}
		}
	}
	
	public static class MethodAccessorAndMutator implements IPropertyAccessor, IPropertyMutator {
		
		private final Object object;
		private final Method method;
		
		public MethodAccessorAndMutator(Object object, Method method) {
			this.object = object;
			this.method = method;
			this.method.setAccessible(true);
		}
		
		@Override
		public Object get() {
			try {
				return method.invoke(object);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new ETFException(e);
			}
		}
		
		@Override
		public void set(Object o) {
			try {
				method.invoke(object, o);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new ETFException(e);
			}
		}
	}
	
	public static class NOPAccessorAndMutator implements IPropertyAccessor, IPropertyMutator {
		
		public static final NOPAccessorAndMutator INSTANCE = new NOPAccessorAndMutator();
		
		@Override
		public Object get() {
			return null;
		}
		
		@Override
		public void set(Object o) {}
	}
	
	public static class PropertyManager {
		
		private final Object instance;
		private final IPropertyMutator mutator;
		private final IPropertyAccessor accessor;
		private final String name;
		
		private static String capitalize(String s) {
			return s.substring(0, 1).toUpperCase() + (s.length() > 1 ? s.substring(1) : "");
		}
		
		public PropertyManager(Object instance, Field field) {
			this.instance = instance;
			field.setAccessible(true);
			IPropertyAccessor accessor = null;
			boolean isFinal = Modifier.isFinal(field.getModifiers());
			IPropertyMutator mutator = isFinal ? NOPAccessorAndMutator.INSTANCE : null;
			for (Method m : instance.getClass().getMethods()) {
				if (mutator != null && accessor != null)
					break;
				
				m.setAccessible(true);
				
				if (m.getName().equals(String.format("get%s", capitalize(field.getName())))) {
					accessor = new MethodAccessorAndMutator(instance, m);
					continue;
				} else if (!isFinal 
						&& m.getName().equals(String.format("set%s", capitalize(field.getName()))) 
						&& m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(field.getType())) {
					mutator = new MethodAccessorAndMutator(instance, m);
					continue;
				} else if (m.getDeclaredAnnotation(GetterMethod.class) != null) {
					if (m.getDeclaredAnnotation(GetterMethod.class).value().equals(field.getName())) {
						accessor = new MethodAccessorAndMutator(instance, m);
						continue;
					}
				} else if (!isFinal && m.getDeclaredAnnotation(SetterMethod.class) != null
						&& m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(field.getType())) {
					if (m.getDeclaredAnnotation(SetterMethod.class).value().equals(field.getName())) {
						mutator = new MethodAccessorAndMutator(instance, m);
						continue;
					}
				}
			}
			
			if (accessor == null)
				accessor = new FieldAccessorAndMutator(instance, field);
			
			if (mutator == null)
				mutator = new FieldAccessorAndMutator(instance, field);
			
			this.accessor = accessor;
			this.mutator = mutator;
			
			this.name = field.getName();
		}
		
		public void setValue(Object value) {
			mutator.set(value);
		}
		
		public Object getValue() {
			return accessor.get();
		}
		
		public String getName() {
			return name;
		}
	}
}
