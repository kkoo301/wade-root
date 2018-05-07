package com.ailk.service.client.http;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.util.Utility;

public class HttpClientAdapter implements IHttpClientAdapter{
	public String buildPostDataString(IDataInput input) {
		if(input!=null){
			return input.toString();
		}
		return null;
	}
	
	public String getServiceUrl(IDataInput input) throws Exception{
		String httpUrl = GlobalCfg.getProperty("service.http.url");
		if(httpUrl==null || "".equals(httpUrl)){
			Utility.error("默认的 Http 服务地址 未配置");
		}
		return httpUrl;
	}

	public IDataOutput buildDataOutput(IDataInput input,String dataString) throws Exception{
		if(dataString==null || "".equals(dataString)){
			Utility.error("系统错误，无法获取到返回结果");
		}
		
		IData r_data=new DataMap(dataString);
		IDataOutput output=new DataOutput();
		
		IData head=r_data.getData("head");
		if(head!=null){
			output.getHead().putAll(head);
		}
		IDataset data=r_data.getDataset("data");
		if(data!=null && data.size()>0){
			output.getData().addAll(data);
		}
		String X_RESULTCODE = (String) output.getHead().get("X_RESULTCODE");
		if (X_RESULTCODE == null){
			Utility.error("系统错误，无法获取到错误编码");
		}
		
		boolean iscatch="true".equals(input.getHead().getString("_IS_CATCH"));
		if (!iscatch) {
			String X_RESULTINFO = (String) output.getHead().get("X_RESULTINFO");
			Utility.error(X_RESULTINFO);
		}
		
		return output;
	}

}