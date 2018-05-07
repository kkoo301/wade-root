package com.ailk.mq.client.config;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: AsyncTask
 * @description: 异步任务
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class AsyncTask implements Serializable {
	
	private static final long serialVersionUID = 7914082816563471254L;

	/**
	 * 任务ID
	 */
	private String taskId;
	
	/**
	 * 任务名
	 */	
	private String taskName;

	/**
	 * 任务实现类
	 */
	private String className;

	/**
	 * 超时时间（任务开始到结束的的最长时间，超过这个值，将有超时控制）
	 */
	private long timeoutSecond;
	
	/**
	 * 子系统编码
	 */
	private String subSysCode;
	
	/**
	 * 任务目标
	 */
	private String destination;
	
	/**
	 * 是否记录任务执行结果
	 */
	private boolean recordResult;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getTimeoutSecond() {
		return timeoutSecond;
	}

	public void setTimeoutSecond(long timeoutSecond) {
		this.timeoutSecond = timeoutSecond;
	}
	
	public boolean isRecordResult() {
		return recordResult;
	}

	public void setRecordResult(boolean recordResult) {
		this.recordResult = recordResult;
	}

	public String getSubSysCode() {
		return subSysCode;
	}

	public void setSubSysCode(String subSysCode) {
		this.subSysCode = subSysCode;
	}
	
	public String getDestination() {
		return destination;
	}

	public void setDestination(String strDestination) {
		this.destination = strDestination;
	}
}
