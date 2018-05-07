package com.ailk.sna.cache.memcache;

import java.util.HashSet;
import java.util.Set;

import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.sna.HttpSession;
import com.ailk.sna.SessionFactory;
import com.ailk.sna.cache.ICacheHandler;
import com.ailk.sna.config.SNACfg;
import com.ailk.sna.data.SessionData;

public class MemcacheHandler implements ICacheHandler{
	
	private String appName;
	private IMemCache client = null,clientd = null;
	
	public MemcacheHandler(String sessionCache, String sessionDataCache, String appName){
		if(sessionCache == null || "".equals(sessionCache)){
			sessionCache = SNACfg.SESSION_CACHE_NAME;
		}
		if(sessionDataCache ==null || "".equals(sessionDataCache)){
			sessionDataCache = SNACfg.SESSION_DATA_CACHE_NAME;
		}
		
		this.appName = (appName != null && !"".equals(appName)) ? appName : SNACfg.APP_NAME;
		
		client = MemCacheFactory.getCache(sessionCache);
		clientd = MemCacheFactory.getCache(sessionDataCache);
	}
	
	@Override
	public Object getSessionCacheValue(String key) {
		if(key == null || "".equals(key)) return null;
		return (client != null ? client.get(key) : null);
	}

	@Override
	public void setSessionCacheValue(String key, Object value) {
		if(key == null || "".equals(key)) return;
		if(client != null) client.set(key, value);
	}

	@Override
	public HttpSession getSessionCache(String wsid) {
		if(wsid == null || "".equals(wsid)) return null;
		Object obj = (client != null ? client.get(wsid) : null);
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
		if(wsid == null || "".equals(wsid)) return;
		if(client != null)client.touch(wsid, SessionFactory.SESSION_TIMEOUT);
	}

	@Override
	public void removeSessionCache(String wsid) {
		if(wsid == null || "".equals(wsid)) return;
		if(client != null)client.delete(wsid);
	}

	@Override
	public boolean sessionCacheExist(String wsid) {
		return client != null ? client.keyExists(wsid) : false;
	}

	@Override
	public SessionData getSessionDataCache(String sessionId) {
		if(sessionId == null || "".equals(sessionId)) return null;
		Object obj = (clientd != null ? clientd.get("SD_" + sessionId) : null);
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
		if(clientd != null)clientd.touch("SD_" + sessionId, SessionFactory.SESSION_TIMEOUT);
	}

	@Override
	public void removeSessionDataCache(String sessionId) {
		if(clientd != null)clientd.delete("SD_" + sessionId);
	}

	@Override
	public String getOnLineUniqueSetKey(){
		return "__" + appName + "_OnLineUnique_Set";
	}
	
	@Override
	public String getOnLineUniqueWSIDSetKey(String uniqueId){
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
			Object obj;
			
			Set<String> uniques = null;
			obj = client.get(cacheKey1);
			if(obj != null){
				uniques = (Set<String>)obj;
			}else{
				uniques = new HashSet<String>();
			}
			uniques.add(uniqueId);
			client.set(cacheKey1, uniques, SessionFactory.SESSION_TIMEOUT);
			
			Set<String> wsids = null;
			obj = client.get(cacheKey2);
			if(obj != null){
				wsids = (Set<String>)obj;
			}else{
				wsids = new HashSet<String>();
			}
			wsids.add(wsid);
			client.set(cacheKey2, wsids, SessionFactory.SESSION_TIMEOUT);
		}
	}

	@Override
	public void removeOnLine(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
			return;
		
		String cacheKey1 = getOnLineUniqueSetKey();
		String cacheKey2 = getOnLineUniqueWSIDSetKey(uniqueId);
		
		if(client != null){
			Object obj = client.get(cacheKey1);
			if(obj != null){
				Set<String> uniques = (Set<String>)obj;
				uniques.remove(uniqueId);
				client.set(cacheKey1, uniques, SessionFactory.SESSION_TIMEOUT);
			}
			client.delete(cacheKey2);
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
			boolean del = false;
			Object obj = client.get(cacheKey2);
			if(obj != null){
				Set<String> wsids = (Set<String>)obj;
				wsids.remove(wsid);

				if(wsids.size() <= 0){
					del = true;
					client.delete(cacheKey2);
				}else{
					client.set(cacheKey2, wsids, SessionFactory.SESSION_TIMEOUT);
				}
			}
			
			if(obj == null || del){
				obj = client.get(cacheKey1);
				if(obj != null){
					Set<String> uniques = (Set<String>)obj;
					uniques.remove(uniqueId);
					client.set(cacheKey1, uniques, SessionFactory.SESSION_TIMEOUT);
				}
			}else{
				client.touch(cacheKey1, SessionFactory.SESSION_TIMEOUT);
			}
		}
	}

	@Override
	public void activeOnLine(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
			return;
		
		String cacheKey1 = getOnLineUniqueSetKey();
		String cacheKey2 = getOnLineUniqueWSIDSetKey(uniqueId);
		
		if(client != null){
			client.touch(cacheKey1, SessionFactory.SESSION_TIMEOUT);
			client.touch(cacheKey2, SessionFactory.SESSION_TIMEOUT);
		}
	}

	@Override
	public boolean isOnLine(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
				return false;
		
		String cacheKey = getOnLineUniqueWSIDSetKey(uniqueId);

		Object obj = (client != null ? client.get(cacheKey) : null);
		if(obj != null){
			return ((Set<String>)obj).size() > 0;
		}
		return false;
	}
	
	@Override
	public boolean isOnLine(String uniqueId, String wsid) {
		if(uniqueId == null || "".equals(uniqueId)
				|| wsid == null || "".equals(wsid))
			return false;
		
		String cacheKey = getOnLineUniqueWSIDSetKey(uniqueId);

		Object obj = (client != null ? client.get(cacheKey) : null);
		if(obj != null){
			return ((Set<String>)obj).contains(wsid);
		}
		return false;
	}

	@Override
	public Set<String> getOnLineUniqueSet(){
		
		String cacheKey = getOnLineUniqueSetKey();
		
		Object obj = (client != null ? client.get(cacheKey) : null);
		if(obj != null){
			return (Set<String>)obj;
		}
		return null;
	}
	
	@Override
	public Set<String> getOnLineWSIDSet(String uniqueId) {
		if(uniqueId == null || "".equals(uniqueId))
			return null;
		
		String cacheKey = getOnLineUniqueWSIDSetKey(uniqueId);

		Object obj = (client != null ? client.get(cacheKey) : null);
		if(obj != null){
			return (Set<String>)obj;
		}
		return null;
	}
	
}