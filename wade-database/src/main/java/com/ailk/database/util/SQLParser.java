package com.ailk.database.util;

import java.util.List;
import java.util.ArrayList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SQLParser
 * @description: 动态SQL解析类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-4-18
 */
public final class SQLParser {

	private StringBuilder sqlstr = new StringBuilder(500);
	private IData param;
	private String group;
	
	/**
	 * construct function
	 * @param param
	 * @throws Exception
	 */
	public SQLParser(IData param) throws Exception {
		if (null != param) {
			this.param = param;
		} else {
			this.param = new DataMap();
		}
	}
	
	/**
	 * construct function
	 * @param param
	 * @throws Exception
	 */
	public SQLParser(IData param, String group) throws Exception {
		if (null != param) {
			this.param = param;
		} else {
			this.param = new DataMap();
		}
		this.group = group;
	}
	
	/**
	 * 获取SQL语句片段中的所有参数集合
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	private static final List<String> getTokens(String sql) throws Exception {
				
		boolean isInnerQuota = false; // 标志: 是否在引号中
		boolean isInnerParam = false; // 标志：是否在参数中
		
		int begParam = -1; // 参数开始位置
		
		List<String> paramNames = new ArrayList<String>(20);
				
		for (int i = 0, len = sql.length(); i < len; i++) {

			char c = sql.charAt(i);
			
			if ('\'' == c) {
				if (isInnerQuota) {
					isInnerQuota = false; // 退出引号中
				} else {
					isInnerQuota = true;  // 进入引号中
				}
				continue;
			}
			
			if (isInnerQuota) {
				continue;
			} 
			
			if (':' == c) {
				isInnerParam = true;
				begParam = i + 1;
				continue;
			} 
			
			if (isInnerParam) {
				if (!DaoUtil.isVariableChar(c)) {
					isInnerParam = false;
					
					String paramName = sql.substring(begParam, i);
					if (!StringUtils.isBlank(paramName)) {
						paramNames.add(paramName);
					}
					begParam = -1;
					
				}
				continue;
			}
		}
		
		if (-1 != begParam) {
			String paramName = sql.substring(begParam, sql.length());
			if (!StringUtils.isBlank(paramName)) {
				paramNames.add(paramName);
			}
		}
		
		return paramNames;
	}
	
	/**
	 * add sql
	 * @param sql
	 * @throws Exception
	 */
	public final void addSQL(String sql) throws Exception {
		
		if (StringUtils.isNotBlank(group)) {
			sql = StringUtils.replace(sql, ":" + group, ":");
		}

		List<String> names = getTokens(sql);
		if (names.isEmpty()) { // 此语句片段没参数字段
			sqlstr.append(sql);
			return;
		} 
		
		for (String name : names) {				
			String value = param.getString(name);
			if (StringUtils.isBlank(value)) { // 参数没匹配忽略此片段
				return;
			}
		}
		
		sqlstr.append(sql);
	
	}
	
	/**
	 * get sql
	 * @return String
	 * @throws Exception
	 */
	public final String getSQL() throws Exception {
		return sqlstr.toString();
	}

	/**
	 * get param
	 * @return IData
	 * @throws Exception
	 */
	public final IData getParam() throws Exception {
		return param;
	}
	
	/**
	 * add parser
	 * @param parser
	 * @throws Exception
	 */
	public void addParser(SQLParser parser) throws Exception {
		param.putAll(parser.getParam());
		sqlstr.append(parser.getSQL());
	}
	
}