package com.ailk.database.dao.impl;

import java.io.Serializable;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.logger.AbstractLogger;
import com.ailk.common.logger.ILogger;
import com.ailk.common.trace.ITracer;
import com.ailk.database.dao.DAOSessionManager;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.jdbc.IResultSetReader;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaStatement;
import com.ailk.database.object.TableMetaObject;
import com.ailk.database.sequence.SequenceFactory;
import com.ailk.database.statement.Parameter;
import com.ailk.database.util.DaoHelper;
import com.ailk.database.util.DaoUtil;
import com.ailk.database.util.SQLParser;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: BaseDAO
 * @description: 基类DAO
 * 
 * @version: v1.0.0
 * @author: $Id: BaseDAO.java 10812 2017-05-18 17:25:16Z liaos $
 * @date: 2013-7-20
 */
public class BaseDAO extends AbstractDAO {

	private static transient Logger log = Logger.getLogger(BaseDAO.class);
	
	/**
	 * 全局事务管理集成的DAO，所有的连接对象通过当前DAO的dataSourceName创建
	 */
	private GeneralDAO gendao = new GeneralDAO();
	
	/**
	 * 指定连接的DAO，适用于独立事务的场景
	 */
	private TableDAO tabdao = new TableDAO();
	
	
	/**
	 * @param logger the logger to set
	 */
	public void setLogger(ILogger logger, Object logObject) {
		gendao.setLogger(logger);
		gendao.setLogObject(logObject);
		tabdao.setLogger(logger);
		tabdao.setLogObject(logObject);
	}
	
	public void setTracer(ITracer tracer) {
		gendao.setTracer(tracer);
		tabdao.setTracer(tracer);
	}
	
