package com.wade.trace.conf;

import com.wade.trace.sample.ISample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AppConf
 * @description: App配置类
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class AppConf {
	
	/**
	 * 服务名
	 */
	private String serviceName;
	
	/**
	 * 动态参数
	 */
	private String[] keys;
	
	/**
	 * 采样对象
	 */
	private ISample sample;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String[] getKeys() {
		return keys;
	}
	
	public void setKeys(String[] keys) {
		this.keys = keys;
	}
	
	public ISample getSample() {
		return sample;
	}
	
	public void setSample(ISample sample) {
		this.sample = sample;
	}
}
