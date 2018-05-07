package com.ailk.mq.util;

import java.util.Map;
import java.util.Random;

import com.ailk.mq.client.Message;
import com.ailk.org.apache.commons.lang3.SerializationUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: MsgUtil
 * @description: 消息相关的工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-4-2
 */
public final class MsgUtil {
	
	private static final Random rand = new Random();
	
	/**
	 * 构造一个异步消息
	 * 
	 * @param taskId
	 * @param taskLogId
	 * @param param
	 * @param secTTL
	 * @return
	 */
	public static final byte[] buildMessage(String taskId, String taskLogId, Map<String, Object> param, long secTTL, long timeoutSecond) {
		
		long currTime = System.currentTimeMillis();
				
		Message message = new Message();
		message.setTaskId(taskId);
		message.setTaskLogId(taskLogId);
		message.setTtl(secTTL);
		message.setTimeoutSecond(timeoutSecond);
		message.setSendtime(currTime);
		message.setParam(param);
		
		return SerializationUtils.serialize(message);
	}
	
	public static final String createLogId() {
		return System.currentTimeMillis() + "" + StringUtils.leftPad("" + rand.nextInt(100000), 5, '0');
	}
	
}
