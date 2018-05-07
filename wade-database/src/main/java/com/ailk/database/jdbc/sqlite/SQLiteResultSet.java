/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.sqlite;

import java.sql.ResultSet;

import com.ailk.database.jdbc.wrapper.ResultSetWrapper;

/**
 * SQLite数据库连接封装类
 * 
 * @className: SQLiteResultSet.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class SQLiteResultSet extends ResultSetWrapper {
	
	public SQLiteResultSet(ResultSet rs) {
		super(rs);
	}

}
