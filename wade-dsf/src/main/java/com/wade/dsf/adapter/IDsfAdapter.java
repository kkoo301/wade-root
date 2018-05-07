/**
 * $
 */
package com.wade.dsf.adapter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfAdapter.java
 * @description: 数据适配接口，提供流转对象，对象转流的功能
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-19
 */
public interface IDsfAdapter {
	
	/**
	 * 将请求流转换成Java对象
	 * @param is
	 * @return
	 */
	public Serializable streamToObject(Map<String, String> requestHeader, InputStream is, String charset) throws Exception;
	
	/**
	 * 将Java对象转换成输出流
	 * @param requestHeader
	 * @param out
	 * @param object
	 * @param charset
	 * @return 数据流大小
	 * @throws Exception
	 */
	public int objectToStream(Map<String, String> requestHeader, OutputStream out, Serializable object, String charset) throws Exception;
	
	/**
	 * 将异常转换成对象流
	 * @param requestHeader
	 * @param out
	 * @param object
	 * @param charset
	 * @return
	 */
	public int exceptionToStream(Map<String, String> requestHeader, OutputStream out, Exception object, String charset);

}
