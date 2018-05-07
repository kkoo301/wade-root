package com.ailk.database.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ailk.org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unchecked")
public final class DatabaseCfg {
	
	private static transient Logger log = Logger.getLogger(DatabaseCfg.class);
	
	/**
	 * 数据源配置文件
	 */
	private static final String DATABASE_XML = "database.xml";
	
	/**
	 * 数据库方言
	 */
	private static String DIALECT = "com.ailk.database.dialect.impl.OracleDialect";
	
	/**
	 * 数据源配置集合类
	 */
	private static Map<String, Map<String, String>> datasources = new HashMap<String, Map<String, String>>();
	
	/**
	 * 数据库查询结果集大小限制筏值
	 */
	private static int limit;
	
	/**
	 * 是否开启跟踪
	 */
	private static boolean trace = false;
	
	private static String DB_CONF_CONN = null;
	private static String DB_CONF_PWD = null;
	
	private static boolean USE_DTM = false;
	
	private static String session = null;
	
	private static String version = "wade";
	
	private static boolean testOnBorrow = false;
	
	private DatabaseCfg() {
		
	}
		
	public static final Map<String, String> getDBConfig(String name) {
		return datasources.get(name);
	}
	
	
	public static final Map<String, Map<String, String>> getAllDBConfig() {
		return datasources;
	}
	
	/**
	 * 获取数据库方言实现类
	 * 
	 * @return
	 */
	public static final String getDialect() {
		return DIALECT;
	}
		
	/**
	 * 获取最大数据集限制值
	 * 
	 * @return int
	 */
	public static final int getLimit() {
		return limit;
	}
	
	/**
	 * get connector
	 * @return
	 */
	public static String getConnector() {
		return DB_CONF_CONN;
	}
	
	/**
	 * get pwd creator
	 * @return
	 */
	public static String getPwdCreator() {
		return DB_CONF_PWD;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isTrace() {
		return trace;
	}
	
	/**
	 * 是否采用DTM管理事务
	 * @return
	 */
	public static boolean useDTM() {
		return USE_DTM;
	}
	
	public static String getSession() {
		return session;
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static boolean isTestOnBorrow() {
		return testOnBorrow;
	}
	
	private static Element getRoot(String file) throws FileNotFoundException, DocumentException, IOException {
		InputStream in = null;
		try {
			in = DatabaseCfg.class.getClassLoader().getResourceAsStream(file);
			if (in == null) {
				throw new FileNotFoundException(file);
			}
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			return root;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (DocumentException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}
	
	static {
		Element root = null;
		try {
			root = getRoot(DATABASE_XML);
		} catch (Exception e) {
			log.error(DATABASE_XML + "数据库配置文件初始化出错!", e);
		}

		// 数据库方言
		DIALECT = root.attributeValue("dialect");
		if (null == DIALECT) { // 默认数据库方言为ORACLE
			DIALECT = "com.ailk.database.dialect.impl.OracleDialect";
		}
				
		//数据库连接器
		DB_CONF_CONN = root.attributeValue("connector");
		if (null == DB_CONF_CONN || DB_CONF_CONN.length() <= 0) {
			DB_CONF_CONN = "com.ailk.database.dbconn.impl.SimpleConnectionManager";
		}
		log.info("数据库连接管理类:" + DB_CONF_CONN);
		
		//数据库连接密码加密器
		DB_CONF_PWD = root.attributeValue("pwdcreator");
		if (null == DB_CONF_PWD || DB_CONF_PWD.length() <= 0) {
			DB_CONF_PWD = "com.ailk.database.dbconn.impl.DefaultDBPasswordCreator";
		}
		log.info("数据库密码管理类:" + DB_CONF_PWD);
		
		
		//数据库最大记录限制
		String limit = root.attributeValue("limit");
		if (limit != null && limit.length() > 0 && StringUtils.isNumeric(limit)) {
			DatabaseCfg.limit = Integer.parseInt(limit);
		} else {
			DatabaseCfg.limit = 100000;
		}
		log.info("数据库查询结果集限制:" + DatabaseCfg.limit);
		
		//数据源跟踪
		String trace = root.attributeValue("trace");
		if (trace != null && trace.length() > 0 && StringUtils.isNumeric(trace)) {
			DatabaseCfg.trace = "true".equals(trace);
		} else {
			DatabaseCfg.trace = false;
		}
		log.info("开启数据源跟踪:" + DatabaseCfg.trace);
		
		//分布式事务管理
		String usedtm = root.attributeValue("usedtm");
		if ("true".equals(usedtm)) {
			DatabaseCfg.USE_DTM = true;
		} else {
			DatabaseCfg.USE_DTM = false;
		}
		log.info("采用DTM管理事务:" + DatabaseCfg.USE_DTM);
		
		String session = root.attributeValue("session");
		if (null == session) {
			DatabaseCfg.session = "com.ailk.database.dao.impl.DefaultDAOSession";
		} else {
			DatabaseCfg.session = session;
		}
		
		String version = root.attributeValue("version");
		if (null == version || version.length() == 0) {
			DatabaseCfg.version = "wade";
		} else {
			DatabaseCfg.version = version;
		}
		
		String testOnBorrow = root.attributeValue("testOnBorrow");
		if ("true".equals(testOnBorrow)) {
			DatabaseCfg.testOnBorrow = true;
		} else {
			DatabaseCfg.testOnBorrow = false;
		}
		
		
		List<Element> elems = root.selectNodes("*");
		for(Element e : elems) {
			String name = e.getName();
			
			Map<String, String> entity = new HashMap<String, String>();
			List<Attribute> attrs = e.attributes();
			for (Attribute attr : attrs) {
				entity.put(attr.getName(), attr.getValue());
			}
			
			entity.put("name", name);
			datasources.put(name, entity);
			
			String alias = entity.get("alias");
			if (alias != null && !"".equals(alias)) {
				datasources.put(alias, entity);
			}
			
		}
		
		log.info("读取数据库连接配置" + datasources.keySet());
		
	}
	
}
