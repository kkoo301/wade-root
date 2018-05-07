package com.ailk.sna;

import java.util.Set;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.sna.cache.CacheFactory;
import com.ailk.sna.cache.ICacheHandler;
import com.ailk.sna.config.SNACfg;
import com.ailk.sna.data.SessionData;

public class SessionFactory {
	
	public static final String SESSION_ID_NAME = SNACfg.SESSION_ID_NAME;
	public static final String COOKIE_NAME = SNACfg.COOKIE_NAME;
	public static final String COOKIE_ENCRYPT_KEY = SNACfg.COOKIE_ENCRYPTKEY;
	public static final int SESSION_TIMEOUT = SNACfg.SESSION_TIMEOUT;
	
	private static SessionFactory _factory = null;
	
	private static ThreadLocal<HttpSession> _session = new ThreadLocal<HttpSession>();
	private static ThreadLocal<SessionData> _sessionData = new ThreadLocal<SessionData>();
	
	private static ICacheHandler _handler = null;
	
	private SessionFactory(){
		this(null);
	}
	
	private SessionFactory(String sessionCache){
		this(sessionCache, null);
	}
	
	private SessionFactory(String sessionCache, String appName){
		this(sessionCache, null, appName);
	}
	
	private SessionFactory(String sessionCache, String sessionDataCache, String appName){
		_handler = CacheFactory.getHandler(sessionCache, sessionDataCache, appName);
	}
	
	public static SessionFactory getInstance(){
		if( null == _factory ){
			synchronized(SessionFactory.class){
				if( null == _factory ){
					_factory = new SessionFactory();
				}
			}
		}
		return _factory;
	}
	
	public static SessionFactory getInstance(String sessionCache){
		return new SessionFactory(sessionCache);
	}
	
	public static SessionFactory getInstance(String sessionCache, String appName){
		return new SessionFactory(sessionCache, appName);
	}
	
	public static SessionFactory getInstance(String sessionCache, String sessionDataCache, String appName){
		return new SessionFactory(sessionCache, sessionDataCache, appName);
	}
	
	public HttpSession getSession(){
		return _session.get();
	}
	
	public void setSession(HttpSession session){
		_session.set(session);
	}
	
	public void clearSesson(){
		_session.remove();
	}
	
	public SessionData getSessionData(){
		return _sessionData.get();
	}
	
	public void setSessionData(SessionData sessionData){
		_sessionData.set(sessionData);
	}
	
	public void clearSessionData(){
		_sessionData.remove();
	}
	
/*	private static String encode(String str){
		try {
			return URLEncoder.encode(DESUtil.encrypt(COOKIE_ENCRYPT_KEY,str,false),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	private static String decode(String str){
		try {
			return DESUtil.decrypt(COOKIE_ENCRYPT_KEY,URLDecoder.decode(str,"UTF-8"),false);
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		return null;
	}*/
	
	//获取随机WSID
	public static String getRandomID(){
		return ((UUID.randomUUID().toString()).replace("-", "")).toUpperCase();
	}
	
	//判断是否从URL中取SessionID
	public static boolean isSessionIdFromUrl(HttpServletRequest request){
		return SNACfg.SESSION_ID_FROM_URL_TAG_VALUE.equals(request.getAttribute(SNACfg.REQUEST_TAG_NAME + ":" + SNACfg.SESSION_ID_FROM_URL_TAG_NAME));
	}
	
	//从URL参数中获取数据
	public static String getSessionIdFromUrl(HttpServletRequest request){
		String q = request.getQueryString();
		if(q != null && !"".equals(q)){
			int idx = q.lastIndexOf(SESSION_ID_NAME + "=");
			if(idx  > -1){
				q = q.substring(idx + SESSION_ID_NAME.length() + 1, q.length());
				idx = q.indexOf("&");
				if(idx > -1) q = q.substring(0, idx);
				return q;
			}
		}
		return null;
	}
	
