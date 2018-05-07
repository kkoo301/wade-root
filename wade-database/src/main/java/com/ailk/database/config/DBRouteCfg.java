package com.ailk.database.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class DBRouteCfg {
	
	private static transient Logger log = Logger.getLogger(DBRouteCfg.class);
	
	/**
	 * 数据库路由配置文件
	 */
	private static final String DBROUTE_FILE = "dbroute.properties";
	
	/**
	 * 路由配置前缀
	 */
	private static final String ROUTE_PREFIX = "route.";
	
	/**
	 * 路由组配置前缀
	 */
	private static final String ROUTE_GROUP_PREFIX = ROUTE_PREFIX + "group.";
	
	/**
	 * 分表规则的前缀
	 */
	private static final String SUB_TABLE_RULE = "rule.subtable.";
	
	/**
	 * 路由配置集合
	 */
	private static Map<String, String> data = new HashMap<String, String>();
	
	private DBRouteCfg() {
	}

	/**
	 * 获取默认路由
	 * 
	 * @param name
	 * @return
	 */
	public static String getDefault(String name) {
		return data.get(ROUTE_PREFIX + getGroup(name) + ".def");
	}

	/**
	 * 获取路由组
	 * 
	 * @param name
	 * @return
	 */
	public static String getGroup(String name) {
				
		String key = ROUTE_GROUP_PREFIX + name;
		String routeGrp = data.get(key);
		if (null == routeGrp) {
			throw new IllegalArgumentException("获取路由组为空! " + key);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("获取数据库路由组:" + routeGrp);
		}
		
		return routeGrp;
	}

	public static String getRoute(String routeGrp, String routeId) {
		
		if (null == routeGrp || routeGrp.length() <= 0) {
			throw new NullPointerException("数据库路由组不能为空");
		}
		
		if (null == routeId || routeId.length() <= 0) {
			throw new NullPointerException("数据库路由编码不能为空");
		}
		
		String routePath = ROUTE_PREFIX + routeGrp + "." + routeId;
		
		if (log.isDebugEnabled()) {
			log.debug("获取数据库路由地址:" + routePath);
		}
		
		return data.get(routePath);
	}
	
	/**
	 * 按表名获取分表规则
	 * @param table
	 * @return
	 */
	public static String getSubTableRule(String table) {
		if (null == table || table.length() == 0) {
			return null;
		}
		
		return data.get(SUB_TABLE_RULE + table);
	}
	
	static {
		Properties props = new Properties();

		try {
			props.load(DBRouteCfg.class.getClassLoader().getResourceAsStream(DBROUTE_FILE));
			
			Enumeration<Object> e = props.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = new String(props.getProperty(key).getBytes("ISO-8859-1"));
				data.put(key, value.trim());
			}
			
		} catch (IOException e) {
			log.error(DBROUTE_FILE + "路由配置文件加载错误!", e);
		}
	}
	
}