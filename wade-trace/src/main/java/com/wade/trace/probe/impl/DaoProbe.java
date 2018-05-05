package com.wade.trace.probe.impl;

import java.util.HashMap;
import java.util.Map;

import com.ailk.mq.util.KafkaUtil;
import com.wade.trace.logsystem.LogKeys;
import com.wade.trace.logsystem.LogSystemUtil;
import com.wade.trace.probe.AbstractProbe;
import com.wade.trace.util.IOUtil;
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
public class DaoProbe extends AbstractProbe {

	/**
	 * 数据源名
	 */
	private String dataSource;
	
	/**
	 * 取连接耗时
	 */
	private long dccost;

	/**
	 * SQL标识
	 */
	private String sqlName;
	
	/**
	 * SQL语句
	 */
	private String sql;
	
	/**
	 * 参数信息
	 */
	private Map<String, Object> sqlParams;
	
	@Override
	public void start() {
		super.start();
		super.setProbeType(DAO);
	}
	
	/**
	 * 获取数据源名
	 * 
	 * @return
	 */
	public String getDataSource() {
		return dataSource;
	}

	/**
	 * 设置数据源名
	 * 
	 * @param dataSource
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 获取取数据库连接耗时
	 * 
	 * @return
	 */
	public long getDccost() {
		return dccost;
	}

	/**
	 * 保存取数据库连接耗时
	 * 
	 * @param dccost
	 */
	public void setDccost(long dccost) {
		this.dccost = dccost;
	}
	
	/**
	 * 获取语句标识
	 * 
	 * @return
	 */
	public String getSqlName() {
		return sqlName;
	}

	/**
	 * 设置语句标识
	 * 
	 * @param sqlName
	 */
	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}

	/**
	 * 获取SQL语句
	 * 
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * 设置SQL语句
	 * 
	 * @param sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * 获取参数信息
	 * 
	 * @return
	 */
	public Map<String, Object> getSqlParams() {
		return sqlParams;
	}

	/**
	 * 设置参数信息
	 * 
	 * @param params
	 */
	public void setParams(Map<String, Object> sqlParams) {
		this.sqlParams = sqlParams;
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
	    logInfo.put(LogKeys.DATASOURCE_NAME, getDataSource());
	    logInfo.put(LogKeys.DCCOST, getDccost());
	    logInfo.put(LogKeys.SQL_NAME, getSqlName());	    		
	    logInfo.put(LogKeys.SQL, getSql());

		LogSystemUtil.send(logInfo);

	}

}
