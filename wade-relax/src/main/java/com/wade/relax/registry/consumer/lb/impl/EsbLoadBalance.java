package com.wade.relax.registry.consumer.lb.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.relax.registry.consumer.lb.HealthCheck;
import com.wade.relax.registry.consumer.SockSite;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: 负载均衡类
 * @auth: steven.zhou
 * @date: 2015-11-30
 */
public class EsbLoadBalance extends AbstractLoadBalance {
	
	private static final Logger LOG = LoggerFactory.getLogger(EsbLoadBalance.class);
	
	private static final EsbLoadBalance instance = new EsbLoadBalance();
	
	private EsbLoadBalance() {
		// 单例
	}
	
	public static final EsbLoadBalance getInstance() {
		return instance;
	}
	
	/**
	 * ESB地址集
	 */
	private static SockSite[] sites;

	/**
	 * 互斥锁
	 */
	private static Lock lock = new ReentrantLock();
	
	/**
	 * 偏移位置
	 */
	private static long offset = 0L;
	
	/**
	 * 获取下一个可用地址
	 * 
	 * @param centerName
	 * @return
	 */
	public static final SockSite next() {

		try {
			lock.lock();

			if (0 == sites.length) {
				throw new IllegalStateException("无可用ESB实例!");
			}
				
			int pos = (int) (offset++ % sites.length);
			return sites[pos];
		} finally {
			lock.unlock();
		}
		
	}
	
	/**
	 * 更新服务地址
	 * 
	 * @param centerName
	 * @param sites
	 */
	public final void updateSockSite(Set<String> address) {
		
		try {
			lock.lock();
			sites = parse(address);
		} finally {
			lock.unlock();
		}
		
		LOG.debug("ESB最新地址集: {}", address);
		
	}
	
	/**
	 * 对ESB地址做检查检查，剔除已失效的地址
	 */
	@Override
	public void healthCheck() {
		
		Set<SockSite> actives = new HashSet<SockSite>(); // 活跃的
		Set<SockSite> unkowns = new HashSet<SockSite>(Arrays.asList(sites)); // 待检查的
		for (SockSite ss : unkowns) {
			if (HealthCheck.isActive(ss)) {
				actives.add(ss);
			} else {
				LOG.warn("系统剔除已失效的ESB地址: " + ss.toString());
			}
		}
		
		try {
			lock.lock();
			sites = actives.toArray(new SockSite[] {});
		} finally {
			lock.unlock();
		}
		
		actives.clear();
		unkowns.clear();
		
	}
	
}
