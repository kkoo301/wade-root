package com.wade.relax.registry;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.config.TextConfig;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.relax.RelaxXml;
import com.wade.zkclient.IZkClient;
import com.wade.zkclient.ZkClient;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: 服务提供者运行时类
 * @auth: steven.zhou
 * @date: 2015-11-30
 */
public final class SystemRuntime {
	
	public static final long SERVICE_GROUP_SIZE = Long.parseLong(GlobalCfg.getProperty("service.group.size", "2000"));
	
	private static final Logger LOG = LoggerFactory.getLogger(SystemRuntime.class);
	private static IZkClient zkClient;
	private static String releaseNumber = null;
	private static boolean appInstance;
	private static String centerName;
	private static String listenAddress;
	
	public static boolean isUseESB = false;
	
	static {
		
		String zkaddr = GlobalCfg.getProperty("zookeeper.addr");
		String serverName = System.getProperty("wade.server.name", "");
		
		if (StringUtils.isNotBlank(zkaddr)) {
			zkClient = new ZkClient(GlobalCfg.getProperty("zookeeper.addr"), 6000, 5000);
		} else {
			LOG.error("未配置zookeeper.addr参数, ZkClient初始化不成功!!!");
		}
					
		if (serverName.startsWith("app")) {
			
			try {
				Map<String, String> map = TextConfig.getProperties("release.txt");
				releaseNumber = (String) map.get("number");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			appInstance = true;
			RelaxXml xml = new RelaxXml();
			xml.load();
			centerName = RelaxXml.getCenterName();
			listenAddress = RelaxXml.getListenAddress();
		} else {
			String useESB = GlobalCfg.getProperty("service.router.use.esb", "false");
			isUseESB = Boolean.parseBoolean(useESB);
			
			LOG.info("该实例作为客户端启动，是否调用ESB: " + isUseESB);
			
			appInstance = false;
			centerName = null;
			listenAddress = null;
		}
			
	}

	public static final IZkClient getZkClient() {
		return zkClient;
	}

	public static final String getReleaseNumber() {
		return releaseNumber;
	}

	public static final boolean isAppInstance() {
		return appInstance;
	}

	public static final String getCenterName() {
		if (appInstance) {
			return centerName;
		}
		return null;
	}

	public static final String getListenAddress() {
		if (appInstance) {
			return listenAddress;
		}
		return null;
	}

	public static final boolean isPrepared() {
		String startTime = System.getProperty("isPrepared", "");
		if (startTime.startsWith("StartTime:")) {
			return true;
		}
		return false;
	}
}
