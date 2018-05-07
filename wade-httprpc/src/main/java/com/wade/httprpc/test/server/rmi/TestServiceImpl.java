/**
 * $
 */
package com.wade.httprpc.test.server.rmi;

import java.util.Date;

import com.wade.httprpc.test.common.rmi.ITestService;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestServiceImpl.java
 * @description: 测试服务实现类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public class TestServiceImpl implements ITestService {

	
	@Override
	public String service(String name, boolean status, Date createTime, int length) throws Exception {
		return new StringBuilder("AI-CRM-CS-GROUP:")
			.append("name=").append(name).append(",")
			.append("status=").append(status).append(",")
			.append("createTime=").append(createTime).append(",")
			.append("length=").append(length)
			.toString();
	}

}
