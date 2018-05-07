package com.ailk.sna;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.sna.config.SNACfg;

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper{

	private String _wsid = null;
	private transient HttpServletResponse _response;
	
	public HttpServletRequestWrapper(HttpServletRequest request,HttpServletResponse response ,String wsid) {
		super(request);
		_wsid = wsid;
		_response = response;
	}
	
	public String getRequestedSessionId(){
		return _wsid;
	}
	
	public boolean isRequestedSessionIdValid(){
		return _wsid!=null;
	}
	
	public boolean  isRequestedSessionIdFromCookie(){
		return true;
	}
	
	public boolean isRequestedSessionIdFromURL(){
		return false;
	}
	
	@Override
	public HttpSession getSession(boolean create) {	
		HttpSession _session = SessionFactory.getInstance().getSession();

		//if(_session == null && create){
		if(create){
			_session = createSession();
		}
		return _session;
	}
	
	private HttpSession createSession(){
		String wsid = SessionFactory.getRandomID();
		HttpSession _session = new HttpSession(wsid);
		SessionFactory.getInstance().setSession(_session);
		if(SNACfg.SESSION_ID_FROM_COOKIE.equals(SNACfg.SESSION_ID_FROM)){
			SessionFactory.setSessionIdToCookie(_response, wsid);
		}
		return _session;
	}
	
	public HttpSession getSession() {
		//return getSession(true);
		return getSession(false);
	}
}