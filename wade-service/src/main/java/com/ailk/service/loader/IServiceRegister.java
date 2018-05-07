package com.ailk.service.loader;

import java.util.Map;

import com.ailk.service.protocol.impl.ServiceEntity;

public interface IServiceRegister {
	
	public Map<String, ServiceEntity> loadService() throws Exception ;

}
