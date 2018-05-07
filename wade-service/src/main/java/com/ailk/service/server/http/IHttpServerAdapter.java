package com.ailk.service.server.http;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;

/**
 * Http服务调用适配器接口
 * @author shieh
 *
 */
public interface IHttpServerAdapter{
	
	public IDataInput createDataInput(String datastr);
	
	public IDataOutput invoke(IDataInput input) throws Exception;
	
	public IDataInput handleInput(IDataInput input);
	
	public IDataOutput handleOutput(IDataInput input,IDataOutput output) throws Exception;
	
	public String createOutStr(IDataInput input,IDataOutput output);
}
