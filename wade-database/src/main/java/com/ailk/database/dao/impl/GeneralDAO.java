/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.dao.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.logger.ILogger;
import com.ailk.common.trace.ITracer;
import com.ailk.database.dao.DAOSessionManager;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.jdbc.wrapper.ConnectionWrapper;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.QuerySQLParser;
import com.ailk.database.object.TableMetaObject;

/**
 * 单表的增删改查功能
 * 
 * @className: GeneralDAO.java
 * @author: liaosheng
 * @date: 2014-3-27
 */
public class GeneralDAO {
	
	private TableDAO dao = new TableDAO();
	
	public GeneralDAO() {
	}
	
	/**
	 * @param logger the logger to set
	 */
	public void setLogger(ILogger logger) {
		dao.setLogger(logger);
	}
	
	public void setTracer(ITracer tracer) {
		dao.setTracer(tracer);
	}
	
	/**
	 * @param logObject the logObject to set
	 */
	public void setLogObject(Object logObject) {
		dao.setLogObject(logObject);
	}
	
	public Timestamp getCurrentTime(String dataSourceName) throws SQLException {
		return dao.getCurrentTime(getConnection(dataSourceName), getDefaultTableMetaObject(dataSourceName));
	}
	
	public String getSequence(String dataSourceName, String seqName) throws SQLException {
		return getSequence(dataSourceName, seqName, 1);
	}
	
	/**
	 * 获取序列
	 * @param dataSourceName 数据源名称
	 * @param seqName 序列名称
	 * @param increment 增长值
	 * @return
	 * @throws SQLException
	 */
	public String getSequence(String dataSourceName, String seqName, int increment) throws SQLException {
		if (null == seqName)
			throw new IllegalArgumentException(String.format("DAO[%s]获取序列异常，序列名不能为null", getClass().getName()));
		
		if (increment <= 0)
			increment = 1;
		
		return dao.getSequence(getConnection(dataSourceName), getDefaultTableMetaObject(dataSourceName), seqName, increment);
	}
	
	public int count(String dataSourceName, String colonSql, IData source) throws SQLException {
		return dao.count(getConnection(dataSourceName), getDefaultTableMetaObject(dataSourceName), colonSql, source);
	}
	
	public int count(String dataSourceName, String sql, String[] values) throws SQLException {
		return dao.count(getConnection(dataSourceName), getDefaultTableMetaObject(dataSourceName), sql, values);
	}
	
