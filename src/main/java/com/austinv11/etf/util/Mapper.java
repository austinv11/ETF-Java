package com.austinv11.etf.util;

import com.austinv11.etf.ETFConfig;
import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.erlang.ErlangList;
import com.austinv11.etf.erlang.ErlangMap;
import com.austinv11.etf.parsing.ETFParser;
import com.austinv11.etf.util.ReflectionUtils.PropertyManager;
import com.austinv11.etf.writing.ETFWriter;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * This represents a mapper which will handle object serialization/deserialization.
 */
public class Mapper {
	
	private ETFConfig config;
	
	public Mapper(ETFConfig config) {
		this.config = config;
	}
	
	public <T> byte[] writeToMap(T obj) {
		return config.createWriter().writeMap(obj).toBytes();
	}
	
	public <T> byte[] write(T obj) {
		ETFWriter writer = config.createWriter();
		ReflectionUtils.findProperties(obj, obj.getClass()).forEach(writer::write);
		return writer.toBytes();
	}
	
	public <T> T read(ErlangMap data, Class<T> clazz) {
		T instance = ReflectionUtils.createInstance(clazz);
		List<PropertyManager> properties = ReflectionUtils.findProperties(instance, clazz);
		for (PropertyManager property : properties) {
			if (data.containsKey(property.getName())) {
				Object obj = data.get(property.getName());
				if (obj instanceof ErlangMap) {
					obj = read((ErlangMap) obj, property.getSetterType());
				} else if (obj instanceof ErlangList && property.getSetterType().isArray()) {
					if (((ErlangList) obj).size() > 0) {
						T[] array = (T[]) Array.newInstance(property.getSetterType().getComponentType(), ((ErlangList) obj).size());
						for (int i = 0; i < array.length; i++) {
							Object obj1 = ((ErlangList) obj).get(i);
							if (obj1 != null)
								array[i] = obj1 instanceof ErlangMap ? read(data, (Class<T>) property.getSetterType().getComponentType()) : (T) obj1;
							else
								array[i] = null;
						}
						obj = array;
					} else
						obj = Array.newInstance(property.getSetterType().getComponentType(), 0);
				}
				property.setValue(obj);
			}
		}
		return instance;
	}
	
	public <T> T read(byte[] data, Class<T> clazz) {
		ETFParser parser = config.createParser(data);
		T instance = ReflectionUtils.createInstance(clazz);
		List<PropertyManager> properties = ReflectionUtils.findProperties(instance, clazz);
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
}
