/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.service.session.app;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 会话全局锁控制对象
 * 
 * @className: AppSessionLock.java
 * @author: liaosheng
 * @date: 2014-5-17
 */
public class AppSessionLock  {
	
	private static final Logger log = Logger.getLogger(AppSessionLock.class);
	
	/**
	 * 存放当前线程会话里所有的锁对象，同一个锁有且只有一个实例
	 */
	private Map<Class<?>, Object> locks = new LinkedHashMap<Class<?>, Object>(100);
	private Map<Class<?>, Method> lockMethods = new HashMap<Class<?>, Method>(100);
	private Map<Class<?>, Method> unlockMethods = new HashMap<Class<?>, Method>(100);
	private Map<Class<?>, Method> cleanMethods = new HashMap<Class<?>, Method>(100);
	
	public AppSessionLock() {
	}
	
	
	/**
	 * 调用clazz的lock方法，并以params为方法的入参
	 * @param clazz
	 * @param params
	 * @throws Exception
	 */
	public boolean lock(Class<?> clazz, Object[] params) throws Exception {
		Object lock = locks.get(clazz);
		Method lockMethod = lockMethods.get(clazz);
		
		if (null == lock || null == lockMethod) {
			try {
				createInstance(clazz);
			} catch (Exception e) {
				throw new Exception (String.format("创建锁%s对象异常", clazz.getName()), e);
			}
		}
		
		lock = locks.get(clazz);
		lockMethod = lockMethods.get(clazz);
		
		try {
			Boolean rtn = (Boolean) lockMethod.invoke(lock, params);
			return rtn;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 调用clazz的unlock方法，并以params为方法的入参
	 * @param clazz
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public boolean unlock(Class<?> clazz, Object[] params) throws Exception {
		Object lock = locks.get(clazz);
		Method unlockMethod = unlockMethods.get(clazz);
		
		if (null == lock || null == unlockMethod) {
			try {
				createInstance(clazz);
			} catch (Exception e) {
				throw new Exception (String.format("创建锁%s对象异常", clazz.getName()), e);
			}
		}
		
		lock = locks.get(clazz);
		unlockMethod = unlockMethods.get(clazz);
		
		try {
			Boolean rtn = (Boolean) unlockMethod.invoke(lock, params);
			return rtn;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 释放所有锁
	 */
	public void cleanLocks() {
		Iterator<Class<?>> iter = locks.keySet().iterator();
		while (iter.hasNext()) {
			Class<?> clazz = iter.next();
			
			Object t = locks.get(clazz);
			if (null != t) {
				if (log.isDebugEnabled()) {
					log.debug("清空线程锁对象:" + clazz);
				}
				Method method = cleanMethods.get(clazz);
				if (null != method) {
					try {
						method.invoke(t, new Object[] {});
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					} 
				}
			}
		}
	}
	
	/**
	 * 创建锁对象实例，并保存对象的lock，unlock方法
	 * 若clazz不存在lock,unlock,则抛出方法未定义异常
	 * 若clazz存在多个lock或unlock,则抛出方法重复定义异常
	 * @param clazz 锁实例对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private void createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
		Object lock = locks.get(clazz);
		if (null == lock) {
			lock = clazz.newInstance();
			locks.put(clazz, lock);
		}
		
		Method lockMethod = lockMethods.get(clazz);
		Method unlockMethod = unlockMethods.get(clazz);
		Method cleanMethod = cleanMethods.get(clazz);
		
		if (null == lockMethod || null == unlockMethod || null == cleanMethod) {
			
			Method[] methods = clazz.getMethods();
			
			for (int i = 0, cnt = methods.length; i < cnt; i++) {
				Method method = methods[i];
				String methodName = method.getName();
				
				if ("lock".equals(methodName)) {
					if (null != lockMethod) {
						throw new NoSuchMethodError(String.format("锁对象%s定义异常，重复定义的lock函数", clazz.getName()));
					}
					lockMethod = method;
				}
				
				if ("unlock".equals(method.getName())) {
					if (null != unlockMethod) {
						throw new NoSuchMethodError(String.format("锁对象%s定义异常，重复定义的unlock函数", clazz.getName()));
					}
					unlockMethod = method;
				}
				
				if ("clean".equals(method.getName())) {
					if (null != cleanMethod) {
						throw new NoSuchMethodError(String.format("锁对象%s定义异常，重复定义的clean函数", clazz.getName()));
					}
					cleanMethod = method;
				}
			}
			
			if (null == lockMethod) {
				throw new NoSuchMethodException(String.format("锁对象%s定义异常，函数lock未定义", clazz.getName()));
			}
			
			if (null == unlockMethod) {
				throw new NoSuchMethodException(String.format("锁对象%s定义异常，函数unlock未定义", clazz.getName()));
			}
			
			if (null == cleanMethod) {
				throw new NoSuchMethodException(String.format("锁对象%s定义异常，函数clean未定义", clazz.getName()));
			}
			
			lockMethod.setAccessible(true);
			unlockMethod.setAccessible(true);
			cleanMethod.setAccessible(true);
			
			lockMethods.put(clazz, lockMethod);
			unlockMethods.put(clazz, unlockMethod);
			cleanMethods.put(clazz, cleanMethod);
		}
	}
}
