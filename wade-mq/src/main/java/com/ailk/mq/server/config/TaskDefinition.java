package com.ailk.mq.server.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.mq.client.config.AsyncTask;
import com.ailk.mq.server.boot.MQServerBoot;
import com.ailk.org.apache.commons.lang3.SerializationUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.zkclient.IZkClient;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: TaskDefinition
 * @description: 异步任务定义
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class TaskDefinition extends Thread {
	
	private static final Logger LOG = LoggerFactory.getLogger(TaskDefinition.class);
	private static final IZkClient zkClient = MQServerBoot.zkClient;
	
	/**
	 * 任务加载周期间隔
	 */
	private int reloadInterval;
	
	public TaskDefinition(int reloadInterval) {
		this.reloadInterval = reloadInterval;
	}
	
	/**
	 * 获取异步任务定义
	 * 
	 * @param taskid
	 * @return
	 */
	public static final AsyncTask getAsyncTaskDefine(String taskid) {
		if (StringUtils.isBlank(taskid)) {
			throw new NullPointerException("taskid不可为空!");
		}
		
		try {
			
			byte[] data = zkClient.readData(TopicDefinition.ZK_PATH_MQ + "/task/" + taskid, true);
			if (null == data) {
				throw new NullPointerException("根据taskid未找到任务定义!");
			}
			
			AsyncTask task = (AsyncTask) SerializationUtils.deserialize(data);
			return task;
						
		} catch (Exception e) {
			LOG.error("获取异步任务定义发生错误!", e);
		}
		
		return null;
	}
	
	/**
	 * 加载异步任务配置数据
	 */
	private static final void reload() {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			conn = ConnectionManagerFactory.getConnectionManager().getConnection("cen1");
			stmt = conn.prepareStatement("SELECT * FROM " + AsyncTaskConfig.ASYNC_TASK + " WHERE STATE = 'U'");
			rs = stmt.executeQuery();

			Map<String, AsyncTask> newTasks = new HashMap<String, AsyncTask>();
			while (rs.next()) {
				String taskid = rs.getString("TASK_ID");
				String taskName = rs.getString("TASK_NAME");
				String destination = rs.getString("DESTINATION");
				String className = rs.getString("CLASS_NAME");
				String timeoutSecond = rs.getString("TIMEOUT_SECOND");
				if (null == timeoutSecond) {
					timeoutSecond = "300";
				}
				String subSysCode = rs.getString("SUBSYS_CODE");
				
				AsyncTask task = new AsyncTask();
				task.setTaskId(taskid);
				task.setTaskName(taskName);
				task.setDestination(destination);
				task.setClassName(className);
				task.setTimeoutSecond(Long.parseLong(timeoutSecond));
				task.setSubSysCode(subSysCode);
				
				newTasks.put(taskid, task);
			}
			
			if (zkClient.exists(TopicDefinition.ZK_PATH_MQ + "/task")) {
				zkClient.deleteRecursive(TopicDefinition.ZK_PATH_MQ + "/task");
			}
			
			zkClient.createPersistent(TopicDefinition.ZK_PATH_MQ + "/task", true);
						
			for (String key : newTasks.keySet()) {
				AsyncTask task = newTasks.get(key);
				String taskid = task.getTaskId();
				
				byte[] data = SerializationUtils.serialize(task);
				zkClient.createPersistent(TopicDefinition.ZK_PATH_MQ + "/task/" + taskid, data);
			}
			
			LOG.info("成功加载异步任务配置:" + newTasks.size() + "条");
			
		} catch (Exception e) {
			LOG.error("加载异步任务配置数据出错!", e);
		} finally {
			try {
				if (null != rs)	rs.close();
				if (null != stmt) stmt.close();
				if (null != conn) conn.close();
			} catch (SQLException e) {
				LOG.error("关闭数据库资源出错!", e);
			}
		}
	}
	
	@Override
	public void run() {
		while (true) {
						
			try {
				
				if (MQServerBoot.isLeader()) {
					LOG.info("当前是主服务,开始周期性加载异步任务定义!");
					reload();
				}
				
				Thread.sleep(1000 * reloadInterval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
