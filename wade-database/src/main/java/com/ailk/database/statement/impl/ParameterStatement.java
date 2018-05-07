package com.ailk.database.statement.impl;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import org.apache.log4j.Logger;
import com.ailk.database.statement.IStatement;
import com.ailk.database.statement.Parameter;

public class ParameterStatement implements IStatement {
	
	private static transient Logger log = Logger.getLogger(ParameterStatement.class);
	
	private PreparedStatement statement;
	private String sql;
	private Parameter param;
	private Parameter[] params;
	private boolean noQueryTimeout = true;
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlstr
	 * @throws Exception
	 */
	public ParameterStatement(Connection conn, String sqlstr, Parameter param) throws Exception {
		statement = conn.prepareStatement(sqlstr);
		this.sql = sqlstr;
		this.param = param;
	}
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlstr
	 * @param param
	 * @param typeScroll
	 * @param concurRead
	 * @throws Exception
	 */
	public ParameterStatement(Connection conn, String sqlstr, Parameter param, int typeScroll, int concurRead) throws Exception {
		statement = conn.prepareStatement(sqlstr, typeScroll, concurRead);
		this.sql = sqlstr;
		this.param = param;
	}
	
	/**
	 * construct function
	 * @param conn
	 * @param sql
	 * @param params
	 * @throws Exception
	 */
	public ParameterStatement(Connection conn, String sqlstr, Parameter[] params) throws Exception {
		statement = conn.prepareStatement(sqlstr);
		this.sql = sqlstr;
		this.params = params;
	}
	
	/**
	 * @param stmtTimeout the stmtTimeout to set
	 */
	public void setQueryTimeout(int stmtTimeout) throws SQLException {
		if (noQueryTimeout && this.statement.getQueryTimeout() == 0) {
			this.statement.setQueryTimeout(stmtTimeout);
			noQueryTimeout = false;
		}
	}
	
	/**
	 * get sql
	 * @return String
	 * @throws Exception
	 */
    public String getSql() throws Exception {
    	return sql;
    }

	/**
	 * get statement
	 * @return Statement
	 * @throws Exception
	 */
    public Statement getStatement() throws Exception {
    	return statement;
    }

	/**
	 * execute query
	 * @return ResultSet
	 * @throws Exception
	 */
    public ResultSet executeQuery() throws Exception {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("SQL " + sql);
    		log.debug("PARAM " + param);
    	}
    	
    	statement.setFetchSize(DEFAULT_FETCH_SIZE);
    	
    	setParameters(statement, param);
    	ResultSet rs = null;
    	try {
    		rs = statement.executeQuery();
    	} catch (SQLException e) {
    		log.error("ERROR SQL " + sql);
    		throw e;
	    }
    	
    	return rs;
    }
    
    /**
     * execute update
     * @return int
     * @throws Exception
     */
    public int executeUpdate() throws Exception {
    	if (param == null)
    		return -1;
    	
    	if (log.isDebugEnabled()) {
    		log.debug("SQL " + sql);
    		log.debug("PARAM " + param);
    	}
    	
    	setParameters(statement, param);
    	
    	try {
    		int result = statement.executeUpdate();
    		return result;
    	} catch (SQLException e) {
    		log.error("ERROR SQL " + sql);
    		throw e;
    	}
    }
    
    /**
     * execute batch
     * @return int[]
     * @throws Exception
     */
    public int[] executeBatch() throws Exception {
    	
    	if (0 == params.length) {
    		return new int[] {};
    	}
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Batch SQL " + sql);
    	}
    	
	    for (int i = 0, paramsSize = params.length; i < paramsSize; i++) {
	    	if (params[i] == null)
	    		continue;
	    	
	    	setParameters(statement, params[i]);
	    	if (log.isDebugEnabled()) {
	    		log.debug("Batch PARAM " + params[i]);
	    	}
	    	statement.addBatch();
	    }
	    
	    try {
	    	int[] result = statement.executeBatch();
	    	return result;
    	} catch (java.sql.SQLException e) {
    		log.error("ERROR Batch SQL " + sql);
    		throw e;
    	} finally {
    		statement.clearBatch();
    	}
    }

	/**
	 * close statement
	 * @throws Exception
	 */
	public void close() throws Exception {
		statement.close();
	}
	
	/**
	 * 动态绑定参数
	 * 
	 * @param statement
	 * @param param
	 * @throws Exception
	 */
	private void setParameters(PreparedStatement statment, Parameter param) throws Exception {
    	for (int i = 0, size = param.size(); i < size; i++) {
    		Object value = param.get(i);
    		if (null == value) {
    			statement.setNull(i + 1, Types.VARCHAR);
    		} else {
    			if (value instanceof StringReader) {
    				statement.setCharacterStream(i + 1, (StringReader) value, value.toString().length());
    			} else {
    				statement.setObject(i + 1, value);
    			}
    		}
    	}
	}
	
	/**
	 * @param params the params to set
	 */
	public void setParams(Parameter[] params) {
		this.params = params;
	}

	/**
	 * set max rows
	 * @throws Exception
	 */
	public void setMaxRows(int max) throws Exception {
		statement.setMaxRows(max);
	}
}