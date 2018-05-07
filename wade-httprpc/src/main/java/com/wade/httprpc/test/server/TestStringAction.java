/**
 * $
 */
package com.wade.httprpc.test.server;

import javax.servlet.http.HttpServletRequest;

import com.wade.httprpc.server.IHttpAction;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestHttpAction.java
 * @description: 测试类, 验证IHttpActipn
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-18
 */
public class TestStringAction implements IHttpAction<String, String> {

	
	@Override
	public String execute(HttpServletRequest url, String request) {
		if ("sleep".equals(request)) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				return e.getMessage();
			}
		}
		return request;
	}
	
	@Override
	public String error(String request, Exception e) {
		return e.getMessage();
	}
	
}
