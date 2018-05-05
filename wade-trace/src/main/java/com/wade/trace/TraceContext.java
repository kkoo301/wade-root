package com.wade.trace;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.wade.trace.conf.EcsConf;
import com.wade.trace.conf.IbsConf;
import com.wade.trace.conf.PfConf;
import com.wade.trace.conf.UipConf;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TraceContext
 * @description: 跟踪上下文
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class TraceContext {
	
	private static ITrace trace;
	private static String logDirectory;
	private static int maxBackupIndex;
	private static int bufferSize;
	private static final long exceptionSampleDenom;
	private static long exceptionCursor = 0;
	
	private static Map<String, EcsConf> ecsConfs = new HashMap<String, EcsConf>();
	private static Map<String, IbsConf> ibsConfs = new HashMap<String, IbsConf>();
	private static Map<String, UipConf> uipConfs = new HashMap<String, UipConf>();
	private static Map<String, PfConf>   pfConfs = new HashMap<String, PfConf>();
	
	private static Map<String, String> mapping;
	
	static {
		
		TraceXml xml = new TraceXml();
		xml.load();
		String traceClazz = xml.getTraceClazz();
		
		try {
			Class<?> clazz = Class.forName(traceClazz);
			trace = (ITrace) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logDirectory = xml.getLogDirectory();
		maxBackupIndex = xml.getMaxBackupIndex();
		bufferSize = xml.getBufferSize();
		exceptionSampleDenom = xml.getExceptionSampleDenom();
		
		ecsConfs = xml.getEcsConf();
		ibsConfs = xml.getIbsConf();
		uipConfs = xml.getUipConf();
		pfConfs  = xml.getPfConf();
		
		mapping = xml.getMapping();
		
	}
	
	public static ITrace getTrace() {
		return trace;
	}

	public static int getMaxBackupIndex() {
		return maxBackupIndex;
	}

	public static String getLogDirectory() {
		return logDirectory;
	}
	
	public static Map<String, EcsConf> getEcsConf() {
		return ecsConfs;
	}
	
	public static Map<String, IbsConf> getIbsConf() {
		return ibsConfs;
	}
	
	public static Map<String, UipConf> getUipConf() {
		return uipConfs;
	}
	
	public static Map<String, PfConf> getPfConf() {
		return pfConfs;
	}
	
	public static Map<String, String> getMapping() {
		return mapping;
	}
	
	public static int getBufferSize() {
		return bufferSize;
	}
	
	/**
	 * 采集异常信息日志
	 * 
	 * @param staffId
	 * @param errinfo
	 */
	public static final void collectException(String staffId, String serviceName, String errinfo) {
		
		if (0 != exceptionCursor++ % exceptionSampleDenom) {
			return;
		}
		
		trace.collectException(staffId, serviceName, errinfo);
	}
	
	/**
	 * 菜单点击日志
	 * 
	 * @param timestamp
	 * @param staffId
	 * @param menuId
	 */
	public static final void menuClick(String timestamp, String staffId, String menuId) {
		trace.menuClick(timestamp, staffId, menuId);
	}
	
	/**
	 * 记录浏览器端的探针
	 * 
	 * @param id
	 * @param traceid
	 * @param statuscode
	 * @param starttime
	 * @param endtime
	 */
	public static final void logBrowserProbe(String id, String traceid, String statuscode, String starttime, String endtime, String ieVer) {
		trace.logBrowserProbe(id, traceid, statuscode, starttime, endtime, ieVer);
	}
	
	/**
	 * 开启WEB探针
	 * 
	 * @param bizId
	 * @param operId
	 * @param sessionId
	 * @param clientIp
	 * @param url
	 * @param menuId
	 * @param param
	 */
	public static final void startWebProbe(String bizId, String operId, String sessionId, String clientIp, String url, String menuId, IData param) {
		trace.startWebProbe(bizId, operId, sessionId, clientIp, url, menuId, param);
	}

	/**
	 * 关闭WEB探针
	 * 
	 * @param success
	 */
	public static final void stopWebProbe(boolean success) {
		trace.stopWebProbe(success);
	}
	
	/**
	 * 开启APP探针
	 * 
	 * @param traceId
	 * @param parentId
	 * @param bizId
	 * @param operId
	 * @param mainServiceName
	 * @param param
	 */
	public static final void startAppProbe(String traceId, String parentId, String bizId, String operId, String mainServiceName, IData param) {
		trace.startAppProbe(traceId, parentId, bizId, operId, mainServiceName, param);
	}

	/**
	 * 关闭APP探针
	 * 
	 * @param success
	 */
	public static final void stopAppProbe(boolean success) {
		trace.stopAppProbe(success);
	}

	/**
	 * 开启(主)服务探针
	 * 
	 * @param traceId
	 * @param serviceName
	 */
	public static final void startMainServiceProbe(String traceId, String parentId, String bizId, String operId, String serviceName) {
		trace.startMainServiceProbe(traceId, parentId, bizId, operId, serviceName);
	}

	/**
	 * 开启(子)服务探针
	 * @param serviceName
	 */
	public static final void startSubServiceProbe(String serviceName) {
		trace.startSubServiceProbe(serviceName);
	}

	/**
	 * 关闭服务探针
	 * 
	 * @param success
	 */
	public static final void stopServiceProbe(boolean success) {
		trace.stopServiceProbe(success);
	}
	
	/**
	 * 开启DAO探针
	 * 
	 * @param dataSource
	 * @param dccost
	 * @param sqlName
	 * @param sql
	 * @param param
	 */
	public static final void startDaoProbe(String dataSource, long dccost, String sqlName, String sql, IData param) {
		trace.startDaoProbe(dataSource, dccost, sqlName, sql, param);
	}
	
	/**
	 * 关闭DAO探针
	 * 
	 * @param success
	 */
	public static final void stopDaoProbe(boolean success) {
		trace.stopDaoProbe(success);
	}
	
	/**
	 * 开启ECS探针
	 * 
	 * @param serviceName
	 * @param operId
	 */
	public static final void startEcsProbe(String serviceName, String operId) {
		trace.startEcsProbe(serviceName, operId);
	}
	
	/**
	 * 关闭ECS探针
	 * 
	 * @param success
	 */
	public static final void stopEcsProbe(boolean success) {
		trace.stopEcsProbe(success);
	}
	
	/**
	 * 开启IBS探针
	 * 
	 * @param serviceName
	 * @param operId
	 */
	public static final void startIbsProbe(String serviceName, String operId) {
		trace.startIbsProbe(serviceName, operId);
	}
	
	/**
	 * 关闭IBS探针
	 * 
	 * @param success
	 */
	public static final void stopIbsProbe(boolean success) {
		trace.stopIbsProbe(success);
	}
	
	/**
	 * 开启PF探针
	 * 
	 * @param serviceName
	 * @param traceId
	 * @param parentId
	 * @param operId
	 */
	public static final void startPfProbe(String serviceName, String traceId, String parentId, String operId) {
		trace.startPfProbe(serviceName, traceId, parentId, operId);
	}
	
	/**
	 * 关闭PF探针
	 * 
	 * @param success
	 */
	public static final void stopPfProbe(boolean success) {
		trace.stopPfProbe(success);
	}
	
	/**
	 * 开启UIP探针
	 * 
	 * @param serviceName
	 * @param traceId
	 * @param parentId
	 * @param operId
	 */
	public static final void startUipProbe(String serviceName, String traceId, String parentId, String operId) {
		trace.startUipProbe(serviceName, traceId, parentId, operId);
	}
	
	/**
	 * 关闭UIP探针
	 * 
	 * @param success
	 */
	public static final void stopUipProbe(boolean success) {
		trace.stopUipProbe(success);
	}
	
	/**
	 * 获取追踪ID，作为X_TRACE_ID参数透传至下游环节
	 * 
	 * @return
	 */
	public static final String getTraceId() {
		return trace.getTraceId();
	}
	
	/**
	 * 获取当前探针的ID，作为X_PTRACE_ID参数透传至下游环节
	 * 
	 * @return
	 */
	public static final String getCurrentProbeId() {
		return trace.getId();
	}
	
	/**
	 * 获取浏览器探针ID
	 * 
	 * @return
	 */
	public static final String getBrowserId() {
		return trace.getBrowserId();
	}
			
}