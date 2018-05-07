package com.ailk.service.client.registry;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.service.client.IProtocalClient;
import com.ailk.service.client.hessian.HessianClient;
import com.wade.relax.registry.consumer.ConsumerRuntime;
import com.wade.relax.registry.consumer.SockSite;

public class RegistryClient implements IProtocalClient {
	
	private static final transient Logger log = Logger.getLogger(RegistryClient.class);
	
	private static final IProtocalClient client = new HessianClient();
	
	
	public RegistryClient() {
		ConsumerRuntime.start();
		log.info("注册中心连接成功");
	}

	@Override
	public IDataOutput request(String url, String svcname, IDataInput input, int soTimeout, int connectTimeout) throws Exception {
		String svcUrl = url;
		
		int index = url.indexOf("registry");
		if(index != -1){
			SockSite ss = ConsumerRuntime.nextAvailableAddress(svcname);
			svcUrl = ss.getUrl();
			
			String context = url.substring(index);
			if (context.length() > 1) {
				svcUrl = svcUrl + "/service";
			} else {
				svcUrl = svcUrl + "/" + context.substring(1);
			}
			
			if (log.isDebugEnabled()) {
				log.debug("从注册中心获取远程服务地址:" + svcname + "->" + svcUrl);
			}
		}else{
			if (log.isDebugEnabled()) {
				log.debug("直接调用远程服务地址:" + svcname + "->" + url);
			}
		}
		
		
		return client.request(svcUrl, svcname, input, soTimeout, connectTimeout);
	}
	
}
