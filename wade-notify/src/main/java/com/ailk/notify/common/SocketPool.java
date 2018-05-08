/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

/**
 * 管理主题对应的所有服务组内的链接
 * 
 * @className:SocketPool.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-24 
 */
public class SocketPool {
	private static final transient Logger log = Logger.getLogger(SocketPool.class);
	
	private static final String HA_PREFIX_KEY = "HA";
	// 桶连接池
	private static Map<String, List<SocketBucket>> pool = new ConcurrentHashMap<String, List<SocketBucket>>();
	// 提供桶的直接调用方式，便于根据具体的queuename 和 servername 进行获取，返回
	private static Map<String, SocketBucket> bucketManager= new ConcurrentHashMap<String, SocketBucket>(); 
	// 保存不能工作的桶
	private static Map<String, Set<SocketBucket>> deadBucket = new ConcurrentHashMap<String, Set<SocketBucket>>();
	private static ReentrantReadWriteLock bucketLock = new ReentrantReadWriteLock();
	
	
	static {
		new Thread(new DetectDeadBucket()).start();
	}
	
	public SocketPool() {
	}
	
	/**
	 * 初始化socket连接池
 	 * @param queueName
	 * @param client socket通信客户端，必须继承自SocketClient
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized static void init(String queueName, Class client) {
		if (pool.get(queueName) != null) {
			return;
		}
		
		if (!client.getSuperclass().equals(SocketClient.class)) {
			throw new RuntimeException("The class named " + client.getName() + " must be instance of SocketClient!");
		}
		
		Set<String> serverNames = NotifyCfg.getServerNames(queueName);
		int serverSize = serverNames.size();
		bucketLock.writeLock().lock();
		List<SocketBucket> buckets = pool.get(queueName);
		if (buckets == null) {
			buckets = new ArrayList<SocketBucket>(serverSize);
			pool.put(queueName, buckets);
		}
		for (String serverName : serverNames) {
			String[] serverAddrs = NotifyCfg.getAddrByQueueAndServer(queueName, serverName);
			SocketBucket bucket = new SocketBucket(client, queueName, serverName, serverAddrs[0], serverAddrs[1], NotifyUtility.CLIENT_INIT_CHANNEL_SIZE);
			
			bucketManager.put(NotifyUtility.buildKey(queueName, serverName), bucket);
			buckets.add(bucket);
		}
		bucketLock.writeLock().unlock();
		try {
			// 等待SocketBucket中的链接通道建立
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int bucketLength = buckets.size();
		for (int i = 0; i < bucketLength; i++) {
			if (buckets.size() >= i) {
				break;
			}
			SocketBucket bucket = buckets.get(i);
			if (!bucket.isWorking()) {
				removeBucket(queueName, bucket);
				i--;
			}
		}
	}
	
	public static String buildHAKey(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(NotifyUtility.buildKey(HA_PREFIX_KEY, queueName), serverName);
		return key;
	}
	
	/**
	 * 初始化HA客户端的连接池
	 * 
	 * @param queueName
	 * @param serverName
	 * @param haServerAddr
	 * @param client
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized static void initHaPool(String queueName, String serverName, String haServerAddr, Class client) {
		String key = buildHAKey(queueName, serverName);
		if (pool.get(key) != null) {
			return;
		}
		
		if (!client.getSuperclass().equals(SocketClient.class)) {
			throw new RuntimeException("The class named " + client.getName() + " must be instance of SocketClient!");
		}
		
		SocketBucket bucket = new SocketBucket(client, queueName, serverName, haServerAddr, null, NotifyUtility.HA_CLIENT_INIT_CHANNEL_SIZE);
		bucketLock.writeLock().lock();
		List<SocketBucket> buckets = pool.get(key);
		if (buckets == null) {
			buckets = new ArrayList<SocketBucket>();
			pool.put(key, buckets);
		}
		bucketManager.put(key, bucket);
		buckets.add(bucket);
		bucketLock.writeLock().unlock();
		try {
			// 等待SocketBucket中的链接通道建立
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int bucketLength = buckets.size();
		for (int i = 0; i < bucketLength; i++) {
			if (buckets.size() >= i) {
				break;
			}
			SocketBucket bucketCheck = buckets.get(i);
			if (!bucketCheck.isWorking()) {
				removeBucket(queueName, bucketCheck);
				i--;
			}
		}
	}
	
	public static boolean isServerWorking(String queueName) {
		boolean working = false;
		bucketLock.readLock().lock();
		List<SocketBucket> buckets = pool.get(queueName);
		if (buckets == null || buckets.isEmpty()) {
			bucketLock.readLock().unlock();
			return false;
		}
		Iterator<SocketBucket> bucketIter = buckets.iterator();
		Set<SocketBucket> removeBuckets = new HashSet<SocketBucket>();
		while (bucketIter.hasNext()) {
			SocketBucket bucket = bucketIter.next();
			if (bucket.isWorking()) {
				bucketLock.readLock().unlock();
				return true;
			} else {
				//bucketIter.remove();
				removeBuckets.add(bucket);
			}
		}
		bucketLock.readLock().unlock();
		for (SocketBucket bucket : removeBuckets) {
			removeBucket(queueName, bucket);
		}
		return working;
	}
	
	/**
	 * 供客户端获取通道信息
	 *  
	 * @param queueName
	 * @return
	 */
	public static ChannelData getChannel(String queueName) {
		if (log.isDebugEnabled()) {
			log.debug("socket pool begin get channel! time :" + System.currentTimeMillis());
		}
		bucketLock.readLock().lock();
		List<SocketBucket> buckets = pool.get(queueName);
		if (buckets == null) {
			bucketLock.readLock().unlock();
			throw new RuntimeException("The queue data is not loaded! You can check if it's inited or the config in notify.xml is right! queueName: " + queueName);
		}
		if (log.isDebugEnabled()) {
			log.debug("Key : " + queueName + "; buckets size : " + buckets.size());
		}
		
		if (buckets.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("No Server is Running! queue name :" + queueName);
			}
			
			bucketLock.readLock().unlock();
			return null;
		}
		
