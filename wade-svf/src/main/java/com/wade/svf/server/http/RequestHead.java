/**
 * $
 */
package com.wade.svf.server.http;

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
public enum RequestHead {
	
	/**
	 * HTTP Content-Type
	 */
	ContentType("Content-Type"),
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
	 * 服务名称, 非标准参数
	 */
	FlowName("FlowName");
	
	private String code; 
	
	private RequestHead(String code) {
		this.code = code;
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
