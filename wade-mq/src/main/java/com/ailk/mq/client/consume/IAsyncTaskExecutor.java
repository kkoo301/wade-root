package com.ailk.mq.client.consume;

import java.util.Map;
import com.ailk.mq.client.Message;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: IAsyncTaskExecutor
 * @description: 异步任务执行者接口
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public interface IAsyncTaskExecutor {
	
	/**
	 * do before
	 * 
	 * @param taskId
	 * @param message
	 * @return true 往下继续执行doAsyncTask；否则，不执行。
	 */
	public boolean doBefore(String taskId, Message message);
	
	/**
	 * 异步任务接口
	 * 
	 * @param taskId
	 * @param param
	 */
	public void doAsyncTask(String taskId, Map<String, Object> param) throws Exception;
	
	/**
	 * do after
	 */
	public void doAfter();
	
	public String getResultInfo();

	public void setResultInfo(String resultInfo);
}
