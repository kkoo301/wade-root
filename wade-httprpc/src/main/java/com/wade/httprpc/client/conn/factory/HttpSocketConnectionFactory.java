/**
 * $
 */
package com.wade.httprpc.client.conn.factory;

import com.wade.httprpc.client.conn.factory.HttpSocketConnection;
import com.wade.httprpc.client.conn.HttpConnection;
import com.wade.httprpc.client.conn.HttpConnectionFactory;
import com.wade.httprpc.client.conn.config.HttpConfigure;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpSocketConnectionFactory.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-23
 */
public class HttpSocketConnectionFactory implements HttpConnectionFactory {
	
	@Override
	public HttpConnection createHttpConnection(HttpConfigure configure) {
		return new HttpSocketConnection(configure);
	}
}
