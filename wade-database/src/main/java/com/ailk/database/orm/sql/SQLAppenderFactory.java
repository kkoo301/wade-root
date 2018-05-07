/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.err.BOException;
import com.ailk.database.orm.sql.oracle.OracleSQLAppender;
import com.ailk.database.orm.util.BOUtil;
import com.ailk.database.util.DaoUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * @description
 * 根据BO对象的方言创建ISQLAppender对象。用来拼装SQL的条件逻辑。
 */
public final class SQLAppenderFactory {

	/**
	 * 根据BO的数据库方言，获取ISQLAppender对象
	 * @param entity
	 * @return
	 */
	public static <T extends BOEntity> ISQLAppender<T> create(Class<T> clazz) throws SQLException {
		String dialect = BOUtil.getDialect(clazz);
		
		try {
			if (SQLDialect.Oracle.getDialect().equals(dialect)) {
				return new OracleSQLAppender<T>(BOUtil.create(clazz, null));
			}
		} catch (BOException e) {
			throw new SQLException(e);
		}
		
		
		return null;
	}
	
	
	/**
	 * 根据BO实例生成SQL扩展器
	 * @param entity
	 * @return
	 * @throws SQLException
	 */
	public static <T extends BOEntity> ISQLAppender<T> create(T entity) throws SQLException {
		String dialect = BOUtil.getDialect(entity.getClass());
		
		if (SQLDialect.Oracle.getDialect().equals(dialect)) {
			return new OracleSQLAppender<T>(entity);
		}
		
		return null;
	}

	
	/**
	 * SQL参数解析
	 * @param colonSql
	 * @return
	 */
	public static final Object[] parseColonSql(String colonSql) {
		char[] sqlchar = colonSql.toCharArray();
		
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
					
					String paramName = colonSql.substring(begParam, i);
					if (!StringUtils.isBlank(paramName)) {
						paramNames.add(paramName.toUpperCase());
					}
					begParam = -1;
					
					sb.append(c);
				}
				continue;
			} 

			sb.append(c);

		}
		
		if (-1 != begParam) {
			String paramName = colonSql.substring(begParam, sqlchar.length);
			if (!StringUtils.isBlank(paramName)) {
				paramNames.add(paramName.toUpperCase());
			}
		}
		
		return new Object[] {sb.toString(), paramNames};
	}
}
