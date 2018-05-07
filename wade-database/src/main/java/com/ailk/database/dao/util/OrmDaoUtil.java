/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月25日
 * 
 * Just Do IT.
 */
package com.ailk.database.dao.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.bo.DataType;
import com.ailk.database.orm.cache.BOColumn;
import com.ailk.database.orm.err.BOException;
import com.ailk.database.orm.sql.ISQLAppender;
import com.ailk.database.orm.sql.SQLAppenderFactory;
import com.ailk.database.orm.sql.SQLMapperFactory;
import com.ailk.database.orm.util.BOUtil;

/**
 * @description
 * DAO工具类
 */
public final class OrmDaoUtil {
	
	private static final Logger log = LoggerFactory.getLogger(OrmDaoUtil.class);
	
	/**
	 * 获取增量更新的SQL对象
	 * @param entity
	 * @param columns
	 * @param needUpdateKey	是否需要修改主键字段
	 * @return
	 * @throws SQLException
	 */
	public static <T extends BOEntity> UpdateSQL createUpdateSQL(T entity, String[] columns, boolean needUpdateKey) throws SQLException {
		//如果没有修改任何字段，则返回NULL
		Set<String> changeColumns = entity.getChangedProperties();
		if (null == changeColumns || changeColumns.isEmpty()) {
			return null;
		}
		
		UpdateSQL usql = new UpdateSQL();
		
		StringBuilder sql = new StringBuilder(100);
		List<String> binds = new ArrayList<String>();
		try {
			String table = BOUtil.getTableName(entity);
			Map<String, BOColumn> bcs = BOUtil.getColumns(entity.getClass());
			
			sql.append("UPDATE ").append(table).append(" SET ");
			
			// 拼增量修改字段
			StringBuilder colSQL = new StringBuilder(100);
			for (String column : changeColumns) {
				BOColumn bc = bcs.get(column);
				// 过滤非表字段
				if (null == bc) {
					continue;
				}
				// 过滤ROWID字段
				if (bc.isRowId()) {
					continue;
				}
				
				// 不需要修改主键字段则过滤掉
				if (bc.isPrimary() && !needUpdateKey) {
					continue;
				}
				
				colSQL.append(column).append("= ?,");
				binds.add(column);
			}
			if (colSQL.length() == 0) {
				return null;
			}
			
			colSQL.deleteCharAt(colSQL.length() - 1);
			sql.append(colSQL.toString());
			
			// 拼主键字段
			if (null == columns) {
				String[] primary = BOUtil.getPrimary(entity.getClass());
				for (int i = 0, len = primary.length; i < len; i++) {
					binds.add(primary[i]);
					if (i == 0) {
						sql.append(" WHERE ").append(primary[i]).append("=?");
					} else {
						sql.append(" AND ").append(primary[i]).append("=?");
					}
				}
			} else {
				for (int i = 0, len = columns.length; i < len; i++) {
					binds.add(columns[i]);
					if (i == 0) {
						sql.append(" WHERE ").append(columns[i]).append("=?");
					} else {
						sql.append(" AND ").append(columns[i]).append("=?");
					}
				}
			}
			
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo(), e);
		}
		
