package com.wade.relax.registry.consumer.lb;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;

import io.netty.handler.codec.http.HttpHeaders.Values;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.relax.registry.consumer.SockSite;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: SockSite健康检查
 * @auth: zhoulin2
 * @date: 2015-11-30
 */
public final class HealthCheck {
	
	private static final Logger LOG = LoggerFactory.getLogger(HealthCheck.class);
	
	public static final boolean isActive(SockSite sockSite) {
		
		GetMethod httpMethod = null;
		
		try {
			
			httpMethod = new GetMethod(sockSite.toString() + "/probe.jsp");
			httpMethod.setRequestHeader(CONNECTION, Values.CLOSE);
			
			HttpClient httpclient = new HttpClient();
			int statusCode = httpclient.executeMethod(httpMethod);
			System.out.println("statusCode=" + statusCode);
			if (HttpStatus.SC_OK == statusCode) {
				return true;
			}
	        
		} catch (Exception e) {
			LOG.error("健康检查发生错误! sockSite: " + sockSite.toString(), e);
		} finally {			
			if (null != httpMethod) {
				httpMethod.releaseConnection();
			}	
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println("--------------");
		SockSite ss = new SockSite("10.200.138.3", 10002);
		System.out.println(isActive(ss));
		System.out.println("--------------");
	}
	
}
