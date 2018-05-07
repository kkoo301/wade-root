package com.ailk.service.bean;

import org.apache.log4j.Logger;

public abstract class AbstractBean implements IBaseBean {
	private transient static final Logger log = Logger.getLogger(AbstractBean.class);
	
	protected void debug(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

}
