package com.wade.relax.registry.consumer.task;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public interface IConsumerTask extends Runnable {
	boolean isReady();
	void run();
	void updateLocalServiceMapping();
}
