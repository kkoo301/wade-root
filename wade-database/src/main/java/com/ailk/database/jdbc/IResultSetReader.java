/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月28日
 * 
 * Just Do IT.
 */
package com.ailk.database.jdbc;

import java.sql.SQLException;

/**
 * @description
 * 处理ResultSet的结果集，避免多次转换
 */
public interface IResultSetReader<ResultSet extends Object, Row extends Object> {
	
	/**
	 * 获数数据集大小
	 * @return
	 */
	public int size();
	
	/**
	 * 返回结果集
	 * @return
	 */
	public ResultSet getResultSet();
	
	/**
	 * 每读取一条数据时执行该方法
	 * @return
	 */
	public Row nextRow();
	
	/**
	 * 将当前行数据添加到结果集里
	 * @param row
	 */
	public void addRow(Row row);
	
	/**
	 * 是否为Rowid
	 * @param columnName
	 * @return
	 */
	public boolean isRowId(String columnName);
	
	/**
	 * 处理读到的字段以及字段值
	 * @param row	当前读取的行对象，即next()返回的对象
	 * @param columnName	字段名
	 * @param value	字段值
	 */
	public void read(Row row, String columnName, Object value, int sqlType) throws SQLException;
	
}
