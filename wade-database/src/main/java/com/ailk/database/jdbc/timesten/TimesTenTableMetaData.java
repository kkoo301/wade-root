/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.timesten;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.TableMetaObject;
import com.ailk.database.object.impl.ColumnObject;

/**
 * TimesTen表结构
 * 
 * @className: TimesTenTableMetaData.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class TimesTenTableMetaData extends TableMetaData {
	
	private static final Logger log = Logger.getLogger(TimesTenTableMetaData.class);
	
	private static final String deaultTableName = "DUAL";
	
	public TimesTenTableMetaData(DataSourceWrapper ds) {
		super(ds);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.TableMetaData#initTable(java.lang.String)
	 */
	@Override
	public void initTable(TableMetaObject table) throws SQLException {
		Connection conn = null;
		String tableName = table.getTableName();
		
		boolean isDefaultTable = deaultTableName.equals(tableName);
		
		try {
			conn = getDataSource().getConnection();
			if (null == conn) {
				return ;
			}
			
			String[] keys = initPrimaryKeys(conn, tableName);
			
			Map<String, IColumnObject> columns = initColumns(conn, tableName, keys);
			
			table.setKeys(keys);
			
			initSQLs(table, columns);
			
			table.setColumns(columns);
			table.setInitialized(Boolean.TRUE);
			
		} catch (SQLException e) {
			log.error(String.format("获取表结构%s@%s.%s错误", getDataSourceName(), isDefaultTable ? "SYS" : getUser(), tableName), e);
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error(String.format("获取表主键%s@%s.%s错误", getDataSourceName(), isDefaultTable ? "SYS" : getUser(), tableName), e);
				}
			}
		}
	}
	
	/**
	 * 初始化查询语句
	 * @param tableName
	 * @param columns
	 */
	private void initSQLs(TableMetaObject table, Map<String, IColumnObject> columns) {
		String tableName = table.getTableName();
		
		StringBuilder selectSQL = new StringBuilder("select ");
		StringBuilder selectRowIdSQL = new StringBuilder("select ROWID, ");
		StringBuilder updateSQL = new StringBuilder("update ").append(tableName).append(" set ");
		StringBuilder deleteSQL = new StringBuilder("delete from ").append(tableName).append(" ");
		StringBuilder insertSQL1 = new StringBuilder("insert into ").append(tableName).append(" ( ");
		StringBuilder insertSQL2 = new StringBuilder(" ) values ( ");
		StringBuilder countSQL = new StringBuilder("select count(1) from (%s) a");
		
		Iterator<String> iter = columns.keySet().iterator();
		while (iter.hasNext()) {
			String colName = iter.next();
			
			selectSQL.append(colName);
			selectRowIdSQL.append(colName);
			updateSQL.append(colName).append(" = ? ");
			insertSQL1.append(colName);
			insertSQL2.append("?");
			
			if (iter.hasNext()) {
				selectSQL.append(", ");
				selectRowIdSQL.append(", ");
				updateSQL.append(", ");
				insertSQL1.append(", ");
				insertSQL2.append(", ");
			}
		}
		
		table.setSelectSQL(selectSQL.append(" from ").append(tableName).toString());
		table.setSelectRowIdSQL(selectRowIdSQL.append(" from ").append(tableName).toString());
		table.setUpdateSQL(updateSQL.toString());
		table.setInsertSQL(insertSQL1.append(insertSQL2).append(")").toString());
		table.setDeleteSQL(deleteSQL.toString());
		table.setCountSQL(countSQL.toString());
		table.setPageSQL("select * from (select row_.*, rownum rownum_ from (%s) row_ where rownum < ?) where rownum_ >= ?");
		table.setSequenceSQL("select %s.nextval as SEQ_%d from SYS.DUAL" );
		table.setSystimeSQL("select sysdate from SYS.DUAL");
	}
	
	
	/**
	 * 初始化表主键数组
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private String[] initPrimaryKeys(Connection conn, String tableName) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String[] keys = null;
		boolean isDefaultTable = deaultTableName.equals(tableName);
		try {
			rs = conn.getMetaData().getPrimaryKeys(null, isDefaultTable ? "SYS" : getDataSource().getUser(), tableName);
			
			List<String> keyList = new ArrayList<String>();
			
			while (rs.next()) {
				String field = rs.getString("COLUMN_NAME");
				keyList.add(field.toUpperCase());
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("表主键%s@%s.%s %s", getDataSourceName(), isDefaultTable ? "SYS" : getUser(), tableName, keyList.toString()));
			}
			keys = keyList.toArray(new String[0]);
			
		} catch (SQLException e) {
			log.error(String.format("获取表主键%s@%s.%s错误", getDataSourceName(), isDefaultTable ? "SYS" : getUser(), tableName), e);
			throw e;
		} finally {
			if (null != pstmt)
				pstmt.close();
			if (null != rs)
				rs.close();
		}
		
		return keys;
	}
	
	
	/**
	 * 根据表主键生成SQL来初始化表字段
	 * @param conn
	 * @param tableName
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private Map<String, IColumnObject> initColumns(Connection conn, String tableName, String[] keys) throws SQLException {
		PreparedStatement pstmt = null;
		Map<String, IColumnObject> columns = null;
		String sql = getColumnsSQL(tableName, keys);
		
		List<String> keysList = Arrays.asList(keys);
		try {
			pstmt = conn.prepareStatement(sql);
			
			ResultSetMetaData metaData = pstmt.executeQuery().getMetaData();
			
			int cnt = metaData.getColumnCount();
			
			columns = new HashMap<String, IColumnObject>(cnt);
			
			for (int i = 1; i <= cnt; i++) {
				IColumnObject column = new ColumnObject();
				String colName = metaData.getColumnName(i).toUpperCase();
				column.setColumnName(colName);
				column.setKey(keysList.contains(colName));
				column.setColumnType(metaData.getColumnType(i));
				column.setColumnDesc(metaData.getColumnLabel(i));
				column.setColumnSize(metaData.getColumnDisplaySize(i));
				column.setDecimalDigits(metaData.getScale(i));
				column.setNullable(metaData.isNullable(i) == ResultSetMetaData.columnNoNulls ? false : true);

				columns.put(column.getColumnName(), column);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (null != pstmt)
				pstmt.close();
		}
		
		return columns;
	}
	
	/**
	 * 根据主键获取查询表字段结构的SQL语句
	 * {@code 需验证Char(1)和Date类型的是否有问题}
	 * @param tableName
	 * @param keys
	 * @return
	 */
	private String getColumnsSQL(String tableName, String[] keys) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		if (deaultTableName.equals(tableName)) {
			sb.append("SYS.DUAL");
		} else {
			sb.append(tableName);
		}
		sb.append(" where rownum = 1");
		
		if (log.isDebugEnabled()) {
			log.debug("初始化表字段查询语句:" + sb.toString());
		}
		
		return sb.toString();
	}
	
	
	@Override
	public String getDefaultTableName() {
		return "SYS." + deaultTableName;
	}
}
