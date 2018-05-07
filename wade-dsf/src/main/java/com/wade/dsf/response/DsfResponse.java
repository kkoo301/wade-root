/**
 * $
 */
package com.wade.dsf.response;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfResponse.java
 * @description: 服务响应对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class DsfResponse implements Serializable {
	
	private static final long serialVersionUID = 8763577960579809036L;
	private Serializable response;
	private String responseInstanceType;
	
	public static final int OK_STATUS = 0;
	public static final int ERR_STATUS = -1;
	
	private int status = OK_STATUS;
	
	public DsfResponse() {
		
	}
	
	/**
	 * 
	 */
	public DsfResponse(Serializable response) {
		this.response = response;
		
		if (null != this.response) {
			this.responseInstanceType = this.response.getClass().getName();
		}
	}
	
	public boolean isOk() {
		return this.status == OK_STATUS;
	}
	
	public void setError() {
		this.status = ERR_STATUS;
	}
	
	/**
	 * @return the response
	 */
	public Serializable getResponse() {
		return response;
	}
	
	/**
	 * @param response the response to set
	 */
	public void setResponse(Serializable response) {
		this.response = response;
		
		if (null != this.response) {
			this.responseInstanceType = this.response.getClass().getName();
		}
	}
	
	/**
	 * @return the responseInstanceType
	 */
	public String getResponseInstanceType() {
		return responseInstanceType;
	}
	
}
