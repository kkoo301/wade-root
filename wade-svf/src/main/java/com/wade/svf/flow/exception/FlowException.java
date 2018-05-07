/**
 * $
 */
package com.wade.svf.flow.exception;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FlowException.java
 * @description: Flow异常, 从服务端返回的所有异常都是该类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class FlowException extends Exception {
	
	private static final long serialVersionUID = 5081876312400585878L;
	
	/**
	 * 异常编码
	 */
	private String errCode;
	
	/**
	 * 异常描述
	 */
	private String errInfo;
	
	public FlowException(String errCode, String errInfo) {
		this(errCode, errInfo, new Exception(String.format("{%s}:{%s}", errCode, errInfo)));
	}
	
	public FlowException(String errCode, String errInfo, Throwable err) {
		super(err);
		this.errCode = errCode;
		this.errInfo = errInfo;
	}
	
	/**
	 * @return the errCode
	 */
	public String getErrCode() {
		return errCode;
	}
	
	/**
	 * @return the errInfo
	 */
	public String getErrInfo() {
		return errInfo;
	}
	
	
	@Override
	public String getMessage() {
		return String.format("{%s}:{%s}", getErrCode(), getErrInfo());
	}

}
