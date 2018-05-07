/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月22日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.config.cache;

/**
 * @description
 * 空的XmlItem对象，用来避免异常xml的重复解析
 */
public class EmptyXmlItem extends XmlItem {
	
	private String error;
	private Exception exception;
	
	public EmptyXmlItem(String error, Exception exception) {
		this.error = error;
		this.exception = exception;
	}
	
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	
	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

}
