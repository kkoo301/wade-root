package com.ailk.mq.server.boot;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.GlobalCfg;
import com.ailk.mq.server.LogWriter;
import com.ailk.mq.server.config.TopicDefinition;
import com.ailk.mq.server.config.TaskDefinition;
import com.wade.zkclient.IZkClient;
import com.wade.zkclient.ZkClient;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: MQServerBoot
 * @description: MQ服务端启动
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class MQServerBoot {
	
	public static final IZkClient zkClient = new ZkClient(GlobalCfg.getProperty("zookeeper.addr"), 6000, 5000);
	private static final String WADE_MQ_PATH = "/wade-mq/server";
	private static final Logger LOG = LoggerFactory.getLogger(MQServerBoot.class);
	private static String thisENode;
	
	/**
	 * 是否为主服务
	 * 
	 * @return
	 */
	public static final boolean isLeader() {
		
		List<String> instances = zkClient.getChildren(WADE_MQ_PATH);
		String[] nodes = instances.toArray(new String[instances.size()]);
	    Arrays.sort(nodes);
	    if (thisENode.equals(WADE_MQ_PATH + "/" + nodes[0])) {
	        return true;
	    } else {
	        return false;
	    }
	    
	}
	
	public static void main(String[] args) {
		
		if(!zkClient.exists(WADE_MQ_PATH)) {
			zkClient.createPersistent(WADE_MQ_PATH, true);			
		}

		thisENode = zkClient.createEphemeralSequential(WADE_MQ_PATH + "/instance-", null);
		
		// 配置重载间隔，默认5分钟一次.
		int reloadInterval = 300;
		if (args.length > 0) {
			int i = Integer.parseInt(args[0]);
			if (reloadInterval < i) {
				reloadInterval = i;
			}
		}
		
		if (isLeader()) {
			LOG.info("当前是主服务,加载topic-definition.xml");
			
			// 加载集群关系配置
			TopicDefinition.reload();
		} else {
			LOG.info("当前不是主服务,不加载topic-definition.xml,由主服务加载!");
		}
		
		// 周期性加载任务定义配置
		new TaskDefinition(reloadInterval).start();
		
		// 启动日志记录员
		new LogWriter().start();
		
	}
}
