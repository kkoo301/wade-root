/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.timesten;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ailk.database.jdbc.wrapper.ResultSetWrapper;

/**
 * TimesTen数据库连接封装类
 * 
 * @className: TimesTenResultSet.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class TimesTenResultSet extends ResultSetWrapper {
	
	public TimesTenResultSet(ResultSet rs) {
		super(rs);
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.wrapper.ResultSetWrapper#setFetchSize(int)
	 */
	@Override
	public void setFetchSize(int rows) throws SQLException {
		getStatement().setFetchSize(rows);
	}
}
