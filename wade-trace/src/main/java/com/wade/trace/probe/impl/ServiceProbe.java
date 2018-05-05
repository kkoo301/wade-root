package com.wade.trace.probe.impl;

import java.util.HashMap;
import java.util.Map;

import com.ailk.mq.util.KafkaUtil;
import com.wade.trace.logsystem.LogKeys;
import com.wade.trace.logsystem.LogSystemUtil;
import com.wade.trace.probe.AbstractProbe;
import com.wade.trace.util.IOUtil;
import com.wade.trace.util.SystemUtil;
import com.wade.relax.registry.SystemRuntime;

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
public class ServiceProbe extends AbstractProbe {
	
	/**
	 * 服务名
	 */
	private String serviceName;

	private boolean mainService = false;
	
	public boolean isMainService() {
		return mainService;
	}

	public void setMainService(boolean mainService) {
		this.mainService = mainService;
	}

	@Override
	public void start() {
		super.start();
		super.setProbeType(SERVICE);
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
		logInfo.put(LogKeys.BIZ_ID, getBizId());
		logInfo.put(LogKeys.OPER_ID, getOperId());
	    logInfo.put(LogKeys.START_TIME, String.valueOf(getStartTime()));
	    logInfo.put(LogKeys.END_TIME, String.valueOf(getEndTime()));
	    logInfo.put(LogKeys.COST_TIME, String.valueOf(getCostTime()));
	    logInfo.put(LogKeys.SUCCESS, isSuccess());
	    
	    /** 特有参数 */
	    logInfo.put(LogKeys.SERVICE_NAME, getServiceName());
	    logInfo.put(LogKeys.CENTER_NAME, SystemRuntime.getCenterName());
	    logInfo.put("mainservice", isMainService());

		LogSystemUtil.send(logInfo);
		
	}

}
