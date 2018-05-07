/**
 * $
 */
package com.wade.httprpc.client.conn;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IHttpConnection.java
 * @description: HTTP请求接口对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-23
 */
public interface HttpConnection {
	
	/**
	 * 创建Socket对象
	 * @return
	 * @throws IOException
	 */
	public Socket createSocket() throws IOException;
	
	/**
	 * 发送Post请求，请求内容为字符串
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String post(String request) throws IOException;
	
	/**
	 * 发送Get请求，请求内容为字符串
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String get(String request) throws IOException;
	
	
	/**
	 * 发送POST请求，请求内容为可序列化的对象
	 * @param request
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public <T extends Serializable> T post(Serializable request, Class<T> clazz) throws IOException;
	
	/**
	 * 关闭Socket连接
	 * @param socket
	 * @throws IOException
	 */
	public void closeSocket(Socket socket) throws IOException;
	
	/**
	 * 异常回调接口
	 * @param callback
	 */
	public HttpErrorCallback getCallBack();
}
