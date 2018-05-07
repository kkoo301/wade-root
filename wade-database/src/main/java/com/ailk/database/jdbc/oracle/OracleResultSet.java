/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.oracle;

import java.sql.ResultSet;

import com.ailk.database.jdbc.wrapper.ResultSetWrapper;

/**
 * Oracle数据库连接封装类
 * 
 * @className: OracleResultSet.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class OracleResultSet extends ResultSetWrapper {
	
	public OracleResultSet(ResultSet rs) {
		super(rs);
	}

}
