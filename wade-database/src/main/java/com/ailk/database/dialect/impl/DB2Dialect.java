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
 * @className: DB2Dialect
 * @description: DB2数据库方言
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-1
 */
public class DB2Dialect extends AbstractDialect {

	private static transient Logger log = Logger.getLogger(DB2Dialect.class);

	@Override
	public String getDialectName() {
		return "DB2";
	}

	@Override
	public int getDialectCode() {
		return DBDialectFactory.DB2;
	}
	
	/**
	 * execute query
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
		
		IStatement statement = new SimpleStatement(conn.getConnection(), sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		return statement.executeQuery();
	}
	
	/**
	 * execute query
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
		
		IStatement statement = new ParameterStatement(conn.getConnection(), sql, param, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		
		return statement.executeQuery();
	}
	
	/**
	 * execute query
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
		
		IStatement statement = new BindingStatement(conn.getConnection(), sql, param, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		return statement.executeQuery();
	}

	@Override
	public String getDateFormat(String value) throws Exception {
		return null;
	}

	@Override
	public String operDate(String value, int interval, String dateType) throws Exception {
		// 暂未实现
		return null;
	}

	@Override
	public String getRownumFunc() throws Exception {
		return null;
	}
	
	@Override
	public String getRownumFunc(int rownum) throws Exception {
		return null;
	}
	
	@Override
	public String getSysdateFunc() throws Exception {
		return null;
	}
	
	@Override
	public String getSequenceFunc(String sequence) throws Exception {
		return sequence;
	}
	
	@Override
	public String getCurrentTimeSql() throws Exception {
		return "values current timestamp";
	}
	
	@Override
	public String getSequenceSql(String sequence) throws Exception {
		return "values nextval for " + getSequenceFunc(sequence);
	}
	
	@Override
	public String getSequenceSql(String eparchy_code, String sequence) throws Exception {
		return null;
	}
	
	@Override
	public String getPagingSql(String sql, Parameter param, long start, long end) throws Exception {
		StringBuilder str = new StringBuilder();
		str.append("select * from (select ROW_NUMBER() OVER() AS rownum_, row_.* from (");
		str.append(sql);
		str.append(") row_ ) where rownum_ > :MIN_NUM and rownum_ <= :MAX_NUM");
		param.add(String.valueOf(start));
		param.add(String.valueOf(end));
		return str.toString();
	}
	
	@Override
	public String getPagingSql(String sql, IData param, long start, long end) throws Exception {
		StringBuilder str = new StringBuilder();
		str.append("select * from (select ROW_NUMBER() OVER() AS rownum_, row_.* from (");
		str.append(sql);
		str.append(") row_ ) where rownum_ > :MIN_NUM and rownum_ <= :MAX_NUM");
		param.put("MIN_NUM", String.valueOf(start));
		param.put("MAX_NUM", String.valueOf(end));
		return str.toString();
	}

	@Override
	public String getSchema(Connection conn) throws SQLException {
		return conn.getMetaData().getUserName().toUpperCase();
	}
	
	
	@Override
	public ResultSet getPrimaryKeys(DBConnection conn, String table_name) throws Exception {
		return conn.getMetaData().getPrimaryKeys(null, getSchema(conn.getConnection()), table_name.toUpperCase());
	}
}
