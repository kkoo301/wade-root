package com.ailk.sna.config;

import java.util.HashMap;
import java.util.Map;

import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.common.config.PropertiesConfig;

public class SNACfg {
	public static final String CACHE_MEMCACHE = "memcache";
	public static final String CACHE_REDIS = "redis";
	
	public static final String SESSION_ID_FROM_URL= "http-url";
	public static final String SESSION_ID_FROM_COOKIE = "http-cookie";
	
	private static SNACfg cfg = null;
	private static Map<String, String> data = new HashMap<String, String>(50);
	
	private static final String KEY_SESSION_CACHE = "sna.session.cache";
	private static final String KEY_SESSION_CACHE_NAME = "sna.session.cache.name";
	private static final String KEY_SESSION_ID_FROM = "sna.session.id.from";
	private static final String KEY_APP_NAME = "sna.app.name";
	private static final String KEY_SESSION_ID_NAME = "sna.session.id.name";
	private static final String KEY_COOKIE_NAME = "sna.cookie.name";
	private static final String KEY_COOKIE_ENCRYPTKEY = "sna.cookie.encryptkey";
	private static final String KEY_SESSION_TIMEOUT = "sna.session.timeout";
	private static final String KEY_SESSION_DATA_CACHE_ENABLE = "sna.session.datacache.enable";
	private static final String KEY_SESSION_DATA_CACHE_NAME = "sna.session.datacache.name";
	private static final String KEY_REQUEST_TAG_NAME = "sna.request.tag.name";
	private static final String KEY_REQUEST_TAG_VALUE = "sna.request.tag.value";
	private static final String KEY_SESSION_ID_FROM_URL_TAG_NAME = "sna.session.id.from.http-url.tag.name";
	private static final String KEY_SESSION_ID_FROM_URL_TAG_VALUE = "sna.session.id.from.http-url.tag.value";
	
	private static final String DEFAULT_SESSION_CACHE= CACHE_MEMCACHE; // redis|memcached for cache type
	private static final String DEFAULT_MEMCACHE_SESSION_CACHE_NAME = MemCacheFactory.SESSION_CACHE;
	private static final String DEFAULT_SESSION_ID_FROM = SESSION_ID_FROM_COOKIE; //http-parameter|http-cookie
	private static final String DEFAULT_REDIS_SESSION_CACHE_NAME = "sna";
	private static final String DEFAULT_APP_NAME = "SNA_APP";
	private static final String DEFAULT_SESSION_ID_NAME = "SESSIONID";
	private static final String DEFAULT_COOKIE_NAME = "WADE_SID";
	private static final String DEFAULT_COOKIE_ENCRYPTKEY = "wade_sna_encryptkey";
	private static final String DEFAULT_SESSION_TIMEOUT = "1800";        //默认为30分钟
	private static final String DEFAULT_SESSION_DATA_CACHE_ENABLE = "false";
	private static final String DEFAULT_MEMCACHE_SESSION_DATA_CACHE_NAME = MemCacheFactory.SESSION_CACHE;
	private static final String DEFAULT_REDIS_SESSION_DATA_CACHE_NAME = "sna";
	private static final String DEFAULT_REQUEST_TAG_NAME = "WADE-SNA-REQ-TAG";
	private static final String DEFAULT_REQUEST_TAG_VALUE = "true";
	private static final String DEFAULT_SESSION_ID_FROM_URL_TAG_NAME = "SESSION-ID-FROM-URL";
	private static final String DEFAULT_SESSION_ID_FROM_URL_TAG_VALUE = "true";
	
	static {
		cfg = getInstance();
	}

	private SNACfg() {

	}

	public static synchronized SNACfg getInstance() {
		if (cfg == null) {
			cfg = new SNACfg();

			try {
				PropertiesConfig cfg = new PropertiesConfig("sna.properties");
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
	
	public static final String SESSION_CACHE = getProperty(KEY_SESSION_CACHE, DEFAULT_SESSION_CACHE);
	
	public static final String SESSION_CACHE_NAME = getProperty(KEY_SESSION_CACHE_NAME, CACHE_MEMCACHE.equals(SESSION_CACHE)
														?DEFAULT_MEMCACHE_SESSION_CACHE_NAME
														:DEFAULT_REDIS_SESSION_CACHE_NAME);
	
	public static final String SESSION_ID_FROM = getProperty(KEY_SESSION_ID_FROM, DEFAULT_SESSION_ID_FROM);
	
	public static final String APP_NAME = getProperty(KEY_APP_NAME, DEFAULT_APP_NAME);
	
	public static final String SESSION_ID_NAME = getProperty(KEY_SESSION_ID_NAME, DEFAULT_SESSION_ID_NAME);
	
	public static final String COOKIE_NAME = getProperty(KEY_COOKIE_NAME, DEFAULT_COOKIE_NAME);

	public static final String COOKIE_ENCRYPTKEY = getProperty(KEY_COOKIE_ENCRYPTKEY, DEFAULT_COOKIE_ENCRYPTKEY);

	public static final int SESSION_TIMEOUT = Integer.parseInt(getProperty(KEY_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT));
	
	public static final boolean SESSION_DATA_CACHE_ENABLE = "true".equals(getProperty(KEY_SESSION_DATA_CACHE_ENABLE, DEFAULT_SESSION_DATA_CACHE_ENABLE));
	
	public static final String SESSION_DATA_CACHE_NAME = getProperty(KEY_SESSION_DATA_CACHE_NAME,CACHE_MEMCACHE.equals(SESSION_CACHE) 
															? DEFAULT_MEMCACHE_SESSION_DATA_CACHE_NAME
															: DEFAULT_REDIS_SESSION_DATA_CACHE_NAME);

	public static final boolean IS_SESSION_ID_FROM_URL = SESSION_ID_FROM_URL.equals(SESSION_ID_FROM);
	
	public static final String REQUEST_TAG_NAME = getProperty(KEY_REQUEST_TAG_NAME, DEFAULT_REQUEST_TAG_NAME);

	public static final String REQUEST_TAG_VALUE = getProperty(KEY_REQUEST_TAG_VALUE, DEFAULT_REQUEST_TAG_VALUE);
	
	public static final String SESSION_ID_FROM_URL_TAG_NAME = getProperty(KEY_SESSION_ID_FROM_URL_TAG_NAME, DEFAULT_SESSION_ID_FROM_URL_TAG_NAME);
	
	public static final String SESSION_ID_FROM_URL_TAG_VALUE = getProperty(KEY_SESSION_ID_FROM_URL_TAG_VALUE, DEFAULT_SESSION_ID_FROM_URL_TAG_VALUE);
}