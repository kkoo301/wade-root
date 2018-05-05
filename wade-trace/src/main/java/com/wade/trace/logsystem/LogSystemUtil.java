package com.wade.trace.logsystem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ailk.org.apache.commons.io.FilenameUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.wade.trace.TraceContext;
import com.wade.trace.util.SystemUtil;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LogSystemUtil
 * @description: 日志系统工具类
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class LogSystemUtil {

	private static final Lock lock = new ReentrantLock();

	/**
	 * 日志缓冲区
	 */
	private static int bufferSize = 1024;

	/**
	 * 日志目录
	 */
	private static final String logDirectory = TraceContext.getLogDirectory();

	/**
	 * 日志文件后缀
	 */
	private static final String suffix = ".dat";

	/**
	 * 日志最大保留数
	 */
	private static final int maxBackupIndex = TraceContext.getMaxBackupIndex();

	/**
	 * 时间戳
	 */
	private static String timestamp;

	private static FileOutputStream fos;
	private static BufferedOutputStream bos;
	private static ObjectOutputStream oos;

	static {

		// 启动计时器
		new Watch().start();

		try {
			LogSystemUtil.timestamp = Watch.timestamp;
			String logFileName = buildLogFileName(LogSystemUtil.timestamp);
			fos = new FileOutputStream(logFileName);
			bufferSize = TraceContext.getBufferSize();
			if (bufferSize <= 0) {
				bufferSize = 8182;
			}
			bos = new BufferedOutputStream(fos, bufferSize);
			oos = new ObjectOutputStream(bos);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送日志
	 * 
	 * @param loginfo
	 */
	public static final void send(Map<String, Object> loginfo) {
		rollOver();
		try {
			lock.lock();
			oos.writeObject(loginfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 日志轮替
	 */
	private static void rollOver() {

		if (LogSystemUtil.timestamp.equals(Watch.timestamp)) {
			return;
		}

		LogSystemUtil.timestamp = Watch.timestamp;

		try {

			lock.lock();

			oos.flush();
			oos.close();

			fos = new FileOutputStream(buildLogFileName(LogSystemUtil.timestamp));
			bos = new BufferedOutputStream(fos, bufferSize);
			oos = new ObjectOutputStream(bos);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 删除备份
	 */
	private static void removeBackups() {

		final int MILLIS_OF_TEN_MIN = 1000 * 60 * 10;
		long now = System.currentTimeMillis();

		for (int i = 100; i >= maxBackupIndex; i--) {

			long timestamp = now - (MILLIS_OF_TEN_MIN * i);
			String time = DateFormatUtils.format(timestamp, "MMddHHmm");
			time = time.substring(0, 7) + "0";

			String fileName = buildLogFileName(time);
			String fileNameOver = fileName + ".over";

			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}

			File fileOver = new File(fileNameOver);
			if (fileOver.exists()) {
				fileOver.delete();
			}

		}

	}

	/**
	 * 拼日志文件名
	 * 
	 * @return
	 */
	private static String buildLogFileName(String timestamp) {
		String name = "bomc." + SystemUtil.getServerName() + "." + timestamp + suffix;
		String logFileName = FilenameUtils.concat(logDirectory, name);
		return logFileName;
	}

	/**
	 * Copyright: Copyright (c) 2015 Asiainfo
	 * 
	 * @className: Watch
	 * @description: 计时器
	 * 
	 * @version: v1.0.0
	 * @author: steven.zhou
	 * @date: 2015-5-12
	 */
	private static class Watch extends Thread {

		/**
		 * 滴答周期
		 */
		private static final int TICK_CYCLE = 10;

		/**
		 * 当前时间戳 MMddHHm0
		 */
		private static String timestamp;

		public Watch() {

			this.setName("Watch");
			tick();

		}

		/**
		 * 滴答动作
		 */
		private void tick() {
			long now = System.currentTimeMillis();
			Watch.timestamp = DateFormatUtils.format(now, "MMddHHmm");
			Watch.timestamp = Watch.timestamp.substring(0, 7) + "0";
		}

		@Override
		public void run() {

			long i = 0L;
			while (true) {
				try {
					tick();
					Thread.sleep(TICK_CYCLE * 1000);

					lock.lock();
					oos.flush();

					if (i++ % 60L == 0) { // 10分钟触发一次
						removeBackups();
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}

	}

	public static void main(String[] args) {

		System.out.println("MILLIS_OF_TEN_MIN=" + maxBackupIndex);
		System.out.println("start..");
		System.out.println("start..");
		System.out.println("start..");

		while (true) {

			Map<String, Object> info = new HashMap<String, Object>();
			info.put("TRACE_ID", UUID.randomUUID().toString());
			info.put("SERVER_NAME", "CS.QueryUser");
			info.put("START_TIME", DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));

			System.out.print("发送开始  --- ");
			send(info);
			System.out.println(" 发送结束!");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
