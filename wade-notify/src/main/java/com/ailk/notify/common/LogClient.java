/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @className:LogClient.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-3 
 */
public class LogClient {

	private static final Logger log = Logger.getLogger(LogClient.class);
	private static BlockingQueue<ByteBuffer> producerLogQueue = new LinkedBlockingQueue<ByteBuffer>();
	private static BlockingQueue<ByteBuffer> consumerLogQueue = new LinkedBlockingQueue<ByteBuffer>();
	private static BlockingQueue<ByteBuffer> serverLogQueue = new LinkedBlockingQueue<ByteBuffer>();
	
	//private static ExecutorService producerService = Executors.newFixedThreadPool(NotifyUtility.LOG_SEND_THREAD_SIZE);
	//private static ExecutorService consumerService = Executors.newFixedThreadPool(NotifyUtility.LOG_SEND_THREAD_SIZE);
	private static ExecutorService serverService = Executors.newFixedThreadPool(NotifyUtility.LOG_SEND_THREAD_SIZE);
	
	private static final int PRODUCER_TYPE = 1;
	private static final int CONSUMER_TYPE = 2;
	private static final int SERVER_TYPE = 3;

	private static SocketAddress address = null;
	private static DatagramSocket ds = null;
	private static byte[] ipBytes = null ;
	
	static {
		initSocketAddress();
		initSocket();
		String ip = NotifyUtility.getLocalIp();
		ipBytes = ip.getBytes();
		
		for (int i = 0; i < NotifyUtility.LOG_SEND_THREAD_SIZE; i++) {
			//producerService.submit(new SendLog(PRODUCER_TYPE));
			//consumerService.submit(new SendLog(CONSUMER_TYPE));
			serverService.submit(new SendLog(SERVER_TYPE));
		}
	}
	
	public static void sendProducerLog(ByteBuffer indexOffset) {
		try {
			producerLogQueue.put(indexOffset);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeProducerLog(ByteBuffer indexOffset) {
		ByteBuffer data = ByteBuffer.allocate(NotifyUtility.LOG_PRODUCER_LENGTH);
		data.put(NotifyUtility.LOG_PRODUCER_STATE);
		data.put(indexOffset);
		data.putLong(System.currentTimeMillis());
		data.put(ipBytes);
		sendMessage(data.array());
		NotifyUtility.releaseByteBuffer(indexOffset);
		NotifyUtility.releaseByteBuffer(data);
	}
	
	public static void sendServerLog(ByteBuffer indexOffset) {
		try {
			indexOffset.rewind();
			serverLogQueue.put(indexOffset);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeServerLog(ByteBuffer indexOffset) {
		ByteBuffer data = ByteBuffer.allocate(NotifyUtility.LOG_SERVER_LENGTH);
		data.put(NotifyUtility.LOG_SERVER_STATE);
		data.put(indexOffset);
		data.putLong(System.currentTimeMillis());
		sendMessage(data.array());
		NotifyUtility.releaseByteBuffer(indexOffset);
		NotifyUtility.releaseByteBuffer(data);
	}
	
	public static void sendConsumerLog(ByteBuffer indexOffset) {
		try {
			consumerLogQueue.put(indexOffset);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void writeConsumerLog(ByteBuffer indexOffset) {
		ByteBuffer data = ByteBuffer.allocate(NotifyUtility.LOG_CONSUMER_LENGTH);
		data.put(NotifyUtility.LOG_CONSUMER_STATE);
		data.put(indexOffset);
		data.putLong(System.currentTimeMillis());
		data.put(ipBytes);
		sendMessage(data.array());
		NotifyUtility.releaseByteBuffer(indexOffset);
		NotifyUtility.releaseByteBuffer(data);
	}
	
	public static void sendMessage(byte[] data) {
		if (address == null) {
			if (log.isDebugEnabled()) {
				log.debug("No Log Server to Send The Message!");
			}
			return ;
		}
		try {
			DatagramPacket dp = new DatagramPacket(data, data.length, address);
			if (ds != null) {
				ds.send(dp);
				
			}
		} catch (SocketException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	private static void initSocketAddress() {
		String addr = NotifyCfg.getLogServerAddr();
		if (StringUtils.isNotBlank(addr)) {
			String[] addrData = addr.split(":");
			address = new InetSocketAddress(addrData[0], Integer.valueOf(addrData[1]));
		}
	}
	
	private static void initSocket() {
		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 发送缓存的日志信息
	 *  
	 * @className:LogClient.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-4-13
	 */
	static class SendLog implements Runnable {
		private int type;
		private BlockingQueue<ByteBuffer> queue;
		
		public SendLog(int type) {
			this.type = type;
			switch (type) {
				case PRODUCER_TYPE:
					queue = producerLogQueue;
					break;
				case CONSUMER_TYPE:
					queue = consumerLogQueue;				
					break;
				case SERVER_TYPE:
					queue = serverLogQueue;
					break;
				default:
					log.error("Type is not exists ! type :" + type);
					break;
			}
		}
		
		public void run() {
			if (queue == null) {
				return;
			}
			switch (type) {
				case PRODUCER_TYPE:
					while (true) {
						try {
							ByteBuffer data = queue.take();
							writeProducerLog(data);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				case CONSUMER_TYPE:
					while (true) {
						try {
							ByteBuffer data = queue.take();
							writeConsumerLog(data);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}				
				case SERVER_TYPE:
					while (true) {
						try {
							ByteBuffer data = queue.take();
							writeServerLog(data);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				default:
					log.error("Type is not exists ! type :" + type);
					break;
			}
		}
		
	}
	
	
	public static void main (String[] args) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			sendMessage("21300000000000000000000000000000000000000000000000000000000000000000000000".getBytes());
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
}
