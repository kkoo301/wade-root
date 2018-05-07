package com.ailk.service.protocol.config;

import com.ailk.service.protocol.config.impl.DefaultServiceResolver;

public class ServiceResolverFactory {
	private static ServiceResolverFactory factory = null;
	private static IServiceResolver resolver = null;
	
	private ServiceResolverFactory(){}
	
	public synchronized static IServiceResolver getResolver() {
		
		if (factory == null) {
			factory = new ServiceResolverFactory();
		}
		
		if (resolver == null) {
			resolver = new DefaultServiceResolver();
		}
		
		return resolver; 
	}
	
}
