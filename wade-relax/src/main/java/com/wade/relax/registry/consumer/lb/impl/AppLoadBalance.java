package com.wade.relax.registry.consumer.lb.impl;

import java.util.HashMap;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.relax.registry.consumer.lb.Center;
import com.wade.relax.registry.consumer.SockSite;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: App负载均衡类
 * @auth: steven.zhou
 * @date: 2015-11-30
 */
public class AppLoadBalance extends AbstractLoadBalance {
	
	private static final Logger LOG = LoggerFactory.getLogger(AppLoadBalance.class);
	
	/**
	 * 中心名->中心对象
	 */
	private static final Map<String, Center> CENTERS = new HashMap<String, Center>();
	
	private static final AppLoadBalance instance = new AppLoadBalance();
	
	private AppLoadBalance() {
		// 单例
	}
	
	public static final AppLoadBalance getInstance() {
		return instance;
	}
	
	/**
	 * 获取下一个可用地址
	 * 
	 * @param centerName
	 * @return
	 */
	public static final SockSite next(String centerName) {
		
		if (null == centerName) {
			throw new NullPointerException("中心名为空! centerName=" + centerName);
		}
		
		Center center = CENTERS.get(centerName);
		if (null == center) {
			throw new NullPointerException("根据中心名:" + centerName + ", 无法找到对应的中心信息!");
		}
		
		return center.next();
	}
	
	/**
	 * 更新服务地址
	 * 
	 * @param centerName
	 * @param address
	 */
	public void updateSockSite(String centerName, Set<String> address) {
		
		synchronized (AppLoadBalance.class) {
			
			LOG.debug("中心 {}, 最新地址集: {}", centerName, address);
			
			SockSite[] sites = parse(address);
			
			Center center = CENTERS.get(centerName);
			if (null == center) {
				center = new Center();
				center.setName(centerName);
				center.setSites(sites);
				
				CENTERS.put(centerName, center);
			} else {
				center.setSites(sites);
			}	
		}
		
	}
	
	/**
	 * 对ESB地址做检查检查，剔除已失效的地址
	 */
	@Override
	public void healthCheck() {
		
		for (Center center : CENTERS.values()) {
			center.healthCheck();
		}
		
	}
	
}
