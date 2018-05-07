package com.ailk.service.client.http;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;

public interface IHttpClientAdapter{
	public String buildPostDataString(IDataInput input);
	
	public String getServiceUrl(IDataInput input) throws Exception;

	public IDataOutput buildDataOutput(IDataInput input,String data) throws Exception;
}