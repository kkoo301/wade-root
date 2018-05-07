/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月5日
 * 
 * Just Do IT.
 */
package com.ailk.database.rule;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.config.DBRouteCfg;

/**
 * @description
 * 分表规则上下文对象
 */
public final class TableRuleContext {
	private static final Logger log = LoggerFactory.getLogger(TableRuleContext.class);
	
	/**
	 * 缓存分表规则对象，避免每次都要newInstance
	 */
	private static Map<String, ISubTableRule> rules = new HashMap<String, ISubTableRule>(10);
	
	/**
	 * 基于线程的上下文对象
	 */
	private static ThreadLocal<Map<String, String>> context = new ThreadLocal<Map<String,String>>() {
		protected Map<String,String> initialValue() {
			return new HashMap<String, String>(10);
		}
	};
	
	/**
	 * 设置上下文属性
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		context.get().put(key, value);
	}

	/**
	 * 获取上下文属性
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return context.get().get(key);
	}
	
	/**
	 * 根据表名获取分表规则对象
	 * @param tableName
	 * @return	dbroute.properties里未配置时则返回null
	 * @throws SQLException
	 */
	public static ISubTableRule getRule(String tableName) throws SQLException {
		if (null == tableName || tableName.trim().length() == 0) {
			throw new SQLException(String.format("获取分表规则异常，表名不能为空", tableName));
		}
		String ruleName = DBRouteCfg.getSubTableRule(tableName.trim().toUpperCase());
		
		ISubTableRule rule = rules.get(ruleName);
		if (null == rule) {
			
			synchronized (rules) {
				
				rule = rules.get(ruleName);
				if (null == rule) {
					
					log.debug("创建分表规则对象，表名：{}，规则实现：{}", new String[] {tableName, ruleName});
					
					rule = createRule(ruleName);
					rules.put(ruleName, rule);
				}
			}
		}
		
		if (null == rule) {
			throw new SQLException(String.format("获取分表规则异常：%s", tableName));
		}
		
		return rule;
	}
	
	/**
	 * 创建分表规则对象
	 * @param ruleClass
	 * @return
	 * @throws SQLException
	 */
	private static ISubTableRule createRule(String ruleClass) throws SQLException {
		try {
			ISubTableRule rule = (ISubTableRule) Class.forName(ruleClass).newInstance();
			return rule;
		} catch (Exception e) {
			throw new SQLException("创建分表规则实例异常：" + ruleClass, e);
		}
		
	}
}
