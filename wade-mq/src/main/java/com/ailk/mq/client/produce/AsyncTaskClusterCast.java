package com.ailk.mq.client.produce;

import java.io.IOException;
import java.util.Map;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mq.client.Message;
import com.ailk.mq.client.config.AsyncTask;
import com.ailk.mq.client.config.ClientRuntimeEnv;
import com.ailk.mq.client.listeners.QueueListener;
import com.ailk.mq.server.LogWriter;
import com.ailk.mq.util.KafkaUtil;
import com.ailk.mq.util.LogUtil;
import com.ailk.mq.util.MsgUtil;
import com.ailk.org.apache.commons.lang3.SerializationUtils;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: AsyncTaskClusterCast
 * @description: 发送至集群的异步任务
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class AsyncTaskClusterCast implements IAsyncMessageClusterCast {
	
	private static final Logger LOG = LoggerFactory.getLogger(AsyncTaskClusterCast.class);
	private static final AsyncTaskClusterCast instance = new AsyncTaskClusterCast();
	
	private AsyncTaskClusterCast() {
		// 工具类，单例。
	}
	
	public static final AsyncTaskClusterCast getInstance() {
		return instance;
	}
	
	@Override
	public String sendAsyncTask(String taskId, Map<String, Object> param) throws IOException {
		return sendAsyncTask(taskId, param, 300 * 1000L);
	}

	@Override
	public String sendAsyncTask(String taskId, Map<String, Object> param, long secTTL) throws IOException {
				
		if (StringUtils.isBlank(taskId)) {
			throw new NullPointerException("taskId不能为空！");
		}
		
		AsyncTask asyncTask = ClientRuntimeEnv.getInstance().getAsyncTaskById(taskId);
		if (null == asyncTask) {
			throw new RuntimeException("根据taskId=" + taskId + ",无法获取对应的任务定义！");
		}
		
		String topic = asyncTask.getDestination();
		String asyncTaskLogId = MsgUtil.createLogId();
		
		long timeoutSecond = asyncTask.getTimeoutSecond();
		param.put("clientServerName", System.getProperty("wade.server.name", ""));
		byte[] payloadMessage = MsgUtil.buildMessage(taskId, asyncTaskLogId, param, secTTL, timeoutSecond);
		byte[] payloadLoginfo = LogUtil.buildSendLog(asyncTaskLogId, taskId, topic, param);
		
		Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
		
		// 发送_ASYNCTASK_LOG消息
		LOG.debug("发送消息，taskId: {}, topic: {}, logId: {}", new String[] {taskId, topic, asyncTaskLogId});
		producer.send(new KeyedMessage<byte[], byte[]>(LogWriter.TOPIC_ASYNCTASK_LOG, asyncTaskLogId.getBytes(), payloadLoginfo));
		
		if (!ClientRuntimeEnv.getInstance().isProductMode()) { // 开发模式下，直接发送给本机
			Message message = (Message)SerializationUtils.deserialize(payloadMessage);
			QueueListener.consumeAsyncMessage(message);
			return asyncTaskLogId;
		}
		
		producer.send(new KeyedMessage<byte[], byte[]>(topic, asyncTaskLogId.getBytes(), payloadMessage));
		
		LOG.debug("发送消息，taskId: {}, topic: {}", taskId, topic);
		return asyncTaskLogId;
	}

}
