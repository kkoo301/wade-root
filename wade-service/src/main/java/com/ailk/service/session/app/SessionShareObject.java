/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.service.session.app;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 会话全局缓存控制对象
 * 
 * @className: AppSessionLock.java
 * @author: liaosheng
 * @param <T>
 * @date: 2014-5-17
 */
public class SessionShareObject <T extends ISessionShareObject> {
	
	private static final Logger log = Logger.getLogger(SessionShareObject.class);
	
	/**
	 * 存放当前线程会话里所有的缓存对象，同一个缓存有且只有一个实例
	 */
	private Map<Class<?>, T> caches = new LinkedHashMap<Class<?>, T>(100);
	
	public SessionShareObject() {
	}
	
	public T get(Class<?> clazz) throws Exception {
		T cache = caches.get(clazz);
		if (null == cache) {
			try {
				cache = createInstance(clazz);
				caches.put(clazz, cache);
			} catch (Exception e) {
				throw new Exception (String.format("创建线程共享对象%s异常", clazz.getName()), e);
			}
		}
		return cache;
	}
	
	
	/**
	 * 释放所有缓存
	 */
	public void cleanCaches() {
		Iterator<Class<?>> iter = caches.keySet().iterator();
		while (iter.hasNext()) {
			Class<?> clazz = iter.next();
			
			T t = caches.get(clazz);
			if (null != t) {
				if (log.isDebugEnabled()) {
					log.debug("释放线程共享缓存对象：" + clazz);
				}
				t.clean();
			}
		}
	}
	
	/**
	 * 创建缓存对象实例，并保存对象的clean方法
	 * 若clazz不存在clean,则抛出方法未定义异常
	 * 若clazz存在多个clean,则抛出方法重复定义异常
	 * @param clazz 缓存实例对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	private T createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
		T cache = caches.get(clazz);
		if (null == cache) {
			cache = (T) clazz.newInstance();
		}
		return cache;
	}
}
