/**
 * $
 */
package com.wade.dsf.server.method;

import java.lang.reflect.Method;
import java.util.Map;

import com.wade.dsf.server.method.MethodParam;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: JavaInterfaces.java
 * @description: 缓存Java接口类，遍历每个方法名，并获取Meta信息生成Map对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public class ServiceImplClassInfo {
	
	/**
	 * 类名
	 */
	private String className;
	
	/**
	 * Public方法名<->
	 */
	private Map<Method, MethodParam[]> methods;
	
	
	public ServiceImplClassInfo () {
		
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * @return the methods
	 */
	public Map<Method, MethodParam[]> getMethods() {
		return methods;
	}
	
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @param methods the methods to set
	 */
	public void setMethods(Map<Method, MethodParam[]> methods) {
		this.methods = methods;
	}

}
