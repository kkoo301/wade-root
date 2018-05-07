/**
 * $
 */
package com.ailk.mq.server.config;

import com.ailk.common.config.GlobalCfg;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AsyncTaskConfig.java
 * @description: 异步任务定义参数
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2017-2-24
 */
public class AsyncTaskConfig {
	
	public static final String ASYNC_TASK = GlobalCfg.getProperty("mq.tab.asynctask", "TD_M_ASYNCTASK");
	public static final String ASYNC_TASK_LOG = GlobalCfg.getProperty("mq.tab.asynctask.log", "TD_M_ASYNCTASK_LOG");
	public static final String ASYNC_TASK_PLAN = GlobalCfg.getProperty("mq.tab.asynctask.plan", "TD_M_ASYNCTASK_PLAN");

}
