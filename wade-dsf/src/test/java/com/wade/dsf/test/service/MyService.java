/**
 * $
 */
package com.wade.dsf.test.service;

import com.wade.dsf.exception.DsfException;
import com.wade.dsf.test.invoker.MyRequest;
import com.wade.dsf.test.invoker.MyResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: MyService.java
 * @description: 测试服务
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-20
 */
public class MyService {
	
	public String hello(String request) throws Exception {
		return "hello world";
	}
	
	public MyResponse hello(MyRequest request) throws Exception {
		if (request.isThrowEcxeption())
			throw new DsfException("111", "2222",  new Exception("xxx"));
		return new MyResponse();
	}

}
