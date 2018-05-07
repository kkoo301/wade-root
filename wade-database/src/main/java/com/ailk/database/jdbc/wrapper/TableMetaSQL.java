/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.wrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ailk.database.object.TableMetaObject;
import com.ailk.database.util.DaoUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 根据表结构和数据创建PreparedStatement对象
 * 
 * @className: TableMetaSQL.java
 * @author: liaosheng
 * @date: 2014-4-12
 */
public final class TableMetaSQL {
	
	/**
	 * 获取查询系统时间的SQL
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public static final String getCurrentTimeSQL(TableMetaObject table) throws SQLException {
		return table.getSystimeSQL();
	}
	
	/**
	 * 获取序列的SQL
	 * @param table
	 * @param seqName
	 * @param increment
	 * @return
	 * @throws SQLException
	 */
	public static final String getSequenceSQL(TableMetaObject table, String seqName, int increment) throws SQLException {
		return table.createSequenceSQL(seqName, increment);
	}
	
	/**
	 * 创建单表的Count语句
	 * @param table
	 * @param keys
	 * @return
	 */
	public static final String createCountSQL(TableMetaObject table, String[] keys) {
		String tableName = table.getTableName();
		
		if (null == keys)
			throw new IllegalArgumentException(String.format("单表[%s]COUNT的条件字段不能为null", tableName));
		
		int cnt = keys.length;
		if (cnt == 0)
			throw new IllegalArgumentException(String.format("单表[%s]COUNT的条件字段不能为空数组", tableName));
		
		StringBuilder sql = new StringBuilder("select count(1) from ").append(table.getTableName()).append(" a ");
		
		for (int i = 0; i < cnt; i++) {
			String key = keys[i];
			
			if (null == key || key.length() <= 0) 
				throw new IllegalArgumentException(String.format("单表[%s]COUNT的条件字段名[%s]不能为空", tableName, Arrays.toString(keys)));
			
			if (i == 0) {
				sql.append(" where ").append(key).append(" = ? ");
			} else {
				sql.append(" and ").append(key).append(" = ? ");
			}
		}
		return sql.toString();
	}
	
	/**
	 * 创建分页语句
	 * @param table
	 * @param sql
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String createPaginationSQL(TableMetaObject table, String sql, int begin, int end) {
		return table.createPageSQL(sql, begin, end);
	}
	
	/**
	 * 创建单表分页查询语句
	 * @param table
	 * @param keys
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String createQueryTableSQL(TableMetaObject table, String[] keys, int begin, int end) {
		String tableName = table.getTableName();
		int cnt = null == keys ? 0 : keys.length;
		
		if (begin < 0 || end < 0 || end < begin)
			throw new IllegalArgumentException(String.format("单表[%s]查询起[%d]始[%d]游标错误", 
					tableName, begin, end));
		
		StringBuilder sql = new StringBuilder(table.getSelectSQL());
		
		for (int i = 0; i < cnt; i++) {
			String key = keys[i];
			
			if (null == key || key.length() <= 0) 
				throw new IllegalArgumentException(String.format("单表[%s]查询的条件字段名[%s]不能为空", tableName, Arrays.toString(keys)));
			
			if (i == 0) {
				sql.append(" where ").append(key).append(" = ? ");
			} else {
				sql.append(" and ").append(key).append(" = ? ");
			}
		}
		
		if (begin == 0 && end == 0) {
			return sql.toString();
		} else {
			return table.createPageSQL(sql.toString(), begin, end);
		}
	}
	
	/**
	 * 创建单表查询语句
	 * @param table
	 * @param keys
	 * @return
	 */
	public static final String createQuerySQL(TableMetaObject table, String[] keys) {
		String tableName = table.getTableName();
		if (null == keys)
			keys = table.getKeys();
		
		int cnt = keys.length;
		if (cnt == 0)
			throw new IllegalArgumentException(String.format("单表[%s]查询的主键字段不能为空", tableName));
		
		StringBuilder sql = new StringBuilder(table.getSelectRowIdSQL());
		
		boolean hasPrimKey = cnt > 0;
		for (int i = 0; i < cnt; i++) {
			String key = keys[i];
			
			if (null == key || key.length() <= 0) 
				throw new IllegalArgumentException(String.format("单表[%s]查询的条件字段名[%s]不能为空", tableName, Arrays.toString(keys)));
			
			if (i == 0) {
				sql.append(" where ").append(key).append(" = ? ");
			} else {
				sql.append(" and ").append(key).append(" = ? ");
			}
		}
		
		if (!hasPrimKey)
			throw new IllegalArgumentException(String.format("单表[%s]查询条件字段[%s]未包含主键[%s]", 
					tableName, 
					Arrays.toString(keys),
					Arrays.toString(table.getKeys())));
			
		return sql.toString();
	}
	
