package com.wade.relax.zk;

import java.util.TreeSet;

import org.I0Itec.zkclient.ZkClient;

public class MaxChildTest {

	public void init() throws Exception {
		
		String PATH = "/maxchild";
		
		ZkClient zkClient = new ZkClient(Constant.zkServerAddr);
		boolean rootExists = zkClient.exists(PATH);
		
		if (!rootExists) {
			zkClient.createPersistent(PATH, true);
		}

		TreeSet<String> set = new TreeSet<String>();
		
		// 创建地址节点(临时类型)
		for (int i = 1; i <= 10000; i++) {
			set.add("Upc.ServiceName." + (1000000 + i));
		}
		
		int i = 1;
		StringBuilder sb = new StringBuilder(10000);
		for (String s : set) {
			sb.append(s);
			sb.append(",");
			if (i++ % 2000 == 0) {
				zkClient.createPersistent(PATH + "/names-" + (i / 2000), sb.toString().getBytes());
				sb = new StringBuilder(1000);
			}
		}
		
	}

	// 提供服务
	public void provide() {

	}

	public static void main(String[] args) throws Exception {
		MaxChildTest service = new MaxChildTest();
		service.init();

		System.out.println("启动 " + service.getClass().getName());
		Thread.sleep(1000 * 60 * 60 * 24);
	}

}