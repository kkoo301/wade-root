package com.ailk.common.exce;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IExceptionParser.java
 * @description: 异常解析类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-11
 */
public interface IExceptionParser {
	
	
	/**
	 * 解析异常对象
	 * @param e
	 * @return
	 */
	public IExceptionWrapper parse(Throwable e);

	/**
	 * 解析异常对象
	 * @param e
	 * @param codeTranslater
	 * @return
	 */
	public IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater);
	
	/**
	 * 解析异常对象
	 * @param e
	 * @param codeTranslater
	 * @param needStack
	 * @return
	 */
	public IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater, boolean needStack);
	
	/**
	 * 解析异常对象
	 * @param e
	 * @param codeTranslater
	 * @param needMask
	 * @param needStack
	 * @return
	 */
	public IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater, boolean needMask, boolean needStack);
}
