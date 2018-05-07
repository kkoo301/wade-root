package com.ailk.service.protocol.config;

import java.util.Map;

public interface ICenterResolver {
	
	public Map<String, CenterInfo> resolve() throws Exception;
	
}
