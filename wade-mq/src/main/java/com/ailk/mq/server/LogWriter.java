package com.ailk.mq.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.mq.client.LogInfo;
import com.ailk.mq.server.config.AsyncTaskConfig;
import com.ailk.mq.util.KafkaUtil;
import com.ailk.org.apache.commons.lang3.SerializationUtils;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: LogWriter
 * @description: 任务日志记录
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-7-21
 */
public final class LogWriter extends Thread {

	public static final String TOPIC_ASYNCTASK_LOG = "topic-async-task-log";
	private static final Logger log = LoggerFactory.getLogger(LogWriter.class);
	
	
	public LogWriter() {
		super("log-writer");
	}

	/**
	 * 任务发送日志
	 * 
	 * @param taskId
	 * @param param
	 */
	public void taskSendLog(LogInfo param) {

		String strLogId = param.getLogid();
		String strTaskId = param.getTaskid();
		String executor = param.getExecutor();
		String strParam = param.getParam().toString();
		strParam = StringUtils.substring(strParam, 0, 3200);
		Time sendTime = new Time(param.getSendTime());

		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "INSERT INTO " + AsyncTaskConfig.ASYNC_TASK_LOG + "(LOG_ID, TASK_ID, EXECUTOR, RESULTINFO, PARAMS, SEND_TIME, STATE) VALUES(?,?,?,?,?,?,?)";

		try {

			conn = ConnectionManagerFactory.getConnectionManager().getConnection("cen1");
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, Long.parseLong(strLogId));
			stmt.setLong(2, Long.parseLong(strTaskId));
			stmt.setString(3, executor);
			stmt.setString(4, "ACCEPTED");
			stmt.setString(5, strParam);
			stmt.setTime(6, sendTime);
			stmt.setString(7, "A");

			stmt.execute();
			conn.commit();

		} catch (Exception e) {
			StringBuilder error = new StringBuilder();
			error.append("sql:").append(sql).append("\n");
			error.append("bind 1 ").append(strLogId).append("\n");
			error.append("bind 2 ").append(strTaskId).append("\n");
			error.append("bind 3 ").append(executor).append("\n");
			error.append("bind 4 ").append("ACCEPTED").append("\n");
			error.append("bind 5 ").append(strParam).append("\n");
			error.append("bind 6 ").append(sendTime).append("\n");
			error.append("bind 7 ").append("A").append("\n");
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			}
			log.error(error.toString(), e);
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 消费者未准备好日志
	 * 
	 * @param taskId
	 * @param param
	 */
	public void taskNotPreparedLog(LogInfo param) {

		String strLogId = param.getLogid();
		String executor = param.getExecutor();
		Time startTime = new Time(param.getStartTime());

		Connection conn = null;
		PreparedStatement stmt = null;

		String sql = "UPDATE " + AsyncTaskConfig.ASYNC_TASK_LOG + " set EXECUTOR=?, RESULTINFO=?, START_TIME=?, STATE=? where LOG_ID=?";
		try {

			conn = ConnectionManagerFactory.getConnectionManager().getConnection("cen1");
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, executor);
			stmt.setString(2, "NOTPREPARED");
			stmt.setTime(3, startTime);
			stmt.setString(4, "X");
			stmt.setString(5, strLogId);
			stmt.execute();
			conn.commit();

		} catch (Exception e) {
			StringBuilder error = new StringBuilder();
			error.append("sql:").append(sql).append("\n");
			error.append("bind 1 ").append(executor).append("\n");
			error.append("bind 2 ").append("NOTPREPARED").append("\n");
			error.append("bind 3 ").append(startTime).append("\n");
			error.append("bind 4 ").append("X").append("\n");
			error.append("bind 5 ").append(strLogId).append("\n");

			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			}

			log.error(error.toString(), e);
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 任务超时日志
	 * 
	 * @param taskId
	 * @param param
	 */
	public void taskTimeoutLog(LogInfo param) {

		String strLogId = param.getLogid();
		String executor = param.getExecutor();
		Time startTime = new Time(param.getStartTime());

		Connection conn = null;
		PreparedStatement stmt = null;

		String sql = "UPDATE " + AsyncTaskConfig.ASYNC_TASK_LOG + " set EXECUTOR=?, RESULTINFO=?, START_TIME=?, STATE=? where LOG_ID=?";
		try {

			conn = ConnectionManagerFactory.getConnectionManager().getConnection("cen1");
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, executor);
			stmt.setString(2, "TIMEOUT");
			stmt.setTime(3, startTime);
			stmt.setString(4, "X");
			stmt.setString(5, strLogId);
			stmt.execute();
			conn.commit();

		} catch (Exception e) {
			StringBuilder error = new StringBuilder();
			error.append("sql:").append(sql).append("\n");
			error.append("bind 1 ").append(executor).append("\n");
			error.append("bind 2 ").append("TIMEOUT").append("\n");
			error.append("bind 3 ").append(startTime).append("\n");
			error.append("bind 4 ").append("X").append("\n");
			error.append("bind 5 ").append(strLogId).append("\n");

			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			}

