package com.wade.relax.zk;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class ServiceConsumer {

	private String SERVICE_NAME = "service-A";

	public void init() {

		String SERVICE_PATH = "/crm/" + SERVICE_NAME;
		ZkClient zkClient = new ZkClient(Constant.zkServerAddr);

		boolean serviceExists = zkClient.exists(SERVICE_PATH);
		if (serviceExists) {
			zkClient.getChildren(SERVICE_PATH);
		} else {
			throw new RuntimeException("service not exist!");
		}

		zkClient.subscribeChildChanges(SERVICE_PATH, new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println(parentPath + " 可用地址:" + currentChilds);
			}
		});
	}

	public static void main(String[] args) throws Exception {
		ServiceConsumer consumer = new ServiceConsumer();
		consumer.init();
		Thread.sleep(1000 * 60 * 60 * 24);
	}

}