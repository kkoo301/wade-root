/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.sqlite;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;

/**
 * SQLite数据源封装类，getConnection为SQLiteConnection的实例
 * 
 * @className: OracleDataSrouceWrapper.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class SQLiteDataSrouceWrapper extends DataSourceWrapper {
	
	private static final String DIALECT = "sqlite";
	public static final String VALIDSQL = "select date('now')";
	
	private TableMetaData tableMetaData;
	
	public SQLiteDataSrouceWrapper(DataSource ds, String user, String owner, String name) {
		super(ds, user, owner, name);
		this.tableMetaData = new SQLiteTableMetaData(this);
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
		SQLiteConnection conn = new SQLiteConnection(getName(), super.getConnection());
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
