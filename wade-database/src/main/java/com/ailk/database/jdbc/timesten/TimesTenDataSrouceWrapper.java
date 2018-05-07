/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.timesten;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;

/**
 * TimesTen数据源封装类，getConnection为TimesTenConnection的实例
 * 
 * @className: TimesTenDataSrouceWrapper.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class TimesTenDataSrouceWrapper extends DataSourceWrapper {
	
	private static final String DIALECT = "timesten";
	public static final String VALIDSQL = "select 1 from SYS.DUAL";
	
	private TableMetaData tableMetaData;
	
	public TimesTenDataSrouceWrapper(DataSource ds, String user, String operUser, String name) {
		super(ds, user, operUser, name);
		this.tableMetaData = new TimesTenTableMetaData(this);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.datasource.DataSourceWrapper#getTableMetaData()
	 */
	@Override
	public TableMetaData getTableMetaData() {
		return this.tableMetaData;
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.datasource.DataSourceWrapper#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		TimesTenConnection conn = new TimesTenConnection(getName(), super.getConnection());
		conn.setStmtTimeout(getStmtTimeout());
		return conn;
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.datasource.DataSourceWrapper#getDialect()
	 */
	@Override
	public String getDialect() {
		return DIALECT;
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.datasource.DataSourceWrapper#getValidSQL()
	 */
	@Override
	public String getValidSQL() {
		return VALIDSQL;
	}
}
