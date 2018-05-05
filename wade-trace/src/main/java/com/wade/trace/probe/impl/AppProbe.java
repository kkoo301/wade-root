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
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class AppProbe extends AbstractProbe {
	
	/**
	 * APP服务器IP
	 */
	private String ip;
	
	/**
	 * 进程名
	 */
	private String serverName;
		
	/**
	 * 额外参数
	 */
	private Map<String, String> ext;
	
	@Override
	public void start() {
		super.start();
		super.setProbeType(APP);
		this.serverName = SystemUtil.getServerName();
		this.ip = SystemUtil.getIp(this.serverName);
	}
		
	/**
	 * 获取App主机IP
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
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
	 * 获取额外参数
	 * 
	 * @return
	 */
	public Map<String, String> getExt() {
		return ext;
	}

	/**
	 * 设置额外参数
	 * 
	 * @param ext
	 */
	public void setExt(Map<String, String> ext) {
		this.ext = ext;
	}
	
	@Override
	public void logging() {
		
		Map<String, Object> logInfo = new HashMap<String, Object>();
		
		/** 公共基础参数 */
		logInfo.put(LogKeys.PROBE_TYPE, getProbeType());
		logInfo.put(LogKeys.ID, getId());
		logInfo.put(LogKeys.PARENT_ID, getParentId());
		logInfo.put(LogKeys.TRACE_ID, getTraceId());
		logInfo.put(LogKeys.BIZ_ID, getBizId());
		logInfo.put(LogKeys.OPER_ID, getOperId());
	    logInfo.put(LogKeys.START_TIME, String.valueOf(getStartTime()));
	    logInfo.put(LogKeys.END_TIME, String.valueOf(getEndTime()));
	    logInfo.put(LogKeys.COST_TIME, String.valueOf(getCostTime()));
	    logInfo.put(LogKeys.SUCCESS, isSuccess());
	    
	    logInfo.put(LogKeys.SERVER_NAME, getServerName());
	    
	    /** 特有参数 */
	    logInfo.put(LogKeys.IP, getIp());
	    logInfo.put(LogKeys.EXT, getExt());

	    LogSystemUtil.send(logInfo);
		
	}

}
