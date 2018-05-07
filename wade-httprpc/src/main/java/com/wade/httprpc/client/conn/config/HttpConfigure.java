/**
 * $
 */
package com.wade.httprpc.client.conn.config;


import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.wade.httprpc.client.conn.config.HttpConfigure;
import com.wade.httprpc.client.conn.HttpConnection;
import com.wade.httprpc.client.conn.HttpConnectionFactory;
import com.wade.httprpc.client.conn.HttpErrorCallback;
import com.wade.httprpc.client.conn.factory.HttpSocketConnectionFactory;
import com.wade.httprpc.client.conn.pool.HttpSocketPool;
import com.wade.httprpc.client.conn.pool.SocketPool;
import com.wade.httprpc.client.conn.pool.SocketPoolFactory;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpConfigure.java
 * @description: 设置HTTP的参数, 如请求头, 超时时长, URL地址, 字符集等
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-22
 */
public class HttpConfigure {
	
	/**
	 * Socket连接池默认初始大小
	 */
	private static final int SOCKET_POOL_INITSIZE = 10;
	/**
	 * Socket连接池默认最大值
	 */
	private static final int SOCKET_POOL_MAXSIZE = 100;
	/**
	 * Socket连接池默认增量值
	 */
	private static final int SOCKET_POOL_INCREMENTSIZE = 10;
	
	/**
	 * 默认的连接超时时长，单位是毫秒
	 */
	public static final int DEFAULT_CONNECT_TIMEOUT = 2000;
	/**
	 * 默认的数据读取时长，单位是毫秒
	 */
	public static final int DEFAULT_READ_TIMEOUT = 10000;
	
	/**
	 * 缓存URL地址
	 */
	private static Map<String, URL> urls = new HashMap<String, URL>(50);
	
	private String host = null;
	private int port = 80;
	private String path = null;
	
	/**
	 * 是否保持连接, 默认为不保持
	 */
	private boolean keepAlive = false;
	
	/**
	 * 创建连接超时时长, 单位毫秒
	 */
	private int connectTimeout = 2000;
	/**
	 * Socket读取籹据超时时长, 单位毫秒
	 */
	private int soTimeout = 10000;
	/**
	 * 请求头CONTENT-TYPE
	 */
	private String contentType = "application/octet-stream;charset=utf-8";
	/**
	 * 请求头CHARSTE
	 */
	private String charset = "UTF-8";
	
	private Map<String, String> header = new HashMap<String, String>();
	
	/**
	 * httpConnection工厂类
	 */
	private HttpConnectionFactory connectionFactory = new HttpSocketConnectionFactory();
	
	/**
	 * Socket连接池
	 */
	private SocketPool socketPool = null;
	
	private int poolInitSize = SOCKET_POOL_INITSIZE;
	private int poolMaxSize = SOCKET_POOL_MAXSIZE;
	private int poolIncrementSize = SOCKET_POOL_INCREMENTSIZE;
	
	/**
	 * HTTP服务地址
	 */
	private String httpServerAddr = null;
	
	private HttpErrorCallback callback = null;
	
	
	/**
	 * 数据类型, 默认为字符串
	 */
	private String dataType = "String";
	
	
	/**
	 * 创建配置对象
	 * @param httpServerAddr
	 * @throws MalformedURLException
	 */
	private HttpConfigure (String httpServerAddr) throws MalformedURLException {
		this.httpServerAddr = httpServerAddr;
		URL cacheUrl = getCacheURL(httpServerAddr);
		this.host = cacheUrl.getHost();
		this.port = cacheUrl.getPort();
		this.path = cacheUrl.getPath();
	}
	
	/**
	 * 创建HttpConfigure对象
	 * @param httpServerAddr
	 * @return
	 * @throws MalformedURLException
	 */
	public static HttpConfigure build(String httpServerAddr) throws MalformedURLException {
		return new HttpConfigure(httpServerAddr);
	}
	
	/**
	 * 创建HttpConfigure对象，可设置读取超时时间
	 * @param httpServerAddr
	 * @param soTimeout
	 * @return
	 * @throws MalformedURLException
	 */
	public static HttpConfigure build(String httpServerAddr, int soTimeout) throws MalformedURLException {
		HttpConfigure config =  new HttpConfigure(httpServerAddr);
		config.setSoTimeout(soTimeout);
		return config;
	}
	
