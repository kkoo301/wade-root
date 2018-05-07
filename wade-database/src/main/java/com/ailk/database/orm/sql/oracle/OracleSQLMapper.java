/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.sql.oracle;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.cache.BOColumn;
import com.ailk.database.orm.err.BOError;
import com.ailk.database.orm.err.BOException;
import com.ailk.database.orm.sql.CURDSql;
import com.ailk.database.orm.sql.ISQLMapper;
import com.ailk.database.orm.util.BOUtil;

/**
 * @description
 * 针对Oracle方言的单表的SQL生成器
 */
public class OracleSQLMapper implements ISQLMapper {
	
	/**
	 * SQL缓存
	 */
	private static Map<Class<? extends BOEntity>, CURDSql> curd = new HashMap<Class<? extends BOEntity>, CURDSql>(10000);

	@Override
	public String count(BOEntity entity) throws BOException {
		Class<? extends BOEntity> clazz = entity.getClass();
		
		CURDSql sql = curd.get(clazz);
		if (null == sql) {
			sql = mapper(entity);
		}
		
		return sql.getCount();
	}
	
	/**
	 * 映射增删改查及Count的SQL
	 * @param entity
	 * @return
	 * @throws BOException
	 */
	private CURDSql mapper(BOEntity entity) throws BOException {
		Class<? extends BOEntity> clazz = entity.getClass();
		synchronized (clazz) {
			CURDSql sql = new CURDSql();
			
			String tableName = BOUtil.getTableName(entity);
			
			StringBuilder selectRowIdSQL = new StringBuilder("SELECT ROWID, ");
			StringBuilder selectSQL = new StringBuilder("SELECT ");
			StringBuilder updateSQL = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
			StringBuilder deleteSQL = new StringBuilder("DELETE FROM ").append(tableName).append(" ");
			StringBuilder insertSQL1 = new StringBuilder("INSERT INTO ").append(tableName).append(" ( ");
			StringBuilder insertSQL2 = new StringBuilder(" ) VALUES ( ");
			StringBuilder countSQL = new StringBuilder("SELECT COUNT(1) FROM ");
			
			Map<String, BOColumn> columns = BOUtil.getColumns(clazz);
			Iterator<String> iter = columns.keySet().iterator();
			while (iter.hasNext()) {
				String colName = iter.next();
				
				// 不处理rowid
				BOColumn bc = columns.get(colName);
				if (bc.isRowId()) {
					continue;
				}
				
				int type = bc.getType();
				if (Types.DATE == type || Types.TIME == type || Types.TIMESTAMP == type) {
					selectRowIdSQL.append("TO_CHAR(").append(colName).append(", 'yyyy-MM-dd HH24:mi:ss') AS ").append(colName);
					selectSQL.append("TO_CHAR(").append(colName).append(", 'yyyy-MM-dd HH24:mi:ss') AS ").append(colName);
				} else {
					selectRowIdSQL.append(colName);
					selectSQL.append(colName);
				}
				
				updateSQL.append(colName).append(" = ? ");
				insertSQL1.append(colName);
				insertSQL2.append("?");
				
				if (iter.hasNext()) {
					selectRowIdSQL.append(",");
					selectSQL.append(",");
					updateSQL.append(",");
					insertSQL1.append(",");
					insertSQL2.append(",");
				}
			}
			
			int len = selectRowIdSQL.length();
			if (selectRowIdSQL.charAt(len - 1) == ',') {
				selectRowIdSQL.deleteCharAt(len - 1);
			}
			
			len = selectSQL.length();
			if (selectSQL.charAt(len - 1) == ',') {
				selectSQL.deleteCharAt(len - 1);
			}
			
			len = insertSQL1.length();
			if (insertSQL1.charAt(len - 1) == ',') {
				insertSQL1.deleteCharAt(len - 1);
			}
			
			len = insertSQL2.length();
			if (insertSQL2.charAt(len - 1) == ',') {
				insertSQL2.deleteCharAt(len - 1);
			}
			
			sql.setSelectRowId(selectRowIdSQL.append(" FROM ").append(tableName).toString());
			sql.setSelect(selectSQL.append(" FROM ").append(tableName).toString());
			sql.setUpdate(updateSQL.toString());
			sql.setInsert(insertSQL1.append(insertSQL2).append(")").toString());
			sql.setDelete(deleteSQL.toString());
			sql.setCount(countSQL.append(tableName).toString());
			/*sql.setPageSQL("select * from (select row_.*, rownum rownum_ from (%s) row_ where rownum <= ?) where rownum_ > ?");
			sql.setSequenceSQL("select %s.nextval from dual connect by level <= %d" );
			sql.setSystimeSQL("select to_char(systimestamp, 'yyyy-MM-dd HH24:mi:ss.ff') from dual");*/
			
			return sql;
		}
	}
	

	@Override
	public StringBuilder pagin(String sql) {
		return new StringBuilder(200)
				.append("SELECT * FROM (SELECT ROW_.*, ROWNUM ROWNUM_ FROM (")
				.append(sql)
				.append(") ROW_ WHERE ROWNUM <= ?) WHERE ROWNUM_ > ?");
	}

	@Override
	public String select(BOEntity entity, String[] columns, boolean isNeedRowId) throws SQLException {
		try {
			if (null == columns || columns.length == 0) {
				Class<? extends BOEntity> clazz = entity.getClass();
				
				CURDSql sql = curd.get(clazz);
				if (null == sql) {
					sql = mapper(entity);
				}
				
				return sql.getSelect(isNeedRowId);
			} else {
				String table = BOUtil.getTableName(entity);
				StringBuilder select = new StringBuilder(100).append("SELECT ");
				
				if (isNeedRowId) {
					select.append(" ROWID,");
				}
				
				Map<String, BOColumn> bcs = BOUtil.getColumns(entity.getClass());
				for (int i = 0, len = columns.length; i < len; i++) {
					String columnName = columns[i];
					BOColumn bc = bcs.get(columnName);
					if (null != bc) {
						
						int type = bc.getType();
						if (Types.DATE == type || Types.TIME == type || Types.TIMESTAMP == type) {
							select.append("TO_CHAR(").append(columnName).append(", 'yyyy-MM-dd HH24:mi:ss') AS ").append(columnName);
						} else {
							select.append(columnName);
						}
						
						if (i + 1 < len) {
							select.append(",");
						}
					} else {
						throw new BOException(BOError.bo10011.getCode(), BOError.bo10011.getInfo(entity.getClass().getName(), columnName));
					}
				}
				
				int len = select.length();
				if (select.charAt(len - 1) == ',') {
					select.deleteCharAt(len - 1);
				}
				
				return select.append(" FROM ").append(table).toString();
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
	}

	@Override
	public String delete(BOEntity entity) throws BOException {
		Class<? extends BOEntity> clazz = entity.getClass();
		
		CURDSql sql = curd.get(clazz);
		if (null == sql) {
			sql = mapper(entity);
		}
		
		return sql.getDelete();
	}

	@Override
	public String update(BOEntity entity) throws BOException {
		Class<? extends BOEntity> clazz = entity.getClass();
		
		CURDSql sql = curd.get(clazz);
		if (null == sql) {
			sql = mapper(entity);
		}
		
		return sql.getUpdate();
	}

	
	@Override
	public String insert(BOEntity entity) throws BOException {
		Class<? extends BOEntity> clazz = entity.getClass();
		
		CURDSql sql = curd.get(clazz);
		if (null == sql) {
			sql = mapper(entity);
		}
		
		return sql.getInsert();
	}
}
