package com.ailk.service.server.http;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.ServiceConstants;
import com.ailk.service.invoker.ServiceInvoker;

/**
 * 默认Http服务调用适配器
 * @author shieh
 *
 */
public class HttpServerAdapter implements IHttpServerAdapter{
	
	public IDataInput createDataInput(String datastr){
		IData input=new DataMap(datastr);
		return new DataInput(input.getData("head"),input.getData("data"));
	}
	
	public IDataOutput invoke(IDataInput input) throws Exception{
		return ServiceInvoker.mainServiceInvoke(input.getHead().getString(ServiceConstants.X_SERVICE_NAME), input);
	}
	
	public IDataInput handleInput(IDataInput input){
		return input;
	}
	
	public IDataOutput handleOutput(IDataInput input,IDataOutput output) throws Exception{
		return output;
	}
	
	public String createOutStr(IDataInput input ,IDataOutput output){
		return output.toString();
	}
}