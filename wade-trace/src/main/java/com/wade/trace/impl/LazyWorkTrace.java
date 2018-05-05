package com.wade.trace.impl;

import com.ailk.common.data.IData;
import com.wade.trace.ITrace;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LazyWorkTrace
 * @description: 懒散应付型
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class LazyWorkTrace implements ITrace {

	@Override
	public void collectException(String staffId, String serviceName, String errinfo) {
		// 什么也不做
	}
	
	@Override
	public void logBrowserProbe(String id, String traceid, String statuscode, String starttime, String endtime, String ieVer) {
		// 什么也不做
	}
	
	@Override
	public void startWebProbe(String bizId, String operId, String sessionId, String clientIp, String url, String menuId, IData param) {
		// 什么也不做
	}

	@Override
	public void stopWebProbe(boolean success) {
		// 什么也不做
	}
	
	@Override
	public void startAppProbe(String traceId, String parentId, String bizId, String operId, String mainServiceName, IData param) {
		// 什么也不做		
	}

	@Override
	public void stopAppProbe(boolean success) {
		// 什么也不做		
	}

	@Override
	public void startMainServiceProbe(String traceId, String parentId, String bizId, String operId, String serviceName) {
		// 什么也不做
	}

	@Override
	public void startSubServiceProbe(String serviceName) {
		// 什么也不做
	}

	@Override
	public void stopServiceProbe(boolean success) {
		// 什么也不做
	}
	
	@Override
	public void startDaoProbe(String dataSource, long dccost, String sqlName, String sql, IData param) {
		// 什么也不做
	}

	@Override
	public void stopDaoProbe(boolean success) {
		// 什么也不做
	}

	@Override
	public void startEcsProbe(String serviceName, String operId) {
		// 什么也不做
	}

	@Override
	public void stopEcsProbe(boolean success) {
		// 什么也不做
	}

	@Override
	public void startIbsProbe(String serviceName, String operId) {
		// 什么也不做
	}

	@Override
	public void stopIbsProbe(boolean success) {
		// 什么也不做
	}

	@Override
	public void startPfProbe(String serviceName, String traceId, String parentId, String operId)	{
		// 什么也不做
	}

	@Override
	public void stopPfProbe(boolean success) {
		// 什么也不做
	}

	@Override
	public void startUipProbe(String serviceName, String traceId, String parentId, String operId) {
		// 什么也不做		
	}

	@Override
	public void stopUipProbe(boolean success) {
		// 什么也不做
	}

	@Override
	public String getTraceId() {
		return "";
	}

	@Override
	public String getId() {
		return "";
	}
	
	@Override
	public String getBrowserId() {
		return "";
	}
	
	public void menuClick(String timestamp, String staffId, String menuId) {
		
	}

}
