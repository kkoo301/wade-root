/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.mysql;

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

import com.ailk.database.jdbc.wrapper.TableMetaData;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.TableMetaObject;
import com.ailk.database.object.impl.ColumnObject;

/**
 * MySQL表结构
 * 
 * @className: MySQLTableMetaData.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class MySQLTableMetaData extends TableMetaData {
	
	private static final Logger log = Logger.getLogger(MySQLTableMetaData.class);
	
	private static final String deaultTableName = "information_schema.SCHEMATA";
	
	public MySQLTableMetaData(MySQLDataSourceWrapper ds) {
		super(ds);
	}
	
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.TableMetaData#initTable(java.lang.String)
	 */
	@Override
	public void initTable(TableMetaObject table) throws SQLException {
		Connection conn = null;
		String tableName = table.getTableName();
		
		long start = System.currentTimeMillis();
		
		try {
			conn = getDataSource().getConnection();
			if (null == conn) {
				return ;
			}
			
			String[] keys = initPrimaryKeys(conn, tableName);
			Map<String, IColumnObject> columns = initColumns(conn, tableName, keys);
			
			table.setKeys(keys);
			table.setColumns(columns);
			
			initSQLs(table, columns);
			
			table.setInitialized(Boolean.TRUE);
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("初始化表对象%s@%s.%s成功", getDataSourceName(), getUser(), tableName));
			}
			
		} catch (SQLException e) {
			log.error(String.format("获取表主键%s@%s.%s错误", getDataSourceName(), getUser(), tableName), e);
			throw e;
		} finally {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error(String.format("获取表主键%s@%s.%s错误", getDataSourceName(), getUser(), tableName), e);
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(String.format("初始化表对象%s@%s.%s完成,耗时 %d ms", getDataSourceName(), getUser(), tableName, (System.currentTimeMillis() - start)));
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
		StringBuilder updateSQL = new StringBuilder("update ").append(tableName).append(" set ");
		StringBuilder deleteSQL = new StringBuilder("delete from ").append(tableName).append(" ");
		StringBuilder insertSQL1 = new StringBuilder("insert into ").append(tableName).append(" ( ");
		StringBuilder insertSQL2 = new StringBuilder(" ) values ( ");
		StringBuilder countSQL = new StringBuilder("select count(1) from (%s) a");
		
		Iterator<String> iter = columns.keySet().iterator();
		while (iter.hasNext()) {
			String colName = iter.next();
			
			selectSQL.append(colName);
			updateSQL.append(colName).append(" = ? ");
			insertSQL1.append(colName);
			insertSQL2.append("?");
			
			if (iter.hasNext()) {
				selectSQL.append(", ");
				updateSQL.append(", ");
				insertSQL1.append(", ");
				insertSQL2.append(", ");
			}
		}
		
		table.setSelectSQL(selectSQL.append(" from ").append(tableName).toString());
		table.setSelectRowIdSQL(selectSQL.toString());
		table.setUpdateSQL(updateSQL.toString());
		table.setInsertSQL(insertSQL1.append(insertSQL2).append(")").toString());
		table.setDeleteSQL(deleteSQL.toString());
		table.setCountSQL(countSQL.toString());
		table.setPageSQL("%s limit ?, ?");
		table.setSequenceSQL("select nextval('%s', %d) as SEQ_ID");
		table.setSystimeSQL("select sysdate()");
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
		try {
			pstmt = conn.prepareStatement("desc " + tableName);
			rs = pstmt.executeQuery();
			
			List<String> keyList = new ArrayList<String>();
			
			while (rs.next()) {
				String field = rs.getString("FIELD").toUpperCase();
				String prim = rs.getString("KEY");
				
				if ("PRI".equals(prim)) {
					keyList.add(field);
				}
			}
			
			keys = keyList.toArray(new String[0]);
			
		} catch (SQLException e) {
			log.error(String.format("获取表主键%s@%s.%s错误", getDataSourceName(), getUser(), tableName), e);
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
		sb.append(tableName);
		
		for (int i=0, cnt = keys.length; i < cnt; i++) {
			if (i == 0)
				sb.append(" where ").append(keys[i]).append(" = '1' ");
			else
				sb.append(" and ").append(keys[i]).append(" = '1' ");
		}
		
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.wrapper.TableMetaData#getDefaultTableName()
	 */
	@Override
	public String getDefaultTableName() {
		return deaultTableName;
	}
}
