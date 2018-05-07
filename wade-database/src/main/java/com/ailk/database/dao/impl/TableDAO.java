/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.logger.ILogger;
import com.ailk.common.trace.ITracer;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.jdbc.oracle.OracleConnection;
import com.ailk.database.jdbc.wrapper.QuerySQLParser;
import com.ailk.database.jdbc.wrapper.TableMetaSQL;
import com.ailk.database.jdbc.wrapper.TableMetaStatement;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.TableMetaObject;

import oracle.jdbc.OracleResultSetMetaData;

/**
 * 单表增删改查的数据访问对象
 * 
 * @className: TableDAO.java
 * @author: liaosheng
 * @date: 2014-5-9
 */

public class TableDAO {
	
	private static final Logger log = Logger.getLogger(TableDAO.class);
	private static final int FETCH_SIZE = 2000;
	
	private boolean autoCloseReadOnlyConnection = false;
	
	private ILogger logger = null;
	private Object logObject = null;
	private ITracer tracer = null;
	
	/**
	 * 
	 */
	public TableDAO() {
		autoCloseReadOnlyConnection = SystemCfg.isReleaseDBConn;
	}
	
	/**
	 * @return the logger
	 */
	public ILogger getLogger() {
		return logger;
	}
	
	/**
	 * @param logger the logger to set
	 */
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}
	
	/**
	 * @return the logObject
	 */
	public Object getLogObject() {
		return logObject;
	}
	
	/**
	 * @param logObject the logObject to set
	 */
	public void setLogObject(Object logObject) {
		this.logObject = logObject;
	}
	
	public ITracer getTracer() {
		return tracer;
	}
	
	public void setTracer(ITracer tracer) {
		this.tracer = tracer;
	}
	
	/**
	 * 获取数据库时间
	 * @param conn
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public Timestamp getCurrentTime(Connection conn, TableMetaObject table) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		
		Timestamp time = null;
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.getCurrentTimeSQL(table);
			stmt = TableMetaStatement.getCurrentTimeStatement(conn, table, sql);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "getCurrentTime", sql + "@" + stmt.getQueryTimeout(), null);
			}
			
			rs = stmt.executeQuery();
			
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 getCurrentTime 耗时 %d ms", cost));
			}
			
			while (rs.next()) {
				String str = rs.getString(1);
				time = Timestamp.valueOf(str);
			}
			
			rs.close();
			stmt.close();
			
			success = true;
			return time;
		} catch (SQLException e) {
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			log.error(err.toString());
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 getCurrentTime %s,共耗时 %d ms", success ? "成功": "失败", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "current", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
			
		}
	}
	
	/**
	 * 获取序列
	 * @param conn
	 * @param table
	 * @param seqName
	 * @param increment
	 * @return
	 * @throws SQLException
	 */
	public String getSequence(Connection conn, TableMetaObject table, String seqName, int increment) throws SQLException {
		if (null == seqName)
			throw new SQLException(String.format("DAO[%s]获取序列异常，序列名不能为null", getClass().getName()));
		
		if (increment <= 0)
			increment = 1;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String seqId = null;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		boolean autoCommit = conn.getAutoCommit();
		
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.getSequenceSQL(table, seqName, increment);
			conn.setAutoCommit(true);
			stmt = TableMetaStatement.getSequence(conn, table, seqName, increment, sql);
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "getSequence", sql + "@" + stmt.getQueryTimeout() + ":" + seqName + ", " + increment, null);
			}
			rs = stmt.executeQuery();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行 getSequence 耗时 %d ms", cost));
			}
			
			while (rs.next()) {
				seqId = rs.getString(1);
			}
			
			rs.close();
			stmt.close();
			
			success = true;
			return seqId;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			log.error(err.toString());
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 getSequence %s,共耗时 %d ms", success ? "成功": "失败", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "sequence", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
			
		}
	}
	
	
	/**
	 * 统计单表查询
	 * @param conn
	 * @param table
	 * @param keys
	 * @param source
	 * @return
	 * @throws SQLException
	 */
	public long countTable(Connection conn, TableMetaObject table, String[] keys, IData source) throws SQLException {
		if (null == keys)
			keys = new String[]{};
		
		if (null == source)
			source = new DataMap();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		long count = 0;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.createCountSQL(table, keys);
			stmt = TableMetaStatement.createCountStatement(conn, table, keys, source, sql);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "count", sql + "@" + stmt.getQueryTimeout(), null);
			}
			
			rs = stmt.executeQuery();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行COUNT耗时 %d ms", cost));
			}
			
			while (rs.next()) {
				count = rs.getLong(1);
			}
			
			rs.close();
			stmt.close();
			success = true;
			return count;
		} catch (SQLException e) {
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(source.toString());
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 COUNT %s,共耗时 %d ms", success ? "成功," + count: "失败", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "count", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
	}
	
	/**
	 * 统计单表查询
	 * @param conn 数据源名称
	 * @param table
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return
	 */
	public long countTable(Connection conn, TableMetaObject table, String[] keys, String values[]) throws SQLException {
		if (null == keys || null == values)
			throw new SQLException(String.format("DAO[%s]单表查询异常，条件字段和字段值不能为null", getClass().getName()));
		
		int cnt = keys.length;
		if (0 == cnt || values.length != cnt)
			throw new SQLException(String.format("DAO[%s]单表查询异常，条件字段[%s]和字段值[%s]不能为空", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		long count = 0;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.createCountSQL(table, keys);
			stmt = TableMetaStatement.createCountStatement(conn, table, keys, values, sql);
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "count", sql + "@" + stmt.getQueryTimeout(), null);
			}
			rs = stmt.executeQuery();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行COUNT耗时 %d ms", cost));
			}
			
			while (rs.next()) {
				count = rs.getLong(1);
			}
			
			rs.close();
			stmt.close();
			
			success = true;
			
			return count;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 COUNT %s,共耗时 %d ms", success ? "成功," + count: "失败", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "count", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public int count(Connection conn, TableMetaObject table, String colonSql, IData source) throws SQLException {
		if (null == source)
			source = new DataMap();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 0;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		
		Object[] par = TableMetaStatement.parseColonSql(colonSql);
		String countSql = (String) par[0];
		List<String> params = (List<String>) par[1];
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = table.createCountSQL(countSql);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> %s", sql));
			}
			
			stmt = conn.prepareStatement(sql);
			
			for (int i = 0, cnt = params.size(); i < cnt; i++) {
				String colName = params.get(i);
				Object value = source.get(colName);
				value = null == value ? "" : value;
				
				stmt.setObject(i + 1, value);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d %s = %s", i + 1, colName, value));
				}
			}
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "count", sql + "@" + stmt.getQueryTimeout(), source);
			}
			
			rs = stmt.executeQuery();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行COUNT耗时 %d ms", cost));
			}
			
			while (rs.next()) {
				count = rs.getInt(1);
			}
			
			rs.close();
			stmt.close();
			success = true;
			return count;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(source.toString());
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 COUNT %s,共耗时 %d ms", success ? "成功," + count: "失败", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "count", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
		
	}
	
	/**
	 * COUNT
	 * @param conn
	 * @param table
	 * @param sql
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public int count(Connection conn, TableMetaObject table, String sql, String[] values) throws SQLException {
		if (null == values)
			values = new String[] {};
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 0;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String dataSourceName = null;
		
		try {
			sql = table.createCountSQL(sql);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> %s", sql));
			}
			
			stmt = conn.prepareStatement(sql);
			
			for (int i = 0, cnt = values.length; i < cnt; i++) {
				stmt.setString(i + 1, values[i]);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", i + 1, values[i]));
				}
			}
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "count", sql + "@" + stmt.getQueryTimeout(), null);
			}
			rs = stmt.executeQuery();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行COUNT耗时 %d ms", cost));
			}
			
			while (rs.next()) {
				count = rs.getInt(1);
			}
			
			rs.close();
			stmt.close();
			success = true;
			return count;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 COUNT %s,共耗时 %d ms", success ? "成功," + count: "失败", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "count", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
		
	} 
	
	
	/**
	 * 执行DML语句
	 * @param conn
	 * @param parser
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(Connection conn, QuerySQLParser parser, String[] values) throws SQLException {
		return executeUpdate(conn, parser.getSQL(), values);
	}
	
	/**
	 * 执行DML语句
	 * @param dataSourceName 数据源名称
	 * @param sql SQL语句
	 * @param values 字段值
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(Connection conn, String sql, String[] values) throws SQLException {
		if (null == values)
			values = new String[] {};
		
		PreparedStatement stmt = null;
		int data;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String dataSourceName = null;
		
		try {
			stmt = TableMetaStatement.createExecuteUpdateStatement(conn, sql, values);
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "update", sql + "@" + stmt.getQueryTimeout(), null);
			}
			data = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行更新耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			
			return data;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 executeUpdate %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "executeupdate", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	
	/**
	 * 执行DML语句
	 * @param conn
	 * @param colonSql
	 * @param source
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(Connection conn, QuerySQLParser colonSql, IData source) throws SQLException {
		return executeUpdate(conn, colonSql.getSQL(), source);
	}
	
	/**
	 * 执行DML语句
	 * @param conn
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(Connection conn, String colonSql, IData source) throws SQLException {
		if (null == source)
			source = new DataMap();
		
		PreparedStatement stmt = null;
		int data;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			Object[] objs = TableMetaStatement.parseColonSql(colonSql);
			sql = (String) objs[0];
			stmt = TableMetaStatement.createExecuteUpdateStatement(conn, colonSql, source, objs);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "update", sql + "@" + stmt.getQueryTimeout(), source);
			}
			
			data = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行更新耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return data;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(source.toString());
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 executeUpdate %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "executeupdate", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(Connection conn, TableMetaObject table, QuerySQLParser sql, String[] values) throws SQLException {
		return executeQuery(conn, table, sql, values, 0, 0, FETCH_SIZE);
	}
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @param limit 数据大小限制
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(Connection conn, TableMetaObject table, QuerySQLParser sql, String[] values, int begin, int end, int fetchsize) throws SQLException {
		return executeQuery(conn, table, sql.getSQL(), values, begin, end, fetchsize);
	}
	
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(Connection conn, TableMetaObject table, String sql, String[] values) throws SQLException {
		return executeQuery(conn, table, sql, values, 0, 0, FETCH_SIZE);
	}
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(Connection conn, TableMetaObject table, String sql, String[] values, int begin, int end, int fetchsize) throws SQLException {
		if (null == values)
			values = new String[] {};
		
		IDataset list = null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sqlstr = null;
		String dataSourceName = null;
		
		if (fetchsize > FETCH_SIZE)
			fetchsize = FETCH_SIZE;
		if (fetchsize <= 0)
			fetchsize = end - begin;
		
		try {
			
			sqlstr = table.createPageSQL(sql, begin, end);
			stmt = TableMetaStatement.createExecuteQueryStatement(conn, table, sqlstr, values, begin, end);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "query", sqlstr + "@" + stmt.getQueryTimeout() + ":" + begin + ", " + end, null);
			}
			
			rs = stmt.executeQuery();
			rs.setFetchSize(fetchsize);
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行查询fetchsize[%d],耗时 %d ms", fetchsize, cost));
			}
			
			list = new DatasetList();
			ResultSetMetaData rsmd = rs.getMetaData();
			int colcnt = rsmd.getColumnCount();
			String[] names = null;
			int[] ids = null;
			while (rs.next()) {
				IData data = new DataMap(colcnt);
				
				if (null == names) {
					int size = rsmd.getColumnCount();
					names = new String[size];
					ids = new int[size];
					for (int i = 1; i <= size; i++) {
						String name = rsmd.getColumnLabel(i).toUpperCase(); // 数据库字段名统一约定为大写
						names[i - 1] = name;
						ids[i - 1] = i;
						data.put(name, getNString(rsmd, i, rs, name));
					}
				} else {
					for (int i = 0, size = names.length; i < size; i++) {
						data.put(names[i], getNString(rsmd, ids[i], rs, names[i]));
					}
				}
				
				list.add(data);
			}
			
			rs.close();
			stmt.close();
			success = true;
			
			return list;
		} catch (SQLException e) {
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(Arrays.toString(values));
			err.append("\nBEGIN:").append(begin);
			err.append("\nEND:").append(end);
			err.append("\nFETCHSIZE:").append(fetchsize);
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 executeQuery %d,共耗时 %d ms", null == list ? 0: list.size(), cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "executequery", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
	}
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(Connection conn, TableMetaObject table, QuerySQLParser colonSql, IData source, int fetchsize) throws SQLException {
		return executeQuery(conn, table, colonSql, source, 0, 0, fetchsize);
	}
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(Connection conn, TableMetaObject table, QuerySQLParser colonSql, IData source, int begin, int end, int fetchsize) throws SQLException {
		return executeQuery(conn, table, colonSql.getSQL(), source, begin, end, FETCH_SIZE);
	}
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(Connection conn, TableMetaObject table, String colonSql, IData source, int fetchsize) throws SQLException {
		return executeQuery(conn, table, colonSql, source, 0, 0, fetchsize);
	}
	
	/**
	 * 查询查询语句
	 * @param conn
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @param fetchsize 批量获取数据大小
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public IDataset executeQuery(Connection conn, TableMetaObject table, String colonSql, IData source, int begin, int end, int fetchsize) throws SQLException {
		if (null == source)
			source = new DataMap();
		
		IDataset list = null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		if (fetchsize > FETCH_SIZE)
			fetchsize = FETCH_SIZE;
		
		try {
			String sb = table.createPageSQL(colonSql, begin, end);
			
			Object[] objs = TableMetaStatement.parseColonSql(sb);
			sql = (String) objs[0];
			
			stmt = TableMetaStatement.createExecuteQueryStatement(conn, table, sql, source, (List<String>)objs[1], begin, end);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "query", sql + "@" + stmt.getQueryTimeout() + ":" + begin + ", " + end, null);
			}
			
			rs = stmt.executeQuery();
			rs.setFetchSize(fetchsize);

			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行查询fetchsize[%d],耗时 %d ms", fetchsize, cost));
			}
			
			list = new DatasetList();
			ResultSetMetaData rsmd = rs.getMetaData();
			int colcnt = rsmd.getColumnCount();
			String[] names = null;
			int[] ids = null;
			while (rs.next()) {
				IData data = new DataMap(colcnt);
				
				if (null == names) {
					int size = rsmd.getColumnCount();
					names = new String[size];
					ids = new int[size];
					for (int i = 1; i <= size; i++) {
						String name = rsmd.getColumnLabel(i).toUpperCase(); // 数据库字段名统一约定为大写
						
						names[i - 1] = name;
						ids[i - 1] = i;
						data.put(name, getNString(rsmd, i, rs, name));
					}
				} else {
					for (int i = 0, size = names.length; i < size; i++) {
						data.put(names[i], getNString(rsmd, ids[i], rs, names[i]));
					}
				}
				
				list.add(data);
			}
			
			rs.close();
			stmt.close();
			success = true;
			
			return list;
		} catch (SQLException e) {
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(source.toString());
			err.append("\nBEGIN:").append(begin);
			err.append("\nEND:").append(end);
			err.append("\nFETCHSIZE:").append(fetchsize);
			log.error(err.toString());
			
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 executeQuery %s,共耗时 %d ms", null == list ? "失败": "成功," + list.size(), cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "executequery", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param dataSourceName　数据源名称
	 * @param keys　条件字段名
	 * @param values　条件字段值
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(Connection conn, TableMetaObject table, String[] keys, String values[]) throws SQLException {
		return queryTable(conn, table, keys, values, 0, 0);
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param conn　数据源名称
	 * @param table 表对象
	 * @param keys　条件字段名
	 * @param values　条件字段值
	 * @param begin
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(Connection conn, TableMetaObject table, String[] keys, String values[], int begin, int end) throws SQLException {
		IDataset list = null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			
			sql = TableMetaSQL.createQueryTableSQL(table, keys, begin, end);
			stmt = TableMetaStatement.createQueryTableStatement(conn, sql, table, keys, values, begin, end);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "query", sql + "@" + stmt.getQueryTimeout() + ":" + begin + ", " + end, null);
			}
			
			rs = stmt.executeQuery();
			rs.setFetchSize(end - begin);
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行查询fetchsize[%d], 耗时 %d ms", end - begin, cost));
			}
			
			Map<String, IColumnObject> columns = table.getColumns();
			
			list = new DatasetList();
			while (rs.next()) {
				IData data = new DataMap(columns.size());
				Iterator<String> iter = columns.keySet().iterator();
				
				while (iter.hasNext()) {
					IColumnObject column = columns.get(iter.next());
					if (column.isNString()) {
						data.put(column.getColumnName(), rs.getNString(column.getColumnName()));
					} else {
						data.put(column.getColumnName(), rs.getString(column.getColumnName()));
					}
				}
				
				iter = null;
				list.add(data);
			}
			
			rs.close();
			stmt.close();
			success = true;
			
			return list;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(Arrays.toString(values));
			err.append("\nBEGIN:").append(begin);
			err.append("\nEND:").append(end);
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 queryTable %s,共耗时 %d ms", null == list ? "失败": "成功," + list.size(), cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "querytable", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
			
		}
		
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param conn　数据源名称
	 * @param table
	 * @param keys　条件字段名
	 * @param source　条件字段名和值的键值对
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(Connection conn, TableMetaObject table, String[] keys, IData source) throws SQLException {
		return queryTable(conn, table, keys, source, 0, 0);
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param conn　数据源名称
	 * @param table
	 * @param keys　条件字段名
	 * @param source　条件字段名和值的键值对
	 * @param begin
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(Connection conn, TableMetaObject table, String[] keys, IData source, int begin, int end) throws SQLException {
		if (null == keys)
			keys = new String[]{};
		
		if (null == source)
			source = new DataMap();
		
		IDataset list = null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.createQueryTableSQL(table, keys, begin, end);
			stmt = TableMetaStatement.createQueryTableStatement(conn, sql, table, keys, source, begin, end);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "query", sql + "@" + stmt.getQueryTimeout() + ":" + begin + ", " + end, source);
			}
			
			rs = stmt.executeQuery();
			
			rs.setFetchSize(end - begin);
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行查询fetchsize[%d],耗时 %d ms", end - begin, cost));
			}
			
			Map<String, IColumnObject> columns = table.getColumns();
			
			list = new DatasetList();
			while (rs.next()) {
				IData data = new DataMap(columns.size());
				Iterator<String> iter = columns.keySet().iterator();
				
				while (iter.hasNext()) {
					IColumnObject column = columns.get(iter.next());
					if (column.isNString()) {
						data.put(column.getColumnName(), rs.getNString(column.getColumnName()));
					} else {
						data.put(column.getColumnName(), rs.getString(column.getColumnName()));
					}
				}
				iter = null;
				list.add(data);
			}
			
			rs.close();
			stmt.close();
			success = true;
			
			return list;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(source.toString());
			err.append("\nBEGIN:").append(begin);
			err.append("\nEND:").append(end);
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && autoCloseReadOnlyConnection) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 queryTable %s,共耗时 %d ms", null == list ? "失败": "成功," + list.size(), cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "querytable", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
		
	}
	
	/**
	 * 根据主键查询指定数据源的一条数据，如果该数据源连接在当前会话里没有事务则主动close
	 * 多主键控制逻辑存在有一个主键也能正常查询的BUG
	 * 不支持唯一索引查询
	 * @param conn 数据源名称
	 * @param table
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return 单行结果集
	 * @throws SQLException
	 */
	public IData queryByPK(Connection conn, TableMetaObject table, String[] keys, String[] values) throws SQLException {
		return queryByPK(conn, table, keys, values, false);
	}
	
	/**
	 * 根据主键查询指定数据源的一条数据，如果该数据源连接在当前会话里没有事务则主动close
	 * 多主键控制逻辑存在有一个主键也能正常查询的BUG
	 * 不支持唯一索引查询
	 * @param conn 数据源名称
	 * @param table
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @param keepalive 是否保持连接
	 * @return 单行结果集
	 * @throws SQLException
	 */
	private IData queryByPK(Connection conn, TableMetaObject table, String[] keys, String[] values, boolean keepalive) throws SQLException {
		if (null == keys || null == values)
			throw new SQLException(String.format("DAO[%s]单表查询异常，主键字段和字段值不能为null", getClass().getName()));
		
		int cnt = keys.length;
		if (0 == cnt || values.length != cnt)
			throw new SQLException(String.format("DAO[%s]单表查询异常，主键字段[%s]和字段值[%s]不能为空", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		IData data = null;
		boolean success = false;
		String sql = null;
		String dataSourceName = null;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		
		try {
			sql = TableMetaSQL.createQuerySQL(table, keys);
			stmt = TableMetaStatement.createQueryByPKStatement(conn, sql, table, keys, values);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "query", sql + "@" + stmt.getQueryTimeout(), null);
			}
			
			rs = stmt.executeQuery();
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行查询耗时 %d ms", cost));
			}
			
			Map<String, IColumnObject> columns = table.getColumns();
			
			Iterator<String> iter = columns.keySet().iterator();
			while (rs.next()) {
				if (null == data)
					data = new DataMap(columns.size());
				
				while (iter.hasNext()) {
					IColumnObject column = columns.get(iter.next());
					if (column.isNString()) {
						data.put(column.getColumnName(), rs.getNString(column.getColumnName()));
					} else {
						data.put(column.getColumnName(), rs.getString(column.getColumnName()));
					}
				}
				
				try {
					String rowid = rs.getString("ROWID");
					data.put("ROWID", rowid);
				} catch (SQLException e) {
					
				}
			}
			
			rs.close();
			stmt.close();
			success = true;
			
			return data;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && !keepalive) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 queryByPK 完成,共耗时 %d ms", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "querybypk", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
	}
	
	/**
	 * 根据主键查询指定数据源的一条数据，如果该数据源连接在当前会话里没有事务则主动close
	 * 多主键控制逻辑存在BUG,针对有一个主键也要能正常查询
	 * 不支持唯一索引查询
	 * @param dataSourceName 数据源名称
	 * @param keys 条件字段名，如果为null则用表的主键
	 * @param source 条件字段名与值的键值对
	 * @return 单行结果集
	 * @throws SQLException
	 */
	public IData queryByPK(Connection conn, TableMetaObject table, String[] keys, IData source) throws SQLException {
		return queryByPK(conn, table, keys, source, false);
	}
	
	/**
	 * 根据主键查询指定数据源的一条数据，如果该数据源连接在当前会话里没有事务则主动close
	 * 多主键控制逻辑存在BUG,针对有一个主键也要能正常查询
	 * 不支持唯一索引查询
	 * @param dataSourceName 数据源名称
	 * @param keys 条件字段名，如果为null则用表的主键
	 * @param source 条件字段名与值的键值对
	 * @param keepalive 是否保持查询连接
	 * @return 单行结果集
	 * @throws SQLException
	 */
	private IData queryByPK(Connection conn, TableMetaObject table, String[] keys, IData source, boolean keepalive) throws SQLException {
		if (null == source)
			throw new SQLException(String.format("DAO[%s]单表查询异常，查询条件数据不能为null或空", getClass().getName()));
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		IData data = null;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			if (null == keys) {
				keys = table.getKeys();
			}
			
			sql = TableMetaSQL.createQuerySQL(table, keys);
			
			stmt = TableMetaStatement.createQueryByPKStatement(conn, sql, table, keys, source);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "query", sql + "@" + stmt.getQueryTimeout(), source);
			}
			
			rs = stmt.executeQuery();
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行查询耗时 %d ms", cost));
			}
			
			Map<String, IColumnObject> columns = table.getColumns();
			
			Iterator<String> iter = columns.keySet().iterator();
			while (rs.next()) {
				if (null == data)
					data = new DataMap(columns.size());
				
				while (iter.hasNext()) {
					IColumnObject column = columns.get(iter.next());
					if (column.isNString()) {
						data.put(column.getColumnName(), rs.getNString(column.getColumnName()));
					} else {
						data.put(column.getColumnName(), rs.getString(column.getColumnName()));
					}
				}
				
				try {
					String rowid = rs.getString("ROWID");
					data.put("ROWID", rowid);
				} catch (SQLException e) {
					
				}
			}
			
			rs.close();
			stmt.close();
			success = true;
			
			return data;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(source.toString());
			log.error(err.toString());
			
			if (null != rs) {
				rs.close();
				rs = null;
			}
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			try {
				if (null != conn && !keepalive) {
					if (!((DBConnection)conn).isTransaction())
						conn.close();
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				cost = System.currentTimeMillis() - start;
				if (log.isDebugEnabled()) {
					log.debug(String.format("DAO> 执行 queryByPK 完成,共耗时 %d ms", cost));
				}
				
				if (null != logger && null != logObject)
					logger.log(logObject, "querybypk", start, cost, null);
				
				if (null != tracer) {
					tracer.stopDaoProbe(success);
				}
			}
		}
	}
	
	/**
	 * 根据表主键删除一条记录，该操作并不提交事务，但有回滚逻辑
	 * @param conn 数据源名称
	 * @param table
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return executeUpdate()返回的结果
	 * @throws SQLException
	 */
	public int deleteByPK(Connection conn, TableMetaObject table, String[] keys, String[] values) throws SQLException {
		if (null == keys) {
			keys = table.getKeys();
		}
		if (null == values)
			throw new SQLException(String.format("DAO[%s]单表删除异常，字段值不能为null", getClass().getName()));
		
		int cnt = keys.length;
		if (0 == cnt || cnt != values.length)
			throw new SQLException(String.format("DAO[%s]单表删除异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		PreparedStatement stmt = null;
		int data = 0;
		boolean success = false;
		String sql = null;
		String dataSourceName = null;
		
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		
		try {
			sql = TableMetaSQL.createDeleteSQL(table, keys);
			
			stmt = TableMetaStatement.createDeleteByPKStatement(conn, sql, table, keys, values);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "delete", sql + "@" + stmt.getQueryTimeout(), null);
			}
			
			data = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行删除耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return data;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 deleteByPK %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "deletebypK", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	/**
	 * 根据表主键删除一条记录，该操作并不提交事务，但有回滚逻辑
	 * @param conn 数据源名称
	 * @param table
	 * @param keys 条件字段名，如果为null，则用表的主键
	 * @param source 条件字段名和值的键值对
	 * @return executeUpdate()返回的结果
	 * @throws SQLException
	 */
	public int deleteByPK(Connection conn, TableMetaObject table, String[] keys, IData source) throws SQLException {
		if (null == source || source.isEmpty())
			throw new SQLException(String.format("DAO[%s]单表删除异常，字段数据不能为null或空", getClass().getName()));
		
		PreparedStatement stmt = null;
		int data = 0;
		boolean success = false;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			if (null == keys) {
				keys = table.getKeys();
			}
			
			sql = TableMetaSQL.createDeleteSQL(table, keys);
			
			stmt = TableMetaStatement.createDeleteByPKStatement(conn, sql, table, keys, source);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "delete", sql + "@" + stmt.getQueryTimeout(), null);
			}
			data = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行删除耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return data;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(source.toString());
			log.error(err.toString());
			
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 deleteByPK %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "deletebypK", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	/**
	 * 根据表主键修改单条记录，该操作并不提交事务，但有回滚逻辑
	 * @param conn 数据源名称
	 * @param table
	 * @param cols 需要修改的字段名
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int updateByPK(Connection conn, TableMetaObject table, String[] cols, String[] colValues, String[] keys, String[] values) throws SQLException {
		if (null == cols || null == colValues)
			throw new SQLException(String.format("DAO[%s]单表修改异常，修改字段和修改字段值不能为null", getClass().getName()));
		
		int colsLen = cols.length;
		if (0 == colsLen || colsLen != colValues.length)
			throw new SQLException(String.format("DAO[%s]单表修改异常，修改字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(cols),
					Arrays.toString(colValues)));
		
		if (null == keys || null == values)
			throw new SQLException(String.format("DAO[%s]单表修改异常，主键字段和字段值不能为null", getClass().getName()));
		
		int keyLen = keys.length;
		if (0 == keyLen || keyLen != values.length)
			throw new SQLException(String.format("DAO[%s]单表修改异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		PreparedStatement stmt = null;
		boolean success = false;
		int data = 0;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.createUpdateSQL(table, cols, keys);
			
			stmt = TableMetaStatement.createUpdateByPKStatement(conn, sql, table, cols, colValues, keys, values);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "delete", sql + "@" + stmt.getQueryTimeout(), null);
			}
			data = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行修改耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return data;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nCOLS:").append(Arrays.toString(cols));
			err.append("\nCOLVALUES:").append(Arrays.toString(colValues));
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 updateByPK %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "updatebypK", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	/**
	 * 根据表主键修改单条记录，该操作并不提交事务，但有回滚逻辑
	 * @param conn 数据源名称
	 * @param table
	 * @param cols 需要修改的字段名，如果为null则全表修改
	 * @param source 需要修改的字段及数据的键值对
	 * @param keys 条件字段名，如果为null则是表主键
	 * @param values 条件字段值，如果为null则根据keys在source里对应的值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int updateByPK(Connection conn, TableMetaObject table, String[] cols, IData source, String[] keys, String[] values) throws SQLException {
		int keyLen = 0;
		if (null != keys && null != values) {
			keyLen = keys.length;
			if (0 == keyLen || keyLen != values.length)
				throw new SQLException(String.format("DAO[%s]单表修改异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
						getClass().getName(),
						Arrays.toString(keys),
						Arrays.toString(values)));
		}
		
		PreparedStatement stmt = null;
		boolean success = false;
		int data = 0;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			if (null == cols) {
				cols = table.getColumnNames();
			}
			
			if (null == keys) {
				keys = table.getKeys();
			}
			
			keyLen = keys.length;
			if (null == values) {
				values = new String[keyLen];
				for (int i = 0; i < keyLen; i++) {
					values[i] = source.getString(keys[i], "");
				}
			}
			
			sql = TableMetaSQL.createUpdateSQL(table, cols, keys);
			
			stmt = TableMetaStatement.createUpdateByPKStatement(conn, sql, table, cols, source, keys, values);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "update", sql + "@" + stmt.getQueryTimeout(), null);
			}
			data = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行修改耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return data;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nCOLS:").append(Arrays.toString(cols));
			err.append("\nCOLVALUES:").append(source.toString());
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 updateByPK %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "updatebypK", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	/**
	 * 根据表主键查询单条数据，并做增量修改，该操作并不提交事务，但有回滚逻辑
	 * @param conn 数据源名称
	 * @param table
	 * @param cols 需要修改的字段名
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int saveByPK(Connection conn, TableMetaObject table, String[] cols, String[] colValues, String[] keys, String[] values) throws SQLException {
		if (null == cols || null == colValues)
			throw new SQLException(String.format("DAO[%s]单表保存异常，修改字段和修改字段值不能为null", getClass().getName()));
		
		int colsLen = cols.length;
		if (0 == colsLen || colsLen != colValues.length)
			throw new SQLException(String.format("DAO[%s]单表保存异常，修改字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(cols),
					Arrays.toString(colValues)));
		
		if (null == keys) {
			keys = table.getKeys();
		}
		
		if (null == values)
			throw new SQLException(String.format("DAO[%s]单表保存异常，主键字段和字段值不能为null", getClass().getName()));
		
		int keyLen = keys.length;
		if (0 == keyLen || keyLen != values.length)
			throw new SQLException(String.format("DAO[%s]单表保存异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		PreparedStatement stmt = null;
		boolean success = false;
		int count = 0;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			IData data = queryByPK(conn, table, keys, values, true);
			if (null == data || data.isEmpty()) {
				return 0;
			}
			
			sql = TableMetaSQL.createSaveSQL(table, keys);
			
			stmt = TableMetaStatement.createSaveByPKStatement(conn, sql, table, data, cols, colValues, keys, values);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "save", sql + "@" + stmt.getQueryTimeout(), null);
			}
			count = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行保存耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return count;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nCOLS:").append(Arrays.toString(cols));
			err.append("\nCOLVALUES:").append(Arrays.toString(colValues));
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nPARAMS:").append(Arrays.toString(values));
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} catch (NullPointerException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 saveByPK %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "savebypK", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	/**
	 * 根据表主键查询单条数据，并做增量修改，该操作并不提交事务，但有回滚逻辑
	 * @param dataSourceName 数据源名称
	 * @param source 修改数据的键值对
	 * @param keys 条件字段名，如果为null则用表的主键
	 * @param values 条件字段值，如果为null，则用keys在source里对应的值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int saveByPK(Connection conn, TableMetaObject table, IData source, String[] keys, String[] values) throws SQLException {
		if (null == source || source.isEmpty())
			throw new SQLException(String.format("DAO[%s]单表保存异常，数据不能为null或空", getClass().getName()));
		
		if (null == keys) {
			keys = table.getKeys();
		}
		
		int keysLen = 0;
		if (null != keys && null == values) {
			keysLen = keys.length;
			values = new String[keysLen];
			for (int i = 0; i< keysLen; i++) {
				values[i] = source.getString(keys[i]);
			}
		}
		
		if (null != keys && null != values) {
			keysLen = keys.length;
			if (0 == keysLen || keysLen != values.length)
				throw new SQLException(String.format("DAO[%s]单表保存异常，修改字段[%s]和字段值[%s]不能为空或个数不一致", 
						getClass().getName(),
						Arrays.toString(keys),
						Arrays.toString(values)));
		}
		
		PreparedStatement stmt = null;
		boolean success = false;
		int count = 0;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			IData data = queryByPK(conn, table, keys, values, true);
			if (null == data || data.isEmpty()) {
				success = true;
				return 0;
			}
			//更新数据
			data.putAll(source);
			
			if (null == keys) 
				keys = table.getKeys();
			sql = TableMetaSQL.createSaveSQL(table, keys);
			
			stmt = TableMetaStatement.createSaveByPKStatement(conn, sql, table, data, source, keys, values);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "save", sql + "@" + stmt.getQueryTimeout(), null);
			}
			count = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行保存耗时 %d ms", cost));
			}
			
			stmt.close();
			
			success = true;
			return count;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nVALUES:").append(Arrays.toString(values));
			err.append("\nPARAMS:").append(source.toString());
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} catch (NullPointerException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 saveByPK %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "savebypK", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	/**
	 * 根据表主键新增数据，该操作并不提交事务，但有回滚逻辑
	 * @param conn
	 * @param table
	 * @param keys
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public int insert(Connection conn, TableMetaObject table, String[] keys, String[] values) throws SQLException {
		if (null == keys || null == values)
			throw new SQLException(String.format("DAO[%s]单表新增异常，主键字段和字段值不能为null", getClass().getName()));
		
		int keyLen = keys.length;
		if (0 == keyLen || keyLen != values.length)
			throw new SQLException(String.format("DAO[%s]单表新增异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		PreparedStatement stmt = null;
		boolean success = false;
		int count = 0;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.createInsertSQL(table);
			
			stmt = TableMetaStatement.createInsertStatement(conn, sql, table, keys, values);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "insert", sql + "@" + stmt.getQueryTimeout(), null);
			}
			count = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行新增耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return count;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nKEYS:").append(Arrays.toString(keys));
			err.append("\nVALUES:").append(Arrays.toString(values));
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 insert %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "insert", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	
	
	/**
	 * 根据表主键新增数据，该操作并不提交事务，但有回滚逻辑
	 * @param conn
	 * @param table
	 * @param source 绑定字段名与字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public int insert(Connection conn, TableMetaObject table, IData source) throws SQLException {
		if (null == source)
			throw new SQLException(String.format("DAO[%s]单表新增异常，数据不能为null", getClass().getName()));
		
		PreparedStatement stmt = null;
		boolean success = false;
		int count = 0;
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		String sql = null;
		String dataSourceName = null;
		
		try {
			sql = TableMetaSQL.createInsertSQL(table);
			stmt = TableMetaStatement.createInsertStatement(conn, sql, table, source);
			
			if (null != tracer) {
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "insert", sql + "@" + stmt.getQueryTimeout(), source);
			}
			count = stmt.executeUpdate();
			
			cost = System.currentTimeMillis() - start - cost;
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> 执行新增耗时 %d ms", cost));
			}
			
			stmt.close();
			success = true;
			return count;
		} catch (SQLException e) {
			//conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(source.toString());
			log.error(err.toString());

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行 insert %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "insert", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	
	public int[] update(Connection conn, TableMetaObject table, IDataset source, String[] cols, String[] keys, int batchsize) throws SQLException {
		if (null == source || source.isEmpty())
			return new int[] {};
		
		PreparedStatement stmt = null;
		int size = source.size();	
		int[] result = new int[size];
		int executeIdx = 0;
		
		if (batchsize <= 0) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("批量值[%d]错误,采用默认值2000", batchsize));
			}
			batchsize = 2000;
		}
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		boolean success = false;
		
		try {
			IDataset subparams = null;
			for (int i = 0; i < size; i++) {
				if (subparams == null) {
					subparams = new DatasetList(batchsize);
				}
				
				executeIdx ++;
				subparams.add(source.get(i));
				
				if ( i > 0 &&  (i + 1) % batchsize == 0 ) {
					if (stmt == null) {
						if (null == keys) {
							keys = table.getKeys();
						}
						
						if (null == cols) {
							cols = table.getColumnNames();
						}
						
						String sql = TableMetaSQL.createUpdateSQL(table, cols, keys);
						
						stmt = TableMetaStatement.createUpdateBatchStatement(conn, sql, table, cols, keys);
						
						if (null != tracer) {
							String dataSourceName = null;
							long connCostTime = 0;
							if (conn instanceof DBConnection) {
								dataSourceName = ((DBConnection)conn).getName();
								connCostTime = ((DBConnection)conn).getConnCostTime();
							}
							tracer.startDaoProbe(dataSourceName, connCostTime, "update", sql + "@" + stmt.getQueryTimeout(), null);
						}
					}
					
					TableMetaStatement.setUpdateBatchParameters(stmt, table, subparams, cols, keys);
					
					int[] res = stmt.executeBatch();
					stmt.clearBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchsize + 1 + j] = res[j];
					}
					
					
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> 执行批量修改 begin %d end %d, batch size %d", (i - batchsize + 1), i, batchsize));
					}
					
					subparams = null;
				}
				
				if (subparams == null) {
					subparams = new DatasetList(batchsize);
				}
			}
			
			if (subparams != null) {
				if (stmt == null) {
					if (null == keys) {
						keys = table.getKeys();
					}
					if (null == cols) {
						cols = table.getColumnNames();
					}
					String sql = TableMetaSQL.createUpdateSQL(table, cols, keys);
					
					stmt = TableMetaStatement.createUpdateBatchStatement(conn, sql, table, cols, keys);
					
					if (null != tracer) {
						String dataSourceName = null;
						long connCostTime = 0;
						if (conn instanceof DBConnection) {
							dataSourceName = ((DBConnection)conn).getName();
							connCostTime = ((DBConnection)conn).getConnCostTime();
						}
						tracer.startDaoProbe(dataSourceName, connCostTime, "update", sql + "@" + stmt.getQueryTimeout(), null);
					}
				}
				
				TableMetaStatement.setUpdateBatchParameters(stmt, table, subparams, cols, keys);
				int[] res = stmt.executeBatch();
				stmt.clearBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> 执行批量修改 begin %d end %d, batch size %d", executeIdx, size, batchsize));
				}
			}
			
			stmt.close();
			success = true;
			return result;
		} catch (SQLException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行updates %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "updates", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	public int[] delete(Connection conn, TableMetaObject table, IDataset source, String[] keys, int batchsize) throws SQLException {
		if (null == source || source.isEmpty())
			return new int[] {};
		
		PreparedStatement stmt = null;
		int size = source.size();	
		int[] result = new int[size];
		int executeIdx = 0;
		
		if (batchsize <= 0) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("批量值[%d]错误,采用默认值2000", batchsize));
			}
			batchsize = 2000;
		}
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		boolean success = false;
		
		try {
			IDataset subparams = null;
			for (int i = 0; i < size; i++) {
				if (subparams == null) {
					subparams = new DatasetList(batchsize);
				}
				
				executeIdx ++;
				subparams.add(source.get(i));
				
				if ( i > 0 &&  (i + 1) % batchsize == 0 ) {
					if (stmt == null) {
						if (null == keys) {
							keys = table.getKeys();
						}
						
						String sql = TableMetaSQL.createDeleteSQL(table, keys);
						
						stmt = TableMetaStatement.createDeleteBatchStatement(conn, sql, table, keys);
						
						if (null != tracer) {
							String dataSourceName = null;
							long connCostTime = 0;
							if (conn instanceof DBConnection) {
								dataSourceName = ((DBConnection)conn).getName();
								connCostTime = ((DBConnection)conn).getConnCostTime();
							}
							tracer.startDaoProbe(dataSourceName, connCostTime, "delete", sql + "@" + stmt.getQueryTimeout(), null);
						}
					}
					
					TableMetaStatement.setDeleteBatchParameters(stmt, table, subparams, keys);
					int[] res = stmt.executeBatch();
					stmt.clearBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchsize + 1 + j] = res[j];
					}
					
					
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> 执行批量删除 begin %d end %d, batch size %d", (i - batchsize + 1), i, batchsize));
					}
					
					subparams = null;
				}
				
				if (subparams == null) {
					subparams = new DatasetList(batchsize);
				}
			}
			
			if (subparams != null) {
				if (stmt == null) {
					if (null == keys) {
						keys = table.getKeys();
					}
					
					String sql = TableMetaSQL.createDeleteSQL(table, keys);
					
					stmt = TableMetaStatement.createDeleteBatchStatement(conn, sql, table, keys);
					
					if (null != tracer) {
						String dataSourceName = null;
						long connCostTime = 0;
						if (conn instanceof DBConnection) {
							dataSourceName = ((DBConnection)conn).getName();
							connCostTime = ((DBConnection)conn).getConnCostTime();
						}
						tracer.startDaoProbe(dataSourceName, connCostTime, "delete", sql + "@" + stmt.getQueryTimeout(), null);
					}
				}
				
				TableMetaStatement.setDeleteBatchParameters(stmt, table, subparams, keys);
				int[] res = stmt.executeBatch();
				stmt.clearBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> 执行批量删除 begin %d end %d, batch size %d", executeIdx, size, batchsize));
				}
			}
			
			stmt.close();
			success = true;
			return result;
		} catch (SQLException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行delets %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "delets", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	public int[] insert(Connection conn, TableMetaObject table, IDataset source, int batchsize) throws SQLException {
		if (null == source || source.isEmpty())
			return new int[] {};
		
		PreparedStatement stmt = null;
		int size = source.size();	
		int[] result = new int[size];
		int executeIdx = 0;
		
		if (batchsize <= 0) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("批量值[%d]错误,采用默认值2000", batchsize));
			}
			batchsize = 2000;
		}
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		boolean success = false;
		
		try {
			IDataset subparams = null;
			for (int i = 0; i < size; i++) {
				if (subparams == null) {
					subparams = new DatasetList(batchsize);
				}
				
				executeIdx ++;
				subparams.add(source.get(i));
				
				if ( i > 0 &&  (i + 1) % batchsize == 0 ) {
					if (stmt == null) {
						String sql = TableMetaSQL.createInsertSQL(table);
						
						stmt = TableMetaStatement.createInsertBatchStatement(conn, sql, table);
						
						if (null != tracer) {
							String dataSourceName = null;
							long connCostTime = 0;
							if (conn instanceof DBConnection) {
								dataSourceName = ((DBConnection)conn).getName();
								connCostTime = ((DBConnection)conn).getConnCostTime();
							}
							tracer.startDaoProbe(dataSourceName, connCostTime, "insert", sql + "@" + stmt.getQueryTimeout(), null);
						}
					}
					
					TableMetaStatement.setInsertBatchParameters(stmt, table, subparams);
					int[] res = stmt.executeBatch();
					stmt.clearBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchsize + 1 + j] = res[j];
					}
					
					
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> 执行批量新增 begin %d end %d, batch size %d", (i - batchsize + 1), i, batchsize));
					}
					
					subparams = null;
				}
				
				if (subparams == null) {
					subparams = new DatasetList(batchsize);
				}
			}
			
			if (subparams != null) {
				if (stmt == null) {
					String sql = TableMetaSQL.createInsertSQL(table);
					
					stmt = TableMetaStatement.createInsertBatchStatement(conn, sql, table);
					
					if (null != tracer) {
						String dataSourceName = null;
						long connCostTime = 0;
						if (conn instanceof DBConnection) {
							dataSourceName = ((DBConnection)conn).getName();
							connCostTime = ((DBConnection)conn).getConnCostTime();
						}
						tracer.startDaoProbe(dataSourceName, connCostTime, "insert", sql + "@" + stmt.getQueryTimeout(), null);
					}
					
					executeIdx = 0;
				}
				
				TableMetaStatement.setInsertBatchParameters(stmt, table, subparams);
				int[] res = stmt.executeBatch();
				stmt.clearBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> 执行批量新增 begin %d end %d, batch size %d", executeIdx, size, batchsize));
				}
			}
			
			stmt.close();
			success = true;
			return result;
		} catch (SQLException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行inserts %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "inserts", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	
	public int[] executeBatch(Connection conn, String[] sqls, int batchsize) throws SQLException {
		if (null == sqls) {
			throw new SQLException("执行批量SQL异常，SQL语句不能为空");
		}
		
		int size = sqls.length;
		Statement stmt = null;
		int[] result = new int[size];
		int executeIdx = 0;
		
		if (batchsize <= 0) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("批量值[%d]错误,采用默认值2000", batchsize));
			}
			batchsize = 2000;
		}
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		boolean success = false;
		boolean finished = false;
		
		try {
			
			stmt = conn.createStatement();
			
			if (null != tracer) {
				String dataSourceName = null;
				long connCostTime = 0;
				if (conn instanceof DBConnection) {
					dataSourceName = ((DBConnection)conn).getName();
					connCostTime = ((DBConnection)conn).getConnCostTime();
				}
				tracer.startDaoProbe(dataSourceName, connCostTime, "batch", sqls[0] + "@" + stmt.getQueryTimeout(), null);
			}
			
			for (int i = 0; i < size; i++) {
				executeIdx ++;
				
				stmt.addBatch(sqls[i]);
				
				if ( i > 0 &&  (i + 1) % batchsize == 0 ) {
					int[] res = stmt.executeBatch();
					stmt.clearBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchsize + 1 + j] = res[j];
					}
					
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> 执行批量语句 begin %d end %d, batch size %d", (i - batchsize + 1), i, batchsize));
					}
					
					finished = true;
				} else {
					finished = false;
				}
			}
			
			if (!finished) {
				int[] res = stmt.executeBatch();
				stmt.clearBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> 执行批量语句 begin %d end %d, batch size %d", executeIdx, size, batchsize));
				}
			}
			
			stmt.close();
			success = true;
			return result;
		} catch (SQLException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行executeBatch %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "executebatch", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public int[] executeBatch(Connection conn, String colonSql, IDataset source, int batchsize) throws SQLException {
		if (null == colonSql) {
			throw new SQLException("批量执行SQL异常，SQL语句不能为空");
		}
		
		if (null == source || source.isEmpty()) {
			source = new DatasetList();
		}
		
		PreparedStatement stmt = null;
		int size = source.size();	
		int[] result = new int[size];
		int executeIdx = 0;
		
		if (batchsize <= 0) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("批量值[%d]错误,采用默认值2000", batchsize));
			}
			batchsize = 2000;
		}
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		boolean success = false;
		boolean finished = false;
		String sql = null;
		List<String> params = null;
		
		try {
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> %s", colonSql));
			}
			
			for (int i = 0; i < size; i++) {
				IData row = source.getData(i);
				executeIdx ++;
				
				if (null == stmt) {
					Object[] objs = TableMetaStatement.parseColonSql(colonSql);
					sql = (String) objs[0];
					params = (List<String>) objs[1];
					stmt = conn.prepareStatement(sql);
				}
				
				for (int j = 0, cnt = params.size(); j < cnt; j++) {
					String colName = params.get(j);
					Object value = row.get(colName);
					value = null == value ? "" : value;
					
					stmt.setObject(j + 1, value);
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> batch [%d] bind %s = [%s]", i, colName, value));
					}
				}
				stmt.addBatch();
				
				if ( i > 0 &&  (i + 1) % batchsize == 0 ) {
					int[] res = stmt.executeBatch();
					stmt.clearBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchsize + 1 + j] = res[j];
					}
					
					finished = true;
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> 执行批量语句 begin %d end %d, batch size %d", (i - batchsize + 1), i, batchsize));
					}
				} else {
					finished = false;
				}
			}
			
			if (!finished) {
				if (null == stmt) {
					Object[] objs = TableMetaStatement.parseColonSql(colonSql);
					sql = (String) objs[0];
					params = (List<String>) objs[1];
					stmt = conn.prepareStatement(sql);
					executeIdx = 0;
				}
				
				int[] res = stmt.executeBatch();
				stmt.clearBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> 执行批量语句 begin %d end %d, batch size %d", executeIdx, size, batchsize));
				}
			}
			
			stmt.close();
			success = true;
			return result;
		} catch (SQLException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行executeBatch %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "executebatch", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	
	public int[] executeBatch(Connection conn, String sql, String[][] source, int batchsize) throws SQLException {
		if (null == sql) {
			throw new SQLException("批量执行SQL异常，SQL语句不能为空");
		}
		
		if (null == source) {
			source = new String[0][0];
		}
		
		PreparedStatement stmt = null;
		int size = source.length;
		int[] result = new int[size];
		int executeIdx = 0;
		
		if (batchsize <= 0) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("批量值[%d]错误,采用默认值2000", batchsize));
			}
			batchsize = 2000;
		}
		
		long start = System.currentTimeMillis();
		long cost = 0L;
		boolean success = false;
		boolean finished = false;
		
		try {
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> %s", sql));
			}
			
			for (int i = 0; i < size; i++) {
				String[] row = source[i];
				executeIdx ++;
				
				if (null == stmt) {
					stmt = conn.prepareStatement(sql);
				}
				
				for (int j = 0, cnt = row.length; j < cnt; j++) {
					Object value = row[j];
					value = null == value ? "" : value;
					
					stmt.setObject(j + 1, value);
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> batch [%d] bind %d = [%s]", i, j + 1, value));
					}
				}
				stmt.addBatch();
				
				if ( i > 0 &&  (i + 1) % batchsize == 0 ) {
					int[] res = stmt.executeBatch();
					stmt.clearBatch();
					
					int resSize = res.length;
					
					for (int j = 0; j < resSize; j++) {
						result[i - batchsize + 1 + j] = res[j];
					}
					
					finished = true;
					if (log.isDebugEnabled()) {
						log.debug(String.format("SQL> 执行批量语句 begin %d end %d, batch size %d", (i - batchsize + 1), i, batchsize));
					}
				} else {
					finished = false;
				}
			}
			
			if (!finished) {
				if (null == stmt) {
					stmt = conn.prepareStatement(sql);
					executeIdx = 0;
				}
				
				int[] res = stmt.executeBatch();
				stmt.clearBatch();
				
				int resSize = res.length;
				
				for (int j = 0; j < resSize; j++) {
					result[size - resSize + j] = res[j];
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> 执行批量语句 begin %d end %d, batch size %d", executeIdx, size, batchsize));
				}
			}
			
			stmt.close();
			success = true;
			return result;
		} catch (SQLException e) {
			//conn.rollback();

			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("DAO> 执行批量语句 %s,共耗时 %d ms", success ? "成功": "失败", cost));
			}
			
			if (null != logger && null != logObject)
				logger.log(logObject, "executebatch", start, cost, null);
			
			if (null != tracer) {
				tracer.stopDaoProbe(success);
			}
		}
	}
	
	
	/**
	 * 读取字符，兼容NString
	 * @param conn
	 * @param meta
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	String getNString(ResultSetMetaData rsmd, int index, ResultSet rs, String columnName) throws SQLException {
		if (rsmd instanceof OracleResultSetMetaData) {
			OracleResultSetMetaData orsmd = (OracleResultSetMetaData) rsmd;
			
			if (orsmd.isNCHAR(index)) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> GetNString %s", columnName));
				}
				return rs.getNString(columnName);
			} else {
				return rs.getString(columnName);
			}
		}
		return rs.getString(columnName);
	}
	
}
