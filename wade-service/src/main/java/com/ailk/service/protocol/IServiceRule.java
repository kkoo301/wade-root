package com.ailk.service.protocol;

import java.io.Serializable;

import com.ailk.common.data.IDataInput;

public interface IServiceRule extends Serializable {
	
	public String getREL();
	
	public int execute(IDataInput input);
	
}
