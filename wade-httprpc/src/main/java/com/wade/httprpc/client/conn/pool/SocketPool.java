/**
 * $
 */
package com.wade.httprpc.client.conn.pool;

import java.net.Socket;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: SocketPool.java
 * @description: 
 * Socket连接池
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-2-21
 */
public interface SocketPool {
	
	/**
	 * 设置HTTP服务地址
	 * @param httpServerAddr
	 */
	public void setServerAddr(String httpServerAddr);
	
	/**
	 * 设置连接数
	 * @param initSize	初始值
	 * @param maxSize	最大值
	 * @param increment	增量值
	 */
	public void createPool(int initSize, int maxSize, int increment);
	
	/**
	 * 返回Socket对象, 如是要为空则抛出异常
	 * @return
	 * @throws NullPointerException
	 */
	public Socket borrowSocket() throws NullPointerException;
	
	/**
	 * 将连接归还给池
	 * @param socket
	 * @return
	 */
	public boolean returnSocket(Socket socket);
	
	/**
	 * 释放所有连接
	 */
	public void destroy();
}
