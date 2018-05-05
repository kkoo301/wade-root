package com.wade.trace.probe;

import com.wade.trace.util.SystemUtil;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AbstractProbe
 * @description: 探针抽象类
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public abstract class AbstractProbe implements IProbe {
	
	/**
	 * 探针类型
	 */
	private String probeType;
	
	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 父ID
	 */
	private String parentId;
	
	/**
	 * 追踪ID
	 */
	private String traceId;
	
	/**
	 * 业务ID
	 */
	private String bizId;
	
	/**
	 * 操作ID
	 */
	private String operId;
	
	/**
	 * 开始时间
	 */
	private long startTime;
	
	/**
	 * 结束时间
	 */
	private long endTime;
	
	/**
	 * 处理耗时(毫秒)
	 */
	private long costTime;
	
	/**
	 * 调用成功与否
	 */
	private boolean success;
	
	@Override
	public void start() {
		this.id = SystemUtil.uuid();
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void stop(boolean success) {
		this.endTime = System.currentTimeMillis();
		this.costTime = (this.endTime - this.startTime);
		this.success = success;
	}

	@Override
	public String getProbeType() {
		return probeType;
	}

	@Override
	public void setProbeType(String probeType) {
		this.probeType = probeType;
	}
	
	@Override
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	@Override
	public String getTraceId() {
		return this.traceId;
	}

	@Override
	public String getBizId() {
		return bizId;
	}

	@Override
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	
	@Override
	public void setOperId(String operId) {
		this.operId = operId;
	}
	
	@Override
	public String getOperId() {
		return this.operId;
	}
	
	@Override
	public long getStartTime() {
		return this.startTime;
	}

	@Override
	public long getEndTime() {
		return this.endTime;
	}
	
	@Override
	public long getCostTime() {
		return this.costTime;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
