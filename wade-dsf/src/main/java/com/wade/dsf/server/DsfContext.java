/**
 * $
 */
package com.wade.dsf.server;

import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;
import com.wade.dsf.server.DsfContext;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfContext.java
 * @description: 请求响应上下文对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public final class DsfContext {
	
	private static ThreadLocal<DsfContext> context = new ThreadLocal<DsfContext>() {
		protected DsfContext initialValue() {
			return new DsfContext();
		};
	};
	
	
	/**
	 * 服务名
	 */
	private String serviceName;
	
	/**
	 * 请求对象
	 */
	private DsfRequest request;
	
	/**
	 * 响应对象
	 */
	private DsfResponse response;
	
	
	private DsfContext() {
		
	}
	
	/**
	 * 获取线程的上下文对象
	 * @return
	 */
	public static DsfContext getContext() {
		return context.get();
	}
	
	/**
	 * 清除线程的上下文对象
	 */
	public static void destory() {
		context.set(new DsfContext());
	}
	
	
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	
	
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
	/**
	 * @return the request
	 */
	public DsfRequest getRequest() {
		return request;
	}
	
	/**
	 * @return the response
	 */
	public DsfResponse getResponse() {
		return response;
	}
	
	/**
	 * @param request the request to set
	 */
	public void setRequest(DsfRequest request) {
		this.request = request;
	}
	
	/**
	 * @param response the response to set
	 */
	public void setResponse(DsfResponse response) {
		this.response = response;
	}
	
}
