package com.austinv11.etf.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Internal class for reflection utilities.
 */
public class ReflectionUtils {
	
	//Woo unsafe! Adds some extra speed when available.
	private static final Object UNSAFE;
	
	static {
		Object tempUnsafe = null;
		try {
			Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			tempUnsafe = theUnsafe.get(null);
		} catch (Throwable e) {
			//Unsafe unavailable
		}
		UNSAFE = tempUnsafe;
	}
	
	public static List<PropertyManager> findProperties(Object instance, Class clazz) {
		if (clazz.isPrimitive())
			return new ArrayList<>();
		
		List<PropertyManager> properties = new ArrayList<>();
		for (Field field : getAllFields(clazz)) {
			if (!Modifier.isTransient(field.getModifiers())) {
				properties.add(new PropertyManager(instance, field));
			}
		}
		return properties;
	}
	
	public static List<Field> getAllFields(Class clazz) {
		List<Field> fields = new ArrayList<>();
		Collections.addAll(fields, clazz.getDeclaredFields());
		Collections.addAll(fields, clazz.getFields());
		return fields;
	}
	
	public static List<Method> getAllMethods(Class clazz) {
		List<Method> methods = new ArrayList<>();
		Collections.addAll(methods, clazz.getDeclaredMethods());
		Collections.addAll(methods, clazz.getMethods());
		return methods;
	}
	
	public static <T> T createInstance(Class<T> clazz) {
		if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
			return (T)(Integer) 0;
		} else if (long.class.equals(clazz) || Long.class.equals(clazz)) {
			return (T)(Long) 0L;
		} else if (double.class.equals(clazz) || Double.class.equals(clazz)) {
			return (T)(Double) 0D;
		} else if (void.class.equals(clazz) || Void.class.equals(clazz)) {
			return null;
		} else if (float.class.equals(clazz) || Float.class.equals(clazz)) {
			return (T)(Float) 0F;
		} else if (byte.class.equals(clazz) || Byte.class.equals(clazz)) {
			return (T)(Byte)(byte) 0;
		} else if (char.class.equals(clazz) || Character.class.equals(clazz)) {
			return (T)(Character)(char) 0;
		} else if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
			return (T)(Boolean) false;
		} else if (short.class.equals(clazz) || Short.class.equals(clazz)) {
			return (T)(Short)(short) 0;
		}
		
		if (clazz.isArray()) {
			return (T) Array.newInstance(clazz.getComponentType(), 0);
		}
			
		if (UNSAFE != null) { //Unsafe available, use it to instantiate the class
			try {
				return (T) ((sun.misc.Unsafe) UNSAFE).allocateInstance(clazz);
			} catch (InstantiationException e) {}
		}
		
		//Fallback to reflection
		try {
			return clazz.getConstructor().newInstance();
		} catch (Exception e) {
			throw new ETFException(e);
		}
	}
	
	public static void setField(Object instance, Field field, Object value) throws IllegalAccessException {
		if (UNSAFE != null) {
			if (int.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putInt(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (int) value);
			} else if (long.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putLong(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (long) value);
			} else if (double.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putDouble(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (double) value);
			} else if (void.class.equals(field.getType())) {
			
			} else if (float.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putFloat(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (float) value);
			} else if (byte.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putByte(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (byte) value);
			} else if (char.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putChar(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (char) value);
			} else if (boolean.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putBoolean(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (boolean) value);
			} else if (short.class.equals(field.getType())) {
				((sun.misc.Unsafe) UNSAFE).putShort(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), (short) value);
			} else {
				((sun.misc.Unsafe) UNSAFE).putObject(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field), value);
			}
		} else { //Fallback if unsafe isn't available
			field.setAccessible(true);
			if (int.class.equals(field.getType())) {
				field.setInt(instance, (int) value);
			} else if (long.class.equals(field.getType())) {
				field.setLong(instance, (long) value);
			} else if (double.class.equals(field.getType())) {
				field.setDouble(instance, (double) value);
			} else if (void.class.equals(field.getType())) {
				
			} else if (float.class.equals(field.getType())) {
				field.setFloat(instance, (float) value);
			} else if (byte.class.equals(field.getType())) {
				field.setByte(instance, (byte) value);
			} else if (char.class.equals(field.getType())) {
				field.setChar(instance, (char) value);
			} else if (boolean.class.equals(field.getType())) {
				field.setBoolean(instance, (boolean) value);
			} else if (short.class.equals(field.getType())) {
				field.setShort(instance, (short) value);
			} else {
				field.set(instance, value);
			}
		}
	}
	
	public static Object getField(Object instance, Field field) throws IllegalAccessException {
		if (UNSAFE != null) {
			if (int.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getInt(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else if (long.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getLong(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else if (double.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getDouble(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else if (void.class.equals(field.getType())) {
				return null;
			} else if (float.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getFloat(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else if (byte.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getByte(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else if (char.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getChar(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else if (boolean.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getBoolean(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else if (short.class.equals(field.getType())) {
				return ((sun.misc.Unsafe) UNSAFE).getShort(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			} else {
				return ((sun.misc.Unsafe) UNSAFE).getObject(instance, ((sun.misc.Unsafe) UNSAFE).objectFieldOffset(field));
			}
		} else { //Fallback if unsafe isn't available
			field.setAccessible(true);
			if (int.class.equals(field.getType())) {
				return field.getInt(instance);
			} else if (long.class.equals(field.getType())) {
				return field.getLong(instance);
			} else if (double.class.equals(field.getType())) {
				return field.getDouble(instance);
			} else if (void.class.equals(field.getType())) {
				return null;
			} else if (float.class.equals(field.getType())) {
				return field.getFloat(instance);
			} else if (byte.class.equals(field.getType())) {
				return field.getByte(instance);
			} else if (char.class.equals(field.getType())) {
				return field.getChar(instance);
			} else if (boolean.class.equals(field.getType())) {
				return field.getBoolean(instance);
			} else if (short.class.equals(field.getType())) {
				return field.getShort(instance);
			} else {
				return field.get(instance);
			}
		}
	}
	
	public interface IPropertyAccessor {
		
		Object get();
		
		Class<?> getType();
	}
	
	public interface IPropertyMutator {
		
		void set(Object o);
		
		Class<?> getType();
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
				return getField(object, field);
			} catch (Throwable e) {
				throw new ETFException("Cannot access " + field.toGenericString(), e);
			}
		}
		
		@Override
		public void set(Object o) {
			try {
				setField(object, field, o);
			} catch (Throwable e) {
				throw new ETFException("Cannot modify " + field.toGenericString(), e);
			}
		}
		
		@Override
		public Class<?> getType() {
			return field.getType();
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
			} catch (Exception e) {
				throw new ETFException("Cannot invoke " + method.toGenericString(), e);
			}
		}
		
		@Override
		public void set(Object o) {
			try {
				method.invoke(object, o);
			} catch (Exception e) {
				throw new ETFException("Cannot invoke " + method.toGenericString(), e);
			}
		}
		
		@Override
		public Class<?> getType() {
			return method.getParameterCount() == 0 ? method.getReturnType() : method.getParameterTypes()[0];
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
		
		@Override
		public Class<?> getType() {
			return Void.class;
		}
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
			for (Method m : getAllMethods(instance.getClass())) {
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
						&& m.getParameterCount() == 1) {
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
		
		public Class<?> getSetterType() {
			return mutator.getType();
		}
		
		public Class<?> getGetterType() {
			return accessor.getType();
		}
	}
}
