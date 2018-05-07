/**
 * $
 */
package com.wade.httprpc.client.conn.pool.socket;

import java.io.IOException;
import java.net.Socket;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpSocket.java
 * @description: 
 * 1. 重载close方法, 并不直接关闭物理链路
 * 2. 添加destroy方法, 关闭物理链路
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-2-21
 */
public class HttpSocket extends Socket {

	/**
	 * 什么事都不做
	 */
	@Override
	public synchronized void close() throws IOException {
		//super.close();
	}
	
	public void destroy() throws IOException {
		super.close();
	}
}
