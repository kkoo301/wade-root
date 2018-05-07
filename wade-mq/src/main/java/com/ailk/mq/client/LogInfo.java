package com.ailk.mq.client;

import java.io.Serializable;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: LogInfo
 * @description: 日志信息
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class LogInfo implements Serializable {
	
	private static final long serialVersionUID = 963523883212217293L;

	/**
	 * 发送
	 */
	public static final int ACTION_SEND = 0;
	
	/**
	 * 开始
	 */
	public static final int ACTION_START = 1;
	
	/**
	 * 结束
	 */
	public static final int ACTION_END = 2;
	
	/**
	 * 超时
	 */
	public static final int ACTION_TIMEOUT = 3;
	
	/**
	 * 为预热完，收到消息后不做处理
	 */
	public static final int ACTION_NOT_PREPARED = 4;
	
	private int action;
	private String logid;
	private String taskid;
	private String executor;
	private long sendTime;
	private long startTime;
	private long endTime;
	private String resultInfo;
	private String state;
	
	private Map<String, Object> param;

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	public String getLogid() {
		return logid;
	}

	public void setLogid(String logid) {
		this.logid = logid;
	}
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
}
