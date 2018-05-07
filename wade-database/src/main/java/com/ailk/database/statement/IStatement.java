package com.ailk.database.statement;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public interface IStatement {
	
	public static final long EXECUTE_MAX_TIME = 10 * 1000l;
	public static final int DEFAULT_FETCH_SIZE = 2000;
	public static final int DEFAULT_BATCH_SIZE = 2000;
	
	/**
	 * get sql
	 * @return String
	 * @throws Exception
	 */
    public String getSql() throws Exception;

	/**
	 * get statement
	 * @return Statement
	 * @throws Exception
	 */
    public Statement getStatement() throws Exception;

	/**
	 * execute query
	 * @return ResultSet
	 * @throws Exception
	 */
    public ResultSet executeQuery() throws Exception;
    
    /**
     * execute update
     * @return int
     * @throws Exception
     */
    public int executeUpdate() throws Exception;

    /**
     * execute batch
     * @return int[]
     * @throws Exception
     */
    public int[] executeBatch() throws Exception;

	/**
	 * close statement
	 * @throws Exception
	 */
	public void close() throws Exception;
	
	/**
	 * set max rows
	 * @throws Exception
	 */
	public void setMaxRows(int max) throws Exception;
	
	/**
	 * 设置Statement对象的QueryTimeout值
	 * @param stmtTimeout
	 */
	public void setQueryTimeout(int stmtTimeout) throws SQLException ;

}