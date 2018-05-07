/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.wrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.jdbc.oracle.OracleConnection;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.TableMetaObject;
import com.ailk.database.util.DaoUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 根据表结构和数据创建PreparedStatement对象
 * 
 * @className: TableMetaStatement.java
 * @author: liaosheng
 * @date: 2014-4-12
 */
public final class TableMetaStatement {
	
	private static final Logger log = Logger.getLogger(TableMetaStatement.class);
	
	
	public static final PreparedStatement getCurrentTimeStatement(Connection conn, TableMetaObject table, String sql) throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		return conn.prepareStatement(sql);
	}
	
	public static final PreparedStatement getSequence(Connection conn, TableMetaObject table, String seqName, int increment, String sql) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		return stmt;
	}
	
	
	public static final PreparedStatement createCountStatement(Connection conn, TableMetaObject table, String[] keys, IData source, String sql) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = source.getString(keys[i], "");
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		
		return stmt;
	}

	
	public static final PreparedStatement createCountStatement(Connection conn, TableMetaObject table, String[] keys, String values[], String sql) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		
		return stmt;
	}
	
	/**
	 * @deprecated
	 * @param conn
	 * @param table
	 * @param sql
	 * @param values
	 * @param begin
	 * @param end
	 * @param limit
	 * @return
	 * @throws SQLException
	 */
	static final PreparedStatement createQueryStatement(Connection conn, TableMetaObject table, String sql, String[] values, int begin, int end, long limit) throws SQLException {
		String sb = TableMetaSQL.createPaginationSQL(table, sql, begin, end);
		
		PreparedStatement stmt = conn.prepareStatement(sb);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sb.toString()));
		}
		
		int cnt = values.length;
		for (int i = 0; i < cnt; i++) {
			String value = values[i];
			
			stmt.setString(i + 1, value);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d = [%s]", i + 1, value));
			}
		}
		
		if (0 == begin && 0 == end) {
			
		} else {
			if (table.isOraclePagin()) {
				stmt.setLong(cnt + 1, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, end));
				}
				
				stmt.setLong(cnt + 2, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]",cnt + 2, begin));
				}
			} else {
				stmt.setLong(cnt + 1, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, end));
				}
				
				stmt.setLong(cnt + 2, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]",cnt + 2, begin));
				}
			}
		}
		
		return stmt;
	}
	
	/**
	 * @deprecated
	 * @param conn
	 * @param table
	 * @param colonSql
	 * @param source
	 * @param begin
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	static final PreparedStatement createExecuteQueryStatement(Connection conn, TableMetaObject table, String colonSql, IData source, int begin, int end) throws SQLException {
		String sb = TableMetaSQL.createPaginationSQL(table, colonSql, begin, end);
		
		Object[] objs = TableMetaSQL.parseColonSql(sb);
		String sql = (String) objs[0];
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		List<String> params = (List<String>) objs[1];
		
		int cnt = params.size();
		for (int i = 0; i < cnt; i++) {
			String value = source.getString(params.get(i), "");
			
			stmt.setString(i + 1, value);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d = [%s]", i + 1, value));
			}
		}
		
		if (0 == begin && 0 == end) {
			
		} else {
			if (table.isOraclePagin()) {
				stmt.setLong(cnt + 1, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, end));
				}
				
				stmt.setLong(cnt + 2, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 2, begin));
				}
			} else {
				stmt.setLong(cnt + 1, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, begin));
				}
				
				stmt.setLong(cnt + 2, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 2, end));
				}
			}
		}
		
		return stmt;
	}
	
	
	public static final PreparedStatement createQueryTableStatement(Connection conn, String sql, TableMetaObject table, String[] keys, String values[], int begin, int end) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		int cnt = keys.length;
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		
		if (begin == 0 && end == 0) {
			
		} else {
			if (table.isOraclePagin()) {
				stmt.setLong(cnt + 1, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 1, end));
				}
				
				stmt.setLong(cnt + 2, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 2, begin));
				}
			} else {
				stmt.setLong(cnt + 1, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 1, begin));
				}
				
				stmt.setLong(cnt + 2, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 2, end));
				}
			}
		}
		
		return stmt;
	}
	
	
	public static final PreparedStatement createQueryTableStatement(Connection conn, String sql, TableMetaObject table, String[] keys, IData source, int begin, int end) throws SQLException {
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		int cnt = keys.length;
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = source.getString(keys[i], "");
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		
		if (begin == 0 && end == 0) {
			
		} else {
			if (table.isOraclePagin()) {
				stmt.setLong(cnt + 1, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 1, end));
				}
				
				stmt.setLong(cnt + 2, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 2, begin));
				}
			} else {
				stmt.setLong(cnt + 1, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 1, begin));
				}
				
				stmt.setLong(cnt + 2, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", cnt + 2, end));
				}
			}
			
		}
		
		return stmt;
	}
	
	
	public static final PreparedStatement createQueryByPKStatement(Connection conn, String sql, TableMetaObject table, String[] keys, String[] values) throws SQLException {
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		
		return stmt;
	}
	
	public static final PreparedStatement createQueryByPKStatement(Connection conn, String sql, TableMetaObject table, String[] keys, IData source) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = source.getString(keys[i], "");
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		return stmt;
	}
	
	
	public static final PreparedStatement createDeleteByPKStatement(Connection conn, String sql, TableMetaObject table, String[] keys, String[] values) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		return stmt;
	}
	
	
	public static final PreparedStatement createDeleteByPKStatement(Connection conn, String sql, TableMetaObject table, String[] keys, IData source) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = source.getString(keys[i], "");
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, keys[i], value));
			}
		}
		return stmt;
	}
	
	public static final PreparedStatement createUpdateByPKStatement(Connection conn, String sql, TableMetaObject table, String[] cols, String[] colValues, String[] keys, String[] values) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		int colsLen = cols.length;
		Map<String, IColumnObject> columns = table.getColumns();
		for (int i = 0; i < colsLen; i++) {
			IColumnObject column = columns.get(cols[i]);
			String value = colValues[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1, cols[i], value));
			}
		}
		
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1 + colsLen, value);
			} else {
				bindValue(stmt, i + 1 + colsLen, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1 + colsLen, keys[i], value));
			}
		}
		return stmt;
	}
	
	public static final PreparedStatement createUpdateByPKStatement(Connection conn, String sql, TableMetaObject table, String[] cols, IData source, String[] keys, String[] values) throws SQLException {
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		int colsLen = cols.length;
		
		Map<String, IColumnObject> columns = table.getColumns();
		
		for (int i = 0; i < colsLen; i++) {
			IColumnObject column = columns.get(cols[i]);
			String value = source.getString(cols[i], "");
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1, value);
			} else {
				bindValue(stmt, i + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]",  i + 1, cols[i], value));
			}
		}
		
		int keyLen = keys.length;
		
		for (int i = 0; i < keyLen; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1 + colsLen, value);
			} else {
				bindValue(stmt, i + 1 + colsLen, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]",  i + 1 + colsLen, keys[i], value));
			}
		}
		return stmt;
	}
	
	
	public static final PreparedStatement createSaveByPKStatement(Connection conn, String sql, TableMetaObject table, IData data, String[] cols, String[] colValues, String[] keys, String[] values) throws SQLException {
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		//将要修改的字段更新到查询出来的结果集里
		for (int i = 0, cnt = cols.length; i< cnt; i++) {
			data.put(cols[i], colValues[i]);
		}
		
		Map<String, IColumnObject> columns = table.getColumns();
		Iterator<String> iterBind = columns.keySet().iterator();
		int index = 0;
		while (iterBind.hasNext()) {
			IColumnObject column = columns.get(iterBind.next());
			String colName = column.getColumnName();
			String value = data.getString(colName, "");
			
			if ("ROWID".equals(colName)) {
				stmt.setString(index + 1, value);
			} else {
				bindValue(stmt, index + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", index + 1, colName, value));
			}
			index ++;
		}
		
		int colSize = columns.size();
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (null == column && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1 + colSize, value);
			} else {
				bindValue(stmt, i + 1 + colSize, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1 + colSize, keys[i], value));
			}
		}
		return stmt;
	}
	
	
	public static final PreparedStatement createSaveByPKStatement(Connection conn, String sql, TableMetaObject table, IData data, IData source, String[] keys, String[] values) throws SQLException {
		Map<String, IColumnObject> columns = table.getColumns();
		
		int keysLen = keys.length;
		if (null == values) {
			values = new String[keysLen];
			for (int i = 0; i < keysLen; i++) {
				values[i] = source.getString(keys[i], "");
			}
		}
		
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Iterator<String> iter = columns.keySet().iterator();
		int index = 0;
		while (iter.hasNext()) {
			IColumnObject column = columns.get(iter.next());
			String colName = column.getColumnName();
			String value = data.getString(colName, "");
			
			if ("ROWID".equals(colName)) {
				stmt.setString(index + 1, value);
			} else {
				bindValue(stmt, index + 1, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", index + 1, colName, value));
			}
			index ++;
		}
		
		int colSize = columns.size();
		for (int i = 0; i < keysLen; i++) {
			IColumnObject column = columns.get(keys[i]);
			String value = values[i];
			
			if (column == null && "ROWID".equals(keys[i])) {
				stmt.setString(i + 1 + colSize, value);
			} else {
				bindValue(stmt, i + 1 + colSize, column, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", i + 1 + colSize, keys[i], value));
			}
		}
		return stmt;
	}
	
	
	public static final PreparedStatement createInsertStatement(Connection conn, String sql, TableMetaObject table, String[] keys, String[] values) throws SQLException {
		Map<String, IColumnObject> columns = table.getColumns();
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		IData cols = new DataMap();
		Iterator<String> bindIter = columns.keySet().iterator();
		while (bindIter.hasNext()) {
			cols.put(bindIter.next(), null);
		}
		for (int i = 0, cnt = keys.length; i < cnt; i++) {
			cols.put(keys[i], values[i]);
		}
		
		Iterator<String> iter = columns.keySet().iterator();
		int index = 0;
		while (iter.hasNext()) {
			String colName = iter.next();
			IColumnObject object = columns.get(colName);
			
			String value = cols.getString(colName, "");
			value = null == value ? "" : value;
			
			if (object == null && "ROWID".equals(colName)) {
				stmt.setString(index + 1, value);
			} else {
				bindValue(stmt, index + 1, object, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", index + 1, colName, value));
			}
			index ++;
		}
		
		return stmt;
	}
	
	public static final PreparedStatement createInsertStatement(Connection conn, String sql, TableMetaObject table, IData source) throws SQLException {
		Map<String, IColumnObject> columns = table.getColumns();
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		Iterator<String> iter = columns.keySet().iterator();
		int index = 0;
		while (iter.hasNext()) {
			String colName = iter.next();
			String value = source.getString(colName, "");
			
			IColumnObject object = columns.get(colName);
			
			if (object == null && "ROWID".equals(colName)) {
				stmt.setString(index + 1, value);
			} else {
				bindValue(stmt, index + 1, object, value);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d %s = [%s]", index + 1, colName, value));
			}
			index ++;
		}
		
		return stmt;
	}
	
	public static final void setInsertBatchParameters(PreparedStatement stmt, TableMetaObject table, IDataset source) throws SQLException {
		int cnt = source.size();
		
		String[] keys = table.getColumnNames();
		Map<String, IColumnObject> columns = table.getColumns();
		
		for (int i = 0; i < cnt; i++) {
			IData row = source.getData(i);
			
			for (int j = 0, keysize = keys.length; j < keysize; j++) {
				String colName = keys[j];
				String value = row.getString(colName, "");
				
				IColumnObject object = columns.get(colName);
				
				if (object == null && "ROWID".equals(colName)) {
					stmt.setString(j + 1, value);
				} else {
					bindValue(stmt, j + 1, object, value);
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> batch %d bind %d %s = [%s]", i, j + 1, colName, value));
				}
			}
			
			stmt.addBatch();
		}
	}
	
	public static final void setDeleteBatchParameters(PreparedStatement stmt, TableMetaObject table, IDataset source, String[] keys) throws SQLException {
		if (null == keys) {
			keys = table.getKeys();
		}
		
		int cnt = source.size();
		Map<String, IColumnObject> columns = table.getColumns();
		
		for (int i = 0; i < cnt; i++) {
			IData row = source.getData(i);
			
			for (int j = 0, keysize = keys.length; j < keysize; j++) {
				String colName = keys[j];
				String value = row.getString(colName, "");
				
				IColumnObject object = columns.get(colName);
				
				if (object == null && "ROWID".equals(colName)) {
					stmt.setString(j + 1, value);
				} else {
					bindValue(stmt, j + 1, object, value);
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> batch %d bind %d %s = [%s]", i, j + 1, colName, value));
				}
			}
			
			stmt.addBatch();
		}
	}
	
	public static final void setUpdateBatchParameters(PreparedStatement stmt, TableMetaObject table, IDataset source, String[] cols, String[] keys) throws SQLException {
		if (null == keys) {
			keys = table.getKeys();
		}
		
		if (null == cols) {
			cols = table.getColumnNames();
		}
		
		int cnt = source.size();
		Map<String, IColumnObject> columns = table.getColumns();
		
		for (int i = 0; i < cnt; i++) {
			IData row = source.getData(i);
			
			int colsLen = cols.length;
			for (int j = 0; j < colsLen; j++) {
				String colName = cols[j];
				String value = row.getString(colName, "");
				
				IColumnObject object = columns.get(colName);
				
				if (object == null && "ROWID".equals(colName)) {
					stmt.setString(j + 1, value);
				} else {
					bindValue(stmt, j + 1, object, value);
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> batch %d bind %d %s = [%s]", i, j + 1, colName, value));
				}
			}
			
			for (int j = 0, keysize = keys.length; j < keysize; j++) {
				String colName = keys[j];
				IColumnObject object = columns.get(colName);
				String value = row.getString(colName, "");
				
				if (object == null && "ROWID".equals(colName)) {
					stmt.setString(j + 1 + colsLen, value);
				} else {
					bindValue(stmt, j + 1 + colsLen, object, value);
				}
				
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> batch %d bind %d %s = [%s]", i, j + 1 + colsLen, colName, value));
				}
			}
			
			stmt.addBatch();
		}
	}
	
	
	
	public static final PreparedStatement createInsertBatchStatement(Connection conn, String sql, TableMetaObject table) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		return stmt;
	}
	
	public static final PreparedStatement createDeleteBatchStatement(Connection conn, String sql, TableMetaObject table, String[] keys) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		return stmt;
	}
	
	public static final PreparedStatement createUpdateBatchStatement(Connection conn, String sql, TableMetaObject table, String[] cols, String[] keys) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		return stmt;
	}
	
	/**
	 * 按SQL直接绑定变量，不处理占位符，无法做NString处理
	 * @param conn
	 * @param sql
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public static final PreparedStatement createExecuteUpdateStatement(Connection conn, String sql, String[] values) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		int cnt = values.length;
		for (int i = 0; i < cnt; i++) {
			String value = values[i];
			
			stmt.setString(i + 1, value);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d = [%s]", i + 1, value));
			}
		}
		
		return stmt;
	}
	
	/**
	 * 创建Update的PrepareStatement对象
	 * @param conn
	 * @param colonSql
	 * @param source
	 * @param objs
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static final PreparedStatement createExecuteUpdateStatement(Connection conn, String colonSql, IData source, Object[] objs) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement((String) objs[0]);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", (String) objs[0]));
		}
		
		List<String> params = (List<String>) objs[1];
		
		for (int i = 0, cnt = params.size(); i < cnt; i++) {
			String columnName = params.get(i);
			String value = source.getString(columnName, "");
			
			bindString(conn, stmt, source, columnName, value, i + 1);
		}
		
		return stmt;
	}
	
	/**
	 * 不支持NString
	 * @param conn
	 * @param table
	 * @param sqlstr
	 * @param values
	 * @param begin
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public static final PreparedStatement createExecuteQueryStatement(Connection conn, TableMetaObject table, String sqlstr, String[] values, int begin, int end) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sqlstr);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sqlstr));
		}
		
		int cnt = values.length;
		for (int i = 0; i < cnt; i++) {
			String value = values[i];
			
			stmt.setString(i + 1, value);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d = [%s]", i + 1, value));
			}
		}
		
		if (0 == begin && 0 == end) {
			
		} else {
			if (table.isOraclePagin()) {
				stmt.setLong(cnt + 1, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, end));
				}
				
				stmt.setLong(cnt + 2, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 2, begin));
				}
			} else {
				stmt.setLong(cnt + 1, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, begin));
				}
				
				stmt.setLong(cnt + 2, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 2, end));
				}
			}
		}
		
		return stmt;
	}
	
	
	/**
	 * 
	 * @param conn
	 * @param table
	 * @param sql
	 * @param source
	 * @param params
	 * @param begin
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public static final PreparedStatement createExecuteQueryStatement(Connection conn, TableMetaObject table, String sql, IData source, List<String> params, int begin, int end) throws SQLException {
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("SQL> %s", sql));
		}
		
		int cnt = params.size();
		for (int i = 0; i < cnt; i++) {
			String columnName = params.get(i);
			String value = source.getString(columnName, "");
			
			bindString(conn, stmt, source, columnName, value, i + 1);
		}
		
		if (0 == begin && 0 == end) {
			
		} else {
			if (table.isOraclePagin()) {
				stmt.setLong(cnt + 1, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, end));
				}
				
				stmt.setLong(cnt + 2, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 2, begin));
				}
			} else {
				stmt.setLong(cnt + 1, begin);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 1, begin));
				}
				
				stmt.setLong(cnt + 2, end);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %s = [%s]", cnt + 2, end));
				}
			}
		}
		
		return stmt;
	}
	
	
	/**
	 * 替换SQL里所有的:XXXX为?，并返回XXXX的List数据
	 * @param colonSql
	 * @return
	 */
	public static final Object[] parseColonSql(String colonSql) {
		char[] sqlchar = colonSql.toCharArray();
		
		boolean isInnerQuota = false; // 标志: 是否在引号中
		boolean isInnerParam = false; // 标志：是否在参数中
		
		int begParam = -1; // 参数开始位置
		
		List<String> paramNames = new ArrayList<String>(20);
		
		StringBuilder sb = new StringBuilder(sqlchar.length);
		for (int i = 0, size=sqlchar.length; i < size; i++) {

			char c = sqlchar[i];
			
			if ('\'' == c) {
				if (isInnerQuota) {
					isInnerQuota = false; // 退出引号中
				} else {
					isInnerQuota = true;  // 进入引号中
				}
				sb.append(c);
				continue;
			}
			
			if (isInnerQuota) {
				sb.append(c);
				continue;
			} 
			
			if (':' == c) {
				
				if (i + 1 < size && '=' == sqlchar[i + 1]) {
					sb.append(c);
					continue;
				}
				
				isInnerParam = true;
				begParam = i + 1;
				sb.append('?');
				continue;
			} 
			
			if (isInnerParam) {
				if (!DaoUtil.isVariableChar(c)) {
					isInnerParam = false;
					
					String paramName = colonSql.substring(begParam, i);
					if (!StringUtils.isBlank(paramName)) {
						paramNames.add(paramName.toUpperCase());
					}
					begParam = -1;
					
					sb.append(c);
				}
				continue;
			} 

			sb.append(c);

		}
		
		if (-1 != begParam) {
			String paramName = colonSql.substring(begParam, sqlchar.length);
			if (!StringUtils.isBlank(paramName)) {
				paramNames.add(paramName.toUpperCase());
			}
		}
		
		return new Object[] {sb.toString(), paramNames};
	}
	
	/**
	 * 根据类型及值范围绑定变量
	 * @param stmt
	 * @param index
	 * @param column
	 * @param value
	 * @throws SQLException
	 */
	private static void bindValue(PreparedStatement stmt, int index, IColumnObject column, String value) throws SQLException {
		if (null == column) {
			throw new SQLException(String.format("绑定第%d个参数时，发现未定义的字段名", index));
		}
		
		if (null == value || value.length() == 0) {
			stmt.setNull(index, Types.NULL);
			return;
		}
		
		if (column.isDatetimeColumn()) {
			stmt.setTimestamp(index, DaoUtil.encodeTimestamp(value));
			return;
		} else if (column.isNumeric()) {
			if (value.length() <= 18) {
				try {
					stmt.setLong(index, Long.parseLong(value));
				} catch (NumberFormatException e) {
					stmt.setString(index, value);
				}
			} else {
				stmt.setString(index, value);
			}
			return;
		} else if (column.isNString()) {
			stmt.setNString(index, value);
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("Add Log SQL> NSbind %d = [%s]", index, value));
			}
		} else {
			stmt.setString(index, value);
			return;
		}
	}
	
	/**
	 * 绑定字符串
	 * @param stmt
	 * @param source
	 * @param columnName
	 * @param value
	 * @param index
	 * @throws SQLException
	 */
	private static void bindString(Connection conn, PreparedStatement stmt, IData source, String columnName, String value, int index) throws SQLException {
		if (conn instanceof OracleConnection) {
			OracleConnection oraConn = (OracleConnection) conn;
			
			String dataSourceName = oraConn.getName();
			String isNStringEnable = System.getProperty(TableMetaData.DATASOURCE_NSTRING + dataSourceName + "." + columnName, "false");
			if ("true".equals(isNStringEnable)) {
				stmt.setNString(index, value);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> NSbind %d = [%s]", index, value));
				}
			} else {
				stmt.setString(index, value);
				if (log.isDebugEnabled()) {
					log.debug(String.format("SQL> bind %d = [%s]", index, value));
				}
			}
		} else {
			stmt.setString(index, value);
			if (log.isDebugEnabled()) {
				log.debug(String.format("SQL> bind %d = [%s]", index, value));
			}
		}
	}
	
	
	public static void main(String[] args) {
		//String sql = "select count(1) from sys_adm_acct where status = :STATUS and A = :A group by status";
		String sql = "iv_serial_number   varchar2(20):=:SERIAL_NUMBER iv_trade_type_code NUMBER(4):=:TRADE_TYPE_CODE";
		Object[] obj = parseColonSql(sql);
		System.out.println(obj[0]);
		System.out.println(obj[1]);
	}
}
