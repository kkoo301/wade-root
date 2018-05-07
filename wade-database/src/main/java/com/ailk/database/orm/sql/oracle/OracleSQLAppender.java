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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.sql.ISQLAppender;
import com.ailk.database.orm.sql.SQLAppenderFactory;

/**
 * @description
 * 适配Oracle　SQL　语法
 */
public class OracleSQLAppender<T extends BOEntity> implements ISQLAppender<T> {
	
	private static final Logger log = LoggerFactory.getLogger(OracleSQLAppender.class);
	
	/**
	 * BO实体对象
	 */
	private T entity;
	
	/**
	 * 生成SQL语句
	 */
	private StringBuilder sql = new StringBuilder(100);
	
	/**
	 * 获取绑定参数名称
	 */
	private List<String> binds = new ArrayList<String>();
	
	/**
	 * 获取绑定参数值
	 */
	private Map<String, Object> parameter;
	
	
	public OracleSQLAppender(T entity) {
		this.entity = entity;
	}
	
	/**
	 * @return the entity
	 */
	@Override
	public T getEntity() {
		return entity;
	}
	
	@Override
	public List<String> getBinds() {
		return binds;
	}
	
	
	@Override
	public Map<String, Object> getParameter() {
		return parameter;
	}
	
	
	@Override
	public void setParameter(Map<String, Object> parameter) {
		if (null == this.parameter)
			this.parameter = parameter;
		else
			this.parameter.putAll(parameter);
	}
	
	@Override
	public void addParameter(String column, Object value) {
		if (null == parameter) {
			parameter = new HashMap<String, Object>(10);
		}
		this.parameter.put(column, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getSQL() throws SQLException {
		Object[] rtn = SQLAppenderFactory.parseColonSql(this.sql.toString());
		
		if (null == rtn) {
			log.error("SQL变量解析异常:" + this.sql.toString(), new Exception("SQL变量解析异常"));
			
			throw new SQLException("SQL变量解析异常");
		}
		
		this.binds = (List<String>) rtn[1];
		
		return (String) rtn[0];
	}
	
	@Override
	public ISQLAppender<T> append(String sql) {
		this.sql.append(sql);
		return this;
	}
	
	@Override
	public ISQLAppender<T> where(String sql) {
		this.sql.append(" WHERE ").append(sql);
		return this;
	}
	
	@Override
	public ISQLAppender<T> and(String column) {
		this.sql.append(" AND ").append(column);
		return this;
	}
	
	@Override
	public ISQLAppender<T> bind(String column) {
		this.sql.append(" :").append(column);
		return this;
	}
	
	@Override
	public ISQLAppender<T> equal() {
		this.sql.append(" = ");
		return this;
	}
	
	@Override
	public ISQLAppender<T> betweenToChar(String column1, String column2, String dateformat) {
		this.sql.append(" BETWEEN TO_CHAR(:").append(column1).append(", '").append(dateformat).append("')");
		this.sql.append(" AND TO_CHAR(:").append(column2).append(", '").append(dateformat).append("')");
		return this;
	}
	
	
	@Override
	public ISQLAppender<T> betweenToDate(String column1, String column2, String dateformat) {
		this.sql.append(" BETWEEN TO_DATE(:").append(column1).append(", '").append(dateformat).append("')");
		this.sql.append(" AND TO_DATE(:").append(column2).append(", '").append(dateformat).append("')");
		return this;
	}
}
