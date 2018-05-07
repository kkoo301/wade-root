package com.ailk.mq.util;

import java.util.Map;

import com.ailk.mq.client.LogInfo;
import com.ailk.org.apache.commons.lang3.SerializationUtils;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: LogUtil
 * @description: 日志构建工具
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-8-2
 */
public final class LogUtil {
	
	/**
	 * 构造发送日志信息
	 * 
	 * @param logId
	 * @param taskId
	 * @param destination
	 * @param param
	 * @return
	 */
	public static final byte[] buildSendLog(String logId, String taskId, String destination, Map<String, Object> param) {
		LogInfo info = new LogInfo();
		info.setAction(LogInfo.ACTION_SEND);
		info.setLogid(logId);
		info.setTaskid(taskId);
		info.setExecutor(destination);
		info.setSendTime(System.currentTimeMillis());
		info.setParam(param);
		return SerializationUtils.serialize(info);
	}
	
	/**
	 * 构造超时日志信息
	 * 
	 * @param logId
	 * @param taskId
	 * @param serverName
	 * @return
	 */
	public static final byte[] buildTimeoutLog(String logId, String taskId, String serverName) {
		LogInfo info = new LogInfo();
		info.setAction(LogInfo.ACTION_TIMEOUT);
		info.setLogid(logId);
		info.setTaskid(taskId);
		info.setExecutor(serverName);
		info.setStartTime(System.currentTimeMillis());
		return SerializationUtils.serialize(info);
	}

	/**
	 * 构造未准备好日志
	 * 
	 * @param logId
	 * @param taskId
	 * @param serverName
	 * @return
	 */
	public static final byte[] buildNotPreparedLog(String logId, String taskId, String serverName) {
		LogInfo info = new LogInfo();
		info.setAction(LogInfo.ACTION_NOT_PREPARED);
		info.setLogid(logId);
		info.setTaskid(taskId);
		info.setExecutor(serverName);
		info.setStartTime(System.currentTimeMillis());
		return SerializationUtils.serialize(info);
	}
	
	/**
	 * 构造开始日志信息
	 * 
	 * @param logId
	 * @param taskId
	 * @param serverName
	 * @return
	 */
	public static final byte[] buildStartLog(String logId, String taskId, String serverName) {
		LogInfo info = new LogInfo();
		info.setAction(LogInfo.ACTION_START);
		info.setLogid(logId);
		info.setTaskid(taskId);
		info.setExecutor(serverName);
		info.setStartTime(System.currentTimeMillis());
		return SerializationUtils.serialize(info);
	}
	
	/**
	 * 构造结束日志信息
	 * 
	 * @param logId
	 * @param resultInfo
	 * @param state
	 * @return
	 */
	public static final byte[] buildEndLog(String logId, String resultInfo, String state) {
		LogInfo info = new LogInfo();
		info.setAction(LogInfo.ACTION_END);
		info.setLogid(logId);
		info.setEndTime(System.currentTimeMillis());
		info.setResultInfo(resultInfo);
		info.setState(state);
		return SerializationUtils.serialize(info);
	}	
}
