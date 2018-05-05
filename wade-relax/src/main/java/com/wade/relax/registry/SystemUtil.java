package com.wade.relax.registry;

import com.ailk.common.config.GlobalCfg;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: 系统级工具类
 * @auth: steven.zhou
 * @date: 2015-11-30
 */
public final class SystemUtil {

	public static final String WADE_RELAX_SLEEP_INTERVAL = "wade.relax.sleep.interval";
	public static final String ESB_GATEWAY_WORKGROUP_SIZE = "esb.gateway.workgroup.size";
	public static final String ESB_GATEWAY_MAXCONTENTLENGTH = "esb.gateway.maxcontentlength";
	public static final String DEFAULT_NTHREADS = String.valueOf(Runtime.getRuntime().availableProcessors() * 2);

	private static int sleepInterval = -1;
	private static int workGroupSize = -1;
	private static int execGroupSize = -1;
	private static int maxContentLength = -1;

	public static final String getCenterName() {
		return SystemRuntime.getCenterName();
	}

	/**
	 * 是否APP实例
	 * 
	 * @return
	 */
	public static final boolean isAppInstance() {
		return SystemRuntime.isAppInstance();
	}
	
	/**
	 * 如果实例名中有"onlinetest", 说明是在线测试实例
	 * 
	 * @return
	 */
	public static final boolean isOnlineTest() {
		
		String serverName = System.getProperty(Constants.SERVER_NAME, "");
		
		if (-1 != serverName.indexOf("onlinetest")) {
			return true;
		}
		
		return false;
		
	}

	/**
	 * 获取休眠周期(单位:秒)
	 *
	 * @return
	 */
	public static final int getSleepInterval() {

		if (-1 == sleepInterval) {
			sleepInterval = Integer.parseInt(GlobalCfg.getProperty(WADE_RELAX_SLEEP_INTERVAL, "60"));
			if (sleepInterval < 10) {
				sleepInterval = 10;
			}
		}

		return sleepInterval;

	}

	/**
	 * 获取WORK线程池大小
	 *
	 * @return
	 */
	public static final int getWorkGroupSize() {
		if (-1 == workGroupSize) {
			workGroupSize = Integer.parseInt(GlobalCfg.getProperty(ESB_GATEWAY_WORKGROUP_SIZE, DEFAULT_NTHREADS));
			if (workGroupSize < 1) {
				workGroupSize = Integer.parseInt(DEFAULT_NTHREADS);
			}
		}

		return workGroupSize;
	}

	/**
	 * 获取MaxContentLength
	 *
	 * @return
	 */
	public static final int getMaxContentLength() {
		if (-1 == maxContentLength) {
			String maxLen = GlobalCfg.getProperty(ESB_GATEWAY_MAXCONTENTLENGTH, "2097152"); // default: 10MB
			maxContentLength = Integer.parseInt(maxLen);
			if (maxContentLength < 1048576 || maxContentLength > 20971520) {
				// < 1MB or > 20MB
				maxContentLength = 10485760;
			}
		}

		return maxContentLength;
	}

}