	/**
	 * @return the httpServerAddr
	 */
	public String getHttpServerAddr() {
		return httpServerAddr;
	}
	
	/**
	 * 创建Connection对象
	 * @return
	 */
	public <I extends Serializable, O extends Serializable> HttpConnection createConnection() {
		return connectionFactory.createHttpConnection(this);
	}
	
	
	/**
	 * @param keepAlive the keepAlive to set
	 */
	public HttpConfigure setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	
	public HttpConfigure setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}
	
	/**
	 * @param readTimeout the readTimeout to set
	 */
	public HttpConfigure setSoTimeout(int readTimeout) {
		this.soTimeout = readTimeout;
		return this;
	}
	
	/**
	 * @param contentType the contentType to set
	 */
	public HttpConfigure setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	
	/**
	 * @param charset the charset to set
	 */
	public HttpConfigure setCharset(String charset) {
		this.charset = charset;
		return this;
	}
	
	/**
	 * 添加Http的Header属性
	 * @param key
	 * @param value
	 * @return
	 */
	public HttpConfigure addHeader(String key, String value) {
		header.put(key, value);
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getHeader() {
		return header;
	}
	
	
	/**
	 * @param poolInitSize the poolInitSize to set
	 */
	public HttpConfigure setPoolInitSize(int poolInitSize) {
		this.poolInitSize = poolInitSize;
		return this;
	}
	
	/**
	 * @param poolMaxSize the poolMaxSize to set
	 */
	public HttpConfigure setPoolMaxSize(int poolMaxSize) {
		this.poolMaxSize = poolMaxSize;
		return this;
	}
	
	/**
	 * @param poolIncrementSize the poolIncrementSize to set
	 */
	public HttpConfigure setPoolIncrementSize(int poolIncrementSize) {
		this.poolIncrementSize = poolIncrementSize;
		return this;
	}
	
	/**
	 * @return the socketPool
	 */
	public SocketPool getSocketPool() {
		return socketPool;
	}
	
	
	/**
	 * 创建指定大小的Socket池, Socket池只能创建一次, 并强制使用KeepAlive
	 * @param initSize
	 * @return
	 * @throws IOException
	 */
	public HttpConfigure createSocketPool() throws IOException {
		if (null == this.socketPool) {
			this.socketPool = SocketPoolFactory.createSocketPool(httpServerAddr, HttpSocketPool.class, poolInitSize, poolMaxSize, poolIncrementSize);
			this.setKeepAlive(true);
		}
		return this;
	}
	
	/**
	 * 创建指定SocketPool实现的池, 并强制使用KeepAlive
	 * @param socketPool the socketPool to set
	 */
	public HttpConfigure setSocketPool(Class<?> socketPool) throws IOException {
		if (null == this.socketPool) {
			this.socketPool = SocketPoolFactory.createSocketPool(httpServerAddr, socketPool, poolInitSize, poolMaxSize, poolIncrementSize);
			this.setKeepAlive(true);
		}
		return this;
	}
	
	/**
	 * @param dataType the dataType to set
	 */
	public HttpConfigure setDataType(String dataType) {
		this.dataType = dataType;
		return this;
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * @return the keepAlive
	 */
	public boolean isKeepAlive() {
		return keepAlive;
	}
	
	/**
	 * @return the connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}
	
	
	/**
	 * @return the readTimeout
	 */
	public int getSoTimeout() {
		return soTimeout;
	}
	
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	
	
	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}
	
	/**
	 * @return the factory
	 */
	public HttpConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}
	
	/**
	 * @param connectionFactory the connectionFactory to set
	 */
	public HttpConfigure setConnectionFactory(HttpConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		return this;
	}
	
	public HttpErrorCallback getCallback() {
		return this.callback;
	}
	
	public HttpConfigure setCallback(HttpErrorCallback callback) {
		this.callback = callback;
		return this;
	}
	
	
	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}
	
	
	private static URL getCacheURL(String url) throws MalformedURLException {
		URL cacheUrl = urls.get(url);
		if (cacheUrl == null) {
			try {
				cacheUrl = new URL(url);
				urls.put(url, cacheUrl);
			} catch (MalformedURLException e) {
				throw e;
			}
		}
		return cacheUrl;
	}

}
