package com.ailk.service.protocol;

import java.io.Serializable;

import com.ailk.common.data.IDataInput;

public interface IService extends Serializable {
	
	public String getName();

	public void setName(String name);
	
	public IServiceRule getRule();

	public void setRule(IServiceRule rule);
	
	public boolean add(String name, IDataInput in, IServiceRule rule);
	
	public IService remove(String name);
	
	public IServiceBus getServiceBus();

}
