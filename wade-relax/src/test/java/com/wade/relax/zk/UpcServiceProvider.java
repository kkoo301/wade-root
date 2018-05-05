package com.wade.relax.zk;

import org.I0Itec.zkclient.ZkClient;

public class UpcServiceProvider {

	private String SERVICE_NAME = "service-A";

	public void init() throws Exception {

		String PATH = "/center";
		
		ZkClient zkClient = new ZkClient(Constant.zkServerAddr);
		boolean rootExists = zkClient.exists(PATH);
		
		if (!rootExists) {
			zkClient.createPersistent(PATH);
		}

		boolean serviceExists = zkClient.exists(PATH + "/" + SERVICE_NAME);
		if (!serviceExists) {
			zkClient.createPersistent(PATH + "/" + SERVICE_NAME);
		}

		
		String ip = "10.143.24.15";
		zkClient.createEphemeral(PATH + "/" + SERVICE_NAME + "/" + ip);
		
		System.out.println("提供的服务节点名称为：" + PATH + "/" + SERVICE_NAME + "/" + ip);
	}

	public void provide() {

	}

	public static void main(String[] args) throws Exception {
		UpcServiceProvider service = new UpcServiceProvider();
		service.init();

		System.out.println("Service Provider...");
		Thread.sleep(1000 * 60 * 60 * 24);
	}

}