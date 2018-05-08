/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server.ha;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.server.MessageCache;
import com.ailk.notify.server.NotifyServer;
import com.ailk.notify.server.ServerFileProxy;

/**
 * HAClient 和 HAServer共用, 用于处理同步数据
 * 
 * @className:HaServerMessageHandler.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-29 
 */
public class HaMessageHandler implements MessageHandler {

	private static final transient Logger log = Logger.getLogger(HaMessageHandler.class);
	
	/**
	 *  处理接收到的请求信息
	 *  
	 * @param channel
	 * @param msg 
	 * @see com.ailk.notify.common.MessageHandler#handler(io.netty.channel.Channel, java.lang.Object) 
	 */
	public void handler(Channel channel, Object msg) {
		ByteBuffer data = (ByteBuffer)msg;
		
		short state = data.getShort();
		byte[] queueBytes = new byte[NotifyUtility.getMaxQueueNameLength()];
		byte[] serverBytes = new byte[NotifyUtility.getMaxServerNameLength()];
		data.get(queueBytes);
		data.get(serverBytes);
		String queueName = new String(queueBytes);
		String serverName = new String(serverBytes);
		if (log.isDebugEnabled()) {
			log.debug("Get Data state : " + state + "; queueName : " + queueName + "; serverName : " + serverName);
		}
		
		HaProxy.addChannels(queueName, serverName, channel);
		int messageOffset = -1;
		if (state == NotifyUtility.HA_STATE.ASK.getState()) {
			if (NotifyServer.isHaServer()) {
				NotifyServer.setCanAcceptData(queueName, serverName, false);
				NotifyServer.setCachePersist(queueName, serverName, false);
			}
			dealAsk(channel, queueName, serverName, data);
			NotifyUtility.releaseByteBuffer(data);
		} else if (state == NotifyUtility.HA_STATE.ANSWER.getState()) {
			dealAnswer(channel, queueName, serverName, data);
			NotifyUtility.releaseByteBuffer(data);
		} else if (state == NotifyUtility.HA_STATE.CAN_CACHE_PERSIST.getState()) {
			NotifyServer.setCachePersist(queueName, serverName, true);
			NotifyUtility.releaseByteBuffer(data);
			ByteBuffer respBuffer = NotifyUtility.getKeepAliveRespBuffer();
			channel.writeAndFlush(respBuffer);
		} else if (state == NotifyUtility.HA_STATE.WRITE_MESSAGE.getState()) {
			long fileName = data.getLong();
			int indexOffset = data.getInt();
			short messageState = data.getShort();
			messageOffset = data.getInt();
			
			if (log.isDebugEnabled()) {
				log.debug("Get data, fileName : " + fileName + "; indexOffset : " + indexOffset + "; messageState : " + messageState + "; messageOffset : " + messageOffset);
			}
			
			
			//ByteBuffer dupBuffer = NotifyUtility.buildBufferFromRemain(data);
			ByteBuffer dupBuffer = data.duplicate();
			ServerFileProxy.getFileUtility(queueName, serverName, fileName, true).writeBuffer(indexOffset, messageOffset, messageState, data);
			
			boolean addCache = false;
			if (!NotifyUtility.MESSSAGE_STATE.isOverState(messageState)) {
				int[] indexRange = NotifyServer.getIndexRangeBeforeServer(queueName, serverName, fileName);
				if (indexRange == null || indexOffset > indexRange[1]) {
					addCache = true;
					MessageCache.addCache(queueName, serverName, fileName, null, indexOffset, dupBuffer, false, false);
				}
			}
			
			ByteBuffer respBuffer = NotifyUtility.getKeepAliveRespBuffer();
			channel.writeAndFlush(respBuffer);
			NotifyUtility.releaseByteBuffer(respBuffer);
			if (!addCache) {
				NotifyUtility.releaseByteBuffer(dupBuffer);
			}
		} else if (state == NotifyUtility.HA_STATE.UPDATE_STATE.getState()) {
			long fileName = data.getLong();
			int indexOffset = data.getInt();
			short messageState = data.getShort();
			if (NotifyUtility.MESSSAGE_STATE.isOverState(messageState)) {
				MessageCache.removeCache(queueName, serverName, fileName, indexOffset);
			}
			ServerFileProxy.getFileUtility(queueName, serverName, fileName).updateState(indexOffset, messageState, false);
			NotifyUtility.releaseByteBuffer(data);
			ByteBuffer respBuffer = NotifyUtility.getKeepAliveRespBuffer();
			channel.writeAndFlush(respBuffer);
			NotifyUtility.releaseByteBuffer(respBuffer);
		}
	}
	
