package com.wade.message.websocket.server;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.config.PropertiesConfig;

public class Config {
	
	private static Config cfg = null;
	private static Map<String, String> data = new HashMap<String, String>(50);
	
	private static final String KEY_WEBSOCKET_CHARSET = "websocket.charset";
	private static final String KEY_WEBSOCKET_CONNECTION_TIMEOUT = "websocket.connection.timeout";
	private static final String KEY_WEBSOCKET_CONNECTION_HEARTBEAT = "websocket.connection.heartbeat";
	private static final String KEY_WEBSOCKET_SERVER_WORKER_CLAZZ = "websocket.server.worker.clazz";
	private static final String KEY_WEBSOCKET_SERVER_HTTP_PUSH = "websocket.server.http.push";
	private static final String KEY_WEBSOCKET_SERVER_USEMQ = "websocket.server.usemq";
	private static final String KEY_WEBSOCKET_SERVER_ACL = "websocket.server.acl";
	
	private static final String DEFAULT_WEBSOCKET_CHARSET = "utf-8";
	private static final String DEFAULT_WEBSOCKET_CONNECTION_TIMEOUT = "30";
	private static final String DEFAULT_WEBSOCKET_CONNECTION_HEARTBEAT = "120";  //秒 
	private static final String DEFAULT_WEBSOCKET_SERVER_WORKER_CLAZZ = "com.wade.message.websocket.server.impl.Worker";
	private static final String DEFAULT_WEBSOCKET_SERVER_HTTP_PUSH = "false";
	private static final String DEFAULT_WEBSOCKET_SERVER_USEMQ = "false";
	
	static {
		cfg = getInstance();
	}
	
	public static synchronized Config getInstance(){
		if (cfg == null) {
			cfg = new Config();
			
			try {
				PropertiesConfig cfg = new PropertiesConfig("websocket-server.properties");
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
	
	public static final Charset  WEBSOCKET_CHARSET = Charset.forName(getProperty(KEY_WEBSOCKET_CHARSET, DEFAULT_WEBSOCKET_CHARSET));
	public static final long     WEBSOCKET_CONNECTION_TIMEOUT = Integer.parseInt(getProperty(KEY_WEBSOCKET_CONNECTION_TIMEOUT, DEFAULT_WEBSOCKET_CONNECTION_TIMEOUT)) * 1000L;
	public static final long     WEBSOCKET_CONNECTION_HEARTBEAT = Integer.parseInt(getProperty(KEY_WEBSOCKET_CONNECTION_HEARTBEAT, DEFAULT_WEBSOCKET_CONNECTION_HEARTBEAT)) * 1000L;
	public static final String   WEBSOCKET_SERVER_WORKER_CLAZZ = getProperty(KEY_WEBSOCKET_SERVER_WORKER_CLAZZ, DEFAULT_WEBSOCKET_SERVER_WORKER_CLAZZ);
	public static final boolean  WEBSOCKET_SERVER_HTTP_PUSH = "true".equals(getProperty(KEY_WEBSOCKET_SERVER_HTTP_PUSH, DEFAULT_WEBSOCKET_SERVER_HTTP_PUSH));
	public static final boolean  WEBSOCKET_SERVER_USEMQ = "true".equals(getProperty(KEY_WEBSOCKET_SERVER_USEMQ, DEFAULT_WEBSOCKET_SERVER_USEMQ));
	public static final String   WEBSOCKET_SERVER_ACL = getProperty(KEY_WEBSOCKET_SERVER_ACL);
}