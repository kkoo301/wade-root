/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.data.impl.Pagination;
import com.ailk.common.logger.AbstractLogger;
import com.ailk.common.logger.ILogger;
import com.ailk.common.trace.AbstractTracer;
import com.ailk.database.dao.util.UpdateSQL;
import com.ailk.database.dao.util.OrmDaoUtil;
import com.ailk.database.jdbc.IResultSetReader;
import com.ailk.database.orm.bo.BOContainer;
import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.cache.BOColumn;
import com.ailk.database.orm.err.BOError;
import com.ailk.database.orm.err.BOException;
import com.ailk.database.orm.sql.ISQLAppender;
import com.ailk.database.orm.sql.ISQLMapper;
import com.ailk.database.orm.sql.SQLAppenderFactory;
import com.ailk.database.orm.sql.SQLMapperFactory;
import com.ailk.database.orm.util.BOUtil;
import com.ailk.database.sequence.SequenceFactory;

/**
 * @description 强类型的单表操作的DAO逻辑封装
 */
public class BaseEntityDAO<T extends BOEntity> extends BaseDAO {

	private static final Logger log = LoggerFactory.getLogger(BaseEntityDAO.class);
	
	private ILogger logger = null;
	private Object logObject = null;
	
	public BaseEntityDAO() {
		this(null);
	}

