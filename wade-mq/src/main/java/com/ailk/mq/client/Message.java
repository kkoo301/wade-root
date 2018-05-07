package com.ailk.mq.client;

import java.io.Serializable;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: Message
 * @description: 消息体
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class Message implements Serializable {
	
	private static final long serialVersionUID = -3351206321279291834L;

	/**
	 * 任务ID
	 */
	private String taskId;

	/**
	 * 任务日志ID
	 */
	private String taskLogId;
	
	/**
	 * 发送时间
	 */
	private long sendtime;
	
	/**
	 * 超时秒数
	 */
	private long ttl;
	
	/**
	 * 超时时间(服务超时时间)
	 */
	private long timeoutSecond;

	/**
	 * 业务参数
	 */
	private Map<String, Object> param;
		
	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}
	
	public long getTimeoutSecond() {
		return timeoutSecond;
	}

	public void setTimeoutSecond(long timeoutSecond) {
		this.timeoutSecond = timeoutSecond;
	}

	public String getTaskLogId() {
		return taskLogId;
	}

	public void setTaskLogId(String taskLogId) {
		this.taskLogId = taskLogId;
	}
	
	public long getSendtime() {
		return sendtime;
	}

	public void setSendtime(long sendtime) {
		this.sendtime = sendtime;
	}
	
	public String toString() {
		StringBuilder sbuff = new StringBuilder();
		sbuff.append("{ taskId=" + taskId);
		sbuff.append(", taskLogId=" + taskLogId);
		sbuff.append(", sendtime=" + sendtime);
		sbuff.append(", ttl=" + ttl);
		sbuff.append(", timeoutSecond=" + timeoutSecond);
		sbuff.append("}");
		return sbuff.toString();
	}
}
