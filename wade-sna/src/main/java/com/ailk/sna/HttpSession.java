package com.ailk.sna;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import com.ailk.common.data.IVisit;
import com.ailk.sna.data.SessionData;

public class HttpSession implements javax.servlet.http.HttpSession,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5380174743618410109L;
	
	private String _id;
	private long _creationTime;
	private long _lastAccessTime;
	
	private transient ServletContext _servletContext;
	private transient boolean _isNew;
	
	private SessionData _sessionData = new SessionData();
	
	public HttpSession(String Id){
		_id=Id;
		_lastAccessTime =_creationTime = System.currentTimeMillis();
		_isNew=true;
	}

	public String getId() {
		return _id;
	}

	public long getCreationTime() {
		return _creationTime;
	}
	
	public long getLastAccessedTime() {
		return _lastAccessTime;
	}
	
	public int getMaxInactiveInterval() {
		return SessionFactory.SESSION_TIMEOUT;
	}

	public void setMaxInactiveInterval(int interval) {
		return;
	}
	
	public ServletContext getServletContext() {
		return _servletContext;
	}
	
	public void setServletContext(ServletContext servletContext){
		_servletContext=servletContext;
	}

	public javax.servlet.http.HttpSessionContext getSessionContext() {
		return new HttpSessionContext();
	}

	public boolean isNew() {
		return _isNew;
	}

	public void invalidate() {
		/*_creationTime=0;
		_lastAccessTime=0;*/
		_sessionData.clear();
		_lastAccessTime = System.currentTimeMillis();
		//SessionFactory.removeSessionCache(_id);
	}

	public Object getAttribute(String name) {
		if(name == null){
			throw new IllegalArgumentException("getAttribute: name parameter cannot be null");
		}
		_lastAccessTime = System.currentTimeMillis();
		return _sessionData.get(name);
	}

	public void setAttribute(String name, Object val) {
		if(name == null){
			throw new IllegalArgumentException("setAttribute: name parameter cannot be null");
		}
		if(val == null)return;
		
		//只允许存入String 或者IVisit类型数据
		if(!(val instanceof IVisit) && !(val instanceof String)){
			throw new IllegalArgumentException("setAttribute: value parameter must be IVisit or String");
		}

		_sessionData.put(name,(Serializable)val);
		_lastAccessTime = System.currentTimeMillis();
	}
	
	public void removeAttribute(String name) {
		if(name == null){
			throw new IllegalArgumentException("removeAttribute: name parameter cannot be null");
		}
		_sessionData.remove(name);
		_lastAccessTime = System.currentTimeMillis();
	}
	
	public Object getValue(String name) {
		return getAttribute(name);
	}
	
	public void putValue(String name, Object val) {
		setAttribute(name,val);
	}
	
	public void removeValue(String name) {
		removeAttribute(name);
	}
	
	public Enumeration<String> getAttributeNames() {
	    return _sessionData.keys();
	}
	
	public String[] getValueNames() {
		return (String[]) _sessionData.keySet().toArray();
	}
	
	public boolean isDirty(){
		return _sessionData.isDirty();
	}
	
	public void clearDirty(){
		_isNew = false;
		_sessionData.clearDirty();
	}
}