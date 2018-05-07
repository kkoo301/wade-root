/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.wrapper;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.object.TableMetaObject;

/**
 * 表结构对象，缓存单表增删改查的原子SQL语句，子类需实现initTable()
 * 
 * @className: TableMetaData.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class TableMetaData {
	
	private static final Logger log = LoggerFactory.getLogger(TableMetaData.class);
	
	public static final String DATASOURCE_NSTRING = "datasource.nstring.";
	
	
	/**
	 * @note: 一定要用ConcurrentHashMap，原先用HashMap，IBM-JDK环境下会导致CPU 100%
	 */
	private Map<String, TableMetaObject> tables = new ConcurrentHashMap<String, TableMetaObject>(10000);
	
	/**
	 * 数据源账号
	 */
	private String user;
	private String owner;
	private String dataSourceName;
	private DataSourceWrapper ds;
	
	public TableMetaData(DataSourceWrapper ds) {
		this.ds = ds;
		this.user = ds.getUser();
		this.owner = ds.getOwner();
		this.dataSourceName = ds.getName();
		
		// 初始化表结构
		initTables();
	}
	
	/**
	 * 初始化表结构
	 */
	private void initTables() {
		String[] tableNames = getInitLoadTableNames();
		if (null != tableNames) {
			for (String tableName : tableNames) {
				
				if (null == tableName || tableName.trim().length() == 0) {
					continue;
				}
				
				tableName = tableName.trim().toUpperCase();
				
				try {
					TableMetaObject meta = tables.get(tableName);
					if (null == meta) {
						meta = new TableMetaObject(tableName);
						
						initTable(meta);
						
						tables.put(tableName, meta);
						
						log.info("数据源[" + dataSourceName + "]初始化表[" + tableName + "]");
					}
				} catch (Exception e) {
					log.info("数据源[" + dataSourceName + "]初始化表[" + tableName + "]结构失败，不影响系统正常运行." + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 获取表结构对象，控制并发创建
	 * @param tableName
	 * @return
	 */
	public TableMetaObject getTableMetaObject(String tableName) throws SQLException {
		synchronized (this) {
			TableMetaObject meta = tables.get(tableName);
			if (null == meta) {
				meta = new TableMetaObject(tableName);
				
				initTable(meta);
				
				tables.put(tableName, meta);
			}
			return meta;
		}
	}
	
	/**
	 * 获取初始化加载的表名列表
	 * @return
	 */
	protected String[] getInitLoadTableNames() {
		return null;
	}
	
	/**
	 * 获取默认的表结构对象
	 * @return
	 * @throws SQLException
	 */
	public TableMetaObject getDefaultTableMetaObject() throws SQLException {
		return getTableMetaObject(getDefaultTableName());
	}
	
	/**
	 * 默认的表对象
	 * @return
	 */
	public String getDefaultTableName() {
		throw new IllegalAccessError("query 必须由子类实现，不能直接使用");
	}
	
	
	/**
	 * 初始化并缓存表主键数组、表结构、增删改查SQL语句
	 * @param table
	 */
	public void initTable(TableMetaObject table) throws SQLException {
		throw new IllegalAccessError("initTable 必须由子类实现，不能直接使用");
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
	
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}
}
