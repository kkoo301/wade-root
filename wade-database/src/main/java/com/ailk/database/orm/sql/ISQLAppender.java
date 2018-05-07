/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ailk.database.orm.bo.BOEntity;

/**
 * @description
 * SQL扩展对象
 */
public interface ISQLAppender <T extends BOEntity> {
	
	
	/**
	 * 获取Entity对象
	 * @return
	 */
	public T getEntity();
	
	/**
	 * 获取SQL内容
	 * @return
	 */
	public String getSQL() throws SQLException ;
	
	/**
	 * 获取绑定参数名列表
	 * @return
	 */
	public List<String> getBinds();
	
	/**
	 * 获取绑定参数值
	 * @return
	 */
	public Map<String, Object> getParameter();
	
	/**
	 * 设置绑定参数
	 * @param parameter
	 */
	public void setParameter(Map<String, Object> parameter);
	
	/**
	 * 添加参数
	 * @param column
	 * @param value
	 */
	public void addParameter(String column, Object value);
	
	/**
	 * sql语句
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ISQLAppender<T> append(String sql) ;
	
	/**
	 * WHERE
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ISQLAppender<T> where(String sql) ;
	
	/**
	 * AND
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ISQLAppender<T> and(String sql) ;
	
	/**
	 * :xxx
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ISQLAppender<T> bind(String column) ;
	
	
	public ISQLAppender<T> equal() ;
	
	
	/**
	 * 生成语句如下：<br>
	 * BETWEEN TO_DATE(:column1, 'dateformat') AND TO_DATE(:column2, 'dateformat')<br>
	 * @param column1
	 * @param column2
	 * @param column3
	 * @param dateformat
	 * @return
	 */
	public ISQLAppender<T> betweenToDate(String column1, String column2, String dateformat);
	
	/**
	 * 生成语句如下：<br>
	 * BETWEEN TO_CHAR(:column1, 'dateformat') AND TO_CHAR(:column2, 'dateformat')<br>
	 * @param column1
	 * @param column2
	 * @param dateformat
	 * @return
	 */
	public ISQLAppender<T> betweenToChar(String column1, String column2, String dateformat);
	
	
}
