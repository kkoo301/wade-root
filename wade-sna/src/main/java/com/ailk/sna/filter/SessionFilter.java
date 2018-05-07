package com.ailk.sna.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.sna.HttpServletRequestWrapper;
import com.ailk.sna.HttpSession;
import com.ailk.sna.SessionFactory;
import com.ailk.sna.config.SNACfg;
import com.ailk.sna.data.SessionData;

public class SessionFilter implements Filter{
	private static transient Logger log = Logger.getLogger(SessionFilter.class);
	private ServletContext _servletContext;

	public void init(FilterConfig config) throws ServletException {
		_servletContext = config.getServletContext();
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest)req;
		HttpServletResponse response=(HttpServletResponse)res;

		try{
			String wsid = null;
			if(SNACfg.SESSION_ID_FROM_URL.equals(SNACfg.SESSION_ID_FROM)){
				wsid = SessionFactory.getSessionIdFromUrl(request);
			}else{
				wsid = SessionFactory.getSessionIdFromCookie(request);
			}
			if(wsid!=null && !"".equals(wsid)){
				HttpSession session = SessionFactory.getInstance().getSessionCache(wsid);
				if(session !=null ){
					session.setServletContext(_servletContext);
					SessionFactory.getInstance().setSession(session);
				}
				
				if(SNACfg.SESSION_DATA_CACHE_ENABLE){
					SessionData sessionData = SessionFactory.getInstance().getSessionDataCache(wsid);
					if(sessionData != null){
						SessionFactory.getInstance().setSessionData(sessionData);
					}else{
						sessionData = new SessionData();
						sessionData.setDirty(true);
						SessionFactory.getInstance().setSessionData(sessionData);
					}
				}
			}

			HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request,response,wsid);
			requestWrapper.setAttribute(SNACfg.REQUEST_TAG_NAME, SNACfg.REQUEST_TAG_VALUE);		
			requestWrapper.setAttribute(SNACfg.REQUEST_TAG_NAME + ":" + SNACfg.SESSION_ID_FROM_URL_TAG_NAME, ("" + SNACfg.IS_SESSION_ID_FROM_URL));	
			
			chain.doFilter(requestWrapper, response);
		}catch(IOException ex1){
			log.error("com.ailk.sna.filter.SessionFilter Error-01", ex1);
			throw ex1;
		}catch(ServletException ex2){
			log.error("com.ailk.sna.filter.SessionFilter Error-02", ex2);
			throw ex2;
		}finally{
			HttpSession session = SessionFactory.getInstance().getSession();
			if(session != null){
				String sessionId = session.getId();
				try{
					//boolean sessionDirty = false;
					if(session.isNew() || session.isDirty()){
						session.clearDirty();
						SessionFactory.getInstance().setSessionCache(sessionId, session);  //store back
						if(log.isDebugEnabled()){
							log.debug("Store Session To SNA SessionCache");
						}
					}else{
						SessionFactory.getInstance().activeSessionCache(sessionId); //touch
						if(log.isDebugEnabled()){
							log.debug("Active SNA SessionCache");
						}
					}
				}catch(Exception ex3){
					log.error("com.ailk.sna.filter.SessionFilter Error-03", ex3);
				}finally{
					SessionFactory.getInstance().clearSesson(); //clear threadlocal session
				}
				if(SNACfg.SESSION_DATA_CACHE_ENABLE){
					try{
						//Session Data
						SessionData sessionData = SessionFactory.getInstance().getSessionData();
						if(sessionData != null){
							if(sessionData.isDirty()){
								sessionData.clearDirty();
								SessionFactory.getInstance().setSessionDataCache(sessionId, sessionData);
								if(log.isDebugEnabled()){
									log.debug("Store SessionData To SNA SessionDataCache");
								}
							}else{
								SessionFactory.getInstance().activeSessionDataCache(sessionId);
								if(log.isDebugEnabled()){
									log.debug("Active SNA SessionDataCache");
								}
							}
						}
					}catch(Exception ex4){
						log.error("com.ailk.sna.filter.SessionFilter Error-04", ex4);
					}finally{
						SessionFactory.getInstance().clearSessionData();
					}
				}
			}
		}
	}
	
	public void destroy() {
		_servletContext=null;
		log=null;
	}
}