	/**
	 * 统计单表查询
	 * @param dataSourceName 数据源名称
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return
	 */
	public long countTable(String dataSourceName, String tableName, String[] keys, String values[]) throws SQLException {
		if (null == keys || null == values)
			throw new IllegalArgumentException(String.format("DAO[%s]单表查询异常，条件字段和字段值不能为null", getClass().getName()));
		
		int cnt = keys.length;
		if (0 == cnt || values.length != cnt)
			throw new IllegalArgumentException(String.format("DAO[%s]单表查询异常，条件字段[%s]和字段值[%s]不能为空", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		return dao.countTable(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, values);
	}
	
	
	/**
	 * 统计意表查询
	 * @param dataSourceName 数据源名称
	 * @param tableName 表名
	 * @param keys 条件字段名
	 * @param source 条件字段名和值的键值对
	 * @return
	 */
	public long countTable(String dataSourceName, String tableName, String[] keys, IData source) throws SQLException {
		if (null == keys)
			keys = new String[]{};
		
		if (null == source)
			source = new DataMap();
		
		return dao.countTable(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, source);
	}
	
	
	/**
	 * 执行DML语句
	 * @param dataSourceName 数据源名称
	 * @param parser SQL语句
	 * @param values 字段值
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(String dataSourceName, QuerySQLParser parser, String[] values) throws SQLException {
		return dao.executeUpdate(getConnection(dataSourceName), parser, values);
	}
	
	/**
	 * 执行DML语句
	 * @param dataSourceName 数据源名称
	 * @param sql SQL语句
	 * @param values 字段值
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(String dataSourceName, String sql, String[] values) throws SQLException {
		if (null == values)
			values = new String[] {};
		
		return dao.executeUpdate(getConnection(dataSourceName), sql, values);
	}
	
	
	/**
	 * 执行DML语句
	 * @param dataSourceName 数据源名称
	 * @param colonSql  以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(String dataSourceName, QuerySQLParser colonSql, IData source) throws SQLException {
		return executeUpdate(dataSourceName, colonSql.getSQL(), source);
	}
	
	/**
	 * 执行DML语句
	 * @param dataSourceName 数据源名称
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(String dataSourceName, String colonSql, IData source) throws SQLException {
		if (null == source)
			source = new DataMap();
		
		return dao.executeUpdate(getConnection(dataSourceName), colonSql, source);
	}
	
	/**
	 * 
	 * @param dataSourceName
	 * @param sqls
	 * @return
	 * @throws SQLException
	 */
	public int[] executeBatch(String dataSourceName, String[] sqls, int batchsize) throws SQLException {
		return dao.executeBatch(getConnection(dataSourceName), sqls, batchsize);
	}
	
	/**
	 * 
	 * @param dataSourceName
	 * @param sql
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public int[] executeBatch(String dataSourceName, String sql, String[][] values, int batchsize) throws SQLException {
		return dao.executeBatch(getConnection(dataSourceName), sql, values, batchsize);
	}
	
	public int[] executeBatch(String dataSourceName, String sql, IDataset source, int batchsize) throws SQLException {
		return dao.executeBatch(getConnection(dataSourceName), sql, source, batchsize);
	}
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, QuerySQLParser sql, String[] values) throws SQLException {
		return executeQuery(dataSourceName, sql, values, 0, 0, 2000);
	}
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, QuerySQLParser sql, String[] values, int begin, int end, int fetchsize) throws SQLException {
		return executeQuery(dataSourceName, sql.getSQL(), values, begin, end, fetchsize);
	}
	
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, String sql, String[] values) throws SQLException {
		return executeQuery(dataSourceName, sql, values, 0, 0, 2000);
	}
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param sql 查询语句
	 * @param values 绑定字段值
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, String sql, String[] values, int begin, int end, int fetchsize) throws SQLException {
		if (null == values)
			values = new String[] {};
		
		return dao.executeQuery(getConnection(dataSourceName), getDefaultTableMetaObject(dataSourceName), sql, values, begin, end, fetchsize);
	}
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, QuerySQLParser colonSql, IData source) throws SQLException {
		return executeQuery(dataSourceName, colonSql, source, 0, 0);
	}
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, QuerySQLParser colonSql, IData source, int begin, int end) throws SQLException {
		return executeQuery(dataSourceName, colonSql.getSQL(), source, begin, end, 2000);
	}
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, String colonSql, IData source, int fetchsize) throws SQLException {
		return executeQuery(dataSourceName, colonSql, source, 0, 0, fetchsize);
	}
	
	/**
	 * 查询查询语句
	 * @param dataSourceName 数据源名称
	 * @param colonSql 以":"号为变量的SQL语句
	 * @param source ":"号后字段名和字段值的键值对
	 * @param begin 开始游标
	 * @param end 结束游标
	 * @return
	 * @throws SQLException
	 */
	public IDataset executeQuery(String dataSourceName, String colonSql, IData source, int begin, int end, int fetchsize) throws SQLException {
		if (null == source)
			source = new DataMap();
		
		return dao.executeQuery(getConnection(dataSourceName), getDefaultTableMetaObject(dataSourceName), colonSql, source, begin, end, fetchsize);
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param dataSourceName　数据源名称
	 * @param keys　条件字段名
	 * @param values　条件字段值
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(String dataSourceName, String tableName, String[] keys, String values[]) throws SQLException {
		return dao.queryTable(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, values);
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param dataSourceName　数据源名称
	 * @param keys　条件字段名
	 * @param values　条件字段值
	 * @param begin
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(String dataSourceName, String tableName, String[] keys, String values[], int begin, int end) throws SQLException {
		return dao.queryTable(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, values, begin, end);
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param dataSourceName　数据源名称
	 * @param keys　条件字段名
	 * @param tableName 表名
	 * @param source　条件字段名和值的键值对
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(String dataSourceName, String tableName, String[] keys, IData source) throws SQLException {
		return dao.queryTable(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, source);
	}
	
	/**
	 * 分页查询单表全字段的多条数据
	 * @param dataSourceName　数据源名称
	 * @param keys　条件字段名
	 * @param source　条件字段名和值的键值对
	 * @param begin
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public IDataset queryTable(String dataSourceName, String tableName, String[] keys, IData source, int begin, int end) throws SQLException {
		if (null == keys)
			keys = new String[]{};
		
		if (null == source)
			source = new DataMap();
		
		return dao.queryTable(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, source, begin, end);
	}
	
	/**
	 * 根据主键查询指定数据源的一条数据，如果该数据源连接在当前会话里没有事务则主动close
	 * 多主键控制逻辑存在有一个主键也能正常查询的BUG
	 * 不支持唯一索引查询
	 * @param dataSourceName 数据源名称
	 * @param tableName 表名
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return 单行结果集
	 * @throws SQLException
	 */
	public IData queryByPK(String dataSourceName, String tableName, String[] keys, String[] values) throws SQLException {
		if (null == keys || null == values)
			throw new IllegalArgumentException(String.format("DAO[%s]单表查询异常，主键字段和字段值不能为null", getClass().getName()));
		
		int cnt = keys.length;
		if (0 == cnt || values.length != cnt)
			throw new IllegalArgumentException(String.format("DAO[%s]单表查询异常，主键字段[%s]和字段值[%s]不能为空", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		return dao.queryByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, values);
	}
	
	/**
	 * 根据主键查询指定数据源的一条数据，如果该数据源连接在当前会话里没有事务则主动close
	 * 多主键控制逻辑存在BUG,针对有一个主键也要能正常查询
	 * 不支持唯一索引查询
	 * @param dataSourceName 数据源名称
	 * @param tableName 表名
	 * @param keys 条件字段名，如果为null则用表的主键
	 * @param source 条件字段名与值的键值对
	 * @return 单行结果集
	 * @throws SQLException
	 */
	public IData queryByPK(String dataSourceName, String tableName, String[] keys, IData source) throws SQLException {
		if (null == source)
			throw new IllegalArgumentException(String.format("DAO[%s]单表查询异常，查询条件数据不能为null", getClass().getName()));
		
		return dao.queryByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, source);
	}
	
	/**
	 * 根据表主键删除一条记录，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据源名称
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return executeUpdate()返回的结果
	 * @throws SQLException
	 */
	public int deleteByPK(String dataSourceName, String tableName, String[] keys, String[] values) throws SQLException {
		return dao.deleteByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, values);
	}
	
	/**
	 * 根据表主键删除一条记录，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据源名称
	 * @param keys 条件字段名，如果为null，则用表的主键
	 * @param source 条件字段名和值的键值对
	 * @return executeUpdate()返回的结果
	 * @throws SQLException
	 */
	public int deleteByPK(String dataSourceName, String tableName, String[] keys, IData source) throws SQLException {
		if (null == source || source.isEmpty())
			throw new IllegalArgumentException(String.format("DAO[%s]单表删除异常，字段数据不能为null或空", getClass().getName()));
		
		return dao.deleteByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, source);
	}
	
	/**
	 * 根据表主键修改单条记录，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据源名称
	 * @param cols 需要修改的字段名
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int updateByPK(String dataSourceName, String tableName, String[] cols, String[] colValues, String[] keys, String[] values) throws SQLException {
		if (null == cols || null == colValues)
			throw new IllegalArgumentException(String.format("DAO[%s]单表修改异常，修改字段和修改字段值不能为null", getClass().getName()));
		
		int colsLen = cols.length;
		if (0 == colsLen || colsLen != colValues.length)
			throw new IllegalArgumentException(String.format("DAO[%s]单表修改异常，修改字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(cols),
					Arrays.toString(colValues)));
		
		if (null == keys || null == values)
			throw new IllegalArgumentException(String.format("DAO[%s]单表修改异常，主键字段和字段值不能为null", getClass().getName()));
		
		int keyLen = keys.length;
		if (0 == keyLen || keyLen != values.length)
			throw new IllegalArgumentException(String.format("DAO[%s]单表修改异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		return dao.updateByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), cols, colValues, keys, values);
	}
	
	/**
	 * 根据表主键修改单条记录，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据源名称
	 * @param cols 需要修改的字段名，如果为null则全表修改
	 * @param source 需要修改的字段及数据的键值对
	 * @param keys 条件字段名，如果为null则是表主键
	 * @param values 条件字段值，如果为null则根据keys在source里对应的值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int updateByPK(String dataSourceName, String tableName, String[] cols, IData source, String[] keys, String[] values) throws SQLException {
		int keyLen = 0;
		if (null != keys && null != values) {
			keyLen = keys.length;
			if (0 == keyLen || keyLen != values.length)
				throw new IllegalArgumentException(String.format("DAO[%s]单表修改异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
						getClass().getName(),
						Arrays.toString(keys),
						Arrays.toString(values)));
		}
		
		return dao.updateByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), cols, source, keys, values);
	}
	
	/**
	 * 根据表主键查询单条数据，并做增量修改，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据源名称
	 * @param cols 需要修改的字段名
	 * @param keys 条件字段名
	 * @param values 条件字段值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int saveByPK(String dataSourceName, String tableName, String[] cols, String[] colValues, String[] keys, String[] values) throws SQLException {
		if (null == cols || null == colValues)
			throw new IllegalArgumentException(String.format("DAO[%s]单表保存异常，修改字段和修改字段值不能为null", getClass().getName()));
		
		int colsLen = cols.length;
		if (0 == colsLen || colsLen != colValues.length)
			throw new IllegalArgumentException(String.format("DAO[%s]单表保存异常，修改字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(cols),
					Arrays.toString(colValues)));
		
		if (null == keys || null == values)
			throw new IllegalArgumentException(String.format("DAO[%s]单表保存异常，主键字段和字段值不能为null", getClass().getName()));
		
		int keyLen = keys.length;
		if (0 == keyLen || keyLen != values.length)
			throw new IllegalArgumentException(String.format("DAO[%s]单表保存异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		return dao.saveByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), cols, colValues, keys, values);
	}
	
	/**
	 * 根据表主键查询单条数据，并做增量修改，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据源名称
	 * @param source 修改数据的键值对
	 * @param keys 条件字段名，如果为null则用表的主键
	 * @param values 条件字段值，如果为null，则用keys在source里对应的值
	 * @return 返回executeUpdate的结果
	 * @throws SQLException
	 */
	public int saveByPK(String dataSourceName, String tableName, IData source, String[] keys, String[] values) throws SQLException {
		if (null == source || source.isEmpty())
			throw new IllegalArgumentException(String.format("DAO[%s]单表保存异常，数据不能为null或空", getClass().getName()));
		
		
		int keysLen = 0;
		if (null != keys && null != values) {
			keysLen = keys.length;
			
			if (0 == keysLen || keysLen != values.length)
				throw new IllegalArgumentException(String.format("DAO[%s]单表保存异常，修改字段[%s]和字段值[%s]不能为空或个数不一致", 
						getClass().getName(),
						Arrays.toString(keys),
						Arrays.toString(values)));
		}
		
		return dao.saveByPK(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), source, keys, values);
	}
	
	/**
	 * 根据表主键新增数据，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据湖名称
	 * @param keys　字段名
	 * @param values　字段值
	 * @return executeUpdate返回的结果
	 * @throws SQLException
	 */
	public int insert(String dataSourceName, String tableName, String[] keys, String[] values) throws SQLException {
		if (null == keys || null == values)
			throw new IllegalArgumentException(String.format("DAO[%s]单表新增异常，主键字段和字段值不能为null", getClass().getName()));
		
		int keyLen = keys.length;
		if (0 == keyLen || keyLen != values.length)
			throw new IllegalArgumentException(String.format("DAO[%s]单表新增异常，主键字段[%s]和字段值[%s]不能为空或个数不一致", 
					getClass().getName(),
					Arrays.toString(keys),
					Arrays.toString(values)));
		
		return dao.insert(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), keys, values);
	}
	
	
	
	/**
	 * 根据表主键新增数据，该操作并不真正提交事务，但有回滚逻辑
	 * @param dataSourceName 数据湖名称
	 * @param source　绑定字段名与字段值的键值对
	 * @return executeUpdate返回的结果
	 * @throws SQLException
	 */
	public int insert(String dataSourceName, String tableName, IData source) throws SQLException {
		if (null == source)
			throw new IllegalArgumentException(String.format("DAO[%s]单表新增异常，数据不能为null", getClass().getName()));
		
		return dao.insert(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), source);
	}
	
