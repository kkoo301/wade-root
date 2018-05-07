package com.ailk.sna.cache.redis;

import java.util.Set;

import com.ailk.cache.redis.RedisFactory;
import com.ailk.cache.redis.client.RedisClient;
import com.ailk.sna.HttpSession;
import com.ailk.sna.SessionFactory;
import com.ailk.sna.cache.ICacheHandler;
import com.ailk.sna.config.SNACfg;
import com.ailk.sna.data.SessionData;

public class RedisHandler implements ICacheHandler{

	private String appName;
	RedisClient client, clientd;
	
	public RedisHandler(String sessionCache, String sessionDataCache, String appName){
		if(sessionCache == null || "".equals(sessionCache)){
			sessionCache = SNACfg.SESSION_CACHE_NAME;
		}
		if(sessionDataCache ==null || "".equals(sessionDataCache)){
			sessionDataCache = SNACfg.SESSION_DATA_CACHE_NAME;
		}
		
		this.appName = (appName != null && !"".equals(appName)) ? appName : SNACfg.APP_NAME;
			
		client = RedisFactory.getRedisClient(sessionCache);
		clientd = RedisFactory.getRedisClient(sessionDataCache);
	}

	@Override
	public Object getSessionCacheValue(String key) {
		if(key == null || "".equals(key)) return null;
		return (client != null ? client.getObject(key) : null);
	}

	@Override
	public void setSessionCacheValue(String key, Object value) {
		if(key == null || "".equals(key)) return;
		if(client != null) client.set(key, value);
	}

	@Override
	public HttpSession getSessionCache(String wsid) {
		if(wsid == null || "".equals(wsid)) return null;
		Object obj = (client != null ? client.getObject(wsid) : null);
		if(obj != null){
			return (HttpSession)obj;
		}
		return null;
	}

	@Override
	public void setSessionCache(String wsid, HttpSession session) {
		if(wsid == null || "".equals(wsid)) return;
		if(client != null)client.set(wsid, session , SessionFactory.SESSION_TIMEOUT);
	}

	@Override
	public void activeSessionCache(String wsid) {
		if(wsid==null || "".equals(wsid)) return;
		if(client != null)client.expire(wsid, SessionFactory.SESSION_TIMEOUT);
	}

	@Override
	public void removeSessionCache(String wsid) {
		if(wsid == null || "".equals(wsid)) return;
		if(client != null)client.del(wsid);
	}

	@Override
	public boolean sessionCacheExist(String wsid) {
		return client != null ? client.exists(wsid) : false;
	}

	@Override
	public SessionData getSessionDataCache(String sessionId) {
		if(sessionId == null || "".equals(sessionId)) return null;
		Object obj = (clientd != null ? clientd.getObject("SD_" + sessionId) : null);
		if(obj != null){
			return (SessionData)obj;
		}
		return null;
	}

	@Override
	public void setSessionDataCache(String sessionId, SessionData data) {
		if(clientd != null)clientd.set("SD_" + sessionId, data, SessionFactory.SESSION_TIMEOUT);
	}

	@Override
	public void activeSessionDataCache(String sessionId) {
		if(sessionId == null || "".equals(sessionId)) return;
		if(clientd != null)clientd.expire("SD_" + sessionId, SessionFactory.SESSION_TIMEOUT);
	}

	@Override
	public void removeSessionDataCache(String sessionId) {
		if(clientd != null)clientd.del("SD_" + sessionId);
	}

	@Override
	public String getOnLineUniqueSetKey(){
		return "__" + appName + "_OnLineUnique_Set";
	}
	
	@Override
	public String getOnLineUniqueWSIDSetKey(String uniqueId) {
		return "__" + appName + "_OnLineWSID_Set_" + uniqueId;
	}

	@Override
	public void addOnLine(String uniqueId, String wsid) {
		if( uniqueId == null || "".equals(uniqueId)
				|| wsid == null || "".equals(wsid) )
			return;
		
		String cacheKey1 = getOnLineUniqueSetKey();
		String cacheKey2 = getOnLineUniqueWSIDSetKey(uniqueId);
		
		if(client != null){
			client.sadd(cacheKey1, uniqueId);
			client.expire(cacheKey1, SessionFactory.SESSION_TIMEOUT);
			
			client.sadd(cacheKey2, wsid);
			client.expire(cacheKey2, SessionFactory.SESSION_TIMEOUT);
		}
	}

	@Override
	public void removeOnLine(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
			return;
		
		String cacheKey1 = getOnLineUniqueSetKey();
		String cacheKey2 = getOnLineUniqueWSIDSetKey(uniqueId);
		
		if(client != null){ 
			
			client.srem(cacheKey1, uniqueId);
			client.expire(cacheKey1, SessionFactory.SESSION_TIMEOUT);
			
			client.del(cacheKey2);
		}
	}

	@Override
	public void removeOnLine(String uniqueId, String wsid) {
		if(uniqueId == null || "".equals(uniqueId)
				|| wsid == null || "".equals(wsid))
			return;
		
		String cacheKey1 = getOnLineUniqueSetKey();
		String cacheKey2 = getOnLineUniqueWSIDSetKey(uniqueId);
		
		if(client != null){
			
			client.srem(cacheKey2, wsid);
			
			long count = client.scard(cacheKey2);
			if(count <= 0){
				client.srem(cacheKey1, uniqueId);
				client.expire(cacheKey1, SessionFactory.SESSION_TIMEOUT);
				
				client.del(cacheKey2);
			}else{
				client.expire(cacheKey2, SessionFactory.SESSION_TIMEOUT);
			}
			
			client.expire(cacheKey1, SessionFactory.SESSION_TIMEOUT);
		}
	}

	@Override
	public void activeOnLine(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
			return;
		
		String cacheKey1 = getOnLineUniqueSetKey();
		String cacheKey2 = getOnLineUniqueWSIDSetKey(uniqueId);
		
		if(client != null){
			client.expire(cacheKey1, SessionFactory.SESSION_TIMEOUT);
			client.expire(cacheKey2, SessionFactory.SESSION_TIMEOUT);
		}
	}

	@Override
	public boolean isOnLine(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
			return false;
		
		String cacheKey = getOnLineUniqueWSIDSetKey(uniqueId);
		if(client != null){
			return client.scard(cacheKey) > 0;
		}
		return false;
	}
	
	@Override
	public boolean isOnLine(String uniqueId, String wsid) {
		if(uniqueId == null || "".equals(uniqueId)
				|| wsid == null || "".equals(wsid))
			return false;
		
		String cacheKey = getOnLineUniqueWSIDSetKey(uniqueId);
		if(client != null){
			return client.sismember(cacheKey, wsid);
		}
		return false;
	}
	
	@Override 
	public Set<String> getOnLineUniqueSet(){
		String cacheKey = getOnLineUniqueSetKey();
		if(client != null){
			return client.smembers(cacheKey);
		}
		return null;
	}

	@Override
	public Set<String> getOnLineWSIDSet(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
			return null;
		String cacheKey = getOnLineUniqueWSIDSetKey(uniqueId);
		if(client != null){
			return client.smembers(cacheKey);
		}
		return null;
	}
}