	/**
	 * 创建单表删除语句
	 * @param table
	 * @param keys
	 * @return
	 */
	public static String createDeleteSQL(TableMetaObject table, String[] keys) {
		String tableName = table.getTableName();
		if (null == keys)
			throw new IllegalArgumentException(String.format("单表[%s]删除的主键字段不能为null", tableName));
		
		int cnt = keys.length;
		if (cnt == 0)
			throw new IllegalArgumentException(String.format("单表[%s]删除的主键字段不能为空数组", tableName));
		
		StringBuilder sql = new StringBuilder(table.getDeleteSQL());
		
		boolean hasPrimKey = cnt > 0;
		for (int i = 0; i < cnt; i++) {
			String key = keys[i];
			
			if (null == key || key.length() <= 0) 
				throw new IllegalArgumentException(String.format("单表[%s]删除的条件字段名[%s]不能为空", tableName, Arrays.toString(keys)));
			
			if (i == 0) {
				sql.append(" where ").append(key).append(" = ? ");
			} else {
				sql.append(" and ").append(key).append(" = ? ");
			}
		}
		
		if (!hasPrimKey)
			throw new IllegalArgumentException(String.format("单表[%s]删除条件字段[%s]未包含主键[%s]", 
					tableName, 
					Arrays.toString(keys),
					Arrays.toString(table.getKeys())));
		
		return sql.toString();
	}
	
	/**
	 * 创建单表修改语句
	 * @param table
	 * @param cols
	 * @param keys
	 * @return
	 */
	public static String createUpdateSQL(TableMetaObject table, String[] cols, String[] keys) {
		String tableName = table.getTableName();
		
		if (null == cols)
			throw new IllegalArgumentException(String.format("单表[%s]修改的主键字段不能为null", tableName));
		
		int colsLen = cols.length;
		if (colsLen == 0)
			throw new IllegalArgumentException(String.format("单表[%s]修改的主键字段不能为空数组", tableName));
		
		if (null == keys)
			throw new IllegalArgumentException(String.format("单表[%s]修改的主键字段不能为null", tableName));
		
		int keysLen = keys.length;
		if (colsLen == 0)
			throw new IllegalArgumentException(String.format("单表[%s]修改的主键字段不能为空数组", tableName));
		
		StringBuilder sql = new StringBuilder("update ").append(tableName).append(" set ");
		
		for (int i = 0; i < colsLen; i++) {
			if (i + 1 != colsLen) {
				sql.append(cols[i]).append(" = ?, ");
			} else {
				sql.append(cols[i]).append(" = ? ");
			}
		}
		
		boolean hasPrimKey = keysLen > 0 ;
		for (int i = 0; i < keysLen; i++) {
			String key = keys[i];
			
			if (null == key || key.length() <= 0) 
				throw new IllegalArgumentException(String.format("单表[%s]修改的条件字段名[%s]不能为空", tableName, Arrays.toString(keys)));
			
			if (i == 0) {
				sql.append(" where ").append(key).append(" = ? ");
			} else {
				sql.append(" and ").append(key).append(" = ? ");
			}
		}
		
		if (!hasPrimKey)
			throw new IllegalArgumentException(String.format("单表[%s]修改条件字段[%s]未包含主键[%s]", 
					tableName, 
					Arrays.toString(keys),
					Arrays.toString(table.getKeys())));
		
		return sql.toString();
	}
	
	/**
	 * 创建全表修改语句
	 * @param table
	 * @param keys
	 * @return
	 */
	public static String createSaveSQL(TableMetaObject table, String[] keys) {
		String tableName = table.getTableName();
		if (null == keys)
			throw new IllegalArgumentException(String.format("单表[%s]保存的主键字段不能为null", tableName));
		
		int cnt = keys.length;
		if (cnt == 0)
			throw new IllegalArgumentException(String.format("单表[%s]保存的主键字段不能为空数组", tableName));
		
		StringBuilder sql = new StringBuilder(table.getUpdateSQL());
		
		boolean hasPrimKey = cnt > 0 ;
		for (int i = 0; i < cnt; i++) {
			String key = keys[i];
			
			if (null == key || key.length() <= 0) 
				throw new IllegalArgumentException(String.format("单表[%s]保存的条件字段名[%s]不能为空", tableName, Arrays.toString(keys)));
			
			if (i == 0) {
				sql.append(" where ").append(key).append(" = ? ");
			} else {
				sql.append(" and ").append(key).append(" = ? ");
			}
		}
		
		if (!hasPrimKey)
			throw new IllegalArgumentException(String.format("单表[%s]保存条件字段[%s]未包含主键[%s]", 
					tableName, 
					Arrays.toString(keys),
					Arrays.toString(table.getKeys())));
			
		return sql.toString();
	}
	
	/**
	 * 创建单表新增语句
	 * @param table
	 * @return
	 */
	public static final String createInsertSQL(TableMetaObject table) {
		return table.getInsertSQL();
	}
	
	/**
	 * 解析:xxx的语句，返回数组{SQL语句，XXXList集合}
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
}
