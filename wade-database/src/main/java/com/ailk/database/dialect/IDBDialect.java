package com.ailk.database.dialect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ailk.common.data.IData;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.statement.Parameter;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: IDBDialect
 * @description: 数据库方言
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-1
 */
public interface IDBDialect {
	
	/**
	 * 获取日期格式
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String getDateFormat(String value) throws Exception;
	
	public ResultSet executeQuery(DBConnection conn, String sql) throws Exception;
	public ResultSet executeQuery(DBConnection conn, String sql, Parameter param) throws Exception;
	public ResultSet executeQuery(DBConnection conn, String sql, IData param) throws Exception;
	
	/**
	 * 日期操作
	 * 
	 * @param value
	 * @param interval
	 * @param dateType
	 * @return
	 * @throws Exception
	 */
	public String operDate(String value, int interval, String dateType) throws Exception;
	
	/**
	 * 返回数据库方言名
	 * 
	 * @return
	 */
	public String getDialectName();
	
	/**
	 * 返回数据库方言编码, 编码清单定义在DBDialectFactory.java
	 * 
	 * @return
	 */
	public int getDialectCode();
	
	public String getRownumFunc() throws Exception;
	public String getRownumFunc(int rownum) throws Exception;
	public String getSysdateFunc() throws Exception;
	public String getSequenceFunc(String sequence) throws Exception;
	public String getCurrentTimeSql() throws Exception;
	public String getSequenceSql(String sequence) throws Exception;
	public String getSequenceSql(String eparchy_code, String sequence) throws Exception;
	public String getPagingSql(String sql, Parameter param, long start, long end) throws Exception;
	public String getPagingSql(String sql, IData param, long start, long end) throws Exception;
	public String getSchema(Connection conn) throws SQLException;
	
	public ResultSet getPrimaryKeys(DBConnection conn, String table) throws Exception;
}
