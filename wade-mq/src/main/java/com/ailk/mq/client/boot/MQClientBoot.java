package com.ailk.mq.client.boot;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.GlobalCfg;
import com.ailk.mq.client.config.ClientRuntimeEnv;
import com.ailk.mq.client.listeners.QueueListener;
import com.ailk.mq.server.config.TopicDefinition;
import com.ailk.org.apache.commons.lang3.StringUtils;

/** 
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: MQClientBoot
 * @description: MQ客户端启动类。
 *  
 *  如果需集成MQ功能，在系统启动时需由框架主动调用该类的startup()方法进行MQ链接挂接。
 *  相当于MQ客户端上线注册。
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public class MQClientBoot {
	
	private static final Logger LOG = LoggerFactory.getLogger(MQClientBoot.class);
	private static String serverName;
	
	private MQClientBoot() {
		
	}
		
	/**
	 * 启动MQ客户端
	 */
	public static void startup() {
		
		String isBrokerClientBoot = GlobalCfg.getProperty("broker.client.boot", "true");
		if ("false".equals(isBrokerClientBoot)) {
			LOG.info("Broker Client Boot 被禁用!");
			return;
		}
		
		
		if (ClientRuntimeEnv.getInstance().isOnlineTest()) {
			LOG.info("在线测试实例，无需启动MQClientBoot.startup()");
			return;
		}
		
		MQClientBoot client = new MQClientBoot();
			
		serverName = ClientRuntimeEnv.getInstance().getServerName(); // 获取JVM服务名
		if (StringUtils.isBlank(serverName)) {
			throw new NullPointerException("初始化MQ连接时发生错误,启动参数中未定义wade.server.name参数!");
		}
		
		Set<String> topics = TopicDefinition.subscibeTopics(serverName); // 获取该实例订阅的主题
		
		/** 启动集群队列监听 */
		client.subscribeTopic(topics);
	}
			
	/**
	 * 监听主题
	 * 
	 * @param queueNames
	 */
	public void subscribeTopic(Set<String> topics) {
				
		for (String topic : topics) {			
			QueueListener listener = new QueueListener(topic);
			listener.start();
			LOG.info("subscribe topic: " + topic);
		}
		
		LOG.info("[" +serverName +"]消费者启动成功!");
	}
	
	public static void main(String[] args) throws IOException {
		MQClientBoot.startup();
	}
	
}