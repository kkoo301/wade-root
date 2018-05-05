package com.wade.relax.registry.consumer.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.wade.relax.registry.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.GlobalCfg;
import com.wade.relax.registry.Constants;
import com.wade.relax.registry.consumer.lb.impl.EsbLoadBalance;
import com.wade.zkclient.IZkChildListener;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public class EsbConsumerTask extends AbstractConsumerTask {

	private static final Logger LOG = LoggerFactory.getLogger(EsbConsumerTask.class);
	
	public static final String PATH_FOR_WEB = Constants.ZK_RELAX_ESB + "/cluster/" + GlobalCfg.getProperty("service.router.use.esb.cluster", "for-web") + "/instances";
	
	/**
	 * 是否已经准备好
	 */
	private boolean isReady = false;
	
	private static final EsbLoadBalance esbLoadBalance = EsbLoadBalance.getInstance();
	
	@Override
	public void run() {
		
		
		zkClient.subscribeChildChanges(PATH_FOR_WEB, new IZkChildListener() {
			
			/**
			 * 服务消费者订阅ESB下的/instances节点，监控实例的变化情况
			 */
			public void handleChildChange(String parentPath, List<String> instances) throws Exception {
								
				Set<String> address = new HashSet<String>();
				address.addAll(instances);
				
				esbLoadBalance.updateSockSite(address);
			}
			
		});
		
		while (true) {

			try {
				
				/** 更新中心地址信息 */
				updateCenterAddress();

				isReady = true;
				TimeUnit.SECONDS.sleep(SystemUtil.getSleepInterval());

			} catch (Exception e) {
				LOG.error("更新中心地址信息出错!", e);

				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}

			}

		}
	}

	/**
	 * 更新中心地址信息
	 */
	private void updateCenterAddress() {
	
		List<String> instances = zkClient.getChildren(PATH_FOR_WEB);
		if (null == instances || 0 == instances.size()) {
			return;
		}
			
		Set<String> address = new HashSet<String>();
		address.addAll(instances);
		
		/**
		 * 周期性更新ESB地址集合
		 */
		esbLoadBalance.updateSockSite(address);

	}

	@Override
	public boolean isReady() {
		return isReady;
	}

	@Override
	public void updateLocalServiceMapping() {
		LOG.info("EsbConsumerTask没有实现updateLocalServiceMapping函数。");
	}

}
