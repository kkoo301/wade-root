package com.ailk.mq.client.produce;

import java.io.IOException;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: IAsyncMessageClusterCast
 * @description: 发送至集群的异步消息接口
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public interface IAsyncMessageClusterCast {
	
	/**
	 * 将异步任务发送到一个工作队列，由工作队列进行任务的分派，工作队列在WD_M_ASYNCTASK.DESTINATION中定义
	 * 
	 * @param taskId 任务ID
	 * @param param 参数
	 * @return 发送成功返回流水号，否则返回null
	 */
	public String sendAsyncTask(String taskId, Map<String, Object>param) throws IOException;
	
	/**
	 * 将异步任务发送到一个工作队列，由工作队列进行任务的分派，工作队列在WD_M_ASYNCTASK.DESTINATION中定义
	 * 
	 * @param taskId 任务ID
	 * @param param 参数
	 * @param secTTL 超时秒数(基于发送时间点算起，多少秒超时，超时的消息不会被消费端处理！)
	 * @return 发送成功返回流水号，否则返回null
	 */
	public String sendAsyncTask(String taskId, Map<String, Object>param, long secTTL) throws IOException;
	
}
