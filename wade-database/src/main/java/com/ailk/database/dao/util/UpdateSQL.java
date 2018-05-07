/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月15日
 * 
 * Just Do IT.
 */
package com.ailk.database.dao.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * 增量修改SQL对象，存放SQL语句及增量修改字段
 */
public class UpdateSQL {
	
	private String sql;
	List<String> columns = new ArrayList<String>();
	
	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}
	
	/**
	 * @return the columns
	 */
	public List<String> getColumns() {
		return columns;
	}
	
	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

}
