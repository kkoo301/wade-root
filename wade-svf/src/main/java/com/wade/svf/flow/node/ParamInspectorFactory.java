/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月24日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.node;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description
 * 管理（创建并缓存）参数检查对象
 */
public final class ParamInspectorFactory {
	
	private static final Logger log = LoggerFactory.getLogger(ParamInspectorFactory.class);
	
	private static final Map<String, IParamInspector> pool = new HashMap<String, IParamInspector>(100);
	
	private Object lock = new Object();
	
	
	private static final ParamInspectorFactory factory = new ParamInspectorFactory();
	
	private ParamInspectorFactory() {
		
	}
	
	/**
	 * 获取实例对象
	 * @return
	 */
	public static ParamInspectorFactory getInstance() {
		return factory;
	}
	
	
	/**
	 * 加载
	 * @param className
	 * @return
	 */
	public IParamInspector get(String className) {
		if (null == className || className.length() == 0) {
			return null;
		}
		
		IParamInspector inst = pool.get(className);
		
		if (null == inst) {
			synchronized (lock) {
				inst = pool.get(className);
				if (null == inst) {
					try {
						inst = (IParamInspector) Class.forName(className).newInstance();
						
						pool.put(className, inst);
					} catch (Exception e) {
						log.error("参数检查对象加载异常", e);
					}
				}
			}
		}
		
		return inst;
	}

}
