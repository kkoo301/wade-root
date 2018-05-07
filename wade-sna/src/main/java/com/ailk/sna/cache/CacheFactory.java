package com.ailk.sna.cache;

import com.ailk.sna.cache.memcache.MemcacheHandler;
import com.ailk.sna.cache.redis.RedisHandler;
import com.ailk.sna.config.SNACfg;

public class CacheFactory{

	public static ICacheHandler getHandler(){
		return getHandler(null);
	}
	
	public static ICacheHandler getHandler(String sessionCache){	
		return getHandler(sessionCache, null);
	}
	
	public static ICacheHandler getHandler(String sessionCache, String appName){
		return getHandler(sessionCache, null, appName);
	}
	
	public static ICacheHandler getHandler(String sessionCache, String sessionDataCache, String appName){
		String cache = SNACfg.SESSION_CACHE;
		if(SNACfg.CACHE_MEMCACHE.equals(cache)){
			return new MemcacheHandler(sessionCache, sessionDataCache, appName);
		}else if(SNACfg.CACHE_REDIS.equals(cache)){
			return new RedisHandler(sessionCache, sessionDataCache, appName);
		}
		return null;
	}
}