/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.ailk.notify.server.NotifyServer;
import com.ailk.notify.server.ha.HaClient;

/**
 * 用于管理一组服务的主机和备机的连接池
 * 
 * @className:SocketBucket.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-24 
 */
public class SocketBucket {
	private static final transient Logger log = Logger.getLogger(SocketBucket.class);
	
	private String[] masterData;
	private String[] salveData;
	private String queueName;
	private String serverName;
	private int size;
	private long version = 0;
	private AtomicBoolean state = new AtomicBoolean(true); // 标记是否向master发送请求
	private AtomicBoolean canWork = new AtomicBoolean(true);// 标记主备是否至少存活一个
	private AtomicBoolean isDetectMastering = new AtomicBoolean(false); // 标记是否正在探测主机是否可用
	private AtomicBoolean isDetectSalveing = new AtomicBoolean(false); // 标记是否正在探测备机是否可用
	private ReentrantReadWriteLock stateChangeLock = new ReentrantReadWriteLock();
	private Class<SocketClient> client;
	
	private List<Channel> masterChannels;
	private List<Channel> salveChannels;
	
	public SocketBucket(Class<SocketClient> client, String queueName, String serverName,String masterAddr, String slaveAddr, int size) {
		this.queueName = queueName;
		this.serverName = serverName;
		masterData = NotifyUtility.splitHost(masterAddr);
		if (StringUtils.isNotBlank(slaveAddr)) {
			salveData = NotifyUtility.splitHost(slaveAddr);
		}
		this.size = size;
		this.client = client;
		init();
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	protected void init() {
		if (this.size <= 0) {
			this.size = NotifyUtility.CLIENT_SOCKET_BUCKET_SIZE;
		}
		masterChannels = new ArrayList<Channel>(size);
		salveChannels = new ArrayList<Channel>(size);
		
		for (int i = 0; i < this.size; i++) {
			try {
				Channel masterChannel = createChannel(masterData);
				addChannel(masterChannels, masterChannel);
				
				if (salveData != null) {
					Channel salveChannel = createChannel(salveData);
					addChannel(salveChannels, salveChannel);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (masterChannels.isEmpty()) {
			changeState(true, false);
			beginDetectMaster();
			if (salveChannels.isEmpty()) {
				canWork.compareAndSet(true, false);
				log.error("No Server both the master and salve are alive for the client! queueName :" + queueName + " ; serverName :" + serverName);
				beginDetectSalve();
			}
		}
	}
	
	public void addChannel(List<Channel> channels, Channel channel) {
		if (channel != null) {
			channels.add(channel);
		}
	}
	
	public void connectMaster() {
		if (state.get()) {
			return;
		}
		stateChangeLock.writeLock().lock();
		for (int i = 0; i < size; i++) {
			Channel channel = createChannel(masterData);
			if (channel == null) {
				masterChannels.clear();
				break;
			}
			addChannel(masterChannels, channel);
		}
		if (masterChannels.size() == size) {
			changeState(false, true);
			canWork.compareAndSet(false, true);
		}
		stateChangeLock.writeLock().unlock();
	}
	
	public Channel createChannel(String[] data) {
		try {
			return client.getConstructor(String.class, String.class, String.class, int.class, SocketBucket.class).newInstance(this.queueName, this.serverName, data[0], Integer.valueOf(data[1]), this).createChannel();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error(e.getMessage());
			// 若当前客户端为HAClient，即认为HA服务未启动，将开启服务的接收数据及加载缓存的功能
			if (client.isAssignableFrom(HaClient.class)) {
				log.error("Connot connect ha server! queueName :" + queueName + "; serverName :" + serverName);
				NotifyServer.setCanAcceptData(queueName, serverName, true);
				NotifyServer.setCachePersist(queueName, serverName, true);
			}
		}
		return null;
	}
	
	public void removeChannel(Channel channel) {
		stateChangeLock.writeLock().lock();
		boolean removeMaster = masterChannels.remove(channel);
		boolean removeSalve = salveChannels.remove(channel);
		if (log.isDebugEnabled()) {
			log.debug("remove channel : " + channel.remoteAddress() + "; is master channel :" + removeMaster);
		}
		if (removeMaster) {
			if (!isDetectMastering.get()) {
				Channel testChannel = createChannel(masterData);
				if (testChannel == null) {
					masterChannels.clear();
					changeState(true, false);
					beginDetectMaster();
				} else {
					addChannel(masterChannels, testChannel);
				}
			}
		} else if (removeSalve) {
			if (!isDetectSalveing.get()) {
				Channel testChannel = createChannel(salveData);
				if (testChannel == null) {
					salveChannels.clear();
					beginDetectSalve();
				} else {
					addChannel(salveChannels, testChannel);
				}
			}
		}
		
		if (masterChannels.isEmpty() && salveChannels.isEmpty()) {
			canWork.compareAndSet(true, false);
			beginDetectMaster();
			beginDetectSalve();
		}
		stateChangeLock.writeLock().unlock();
	}
	
	public void changeState(boolean expect, boolean update) {
		boolean success = state.compareAndSet(expect, update);
		if (success) {
			this.version++;
		}
	}
	
	/**
	 * 当主机无法链接时，开始探测，直到主机能联通
	 */
	public void beginDetectMaster() {
		if (!isDetectMastering.get()) {
			synchronized (this) {
				if (!isDetectMastering.get()) {
					isDetectMastering.compareAndSet(false, true);
					new Thread(new DetectMaster(true)).start();
				}
			}
		}
	}
	
	/**
	 * 只有在主备都无法链接的情况下，才会去探测备机的链接是否可有 
	 */
	public void beginDetectSalve() {
		if (canWork.get()) {
			return ;
		}
		
		if (salveData == null) {
			return ;
		}
		
		if (!isDetectSalveing.get()) {
			synchronized (this) {
				if (!isDetectSalveing.get()) {
					isDetectSalveing.compareAndSet(false, true);
					new Thread(new DetectMaster(false)).start();
				}
			}
		}
	}

	/**
	 * 判断当前桶内是否有链接可用
	 * @return
	 */
	public boolean isWorking() {
		return canWork.get();
	}
	
	public void returnChannel(ChannelData channelData) {
		/*stateChangeLock.writeLock().lock();
		if (channelData.getVersion() != this.version) {
			if (!channelData.isMaster() && channelData.getChannel().isActive()) {
				salveChannels.add(channelData.getChannel());
			} else {
				log.info("The channel version is over time!");
				channelData.getChannel().close();
			}
			stateChangeLock.writeLock().unlock();
			return;
		}
		
		if (channelData.isMaster()) {
			masterChannels.add(channelData.getChannel());
		} else {
			salveChannels.add(channelData.getChannel());
		}
		stateChangeLock.writeLock().unlock();*/
	}
	
	public ChannelData getChanel() {
		if (!canWork.get()) {
			log.info("No Channel is working now!!!");
			return null;
		}
		Channel channel = null;
		boolean isMaster = false;
		stateChangeLock.readLock().lock();
		if (state.get()) {
			if (masterChannels.size() == 0) {
				return null;
			}
			channel = masterChannels.get(RandomUtils.nextInt(masterChannels.size()));
			isMaster = true;
		} else {
			if (salveChannels.size() == 0) {
				return null;
			}
			channel = salveChannels.get(RandomUtils.nextInt(salveChannels.size()));
			isMaster = false;
		}
		stateChangeLock.readLock().unlock();
		if (channel != null) {
			return new ChannelData(this.queueName, this.serverName, channel, isMaster, this.version);
		}
		if (log.isDebugEnabled()) {
			log.debug("can not get channel, reget again!");
		}
		return getChanel();
	}

	private class DetectMaster implements Runnable {

		boolean isMaster;
		public DetectMaster(boolean isMaster) {
			this.isMaster = isMaster;
		}
		
		@SuppressWarnings("static-access")
		public void run() {
			List<Channel> channels = null;
			String[] serverData = null;
			if (isMaster) {
				channels = masterChannels;
				serverData = masterData;
			} else {
				channels = salveChannels;
				serverData = salveData;
			}
			
			if (serverData == null) {
				log.error("No serve data for detect !!! isMaster : " + isMaster);
				return ;
			}
			
			boolean canCancel = false;
			while (true) {
				if (canCancel) {
					break;
				}
				stateChangeLock.writeLock().lock();
				if (!(isMaster && state.get())) {
					
					channels.clear();
					//stateChangeLock.writeLock().unlock();
					for (int i = 0; i < size; i++) {
						Channel channel = createChannel(serverData);
						if (channel == null) {
							//stateChangeLock.writeLock().lock();
							channels.clear();
							//stateChangeLock.writeLock().unlock();
							break;
						}
						addChannel(channels, channel);
					}
				}
				if (channels.size() == size) {
					canCancel = true;
					if (isMaster) {
						changeState(false, true);
					}
					canWork.compareAndSet(false, true);
					
					stateChangeLock.writeLock().unlock();
					break;
				}
				stateChangeLock.writeLock().unlock();
				try {
					Thread.currentThread().sleep(NotifyUtility.DETECT_MASTER_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (isMaster) {
				isDetectMastering.compareAndSet(true, false);
			} else {
				isDetectSalveing.compareAndSet(true, false);
			}
		}
	}
	
}
