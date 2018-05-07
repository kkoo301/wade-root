package com.ailk.mq.client.listeners.callable;

import java.util.Map;
import java.util.concurrent.Callable;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mq.client.Message;
import com.ailk.mq.client.config.AsyncTask;
import com.ailk.mq.client.config.ClientRuntimeEnv;
import com.ailk.mq.client.consume.IAsyncTaskExecutor;
import com.ailk.mq.util.ErrorUtil;
import com.ailk.mq.util.KafkaUtil;
import com.ailk.mq.util.LogUtil;
import com.ailk.mq.server.LogWriter;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: TaskExecuteCallable
 * @description: 异步任务执行Callable
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public class TaskExecuteCallable implements Callable {

	private static final Logger log = LoggerFactory.getLogger(TaskExecuteCallable.class);
	private Message message;
	
	public TaskExecuteCallable(Message message) {
		this.message = message;
	}
	
	@Override
	public Object call() throws Exception {
		
		String taskId = message.getTaskId();
		String taskLogId = message.getTaskLogId();
		Map<String, Object> param = message.getParam();
		
		AsyncTask task = ClientRuntimeEnv.getInstance().getAsyncTaskById(taskId);
		String className = task.getClassName();
		
		IAsyncTaskExecutor executor = null;
		
		try {
			Class clazz = Class.forName(className);
			executor = (IAsyncTaskExecutor) clazz.newInstance();
			if (!executor.doBefore(taskId, message)) {
				return null; 
			}
			executor.doAsyncTask(taskId, param);
			executor.doAfter();
		} catch (Exception e) {
			log.error("异步任务执行失败!", e);
			byte[] payload = LogUtil.buildEndLog(taskLogId, ErrorUtil.exceptionDetail(e), "X");
			Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
			producer.send(new KeyedMessage<byte[], byte[]>(LogWriter.TOPIC_ASYNCTASK_LOG, taskLogId.getBytes(), payload));
			executor.setResultInfo(System.getProperty("wade.server.name") + "\n" + e.getMessage());
		}
		
		return null;
	}
	
}
