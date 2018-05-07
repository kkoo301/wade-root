/**
 * 
 */
package com.ailk.service.server.sec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;

/**
 * 
 * @author yifur
 * 
 */
public class ServiceSecurity implements IServiceSecurity {
	private static final transient Logger log = Logger.getLogger(ServiceSecurity.class);
	
	
	@Override
	public String createKey(String encry, IData head) {
		return String.valueOf(System.currentTimeMillis());
	}
	
	
	@Override
	public boolean isValidKey(String key, String encry) {
		if (log.isDebugEnabled()) {
			log.debug("服务效验[" + key + "][" + encry + "]");
		}
		return true;
	}
	
}
