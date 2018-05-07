/**
 * $
 */
package com.wade.httprpc.test.server;

import javax.servlet.http.HttpServletRequest;

import com.wade.httprpc.server.IHttpAction;
import com.wade.httprpc.test.obj.TestRequest;
import com.wade.httprpc.test.obj.TestResponse;

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
public class TestObjAction implements IHttpAction<TestRequest, TestResponse> {

	
	@Override
	public TestResponse execute(HttpServletRequest url, TestRequest request) {
		TestResponse tr = new TestResponse();
		
		byte[] b = new byte[1024*1024];
		for (int i = 0, max = 1024*1024; i < max; i++) {
			b[i] = 1;
		}
		
		tr.setBytes(b);
		
		return tr;
	}
	
	@Override
	public TestResponse error(TestRequest request, Exception e) {
		return new TestResponse(-1, "ERROR:" + e.getMessage());
	}
	
}
