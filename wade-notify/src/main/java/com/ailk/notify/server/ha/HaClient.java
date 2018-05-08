/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server.ha;

import io.netty.channel.Channel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ailk.notify.common.ChannelData;
import com.ailk.notify.common.FileUtility;
import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyCfg;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.SocketBucket;
import com.ailk.notify.common.SocketClient;
import com.ailk.notify.common.SocketPool;
import com.ailk.notify.common.code.ByteBufferMessageDecoder;
import com.ailk.notify.common.code.ByteBufferMessageEncoder;
import com.ailk.notify.server.NotifyServer;
import com.ailk.notify.server.ServerFileProxy;

/**
 * HA客户端，在Master端调用
 * 
 * 在HA客户端与服务端打开链接时， 客户端发起请求，判断主备之间的数据差，决定由客户端或服务端发起同步的文件，并进行同步
 * 当链接断开时，重置当前服务的可用文件列表， 当前已写入数据的文件将不能继续写入新的数据（可以更新状态），避免HA客户端和服务端数据出现冲突
 * 
 * @className:HaClient.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-20 
 */
public class HaClient extends SocketClient{
	private static final transient Logger log = Logger.getLogger(HaClient.class);
	
	private HaMessageHandler haHandler = new HaMessageHandler();
	private static final Map<String, Boolean> isSyncingMap = new ConcurrentHashMap<String, Boolean>();
	private String queueAndServerName;
	
	public HaClient(String queueName, String serverName, String ip, int port, SocketBucket bucket) {
		super(queueName, serverName, ip, port, bucket);
		queueAndServerName = NotifyUtility.buildKey(queueName, serverName);
		isSyncingMap.put(queueAndServerName, false);
	}

	public static void init() {
		Map<String, String> haDatas = NotifyCfg.getHaServerAddrs();
		Set<String> haKeys = haDatas.keySet();
		if (log.isDebugEnabled()) {
			log.debug("Open ha client, ha server addr size :" + haDatas.size());
		}
		for (String haKey : haKeys) {
			String[] queueAndServer = NotifyUtility.splitKey(haKey);
			if (log.isDebugEnabled()) {
				log.debug("Ha client init pool! queue name : " + queueAndServer[0] + " ; server name : " + queueAndServer[1] + " ; ha addr : " + haDatas.get(haKey));
			}
			SocketPool.initHaPool(queueAndServer[0], queueAndServer[1], haDatas.get(haKey), HaClient.class);
		}
	}
	
	public void openChannel(Channel channel) {
		if (isSyncingMap.get(queueAndServerName)) {
			return ;
		} else {
			synchronized (HaClient.class) {
				if (isSyncingMap.get(queueAndServerName)) {
					return;
				} else {
					isSyncingMap.put(queueAndServerName, true);
				}
			}
		}
		
		// 与备机比对数据，判断是否需要同步
		Set<String> fileUtilityKeySet = ServerFileProxy.getFileSet();
		boolean hasFileUtility = false;
		List<Object[]> fileRangeList = new LinkedList<Object[]>();
		for (String fileUtilityKey : fileUtilityKeySet) {
			if (fileUtilityKey.startsWith(queueAndServerName)) {
				hasFileUtility = true;
				String[] keyDatas = NotifyUtility.splitKey(fileUtilityKey);
				long fileName = Long.valueOf(keyDatas[2]);
				FileUtility fileUtility = ServerFileProxy.getFileUtility(queueName, serverName, fileName);
				
				// 获取文件的待处理数据范围，在服务启动前进行数据同步
				int [] indexRange = fileUtility.getUnOverDataRange();
				fileRangeList.add(new Object[]{fileName, indexRange[0], indexRange[1]});
				
				if (log.isDebugEnabled()) {
					log.debug("File range, queueName : " + queueName + "; serverName :" + serverName + "; fileName :" + fileName + " ;start :" + indexRange[0] + " ; end :" + indexRange[1]);
				}
				
				NotifyServer.setIndexRangeBeforeServer(fileUtilityKey, new int[]{indexRange[0], indexRange[1]});
			}
		}
		
		if (!hasFileUtility) {
			fileRangeList.add(new Object[]{NotifyUtility.NONFILE_FILE_NAME, 0, 0});
		}
		
		HaProxy.ask(queueName, serverName, fileRangeList);
	}
	
