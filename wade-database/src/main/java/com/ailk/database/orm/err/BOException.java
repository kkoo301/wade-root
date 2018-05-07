package com.ailk.database.orm.err;

/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月20日
 * 
 * Just Do IT.
 */
public class BOException extends Exception {
	
	private static final long serialVersionUID = 5081876312400585878L;
	
	/**
	 * 异常编码
	 */
	private String errCode;
	
	/**
	 * 异常描述
	 */
	private String errInfo;
	
	public BOException(String errCode, String errInfo) {
		this(errCode, errInfo, new Exception(String.format("{%s}:{%s}", errCode, errInfo)));
	}
	
	public BOException(String errCode, String errInfo, Throwable err) {
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