	public void dealAnswer(Channel channel, String queueName, String serverName, ByteBuffer data) {
		if (!NotifyUtility.NEED_BACKUP_MASTER) {
			return ;
		}
		int messageLength = NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength() * 4;
		HaProxy.removeAllSyncFileMaps(queueName, serverName);
		
		if (log.isDebugEnabled()) {
			log.debug("begin deal answer; queueName :" + queueName + "; serverName :" + serverName + "; pos :" + data.position() + " ; limit:" + data.limit());
		}
		
		while (data.limit() - data.position() >= messageLength) {
			long fileName = data.getLong();
			int remoteStartIndex = data.getInt();
			int remoteCurOffset = data.getInt();
			int localStartIndex = data.getInt();
			int localEndIndex = data.getInt();
			
			if (remoteStartIndex == NotifyUtility.ASK_FILE_STATE.FILE_IS_IN_HISTORY.getState()) {
				// 历史文件目录的内容在服务启动时 通过脚本进行同步
				if (ServerFileProxy.getHistoryFileNames(queueName, serverName).contains(fileName)) {
					log.info("File is in histroy! the queue name : " + queueName + "; the server name : " + serverName + " ; file name :" + fileName);
				} else {
					log.info("File need move to history! the queue name : " + queueName + "; the server name : " + serverName + " ; file name :" + fileName);
				}
				
				//ServerFileProxy.removeActiveFileName(queueName, serverName, fileName);
				ServerFileProxy.getFileUtility(queueName, serverName, fileName).remove();
			} else {
				if (log.isInfoEnabled()) {
					log.info("File is active in remote files! queue name :" + queueName + "; server name " + serverName + "; fileName : " + fileName);
				}
				
				HaProxy.addSyncFileMaps(queueName, serverName, fileName);
				HaProxy.beginSyncData(queueName, serverName, fileName, remoteStartIndex, remoteCurOffset, localStartIndex, localEndIndex);
			}
		}
		
		NotifyServer.setCanAcceptData(queueName, serverName, true);
		if (log.isInfoEnabled()) {
			log.info("Sync Index offset over ! queue name : " + queueName + "; server name :" + serverName);
		}
	}
	
	/**
	 * 将远程发送的信息与本地进行对比，决定是否要进行消息同步
	 * 当本地的数据比远程更新时，本地不进行同步
	 * 当本地为HAClient时, 需等到数据同步到一定数量时才可以启动NotifyServer
	 * 当本地为HAServer时，可异步执行
	 * 
	 * @param queueName
	 * @param serverName
 	 * @param fileName
	 * @param indexOffset
	 * @param endIndexOffset
	 */
	public void dealAsk(Channel channel, String queueName, String serverName, ByteBuffer data) {
		int messageLength = NotifyUtility.getMaxIndexFileNameLength()+ NotifyUtility.getMaxIndexOffsetLength() + NotifyUtility.getMaxIndexOffsetLength();
		List<Long> fileNames = ServerFileProxy.getActiveFileNames(queueName, serverName);
		List<Long> activeFileNames = new ArrayList<Long>();
		if (fileNames != null && !fileNames.isEmpty()) {
			activeFileNames.addAll(fileNames);
		}
		List<Object[]> answerData = new ArrayList<Object[]>();
		
		boolean isMasterDataNew = data.getInt() == 1 ? true : false;
		if (log.isInfoEnabled()) {
			log.info("Salve deal ask! queueName : " + queueName + " ; serverName : " + serverName + " ; isMasterDataRuning : " + isMasterDataNew);
		}
		
		HaProxy.updateMasterDataMap(queueName, serverName, isMasterDataNew);
		
		List<Object[]> indexCompareList = new ArrayList<Object[]>();
		while ((data.limit() - data.position()) >= messageLength) {
			
			if (log.isDebugEnabled()) {
				log.debug("begin build answer data, queueName : " + queueName + "; serverName :" + serverName + "; pos :" + data.position() + "; limit :" + data.limit());
			}
			Object[] indexCompare = new Object[5];
			long fileName = data.getLong();
			int startIndex = data.getInt();
			int endIndex = data.getInt();
			indexCompare[0] = fileName;
			indexCompare[1] = startIndex;
			indexCompare[2] = endIndex;
			
			// 判断请求的文件是否在备机的活跃文件列表中，若不存在，则判断是否在历史文件中
			boolean removeSuccess = activeFileNames.remove(fileName);
			if (removeSuccess) {
				int[] curIndexRange = ServerFileProxy.getFileUtility(queueName, serverName, fileName).getUnOverDataRange();
				answerData.add(new Object[] {fileName, curIndexRange[0], curIndexRange[1], startIndex, endIndex});
				indexCompare[3] = curIndexRange[0];
				indexCompare[4] = curIndexRange[1];
				indexCompareList.add(indexCompare);
			} else {
				List<Long> historyFileNames = ServerFileProxy.getHistoryFileNames(queueName, serverName);
				if (historyFileNames != null && historyFileNames.contains(fileName)) {
					answerData.add(new Object[] {fileName, NotifyUtility.ASK_FILE_STATE.FILE_IS_IN_HISTORY.getState(), 0, startIndex, endIndex});
				} else {
					answerData.add(new Object[] {fileName, NotifyUtility.ASK_FILE_STATE.FILE_NOT_IN_SALVE_ACTIVE.getState(), 0, startIndex, endIndex});
				}
			}
		}
		
		for (Long fileName : activeFileNames) {
			Object[] indexCompare = new Object[5];
			indexCompare[0] = fileName;
			indexCompare[1] = NotifyUtility.ASK_FILE_STATE.FILE_IN_SLAVE_NOT_IN_ASK;
			indexCompare[2] = 0;
			
			int[] curIndexRange = ServerFileProxy.getFileUtility(queueName, serverName, fileName).getUnOverDataRange();
			//answerData.add(new Object[] {fileName, curIndexRange[0], curIndexRange[1]});
			indexCompare[3] = curIndexRange[0];
			indexCompare[4] = curIndexRange[1];
			indexCompareList.add(indexCompare);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("begin answer client! queueName : " + queueName + " ; serverName :" + serverName);
		}
		HaProxy.answer(channel, queueName, serverName, answerData);
		HaProxy.beginSyncData(queueName, serverName, indexCompareList);
	}
	
}
