package com.ailk.database.statement.impl;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Types;

import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.statement.IStatement;
import com.ailk.database.util.DaoUtil;

import java.util.ArrayList;
import java.util.List;

public class BindingStatement implements IStatement {
	
	private static transient Logger log = Logger.getLogger(BindingStatement.class);
	
	private PreparedStatement statement;
	private String sql;
	private IData param;
	private IDataset params;
	private String[] names;
	private boolean noQueryTimeout = true;
	
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlstr
	 * @throws Exception
	 */
	public BindingStatement(Connection conn, String sqlstr, IData param) throws Exception {
		preprocStatement(sqlstr);
		statement = conn.prepareStatement(sql);
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
	public BindingStatement(Connection conn, String sqlstr, IData param, int typeScroll, int concurRead) throws Exception {
		preprocStatement(sqlstr);
		statement = conn.prepareStatement(sql, typeScroll, concurRead);
		this.param = param;
	}
	
	/**
	 * construct function
	 * @param conn
	 * @param sqlstr
	 * @param params
	 * @throws Exception
	 */
	public BindingStatement(Connection conn, String sqlstr, IDataset params) throws Exception {
		preprocStatement(sqlstr);
		statement = conn.prepareStatement(sql);
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
    	
    	ResultSet rs = null;
    	
    	statement.setFetchSize(DEFAULT_FETCH_SIZE);
    	setParameters(statement, names, param);
    	
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
    	if (log.isDebugEnabled()) {
    		log.debug("SQL " + sql);
    		log.debug("PARAM " + param);
    	}
    	
    	setParameters(statement, names, param);
    	int result = 0;
    	try {
    		result = statement.executeUpdate();
    	} catch (SQLException e) {
    		log.error("ERROR SQL " + sql);
    		throw e;
    	}
    	return result;
    }
    
    /**
     * execute batch
     * @return int[]
     * @throws Exception
     */
    public int[] executeBatch() throws Exception {
    	
    	if (params.size() == 0) {
    		return new int[] {};
    	}
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Batch SQL " + sql);
    	}
    	
    	for (int i = 0, size = params.size(); i < size; i++) {
    		setParameters(statement, names, (IData) params.get(i));
    		if (log.isDebugEnabled()) {
    			log.debug("Batch PARAM " + params.get(i));
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
	 * set parameters
	 * @param statement
	 * @param param
	 * @throws Exception
	 */
	public void setParameters(PreparedStatement statment, String[] names, IData param) throws Exception {
		
    	for (int i = 0, size=names.length; i < size; i++) {
    		String name = names[i];
    		Object value = param.get(name);
    		if (null == value) {
    			statement.setNull(i + 1, Types.VARCHAR);
    			if (log.isDebugEnabled()) {
    				log.debug("[" + (i + 1) + "]BINDING [" + name + "] null");
    			}
    		} else {
    			if (value instanceof StringReader) {
    				statement.setCharacterStream(i + 1, (StringReader) value, value.toString().length());
    			} else {
    				statement.setObject(i + 1, value);
    			}
    			
    			if (log.isDebugEnabled()) {
    				log.debug("[" + (i + 1) + "]BINDING [" + name + "] [" + value + "]");
    			}
    		}
    	}
	}
		
	/**
	 * 参数替换成?，并收集参数名
	 * 
	 * @author zhoulin2
	 * @param sqlstr
	 * @throws Exception
	 */
	private void preprocStatement(String sqlstr) throws Exception {
		
		char[] sqlchar = sqlstr.toCharArray();
		
		boolean isInnerQuota = false; // 标志: 是否在引号中
		boolean isInnerParam = false; // 标志：是否在参数中
		
		int begParam = -1; // 参数开始位置
		
		List<String> paramNames = new ArrayList<String>(20);
		
		StringBuilder sb = new StringBuilder(sqlchar.length);
		for (int i = 0, size=sqlchar.length; i < size; i++) {

			char c = sqlchar[i];
			
			if ('\'' == c) {
				if (isInnerQuota) {
					isInnerQuota = false; // 退出引号中
				} else {
					isInnerQuota = true;  // 进入引号中
				}
				sb.append(c);
				continue;
			}
			
			if (isInnerQuota) {
				sb.append(c);
				continue;
			} 
			
			if (':' == c) {
				isInnerParam = true;
				begParam = i + 1;
				sb.append('?');
				continue;
			} 
			
			if (isInnerParam) {
				if (!DaoUtil.isVariableChar(c)) {
					isInnerParam = false;
					
					String paramName = sqlstr.substring(begParam, i);
					if (!StringUtils.isBlank(paramName)) {
						paramNames.add(paramName);
					}
					begParam = -1;
					
					sb.append(c);
				}
				continue;
			} 

			sb.append(c);

		}
		
		if (-1 != begParam) {
			String paramName = sqlstr.substring(begParam, sqlchar.length);
			if (!StringUtils.isBlank(paramName)) {
				paramNames.add(paramName);
			}
		}
		
		this.sql = sb.toString();
		this.names = (String[]) paramNames.toArray(new String[0]);
	}
		
	/**
	 * set max rows
	 * @throws Exception
	 */
	public void setMaxRows(int max) throws Exception {
		statement.setMaxRows(max);
	}
	
	/**
	 * @param params the params to set
	 */
	public void setParams(IDataset params) {
		this.params = params;
	}
	
}