		usql.setSql(sql.toString());
		usql.setColumns(binds);
		return usql;
	}
	
	/**
	 * 绑定增量修改语句的字段
	 * @param entity
	 * @param stmt
	 * @throws SQLException
	 */
	public static <T extends BOEntity> void bindUpdateStmt(T entity, PreparedStatement stmt, List<String> binds) throws SQLException {
		try {
			Map<String, Object> backup = entity.backup(false);
			Map<String, Object> current = entity.toMap();
			Map<String, BOColumn> columns = BOUtil.getColumns(entity.getClass());
			
			int primarys = BOUtil.getPrimary(entity.getClass()).length;
			for (int i = 0, size = binds.size(); i < size; i++) {
				String column = binds.get(i);
				
				if (i + primarys < size) {
					
					Object value = current.get(column);
					if (null == value) {
						BOColumn bc = columns.get(column);
						if (null != bc) {
							value = bc.getDefval();
						}
					}
					
					OrmDaoUtil.bind(stmt, entity, i + 1, column, value);
				} else {
					//优先获取前数据获取，当前数据为NULL时再从备份数据获取
					Object value = current.get(column);
					if (null == value) {
						value = backup.get(column);
					}
					
					//当值为NULL时最后判断是否有默认值
					if (null == value) {
						BOColumn bc = columns.get(column);
						if (null != bc) {
							value = bc.getDefval();
						}
					}
					
					OrmDaoUtil.bind(stmt, entity, i + 1, column, value);
				}
			}
			
		} catch (SQLException e) {
			throw e;
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
	}
	
	/**
	 * Update SQL
	 * @param entity
	 * @return
	 * @throws SQLException
	 */
	public static <T extends BOEntity> String createFullUpdateSQL(T entity) throws SQLException {
		try {
			StringBuilder sql = new StringBuilder(100);
			sql.append(SQLMapperFactory.getMapper(entity).update(entity));
			
			// 按主键拼条件语句，并将新拼的变量保存在appendKeys里
			Map<String, Object> current = entity.toMap();
			
			ISQLAppender<T> appender = SQLAppenderFactory.create(entity);
			appender.setParameter(current);
			
			boolean existsWhere = false;
			String[] primary = BOUtil.getPrimary(entity.getClass());
			for (String key : primary) {
				if (!existsWhere) {
					appender.where(key).equal().bind(key);
					existsWhere = true;
				} else {
					appender.and(key).equal().bind(key);
				}
			}
			sql.append(appender.getSQL());
						
			return sql.toString();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * 绑定Update语句的参数
	 * @param entity
	 * @param stmt
	 * @throws SQLException
	 */
	public static <T extends BOEntity> void bindFullUpdateStmt(T entity, PreparedStatement stmt) throws SQLException {
		try {
			Map<String, Object> backup = entity.backup(false);
			
			Map<String, BOColumn> columns = BOUtil.getColumns(entity.getClass());
			Map<String, Object> current = entity.toMap();
			int index = 0;
			for (Map.Entry<String, BOColumn> item : columns.entrySet()) {
				BOColumn bc = item.getValue();
				if (bc.isRowId()) {
					continue;
				}
				String column = item.getKey();
				index ++;
				
				Object value = current.get(column);
				if (null == value) {
					if (null != bc) {
						value = bc.getDefval();
					}
				}
				
				OrmDaoUtil.bind(stmt, entity, index, column, value);
			}
			
			String[] primary = BOUtil.getPrimary(entity.getClass());
			for (int i = 0, len = primary.length; i < len; i++) {
				String column = primary[i];
				
				Object value = backup.get(column);
				if (null == value) {
					BOColumn bc = columns.get(column);
					if (null != bc) {
						value = bc.getDefval();
					}
				}
				
				OrmDaoUtil.bind(stmt, entity, i + index + 1, column, value);
			}
		} catch (SQLException e) {
			throw e;
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
	}
	
	
	/**
	 * Insert SQL
	 * @param entity
	 * @return
	 * @throws SQLException
	 */
	public static <T extends BOEntity> String createInsertSQL(T entity) throws SQLException {
		StringBuilder sql = new StringBuilder(100);
		try {
			sql.append(SQLMapperFactory.getMapper(entity).insert(entity));
			return sql.toString();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Bind Insert
	 * @param entity
	 * @param stmt
	 * @param backup
	 * @throws SQLException
	 */
	public static <T extends BOEntity> void bindInsertStmt(T entity, PreparedStatement stmt) throws SQLException {
		try {
			Map<String, BOColumn> columns = BOUtil.getColumns(entity.getClass());
			Map<String, Object> data = entity.toMap();
			int index = 0;
			for (Map.Entry<String, BOColumn> item : columns.entrySet()) {
				BOColumn bc = item.getValue();
				if (bc.isRowId()) {
					continue;
				}
				
				String column = item.getKey();
				Object value = data.get(column);
				if (null == value) {
					value = bc.getDefval();
				}
				
				OrmDaoUtil.bind(stmt, entity, index + 1, column, value);
				index ++;
			}
		} catch (SQLException e) {
			throw e;
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
	}
	
	/**
	 * 生成单表的Delete的SQL，columns为指定的条件字段，为空是按主键来生成条件字段
	 * @param entity
	 * @param columns
	 * @return
	 * @throws SQLException
	 */
	public static <T extends BOEntity> String createDeleteSQL(T entity, String[] columns) throws SQLException {
		try {
			StringBuilder sql = new StringBuilder(100);
			sql.append(SQLMapperFactory.getMapper(entity).delete(entity));
			
			// 按主键拼条件语句，并将新拼的变量保存在appendKeys里
			Map<String, Object> current = entity.toMap();
			
			ISQLAppender<T> appender = SQLAppenderFactory.create(entity);
			appender.setParameter(current);
			
			boolean existsWhere = false;
			
			if (null == columns) {
				String[] primary = BOUtil.getPrimary(entity.getClass());
				for (String key : primary) {
					if (!existsWhere) {
						appender.where(key).equal().bind(key);
						existsWhere = true;
					} else {
						appender.and(key).equal().bind(key);
					}
				}
			} else {
				for (String key : columns) {
					if (!existsWhere) {
						appender.where(key).equal().bind(key);
						existsWhere = true;
					} else {
						appender.and(key).equal().bind(key);
					}
				}
			}
			sql.append(appender.getSQL());
			
			log.debug("SQL>" + sql.toString());
			
			return sql.toString();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Delete的变量绑定
	 * @param entity
	 * @param appendKeys
	 * @param stmt
	 * @param backup
	 * @param columns
	 * @throws SQLException
	 */
	public static <T extends BOEntity> void bindDeleteStmt(T entity, PreparedStatement stmt, Map<String, Object> backup, String[] columns) throws SQLException {
		
		try {
			// 按主键绑定变量
			Map<String, BOColumn> bos = BOUtil.getColumns(entity.getClass());
			if (null == columns) {
				String[] primary = BOUtil.getPrimary(entity.getClass());
				for (int i = 0, len = primary.length; i < len; i++) {
					String column = primary[i];
					
					Object value = backup.get(column);
					if (null == value) {
						BOColumn bo = bos.get(column);
						if (null != bo) {
							value = bo.getDefval();
						}
					}
					
					bind(stmt, entity, i + 1, column, value);
				}
			} else {
				for (int i = 0, len = columns.length; i < len; i++) {
					String column = columns[i];
					
					Object value = backup.get(column);
					if (null == value) {
						BOColumn bo = bos.get(column);
						if (null != bo) {
							value = bo.getDefval();
						}
					}
					
					bind(stmt, entity, i + 1, column, value);
				}
			}
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
		
	}

	/**
	 * 从ResultSet里读取数据，并转换数据类型，转换原则：<br>
	 * 1.Number类型，长度<=9位转成int,　<=18位转成long，其它转成字符串操作<br>
	 * 2.日期类型（Date&Timestamp)转换成String<br>
	 * 3.布尔类型（Boolean)不做转换<br>
	 * 4.ROWID转换成字符串处理<br>
	 * 5.其它类型默认转字符串<br>
	 * @param column
	 * @param rs
	 * @return
	 * @throws BOException
	 */
	public static Object getRSValueByBOType(BOColumn column, ResultSet rs) throws SQLException {
		int type = column.getType();
		int length = column.getLength();
		String key = column.getName();
		
		try {
			switch (type) {
			case Types.ROWID :
				return new String(rs.getRowId("ROWID").getBytes());
			case Types.NUMERIC :
				if (length <= 9) {
					return rs.getInt(key);
				}
				if (length <= 18) {
					return rs.getLong(key);
				}
				return rs.getString(key);
			case Types.DATE :
				return rs.getString(key);
			case Types.TIME :
				return rs.getString(key);
			case Types.TIMESTAMP :
				return rs.getString(key);
			case Types.VARCHAR :
				return rs.getString(key);
			case Types.NVARCHAR :
				return rs.getString(key);
			default :
				return rs.getString(key);
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage() + ":" + key, e);
		}
	}
	
	/**
	 * 参数绑定
	 * 
	 * @param stmt
	 * @param entity
	 * @param index
	 * @param column
	 * @param value
	 * @throws SQLException
	 */
	public static <T extends BOEntity> void bind(PreparedStatement stmt, T entity, int index, String column, Object value) throws SQLException {
		Class<? extends BOEntity> clazz = entity.getClass();
		
		String table = null;
		
		try {
			table = BOUtil.getTableName(entity);
			int type = BOUtil.getColumnType(clazz, column);
			int length = BOUtil.getColumnLength(clazz, column);
			
			if (!BOUtil.existsColumn(clazz, column)) {
				type = -99999;
			}
			
			switch (type) {
			case -1:
				throw new SQLException("不支持的数据类型:" + table + "." + column);
			case -99999:
				debugBind(index, column, value, "java.lang.String", length);
				stmt.setString(index, null == value ? "" : value.toString());
				break;
			case Types.VARCHAR:
				debugBind(index, column, value, "Types.VARCHAR", length);
				stmt.setString(index, null == value ? "" : value.toString());
				break;
			case Types.NVARCHAR://处理生僻字入库乱码问题
				debugBind(index, column, value, "Types.NVARCHAR", length);
				stmt.setNString(index, null == value ? "" : value.toString());
				break;
			case Types.DATE:
				debugBind(index, column, value, "Types.DATE[TIMESTAMP]", length);
				
				if (value instanceof String) {
					String datestr = (String) value;
					if (datestr.length() == 0) {
						stmt.setTimestamp(index, null);
					} else {
						java.sql.Date date = DataType.strToSqlDate(datestr);
						stmt.setTimestamp(index, null == value ? null : new Timestamp(date.getTime()));
					}
				} else {
					stmt.setTimestamp(index, null == value ? null : new Timestamp(((java.sql.Date) value).getTime()));
				}
				
				break;
			case Types.TIMESTAMP:
				debugBind(index, column, value, "Types.TIMESTAMP", length);
				if (value instanceof String) {
					String datestr = (String) value;
					if (datestr.length() == 0) {
						stmt.setTimestamp(index, null);
					} else {
						java.sql.Date date = DataType.strToSqlDate((String) value);
						stmt.setTimestamp(index, null == value ? null : new Timestamp(date.getTime()));
					}
				} else {
					stmt.setTimestamp(index, null == value ? null : (Timestamp)value);
				}
				break;
			case Types.TIME:
				debugBind(index, column, value, "Types.TIME", length);
				if (value instanceof String) {
					String datestr = (String) value;
					if (datestr.length() == 0) {
						stmt.setTimestamp(index, null);
					} else {
						java.sql.Date date = DataType.strToSqlDate((String) value);
						stmt.setTimestamp(index, null == value ? null : new Timestamp(date.getTime()));
					}
				} else {
					stmt.setTimestamp(index, null == value ? null : new Timestamp(((java.sql.Time) value).getTime()));
				}
				break;
			default:
				debugBind(index, column, value, "Types.String", length);
				stmt.setObject(index, value);
				break;
			}
		} catch (SQLException e) {
			throw e;
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
		
	}
	
	/**
	 * 参数绑定调试日志
	 * @param index
	 * @param column
	 * @param value
	 * @param type
	 * @param length
	 */
	 static void debugBind(int index, String column, Object value, String type, int length) {
		log.debug("SQL> bind:{}, col:{}, value:{}, type:{}, length:{}", new String[] {String.valueOf(index), column, String.valueOf(value), type, String.valueOf(length)});
	}
}
