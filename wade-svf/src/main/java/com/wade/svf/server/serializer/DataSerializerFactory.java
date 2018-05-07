/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月27日
 * 
 * Just Do IT.
 */
package com.wade.svf.server.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * 数据序列化对象工厂
 */
public final class DataSerializerFactory {

	public static final String DATA_APPLICATION_JSON = "application/json";
	public static final String DATA_BINARY_JSON = "binary/json-stream";
	
	/**
	 * 工厂类
	 */
	private static final DataSerializerFactory factory = new DataSerializerFactory();
	
	/**
	 * IDataSerializer的实例缓存
	 */
	private static Map<String, IDataSerializer> instances = new HashMap<String, IDataSerializer>(10);
	
	private DataSerializerFactory() {
		
	}
	
	/**
	 * get factory
	 * @return
	 */
	public static DataSerializerFactory getFactory() {
		return factory;
	}
	
	/**
	 * 根据数据类型获取序列化对象
	 * @param dataType
	 * @return
	 */
	public IDataSerializer getSerializer(String dataType) {
		return instances.get(dataType);
	}
	
	static {
		instances.put(DATA_APPLICATION_JSON, new JsonStringSerializer());
		instances.put(DATA_BINARY_JSON, new JsonStringSerializer());
	}
}
