package com.wade.trace.probe.impl;

import java.util.HashMap;
import java.util.Map;

import com.wade.trace.logsystem.LogKeys;
import com.wade.trace.logsystem.LogSystemUtil;
import com.wade.trace.probe.AbstractProbe;
import com.wade.trace.util.SystemUtil;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: 
 * @description: 服开探针
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class PfProbe extends AbstractProbe {

	/**
	 * 服务器IP
	 */
	private String ip;
		
	/**
	 * 进程名
	 */
	private String serverName;
	
	/**
	 * 服务名
	 */
	private String serviceName;
	
	@Override
	public void start() {
		super.start();
		super.setProbeType(PF);
		this.serverName = SystemUtil.getServerName();
		this.ip = SystemUtil.getIp(this.serverName);
	}
	
	/**
	 * 获取电渠主机IP
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 设置电渠主机IP 
	 * 
	 * @param echIp
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	/**
	 * 获取进程名
	 * 
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 设置进程名
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * 获取服务名
	 * 
	 * @return
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * 设置服务名
	 * 
	 * @param serviceName
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
		
	@Override
	public void logging() {
		
		Map<String, Object> logInfo = new HashMap<String, Object>();
		
		/** 公共基础参数 */
		logInfo.put(LogKeys.PROBE_TYPE, getProbeType());
		logInfo.put(LogKeys.ID, getId());
		logInfo.put(LogKeys.PARENT_ID, getParentId());
		logInfo.put(LogKeys.TRACE_ID, getTraceId());
		logInfo.put(LogKeys.OPER_ID, getOperId());
	    logInfo.put(LogKeys.START_TIME, String.valueOf(getStartTime()));
	    logInfo.put(LogKeys.END_TIME, String.valueOf(getEndTime()));
	    logInfo.put(LogKeys.COST_TIME, String.valueOf(getCostTime()));
	    logInfo.put(LogKeys.SUCCESS, isSuccess());
	    
	    logInfo.put(LogKeys.SERVICE_NAME, getServiceName());
	    logInfo.put(LogKeys.SERVER_NAME, getServerName());
	    
	    /** 特有参数 */
	    logInfo.put(LogKeys.IP, getIp());

		LogSystemUtil.send(logInfo);
		
	}
	
}
