/**
 * $
 */
package com.wade.dsf.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import com.wade.dsf.exception.DsfException;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfAction.java
 * @description: DSF执行
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-10-15
 */
public interface IDsfAction {
	
	/**
	 * 初始化
	 * @throws DsfException
	 */
	public void init() throws DsfException;
	
	/**
	 * 反序列化
	 * @param serviceName
	 * @param requestHeader
	 * @param is
	 * @return
	 * @throws DsfException
	 */
	public Serializable read(String serviceName, Map<String, String> requestHeader, InputStream is) throws DsfException;
	
	
	/**
	 * 序列化对象
	 * @param serviceName
	 * @param requestHeader
	 * @param out
	 * @param object
	 * @return
	 * @throws DsfException
	 */
	public int write(String serviceName, Map<String, String> requestHeader, OutputStream out, Serializable object) throws DsfException;
	
	
	/**
	 * 请求执行
	 * @param serviceName
	 * @param header
	 * @param body
	 * @return
	 * @throws DsfException
	 */
	public Serializable execute(String serviceName, Map<String, String> header, Serializable body) throws DsfException;
	
	/**
	 * 异常处理
	 * @param serviceName
	 * @param header
	 * @param out
	 * @param e
	 * @return
	 */
	public int error(String serviceName, Map<String, String> header, OutputStream out, Exception e);

}
