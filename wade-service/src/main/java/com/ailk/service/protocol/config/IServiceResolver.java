package com.ailk.service.protocol.config;

import java.io.Serializable;

import com.ailk.service.protocol.IServiceProtocol;

public interface IServiceResolver extends Serializable {
	
	public IServiceProtocol resolve(String xml) throws Exception ;
	
	public void setPath(String path);
	
	public void setSuffix(String suffix);

	public String getName(String xml);
}
