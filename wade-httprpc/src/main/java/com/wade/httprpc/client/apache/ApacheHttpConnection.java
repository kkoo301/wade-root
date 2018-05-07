/**
 * $
 */
package com.wade.httprpc.client.apache;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.wade.httprpc.client.conn.HttpConnection;
import com.wade.httprpc.client.conn.HttpErrorCallback;
import com.wade.httprpc.client.conn.config.HttpConfigure;
import com.wade.httprpc.util.SerializeUtil;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ApacheHttpClient.java
 * @description: 采用Apache的HttpClient实现
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-5-17
 */
public class ApacheHttpConnection implements HttpConnection {
	
	private HttpConfigure configure = null;
	
	public ApacheHttpConnection(HttpConfigure configure) {
		this.configure = configure;
	}
	
	@Override
	public Socket createSocket() throws IOException {
		return null;
	}

	@Override
	public String post(String request) throws IOException {
		return post(request, String.class);
	}
	
	/* (non-Javadoc)
	 * @see com.wade.httprpc.client.conn.HttpConnection#get(java.lang.String)
	 */
	@Override
	public String get(String request) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {		
			HttpGet httpGet = new HttpGet(configure.getHttpServerAddr() + "?" + request);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			
			int status = httpResponse.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == status) {
				return EntityUtils.toString(httpResponse.getEntity());
			} else {
				throw new IOException(String.format("请求状态异常%s，返回值%d", configure.getHttpServerAddr() + "?" + request, status));
			}
		} finally {
			httpClient.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T post(Serializable request, Class<T> clazz) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(configure.getHttpServerAddr());
			httpPost.setHeader("Content-Type", configure.getContentType());
			httpPost.setHeader("Connection", configure.isKeepAlive() ? "Keep-Alive" : "close");
			
			RequestConfig config = RequestConfig.custom()
					.setSocketTimeout(configure.getSoTimeout())
					.setConnectTimeout(configure.getConnectTimeout())
					.build();
			httpPost.setConfig(config);
			
			byte[] data = null;
			if (request instanceof String) {
				data = ((String) request).getBytes(configure.getCharset());
			} else {
				data = SerializeUtil.serialize(request);
			}
			httpPost.setEntity(new ByteArrayEntity(data));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				InputStream is = httpResponse.getEntity().getContent();
				
				if (String.class.equals(clazz)) {
					return (T) EntityUtils.toString(httpResponse.getEntity());
				}
				
				return (T)SerializeUtil.deserialize(is);
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				throw new IOException(result);
			}
		} finally {
			httpClient.close();
		}
	}

	@Override
	public void closeSocket(Socket socket) throws IOException {
		
	}
	
	
	@Override
	public HttpErrorCallback getCallBack() {
		return null;
	}

}
