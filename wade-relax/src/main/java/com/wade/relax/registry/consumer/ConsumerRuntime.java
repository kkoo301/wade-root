package com.wade.relax.registry.consumer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.org.apache.commons.lang3.StringUtils;

import com.wade.relax.registry.SystemRuntime;
import com.wade.relax.registry.SystemUtil;
import com.wade.relax.registry.ZkGuardTask;
import com.wade.relax.registry.consumer.lb.impl.EsbLoadBalance;
import com.wade.relax.registry.consumer.lb.ILoadBalance;
import com.wade.relax.registry.consumer.lb.impl.AppLoadBalance;
import com.wade.relax.registry.consumer.task.IConsumerTask;
import com.wade.relax.registry.consumer.task.AppConsumerTask;
import com.wade.relax.registry.consumer.task.EsbConsumerTask;
import com.wade.relax.registry.consumer.SockSite;
import com.wade.relax.exception.NotFoundCenterException;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public final class ConsumerRuntime {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConsumerRuntime.class);
	
	/**
	 * 消费者例行任务
	 */
	private static IConsumerTask task = null;
	
	/**
	 * 负载均衡器
	 */
	private static ILoadBalance loadBalance = null;
	
	/**
	 * 服务到[中心名,版本]的映射 
	 */
	private static Map<String, String> serviceMapping;
	
	/**
	 * 线程是否已启动，防止重复启动!
	 */
	private static boolean started = false;
	
	private ConsumerRuntime() {
		
	}
	
	/**
	 * 提供服务映射关系
	 * 
	 * @return
	 */
	public static final Map<String, String> getServiceMapping() {
		return serviceMapping;
	}
	
	/**
	 * 保存(服务名到中心名)映射关系
	 * 
	 * @param map
	 */
	public static final void updateLocalServiceMapping(Map<String, String> map) {
		serviceMapping = map;
	}
	
	/**
	 * 更新服务名中心名映射关系
	 */
	public static final void updateLocalServiceMapping() {
		task.updateLocalServiceMapping();
	}
	
	/**
	 * 启动消费者例行任务
	 */
	public static final void start() {
				
		if (SystemRuntime.isUseESB) {
			task = new EsbConsumerTask();
			loadBalance = EsbLoadBalance.getInstance();
		} else {
			task = new AppConsumerTask();
			loadBalance = AppLoadBalance.getInstance();
		}
		
		if (SystemUtil.isOnlineTest()) {
			LOG.info("在线测试实例，无需启动ConsumerRuntime.start()");
			return;
		}
				
		if (started) {
			return;
		}
		
		started = true;
		
		new Thread(new ZkGuardTask(loadBalance)).start();
		new Thread(task).start();
		waitConsumerTaskReady(task);
		
		LOG.info("服务消费者初始化成功!");
	}
	
	/**
	 * 根据服务名返回服务版本
	 * 
	 * @param serviceName
	 * @return
	 */
	public static final String getVersion(String serviceName) {
		return serviceMapping.get(serviceName);
	}
	
	/**
	 * 根据服务名获取一个可用地址(提供给服务框架使用)
	 * 
	 * @param serviceName
	 * @return
	 */
	public static final SockSite nextAvailableAddress(String serviceName) {
		
		// 如果开启 service.router.use.esb 开关，那么获取的是ESB实例地址
		if (SystemRuntime.isUseESB) {
			SockSite sockSite = EsbLoadBalance.next();
			return sockSite;
		}
		
		// 如果未开启 service.router.use.esb 开关，那么获取的是APP实例地址
		String centerName = serviceMapping.get(serviceName);
		if (StringUtils.isBlank(centerName)) {
			throw new NotFoundCenterException("根据服务名: " + serviceName + ", 无法找到对应的中心名。");
		}
		
		return AppLoadBalance.next(centerName);
		
	}
	
	/**
	 * 等待消费者任务准备好
	 */
	private static void waitConsumerTaskReady(IConsumerTask task) {

		while (true) {
			if (task.isReady()) {
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
