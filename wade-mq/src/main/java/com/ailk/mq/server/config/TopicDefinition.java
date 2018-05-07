package com.ailk.mq.server.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mq.server.boot.MQServerBoot;
import com.wade.zkclient.IZkClient;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: TopicDefinition
 * @description: 集群定义
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class TopicDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(TopicDefinition.class);
	private static final IZkClient zkClient = MQServerBoot.zkClient;
	public static final String ZK_PATH_MQ = "/wade-mq";
		
	/**
	 * 根据实例名获取订阅的主题
	 * 
	 * @param serverName
	 * @return
	 */
	public static final Set<String> subscibeTopics(final String serverName) {
		Set<String> rtn = new HashSet<String>();
		
		List<String> topics = zkClient.getChildren(ZK_PATH_MQ + "/topic");
		for (String topic : topics) {
			if (zkClient.exists(ZK_PATH_MQ + "/topic/" + topic + "/" + serverName)) {
				rtn.add(topic);
			}
		}
		
		return rtn;
	}
	
	/**
	 * 加载配置
	 */
	public static final void reload() {
		
		SAXBuilder builder = new SAXBuilder();
		
		try {
			
			if (zkClient.exists(ZK_PATH_MQ + "/topic")) {
				zkClient.deleteRecursive(ZK_PATH_MQ + "/topic");
			}
			
			Document doc = builder.build(TopicDefinition.class.getClassLoader().getResourceAsStream("topic-definition.xml"));
			Element root = doc.getRootElement();

			List<Element> topics = root.getChildren("topic");
			for (Element topic : topics) {

				String topicName = topic.getAttributeValue("name");
				zkClient.createPersistent(ZK_PATH_MQ + "/topic/" + topicName, true);
				
				List<Element> consumers = topic.getChildren("consumer");
				for (Element consumer : consumers) {
					String serverName = consumer.getAttributeValue("name");
										
					zkClient.createPersistent(ZK_PATH_MQ + "/topic/" + topicName + "/" + serverName, true);
				}
			}
			
			LOG.info("集群关系配置成功加载!");
			
		} catch (JDOMException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

	}

}
