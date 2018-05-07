package com.ailk.service.protocol.impl;

import com.ailk.common.data.IDataInput;
import com.ailk.service.protocol.IServiceRule;

public class ServiceRule implements IServiceRule {
	
	private static final long serialVersionUID = 1L;
	private String rel;
	
	public ServiceRule(String rel) {
		this.rel = rel;
	}
	
	public String getREL() {
		return this.rel;
	}
	
	public int execute(IDataInput input) {
		return 0;
	}

}
