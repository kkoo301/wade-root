/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.ailk.notify.server.MessageCache;
import com.ailk.notify.server.ha.HaProxy;

/**
 * 对数据文件和索引文件进行处理；
 * 每个实力对象处理一对索引和数据文件；
 * 
 * 
 * @className:FileUtility.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-4 
 */
public class FileUtility {

	private static final transient Logger log = Logger.getLogger(FileUtility.class);
	
	private IndexUtility indexUtility = null;
	private MessageUtility messageUtility = null;
	private String queueName = null;
	private String serverAddrName = null;
	private long fileNamePrefix = 0;
	private AtomicBoolean isAvailable = new AtomicBoolean(true);
	private BufferedOutputStream stream; 
	private FileChannel streamChannel; // 字节流文件句柄, 读数据时调用
	private FileChannel posChannel; // 记录最后一次将字节流数据同步到格式化数据的位置
	private File streamFile;
	private File posFile;
	private MappedByteBuffer posMap; // 持久化最后一次将字节流数据同步到格式化数据的位置
	private MappedByteBuffer indexSizeBuffer; //记录字节流中已经写入的index索引的长度
	private AtomicLong streamPosition = new AtomicLong(0);// 内存中记录最后一次将字节流数据同步到格式化数据的位置
	//private boolean canWriteNewData = false; // 在格式化数据时, 若canWriteNewData==false && streamPosition有数据 则表示当前文件为重启后不可继续写入数据的文件
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private ReentrantReadWriteLock utilityLock = new ReentrantReadWriteLock();
	private Condition utilityCondition = utilityLock.writeLock().newCondition();

	private ReentrantLock posLock = new ReentrantLock();
	private static final int syncThreadSize = 3;
	private ExecutorService syncService = Executors.newFixedThreadPool(syncThreadSize);
	
	private boolean isPrepareState = false; // 标记当前是否为准备状态
	
	private Lock logLock = new ReentrantLock();
	
