package com.ailk.service.client;

import com.ailk.common.config.GlobalCfg;
import com.ailk.service.client.hessian.HessianClient;
import com.ailk.service.client.json.Json2Client;

/**
 * @author yifur
 *
 */
public final class ProtocalClientFactory {
	
	private static IProtocalClient client = null;
	
	private static final String mode = GlobalCfg.getProperty("service.router.addr", "h");
	
	private ProtocalClientFactory() {	}
		
	public static final IProtocalClient getClient() {
		return client;
	}
	
	static {
		switch (mode.charAt(0)) {
			case 'h' :
				client = new HessianClient();
				break;
			case 'r' :
				client = new com.ailk.service.client.registry.RegistryClient();
				break;
			case 'j' :
				client = new Json2Client();
				break;
			default :
				client = new HessianClient();
		}
	}
	
}
