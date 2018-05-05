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
public class WebProbe extends AbstractProbe {

	/**
	 * 会话ID
	 */
	private String sessionId;

	/**
	 * 客户端IP
	 */
	private String clientIp;
	
	/**
	 * WEB服务器IP
	 */
	private String ip;
	
	/**
	 * 进程名
	 */
	private String serverName;
	
	/**
	 * 页面URL
	 */
	private String url;
	
	/**
	 * 菜单ID
	 */
	private String menuid;
		
	/**
	 * 额外参数
	 */
	private Map<String, String> ext;
	
	@Override
	public void start() {
		super.start();
		super.setProbeType(WEB);
		this.serverName = SystemUtil.getServerName();
		this.ip = SystemUtil.getIp(this.serverName);
	}
	
	/**
	 * 获取会话ID
	 * 
	 * @return
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * 设置会话ID
	 * 
	 * @param sessionId
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * 获取客户端IP
	 * 
	 * @return
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * 设置客户端IP
	 * 
	 * @param clientIp
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	/**
	 * 获取WEB主机IP
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 返回WEB进程名
	 * 
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 获取页面URL
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置页面URL
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取菜单ID
	 * 
	 * @return
	 */
	public String getMenuid() {
		return menuid;
	}

	/**
	 * 设置菜单ID
	 * 
	 * @param menuid
	 */
	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}
	
	/**
	 * 获取额外信息
	 * 
	 * @return
	 */
	public Map<String, String> getExt() {
		return ext;
	}

	/**
	 * 设置额外信息
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
	    logInfo.put(LogKeys.SESSION_ID, getSessionId());
	    logInfo.put(LogKeys.CLIENT_IP, getClientIp());
		logInfo.put(LogKeys.IP, getIp());
		logInfo.put(LogKeys.URL, getUrl());
		logInfo.put(LogKeys.MENU_ID, getMenuid());
		
	    logInfo.put(LogKeys.EXT, getExt());

		LogSystemUtil.send(logInfo);
		
	}

}