			log.error(error.toString(), e);
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 任务开始日志
	 * 
	 * @param taskId
	 * @param param
	 */
	public void taskStartLog(LogInfo param) {

		String strLogId = param.getLogid();
		String executor = param.getExecutor();
		Time startTime = new Time(param.getStartTime());

		Connection conn = null;
		PreparedStatement stmt = null;

		String sql = "UPDATE " + AsyncTaskConfig.ASYNC_TASK_LOG + " set EXECUTOR=?, RESULTINFO=?, START_TIME=?, STATE=? where LOG_ID=?";
		try {

			conn = ConnectionManagerFactory.getConnectionManager().getConnection("cen1");
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, executor);
			stmt.setString(2, "RUNNING");
			stmt.setTime(3, startTime);
			stmt.setString(4, "R");
			stmt.setString(5, strLogId);
			stmt.execute();
			conn.commit();

		} catch (Exception e) {
			StringBuilder error = new StringBuilder();
			error.append("sql:").append(sql).append("\n");
			error.append("bind 1 ").append(executor).append("\n");
			error.append("bind 2 ").append("RUNNING").append("\n");
			error.append("bind 3 ").append(startTime).append("\n");
			error.append("bind 4 ").append("R").append("\n");
			error.append("bind 5 ").append(strLogId).append("\n");

			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			}

			log.error(error.toString(), e);
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 任务结束日志
	 * 
	 * @param taskId
	 * @param param
	 */
	public void taskEndLog(LogInfo param) {

		String strLogId = param.getLogid();
		Time endTime = new Time(param.getEndTime());
		String resultInfo = param.getResultInfo();
		resultInfo = StringUtils.substring(resultInfo, 0, 1500);
		String state = param.getState();

		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "UPDATE " + AsyncTaskConfig.ASYNC_TASK_LOG + " set RESULTINFO=?, END_TIME=?, STATE=? where LOG_ID=?";
		try {

			conn = ConnectionManagerFactory.getConnectionManager().getConnection("cen1");
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, resultInfo);
			stmt.setTime(2, endTime);
			stmt.setString(3, state);
			stmt.setLong(4, Long.parseLong(strLogId));
			stmt.execute();
			conn.commit();

		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			}

			StringBuilder error = new StringBuilder();
			error.append("sql:").append(sql).append("\n");
			error.append("bind 1 ").append(resultInfo).append("\n");
			error.append("bind 2 ").append(endTime).append("\n");
			error.append("bind 3 ").append(state).append("\n");
			error.append("bind 4 ").append(strLogId)
					.append("\n");

			log.error(error.toString(), e);
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void run() {

		log.info("异步日志记录线程启动成功!");

		try {
			
			ConsumerConfig config = KafkaUtil.createConsumerConfig("topic-async-task-log-group1");
			ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);

			Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
			topicCountMap.put(TOPIC_ASYNCTASK_LOG, new Integer(1));
			Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);

			KafkaStream<byte[], byte[]> stream = consumerMap.get(TOPIC_ASYNCTASK_LOG).get(0);
			ConsumerIterator<byte[], byte[]> it = stream.iterator();
			while (it.hasNext()) {
				MessageAndMetadata<byte[], byte[]> mam = it.next();
				byte[] payloadLoginfo = mam.message();
				LogInfo info = (LogInfo) SerializationUtils.deserialize(payloadLoginfo);
				
				log.debug("处理日志信息：action={},taskId={},logId={}", new String[] {String.valueOf(info.getAction()), info.getTaskid(), info.getLogid()});

				int action = info.getAction();

				switch (action) {
				case LogInfo.ACTION_SEND:
					taskSendLog(info);
					break;
				case LogInfo.ACTION_START:
					taskStartLog(info);
					break;
				case LogInfo.ACTION_END:
					taskEndLog(info);
					break;
				case LogInfo.ACTION_TIMEOUT:
					taskTimeoutLog(info);
					break;
				case LogInfo.ACTION_NOT_PREPARED:
					taskNotPreparedLog(info);
					break;
				}

			}
			
		} catch (Exception e) {
			log.error("日志事件监听线程发生异常!", e);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

	}
}
