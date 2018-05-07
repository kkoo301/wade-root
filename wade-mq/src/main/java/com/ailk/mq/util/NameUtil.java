package com.ailk.mq.util;

import java.util.HashSet;
import java.util.Set;

import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: NameUtil
 * @description: 命名工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-4-2
 */
public final class NameUtil {
	
	/**
	 * 根据服务名，返回对应的私有队列名
	 * 
	 * @param serverName
	 * @return
	 */
	public static final String spellPrivateQueueName(final String serverName) {
		
		if (StringUtils.isBlank(serverName)) {
			throw new NullPointerException("服务名serverName不能为空!");
		}
		
		return "private|" + serverName;
	}
	
	/**
	 * 根据组名，返回对应的集群任务队列名
	 * 
	 * @param clusterName
	 * @return
	 */
	public static final String spellClusterTaskQueueName(final String clusterName) {
		if (StringUtils.isBlank(clusterName)) {
			throw new NullPointerException("集群名clusterName不能为空!");
		}
		return "clustertask|" + clusterName;
	}
	
	/**
	 * 根据组名，返回对应的集群任务队列名
	 * 
	 * @param clusterNames
	 * @return
	 */
	public static final Set<String> spellClusterTaskQueueName(Set<String> clusterNames) {
		
		if (null == clusterNames) {
			throw new NullPointerException("集群名不能为空!");
		}
		
		Set<String> clusterQueueNames = new HashSet<String>();
		for (String clusterName : clusterNames) {
			String clusterQueueName = spellClusterTaskQueueName(clusterName);
			clusterQueueNames.add(clusterQueueName);
		}
		
		return clusterQueueNames;
	}
		
	public static final String spellThreadName(final String queueName) {
		
		if (StringUtils.isBlank(queueName)) {
			throw new NullPointerException("queueName名不能为空!");
		}
		
		return queueName + "|listener";
	}
	
}