	public BaseEntityDAO(String dataSourceName) {
		this.dataSourceName = dataSourceName;
		
		setLogger(AbstractLogger.getLogger(getClass()), this);
		setTracer(AbstractTracer.getTracer(getClass()));
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
	
	/**
	 * 设置日志对象
	 */
	public void setLogger(ILogger logger, Object logObject) {
		setLogger(logger);
		setLogObject(logObject);
	}
	
	/**
	 * 设置数据源名称
	 * @param dataSourceName the dataSourceName to set
	 */
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	/**
	 * 获取序列，序列命名规则：TABLE_NAME$SEQ
	 * @return
	 */
	public long getNewId(Class<? extends BOContainer> clazz) throws SQLException {
		try {
			String seqName = BOUtil.getTableName(clazz) + "$SEQ";
			return Long.parseLong(SequenceFactory.nextval(getDataSourceName(), seqName));
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	/**
	 * 根据表名生成序列，序列命名规则：TABLE_NAME$SEQ
	 * @param tableName
	 * @return
	 */
	public long getNewId(String tableName) throws SQLException {
		try {
			String seqName = tableName + "$SEQ";
			return Long.parseLong(SequenceFactory.nextval(getDataSourceName(), seqName));
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据主键创建BO对象
	 * 
	 * @param clazz
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public T createEntityByPrimary(Class<T> clazz, Object... values) throws SQLException {
		T t = null;

		try {
			t = BOUtil.createByPrimary(clazz, values);
		} catch (BOException e) {
			throw new SQLException(e);
		}

		return t;
	}

	
	/**
	 * 按主键查询
	 * @param entity
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public T query(T entity, Object... parameter) throws SQLException {
		return query(entity, null, parameter);
	}

	/**
	 * 按主键查询，从上下文获取数据库连接，并执行查询操作，
	 * @param entity
	 * @param cols
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public T query(T entity, String[] cols, Object... parameter) throws SQLException {
		// 验证是否有表名
		try {
			String tableName = BOUtil.getTableName(entity);
			if (null == tableName || tableName.isEmpty()) {
				throw new BOException(BOError.bo10015.getCode(), BOError.bo10015.getInfo(entity.getClass()));
			}
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
		
		T bo = null;
		StringBuilder sql = new StringBuilder(100);
		long start = System.currentTimeMillis();
		long cost = 0L;
		try {
			// 拼查询语句
			sql.append(SQLMapperFactory.getMapper(entity).select(entity, cols, true));
			
			// 拼查询条件
			ISQLAppender<T> appender = SQLAppenderFactory.create(entity);
			String[] primary = BOUtil.getPrimary(entity.getClass());
			for (int i = 0, size = primary.length; i < size; i++) {
				if (i == 0) {
					appender.where(primary[i]).equal().bind(primary[i]);
				} else {
					appender.and(primary[i]).equal().bind(primary[i]);
				}
			}
			sql.append(appender.getSQL());
			log.debug("SQL>" + sql.toString());
			
			// 取连接对象
			Connection conn = getDataBaseConnection();

			PreparedStatement stmt = conn.prepareStatement(sql.toString());

			// 变量绑定
			List<String> binds = appender.getBinds();
			for (int i = 0, size = binds.size(); i < size; i++) {
				String column = binds.get(i);
				OrmDaoUtil.bind(stmt, entity, i + 1, column, parameter[i]);
			}

			// 执行SQL查询
			ResultSet rs = stmt.executeQuery();
			
			//　读结果集，并生成BO对象
			bo = (T) BOUtil.create(entity.getClass(), null);
			Map<String, BOColumn> columns = BOUtil.getColumns(entity.getClass());
			String rowId = null;
			boolean specCols = null != cols && cols.length > 0;
			while (rs.next()) {
				// if 处理指定查询字段的场景
				if (specCols) {
					for (int i = 0, len = cols.length; i < len; i++) {
						BOColumn bc = columns.get(cols[i]);
						bo.initProperty(cols[i], OrmDaoUtil.getRSValueByBOType(bc, rs));
					}
				} else {
					for (Map.Entry<String, BOColumn> item : columns.entrySet()) {
						
						BOColumn bc = item.getValue();
						if (bc.isRowId()) {
							continue;
						}
						
						bo.initProperty(item.getKey(), OrmDaoUtil.getRSValueByBOType(item.getValue(), rs));
					}
					rowId = new String(rs.getRowId("ROWID").getBytes());
				}
			}
			
			// 能查询到数据则设置RowId
			if (bo.isEmpty()) {
				bo.setRowId(rowId);
			}
			
			bo.backup(true);
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(Arrays.toString(parameter));
			log.error(err.toString());
			
			throw e;
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		} finally {
			cost = System.currentTimeMillis() - start;
			if (null != logger && null != logObject)
				logger.log(logObject, "query", start, cost, null);
		}

		return bo;
	}
	
	/**
	 * 
	 * @param cols
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int count(ISQLAppender<T> appender) throws SQLException {
		StringBuilder sql = new StringBuilder(100);
		long start = System.currentTimeMillis();
		long cost = 0L;
		try {
			T entity = appender.getEntity();
			
			// 拼查询语句
			sql.append(SQLMapperFactory.getMapper(entity).count(entity));
			sql.append(appender.getSQL());
			log.debug("SQL>" + sql.toString());
			
			// 取连接对象
			Connection conn = getDataBaseConnection();

			PreparedStatement stmt = conn.prepareStatement(sql.toString());

			// 变量绑定
			List<String> binds = appender.getBinds();
			Map<String, Object> parameter = appender.getParameter();
			for (int i = 0, size = binds.size(); i < size; i++) {
				String column = binds.get(i);
				OrmDaoUtil.bind(stmt, entity, i + 1, column, parameter.get(column));
			}

			// 执行SQL查询
			ResultSet rs = stmt.executeQuery();
			
			//　读结果集，并生成BO对象
			int count = 0;
			while (rs.next()) {
				count = rs.getInt(1);
			}
			
			return count;
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：COUNT:");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - start);
			err.append("\nPARAMS:").append(appender.getBinds());
			log.error(err.toString());

			throw e;
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		} finally {
			cost = System.currentTimeMillis() - start;
			if (null != logger && null != logObject)
				logger.log(logObject, "count", start, cost, null);
		}
	}

	/**
	 * 按主键查询，需要特殊处理日期类型
	 * 
	 * @param conn
	 * @param appender
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<T> query(String[] cols, ISQLAppender<T> appender, int start, int end) throws SQLException {
		List<T> array = new ArrayList<T>();
		
		boolean pagin = (start < end);
		
		StringBuilder sql = new StringBuilder(100);
		long startTime = System.currentTimeMillis();
		long cost = 0L;
		try {
			// 拼查询语句
			T entity = appender.getEntity();
			ISQLMapper mapper = SQLMapperFactory.getMapper(entity);
			sql.append(mapper.select(entity, cols, false));
			
			// 拼查询条件
			sql.append(appender.getSQL());
			
			// 拼分页语句
			if (pagin) {
				sql = mapper.pagin(sql.toString());
			}
			
			log.debug("SQL>" + sql.toString());
			
			// 取连接对象
			Connection conn = getDataBaseConnection();

			PreparedStatement stmt = conn.prepareStatement(sql.toString());

			// 变量绑定
			List<String> binds = appender.getBinds();
			Map<String, Object> parameter = appender.getParameter();
			int size = binds.size();
			for (int i = 0; i < size; i++) {
				String column = binds.get(i);
				OrmDaoUtil.bind(stmt, entity, i + 1, column, parameter.get(column));
			}
			if (pagin) {
				log.debug("SQL> bind:{}, paing-end:{}", binds.size() + 1, end);
				stmt.setInt(size + 1, end);
				log.debug("SQL> bind:{}, paing-start:{}", binds.size() + 2, start);
				stmt.setInt(size + 2, start);
			}

			// 执行SQL查询并设置批量读取阈值
			ResultSet rs = stmt.executeQuery();
			rs.setFetchSize(200);
			
			//　读结果集，并生成BO对象
			T bo = (T) BOUtil.create(entity.getClass(), null);
			Map<String, BOColumn> columns = BOUtil.getColumns(entity.getClass());
			boolean specCols = null != cols && cols.length > 0;
			
			while (rs.next()) {
				// if 处理指定查询字段的场景
				if (specCols) {
					for (int i = 0, len = cols.length; i < len; i++) {
						BOColumn bc = columns.get(cols[i]);
						bo.initProperty(cols[i], OrmDaoUtil.getRSValueByBOType(bc, rs));
					}
				} else {
					for (Map.Entry<String, BOColumn> item : columns.entrySet()) {
						
						BOColumn bc = item.getValue();
						if (bc.isRowId()) {
							continue;
						}
						
						bo.initProperty(item.getKey(), OrmDaoUtil.getRSValueByBOType(item.getValue(), rs));
					}
				}
				// 设置备份值
				bo.backup(true);
				array.add(bo);
			}
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - startTime);
			err.append("\nPARAMS:").append(appender.getBinds());
			log.error(err.toString());
			
			throw e;
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		} finally {
			cost = System.currentTimeMillis() - start;
			if (null != logger && null != logObject)
				logger.log(logObject, "query", start, cost, null);
		}
		
		return array;
	}
	
	/**
	 * 按主键单表修改
	 * @param entity
	 * @throws SQLException
	 */
	public void save(T entity) throws SQLException {
		save(entity, null);
	}

	/**
	 * 单表保存，根据状态控制对物理数据做增删改操作，若无主键可通过columns来指定条件字段，状态说明如下：<br>
	 * 1.新增：isNew()为true；<br>
	 * 2.删除：isDelete()为true；<br>
	 * 3.修改：其它
	 * @param entity
	 * @param columns
	 * @throws SQLException
	 */
	public void save(T entity, String[] columns) throws SQLException {
		save(entity, columns, false);
	}
	
	/**
	 * 单表保存，根据状态控制对物理数据做增删改操作，若无主键可通过columns来指定条件字段，状态说明如下：<br>
	 * 1.新增：isNew()为true；<br>
	 * 2.删除：isDelete()为true；<br>
	 * 3.修改：其它
	 * @param entity
	 * @param columns
	 * @param needUpdateKey	是否需要修改主键
	 * @throws SQLException
	 */
	public void save(T entity, String[] columns, boolean needUpdateKey) throws SQLException {
		if (entity.isNew()) {
			insert(entity);
			return ;
		}
		
		if (entity.isDelete()) {
			delete(entity, columns);
			return ;
		}
		
		update(entity, columns, needUpdateKey);
		return ;
	}
	
	/**
	 * 按主键单表保存
	 * @param entities
	 * @throws SQLException
	 */
	public void save(T[] entities) throws SQLException {
		save(entities, null);
	}
	
	/**
	 * 遍历修改
	 * @param entities
	 * @param columns
	 * @throws SQLException
	 */
	public void save(T[] entities, String[] columns) throws SQLException {
		save(entities, columns, false);
	}
	
	/**
	 * 
	 * @param entities
	 * @param columns
	 * @param needUpdateKey
	 * @throws SQLException
	 */
	public void save(T[] entities, String[] columns, boolean needUpdateKey) throws SQLException {
		for (T entity : entities) {
			save(entity, columns, needUpdateKey);
		}
	}
	
	/**
	 * 按主键的批量保存
	 * @param entities
	 * @throws SQLException
	 */
	public void saveBatch(T[] entities) throws SQLException {
		saveBatch(entities, null);
	}
	
	/**
	 * 批量操作，可通过columns指定条件字段
	 * @param entities
	 * @param columns
	 * @throws Exception
	 */
	public void saveBatch(T[] entities, String[] columns) throws SQLException {
		saveBatch(entities, columns, false);
	}
	
	/**
	 * 批量操作，可通过columns指定条件字段
	 * @param entities
	 * @param columns
	 * @param needUpdateKey	是否需要修改主机
	 * @throws SQLException
	 */
	public void saveBatch(T[] entities, String[] columns, boolean needUpdateKey) throws SQLException {
		Connection conn = null;
		String delSQL = null;
		PreparedStatement delStmt = null;
		
		String insSQL = null;
		PreparedStatement insStmt = null;
		
		String updSQL = null;
		List<String> updBinds = null;
		PreparedStatement updStmt = null;
		
		long startTime = System.currentTimeMillis();
		long cost = 0L;
		try {
			for (T entity : entities) {
				if (entity.isDelete()) {
					if (null == delSQL) {
						delSQL = OrmDaoUtil.createDeleteSQL(entity, columns);
					}
					log.debug("Batch SQL>" + delSQL);
					
					if (null == conn) {
						conn = getDataBaseConnection();
					}
					
					if (null == delStmt) {
						delStmt = conn.prepareStatement(delSQL);
					}
					
					OrmDaoUtil.bindDeleteStmt(entity, delStmt, entity.backup(false), columns);
					delStmt.addBatch();
					
					continue;
				} else if (entity.isNew()) {
					if (null == insSQL) {
						insSQL = OrmDaoUtil.createInsertSQL(entity);
					}
					log.debug("Batch SQL>" + insSQL);
					
					if (null == conn) {
						conn = getDataBaseConnection();
					}

					if (null == insStmt) {
						insStmt = conn.prepareStatement(insSQL);
					}

					OrmDaoUtil.bindInsertStmt(entity, insStmt);
					insStmt.addBatch();
					
					continue;
				} else {// Update
					if (null == updSQL) {
						UpdateSQL usql = OrmDaoUtil.createUpdateSQL(entity, columns, needUpdateKey);
						if (null == usql) {
							continue;
						}
						updSQL = usql.getSql();
						updBinds = usql.getColumns();
					}
					log.debug("Batch SQL>" + updSQL);
					
					if (null == conn) {
						conn = getDataBaseConnection();
					}

					if (null == updStmt) {
						updStmt = conn.prepareStatement(updSQL);
					}

					OrmDaoUtil.bindUpdateStmt(entity, updStmt, updBinds);
					updStmt.addBatch();
				}
			} //end for
			
			if (null != delStmt) {
				delStmt.executeBatch();
			}
			
			if (null != insStmt) {
				insStmt.executeBatch();
			}
			
			if (null != updStmt) {
				updStmt.executeBatch();
			}
		} catch (SQLException e) {
			if (null != conn)
				conn.rollback();
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(delSQL);
			err.append("@").append(insSQL);
			err.append("@").append(updSQL);
			err.append("|").append(System.currentTimeMillis() - startTime);
			log.error(err.toString());
			
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - startTime;
			if (null != logger && null != logObject)
				logger.log(logObject, "batch", startTime, cost, null);
		}
		
	}
	
	/**
	 * 数据新增
	 * @param entity
	 * @throws SQLException
	 */
	public void insert(T entity) throws SQLException {
		// 验证是否有表名
		try {
			String tableName = BOUtil.getTableName(entity);
			if (null == tableName || tableName.isEmpty()) {
				throw new BOException(BOError.bo10015.getCode(), BOError.bo10015.getInfo(entity.getClass()));
			}
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
				
		Connection conn = null;
		String sql = null;
		long startTime = System.currentTimeMillis();
		long cost = 0L;
		try {
			// 拼新增语句
			sql = OrmDaoUtil.createInsertSQL(entity);
			log.debug("SQL>" + sql.toString());
			
			// 取连接对象
			conn = getDataBaseConnection();

			PreparedStatement stmt = conn.prepareStatement(sql);

			// 变量绑定
			OrmDaoUtil.bindInsertStmt(entity, stmt);

			// 执行SQL
			stmt.executeUpdate();
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - startTime);
			err.append("\nPARAMS:").append(entity.toMap());
			log.error(err.toString());

			if (null != conn) {
				conn.rollback();
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - startTime;
			if (null != logger && null != logObject)
				logger.log(logObject, "insert", startTime, cost, null);
		}
	
	}
	
	/**
	 * 单表删除，若无主键可通过columns指定条件字段
	 * delete from table where key = back.get(key)
	 * @param entity
	 * @param columns	条件字段
	 * @throws SQLException
	 */
	public void delete(T entity, String[] columns) throws SQLException {
		// 验证是否有表名
		try {
			String tableName = BOUtil.getTableName(entity);
			if (null == tableName || tableName.isEmpty()) {
				throw new BOException(BOError.bo10015.getCode(), BOError.bo10015.getInfo(entity.getClass()));
			}
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
				
		Connection conn = null;
		String sql = null;
		long startTime = System.currentTimeMillis();
		long cost = 0L;
		try {
			// 拼删除语句
			sql = OrmDaoUtil.createDeleteSQL(entity, columns);
			
			// 取连接对象
			conn = getDataBaseConnection();

			PreparedStatement stmt = conn.prepareStatement(sql);

			// 变量绑定
			OrmDaoUtil.bindDeleteStmt(entity, stmt, entity.backup(false), columns);

			// 执行SQL
			stmt.executeUpdate();
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - startTime);
			err.append("\nPARAMS:").append(entity.toMap());
			log.error(err.toString());

			if (null != conn) {
				conn.rollback();
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - startTime;
			if (null != logger && null != logObject)
				logger.log(logObject, "delete", startTime, cost, null);
		}
	}
	
	/**
	 * 根据主键字段修改数据，若无主键可通过columns来指定条件字段
	 * @param entity
	 * @param columns
	 * @throws SQLException
	 */
	public void update(T entity, String[] columns) throws SQLException {
		update(entity, columns, false);
	}
	
	/**
	 * 根据主键字段修改数据，若无主键可通过columns来指定条件字段
	 * @param entity
	 * @param columns
	 * @param needUpdateKey	是否需要修改主键字段
	 * @throws SQLException
	 */
	public void update(T entity, String[] columns, boolean needUpdateKey) throws SQLException {
		// 验证是否有表名
		try {
			String tableName = BOUtil.getTableName(entity);
			if (null == tableName || tableName.isEmpty()) {
				throw new BOException(BOError.bo10015.getCode(), BOError.bo10015.getInfo(entity.getClass()));
			}
		} catch (BOException e) {
			throw new SQLException(e.getErrInfo());
		}
				
		Connection conn = null;
		String sql = null;
		long startTime = System.currentTimeMillis();
		long cost = 0L;
		try {
			// 拼修改语句
			UpdateSQL usql = OrmDaoUtil.createUpdateSQL(entity, columns, needUpdateKey);
			if (null == usql) {
				log.debug("BO数据未修改，不做SQL操作");
				return;
			}
			sql = usql.getSql();
			log.debug("SQL>" + sql);
			
			// 取连接对象
			conn = getDataBaseConnection();

			PreparedStatement stmt = conn.prepareStatement(sql.toString());

			// 变量绑定
			OrmDaoUtil.bindUpdateStmt(entity, stmt, usql.getColumns());

			// 执行SQL查询
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - startTime);
			err.append("\nPARAMS:").append(entity.toMap());
			log.error(err.toString());
			
			if (null != conn) {
				conn.rollback();
			}
			
			throw e;
		} finally {
			cost = System.currentTimeMillis() - startTime;
			if (null != logger && null != logObject)
				logger.log(logObject, "update", startTime, cost, null);
		}
	}
	
	/**
	 * 执行统计SQL
	 * @param sql
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public int executeCount(String sql, Map<String, String> parameter) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String realSQL = null;
		long startTime = System.currentTimeMillis();
		long cost = 0L;
		try {
			Object[] rtn = SQLAppenderFactory.parseColonSql(sql);
			realSQL = (String) rtn[0];
			List<String> binds = (List<String>) rtn[1];
			
			realSQL = "SELECT COUNT(1) FROM (" + realSQL + ")";
			
			log.debug("SQL> count :" + realSQL);
			conn = getDataBaseConnection();
			
			stmt = conn.prepareStatement(realSQL);
			for (int i = 0, size = binds.size(); i < size; i++) {
				String column = binds.get(i);
				String value = parameter.get(column);
				
				log.debug("SQL>bind:{}, col:{}, value:{}", new String[] {String.valueOf(i + 1), column, value});
				stmt.setString(i + 1, value);
			}
			
			rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
			
			return 0;
		} catch (SQLException e) {
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - startTime);
			err.append("\nPARAMS:").append(parameter);
			log.error(err.toString());
			
			throw e;
		} finally {
			if (null != rs) {
				rs.close();
			}
			if (null != stmt) {
				stmt.close();
			}
			
			cost = System.currentTimeMillis() - startTime;
			if (null != logger && null != logObject)
				logger.log(logObject, "count", startTime, cost, null);
		}
	}
	
	
	/**
	 * 通过自定义的读取对象存放查询结果集
	 * @param sql　通过:xxx来绑定的SQL语句
	 * @param parameter	匹配SQL里所有的变量，且都是按字符绑定，若需要转换类型需要在SQL里处理
	 * @param reader
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public <Rs, Row> Rs executeQuery(IResultSetReader<Rs, Row> reader, String sql, Map<String, String> parameter, Pagination pagin) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : executeCount(sql, parameter));
				return reader.getResultSet();
			}
			
			if (pagin.isNeedCount()) {
				count = executeCount(sql, parameter);
			} else {
				count = pagin.getCount();
			}
		}
		
		// 当Count数值为0时，不再执行查询语句，直接返回结果集
		if (isrange) {
			pagin.setCount(count);
			if (count == 0 && pagin.isNeedCount()) {
				return reader.getResultSet();
			}
		}
		
		String realSQL = null;
		long startTime = System.currentTimeMillis();
		long cost = 0L;
		try {
			Object[] rtn = SQLAppenderFactory.parseColonSql(sql);
			realSQL = (String) rtn[0];
			List<String> binds = (List<String>) rtn[1];
			
			if (isrange) {
				realSQL = "SELECT * FROM (SELECT ROW_.*, ROWNUM ROWNUM_ FROM (" + realSQL + ") ROW_ WHERE ROWNUM <= ?) WHERE ROWNUM_ > ?";
			}
			
			log.debug("SQL>" + realSQL);
			conn = getDataBaseConnection();
			
			stmt = conn.prepareStatement(realSQL);
			int size = binds.size();
			for (int i = 0; i < size; i++) {
				String column = binds.get(i);
				String value = parameter.get(column);
				
				log.debug("SQL>bind:{}, col:{}, value:{}", new String[] {String.valueOf(i + 1), column, value});
				stmt.setString(i + 1, value);
			}
			if (isrange) {
				log.debug("SQL>bind:{}, col:{end}, value:{}", new String[] {String.valueOf(size + 1), String.valueOf(pagin.getEnd())});
				stmt.setInt(size + 1, pagin.getEnd());
				log.debug("SQL>bind:{}, col:{start}, value:{}", new String[] {String.valueOf(size + 2), String.valueOf(pagin.getStart())});
				stmt.setInt(size + 2, pagin.getStart());
			}
			
			rs = stmt.executeQuery();
			rs.setFetchSize(isrange ? pagin.getFetchSize() : 1000);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			String[] columns = null;
			Row row = null;
			while (rs.next()) {
				row = reader.nextRow();
				if (null == columns) {
					int colsize = rsmd.getColumnCount();
					columns = new String[colsize];
					for (int i = 1; i <= colsize; i++) {
						String name = rsmd.getColumnLabel(i).toUpperCase(); // 数据库字段名统一约定为大写
						columns[i - 1] = name;
						if (reader.isRowId(name)) {
							reader.read(row, name, new String(rs.getRowId(name).getBytes()), -1);
						} else {
							reader.read(row, name, rs.getString(name), -1);
						}
					}
				} else {
					for (int i = 0, len = columns.length; i < len; i++) {
						String name = columns[i];
						if (reader.isRowId(name)) {
							reader.read(row, name, new String(rs.getRowId(name).getBytes()), -1);
						} else {
							reader.read(row, name, rs.getString(name), -1);
						}
					}
				}
				
				if (null != row) {
					reader.addRow(row);
				}
			}
			return reader.getResultSet();
		} catch (SQLException e) {
			
			StringBuilder err = new StringBuilder(300);
			err.append("SQL执行异常：");
			err.append(dataSourceName);
			err.append("@").append(sql);
			err.append("|").append(System.currentTimeMillis() - startTime);
			err.append("\nPARAMS:").append(parameter);
			log.error(err.toString());
			
			throw e;
		} finally {
			if (null != rs) {
				rs.close();
			}
			if (null != stmt) {
				stmt.close();
			}
			
			cost = System.currentTimeMillis() - startTime;
			if (null != logger && null != logObject)
				logger.log(logObject, "query", startTime, cost, null);
		}
	}
	
}
