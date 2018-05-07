/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.mysql;

import com.ailk.database.jdbc.wrapper.DataBaseMetaData;

/**
 * TODO
 * 
 * @className: MySQLDataBaseMetaData.java
 * @author: liaosheng
 * @date: 2014-4-14
 */
public class MySQLDataBaseMetaData extends DataBaseMetaData {
	
	public MySQLDataBaseMetaData(MySQLDataSourceWrapper ds) {
		super(ds);
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.wrapper.DataBaseMetaData#getPaginationSQL(java.lang.StringBuilder, int, int)
	 */
	@Override
	public StringBuilder getPaginationSQL(StringBuilder sql, int begin, int end) {
		if (0 == begin && 0 == end) {
			return sql;
		} else {
			return sql.append(" limit ?, ? ");
		}
	}
	
}
