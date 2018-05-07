/**
 * $ID$
 */
package com.wade.dsf.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfRequest.java
 * @description: 服务请求对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class DsfRequest implements Serializable {
	
	private static final long serialVersionUID = 3981613201155593449L;

	/**
	 * 当前请求的服务名
	 */
	private String serviceName;
	
	/**
	 * 当前请求的业务数据
	 */
	private Serializable request;
	
	/**
	 * 请求实体对象类型
	 */
	private String requestInstanceType;
	
	/**
	 * 序列化格式
	 */
	private String requestType;
	
	/**
	 * 客户端IP地址
	 */
	private String clientIp;
	
	/**
	 * 客户端Mac地址
	 */
	private String clientMac;
	
	private Map<String, String> header = new HashMap<String, String>(10);
	
	public DsfRequest(String serviceName, Map<String, String> header, Serializable request) {
		this.serviceName = serviceName;
		this.header = header;
		this.request = request;
		
		if (null != this.request) {
			requestInstanceType = this.request.getClass().getName();
		}
		
		if (null != this.header) {
			this.requestType = this.header.get(DsfRequestHeader.ContextType.getCode());
			this.clientIp = this.header.get(DsfRequestHeader.ClientIp.getCode());
			this.clientMac = this.header.get(DsfRequestHeader.ClientMac.getCode());
		}
	}
	
	
	/**
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		return header;
	}
	
	
	/**
	 * 获取请求原始对象
	 * @return the request
	 */
	public Serializable getRequest() {
		return request;
	}
	
	/**
	 * 设置请求原始对象
	 * @param request the request to set
	 */
	public void setRequest(Serializable request) {
		this.request = request;
	}
	
	/**
	 * 获取服务名
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	
	/**
	 * 设置服务名
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * 获取请求实例的对象类型，即类名
	 * @return the requestInstanceType
	 */
	public String getRequestInstanceType() {
		return requestInstanceType;
	}
	
	/**
	 * 获取请求类型，即Http协议中的Context-Type
	 * @return the serializeType
	 */
	public String getRequestType() {
		return requestType;
	}
	
	/**
	 * 获取客户端IP地址
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}
	
	/**
	 * 获取客户端Mac地址
	 * @return the clientMac
	 */
	public String getClientMac() {
		return clientMac;
	}
	
}
