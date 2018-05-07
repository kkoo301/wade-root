/**
 * $
 */
package com.wade.dsf.test.startup;

import org.apache.log4j.Logger;

import com.wade.dsf.exception.DsfException;
import com.wade.dsf.startup.IDsfStartup;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: MyStartup.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-18
 */
public class MyStartup implements IDsfStartup {
	
	private static final Logger log = Logger.getLogger(MyStartup.class);

	@Override
	public void startup() throws DsfException {
		if (log.isDebugEnabled()) {
			log.debug("run mystartup");
		}
	}

}
