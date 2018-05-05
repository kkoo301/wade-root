package com.wade.relax.esb.gateway;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.wade.relax.registry.consumer.SockSite;

/**
 * Copyright: Copyright (c) 2017 Asiainfo
 *
 * @desc: ESB监控类
 * @auth: steven.zhou
 * @date: 2017-04-07
 */
public final class EsbMonitor {
	
	// 接入统计，K:来源地址, V:接入次数
	public static Map<String, AtomicLong> srcStat = new HashMap<String, AtomicLong>();
	
	// 转发统计，K:目的地址, V:转发次数
	public static Map<String, AtomicLong> dstStat = new HashMap<String, AtomicLong>();

	/**
	 * 接入统计
	 * 
	 * @param srcIp
	 */
	public static final void incrementSrcCount(String srcIp) {
		AtomicLong cnt = srcStat.get(srcIp);
		if (null == cnt) {
			cnt = new AtomicLong(0);
			srcStat.put(srcIp, cnt);
		}
		cnt.incrementAndGet();
	}
	
	/**
	 * 转发统计
	 * 
	 * @param sockSite
	 */
	public static final void incrementDstCount(SockSite sockSite) {
		
		String dstIp = sockSite.getIp() + ":" + sockSite.getPort();
		AtomicLong cnt = dstStat.get(dstIp);
		if (null == cnt) {
			cnt = new AtomicLong(0);
			dstStat.put(dstIp, cnt);
		}
		cnt.incrementAndGet();
	}

	/**
	 * 获取接入统计数据
	 * 
	 * @return
	 */
	public static final Map<String, Long> getSrcStat() {
		
		Map<String, Long> rtn = new HashMap<String, Long>(srcStat.size());
		
		for (String key : srcStat.keySet()) {
			rtn.put(key, srcStat.get(key).get());
		}
		
		return rtn;
		
	}
	
	/**
	 * 获取转发统计数据
	 * 
	 * @return
	 */
	public static final Map<String, Long> getDstStat() {
		
		Map<String, Long> rtn = new HashMap<String, Long>(dstStat.size());
		
		for (String key : dstStat.keySet()) {
			rtn.put(key, dstStat.get(key).get());
		}
		
		return rtn;
		
	}
}