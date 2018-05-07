package com.ailk.database.dialect.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.dialect.AbstractDialect;
import com.ailk.database.dialect.DBDialectFactory;
import com.ailk.database.statement.IStatement;
import com.ailk.database.statement.Parameter;
import com.ailk.database.statement.impl.BindingStatement;
import com.ailk.database.statement.impl.ParameterStatement;
import com.ailk.database.statement.impl.SimpleStatement;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: MySQLDialect
 * @description: MYSQL数据库方言
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-1
 */
public class MySQLDialect extends AbstractDialect {

	private static transient Logger log = Logger.getLogger(MySQLDialect.class);
	
	@Override
	public String getDialectName() {
		return "MYSQL";
	}
	
	@Override
	public int getDialectCode() {
		return DBDialectFactory.MYSQL;
	}
	
	/**
	 * 
	 * 
	 * @param conn
	 * @param sql
	 * @return ResultSet
	 * @throws Exception
	 */
	@Override
	public ResultSet executeQuery(DBConnection conn, String sql) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("database session[" + Thread.currentThread().getId() + "] use connection [" + conn.getName() + "]");
		}
		
		IStatement statement = new SimpleStatement(conn.getConnection(), sql);
		return statement.executeQuery();
	}
	
	/**
	 * 
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return ResultSet
	 * @throws Exception
	 */
	@Override
	public ResultSet executeQuery(DBConnection conn, String sql, Parameter param) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("database session[" + Thread.currentThread().getId() + "] use connection [" + conn.getName() + "]");
		}
			
		IStatement statement = new ParameterStatement(conn.getConnection(), sql, param);
		return statement.executeQuery();
	}
	
	/**
	 * 
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return ResultSet
	 * @throws Exception
	 */
	@Override
	public ResultSet executeQuery(DBConnection conn, String sql, IData param) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("database session[" + Thread.currentThread().getId() + "] use connection [" + conn.getName() + "]");
		}
		
		IStatement statement = new BindingStatement(conn.getConnection(), sql, param);
		return statement.executeQuery();
	}

	@Override
	public String getDateFormat(String value) throws Exception {
		switch (value.length()) {
			case 4:
				return "%Y";
			case 6:
				return "%Y%m";
			case 7:
				return "%Y-%m";
			case 8:
				return "%Y%m%d";
			case 10:
				return "%Y-%m-%d";
			case 13:
				return "%Y-%m-%d %H";
			case 16:
				return "%Y-%m-%d %H:%i";
			case 19:
				return "%Y-%m-%d %H:%i:%s";
		}
		return "%Y-%m-%d %H:%i:%s";
	}

	@Override
	public String operDate(String value, int interval, String dateType) throws Exception {
		String operate = interval > 0 ? "+" : "-";
		return value + " " + operate + " INTERVAL " + Math.abs(interval) + " " + dateType.toUpperCase();
	}

	@Override
	public String getRownumFunc() throws Exception {
		return "rownum()";
	}
	
	@Override
	public String getRownumFunc(int rownum) throws Exception {
		return "limit " + rownum;
	}
	
	@Override
	public String getSysdateFunc() throws Exception {
		return "now()";
	}
	
	@Override
	public String getSequenceFunc(String sequence) throws Exception {
		return "nextval('" + sequence + "')";
	}
	
	@Override
	public String getCurrentTimeSql() throws Exception {
		return "select " + getSysdateFunc();
	}
	
	@Override
	public String getSequenceSql(String sequence) throws Exception {
		return "select " + getSequenceFunc(sequence);
	}
	
	@Override
	public String getSequenceSql(String eparchy_code, String sequence) throws Exception {
		return "select F_SYS_GETSEQID('" + eparchy_code + "', '" + sequence + "')";
	}
	
	@Override
	public String getPagingSql(String sql, Parameter param, long start, long end) throws Exception {
		StringBuilder str = new StringBuilder();
		str.append(sql);
		str.append(" limit " + String.valueOf(start) + ", " + String.valueOf(end));
		return str.toString();
	}
	
	@Override
	public String getPagingSql(String sql, IData param, long start, long end) throws Exception {
		StringBuilder str = new StringBuilder();
		str.append(sql);
		str.append(" limit " + String.valueOf(start) + ", " + String.valueOf(end));
		return str.toString();
	}

	@Override
	public String getSchema(Connection conn) throws SQLException {
		return "%";
	}
	
	@Override
	public ResultSet getPrimaryKeys(DBConnection conn, String table_name) throws Exception {
		return conn.getMetaData().getPrimaryKeys(null, getSchema(conn.getConnection()), table_name.toUpperCase());
	}
}