		SocketBucket bucket = buckets.get(RandomUtils.nextInt(buckets.size()));
		if (bucket.isWorking()) {
			if (log.isDebugEnabled()) {
				log.debug("socket pool get channel success! time :" + System.currentTimeMillis());
			}
			ChannelData channel = bucket.getChanel();
			bucketLock.readLock().unlock();
			return channel;
		}
		bucketLock.readLock().unlock();
		if (log.isDebugEnabled()) {
			log.debug("socket pool get channel faild, bucket is not working! time :" + System.currentTimeMillis());
		}
		removeBucket(queueName, bucket);
		return getChannel(queueName);
	}
	
	public static ChannelData getChannel(String queueName, String serverName) {
		SocketBucket bucket = bucketManager.get(NotifyUtility.buildKey(queueName, serverName));
		if (bucket.isWorking()) {
			return bucket.getChanel();
		}
		return null;
	}
	
	private static void removeBucket(String queueName, SocketBucket bucket) {
		bucketLock.writeLock().lock();
		pool.get(queueName).remove(bucket);
		bucketLock.writeLock().unlock();
		Set<SocketBucket> deadBucketSet = deadBucket.get(queueName);
		if (deadBucketSet == null) {
			synchronized (deadBucket) {
				deadBucketSet = deadBucket.get(queueName);
				if (deadBucketSet == null) {
					deadBucketSet = new HashSet<SocketBucket>();
					deadBucket.put(queueName, deadBucketSet);
				}
			}
		}
		deadBucketSet.add(bucket);
	}
	
	public static void connectMaster(String queueName) {
		List<SocketBucket> buckets = pool.get(queueName);
		for (SocketBucket bucket : buckets) {
			bucket.connectMaster();
		}
	}
	
	public static void returnHaChannel(ChannelData channelData) {
		String queueName = channelData.getQueueName();
		String serverName = channelData.getServerName();
		String key = buildHAKey(queueName, serverName);
		SocketBucket bucket = bucketManager.get(key);
		if (log.isDebugEnabled()) {
			log.debug("Return ha channel, queue name : " + queueName + "; server name : " + serverName + "; haKey : " + key + "; bucket :" + bucket);
		}
		bucket.returnChannel(channelData);
	}
	
	/**
	 *  当客户端使用完通道后需调用该方法将通道返回
	 *  
	 * @param channelData
	 */
	public static void returnChannel(ChannelData channelData) {
		if (channelData == null) {
			return;
		}
		String queueName = channelData.getQueueName();
		String serverName = channelData.getServerName();
		String key = NotifyUtility.buildKey(queueName, serverName);
		if (log.isDebugEnabled()) {
			log.debug("Return channel, QueueName : " + queueName + "; serverName : " + serverName);
		}
		bucketManager.get(key).returnChannel(channelData);
	}
	
	/**
	 * 探测无效桶是否可用，当可用时加入池中继续使用
	 * 
	 * @className:SocketPool.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-4-9
	 */
	static class DetectDeadBucket implements Runnable {

		public void run() {
			while (true) { 
				Set<String> keys = deadBucket.keySet();
				for (String key : keys) {
					Set<SocketBucket> bucketSet = deadBucket.get(key);
					Iterator<SocketBucket> bucketIter = bucketSet.iterator();
					while (bucketIter.hasNext()) {
						SocketBucket bucket = bucketIter.next();
						if (bucket.isWorking()) {
							bucketLock.writeLock().lock();
							pool.get(key).add(bucket);
							bucketLock.writeLock().unlock();
							bucketIter.remove();
						} else {
							bucket.beginDetectMaster();
							bucket.beginDetectSalve();
						}
					}
				}
				try {
					Thread.currentThread().sleep(NotifyUtility.DETECT_DEAD_BUCKET_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
