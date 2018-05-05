package com.wade.relax.tm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LocalXGuarder
 * @description: 本地事务超时保障机制
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public final class LocalXGuarder extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(LocalXGuarder.class);


	@Override
	public void run() {

		LOG.info("启动事务保障线程...");

		while (true) {

			try {

				Thread.sleep(1000);

				Set<String> tidSet = LocalXSupervise.LTC.keySet();
				for (String tid : tidSet) {
					if (isTimeout(tid)) {
						LOG.warn("tid: " + tid + " is timeout, system rollback automatically!");
						rollback(tid);
					}
				}

			} catch (Exception e) {
				LOG.error("本地事务保障线程运行出错!", e);
			}

		}

	}

	/**
	 * 回滚超时的本地事务
	 * 
	 * @param tid
	 */
	private static final void rollback(String tid) {

		Map<String, Connection> map = LocalXSupervise.LTC.get(tid);
		for (String key : map.keySet()) {
			Connection conn = map.get(key);
			try {
				conn.rollback();
				LOG.warn("dataSouceName: {} rollback success.", key);
			} catch (SQLException e) {
				LOG.error("dataSouceName: " + key + " rollback failure.", e);
			}
		}

		// 从LTC中清除
		LocalXSupervise.LTC.remove(tid);

	}

	/**
	 * 判断事务是否已超时 <br/>
	 * 
	 * 事务ID示例: tx-567776880e034a4ab821049d6c163d65-1452063586 <br/>
	 * 格式: tx-${uuid}-${开始时间戳}-${超时秒数} <br />
	 * 
	 * @param tid
	 * @return
	 */
	public static final boolean isTimeout(String tid) {

		try {

			String startTime = tid.substring(36, 46);
			String sTimeout = tid.substring(47);
						
			long now = System.currentTimeMillis() / 1000;
			long costtime = now - Long.parseLong(startTime);
			long timeout = Long.parseLong(sTimeout);
			
			if (costtime > timeout) {
				return true;
			}

		} catch (Exception e) {
			LOG.error("", e);
			return true;
		}

		return false;

	}
	
	public static void main(String[] args) {
		isTimeout("tx-567776880e034a4ab821049d6c163d65-1452063586-30099");
	}
	
}
