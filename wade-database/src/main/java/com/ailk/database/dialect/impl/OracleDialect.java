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
 * @className: OracleDialect
 * @description: ORACLE数据库方言
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-1
 */
public class OracleDialect extends AbstractDialect {

	private static transient Logger log = Logger.getLogger(OracleDialect.class);
	
	@Override
	public String getDialectName() {
		return "ORACLE";
	}

	@Override
	public int getDialectCode() {
		return DBDialectFactory.ORACLE;
	}
	
	/**
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultSet executeQuery(DBConnection conn, String sql) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug(">>>使用连接 [" + conn.getName() + "]");
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
			log.debug(">>>使用连接 [" + conn.getName() + "]");
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
			log.debug(">>>使用连接[" + conn.getName() + "]");
		}
		
		IStatement statement = new BindingStatement(conn.getConnection(), sql, param);
		return statement.executeQuery();
	}

	@Override
	public String getDateFormat(String value) throws Exception {
		switch (value.length()) {
			case 4:
				return "yyyy";
			case 6:
				return "yyyymm";
			case 7:
				return "yyyy-mm";
			case 8:
				return "yyyymmdd";
			case 10:
				return "yyyy-mm-dd";
			case 13:
				return "yyyy-mm-dd hh24";
			case 16:
				return "yyyy-mm-dd hh24:mi";
			case 19:
				return "yyyy-mm-dd hh24:mi:ss";
		}
		return "yyyy-mm-dd hh24:mi:ss";
	}

	@Override
	public String operDate(String value, int interval, String dateType) throws Exception {
		
		String type = dateType.toUpperCase();
		String operate = interval > 0 ? "+" : "-";
		
		if ("YEAR".equals(type)) {
			return "add_months(" + value + ", " + interval * 12 + ")";
		} else if ("MONTH".equals(type)) {
			return "add_months(" + value + ", " + interval + ")";
		} else if ("DAY".equals(type)) {
			return value + operate + Math.abs(interval);
		} else if ("HOUR".equals(type)) {
			return value + operate + Math.abs(interval / 24);
		} else if ("MINUTE".equals(type)) {
			return value + operate + Math.abs(interval / (24 * 60));
		} else if ("SECOND".equals(type)) {
			return value + operate + Math.abs(interval / (24 * 60 * 60));
		}
		
		throw new IllegalArgumentException("数据类型参数错误！dateType" + dateType + "，有效值为: [YEAR|MONTH|DAY|HOUR|MINUTE|SECOND]");
	}

	@Override
	public String getRownumFunc() throws Exception {
		return "rownum";
	}
	
	@Override
	public String getRownumFunc(int rownum) throws Exception {
		return "rownum = " + rownum;
	}

	@Override
	public String getSysdateFunc() throws Exception {
		return "sysdate";
	}
	
	@Override
	public String getSequenceFunc(String sequence) throws Exception {
		return sequence + ".nextval";
	}
	
	@Override
	public String getCurrentTimeSql() throws Exception {
		return "select " + getSysdateFunc() + " from dual";
	}
	
	@Override
	public String getSequenceSql(String sequence) throws Exception {
		return "select " + getSequenceFunc(sequence) + " from dual";
	}
	
	@Override
	public String getSequenceSql(String eparchy_code, String sequence) throws Exception {
		return "select F_SYS_GETSEQID('" + eparchy_code + "', '" + sequence + "') from dual";
	}
	
	@Override
	public String getPagingSql(String sql, Parameter param, long start, long end) throws Exception {
		StringBuilder str = new StringBuilder();
		str.append("select * from (select row_.*, rownum rownum_ from (");
		str.append(sql);
		str.append(") row_ where rownum <= ?) where rownum_ >= ?");
		param.add(String.valueOf(end));
		param.add(String.valueOf(start));
		return str.toString();
	}
	
	@Override
	public String getPagingSql(String sql, IData param, long start, long end) throws Exception {
		StringBuilder str = new StringBuilder();
		str.append("select * from (select row_.*, rownum rownum_ from (");
		str.append(sql);
		str.append(") row_ where rownum <= :MAX_NUM) where rownum_ >= :MIN_NUM");
		param.put("MIN_NUM", String.valueOf(start));
		param.put("MAX_NUM", String.valueOf(end));
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