	public FileUtility(String queueName, String serverAddrName, long fileName, String mode) {
		this.queueName = queueName;
		this.serverAddrName = serverAddrName;
		this.fileNamePrefix = fileName;
		
		String rootPath = NotifyCfg.getRootPath();
		NotifyUtility.checkDir(rootPath);
		String queuePath = NotifyUtility.buildPath(rootPath, queueName);
		NotifyUtility.checkDir(queuePath);
		String serverPath = NotifyUtility.buildPath(queuePath, serverAddrName);
		NotifyUtility.checkDir(serverPath);
		String filePath = NotifyUtility.buildPath(serverPath, String.valueOf(fileName));
		String indexFilePath = filePath + NotifyUtility.INDEX_FILE_SUFFIX; 
		if (log.isDebugEnabled()) {
			log.debug("indexFilePath :" + indexFilePath);
		}
		checkFile(indexFilePath);
		String msgFilePath = filePath + NotifyUtility.MSG_FILE_SUFFIX;
		if (log.isDebugEnabled()) {
			log.debug("msgFilePath :" + msgFilePath);
		}
		checkFile(msgFilePath);
		
		indexUtility = new IndexUtility(filePath, mode);
		messageUtility = new MessageUtility(filePath, mode);
		
		try {
			String streamFileName = filePath + NotifyUtility.STREAM_FILE_SUFFIX;
			String streamFilePositionName = filePath + NotifyUtility.STREAM_POSITION_FILE_SUFFIX;
			checkFile(streamFileName);
			checkFile(streamFilePositionName);
			streamFile = new File(streamFileName);
			posFile = new File(streamFilePositionName);
			stream = new BufferedOutputStream(new FileOutputStream(streamFile));
			streamChannel = new RandomAccessFile(streamFile, "rw").getChannel();
			posChannel = new RandomAccessFile(posFile, "rw").getChannel();
			posMap = posChannel.map(MapMode.READ_WRITE, 0, 8);
			indexSizeBuffer = posChannel.map(MapMode.READ_WRITE, 8, 4);
			
			long posValue = posMap.getLong();
			if (posValue > 0) {
				streamPosition.set(posValue);
			}
			
			int indexSize = indexSizeBuffer.getInt();
			if (indexSize > 0) {
				indexUtility.setCapacity(indexSize);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (getIndexLength() > 0) {
			setAvailable(false);
		}
		
		for (int i = 0; i < syncThreadSize; i++) {
			syncService.execute(new SyncStreamToBuffer());
		}
	}
	
	public void setPrepareState(boolean state) {
		isPrepareState = state;
	}
	
	/**
	 * 判断该组文件是否可用于继续添加数据 
	 */
	public boolean isAvailable() {
		if (!isAvailable.get()) {
			return false;
		}
		return indexUtility.canAddData();
	}
	
	public void setAvailable(boolean available) {
		isAvailable.compareAndSet(!available, available);
	}
	
	private void checkFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 创建消息偏移量
	 *  
	 * @return
	 */
	public int createIndexOffset() {
		return this.indexUtility.updateOffset();
	}
	
	/**
	 * 设置文件的索引偏移量
	 * 
	 * @param capacity
	 */
	public void setCapacity(int capacity) {
		this.indexUtility.setCapacity(capacity);
	}
	
	/**
	 * 在已知index和message偏移量的情况下，对消息进行持久化
	 * 在HA同步数据的时候用到
	 * 
	 * @param indexOffset
	 * @param messageOffset
	 * @param state
	 * @param data
	 * @return
	 */
	public void writeBuffer(int indexOffset, int messageOffset, short state, ByteBuffer data) {
		ByteBuffer index = null;
		try {
			int length = data.limit() - data.position();
			index = ByteBuffer.allocate(NotifyUtility.INDEX_LENGTH);
			index.putShort(state);
			index.putInt(messageOffset);
			index.putInt(length);
			
			utilityLock.writeLock().lock();
			messageUtility.createMessage(data, messageOffset);
			indexUtility.createIndex(index, indexOffset);
			utilityCondition.signalAll();
		} finally {
			utilityLock.writeLock().unlock();
			NotifyUtility.releaseByteBuffer(index);
			
			// createMessage 已经释放
			//NotifyUtility.releaseByteBuffer(data);
		} 
	}
	
	protected void writeStream(ByteBuffer index) {
		try {
			stream.write(index.array());
			stream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			NotifyUtility.releaseByteBuffer(index);
		}
	}

	public boolean write(int indexOffset, ByteBuffer data) {
		/*if (!canWriteNewData) {
			canWriteNewData = true;
		}*/
		ByteBuffer index = null;
		int length = 0;
		int messageOffset = 0;
		short state = 0;
		data.mark();
		//ByteBuffer newData = NotifyUtility.buildBufferFromRemain(data);
		try {
			lock.writeLock().lock();
			length = NotifyUtility.INDEX_LENGTH + data.limit() - data.position();
			messageOffset = messageUtility.updateOffset(data.limit() - data.position());
			index = ByteBuffer.allocate(4 + length);
			state = NotifyUtility.MESSSAGE_STATE.SUCCESS_RECEIVED.getState();
			index.putInt(length);
			index.putShort(state);
			index.putInt(indexOffset);
			index.putInt(messageOffset);
			index.put(data);
			index.flip();
			
			stream.write(index.array());
			stream.flush();
						
			indexSizeBuffer.clear();
			indexSizeBuffer.putInt(indexUtility.getCapacity());
		} catch (Throwable e) {
			e.printStackTrace();
			state = NotifyUtility.MESSSAGE_STATE.PERSIST_INDEX_FAILED.getState();
			return false;
			//indexUtility.updateMessageStatus(indexOffset, state);
		} finally {
			lock.writeLock().unlock();
			data.reset();
			HaProxy.write(queueName, serverAddrName, this.fileNamePrefix, indexOffset, messageOffset, state, data);
			//NotifyUtility.releaseByteBuffer(data);
			NotifyUtility.releaseByteBuffer(index);
		}
		return true;
	}
	
	public long getIndexFileSize() {
		return indexUtility.getCurFileSize();
	}
	
	public long getMsgFileSize() {
		return messageUtility.getCurFileSize();
	}
	
	public long getStreamFileSize() {
		return streamFile.length();
	}
	/**
	 * 将消息内容持久化
	 *  
	 * @param data
	 * @return 
	 */
	public boolean writeBuffer(int indexOffset, ByteBuffer data) {
		ByteBuffer index = null;
		boolean isCreated = false;
		try {
			int length = data.limit() - data.position();
			int messageOffset = messageUtility.updateOffset(length);
			data.mark();
			//ByteBuffer newData = NotifyUtility.buildBufferFromRemain(data);;
			isCreated = messageUtility.createMessage(data, messageOffset);
			data.reset();
			index = ByteBuffer.allocate(NotifyUtility.INDEX_LENGTH);
			short state = NotifyUtility.MESSSAGE_STATE.SUCCESS_RECEIVED.getState();
			if (!isCreated) {
				state = NotifyUtility.MESSSAGE_STATE.PERSIST_MESSAGE_FAILED.getState();
			}
			index.putShort(state);
			index.putInt(messageOffset);
			index.putInt(length);
			isCreated = indexUtility.createIndex(index, indexOffset);
			if (!isCreated) {
				state = NotifyUtility.MESSSAGE_STATE.PERSIST_INDEX_FAILED.getState();
				indexUtility.updateMessageStatus(indexOffset, state);
			}
			
			HaProxy.write(queueName, serverAddrName, this.fileNamePrefix, indexOffset, messageOffset, state, data);
		} finally {
			/*if (data != null) {
				NotifyUtility.releaseByteBuffer(data);
			} */
			if (index != null) {
				NotifyUtility.releaseByteBuffer(index);
			}
		}
		return isCreated;
	}
	
	/**
	 * 仅供日志服务使用, 数据不做备份
	 *  
	 * @param data
	 * @param indexOffset
	 */
	public void writeLog(ByteBuffer data, int indexOffset, byte state) {
		int length = NotifyUtility.LOG_LENGTH;
		int messageOffset = -1;
		ByteBuffer indexBuffer = null;
		ByteBuffer buffer = null;
		try {
			logLock.lock();
			indexBuffer = indexUtility.getMessageIndex(indexOffset);
			if (indexBuffer == null || !NotifyUtility.MESSSAGE_STATE.hasState(indexBuffer.getShort())) {
				messageOffset = messageUtility.updateOffset(length);
				ByteBuffer index = ByteBuffer.allocate(NotifyUtility.INDEX_LENGTH);
				index.putShort(NotifyUtility.MESSSAGE_STATE.LOG_DATA.getState());
				index.putInt(messageOffset);
				index.putInt(length);
				indexUtility.createIndex(index, indexOffset);
			} else {
				indexBuffer.rewind();
				indexBuffer.getShort();
				messageOffset = indexBuffer.getInt();
			}
			
			//buffer = messageUtility.getMessage(messageOffset, length, MapMode.READ_WRITE);
			switch (state) {
				case NotifyUtility.LOG_PRODUCER_STATE:  // 生产者发送
					break;
				case NotifyUtility.LOG_SERVER_STATE:  // 服务端发送
					messageOffset += NotifyUtility.LOG_PERSIST_PRODUCER_LENGTH; 
					break;
				case NotifyUtility.LOG_CONSUMER_STATE:  // 消费者发送
					messageOffset += NotifyUtility.LOG_PERSIST_PRODUCER_LENGTH + NotifyUtility.LOG_PERSIST_SERVER_LENGTH;
					break;
				default : break;
			}
			/*
			//TODO
			log.fatal("begin data.limit :" + data.limit() + "; data.pos :" + data.position() + "; queueName ：" + queueName + 
					"; serverName :" + serverAddrName + " ; fileName :" + fileNamePrefix + " ; indexOffset :" + indexOffset + 
					"; message file size :" + messageUtility.getCurFileSize() + "; messageOffset :" + messageOffset);
			//TODO
*/			
			
			messageUtility.createMessage(data, messageOffset);
			
			/*//TODO
			log.fatal("end data.limit :" + data.limit() + "; data.pos :" + data.position() + "; queueName ：" + queueName + 
					"; serverName :" + serverAddrName + " ; fileName :" + fileNamePrefix + " ; indexOffset :" + indexOffset + 
					"; message file size :" + messageUtility.getCurFileSize() + "; messageOffset :" + messageOffset);
			//TODO
*/			
			/*buffer.put(data);
			buffer.force();*/
		} finally {
			logLock.unlock();
			NotifyUtility.releaseByteBuffer(data);
			NotifyUtility.releaseByteBuffer(indexBuffer);
			NotifyUtility.releaseByteBuffer(buffer);
		}
	}
	
	public boolean updateState(int indexOffset, short state) {
		return updateState(indexOffset, state, true);
	}
	
	/**
	 * 更新索引中的消息状态
	 * @param messageIndexOffset
	 * @param state
	 * @return
	 */
	public boolean updateState(int indexOffset, short state, boolean doHa) {
		if (doHa) {
			HaProxy.updateState(queueName, serverAddrName, fileNamePrefix, indexOffset, state);
		}
		boolean hasLock = false;
		try {
			if ((indexOffset + NotifyUtility.INDEX_LENGTH) > indexUtility.getCurFileSize()) {
				utilityLock.readLock().lock();
				hasLock = true;
				utilityCondition.await(2, TimeUnit.MILLISECONDS);
				return updateState(indexOffset, state, false);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (hasLock) {
				utilityLock.readLock().unlock();
			}
		}
		return indexUtility.updateMessageStatus(indexOffset, state);
	}
	
	/**
	 * 根据索引获取消息内容 
	 * @param messageIndexOffset
	 * @return
	 */
	public ByteBuffer getMessage(int messageOffset, int length) {
		return messageUtility.getMessage(messageOffset, length, MapMode.READ_ONLY);
	}
	
	public int getMessageOffset() {
		return messageUtility.getCurOffset();
	}

	/**
	 * 根据索引位置，获取对应的索引信息 
	 * @param offset
	 * @return
	 */
	public ByteBuffer getIndex(int offset) {
		return indexUtility.getMessageIndex(offset);
	}
	
	/**
	 * 获取索引文件当前的数据量 
	 * @return
	 */
	public int getIndexLength() {
		return indexUtility.getCapacity();
	}
	
	/**
	 * 根据索引位置, 获取对应的消息 
	 * @param offset
	 * @return
	 */
	public ByteBuffer getMessageByIndexOffset(int offset) {
		if ((offset + NotifyUtility.INDEX_LENGTH) > indexUtility.getCurFileSize()) {
			utilityLock.readLock().lock();
			try {
				if ((offset + NotifyUtility.INDEX_LENGTH) > indexUtility.getCurFileSize()) {
					utilityCondition.await(1, TimeUnit.SECONDS);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				utilityLock.readLock().unlock();
			}
			return getMessageByIndexOffset(offset);
		}
		
		ByteBuffer messageIndex = indexUtility.getMessageIndex(offset);
		messageIndex.getShort();
		int startPosition = messageIndex.getInt();
		int length = messageIndex.getInt();
		NotifyUtility.releaseByteBuffer(messageIndex);
		if ((startPosition + length) > messageUtility.getCurFileSize()) {
			utilityLock.readLock().lock();
			try {
				if ((startPosition + length) > messageUtility.getCurFileSize()) {
					utilityCondition.await(1, TimeUnit.SECONDS);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				utilityLock.readLock().unlock();
			}
			return getMessageByIndexOffset(offset);
		}
		return getMessage(startPosition, length);
	}
	
	/**
	 * 获取文件内未处理结束的消息的范围 
	 * @return
	 */
	public int[] getUnOverDataRange() {
		int[] data = new int[]{0, 0};
		
		int size = indexUtility.getCapacity();
		if (size <= 0) {
			return data;
		}
		int position = 0;
		for (position = 0; position < size; ) {
			ByteBuffer message = indexUtility.getMessageIndex(position);
			if (message == null) {
				position += NotifyUtility.INDEX_LENGTH;
				continue;
			}
			short state = message.getShort();
			if (!NotifyUtility.MESSSAGE_STATE.isOverState(state)) {
				data[0] = position;
				break;
			}
			NotifyUtility.releaseByteBuffer(message);
			position += NotifyUtility.INDEX_LENGTH;
		}
		
		if (position == size) {
			data[0] = size - NotifyUtility.INDEX_LENGTH;
		}
		data[1] = size - NotifyUtility.INDEX_LENGTH;
		if (data[0] < 0) {
			data[0] = 0;
		}
		if (data[1] < 0) {
			data[1] = data[0];
		}
		return data;
	}
	
	/**
	 * 判断消息是否已经全部被消费, 当文件中的消息已经全部被消费时，
	 * 则将数据文件和索引文件搬迁到历史目录中，并清除程序中的句柄
	 * 
	 * @return 返回是否已经全部被消费
	 */
	public boolean dealIfAllConsumered() {
		if (isAvailable()) {
			return false;
		}

		if (messageUtility.getCurOffset() == 0) {
			return true;
		}
		
		int size = indexUtility.getCapacity() - NotifyUtility.INDEX_LENGTH;
		
		for (int position = size; position >= 0; ) {
			ByteBuffer message = indexUtility.getMessageIndex(position);
			if (message == null) {
				position -= NotifyUtility.INDEX_LENGTH;
				continue;
			}
			short state = message.getShort();
			if (!NotifyUtility.MESSSAGE_STATE.hasState(state)) {
				position -= NotifyUtility.INDEX_LENGTH;
				continue;
			}
			if (!NotifyUtility.MESSSAGE_STATE.isOverState(state)) {
				return false;
			}
			NotifyUtility.releaseByteBuffer(message);
			position -= NotifyUtility.INDEX_LENGTH;
		}
		return true;
	}
	
	public void destroy() {
		if (log.isDebugEnabled()) {
			log.debug("FileUtility is destroyed! queue name : " + queueName + " ; server name : " + serverAddrName + " ; fileName : " + fileNamePrefix);
		}
		syncService.shutdownNow();
		
		try {
			setAvailable(false);
			lock.writeLock().lock();
			indexUtility.destroy();
			messageUtility.destroy();
			stream.flush();
			stream.close();
			streamChannel.close();
			posChannel.force(true);
			posChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			lock.writeLock().unlock();
		}
		
		NotifyUtility.releaseByteBuffer(posMap);
		NotifyUtility.releaseByteBuffer(indexSizeBuffer);
	}
	
	public void remove() {
		if (log.isInfoEnabled()) {
			log.info("FileUtility is removed! queue name : " + queueName + " ; server name : " + serverAddrName + " ; fileName : " + fileNamePrefix);
			/*try {
				throw new RuntimeException("remove!");
			} catch (Throwable e) {
				e.printStackTrace();
			}*/
		}
		setAvailable(false);
		syncService.shutdownNow();
		try {
			lock.writeLock().lock();
			indexUtility.remove();
			messageUtility.remove();
			stream.flush();
			stream.close();
			
			if (streamChannel.isOpen()) {
				streamChannel.force(true);
				streamChannel.close();
			}
			if (streamFile.exists()) {
				streamFile.delete();
			}
			
			if (posChannel.isOpen()) {
				posChannel.force(true);
				posChannel.close();
			}
			
			if (posFile.exists()) {
				posFile.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			lock.writeLock().unlock();
		}
		
		NotifyUtility.releaseByteBuffer(posMap);
		NotifyUtility.releaseByteBuffer(indexSizeBuffer);
	}
	
	public long[] getReadPos() {
		lock.readLock().lock();
		ByteBuffer lengthBuffer = null;
		boolean isUnlocked = false;
		try {
			long pos = streamPosition.get();
			if (!streamChannel.isOpen()) {
				return new long[]{pos, 0};
			}
			if ((pos + 4) > streamChannel.size()) {
				if (!isAvailable()) {
					return new long[]{pos, 0};
				}
				
				lock.readLock().unlock();
				isUnlocked = true;
				Thread.currentThread().sleep(5);
				return new long[] {0, 0};//getReadPos();
			}
			lengthBuffer = streamChannel.map(MapMode.READ_ONLY, pos, 4);
			int length = lengthBuffer.getInt();
			if (length <= 0) {
				throw new RuntimeException("The data in stream file is illegal!!! length :" + length + "; pos :" + pos + " ; channelSize :" + streamChannel.size());
			}
			streamPosition.set(pos + 4 + length);
			return new long[]{pos + 4, length};
		} catch (IllegalMonitorStateException e) {
			log.info(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			log.info("Current thread is interrupt!");
		} catch (Throwable e) {
			log.info(e.getMessage());
		} finally {
			if (!isUnlocked) {
				lock.readLock().unlock();
			}
			NotifyUtility.releaseByteBuffer(lengthBuffer);
		}
		return new long[]{0, 0};
	}
	
	/**
	 * 将字节流文件中的数据同步到
	 *  
	 * @className:FileUtility.java
	 *index.putInt(length); index.putShort(state); index.putInt(indexOffset); index.putInt(messageOffset); index.put(data);
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-4-14
	 */
	class SyncStreamToBuffer implements Runnable {

		public void run() {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e1) {
				log.info("Sync Stream To Buffer is interrupted!!");
			}
			while (true) {
				if (streamChannel == null) {
					break;
				}
				MappedByteBuffer dataBuffer = null;
				try {
					if (!isAvailable() && streamPosition.get() >= streamChannel.size()) {
						break;
					}
					
					if (!streamChannel.isOpen()) {
						break;
					}
					
					if (isPrepareState) {
						try {
							Thread.currentThread().sleep(2000);
							continue;
						} catch (InterruptedException e) {
							log.info("Thread is interrupted! message :" + e.getMessage());
						}
					}
					
					long[] posLength = getReadPos();
					long pos = posLength[0];
					int length = (int)posLength[1];
					
					if (pos < 0 || length <= 0) {
						continue;
					}
					dataBuffer = streamChannel.map(MapMode.READ_WRITE, pos, length);
					short state = dataBuffer.getShort();
					int indexOffset = dataBuffer.getInt();
					int messageOffset = dataBuffer.getInt();
					
					ByteBuffer cacheData = ByteBuffer.allocate(dataBuffer.limit() - dataBuffer.position());
					dataBuffer.mark();
					cacheData.put(dataBuffer);
					cacheData.flip();
					dataBuffer.reset();
					writeBuffer(indexOffset, messageOffset, state, dataBuffer);
					//if (!canWriteNewData) {
					MessageCache.addCache(queueName, serverAddrName, fileNamePrefix, null, indexOffset, cacheData, false, false);
					//}
					posLock.lock();
					posMap.clear();
					if (pos >= 0 && length > 0) {
						posMap.putLong(pos + length);
					}
					posLock.unlock();
				} catch (ClosedChannelException e) {
					log.info("File is Closed! queueName :" + queueName + "; serverName :" + serverAddrName + "; fileName :" + fileNamePrefix);
					break;
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					//lock.unlock();
					//NotifyUtility.releaseByteBuffer(dataBuffer);
				}
			
			}
		}
		
	}
	
}
