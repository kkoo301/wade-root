package com.wade.relax.registry.consumer.lb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.org.apache.commons.lang3.StringUtils;

import com.wade.relax.registry.consumer.SockSite;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: 中心
 * @auth: steven.zhou
 * @date: 2015-11-30
 */
public class Center {

	private static final Logger LOG = LoggerFactory.getLogger(Center.class);
			
	/**
	 * 中心名
	 */
	private String name;

	/**
	 * 中心地址集
	 */
	private SockSite[] sites;

	/**
	 * 互斥锁
	 */
	private Lock lock = new ReentrantLock();
	
	/**
	 * 偏移位置
	 */
	private long offset = 0L;

	/**
	 * 获取中心名
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置中心名
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 更新中心地址集合
	 * 
	 * @param sites
	 */
	public void setSites(SockSite[] sites) {
		
		if (null == sites) {
			return;
		}
		
		try {
			lock.lock();
			this.sites = sites;
		} finally {
			lock.unlock();
		}
		
	}

	/**
	 * 获取该中心下一个可用实例的地址
	 * 
	 * @return
	 */
	public SockSite next() {

		try {

			lock.lock();

			if (0 == sites.length) {
				throw new IllegalStateException("中心: " + this.name + ", 无可用实例!");
			}
			
			int pos = (int) (this.offset++ % sites.length);
			return sites[pos];

		} finally {
			lock.unlock();
		}

	}

	public void healthCheck() {
		
		Set<SockSite> actives = new HashSet<SockSite>(); // 活跃的
		Set<SockSite> unkowns = new HashSet<SockSite>(Arrays.asList(sites)); // 待检查的
		for (SockSite ss : unkowns) {
			if (HealthCheck.isActive(ss)) {
				actives.add(ss);
			} else {
				LOG.warn("系统剔除已失效的APP地址: " + ss.toString() + ", 中心: " + this.name);
			}
		}
		
		try {
			lock.lock();
			this.sites = actives.toArray(new SockSite[] {});
		} finally {
			lock.unlock();
		}
		
		actives.clear();
		unkowns.clear();
	}
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(200);
		for (SockSite site : this.sites) {
			sb.append(site);
			sb.append(",");
		}

		String strSites = StringUtils.stripEnd(sb.toString(), ",");
		return "name: " + this.name + ", position: " + this.offset + ", sites: " + strSites;

	}
}
