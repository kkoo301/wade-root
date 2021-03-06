/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.altibase;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.ailk.database.jdbc.wrapper.ConnectionWrapper;

/**
 * Altibase 数据库连接封装类
 * 
 * @className: AltibaseConnection.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class AltibaseConnection extends ConnectionWrapper {
	
	public AltibaseConnection(String dataSourceName, Connection conn) throws SQLException {
		super(dataSourceName, conn);
	}


	@Override
	public Statement createStatement() throws SQLException {
		return new AltibaseStatement(this, connection.createStatement());
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return new AltibasePreparedStatement(this, connection.prepareStatement(sql));
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return new AltibaseCallableStatement(this, connection.prepareCall(sql));
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return new AltibaseStatement(this, connection.createStatement(resultSetType, resultSetConcurrency));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return new AltibasePreparedStatement(this, connection.prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return new AltibaseCallableStatement(this, connection.prepareCall(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return new AltibaseStatement(this, connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return new AltibasePreparedStatement(this, connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return new AltibaseCallableStatement(this, connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return new AltibasePreparedStatement(this, connection.prepareStatement(sql, autoGeneratedKeys));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		return new AltibasePreparedStatement(this, connection.prepareStatement(sql, columnIndexes));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		return new AltibasePreparedStatement(this, connection.prepareStatement(sql, columnNames));
	}
	
	
	@Override
	public void close() throws SQLException {
		try {
			setTransaction(true);
			rollback();
		} catch (Exception e) {
			
		} finally {
			super.close();
		}
	}

}
