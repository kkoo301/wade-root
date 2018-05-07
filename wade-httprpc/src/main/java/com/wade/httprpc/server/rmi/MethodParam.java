/**
 * $
 */
package com.wade.httprpc.server.rmi;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: MethodParam.java
 * @description: 方法参数
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public class MethodParam {
	
	private String paramName;
	private Class<?> paramType;
	
	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}
	
	/**
	 * @return the paramType
	 */
	public Class<?> getParamType() {
		return paramType;
	}
	
	/**
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	/**
	 * @param paramType the paramType to set
	 */
	public void setParamType(Class<?> paramType) {
		this.paramType = paramType;
	}
	

}
