package com.ailk.common.util.parser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * IValueFilter的加载器
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ValueFilterLoader.java
 * @author: liaosheng
 * @date: 2015-7-1
 */
public final class ValueFilterLoader {
	
	private static final Logger log = Logger.getLogger(ValueFilterLoader.class);
	
	private Map<String, IValueFilter> filters = new ConcurrentHashMap<String, IValueFilter>(20);
	private static ValueFilterLoader loader = new ValueFilterLoader();
	
	private ValueFilterLoader() {
		
	}
	
	public static ValueFilterLoader getInstance() {
		return loader;
	}
	
	/**
	 * 获取值过滤器实例
	 * @param clazz
	 * @return
	 */
	public IValueFilter getFilter(String clazz) {
		if (null == clazz || clazz.length() == 0)
			return null;
		
		IValueFilter filter = filters.get(clazz);
		if (null == filter) {
			try {
				Class<?> c = Class.forName(clazz);
				filter = (IValueFilter) c.newInstance();
				filters.put(clazz, filter);
			} catch (Exception e) {
				log.error("字段过滤器初始化失败", e);
			}
		}
		
		return filter;
	}

}
