/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server.ha;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.ailk.notify.common.FileUtility;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.server.NotifyServer;
import com.ailk.notify.server.ServerFileProxy;

/**
 * HA客户端和服务端的代理类，通过该方法判断HA数据发往服务端或客户端
 * 
 * @className:HaCommon.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-30 
 */
public class HaProxy {
	private static final transient Logger log = Logger.getLogger(HaProxy.class);
	
	private static Map<String, List<Channel>> clientChannels = new HashMap<String, List<Channel>>();
	private static Map<String, Boolean> isMasterDataNewMap = new HashMap<String, Boolean>();
	
	// 保存要处理的文件索引范围
	private static Map<String, List<Object[]>> fileRanges = new ConcurrentHashMap<String, List<Object[]>>(); 
	private static ExecutorService syncService = Executors.newCachedThreadPool();
	
	// 保存需要进行同步的文件列表
	private static Map<String, Set<Long>> syncFileMaps = new ConcurrentHashMap<String, Set<Long>>();
	
	public synchronized static Set<Long> getSyncFiles(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		return syncFileMaps.get(key);
	}
	
	public static synchronized void removeSyncFile(String queueName, String serverName, long fileName) {
		getSyncFiles(queueName, serverName).remove(fileName);
	}
	
	public static synchronized void removeAllSyncFileMaps(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		syncFileMaps.remove(key);
	}
	
	public static void addSyncFileMaps(String queueName, String serverName, long fileName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		Set<Long> files = syncFileMaps.get(key);
		if (files == null) {
			synchronized (syncFileMaps) {
				files = syncFileMaps.get(key);
				if (files == null) {
					files = new HashSet<Long>();
					syncFileMaps.put(key, files);
				}
			}
		}
		files.add(fileName);
	}
	
	public static void updateMasterDataMap(String queueName, String serverName, boolean isMasterNew) {
		isMasterDataNewMap.put(NotifyUtility.buildKey(queueName, serverName), isMasterNew);
	}
	
	public static boolean isMasterDataNew(String queueName, String serverName) {
		return isMasterDataNewMap.get(NotifyUtility.buildKey(queueName, serverName));
	}
	
