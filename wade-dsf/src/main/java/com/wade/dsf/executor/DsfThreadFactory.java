/**
 * $
 */
package com.wade.dsf.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfThreadFactory.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-19
 */
public class DsfThreadFactory implements ThreadFactory {

	static final AtomicInteger poolNumber = new AtomicInteger(1);
	final ThreadGroup group;
	final AtomicInteger threadNumber = new AtomicInteger(1);
	final String namePrefix;

	public DsfThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "dsfpool-" + poolNumber.getAndIncrement() + "-app-";
	}

	
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new DsfThread(group, r, namePrefix + threadNumber.getAndIncrement());
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}

}
