/**
 * $
 */
package com.wade.httprpc.test.client;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import com.wade.httprpc.client.conn.config.HttpConfigure;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestHttpClient.java
 * @description: Http客户端测试类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-22
 */
public class TestStringClient {
	
	public static AtomicLong cnt = new AtomicLong(0);
	public static AtomicLong suc = new AtomicLong(0);
	public static AtomicLong err = new AtomicLong(0);
	public static AtomicLong cost = new AtomicLong(0);
	
	public static void main(String[] args) throws Exception {
		String httpServerAddr = "http://10.174.43.37:10003/httpserver";
		
		/*for (;;) {
			String res = HttpConfigure
				.build(httpServerAddr)
				.addHeader("name1", "value1")	//扩展的HTTP请求头信息
				.setPoolInitSize(1)
				.setPoolMaxSize(10)
				.setPoolIncrementSize(1)
				.setSocketPool()				//避免重复创建Socket对象, 提升
				.createConnection()
				.post("abcdefg");				//需要发送的数据
			
			System.out.println(res);
			Thread.sleep(1000);
		}*/
		startTimer();
		Thread.sleep(10000);
		parallel(httpServerAddr);
	}
	
	/**
	 * POOL: 20并发1K数据, 每个并发跑10000次, 成功率:100%, 总耗时:1987728, 平均耗时:7.121ms
	 * POOL: 20并发0.1K数据, 每个并发跑10000次, 成功率:100%, 总耗时:1987728, 平均耗时:7.121ms
	 * 
	 * SOCK: 10并发1K数据, 每个并发跑10000次, 成功率:100%, 总耗时:3509545, 平均耗时:16.169ms
	 * 
	 * @param httpServerAddr
	 * @throws IOException
	 */
	public static void parallel(final String httpServerAddr) throws IOException {
		for (int i = 0; i < 10; i++) {
			new Thread(String.valueOf(i)) {
				public void run() {
					long startTime = System.currentTimeMillis();
					for (long j=0, size = 10000L; j < size; j++) {
						cnt.addAndGet(1);
						try {
							StringBuilder data = new StringBuilder(1024);
							for (int k = 0, len = 10; k < len; k ++) {
								for (int m = 0; m < 1; m++) {
									data.append(k);
								}
							}
							String res = HttpConfigure
								.build(httpServerAddr)
								.addHeader("name1", "value1")
								
								.setConnectTimeout(3000)
								.setSoTimeout(3000)
								
								.setPoolInitSize(100)
								.setPoolMaxSize(1000)
								.setPoolIncrementSize(100)
								.createSocketPool()
								
								.createConnection()
								.post(data.toString());
							if (res.getBytes().length == data.toString().getBytes().length) {
								suc.addAndGet(1);
							}
						} catch (Exception e) {
							err.addAndGet(1);
							e.printStackTrace();
						}
					}
					long endTime = (System.currentTimeMillis() - startTime);
					
					System.out.println(String.format("Thread " + Thread.currentThread().getName() + " cost %d ms.", endTime));
					
					cost.addAndGet(endTime);
				}
			}.start();
		}
	}
	
	
	private static void startTimer() {
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				System.out.println(String.format("cnt: %d, suc: %d, err: %d, cost: %d ms", cnt.get(), suc.get(), err.get(), cost.get()));
			}
		};
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 10000, 10000);
	}
}
