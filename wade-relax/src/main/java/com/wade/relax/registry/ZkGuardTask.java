package com.wade.relax.registry;

import com.wade.relax.registry.consumer.lb.ILoadBalance;
import com.wade.zkclient.IZkClient;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: ZK高可用保障线程
 * @auth: zhoulin2
 * @date: 2015-11-30
 */
public class ZkGuardTask implements Runnable {

	private IZkClient zkClient = SystemRuntime.getZkClient();
	
	// 当前的负载均衡器
	private ILoadBalance loadBalance;
	
	// 失败次数
	private int failureCnt = 0;

	public ZkGuardTask(ILoadBalance loadBalance) {
		this.loadBalance = loadBalance;
	}

	@Override
	public void run() {

		while (true) {

			/**
			 * isConnected() == false 集群不一定不可用; isConnected() == true 集群一定是可用的。<br />
			 * 
			 * 如果当前连的zk节点挂了, isConnected()返回false, 只要客户端重连成功, 集群没挂, 过后又会返回true
			 * 
			 */
			if (zkClient.isConnected()) {
				this.failureCnt = 0; //
			} else {
				if (this.failureCnt++ > 5) { // 这时才能确认zk确实挂掉了
					this.failureCnt = 0; // 归零后重新计数
					loadBalance.healthCheck();
				}
			}

			try {
				Thread.sleep(1000 * 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}