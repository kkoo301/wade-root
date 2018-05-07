package com.ailk.sna.cache;

import java.util.Set;

import com.ailk.sna.HttpSession;
import com.ailk.sna.data.SessionData;

public interface ICacheHandler{
	
	public Object getSessionCacheValue(String key);
	
	public void setSessionCacheValue(String key, Object value);
	
	public HttpSession getSessionCache(String wsid);
	
	public void setSessionCache(String wsid,HttpSession session);
	
	public void activeSessionCache(String wsid);
	
	public void removeSessionCache(String wsid);
	
	public boolean sessionCacheExist(String wsid);
	
	public SessionData getSessionDataCache(String sessionId);
	
	public void setSessionDataCache(String sessionId, SessionData data);
	
	public void activeSessionDataCache(String sessionId);
	
	public void removeSessionDataCache(String sessionId);
	
	public String getOnLineUniqueSetKey();
	
	public String getOnLineUniqueWSIDSetKey(String uniqueId);
	
	public void addOnLine(String uniqueId , String wsid);
	
	public void removeOnLine(String uniqueId);
	
	public void removeOnLine(String uniqueId, String wsid);
	
	public void activeOnLine(String uniqueId);
	
	public boolean isOnLine(String uniqueId); 
	
	public boolean isOnLine(String uniqueId, String wsid);
	
	public Set<String> getOnLineUniqueSet();
	
	public Set<String> getOnLineWSIDSet(String uniqueId);
}