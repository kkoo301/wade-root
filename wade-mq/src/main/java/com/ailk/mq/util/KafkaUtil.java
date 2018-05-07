package com.ailk.mq.util;

import java.util.Properties;

import com.ailk.common.config.GlobalCfg;

import kafka.consumer.ConsumerConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: KafkaUtil
 * @description: Kafka工具类
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class KafkaUtil {
	
	private static Producer<byte[], byte[]> instance;
	
	static {
		String brokerList = GlobalCfg.getProperty("broker.list");
		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		instance = new Producer<byte[], byte[]>(new ProducerConfig(props));
	}
	
	public static final ConsumerConfig createConsumerConfig(String groupId) {
		Properties props = new Properties();
		props.put("group.id", groupId);
		String zkAddr = GlobalCfg.getProperty("zookeeper.addr") + "/kafka";
		props.put("zookeeper.connect", zkAddr);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "smallest");
		return new ConsumerConfig(props);
	}
	
	public static final Producer<byte[], byte[]> getProducerInstance() {
		return instance;
	}
}
