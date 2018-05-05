package com.wade.trace.probe;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IProbe
 * @description: 探针接口
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public interface IProbe {
	
	public static final String BROWSER = "browser";
	public static final String WEB = "web";
	public static final String ESB = "esb";
	public static final String APP = "app";
	public static final String SERVICE = "service";
	public static final String DAO = "dao";
	public static final String DATASOURCE = "datasource";
	
	public static final String ECS = "ecs";
	public static final String IBS = "ibs";
	public static final String UIP = "uip";
	public static final String PF  = "pf";
	
	/**
	 * 启动探针
	 */
	public void start();

	/**
	 * 停止探针
	 * 
	 * @param success
	 */
	public void stop(boolean success);
	
	/**
	 * 获取探针类型
	 * 
	 * @return
	 */
	public String getProbeType();
	
	/**
	 * 设置探针类型
	 * 
	 * @param type
	 */
	public void setProbeType(String probeType);
	
	/**
	 * 获取ID
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * 设置ID
	 * 
	 * @param id
	 */
	public void setId(String id);
	
	/**
	 * 获取父ID
	 * 
	 * @return
	 */
	public String getParentId();
	
	/**
	 * 设置父ID
	 * 
	 * @param parentId
	 */
	public void setParentId(String parentId);
	
	/**
	 * 获取追踪ID
	 * 
	 * @return
	 */
	public String getTraceId();

	/**
	 * 设置追踪ID
	 * 
	 * @param traceId
	 */
	public void setTraceId(String traceId);

	/**
	 * 获取业务ID
	 * 
	 * @return
	 */
	public String getBizId();

	/**
	 * 设置业务ID
	 * 
	 * @param bizId
	 */
	public void setBizId(String bizId);

	
	/**
	 * 获取操作ID
	 * 
	 * @return
	 */
	public String getOperId();
	
	/**
	 * 设置操作ID
	 * 
	 * @param loginId
	 */
	public void setOperId(String operId);
	
	/**
	 * 获取开始时间
	 * 
	 * @return
	 */
	public long getStartTime();

	/**
	 * 获取结束时间
	 * 
	 * @return
	 */
	public long getEndTime();
	
	/**
	 * 获取处理耗时(毫秒)
	 * 
	 * @return
	 */
	public long getCostTime(); 
	
	/**
	 * 探针所在的业务逻辑是否处理成功
	 * 
	 * @return
	 */
	public boolean isSuccess();
	
	/**
	 * 设置探针所在的业务逻辑处理成功与否
	 * 
	 * @param success
	 */
	public void setSuccess(boolean success);
	
	/**
	 * 记录日志
	 */
	public void logging();
}