	/**
	 * 批量执行
	 * @param dataSourceName 数据源
	 * @param tableName 表名
	 * @param source 参数
	 * @param batchsize 批量大小
	 * @return
	 * @throws SQLException
	 */
	public int[] insert(String dataSourceName, String tableName, IDataset source, int batchsize) throws SQLException {
		if (null == source || source.isEmpty())
			return new int[] {};
		return dao.insert(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), source, batchsize);
	}
	
	public int[] delete(String dataSourceName, String tableName, IDataset source, String[] keys, int batchsize) throws SQLException {
		if (null == source || source.isEmpty())
			return new int[] {};
		return dao.delete(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), source, keys, batchsize);
	}
	
	public int[] update(String dataSourceName, String tableName, IDataset source, int batchsize) throws SQLException {
		if (null == source || source.isEmpty())
			return new int[] {};
		return dao.update(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), source, null, null, batchsize);
	}
	public int[] update(String dataSourceName, String tableName, IDataset source, String[] cols, String[] keys, int batchsize) throws SQLException {
		if (null == source || source.isEmpty())
			return new int[] {};
		return dao.update(getConnection(dataSourceName), getTableMetaObject(dataSourceName, tableName), source, cols, keys, batchsize);
	}
	
	/**
	 * 获取数据库连接
	 * @param dataSourceName
	 * @return
	 */
	private ConnectionWrapper getConnection(String dataSourceName) throws SQLException {
		try {
			return (ConnectionWrapper) DAOSessionManager.getManager().getSession().getConnection(dataSourceName);
		} catch (Exception e) {
			throw new SQLException(e);
		}
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
	private TableMetaObject getTableMetaObject(String dataSourceName, String tableName) throws SQLException {
		return getDataSource(dataSourceName).getTableMetaData().getTableMetaObject(tableName);
	}
	
	private TableMetaObject getDefaultTableMetaObject(String dataSourceName) throws SQLException {
		return getDataSource(dataSourceName).getTableMetaData().getDefaultTableMetaObject();
	}
}