	/**
	 * 初始化
	 */
	public void initial(String dataSourceName) {
		if (null == dataSourceName || dataSourceName.length() <= 0) {
			throw new IllegalArgumentException("数据库连接名不能为空!");
		}
		this.dataSourceName = dataSourceName;
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("在数据源%s创建DAO", dataSourceName));
		}
	}
	
	/**
	 * 获取会话级数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
	protected DBConnection getDataBaseConnection() throws SQLException {
		if (null == dataSourceName || dataSourceName.length() <= 0) {
			throw new SQLException("数据库连接名不能为空!");
		}
		
		try {
			return (DBConnection) DAOSessionManager.getManager().getSession().getConnection(dataSourceName);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * 统计SQL,若SystemCfg.isReleaseDBConn == true 将自动关闭连接
	 * @param sql xxx = ? 的变量绑定语句
	 * @param param 参数
	 * @return
	 * @throws Exception
	 */
	public int getCount(String sql, Parameter param) throws Exception {
		return gendao.count(dataSourceName, sql, param.getValues());
	}
	
	public int getCount(String sql, String[] values) throws Exception {
		return gendao.count(dataSourceName, sql, values);
	}
	
	/**
	 * 统计SQL,若SystemCfg.isReleaseDBConn == true 将自动关闭连接
	 * @param colonSql "xxx=:xxx" 的变量绑定语句
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public int getCount(String colonSql, IData source) throws Exception {
		return gendao.count(dataSourceName, colonSql, source);
	}
	
	public int getCount(String sql) throws Exception {
		return gendao.count(dataSourceName, sql, new DataMap());
	}
	
	/**
	 * 已废除的方法，存在SQL注入的风险
	 * 
	 * @param sql
	 * @return int
	 * @throws Exception
	 */
	@Deprecated
	public int executeUpdate(String sql) throws Exception {
		throw new Exception("dao.executeUpdate(sql) 方法已作废!");
	}
	
	
	/**
	 * 执行带事务的SQL
	 * @parma conn 指定的数据库连接，多用于独立事务场景
	 * @param sql　带事务的SQL
	 * @param param　SQL绑定的变量值
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(DBConnection conn, String sql, Object[] param) throws Exception {
		if (null == param)
			throw new Exception("参数不能为null");
		
		int cnt = param.length;
		String[] values = new String[cnt];
		
		for (int i = 0; i < cnt; i++) {
			Object value = param[i];
    		if (null == value) {
    			values[i] = "";
    		} else {
    			if (value instanceof StringReader) {
    				values[i] = ((StringReader) value).toString();
    			} else {
    				values[i] = (String) value;
    			}
    		}
		}
		
		return tabdao.executeUpdate(conn, sql, values);
	}

	/**
	 * execute update
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(String sql, Object[] param) throws Exception {
		if (null == param)
			throw new Exception("参数不能为null");
		
		int cnt = param.length;
		String[] values = new String[cnt];
		
		for (int i = 0; i < cnt; i++) {
			Object value = param[i];
    		if (null == value) {
    			values[i] = "";
    		} else {
    			if (value instanceof StringReader) {
    				values[i] = ((StringReader) value).toString();
    			} else {
    				values[i] = (String) value;
    			}
    		}
		}
		
		return gendao.executeUpdate(dataSourceName, sql, values);
	}
	
	
	/**
	 * execute update
	 * 
	 * @param conn
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(DBConnection conn, String sql, Parameter param) throws Exception {
		long start = System.currentTimeMillis();

		try {
			return tabdao.executeUpdate(conn, sql, param.getValues());
		} catch (Exception e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
			sendLog(start, null);
		}
	}
	
	/**
	 * execute update
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(String sql, Parameter param) throws Exception {
		return gendao.executeUpdate(dataSourceName, sql, param.getValues());
	}
	
	/**
	 * execute update
	 * 
	 * @param sql
	 * @param param
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(String sql, IData param) throws Exception {
		return gendao.executeUpdate(dataSourceName, sql, param);
	}
	
	public int executeUpdate(DBConnection conn, String sql, IData param) throws Exception {
		return tabdao.executeUpdate(conn, sql, param);
	}
	
	public int[] executeBatch(DBConnection conn, String[] sqls) throws Exception {
		return tabdao.executeBatch(conn, sqls, 0);
	}
	
	public int[] executeBatch(String[] sqls) throws Exception {
		return gendao.executeBatch(dataSourceName, sqls, 0);
	}
	
	public int[] executeBatch(DBConnection conn, String sql, Parameter[] params) throws Exception {
		if (null == params)
			throw new NullPointerException("批量SQL执行异常，参数不能为空");
		
		int cnt = params.length;
		String[][] values = new String[cnt][];

		for (int i = 0; i < cnt; i++) {
			values[i] = params[i].getValues();
		}
		
		return tabdao.executeBatch(conn, sql, values, 0);
	}
	
	public int[] executeBatch(String colonSql, IDataset source, int batchsize) throws Exception {
		if (null == source) {
			source = new DatasetList();
		}
		
		return gendao.executeBatch(dataSourceName, colonSql, source, batchsize);
	}
	
	public int[] executeBatch(DBConnection conn, String colonSql, IDataset source) throws Exception {
		if (null == source) {
			source = new DatasetList();
		}
		
		return tabdao.executeBatch(conn, colonSql, source, 0);
	}
	
	public int[] executeBatch(DBConnection conn, String colonSql, IDataset source, int batchsize) throws Exception {
		if (null == source) {
			source = new DatasetList();
		}
		
		return tabdao.executeBatch(conn, colonSql, source, batchsize);
	}
	
	public int[] executeBatch(DBConnection conn, String sql, Parameter[] params, int batchsize) throws Exception {
		if (null == params) {
			params = new Parameter[0];
		}
		
		int cnt = params.length;
		String[][] values = new String[cnt][];

		for (int i = 0; i < cnt; i++) {
			values[i] = params[i].getValues();
		}
		
		return tabdao.executeBatch(conn, sql, values, batchsize);
	}
	
	public int[] executeBatch(String sql, Parameter[] params) throws Exception {
		if (null == params)
			throw new NullPointerException("批量SQL执行异常，参数不能为空");
		
		int cnt = params.length;
		String[][] values = new String[cnt][];

		for (int i = 0; i < cnt; i++) {
			values[i] = params[i].getValues();
		}
		
		return gendao.executeBatch(dataSourceName, sql, values, 0);
	}
	
	public int[] executeBatch(String sql, IDataset source) throws Exception {
		return executeBatch(sql, source, 0);
	}
	
	/**
	 * get sys date
	 * 
	 * @param conn
	 * @return String
	 * @throws Exception
	 */
	public String getSysDate(DBConnection conn) throws Exception {
		Timestamp time = tabdao.getCurrentTime(conn, getDefaultTableMetaObject(conn.getName()));
		return time == null ? null : DaoUtil.decodeTimestamp("yyyy-MM-dd", time);
	}
	
	/**
	 * get sys date
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getSysDate() throws Exception {
		Timestamp time = gendao.getCurrentTime(dataSourceName);
		return time == null ? null : DaoUtil.decodeTimestamp("yyyy-MM-dd", time);
	}

	/**
	 * get sys time
	 * 
	 * @param conn
	 * @return String
	 * @throws Exception
	 */
	public String getSysTime(DBConnection conn) throws Exception {
		Timestamp time = getCurrentTime(conn);
		return time == null ? null : DaoUtil.decodeTimestamp("yyyy-MM-dd HH:mm:ss.SSS", time);
	}

	/**
	 * get sys time
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getSysTime() throws Exception {
		Timestamp time = gendao.getCurrentTime(dataSourceName);
		return time == null ? null : DaoUtil.decodeTimestamp("yyyy-MM-dd HH:mm:ss.SSS", time);
	}
	
	/**
	 * get current time
	 * 
	 * @param conn
	 * @return Timestamp
	 * @throws Exception
	 */
	public Timestamp getCurrentTime(DBConnection conn) throws Exception {
		return tabdao.getCurrentTime(conn, getDefaultTableMetaObject(conn.getName()));
	}

	/**
	 * get current time
	 * 
	 * @return Timestamp
	 * @throws Exception
	 */
	public Timestamp getCurrentTime() throws Exception {
		return gendao.getCurrentTime(dataSourceName);
	}
	
	
	/**
	 * 获取序列的下一个值
	 * 
	 * @param clazz 序列类
	 * @return
	 * @throws Exception
	 */
	public String getSequence(Class<?> clazz) throws Exception {
		return SequenceFactory.nextval(this.dataSourceName, clazz);
	}

	/**
	 * 获取序列的下一个值
	 * 
	 * @param clazz 序列类
	 * @param eparchyCode 地州编码
	 * @return
	 * @throws Exception
	 */
	public String getSequence(Class<?> clazz, String eparchyCode) throws Exception {
		return SequenceFactory.nextval(this.dataSourceName, clazz, eparchyCode);
	}

	/**
	 * 获取序列的下一个值
	 * 
	 * @param seqName 序列名
	 * @return
	 * @throws Exception
	 */
	public String getSequence(String seqName) throws Exception {
		return SequenceFactory.nextval(dataSourceName, seqName);
	}
	
	
	public boolean insert(DBConnection conn, String tableName, IData source) throws Exception {
		int rows = tabdao.insert(conn, getTableMetaObject(conn.getName(), tableName), source);
		return rows != 0;
	}
	
	
	public boolean insert(String tableName, IData source) throws Exception {
		int rows = gendao.insert(dataSourceName, tableName, source);
		return rows != 0;
	}
	
	public int[] insert(DBConnection conn, String tableName, IDataset source, int batchsize) throws Exception {
		return tabdao.insert(conn, getTableMetaObject(dataSourceName, tableName), source, batchsize);
	}
	
	public int[] insert(String tableName, IDataset source) throws Exception {
		return gendao.insert(dataSourceName, tableName, source, 0);
	}
	
	public int[] insert(String tableName, IDataset source, int batchsize) throws Exception {
		return gendao.insert(dataSourceName, tableName, source, batchsize);
	}
	
	
	public boolean update(DBConnection conn, String tableName, IData source) throws Exception {
		int rows = tabdao.updateByPK(conn, getTableMetaObject(conn.getName(), tableName), null, source, null, null);
		return rows != 0;
	}
	
	public boolean update(String tableName, IData source) throws Exception {
		int rows = gendao.updateByPK(dataSourceName, tableName, null, source, null, null);
		return rows != 0;
	}
	
	public boolean update(String tableName, IData source, String[] keys) throws Exception {
		int rows = gendao.updateByPK(dataSourceName, tableName, null, source, keys, null);
		return rows != 0;
	}
	
	public boolean update(String tableName, IData source, String[] cols, String[] keys, String values[]) throws Exception {
		int rows = gendao.updateByPK(dataSourceName, tableName, cols, source, keys, values);
		return rows != 0;
	}
	
	public boolean update(String tableName, IData source, String[] keys, String values[]) throws Exception {
		int rows = gendao.updateByPK(dataSourceName, tableName, null, source, keys, values);
		return rows != 0;
	}
	
	public int[] update(String tableName, IDataset source, int batchsize) throws Exception {
		return gendao.update(dataSourceName, tableName, source, batchsize);
	}
	
	public int[] update(String tableName, IDataset source) throws Exception {
		return gendao.update(dataSourceName, tableName, source, 0);
	}
	
	public int[] update(DBConnection conn, String tableName, IDataset source, String[] keys, int batchsize) throws Exception {
		return tabdao.update(conn, getTableMetaObject(conn.getName(), tableName), source, null, keys, batchsize);
	}
	
	public int[] update(DBConnection conn, String tableName, IDataset source, String[] columns, String[] keys, int batchsize) throws Exception {
		return tabdao.update(conn, getTableMetaObject(conn.getName(), tableName), source, columns, keys, batchsize);
	}
	
	public int[] update(String tableName, IDataset source, String[] keys) throws Exception {
		return gendao.update(dataSourceName, tableName, source, null, keys, 0);
	}
	
	public int[] update(String tableName, IDataset source, String[] keys, int batchsize) throws Exception {
		return gendao.update(dataSourceName, tableName, source, null, keys, batchsize);
	}
	
	public int[] update(String tableName, IDataset source, String[] columns, String[] keys) throws Exception {
		return gendao.update(dataSourceName, tableName, source, columns, keys, 0);
	}
	
	public int[] update(String tableName, IDataset source, String[] columns, String[] keys, int batchsize) throws Exception {
		return gendao.update(dataSourceName, tableName, source, columns, keys, 0);
	}
	
	public boolean delete(DBConnection conn, String tableName, IData source) throws Exception {
		int rows = tabdao.deleteByPK(conn, getTableMetaObject(conn.getName(), tableName), null, source);
		return rows != 0;
	}
	
	public boolean delete(String tableName, IData source) throws Exception {
		int rows = gendao.deleteByPK(dataSourceName, tableName, null, source);
		return rows != 0;
	}
	
	public boolean delete(String tableName, IData source, String[] keys) throws Exception {
		int rows = gendao.deleteByPK(dataSourceName, tableName, keys, source);
		return rows != 0;
	}
	
	public boolean delete(String tableName, String[] keys, String[] values) throws Exception {
		int rows = gendao.deleteByPK(dataSourceName, tableName, keys, values);
		return rows != 0;
	}
	
	public int[] delete(String tableName, IDataset source) throws Exception {
		return gendao.delete(dataSourceName, tableName, source, null, 0);
	}
	
	public int[] delete(String tableName, IDataset source, int batchsize) throws Exception {
		return gendao.delete(dataSourceName, tableName, source, null, batchsize);
	}
	
	public int[] delete(DBConnection conn, String tableName, IDataset source, String[] keys, int batchsize) throws Exception {
		return tabdao.delete(conn, getTableMetaObject(dataSourceName, tableName), source, keys, batchsize);
	}
	
	public int[] delete(String tableName, IDataset source, String[] keys) throws Exception {
		return gendao.delete(dataSourceName, tableName, source, keys, 0);
	}
	
	public int[] delete(String tableName, IDataset source, String[] keys, int batchsize) throws Exception {
		return gendao.delete(dataSourceName, tableName, source, keys, batchsize);
	}
	
	public boolean save(DBConnection conn, String tableName, IData source) throws Exception {
		int rows = tabdao.saveByPK(conn, getTableMetaObject(conn.getName(), tableName), source, null, null);
		return rows != 0;
	}
	
	
	public boolean save(String tableName, IData source) throws Exception {
		int rows = gendao.saveByPK(dataSourceName, tableName, source, null, null);
		return rows != 0;
	}
	
	public boolean save(DBConnection conn, String tableName, IData source, String[] keys) throws Exception {
		int rows = tabdao.saveByPK(conn, getTableMetaObject(conn.getName(), tableName), source, keys, null);
		return rows != 0;
	}
	
	public boolean save(String tableName, IData source, String[] keys) throws Exception {
		int rows = gendao.saveByPK(dataSourceName, tableName, source, keys, null);
		return rows != 0;
	}
	
	public boolean save(DBConnection conn, String tableName, IData source, String[] keys, String[] values) throws Exception {
		int rows = tabdao.saveByPK(conn, getTableMetaObject(dataSourceName, tableName), source, keys, values);
		return rows != 0;
	}
	
	public boolean save(String tableName, IData source, String[] keys, String[] values) throws Exception {
		int rows = gendao.saveByPK(dataSourceName, tableName, source, keys, values);
		return rows != 0;
	}
	
	public IData queryByPK(DBConnection conn, String tableName, IData source) throws Exception {
		return tabdao.queryByPK(conn, getTableMetaObject(conn.getName(), tableName), null, source);
	}
	
	public IData queryByPK(String tableName, IData source) throws Exception {
		return gendao.queryByPK(dataSourceName, tableName, null, source);
	}
	
	public IData queryByPK(DBConnection conn, String tableName, String[] keys, String[] values) throws Exception {
		return tabdao.queryByPK(conn, getTableMetaObject(conn.getName(), tableName), keys, values);
	}
	
	public IData queryByPK(String tableName, String[] keys, String[] values) throws Exception {
		return gendao.queryByPK(dataSourceName, tableName, keys, values);
	}
	
	public IData queryByPK(DBConnection conn, String tableName, IData source, String[] keys) throws Exception {
		return tabdao.queryByPK(conn, getTableMetaObject(conn.getName(), tableName), keys, source);
	}
	
	public IData queryByPK(String tableName, IData source, String[] keys) throws Exception {
		return gendao.queryByPK(dataSourceName, tableName, keys, source);
	}
	
	
	public IDataset queryList(DBConnection conn, String sql, int fetchsize) throws Exception {
		IDataset rows = tabdao.executeQuery(conn, getDefaultTableMetaObject(conn.getName()), sql, new DataMap(), fetchsize);
		if (null != rows) {
			return rows;
		}
		return new DatasetList();
	}
	
	public IDataset queryList(String sql) throws Exception {
		throw new Exception("dao.queryList(sql)由于存在变量绑定漏洞,该方法已作废");
	}
	
	public IDataset queryList(DBConnection conn, String sql, Parameter param, int fetchsize) throws Exception {
		if (null == param || param.size() == 0) {
			return queryList(conn, sql, fetchsize);
		}
		
		IDataset rows = tabdao.executeQuery(conn, getDefaultTableMetaObject(conn.getName()), sql, param.getValues(), 0, 0, fetchsize);
		if (null != rows) {
			return rows;
		}
		return new DatasetList();
	}
	
	
	public IDataset queryList(String sql, Parameter param) throws Exception {
		if (null == param || param.size() == 0) {
			return queryList(sql, new Object[] {});
		}
		
		IDataset rows = gendao.executeQuery(dataSourceName, sql, param.getValues());
		if (null != rows) {
			return rows;
		}
		return new DatasetList();
	}
	
	
	public IDataset queryList(DBConnection conn, String sql, IData source, int fetchsize) throws Exception {
		if (null == source || source.isEmpty()) {
			return queryList(conn, sql, fetchsize);
		}
		
		IDataset rows = tabdao.executeQuery(conn, getDefaultTableMetaObject(conn.getName()), sql, source, 0, 0, fetchsize);
		if (null != rows) {
			return rows;
		}
		
		return new DatasetList();
	}
	
	public IDataset queryList(String sql, IData source) throws Exception {
		if (null == source)
			source = new DataMap();
		
		IDataset rows = gendao.executeQuery(dataSourceName, sql, source, 2000);
		if (null != rows) {
			return rows;
		}
		return new DatasetList();
	}
	
	
	public IDataset queryList(DBConnection conn, String sql, Parameter param, Pagination pagin) throws Exception {
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : getCount(sql, param));
				return new DatasetList();
			}
			
			if (pagin.isNeedCount()) {
				count = getCount(sql, param);
			} else {
				count = pagin.getCount();
			}
		}
		
		// 当Count数值为0时，不再执行查询语句，直接返回结果集
		if (isrange) {
			if (count == 0 && pagin.isNeedCount()) {
				pagin.setCount(count);
				return new DatasetList();
			}
		}
		
		IDataset rows = new DatasetList();
		
		if (isrange) {
			rows = tabdao.executeQuery(conn, getDefaultTableMetaObject(conn.getName()), sql, param.getValues(), pagin.getStart(), pagin.getEnd(), pagin.getPageSize());
			pagin.setCount(count);
		} else {
			rows = tabdao.executeQuery(conn, getDefaultTableMetaObject(conn.getName()), sql, param.getValues(), 0, 0, 0);
		}
		
		if (null != rows) {
			return rows;
		}
		
		return new DatasetList();
	}
	
	
	public IDataset queryList(String sql, Parameter param, Pagination pagin) throws Exception {
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : getCount(sql, param));
				return new DatasetList();
			}
			
			if (pagin.isNeedCount()) {
				count = getCount(sql, param);
			} else {
				count = pagin.getCount();
			}
		}
		
		if (isrange) {
			if (count == 0 && pagin.isNeedCount()) {
				pagin.setCount(count);
				return new DatasetList();
			}
		}
		
		IDataset rows = new DatasetList();
		
		if (isrange) {
			rows = gendao.executeQuery(dataSourceName, sql, param.getValues(), pagin.getStart(), pagin.getEnd(), pagin.getPageSize());
			pagin.setCount(count);
		} else {
			rows = gendao.executeQuery(dataSourceName, sql, param.getValues(), 0, 0, 0);
		}
		
		if (null != rows) {
			return rows;
		}
		
		return new DatasetList();
	}
	
	public IDataset queryList(SQLParser parser) throws Exception {
		IDataset rows = gendao.executeQuery(dataSourceName, parser.getSQL(), new DataMap(parser.getParam()), 0);
		if (null != rows) {
			return rows;
		}
		return new DatasetList();
	}
	
	public IDataset queryList(SQLParser parser, Pagination pagin) throws Exception {
		if (null == parser) {
			throw new NullPointerException("SQL解析对象不能为空");
		}
		
		if (null == pagin) {
			return queryList(parser);
		}
		
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : getCount(parser.getSQL(), new DataMap(parser.getParam())));
				return new DatasetList();
			}
			
			if (pagin.isNeedCount()) {
				count = getCount(parser.getSQL(), new DataMap(parser.getParam()));
			} else {
				count = pagin.getCount();
			}
		}
		
		if (isrange) {
			if (count == 0 && pagin.isNeedCount()) {
				pagin.setCount(count);
				return new DatasetList();
			}
		}
		
		IDataset rows = new DatasetList();
		
		if (isrange) {
			rows = gendao.executeQuery(dataSourceName, parser.getSQL(), new DataMap(parser.getParam()), pagin.getStart(), pagin.getEnd(), pagin.getPageSize());
			pagin.setCount(count);
		} else {
			rows = gendao.executeQuery(dataSourceName, parser.getSQL(), new DataMap(parser.getParam()), 0, 0, 0);
		}
		
		if (null != rows) {
			return rows;
		}
		
		return new DatasetList();
	}
	
	public int executeUpdate(SQLParser parser) throws Exception {
		return gendao.executeUpdate(dataSourceName, parser.getSQL(), new DataMap(parser.getParam()));
	}
	
	/**
	 * 
	 * @param sql
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public IDataset queryList(String sql, Pagination pagin) throws Exception {
		return queryList(sql, new Object[] {}, pagin);
	}
	
	public IDataset queryList(String sql, Object[] param) throws Exception {
		if (null == param)
			return queryList(sql);
		
		int cnt = param.length;
		String[] values = new String[cnt];
		
		for (int i = 0; i < cnt; i++) {
			Object value = param[i];
    		if (null == value) {
    			values[i] = "";
    		} else {
    			if (value instanceof StringReader) {
    				values[i] = ((StringReader) value).toString();
    			} else {
    				values[i] = value.toString();
    			}
    		}
		}
		
		IDataset rows = gendao.executeQuery(dataSourceName, sql, values);
		if (null != rows) {
			return rows;
		}
		return new DatasetList();
	}
	
	
	public IDataset queryList(String sql, Object[] param, Pagination pagin) throws Exception {
		if (null == param)
			return queryList(sql);
		
		int cnt = param.length;
		String[] values = new String[cnt];
		
		for (int i = 0; i < cnt; i++) {
			Object value = param[i];
    		if (null == value) {
    			values[i] = "";
    		} else {
    			if (value instanceof StringReader) {
    				values[i] = ((StringReader) value).toString();
    			} else {
    				values[i] = (String) value;
    			}
    		}
		}
		
		
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : getCount(sql, values));
				return new DatasetList();
			}
			
			if (pagin.isNeedCount()) {
				count = getCount(sql, values);
			} else {
				count = pagin.getCount();
			}
		}
		
		if (isrange) {
			if (count == 0 && pagin.isNeedCount()) {
				pagin.setCount(count);
				return new DatasetList();
			}
		}
		
		IDataset rows = new DatasetList();
		
		if (isrange) {
			rows = gendao.executeQuery(dataSourceName, sql, values, pagin.getStart(), pagin.getEnd(), pagin.getPageSize());
			pagin.setCount(count);
		} else {
			rows = gendao.executeQuery(dataSourceName, sql, values, 0, 0, 0);
		}
		
		if (null != rows) {
			return rows;
		}
		
		return new DatasetList();
	}
	
	/**
	 * 单表查询
	 * @param conn
	 * @param tableName
	 * @param keys
	 * @param source
	 * @param pagin
	 * @return
	 * @throws Exception
	 */
	public IDataset queryTable(String dataSourceName, String tableName, String[] keys, IData source, Pagination pagin) throws Exception {
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		if (isrange) {
			if (pagin.isOnlyCount()) {
				long count = gendao.countTable(dataSourceName, tableName, keys, source);
				pagin.setCount(count);
				return new DatasetList();
			}
			
			if (pagin.isNeedCount()) {
				long count = gendao.countTable(dataSourceName, tableName, keys, source);
				pagin.setCount(count);
			}
			
			return gendao.queryTable(dataSourceName, tableName, keys, source, pagin.getStart(), pagin.getEnd());
		} else {
			return gendao.queryTable(dataSourceName, tableName, keys, source, 0, 0);
		}
	}
	
	
	public IDataset queryList(DBConnection conn, String sql, IData source, Pagination pagin) throws Exception {
		if (null == source || source.isEmpty()) {
			return queryList(conn, sql, pagin.getPageSize());
		}
		
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : getCount(sql, source));
				return new DatasetList();
			}
			
			if (pagin.isNeedCount()) {
				count = getCount(sql, source);
			} else {
				count = pagin.getCount();
			}
		}
		
		if (isrange) {
			pagin.setCount(count);
			if (count == 0 && pagin.isNeedCount()) {
				pagin.setCount(count);
				return new DatasetList();
			}
		}
		
		IDataset rows = new DatasetList();
		
		if (isrange) {
			rows = tabdao.executeQuery(conn, getDefaultTableMetaObject(conn.getName()), sql, source, pagin.getStart(), pagin.getEnd(), pagin.getPageSize());
			pagin.setCount(count);
		} else {
			rows = tabdao.executeQuery(conn, getDefaultTableMetaObject(conn.getName()), sql, source, 0, 0, 0);
		}
		
		if (null != rows) {
			return rows;
		}
		
		return new DatasetList();
	}
	
	public IDataset queryList(String sql, IData source, Pagination pagin) throws Exception {
		if (null == source || source.isEmpty()) {
			return queryList(sql, pagin);
		}
		
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : getCount(sql, source));
				return new DatasetList();
			}
			
			if (pagin.isNeedCount()) {
				count = getCount(sql, source);
			} else {
				count = pagin.getCount();
			}
		}
		
		if (isrange) {
			pagin.setCount(count);
			if (count == 0 && pagin.isNeedCount()) {
				pagin.setCount(count);
				return new DatasetList();
			}
		}
		
		IDataset rows = new DatasetList();
		
		if (isrange) {
			rows = gendao.executeQuery(dataSourceName, sql, source, pagin.getStart(), pagin.getEnd(), pagin.getPageSize());
			pagin.setCount(count);
		} else {
			rows = gendao.executeQuery(dataSourceName, sql, source, 0, 0, 0);
		}
		
		if (null != rows) {
			return rows;
		}
		
		return new DatasetList();
	}
	
	
	/**
	 * 通过自定义的读取对象存放查询结果集
	 * @param sql　通过:xxx来绑定的SQL语句
	 * @param parameter	匹配SQL里所有的变量，且都是按字符绑定，若需要转换类型需要在SQL里处理
	 * @param reader
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <Rs extends Serializable, Row extends Serializable> Rs queryList(IResultSetReader<Rs, Row> reader, String sql, IData parameter, Pagination pagin) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean isrange = pagin != null && pagin.getPageSize() > 0;
		long count = 0L;
		if (isrange) {
			if (pagin.isOnlyCount()) {
				pagin.setCount(pagin.getCount() > 0 ? pagin.getCount() : getCount(sql, parameter));
				return reader.getResultSet();
			}
			
			if (pagin.isNeedCount()) {
				count = getCount(sql, parameter);
			} else {
				count = pagin.getCount();
			}
		}
		if (isrange) {
			pagin.setCount(count);
			if (count == 0 && pagin.isNeedCount()) {
				pagin.setCount(count);
				return reader.getResultSet();
			}
		}
		
		try {
			Object[] rtn = TableMetaStatement.parseColonSql(sql);
			String realSQL = (String) rtn[0];
			List<String> binds = (List<String>) rtn[1];
			
			if (isrange) {
				realSQL = realSQL.replaceAll("ROWID,", "");
				realSQL = "SELECT * FROM (SELECT ROW_.*, ROWNUM ROWNUM_ FROM (" + realSQL + ") ROW_ WHERE ROWNUM <= ?) WHERE ROWNUM_ > ?";
			}
			
			log.debug("SQL>" + realSQL);
			conn = getDataBaseConnection();
			
			stmt = conn.prepareStatement(realSQL);
			int size = binds.size();
			for (int i = 0; i < size; i++) {
				String column = binds.get(i);
				String value = parameter.getString(column);
				
				log.debug(String.format("SQL>bind:%s, col:%s, value:%s", new Object[] {String.valueOf(i + 1), column, value}));
				stmt.setString(i + 1, value);
			}
			if (isrange) {
				log.debug(String.format("SQL>bind:%s, col:end, value:%s", new Object[] {String.valueOf(size + 1), String.valueOf(pagin.getEnd())}));
				stmt.setInt(size + 1, pagin.getEnd());
				log.debug(String.format("SQL>bind:%s, col:start, value:%s", new Object[] {String.valueOf(size + 2), String.valueOf(pagin.getStart())}));
				stmt.setInt(size + 2, pagin.getStart());
			}
			
			rs = stmt.executeQuery();
			rs.setFetchSize(1000);
			
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
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != rs) {
				rs.close();
			}
			if (null != stmt) {
				stmt.close();
			}
		}
	}
	
	
	
	public Object callFunc(DBConnection conn, String name, String[] paramNames, IData params, int returnType) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			Object obj = DaoHelper.callFunc(conn, name, paramNames, params, returnType);
			
			return obj;
		} catch (Exception e) {
			throw e;
		} finally {
			
			sendLog(start, null);
			
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
		}
	}
	
	public Object callFunc(String name, String[] paramNames, IData params, int returnType) throws Exception {
		return callFunc(getDataBaseConnection(), name, paramNames, params, returnType);
	}
	
	
	/**
	 * send log
	 * 
	 * @param start
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void sendLog(long start, String subkey) {
		ILogger logger = AbstractLogger.getLogger(getClass());
		
		if (null == logger) 
			return ;
			
		logger.log(this, subkey, start, (System.currentTimeMillis() - start), null);
	}
	
	
	public void callProc(DBConnection conn, String name, String[] paramNames, IData params) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			DaoHelper.callProc(conn, name, paramNames, params);
		} catch (Exception e) {
			throw e;
		} finally {
			sendLog(start, null);
			
			if (log.isDebugEnabled()) {
				log.debug("SQL execute cosetime :" + (System.currentTimeMillis() - start));
			}
		}
	}
	
	
	/**
	 * call proc
	 * 
	 * @param name
	 * @param paramNames
	 * @param params
	 * @throws Exception
	 */
	public void callProc(String name, String[] paramNames, IData params) throws Exception {
		callProc(getDataBaseConnection(), name, paramNames, params);
	}
	
	
	/**
	 * 获取数据源
	 * @param dataSourceName
	 * @return
	 */
	private DataSourceWrapper getDataSource(String dataSourceName) {
		return ConnectionManagerFactory.getConnectionManager().getDataSource(dataSourceName);
	}
	
	/**
	 * 获取表结构
	 * @param dataSourceName
	 * @param tableName
	 * @return
	 */
	private TableMetaObject getTableMetaObject(String dataSourceName, String tableName) throws Exception {
		return getDataSource(dataSourceName).getTableMetaData().getTableMetaObject(tableName);
	}
	
	private TableMetaObject getDefaultTableMetaObject(String dataSourceName) throws Exception {
		return getDataSource(dataSourceName).getTableMetaData().getDefaultTableMetaObject();
	}
}