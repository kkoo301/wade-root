/**
 * 
 */
package com.ailk.database.session;

import com.ailk.common.CodeException;

/**
 * @author yifur
 *
 */
public class SessionConnectionException extends CodeException {
	
	private static final long serialVersionUID = 1017319589004570148L;

	/**
	 * 会话连接异常,封装JDBC,JNDI,DBCP取数据库连接的异常类
	 */
	public SessionConnectionException(String code, String[] params) {
		super(code, params);
	}
	
}