	public static void addChannels(String queueName, String serverName, Channel channel) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		List<Channel> channels = clientChannels.get(key);
		if (channels == null) {
			synchronized (HaProxy.class) {
				channels = clientChannels.get(key);
				if (channels == null) {
					channels = new LinkedList<Channel>();
					clientChannels.put(key, channels);
				}
			}
		}
		synchronized (clientChannels) {
			if (!channels.contains(channel)) {
				channels.add(channel);
			}
		}
	}
	
	public static void removeChannel(String queueName, String serverName, Channel channel) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		List<Channel> channels = clientChannels.get(key);
		if (channels != null) {
			channels.remove(channel);
		}
	}
	
	public static String removeChannel(Channel channel) {
		Set<String> keys = clientChannels.keySet();
		for (String key : keys) {
			List<Channel> channels = clientChannels.get(key);
			if (channels.contains(channel)) {
				channels.remove(channel);
				return key;
			}
		}
		return null;
	}
	
	public static Channel getChannel(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		List<Channel> channels = clientChannels.get(key);
		if (channels == null || channels.isEmpty()) {
			NotifyServer.setCanAcceptData(queueName, serverName, true);
			NotifyServer.setCachePersist(queueName, serverName, true);
			return null;
		}
		return channels.get(RandomUtils.nextInt(channels.size()));
	}
	
	/**
	 * 构建请求信息，用于在HA客户端或服务端启动时，构建数据访问另一端是否需要对数据进行同步
	 * 启动时发送的构造信息为该服务对应的持久化信息中的最小已完成数据的索引
	 * 
	 * @param queueName
	 * @param serverName
	 * @param fileName
	 * @param indexOffset
	 * @return
	 */
	public static ByteBuffer buildAsk(String queueName, String serverName, List<Object[]> fileRangeList) {
		int rangeLength = fileRangeList == null ? 0 : (fileRangeList.size() * (NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength() + NotifyUtility.getMaxIndexOffsetLength()));
		
		if (log.isDebugEnabled()) {
			log.debug("queueName :" + queueName + "; serverName :" + serverName + "; file size :" + fileRangeList.size() + "; buffer length :" + rangeLength);
		}
		
		ByteBuffer data = ByteBuffer.allocate(2 + NotifyUtility.getMaxQueueNameLength() + NotifyUtility.getMaxServerNameLength() + 4 + rangeLength);
		data.putShort(NotifyUtility.HA_STATE.ASK.getState());
		data.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
		data.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
		
		// 标记主机服务是否正在运行
		//若正在运行，则表明主机的文件是较新的，需用主机覆盖备机
		//若不再运行，则表明备机的文件是较新的，则用备机覆盖主机
		data.putInt(NotifyServer.canAcceptData(queueName, serverName) ? 1 : 0);
		
		if (log.isDebugEnabled()) {
			log.debug("queueName :" + queueName + "; serverName :" + serverName + "; file size :" + fileRangeList.size() + "; begin loop file!");
		}
		
		for (Object[] fileRange : fileRangeList) {
			long fileName = Long.valueOf(fileRange[0].toString().trim());
			int start = Integer.valueOf(fileRange[1].toString().trim());
			int end = Integer.valueOf(fileRange[2].toString().trim());
			if (log.isDebugEnabled()) {
				log.debug("begin queueName : " + queueName + "; serverName :" + serverName + " ;fileName :" + fileName + 
						" ; start :" + start + "; end :" + end + "; data.limit : " + data.limit() + "; data.pos :" + data.position());
			}
			
			data.putLong(fileName);
			data.putInt(start);
			data.putInt(end);
			
			if (log.isDebugEnabled()) {
				log.debug("end queueName : " + queueName + "; serverName :" + serverName + " ;fileName :" + fileName + 
						" ; start :" + start + "; end :" + end + "; data.limit : " + data.limit() + "; data.pos :" + data.position());
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("build ask over!");
		}
		data.flip();
		return data;
	}
	
	public static ByteBuffer buildSyncOver(String queueName, String serverName) {
		if (getSyncFiles(queueName, serverName) != null) {
			return null;
		}
		
		ByteBuffer data = ByteBuffer.allocate(2 + NotifyUtility.getMaxQueueNameLength() + NotifyUtility.getMaxServerNameLength());
		data.putShort(NotifyUtility.HA_STATE.CAN_CACHE_PERSIST.getState());
		data.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
		data.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
		data.flip();
		return data;
	}
	
	/**
	 * 当HA接收到ASK请求后，将本地信息的 
	 * @param queueName
	 * @param serverName
	 * @return
	 */
	public static ByteBuffer buildAnswer(String queueName, String serverName, List<Object[]> fileCapacityList) {
		int rangeLength = fileCapacityList == null ? 0 : (fileCapacityList.size() * (NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength()));
		ByteBuffer data = ByteBuffer.allocate(2 + NotifyUtility.getMaxQueueNameLength() + NotifyUtility.getMaxServerNameLength() + rangeLength);
		data.putShort(NotifyUtility.HA_STATE.ANSWER.getState());
		data.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
		data.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
		for (Object[] fileRange : fileCapacityList) {
			data.putLong((Long)fileRange[0]);
			data.putInt((Integer)fileRange[1]);
		}
		data.flip();
		return data;
	}
	
	/**
	 * 构造更新状态的消息体
	 * 
	 * @param queueName
	 * @param serverName
	 * @param fileName
	 * @param indexOffset
	 * @param state
	 * @return
	 */
	public static ByteBuffer buildUpdateState(String queueName, String serverName, long fileName, int indexOffset, short state) {
		ByteBuffer updateData = ByteBuffer.allocate(NotifyUtility.HA_UPDATE_DATA_LENGTH);
		updateData.putShort(NotifyUtility.HA_STATE.UPDATE_STATE.getState());
		updateData.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
		updateData.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
		updateData.putLong(fileName);
		updateData.putInt(indexOffset);
		updateData.putShort(state);
		updateData.flip();
		return updateData;
	}
	
	/**
	 * 构造同步写数据的消息体
	 * 
	 * @param queueName
	 * @param serverName
	 * @param fileName
	 * @param indexOffset
	 * @param messageOffset
	 * @param state
	 * @param data
	 * @return
	 */
	public static ByteBuffer buildWrite(String queueName, String serverName, long fileName, int indexOffset, int messageOffset, short state, ByteBuffer data) {
		
		int writeDataLength = NotifyUtility.HA_WIRTE_DATA_LENGHT + (data.limit() - data.position());
		ByteBuffer writeData = ByteBuffer.allocate(writeDataLength);
		writeData.putShort(NotifyUtility.HA_STATE.WRITE_MESSAGE.getState());
		writeData.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
		writeData.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
		writeData.putLong(fileName);
		writeData.putInt(indexOffset);
		writeData.putShort(state);
		writeData.putInt(messageOffset);
		writeData.put(data);
		writeData.flip();
		return writeData;
	}

	public static List<Object[]> getFileRange(String queueName, String serverName) {
		return fileRanges.get(NotifyUtility.buildKey(queueName, serverName));
	}
	
	public static void ask(String queueName, String serverName, List<Object[]> fileRangeList) {
		if (log.isDebugEnabled()) {
			log.debug("Begin ask ! queuename : " + queueName + "; serverName :" + serverName + "; can backup :" + NotifyUtility.NEED_BACKUP_MASTER);
		}
		
		if (NotifyUtility.NEED_BACKUP_MASTER) {
			fileRanges.put(NotifyUtility.buildKey(queueName, serverName), fileRangeList);
			ByteBuffer askData = buildAsk(queueName, serverName, fileRangeList);
			
			write(queueName, serverName, askData);
		}
	}
	
	public static void answer(Channel channel, String queueName, String serverName, List<Object[]> fileCapacityList) {
		if (NotifyUtility.NEED_BACKUP_MASTER) {
			ByteBuffer answerData = buildAnswer(queueName, serverName, fileCapacityList);
			write(channel, queueName, serverName, answerData);
		}
	}
	
	public static void write(String queueName, String serverName, long fileName, int indexOffset, int messageOffset, short messageState, ByteBuffer data) {
		if (NotifyUtility.NEED_BACKUP_MASTER) {
			
			ByteBuffer writeData = buildWrite(queueName, serverName, fileName, indexOffset, messageOffset, messageState, data);
			write(queueName, serverName, writeData);
		}
		NotifyUtility.releaseByteBuffer(data);
	}
	
	public static void updateState(String queueName, String serverName, long fileName, int indexOffset, short state) {
		if (NotifyUtility.NEED_BACKUP_MASTER) {
			ByteBuffer updateData = buildUpdateState(queueName, serverName, fileName, indexOffset, state);
			write(queueName, serverName, updateData);
		}
	}
	
	public static void write(Channel channel, String queueName, String serverName, ByteBuffer data) {
		if (!NotifyUtility.NEED_BACKUP_MASTER) {
			return ;
		}
		if (NotifyServer.isHaServer()) {
			channel.writeAndFlush(data);
			NotifyUtility.releaseByteBuffer(data);
		} else {
			HaClient.write(queueName, serverName, data);
		}
	}
	
	public static void write(String queueName, String serverName, ByteBuffer data) {
		if (log.isDebugEnabled()) {
			log.debug("Begin write :" + NotifyUtility.NEED_BACKUP_MASTER);
		}
		
		if (!NotifyUtility.NEED_BACKUP_MASTER) {
			return ;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Is ha server :" + NotifyServer.isHaServer());
		}
		
		if (NotifyServer.isHaServer()) {
			Channel channel = getChannel(queueName, serverName);
			
			if (log.isDebugEnabled()) {
				log.debug("Ha server write data to client ! channel :" + channel + " ; data :" + data);
			}
			
			if (channel != null) {
				channel.writeAndFlush(data);
			}
			NotifyUtility.releaseByteBuffer(data);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Ha client write data to server ! data :" + data);
			}
			
			HaClient.write(queueName, serverName, data);
		}
	}
	
	/**
	 * 开始同步主备之间的数据 HAServer端调用
	 *  
	 * @param queueName
	 * @param serverName
	 * @param indexRange
	 */
	public static void beginSyncData(String queueName, String serverName, List<Object[]> indexRanges) {
		if (!NotifyUtility.NEED_BACKUP_MASTER) {
			return ;
		}
		removeAllSyncFileMaps(queueName, serverName);
		for (Object[] range : indexRanges) {
			long fileName = (Long)range[0];
			addSyncFileMaps(queueName, serverName, fileName);
			int remoteStartIndex = (Integer)range[1];
			int remoteEndIndex = (Integer)range[2];
			int localStartIndex = (Integer)range[3];
			int localEndIndex = (Integer)range[4];
			beginSyncData(queueName, serverName, fileName, remoteStartIndex, remoteEndIndex, localStartIndex, localEndIndex);
		}
	}
	
	/**
	 * 开始同步主备之间的数据 HAClient端调用
	 * 
	 * @param queueName
	 * @param serverName
	 * @param indexRange
	 */
	public static void beginSyncData(String queueName, String serverName, long fileName, int remoteStartIndex, int remoteEndIndex, int localStartIndex, int localEndIndex) {
		if (!NotifyUtility.NEED_BACKUP_MASTER) {
			return ;
		}
		String key = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), fileName);
		if (!NotifyServer.canAcceptData(queueName, serverName)) {
			// 当服务还不能接收数据时 (目前文件采用每次启动或HA断开后都向新的文件写数据的方式, 所以此处设置意义不大, 仅做备用)
			ServerFileProxy.getFileUtility(key).setCapacity((remoteEndIndex > localEndIndex) ? remoteEndIndex : localEndIndex);
		}
		
		if (!NotifyServer.canCachePersist(queueName, serverName)) {
			int start = localStartIndex < remoteStartIndex ? localStartIndex : remoteStartIndex;
			int end = localEndIndex > remoteEndIndex ? localEndIndex : remoteEndIndex;
			NotifyServer.updateIndexRangeBeforeServer(key, new int[] {start, end});
		}
		
		int writeStartIndex = 0;
		int writeEndIndex = 0;
		if (remoteStartIndex == NotifyUtility.ASK_FILE_STATE.FILE_NOT_IN_SALVE_ACTIVE.getState()) {
			// 该状态仅主机能收到
			// 当文件在备机中不存在时，以主机数据为准
			writeStartIndex = 0;
			writeEndIndex = localEndIndex;
		} else if (remoteStartIndex == NotifyUtility.ASK_FILE_STATE.FILE_IN_SLAVE_NOT_IN_ASK.getState()) {
			// 该状态仅备机能收到
			// 当文件在备机中存在，但是在主机中不存在时， 以备机为准
			writeStartIndex = 0;
			writeEndIndex = localEndIndex;
		} else if (localEndIndex > remoteEndIndex) {
			// 当本地结束位置比远程结束位置大时， 认为本地的数据较新(即便状态不一致也认为本地较新，基于允许重发的情况下)，用本地覆盖远程
			writeStartIndex = localStartIndex > remoteStartIndex ? remoteStartIndex : localStartIndex;
			writeEndIndex = localEndIndex;
		} else if (localEndIndex == remoteEndIndex && localStartIndex > remoteStartIndex) {
			// 当本地和远程的结束位置一致时，如果本地的开始位置较大，则以本地为准(但是要从起始位置较小即remoteStartIndex 开始向远程覆盖数据)
			writeStartIndex = remoteStartIndex;
			writeEndIndex = localEndIndex;
		} else if (!NotifyServer.isHaServer() && localStartIndex == remoteStartIndex && localEndIndex == remoteEndIndex) {
			// 当主备数据起始位置一致时，以主机内容为准
			writeStartIndex = localStartIndex;
			writeEndIndex = localEndIndex;
		}
		
		if (writeEndIndex > 0) {
			syncService.submit(new WriteSyncData(queueName, serverName, fileName, writeStartIndex, writeEndIndex));
		} else {
			removeSyncFile(queueName, serverName, fileName);
		}
	}
	
	static class WriteSyncData implements Runnable {

		private String queueName;
		private String serverName;
		private long fileName;
		private int startIndex;
		private int endIndex;
		
		public WriteSyncData(String queueName, String serverName, long fileName, int startIndex, int endIndex) {
			this.queueName = queueName;
			this.serverName = serverName;
			this.fileName = fileName;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		
		public void run() {
			String key = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), fileName);
			FileUtility fileUtility = ServerFileProxy.getFileUtility(key);
			
			if (log.isInfoEnabled()) {
				log.info("Begin sync data; start :" + startIndex + "; end :" + endIndex);
			}
			
			for (int i = startIndex; i <= endIndex; ) {
				ByteBuffer index = fileUtility.getIndex(startIndex);
				short messageState = index.getShort();
				int messageOffset = index.getInt();
				int length = index.getInt();
				ByteBuffer data = fileUtility.getMessage(messageOffset, length);
				HaProxy.write(queueName, serverName, fileName, startIndex, messageOffset, messageState, data);
				NotifyUtility.releaseByteBuffer(index);
				i += NotifyUtility.INDEX_LENGTH;
			}
			
			NotifyServer.setCanAcceptData(queueName, serverName, true);
			NotifyServer.setCachePersist(queueName, serverName, true);
			
			removeSyncFile(key, key, fileName);
			ByteBuffer syncOverData = buildSyncOver(queueName, serverName);
			if (syncOverData != null) {
				HaProxy.write(queueName, serverName, syncOverData);
			}
		}
	}
}
