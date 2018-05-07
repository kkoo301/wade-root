/**
 * $
 */
package com.ailk.common.exce;

import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ExceptionUtil.java
 * @description: 
 * 读取global.properties里的exception.parser，创建IExceptionParser对象，并解析异常返回IExceptionWrapper
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-11
 */
public final class ExceptionUtil {
	
	private static final transient Logger log = Logger.getLogger(ExceptionUtil.class);
	
	private static IExceptionParser parser = null;
	
	static {
		try {
			parser = (IExceptionParser) Class.forName(GlobalCfg.getProperty("exception.parser")).newInstance();
		} catch (Exception e) {
			log.info(String.format("创建异常解析类%s失败，采用默认实现: %s。", GlobalCfg.getProperty("exception.parser"), WadeExceptionParser.class.getName()));
			parser = new WadeExceptionParser();
		}
	}
	
	/**
	 * 解析异常
	 * @param e
	 * @return
	 */
	public static IExceptionWrapper parse(Throwable e) {
		return parser.parse(e);
	}

	/**
	 * 解析异常
	 * @param e
	 * @param codeTranslater
	 * @return
	 */
	public static IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater) {
		return parser.parse(e, codeTranslater);
	}
	
	/**
	 * 解析异常
	 * @param e
	 * @param codeTranslater 异常编码翻译对象
	 * @param needStack 是否需要堆栈信息
	 * @return
	 */
	public static IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater, boolean needStack) {
		return parser.parse(e, codeTranslater, false, needStack);
	}
	
	/**
	 * 解析异常
	 * @param e
	 * @param codeTranslater 异常编码翻译对象
	 * @param needMask 是否不显示详细错误
	 * @param needStack  是否需要堆栈信息
	 * @return
	 */
	public static IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater, boolean needMask, boolean needStack) {
		return parser.parse(e, codeTranslater, needMask, needStack);
	}
}
