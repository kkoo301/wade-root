/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年8月1日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.express;

import java.util.HashMap;
import java.util.Map;

import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;

/**
 * @description
 * 创建并缓存IValueExpress对象
 */
public final class ValueExpressFactory {
	
	/**
	 * 缓存IValueExpress对象
	 */
	private static Map<String, IValueExpress> expresses = new HashMap<String, IValueExpress>(10);
	
	private static final ValueExpressFactory factory = new ValueExpressFactory();
	
	private final Object lock = new Object();
	
	
	private ValueExpressFactory() {
		
	}
	
	public static ValueExpressFactory getFactory() {
		return factory;
	}
	
	/**
	 * 根据配置内容获取表达式对象
	 * @param config
	 * @return
	 */
	public IValueExpress getExpress(String config) {
		return expresses.get(config);
	}
	
	/**
	 * 创建并缓存表达式对象
	 * @param config
	 * @param clazz
	 * @return
	 * @throws FlowException
	 */
	public IValueExpress createExpress(String config, String clazz) throws FlowException {
		IValueExpress express = expresses.get(config);
		
		if (null == express) {
			synchronized (lock) {
				express = expresses.get(config);
				if (null == express) {
					try {
						express = (IValueExpress) Class.forName(clazz).newInstance();
						
						expresses.put(config, express);
						
						return express;
					} catch (Exception e) {
						throw new FlowException(FlowErr.flow10015.getCode(), FlowErr.flow10015.getInfo(config + ":" + clazz + "," + e.getMessage()), e);
					}
				}
			}
		}
		
		return express;
	}
	

}
