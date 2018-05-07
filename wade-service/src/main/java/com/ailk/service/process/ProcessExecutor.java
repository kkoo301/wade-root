package com.ailk.service.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;


public class ProcessExecutor {

	private static final transient Logger log = Logger.getLogger(ProcessExecutor.class);
	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	private ProcessExecutor() {

	}

	/**
	 * 方法调用超时控制
	 * 
	 * @param instance
	 * @param method
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static boolean invoke(ProcessCallable callable, long timeout) {
		String name = callable.getProcessName();
		boolean result = false;

		if (log.isDebugEnabled()) {
			log.debug("主进程开始执行:[name:" + name + "][subsys:" + callable.getGroup() + "][timeout:" + timeout + "]");
		}

		Future<Boolean> future = null;
		future = executor.submit(callable);

		long start = System.currentTimeMillis();
		try {
			result = future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			if (future != null) {
				callable.cancel(true);
				future.cancel(true);
			}
			
			result = false;
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("主进程执行异常:[name:" + name + "][msg:" + e.getClass() + "@" + e.getMessage() + "]");
			}
			result = false;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("主进程执行完成:[name:" + name + "][costtime:" + (System.currentTimeMillis() - start) + "]");
			}
		}
		return result;
	}

	public static void shutdown() {
		executor.shutdownNow();
	}

}
