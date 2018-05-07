/**
 * $
 */
package com.wade.httprpc.client.apache;

import com.wade.httprpc.client.conn.HttpConnection;
import com.wade.httprpc.client.conn.HttpConnectionFactory;
import com.wade.httprpc.client.conn.config.HttpConfigure;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ApacheHttpConnectionFactory.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-5-17
 */
public class ApacheHttpConnectionFactory implements HttpConnectionFactory {

	
	@Override
	public HttpConnection createHttpConnection(HttpConfigure configure) {
		return new ApacheHttpConnection(configure);
	}

}
