/**
 * $
 */
package com.wade.dsf.exception;

import com.ailk.common.data.IData;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfException.java
 * @description: Dsf异常, 从服务端返回的所有异常都是该类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class DsfException extends Exception {
	
	private static final long serialVersionUID = 5081876312400585878L;
	
	/**
	 * 异常编码
	 */
	private String errCode;
	
	/**
	 * 异常描述
	 */
	private String errInfo;
	
	private IData data;
	
	/**
	 * @return the data
	 */
	public IData getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(IData data) {
		this.data = data;
	}
	
	public DsfException(String errCode, String errInfo) {
		this(errCode, errInfo, new Exception(String.format("{%s}:{%s}", errCode, errInfo)));
	}
	
	public DsfException(String errCode, String errInfo, Throwable err) {
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
