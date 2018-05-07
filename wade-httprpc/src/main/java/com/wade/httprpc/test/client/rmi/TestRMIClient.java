/**
 * $
 */
package com.wade.httprpc.test.client.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;

import com.wade.httprpc.client.conn.config.HttpConfigure;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestRMIClient.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public class TestRMIClient {

	public static void main(String[] args) throws MalformedURLException, IOException {
		String httpServerAddr = "http://127.0.0.1:8080/service";
		String serviceName = "Crm.TestService";
		
		HashMap<String, Serializable> request = new HashMap<String, Serializable>();
		//服务名
		request.put("SERVICE_NAME", serviceName);
		
		//业务参数
		request.put("name", "何三谢");
		request.put("status", "true");
		request.put("length", "10");
		request.put("createTime", new Date());
		
		HashMap<?, ?> response = HttpConfigure
				.build(httpServerAddr)
				.createConnection()
				.post(request, HashMap.class);
		
		Exception e = (Exception) response.get("X_EXCEPTION");
		if (null != e) {
			e.printStackTrace();
		} else {
			System.out.println(response);
		}
	}
}
