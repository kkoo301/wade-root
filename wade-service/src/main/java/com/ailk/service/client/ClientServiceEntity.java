package com.ailk.service.client;

import com.ailk.common.data.IDataInput;
import com.ailk.service.protocol.IService;
import com.ailk.service.protocol.IServiceBus;
import com.ailk.service.protocol.IServiceRule;
import com.ailk.service.protocol.impl.ServiceBus;

public class ClientServiceEntity implements IService {

	private static final long serialVersionUID = 1L;
	
	private IServiceBus bus = new ServiceBus();
	private String name;
	private IServiceRule rule;
	
	public ClientServiceEntity() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public IServiceRule getRule() {
		return rule;
	}

	public void setRule(IServiceRule rule) {
		this.rule = rule;
	}
	
	public boolean add(String name, IDataInput in, IServiceRule rule) {
		return bus.add(name, in, rule);
	}
	
	public IService remove(String name) {
		return bus.remove(name);
	}
	
	public IServiceBus getServiceBus() {
		return this.bus;
	}
	
}
