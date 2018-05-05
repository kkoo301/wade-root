package com.wade.relax.registry.consumer.task;

import com.wade.relax.registry.SystemRuntime;
import com.wade.zkclient.IZkClient;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public abstract class AbstractConsumerTask implements IConsumerTask {
	
	protected static final IZkClient zkClient = SystemRuntime.getZkClient();
	
}
