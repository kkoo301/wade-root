package com.wade.relax.registry.consumer.lb.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.relax.registry.consumer.SockSite;
import com.wade.relax.registry.consumer.lb.ILoadBalance;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: 负载均衡抽象类
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public abstract class AbstractLoadBalance implements ILoadBalance {
	
	/** 
	 * 解析字符串地址为SockSite对象
	 * 
	 * @param address
	 * @return
	 */
	public SockSite[] parse(Set<String> address) {
		
		int size = address.size();
		SockSite[] sites = new SockSite[size];
		
		int i = 0;
		for (String entity : address) {
			
			String[] slice = StringUtils.split(entity, ':');
			String ip = slice[0];
			int port = Integer.parseInt(slice[1]);
			
			sites[i++] = new SockSite(ip, port);
		}
		
		List<SockSite> list = Arrays.asList(sites);
		Collections.shuffle(list);
		sites = list.toArray(new SockSite[0]);
		
		return sites;
	}
	
}