	//从request cookie中获取数据
	public static String getSessionIdFromCookie(HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0){
			for(Cookie ck : cookies){
				if(COOKIE_NAME.equals(ck.getName())){
					//return decode(ck.getValue());
					return ck.getValue();
				}
			}
		}
		return null;
	}	
	
	//设置WSID到response cookie
	public static void setSessionIdToCookie(HttpServletResponse response, String wsid){
		if( wsid == null || "".equals(wsid.trim()) ){
			return;
		}
		
		/*
		Cookie ck = new Cookie(COOKIE_NAME, val);
		ck.setPath("/");
		
		response.addCookie(ck);
		*/
		
		StringBuilder cookie = new StringBuilder();
		cookie.append(COOKIE_NAME + "=" + wsid);
		cookie.append(";Path=/");
		cookie.append(";HttpOnly");
		
		response.addHeader("Set-Cookie", cookie.toString());
	}
	
	/**
	 * 兼容业务代码中调用的setCookie方法
	 * @param response
	 * @param val
	 */
	public static void setCookie(HttpServletResponse response, String wsid){
		setSessionIdToCookie(response, wsid);
	}
	
	//删除response cookie
	public static void removeCookie(HttpServletResponse response){
		
		/*Cookie ck = new Cookie(COOKIE_NAME,null);
		ck.setPath("/");
		
		response.addCookie(ck);*/
		
		StringBuilder cookie = new StringBuilder();
		cookie.append(COOKIE_NAME + "=");
		cookie.append(";Path=/");
		cookie.append(";Expires=Thu, 01 Jan 1970 00:00:00 GMT");
		cookie.append(";HttpOnly");
		
		response.addHeader("Set-Cookie", cookie.toString());
	}	
	
	/*public static String[] getDataFromCookie(HttpServletRequest request){
		String data=getCookie(request);
		if(data!=null && !"".equals(data)){
			return data.split(",");
		}
		return null;
	}
	
	static void setDataToCookie(HttpServletResponse response,String[] data){
		if(data!=null){
			setCookie(response,Utility.getStrByArray(data));
		}
	}*/
	
	public Object getSessionCacheValue(String key){
		return _handler.getSessionCacheValue(key);
	}
	
	public void setSessionCacheValue(String key, Object value){
		_handler.setSessionCacheValue(key, value);
	}
	
	public HttpSession getSessionCache(String wsid){
		return _handler.getSessionCache(wsid);
	}
	
	public void setSessionCache(String wsid,HttpSession session){
		_handler.setSessionCache(wsid, session);
	}
	
	public void activeSessionCache(String wsid){
		_handler.activeSessionCache(wsid);
	}
	
	public void removeSessionCache(String wsid){
		_handler.removeSessionCache(wsid);
	}
	
	//判断指定的 wsid SNA Session远端是否存在
	public boolean sessionCacheExist(String wsid){
		return _handler.sessionCacheExist(wsid);
	}
	
	public SessionData getSessionDataCache(String sessionId){
		return _handler.getSessionDataCache(sessionId);
	}
	
	public void setSessionDataCache(String sessionId, SessionData data){
		_handler.setSessionDataCache(sessionId, data);
	}
	
	public void activeSessionDataCache(String sessionId){
		_handler.activeSessionDataCache(sessionId);
	}
	
	public void removeSessionDataCache(String sessionId){
		_handler.removeSessionDataCache(sessionId);
	}
	
	/******************* OnLine *********************************************/
/*	private static int get_ASCII_SUM(String str){
		if(str!=null && !"".equals(str)){
			char[] chars = str.toCharArray(); // 把字符中转换为字符数组
			int total=0;
			for(int i = 0; i < chars.length; i++) {// 输出结果
				total+=((int)chars[i]);
			}
			return total;
		}
		return 0;
	}*/
	
	
	public void addOnLine(String uniqueId , String wsid){
		_handler.addOnLine(uniqueId, wsid);
	}
	
	public void removeOnLine(String uniqueId){
		_handler.removeOnLine(uniqueId);
	}
	
	public void removeOnLine(String uniqueId, String wsid){
		_handler.removeOnLine(uniqueId, wsid);
	}
	
	public void activeOnLine(String uniqueId){
		_handler.activeOnLine(uniqueId);
	}
	
	public boolean isOnLine(String uniqueId){
		return _handler.isOnLine(uniqueId);
	}
	
	public boolean isOnLine(String uniqueId, String wsid){
		return _handler.isOnLine(uniqueId, wsid);
	}
	
	public Set<String> getOnLineUniqueSet(){
		return _handler.getOnLineUniqueSet();
	}

	public Set<String> getOnLineWSIDSet(String uniqueId){
		return _handler.getOnLineWSIDSet(uniqueId);
	}
}
