package com.wade.relax.zk;

import org.I0Itec.zkclient.ZkClient;

public class ResServiceProvider {

	private String SERVICE_NAME = "service-A";

	public void init() throws Exception {
		
		String PATH = "/center/res";
		
		ZkClient zkClient = new ZkClient(Constant.zkServerAddr);
		boolean rootExists = zkClient.exists(PATH);
		
		if (!rootExists) {
			zkClient.createPersistent(PATH, true);
		}

		if (!zkClient.exists(PATH + "/services")) {
			zkClient.createPersistent(PATH + "/services", true);
		}
		
		if (!zkClient.exists(PATH + "/instances")) {
			zkClient.createPersistent(PATH + "/instances", true);
		}
		
		// 创建地址节点(临时类型)
		for (int i = 1; i <= 12; i++) {
			zkClient.createEphemeral(PATH + "/instances/10.143.2.15:" + (10000 + i), null);	
		}
		
		for (String serviceName : Constant.resServices) {
			zkClient.createEphemeral(PATH + "/services/" + serviceName);
		}
		
	}

	// 提供服务
	public void provide() {

	}

	public static void main(String[] args) throws Exception {
		ResServiceProvider service = new ResServiceProvider();
		service.init();

		System.out.println("启动 " + service.getClass().getName());
		Thread.sleep(1000 * 60 * 60 * 24);
	}

}