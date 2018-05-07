/**
 * $
 */
package com.wade.dsf.test.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.wade.dsf.test.invoker.MyRequest;
import com.wade.httprpc.util.SerializeUtil;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpCaller.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-20
 */
public class HttpCaller {
	
	public static void main(String[] args) throws Exception {
		post("http://127.0.0.1:8080/dsf/MyService2");
		get("http://127.0.0.1:8080/dsf/MyService");
	}

	public static void get(String serverAddr) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom().build();
		try {
			
			StringBuilder params = new StringBuilder(100);
			params.append(serverAddr);
			params.append("?");
			params.append("name=");
			params.append("liaos");
			params.append("&addr=");
			params.append(URLEncoder.encode("湖南长沙", "utf-8"));
			
			HttpGet httpGet = new HttpGet(serverAddr);
			httpGet.setHeader("Context-Type", "text/java-string");
			httpGet.setHeader("Connection", "close");
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				InputStream is = httpResponse.getEntity().getContent();
				Serializable s = SerializeUtil.deserialize(is);
				print(serverAddr, s);
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				throw new IOException(result);
			}
		} finally {
			httpClient.close();
		}
	}
	
	
	public static void post(String serverAddr) throws Exception {
		MyRequest request = new MyRequest();
		request.setThrowEcxeption(true);
		
		CloseableHttpClient httpClient = HttpClients.custom().build();
		try {		
			HttpPost httpPost = new HttpPost(serverAddr);
			httpPost.setHeader("Context-Type", "binary/java-stream");
			httpPost.setHeader("Connection", "close");
			
			byte[] data = SerializeUtil.serialize(request);
			httpPost.setEntity(new ByteArrayEntity(data));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				InputStream is = httpResponse.getEntity().getContent();
				Serializable s = SerializeUtil.deserialize(is);
				print(serverAddr, s);
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				throw new IOException(result);
			}
		} finally {
			httpClient.close();
		}
	}
	
	private static void print(String serverAddr, Serializable response) {
		System.out.println("打印服务返回内容：" + serverAddr);
		if (response instanceof Throwable) {
			((Throwable)response).printStackTrace();
		} else {
			System.out.println(response.getClass().getName() + "=" + response.toString());
		}
		System.out.println();
	}
}
