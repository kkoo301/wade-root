package com.ailk.mq.client.config;

import com.ailk.common.config.GlobalCfg;
import com.ailk.mq.server.config.TaskDefinition;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ClientRuntimeEnv
 * @description: 客户端运行时环境
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class ClientRuntimeEnv {
	
	private static ClientRuntimeEnv instance = new ClientRuntimeEnv();
	public String serverName;
	public boolean productMode;

	private ClientRuntimeEnv() {
		this.serverName = System.getProperty("wade.server.name");
		if (null == this.serverName) {
			throw new IllegalArgumentException("JVM启动参数中未定义服务名，导致MQ客户端挂接不成功！定义方式： -Dwade.server.name=${xx}");
		}
		this.productMode = Boolean.parseBoolean(GlobalCfg.getProperty("broker.product.mode", "false"));
	}
	
	public static final ClientRuntimeEnv getInstance() {
		return instance;
	}
	
	public AsyncTask getAsyncTaskById(String taskId) {
		AsyncTask task = TaskDefinition.getAsyncTaskDefine(taskId);
		if (null == task) {
			throw new NullPointerException("taskId:" + taskId + ",没有获取到对应的任务定义!");
		}
		return task;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public boolean isProductMode() {
		return productMode;
	}
	
	/**
	 * 如果实例名中有"onlinetest", 说明是在线测试实例
	 * 
	 * @return
	 */
	public boolean isOnlineTest() {
		
		if (-1 != serverName.indexOf("onlinetest")) {
			return true;
		}
		
		return false;
	}
}
