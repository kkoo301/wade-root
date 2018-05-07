package com.ailk.service.server;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.service.client.registry.RegistryClient;
import com.ailk.service.server.hessian.Hessian2Server;


public class ServiceGateway extends Hessian2Server {
	
	private static final RegistryClient client = new RegistryClient();
	
	@Override
	protected IDataOutput mainServiceInvoke(String serviceName, IDataInput input) throws Exception {
		return client.request("", serviceName, input, 60 * 1000, 10 * 1000);
	}

}
