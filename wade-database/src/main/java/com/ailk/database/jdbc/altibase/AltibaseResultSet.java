/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.altibase;

import java.sql.ResultSet;

import com.ailk.database.jdbc.wrapper.ResultSetWrapper;

/**
 * AltibaseResultSet封装类
 * 
 * @className: AltibaseResultSet.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class AltibaseResultSet extends ResultSetWrapper {
	
	public AltibaseResultSet(ResultSet rs) {
		super(rs);
	}

}
