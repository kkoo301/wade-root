package com.wade.relax.registry.consumer.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.wade.relax.registry.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.relax.registry.Constants;
import com.wade.relax.registry.consumer.ConsumerRuntime;
import com.wade.relax.registry.consumer.lb.impl.AppLoadBalance;
import com.wade.zkclient.IZkChildListener;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public class AppConsumerTask extends AbstractConsumerTask {

	private static final Logger LOG = LoggerFactory.getLogger(AppConsumerTask.class);
	
	/**
	 * 中心名集合
	 */
	private static Set<String> CENTER_NAMES;

	private static final AppLoadBalance appLoadBalance = AppLoadBalance.getInstance();
	
	/**
	 * 是否已经准备好
	 */
	private boolean isReady = false;
	
	/**
	 * 更新服务名中心名映射关系
	 */
	public void updateLocalServiceMapping() {
		Map<String, String> serviceMapping = serviceMapping();
		ConsumerRuntime.updateLocalServiceMapping(serviceMapping);
		LOG.info("更新服务与中心的映射关系...");
	}
	
	@Override
	public void run() {
		
		CENTER_NAMES = getCenterNames();
		
		/** 更新服务名中心名映射 */
		Map<String, String> serviceMapping = serviceMapping();
		ConsumerRuntime.updateLocalServiceMapping(serviceMapping);
		
		for (String centerName : CENTER_NAMES) {
			String path = Constants.ZK_RELAX + centerName + "/instances";
			
			zkClient.subscribeChildChanges(path, new IZkChildListener() {
				
				/**
				 * 服务消费者订阅各中心下的/instances节点，监控实例的变化情况
				 */
				public void handleChildChange(String parentPath, List<String> instances) throws Exception {
					
					String cn = StringUtils.split(parentPath, '/')[1];
					
					Set<String> address = new HashSet<String>();
					address.addAll(instances);
					
					appLoadBalance.updateSockSite(cn, address);
				}
				
			});
			
		}
		
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
	
	@Override
	public boolean isReady() {
		return isReady;
	}

	/**
	 * 更新中心地址信息
	 */
	private void updateCenterAddress() {

		for (String centerName : CENTER_NAMES) {
			
			String path = Constants.ZK_RELAX + centerName + "/instances";

			Set<String> address = new HashSet<String>();
			List<String> instances = zkClient.getChildren(path);
			if (null != instances && 0 != instances.size()) {
				address.addAll(instances);
			}

			/**
			 * 周期性更新各中心对应的地址集合
			 */
			appLoadBalance.updateSockSite(centerName, address);

		}

	}

	/**
	 * 获取中心名清单 </br>
	 * 
	 * 注：只获取版本号最新的中心名
	 * 
	 * @return
	 */
	private Set<String> getCenterNames() {

		Set<String> rtn = new HashSet<String>();

		String centerPath = StringUtils.stripEnd(Constants.ZK_RELAX, "/");
		if (!zkClient.exists(centerPath)) {
			if (!zkClient.exists(Constants.WADE_RELAX)) {
				zkClient.createPersistent(Constants.WADE_RELAX);
			}
			zkClient.createPersistent(centerPath);
		}

		List<String> centerNames = zkClient.getChildren(StringUtils.stripEnd(Constants.ZK_RELAX, "/"));
		rtn.addAll(centerNames);
				
		return rtn;
	
	}
	
	/**
	 * 构造 [ 服务名 -> 中心名 ] 的映射关系
	 * 
	 * @return
	 */
	private Map<String, String> serviceMapping() {

		Map<String, String> rtn = new HashMap<String, String>();
		
		for (String centerName : CENTER_NAMES) {
			int count = 0;
			String path = Constants.ZK_RELAX + centerName + "/services";
						
			List<String> names = zkClient.getChildren(path);
			if (null != names) {
				for (String name : names) {
					rtn.put(name, centerName);
					count++;
				}
			}
			
			LOG.info("中心 " + centerName + " 提供 " + count + " 个服务!");
		}
		
		return rtn;
	}
		
}
