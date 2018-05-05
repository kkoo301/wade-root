package com.wade.trace.sample.impl;

import com.wade.trace.sample.AbstractSample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AppSample
 * @description: App采样
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class AppSample extends AbstractSample {
	
	/**
	 * 服务名
	 */
	private String serviceName;

	public AppSample(String serviceName, long sampleDenom) {
		super(sampleDenom);
		this.serviceName = serviceName;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	@Override
	public String toString() {
		return "{ serviceName : " + this.serviceName + ", sample_denom : " + getSampleDenom() + "}";		
	}
	
}
