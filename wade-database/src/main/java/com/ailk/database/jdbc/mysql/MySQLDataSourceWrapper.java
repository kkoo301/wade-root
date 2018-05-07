/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ailk.database.jdbc.wrapper.DataBaseMetaData;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;

/**
 * MySQL数据源封装类，getConnection为MySQLConnection实例
 * 
 * @className: MySQLDataSourceWrapper.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class MySQLDataSourceWrapper extends DataSourceWrapper {
	
	private static final String DIALECT = "mysql";
	public static final String VALIDSQL = "select 1";
	
	private DataBaseMetaData dataBaseMetaData;
	private TableMetaData tableMetaData;
	
	public MySQLDataSourceWrapper(DataSource ds, String user, String operUser, String name) {
		super(ds, user, operUser, name);
		this.dataBaseMetaData = new MySQLDataBaseMetaData(this);
		this.tableMetaData = new MySQLTableMetaData(this);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.datasource.DataSourceWrapper#getTableMetaData()
	 */
	@Override
	public TableMetaData getTableMetaData() {
		return this.tableMetaData;
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.wrapper.DataSourceWrapper#getDataBaseMetaData()
	 */
	@Override
	public DataBaseMetaData getDataBaseMetaData() {
		return this.dataBaseMetaData;
	}

	
	/* (non-Javadoc)
	 * @see com.ailk.database.jdbc.datasource.DataSourceWrapper#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		MySQLConnection conn = new MySQLConnection(getName(), super.getConnection());
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
