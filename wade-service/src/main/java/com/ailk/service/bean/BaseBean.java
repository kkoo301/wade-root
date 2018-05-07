package com.ailk.service.bean;

import com.ailk.common.data.IVisit;
import com.ailk.service.session.SessionManager;

public class BaseBean extends AbstractBean {
	
	/**
	 * getVisit
	 * @return
	 */
	public static IVisit getVisit() {
		return SessionManager.getInstance().getVisit();
	}
	
	
}