package com.wade.relax.registry.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.relax.registry.Constants;
import com.wade.relax.registry.SystemRuntime;
import com.wade.relax.registry.SystemUtil;
import com.wade.zkclient.IZkClient;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: 服务提供者运行时类
 * @auth: steven.zhou
 * @date: 2015-11-30
 */
public final class ProviderRuntime {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProviderRuntime.class);
		
	private static IZkClient zkClient;
	private static String centerName;
	private static String listenAddress;
	private static String releaseNumber;
	
	/**
	 * 启动消费者例行任务
	 */
	public static void start() {
		
		if (SystemUtil.isOnlineTest()) {
			LOG.info("在线测试实例，无需启动ProviderRuntime.start()");
			return;
		}
		
		zkClient = SystemRuntime.getZkClient();
		centerName = SystemRuntime.getCenterName();
		listenAddress = SystemRuntime.getListenAddress();
		releaseNumber = SystemRuntime.getReleaseNumber();
		
		String serverName = System.getProperty(Constants.SERVER_NAME, "");
		String path = Constants.ZK_RELAX + centerName + "/instances";
		
		if (!zkClient.exists(path)) {
			zkClient.createPersistent(path, true);
		}
		
		String data = serverName + "|" + System.getProperty("isPrepared", "");
		String ephemeralPath = path + "/" + listenAddress;
		byte[] dataBytes = data.getBytes();
		zkClient.createEphemeral(ephemeralPath, dataBytes);
		LOG.debug("instance {} online!", serverName);
		
		startEphemeralGuarder(ephemeralPath, dataBytes);
	}
	
	/**
	 * 临时节点守护线程
	 * 
	 * @param path
	 * @param data
	 */
	private static final void startEphemeralGuarder(final String path, final byte[] data) {
		
		LOG.info("开启APP心跳守护线程...");
		
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				String startTime = System.getProperty("isPrepared", "");
				String serverName = System.getProperty(Constants.SERVER_NAME, "");
			    if (startTime.startsWith("StartTime:")) {
			    	if (!zkClient.exists(path)) {
			    		zkClient.createEphemeral(path, data);
						LOG.info("APP实例 {} 被心跳守护线程重新注册上线!", serverName);	
			    	}
			    }
			}
		}, 10000, 60000);
	}
	
	/**
	 * 上报中心服务(提供给服务框架使用)
	 * 
	 * @param serviceNames
	 */
	public static final void reportCenterServiceNames(Set<String> serviceNames) {
		
		if (SystemUtil.isOnlineTest()) {
			LOG.info("在线测试实例，无需往注册中心上报服务!");
			return;
		}
		
		if (0 == serviceNames.size()) {
			LOG.info("中心:" + centerName + ", 无需发布服务,可能属于后台接口类.");
			return;
		}
		
		String path = Constants.ZK_RELAX + centerName + "/services";
		if (!zkClient.exists(path)) {
			zkClient.createPersistent(path, true);
		} else {
			byte[] bytes = zkClient.readData(path);
			if (null != bytes) {
				String oldReleaseNumber = new String(bytes);
				if (releaseNumber.compareTo(oldReleaseNumber) <= 0) {
					LOG.info("无需重复发布服务名");
					return;
				}
			}
		}
		
		long start = System.currentTimeMillis();
		zkClient.writeData(path, releaseNumber.getBytes()); // 立马写入当前版本号
		List<String> names = zkClient.getChildren(path);
		Collection<String>[] rtn = collectionDifference(serviceNames, names);
		
		LOG.info("中心" + SystemRuntime.getCenterName() + "开始发布服务, 新增服务:" + rtn[0].size() + "个，删除服务: " + rtn[1].size() + "个。");
		LOG.info("由于需创建大量服务节点，耗时较长，请勿中断!");
		
		for (String name : rtn[1]) {
			zkClient.delete(path + "/" + name);
		}
		
		for (String name : rtn[0]) {
			zkClient.createPersistent(path + "/" + name);
		}
		
		LOG.info("服务发布完成，耗时: " + (System.currentTimeMillis() - start) + "ms");
		
	}
	
	/**
	 * 集合差异比较
	 * 
	 * @param n 新集合
	 * @param o 老集合
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Collection<String>[] collectionDifference(Collection<String> n, Collection<String> o) {
		
		Set<String> s1 = new HashSet<String>();
		Set<String> s2 = new HashSet<String>();
		
		s1.addAll(n); // 复制
		s2.addAll(o); // 复制
		
		s1.removeAll(s2); // 新增的		
		o.removeAll(n); // 删除的
		
		Collection[] rtn = {s1, o};
		return rtn;
	}
	
	/**
	 * 实例优雅下线(供probe.jsp调用)
	 */
	public static final boolean gracefullyShutdown() {
		
		if (SystemUtil.isOnlineTest()) {
			LOG.info("在线测试实例，无需优雅停机!");
			return true;
		}
		
		String path = Constants.ZK_RELAX + centerName + "/instances";
		boolean b = zkClient.delete(path + "/" + listenAddress);
		LOG.info("instance " + listenAddress + " gracefully shutdown " + (b ? "success" : "failure"));
		return b;
	}
		
}
