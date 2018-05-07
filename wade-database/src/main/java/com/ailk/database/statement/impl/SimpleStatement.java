package com.ailk.database.statement.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import com.ailk.database.statement.IStatement;

public class SimpleStatement implements IStatement {
	
	private static transient Logger log = Logger.getLogger(SimpleStatement.class);
	
	private Statement statement;
	private String sql;
	private String[] sqls;
	private boolean noQueryTimeout = true;
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlstr
	 * @throws Exception
	 */
	public SimpleStatement(Connection conn, String sqlstr) throws Exception {
		statement = conn.createStatement();
		this.sql = sqlstr;
	}
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlstr
	 * @param typeScroll
	 * @param concurRead
	 * @throws Exception
	 */
	public SimpleStatement(Connection conn, String sqlstr, int typeScroll, int concurRead) throws Exception {
		statement = conn.createStatement(typeScroll, concurRead);
		this.sql = sqlstr;
	}
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlsstr
	 * @throws Exception
	 */
	public SimpleStatement(Connection conn, String[] sqlsstr) throws Exception {
		statement = conn.createStatement();
		this.sqls = sqlsstr;
	}
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlsstr
	 * @param typeScroll
	 * @param concurRead
	 * @throws Exception
	 */
	public SimpleStatement(Connection conn, String[] sqlsstr, int typeScroll, int concurRead) throws Exception {
		statement = conn.createStatement(typeScroll, concurRead);
		this.sqls = sqlsstr;
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
    	
    	ResultSet rs = null;
    	
    	if (log.isDebugEnabled()) {
    		log.debug("SQL " + sql);
    	}
    	
    	statement.setFetchSize(DEFAULT_FETCH_SIZE);
    	
    	try {
    		rs = statement.executeQuery(sql);
    	} catch (java.sql.SQLException e) {
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
    	
    	if (log.isDebugEnabled()) {
    		log.debug("SQL " + sql);
    	}
    	
    	try {
    		int result = statement.executeUpdate(sql);
    		return result;
    	} catch (java.sql.SQLException e) {
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
    	
    	try {
    		for (int i = 0, size=sqls.length; i < size; i++) {
        		if (log.isDebugEnabled()) {
        			log.debug("SQL " + sqls[i]);
        		}
        		statement.addBatch(sqls[i]);
        	}
    		
        	int[] result = statement.executeBatch();
        	
        	return result;
    	} catch (Exception e) {
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
	 * set max rows
	 * @throws Exception
	 */
	public void setMaxRows(int max) throws Exception {
		statement.setMaxRows(max);
	}	
}