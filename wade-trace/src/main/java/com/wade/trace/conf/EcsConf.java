package com.wade.trace.conf;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.trace.sample.ISample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: EcsConf
 * @description: 电渠探针配置
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class EcsConf {
	
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
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{ serviceName : '" + this.serviceName + "',");
		sb.append(" keys : '" + StringUtils.join(this.keys, ",") + "',");
		sb.append(" sample : " + this.sample + " }");
		return sb.toString();
		
	}
	
}
