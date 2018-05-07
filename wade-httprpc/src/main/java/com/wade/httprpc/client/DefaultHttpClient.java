/**
 * $
 */
package com.wade.httprpc.client;


import java.io.IOException;
import java.io.Serializable;

import com.wade.httprpc.client.conn.config.HttpConfigure;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DefaultHttpClient.java
 * @description: 客户端调用类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-22
 */
public final class DefaultHttpClient {

	/**
	 * Java序列化对象
	 * @param httpServerAddr
	 * @param request
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static final <T extends Serializable> T call(String httpServerAddr, Serializable request, Class<T> clazz) throws IOException {
		return HttpConfigure
				.build(httpServerAddr)
				.createConnection()
				.post(request, clazz);
	}
	
	/**
	 * Java序列化对象
	 * @param httpServerAddr
	 * @param request
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static final <T extends Serializable> T call(String httpServerAddr, Serializable request, Class<T> clazz, int soTimeout) throws IOException {
		return HttpConfigure
				.build(httpServerAddr, soTimeout)
				.createConnection()
				.post(request, clazz);
	}
	
	/**
	 * 纯字符串的调用
	 * @param httpServerAddr
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static final String call(String httpServerAddr, String request) throws IOException {
		return HttpConfigure
				.build(httpServerAddr)
				.createConnection()
				.post(request);
	}
}
