/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.altibase;


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

import com.ailk.database.jdbc.altibase.AltibaseTableMetaData;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.TableMetaObject;
import com.ailk.database.object.impl.ColumnObject;

/**
 * Altibase表结构
 * 
 * @className: AltibaseTableMetaData.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class AltibaseTableMetaData extends TableMetaData {
	
	private static final Logger log = Logger.getLogger(AltibaseTableMetaData.class);
	private static final String deaultTableName = "dual";	
	private static StringBuilder primaryKeysSQL = new StringBuilder(500);
	
	static {
		primaryKeysSQL.append("SELECT A.TABLE_NAME, D.COLUMN_NAME, B.CONSTRAINT_NAME AS PK_NAME");
		primaryKeysSQL.append("  FROM SYSTEM_.SYS_TABLES_ A, SYSTEM_.SYS_CONSTRAINTS_ B, SYSTEM_.SYS_CONSTRAINT_COLUMNS_ C, SYSTEM_.SYS_COLUMNS_ D");
		primaryKeysSQL.append(" WHERE A.TABLE_NAME = ?");
		primaryKeysSQL.append("   AND A.TABLE_ID = B.TABLE_ID");
		primaryKeysSQL.append("   AND B.CONSTRAINT_TYPE = 3");
		primaryKeysSQL.append("   AND C.CONSTRAINT_ID = B.CONSTRAINT_ID");
		primaryKeysSQL.append("   AND D.COLUMN_ID = C.COLUMN_ID");
	}
	
	public AltibaseTableMetaData(DataSourceWrapper ds) {
		super(ds);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.TableMetaData#initTable(java.lang.String)
	 */
	@Override
	public void initTable(TableMetaObject table) throws SQLException {
		Connection conn = null;
		String tableName = table.getTableName();
		
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
			table.setOraclePagin(false);
			
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
		StringBuilder countSQL = new StringBuilder("select count(1) from (%s) ");
		
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
		table.setUpdateSQL(updateSQL.toString());
		table.setInsertSQL(insertSQL1.append(insertSQL2).append(")").toString());
		table.setDeleteSQL(deleteSQL.toString());
		table.setCountSQL(countSQL.toString());
		table.setPageSQL("%s limit ? ?");
		table.setSequenceSQL(null); // altibase 无序列
		table.setSystimeSQL("select to_char(systimestamp, 'yyyy-MM-dd HH24:mi:ss.ff') from dual");
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
			String sql = primaryKeysSQL.toString();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tableName);
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("获取表主键SQL:%s", sql));
				log.debug(String.format("获取表主键绑定参数:1 = %s", tableName));
			}
			rs = pstmt.executeQuery();
			
			List<String> keyList = new ArrayList<String>();
			
			while (rs.next()) {
				String field = rs.getString("COLUMN_NAME");
				if (!keyList.contains(field))
					keyList.add(field.toUpperCase());
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
		sb.append(" limit 1");
		
		if (log.isDebugEnabled()) {
			log.debug("初始化表字段查询语句:" + sb.toString());
		}
		
		return sb.toString();
	}
	
	@Override
	public String getDefaultTableName() {
		return deaultTableName;
	}
	
}
