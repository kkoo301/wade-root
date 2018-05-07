/**
 * $
 */
package com.wade.httprpc.client.conn;

import com.wade.httprpc.client.conn.HttpConnection;
import com.wade.httprpc.client.conn.config.HttpConfigure;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpConnectionFactory.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-23
 */
public interface HttpConnectionFactory {
	
	public HttpConnection createHttpConnection(HttpConfigure configure);

}
