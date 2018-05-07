/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.oracle;


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
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.TableMetaObject;
import com.ailk.database.object.impl.ColumnObject;

import oracle.jdbc.OracleResultSetMetaData;

/**
 * Oracle表结构
 * 
 * @className: OracleTableMetaData.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class OracleTableMetaData extends TableMetaData {
	
	private static final Logger log = Logger.getLogger(OracleTableMetaData.class);
	
	private static final String deaultTableName = "dual";
	
	private static final String primaryKeysSQL = "select c.owner as table_schem, c.table_name, c.column_name, c.position as key_seq, c.constraint_name as pk_name　from all_cons_columns c, all_constraints k where k.constraint_type = 'P' and k.table_name = ? and k.owner = ? and k.constraint_name = c.constraint_name and k.table_name = c.table_name and k.owner = c.owner order by column_name";
	private static final String primaryKeysSQL2 = "select null as table_cat, c.owner as table_schem, c.table_name, c.column_name, c.position as key_seq, c.constraint_name as pk_name　from all_cons_columns c, all_constraints k where k.constraint_type = 'P' and k.table_name = ? and k.owner like ? escape '/' and k.constraint_name = c.constraint_name and k.table_name = c.table_name and k.owner = c.owner order by column_name";
	
	public OracleTableMetaData(DataSourceWrapper ds) {
		super(ds);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.wrapper.TableMetaData#getInitLoadTableNames()
	 */
	@Override
	protected String[] getInitLoadTableNames() {
		try {
			Properties props = new Properties();
			props.load(getClass().getClassLoader().getResourceAsStream("loadtables.properties"));
			
			String tableNames = props.getProperty(getDataSourceName() + ".load.tables", "");
			if (null == tableNames || tableNames.trim().length() == 0) {
				return null;
			}
			return tableNames.split(",");
		} catch (Exception e) {
			log.info("加载初始化表名异常，" + e.getMessage());
			return null;
		}
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
			table.setOraclePagin(true);
			
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
		StringBuilder selectRowIdSQL = new StringBuilder("select ROWID, ");
		StringBuilder updateSQL = new StringBuilder("update ").append(tableName).append(" set ");
		StringBuilder deleteSQL = new StringBuilder("delete from ").append(tableName).append(" ");
		StringBuilder insertSQL1 = new StringBuilder("insert into ").append(tableName).append(" ( ");
		StringBuilder insertSQL2 = new StringBuilder(" ) values ( ");
		StringBuilder countSQL = new StringBuilder("select count(1) from (%s) ");
		
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
		table.setPageSQL("select * from (select row_.*, rownum rownum_ from (%s) row_ where rownum <= ?) where rownum_ > ?");
		table.setSequenceSQL("select %s.nextval from dual connect by level <= %d" );
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
			String sql = primaryKeysSQL;
			String owner = getOwner();
			
			if (null == getOwner() || getOwner().length() <= 0) {
				sql = primaryKeysSQL2;
				owner = "%";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tableName);
			pstmt.setString(2, owner);
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("获取表主键SQL:%s", sql));
				log.debug(String.format("获取表主键绑定参数:1 = %s", tableName));
				log.debug(String.format("获取表主键绑定参数:2 = %s", owner));
			}
			rs = pstmt.executeQuery();
			
			List<String> keyList = new ArrayList<String>();
			
			while (rs.next()) {
				String field = rs.getString("COLUMN_NAME").toUpperCase();
				if (!keyList.contains(field))
					keyList.add(field);
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
			
			OracleResultSetMetaData metaData = (OracleResultSetMetaData) pstmt.executeQuery().getMetaData();
			
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
				column.setNString(metaData.isNCHAR(i));

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
		sb.append(" sample block(1) where 1=2");
		
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
