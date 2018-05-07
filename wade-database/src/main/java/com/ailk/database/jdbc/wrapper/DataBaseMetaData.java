/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.wrapper;


/**
 * 表结构对象，缓存单表增删改查的原子SQL语句，子类需实现initTable()
 * 
 * @className: TableMetaData.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class DataBaseMetaData {
	
	/**
	 * 数据源账号
	 */
	private String user;
	private String dataSourceName;
	private DataSourceWrapper ds;
	
	public DataBaseMetaData(DataSourceWrapper ds) {
		this.ds = ds;
		this.user = ds.getUser();
		this.dataSourceName = ds.getName();
	}
	
	
	/**
	 * 初始化并缓存表主键数组、表结构、增删改查SQL语句
	 * @param table
	 */
	public StringBuilder getPaginationSQL(StringBuilder sql, int begin, int end) {
		throw new IllegalAccessError("getPaginationSQL 必须由子类实现，不能直接使用");
	}
	
	/**
	 * 获取数据源
	 */
	public DataSourceWrapper getDataSource() {
		return this.ds;
	}

	
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * @return the dataSourceName
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}
}
