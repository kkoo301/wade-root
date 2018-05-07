package com.wade.message.comet.server;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.config.PropertiesConfig;

public class Config {
	
	private static Config cfg = null;
	private static Map<String, String> data = new HashMap<String, String>(50);
	
	private static final String KEY_COMET_CHARSET = "comet.charset";
	private static final String KEY_COMET_CONNECTION_TIMEOUT = "comet.connection.timeout";
	private static final String KEY_COMET_CONNECTION_HEARTBEAT = "comet.connection.heartbeat";
	private static final String KEY_COMET_SERVER_WORKER_CLAZZ = "comet.server.worker.clazz";
	private static final String KEY_COMET_SERVER_HTTP_PUSH = "comet.server.http.push";
	private static final String KEY_COMET_SERVER_USEMQ = "comet.server.usemq";
	private static final String KEY_COMET_SERVER_ALLOW_ORIGIN = "comet.server.allow.origin";
	private static final String KEY_COMET_SERVER_ACL = "comet.server.acl";
	
	private static final String DEFAULT_COMET_CHARSET = "utf-8";
	private static final String DEFAULT_COMET_CONNECTION_TIMEOUT = "300";  //秒  该值必须大于心跳时间
	private static final String DEFAULT_COMET_CONNECTION_HEARTBEAT = "30";  //秒 
	private static final String DEFAULT_COMET_SERVER_WORKER_CLAZZ = "com.wade.message.comet.server.impl.Worker";    
	private static final String DEFAULT_COMET_SERVER_HTTP_PUSH = "false";
	private static final String DEFAULT_COMET_SERVER_USEMQ = "false";
	private static final String DEFAULT_COMET_SERVER_ALLOW_ORIGIN = "*";
	
	static {
		cfg = getInstance();
	}
	
	public static synchronized Config getInstance(){
		if (cfg == null) {
			cfg = new Config();
			
			try {
				PropertiesConfig cfg = new PropertiesConfig("comet-server.properties");
				data = cfg.getProperties();
			}catch (Exception e){
				
			}
		}
		
		return cfg;
	}
	
	private static String getProperty(String name) {
		if (data.containsKey(name)) {
			String value = data.get(name);
			return value != null ? value : null;
		}
		return null;
	}

	private static String getProperty(String name, String defval) {
		String value = getProperty(name);
		if (value == null) {
			return defval;
		} else {
			return value;
		}
	}
	
	public static final int RESULT_CODE_DATA = 2;
	public static final int RESULT_CODE_OK = 1;
	public static final int RESULT_CODE_CONTINUE = 0;
	public static final int RESULT_CODE_ERROR = -1;
	public static final int RESULT_CODE_RETRY = -2;
	public static final int RESULT_CODE_STOP = -3;
	
	public static final Charset    COMET_CHARSET = Charset.forName(getProperty(KEY_COMET_CHARSET, DEFAULT_COMET_CHARSET));
	public static final long       COMET_CONNECTION_TIMEOUT = Integer.parseInt(getProperty(KEY_COMET_CONNECTION_TIMEOUT, DEFAULT_COMET_CONNECTION_TIMEOUT)) * 1000L;
	public static final long       COMET_CONNECTION_HEARTBEAT = Integer.parseInt(getProperty(KEY_COMET_CONNECTION_HEARTBEAT, DEFAULT_COMET_CONNECTION_HEARTBEAT)) * 1000L;
	public static final String     COMET_SERVER_WORKER_CLAZZ = getProperty(KEY_COMET_SERVER_WORKER_CLAZZ, DEFAULT_COMET_SERVER_WORKER_CLAZZ);
	public static final boolean    COMET_SERVER_HTTP_PUSH = "true".equals(getProperty(KEY_COMET_SERVER_HTTP_PUSH, DEFAULT_COMET_SERVER_HTTP_PUSH));
	public static final boolean    COMET_SERVER_USEMQ = "true".equals(getProperty(KEY_COMET_SERVER_USEMQ, DEFAULT_COMET_SERVER_USEMQ));
	public static final String     COMET_SERVER_ALLOW_ORIGIN = getProperty(KEY_COMET_SERVER_ALLOW_ORIGIN, DEFAULT_COMET_SERVER_ALLOW_ORIGIN);
	public static final String     COMET_SERVER_ACL = getProperty(KEY_COMET_SERVER_ACL);
	
}