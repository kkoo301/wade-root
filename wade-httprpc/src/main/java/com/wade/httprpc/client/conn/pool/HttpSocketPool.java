/**
 * $
 */
package com.wade.httprpc.client.conn.pool;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.wade.httprpc.client.conn.pool.HttpSocketPool;
import com.wade.httprpc.client.conn.pool.SocketPool;
import com.wade.httprpc.client.conn.pool.socket.HttpSocket;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpSocketPool.java
 * @description: 采用先进先出的队列实现Socket池, 默认大小为30
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-2-21
 */
public class HttpSocketPool implements SocketPool {
	
	private Queue<Socket> pool = null;
	
	private String httpServerAddr = null;
	
	private int initSize = 10;
	private int maxSize = 100;
	private int increment = 10;
	
	private boolean init = false;
	
	public HttpSocketPool() {

	}
	
	/**
	 * 创建池
	 */
	public void createPool(int initSize, int maxSize, int increment) {
		synchronized (this) {
			if (!init) {
				this.initSize = initSize;
				this.maxSize = maxSize;
				this.increment = increment;
				
				pool = new ConcurrentLinkedQueue<Socket>();
				
				for (int socketId = 0; socketId < initSize; socketId++) {
					pool.offer(new HttpSocket());
				}
				
				startTimer();
				
				init = true;
			}
		}
	}
	
	/**
	 * 启动定时任务<br>
	 * 1. 输出Pool的当前大小, 即空闲连接数<br>
	 * 2. 当空闲连接数小于maxSize的75%时, 扩容<br>
	 */
	private void startTimer() {
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				int idle = pool.size();
				if (idle <= initSize) {
					for (int i = 0; i < increment; i++) {
						pool.offer(new HttpSocket());
					}
					
					System.out.println(String.format("POOL %30s ADD SIZE %d, INIT SIZE %d, IDLE SIZE %d", getServerAddr(), increment, initSize, pool.size()));
				} else {
					//System.out.println(String.format("POOL %30s IDLE SIZE %d", getServerAddr(), pool.size()));
				}
			}
		};
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}
	
	
	/**
	 * @return the maxSize
	 */
	public int getInitSize() {
		return initSize;
	}
	
	
	/**
	 * @return the httpServerAddr
	 */
	public String getServerAddr() {
		return httpServerAddr;
	}
	
	
	@Override
	public void setServerAddr(String httpServerAddr) {
		this.httpServerAddr = httpServerAddr;
	}
	
	
	/**
	 * 从池获取空闲连接
	 * @return
	 * @throws Exception
	 */
	@Override
	public Socket borrowSocket() throws NullPointerException {
		Socket sock = pool.poll();
		
		if (null == sock) {
			throw new NullPointerException(String.format("连接池%s已满请调整参数,初始连接数:%d,最大连接数:%d", httpServerAddr, initSize, maxSize));
		}
		
		return sock;
	}
	
	
	/**
	 * 将连接归还到池里
	 * @param socket
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean returnSocket(Socket socket) {
		return pool.offer(socket);
	}
	
	/**
	 * 释放所有连接
	 */
	@Override
	public void destroy() {
		Socket sock = pool.poll();
		while (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sock = pool.poll();
		}
	}
	
	public static void main(String[] args) throws Exception {
		HttpSocketPool pool = new HttpSocketPool();
		pool.createPool(10, 100, 10);
		pool.setServerAddr("http://127.0.0.1:8080/server");
		pool.returnSocket(pool.borrowSocket());
		/*for (;;) {
			Thread.sleep(2000);
			try {
				pool.borrowSocket();
				pool.borrowSocket();
				//pool.borrowSocket();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
	}
	
}
