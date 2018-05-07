/**
 * $
 */
package com.wade.httprpc.test.client;

import java.io.IOException;

import com.wade.httprpc.client.DefaultHttpClient;
import com.wade.httprpc.test.obj.TestRequest;
import com.wade.httprpc.test.obj.TestResponse;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestHttpClient.java
 * @description: Http客户端测试类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-22
 */
public class TestObjClient {
	
	public static void main(String[] args) throws IOException {
		final String httpServerAddr = "http://127.0.0.1:8080/server";
		TestRequest req = new TestRequest();
		TestResponse tr = DefaultHttpClient.call(httpServerAddr, req, TestResponse.class);
		System.out.println(">>>>>>>>>>>>>:"+tr.toString());
		/*tr = DefaultHttpClient.call(httpServerAddr, new TestRequest());
		System.out.println(tr);
		tr = DefaultHttpClient.call(httpServerAddr, new TestRequest());
		System.out.println(tr);
		tr = DefaultHttpClient.call(httpServerAddr, new TestRequest());
		System.out.println(tr);
		tr = DefaultHttpClient.call(httpServerAddr, new TestRequest());
		System.out.println(tr);*/
		parallel(httpServerAddr);
	}
	
	
	public static void parallel(final String httpServerAddr) throws IOException {
		for (int i = 0; i < 10; i++) {
			new Thread() {
				public void run() {
					for (;;) {
						try {
							TestRequest tr = new TestRequest();
							byte[] b = new byte[1024*1024];
							for (int i = 0, max = 1024*1024; i < max; i++) {
								b[i] = 1;
							}
							tr.setBytes(b);
							DefaultHttpClient.call(httpServerAddr, tr, TestResponse.class);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	}
}
