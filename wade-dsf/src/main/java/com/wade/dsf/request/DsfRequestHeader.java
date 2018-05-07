/**
 * $
 */
package com.wade.dsf.request;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: RequestHeader.java
 * @description: 请求头参数定义
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-3
 */
public enum DsfRequestHeader {
	
	/**
	 * HTTP Context-Type
	 */
	ContextType("Context-Type"),
	/**
	 * 字符集
	 */
	Charset("Charset"),
	/**
	 * 请求串
	 */
	QueryString("QueryString"),
	
	/**
	 * HTTP Context-Type
	 */
	Method("Method"),
	
	/**
	 * Http Client-IP, 非标准参数
	 */
	ClientIp("Client-IP"),
	
	/**
	 * Http Client-Mac, 非标准参数
	 */
	ClientMac("Client-Mac"),
	
	/**
	 * Http Dsf-Client-Name, 非标准参数
	 */
	DSFClientName("DSF-Client-Name"),
	
	/**
	 * 服务名称, 非标准参数
	 */
	ServiceName("ServiceName");
	
	private String code; 
	
	private DsfRequestHeader(String code) {
		this.code = code;
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
