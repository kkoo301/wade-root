package com.ailk.database.dbconn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import javax.naming.NamingException;

import com.ailk.database.jdbc.wrapper.DataSourceWrapper;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: IConnectionManager
 * @description: 数据库连接管理接口
 * 
 * @version: v1.0.0
 * @author: liaos
 * @date: 2013-7-20
 */
public interface IConnectionManager {
	
	/**
	 * 初始化数据源
	 */
	public void initConnectionManager();
	
	/**
	 * 获取数据库连接,dataSourceName 必须是配置在wd_datasource_connect表里并且wd_datasource_connect.service_name={-Dwade.server.name}
	 * @param dataSourceName
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public Connection getConnection(String dataSourceName) throws SQLException;
	
	/**
	 * 获取数据库连接dataSourceName对应的数据库真实账户名
	 * @param name
	 * @return
	 */
	public String getUserName(String dataSourceName);
	
	/**
	 * 释放所有数据库连接
	 * @throws SQLException
	 */
	public void destroyConnections() throws SQLException;
	
	/**
	 * 获取数据库连接dataSourceName的SQL超时时长
	 * @param dataSourceName
	 * @return
	 */
	public int getStatTimeout(String dataSourceName);
	
	/**
	 * 探测数据库连接
	 * @param dataSourceName
	 * @return
	 */
	public void testConnection();
	
	/**
	 * 获取DataSource
	 * @param dataSourceName
	 * @return
	 */
	public DataSourceWrapper getDataSource(String dataSourceName);
	
	/**
	 * 获取数据源列表
	 * @return
	 */
	public Set<String> listDataSource();
	
	/**
	 * 获取数据源实时监控信息
	 * @param dataSourceName
	 * @return
	 */
	public String traceInfo(String dataSourceName);
	
	/**
	 * 跟踪连接
	 * @param dataSourceName
	 * @param conn
	 */
	public void trace(String dataSourceName, UUID traceId);
	
	
	/**
	 * 清除连接跟踪
	 * @param dataSourceName
	 * @param conn
	 */
	public void cleanTrace(String dataSourceName, UUID traceId);
	
	public void cleanAllTrace();
}
