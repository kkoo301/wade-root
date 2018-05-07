/**
 * $
 */
package com.wade.httprpc.client.conn.pool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wade.httprpc.client.conn.pool.SocketPool;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: SocketPoolFactory.java
 * @description: SocketPool工厂类, 确保每个URL只会有一个SocketPool实列
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-2-21
 */
public final class SocketPoolFactory {
	
	private static Map<String, SocketPool> pools = new HashMap<String, SocketPool>(10);
	
	private SocketPoolFactory() {
		
	}
	
	public static SocketPool createSocketPool(String url, Class<?> clazz, int initSize, int maxSize, int increment) throws IOException {
		SocketPool pool = pools.get(url);
		if (null == pool) {
			synchronized (pools) {
				pool = pools.get(url);
				if (null == pool) {
					try {
						pool = (SocketPool) clazz.newInstance();
						pool.setServerAddr(url);
						pool.createPool(initSize, maxSize, increment);
						pools.put(url, pool);
					} catch (Exception e) {
						throw new IOException("创建SocketPool异常, 无法实例化", e);
					}
				}
			}
			
		}
		return pool;
	}

}
