package com.ailk.mq.client.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import com.ailk.mq.client.listeners.callable.TaskExecuteCallable;
import com.ailk.mq.client.Message;
import com.ailk.mq.util.KafkaUtil;
import com.ailk.org.apache.commons.lang3.SerializationUtils;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: QueueListener
 * @description: 队列监听器
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public class QueueListener extends Thread {
	
	private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);
	
	/**
	 * 线程池，用来执行异步任务
	 */
	private static final ExecutorService executor = new ThreadPoolExecutor(5, 5, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(500));
	
	/**
	 * 主题名
	 */
	private String topic;
		
	/**
	 * 
	 * @param queueName 队列名
	 */
	public QueueListener(String topic) {
		
		// 设置线程名
		super("topic-" + topic + "|listener");
		this.topic = topic;
	}
	
	public void run() {
		
		ConsumerConfig config = KafkaUtil.createConsumerConfig("default-group");
		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			MessageAndMetadata<byte[], byte[]> mam = it.next();
			byte[] payloadMessage = mam.message();
			Message message = (Message)SerializationUtils.deserialize(payloadMessage);
			consumeAsyncMessage(message);
			LOG.info("consume, partition: " + mam.partition() + ", taskid: " + message.getTaskId());
		}
		
	}

	/**
	 * 消费异步消息
	 * 
	 * @param message
	 */
	public static void consumeAsyncMessage(Message message) {
		TaskExecuteCallable callable = new TaskExecuteCallable(message);
		FutureTask<TaskExecuteCallable> task = new FutureTask<TaskExecuteCallable>(callable);
		executor.execute(task); // 放到线程池中去执行
	}

}