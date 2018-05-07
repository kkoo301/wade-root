/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.oracle;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;

/**
 * Oracel数据源封装类，getConnection为OracleConnection的实例
 * 
 * @className: OracleDataSrouceWrapper.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class OracleDataSrouceWrapper extends DataSourceWrapper {
	
	private static final String DIALECT = "oracle";
	public static final String VALIDSQL = "select 1 from dual";
	
	private TableMetaData tableMetaData;
	
	public OracleDataSrouceWrapper(DataSource ds, String user, String owner, String name) {
		super(ds, user, owner, name);
		this.tableMetaData = new OracleTableMetaData(this);
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
		OracleConnection conn = new OracleConnection(getName(), super.getConnection());
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
