package com.ailk.rpc.server;

import org.apache.log4j.Logger;

public class HeartBeat {
	
	public static final String ALIVE = "alive";
	private static transient Logger log = Logger.getLogger(HeartBeat.class);
	
	public String isAlive() {
		if (log.isDebugEnabled()) {
			log.debug("服务端心跳");
		}
		return ALIVE;
	}
	
}
