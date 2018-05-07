package com.ailk.service.protocol;

import java.io.Serializable;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;

public interface IServiceCreater extends Serializable {
	
	public IDataOutput call(String name, IDataInput input) throws Exception ;

}