	@Override
	public void closeChannel(Channel channel) {
		ServerFileProxy.setAllUnBlankFileUnAvailable();
		NotifyServer.setCanAcceptData(queueName, serverName, true);
		NotifyServer.setCachePersist(queueName, serverName, true);
		if (isSyncingMap.get(queueAndServerName)) {
			return ;
		} else {
			if (isSyncingMap.get(queueAndServerName)) {
				return;
			} else {
				isSyncingMap.put(queueAndServerName, false);
			}
		}
	}
	
	public static void write(String queueName, String serverName, ByteBuffer data) {
		String key = SocketPool.buildHAKey(queueName, serverName);
		ChannelData channelData = SocketPool.getChannel(key);
		
		if (log.isDebugEnabled()) {
			log.debug("HaClient begin write data; channelData : " + channelData + " ; data :" + data);
		}

		if (channelData != null) {
			try {
				channelData.getChannel().writeAndFlush(data);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			SocketPool.returnHaChannel(channelData);
		} else {
			NotifyServer.setCanAcceptData(queueName, serverName, true);
			NotifyServer.setCachePersist(queueName, serverName, true);
		}
		NotifyUtility.releaseByteBuffer(data);
	}
	
	@Override
	public int getLoopGroupSize() {
		return NotifyUtility.EVENTLOOP_HA_CLIENT_SIZE;
	}
	
	@SuppressWarnings("rawtypes")
	public MessageToByteEncoder getEncoder() {
		return new ByteBufferMessageEncoder();
	}
	
	public ByteToMessageDecoder getDecoder() {
		return new ByteBufferMessageDecoder();
	}
	
	public MessageHandler getMessageHandler() {
		return haHandler;
	}
	
	public static void main(String[] args) throws InterruptedException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		System.setProperty("wade.server.port", "8000");
		
		init();
		
		String queueName = "testNq0001";
		String serverName = "testnqs001";
		long fileName = System.currentTimeMillis();
		long start = System.currentTimeMillis();
		int indexOffset = 0;
		int messageOffset = 0;
		String msg = "4567890fghjklcvbnm,ajfdknmakjsfkdskfjajfhakdfhkjahdfqweyuiewqhfkdsnmzc nvrou41943874917284361873647$^@!^*#*)@#)@!)~*)(";
		int msgLength = msg.getBytes().length;
		for (int i = 0; i < 10; i++) {
			//byte[] dataBytes = NotifyUtility.KEEP_ALIVE_KEY_WORD.getBytes();
			ByteBuffer data = ByteBuffer.allocate(2 + NotifyUtility.getMaxQueueNameLength() + NotifyUtility.getMaxServerNameLength() + 
					NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength() + 2 + 4 + msgLength);
			data.putShort(NotifyUtility.HA_STATE.WRITE_MESSAGE.getState());
			data.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
			data.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
			data.putLong(fileName);
			data.putInt(indexOffset);
			data.putShort(NotifyUtility.MESSSAGE_STATE.RETURN_PRODUCER_SUCESS.getState());
			data.putInt(messageOffset);
			data.put(msg.getBytes());
			data.flip();
			HaClient.write(queueName, serverName, data);
			NotifyUtility.releaseByteBuffer(data);
			indexOffset += NotifyUtility.getMaxIndexOffsetLength();
			messageOffset += msgLength;
			//System.out.println(i);
		}
		System.out.println("use Time " + (System.currentTimeMillis() - start) + " ; cur time : " + System.currentTimeMillis());
		//System.out.println("send count : " + sendCount.intValue() + " ; resp count : " + respCount.intValue());
	}
}
