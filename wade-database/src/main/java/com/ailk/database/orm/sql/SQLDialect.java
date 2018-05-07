/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.sql;

/**
 * @description TODO
 */
public enum SQLDialect {
	

	Oracle("oracle"), MySQL("mysql");

	private String dialect;
	
	SQLDialect(String dialect) {
		this.dialect = dialect;
	}
	
	/**
	 * @return the dialect
	 */
	public String getDialect() {
		return dialect;
	}

}
