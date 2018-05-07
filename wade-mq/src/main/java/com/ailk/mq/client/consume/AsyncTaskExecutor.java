package com.ailk.mq.client.consume;

import java.util.Map;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mq.client.consume.IAsyncTaskExecutor;
import com.ailk.mq.client.config.ClientRuntimeEnv;
import com.ailk.mq.client.Message;
import com.ailk.mq.util.KafkaUtil;
import com.ailk.mq.util.LogUtil;
import com.ailk.mq.server.LogWriter;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: AsyncTaskExecutor
 * @description: 异步任务执行者抽象类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public abstract class AsyncTaskExecutor implements IAsyncTaskExecutor {
	
	private static final Logger LOG = LoggerFactory.getLogger(AsyncTaskExecutor.class);
	
	private String taskLogId;
	private long timeoutSecond;
	private String resultInfo = "SUCCESS"; 
	
	/**
	 * 异步任务执行前处理
	 * 
	 * @param taskId
	 * @param message
	 */
	@Override
	public boolean doBefore(String taskId, Message message) {
		
		// 任务开始时，记录开始日志		
		this.taskLogId = message.getTaskLogId();
		this.timeoutSecond = message.getTimeoutSecond();
		String serverName = ClientRuntimeEnv.getInstance().getServerName();
				
		String strPrepared = System.getProperty("isPrepared");
		boolean prepared = (strPrepared != null);
		
		if (ClientRuntimeEnv.getInstance().isProductMode() && !prepared) {
			LOG.error("生产模式下，进程未预热完，异步任务不做处理: " + message);
			
			byte[] payload = LogUtil.buildNotPreparedLog(taskLogId, taskId, serverName);
			Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
			producer.send(new KeyedMessage<byte[], byte[]>(LogWriter.TOPIC_ASYNCTASK_LOG, taskLogId.getBytes(), payload));
			
			return false;
		}
		
		long sendTime = message.getSendtime(); // 发送时间
		
		if ((System.currentTimeMillis() - sendTime) > message.getTtl()) {
			LOG.error("异步任务已超时! " + message);
						
			byte[] payload = LogUtil.buildTimeoutLog(taskLogId, taskId, serverName);
			Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
			producer.send(new KeyedMessage<byte[], byte[]>(LogWriter.TOPIC_ASYNCTASK_LOG, taskLogId.getBytes(), payload));
			
			return false; // 消息超时，不做处理!
		}

		byte[] payload = LogUtil.buildStartLog(taskLogId, taskId, serverName);
		Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
		producer.send(new KeyedMessage<byte[], byte[]>(LogWriter.TOPIC_ASYNCTASK_LOG, taskLogId.getBytes(), payload));
		return true;
	}
	
	/**
	 * 业务侧实现的异步任务接口
	 */
	@Override
	public abstract void doAsyncTask(String taskId, Map<String, Object> param) throws Exception;
	
	/**
	 * 异步任务执行后处理
	 * 
	 * @param taskId
	 * @param message
	 */
	@Override
	public void doAfter() {
		// 任务结束时，记录结束日志
		byte[] payload = LogUtil.buildEndLog(taskLogId, resultInfo, "F");
		Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
		producer.send(new KeyedMessage<byte[], byte[]>(LogWriter.TOPIC_ASYNCTASK_LOG, taskLogId.getBytes(), payload));
	}
	
	@Override
	public String getResultInfo() {
		return resultInfo;
	}

	@Override
	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}
	
	public String getTaskLogId() {
		return taskLogId;
	}

	public long getTimeoutSecond() {
		return timeoutSecond;
	}
	
}
