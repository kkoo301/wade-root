/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * @className:MessageUtility.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-4 
 */
public class MessageUtility {

	private static final transient Logger log = Logger.getLogger(MessageUtility.class);

	private File file = null;
	private RandomAccessFile randomFile = null;
	private FileChannel messageChannel = null;
	
	private AtomicInteger curOffset = new AtomicInteger(0);
	private String messageFileName;
	
	public MessageUtility(String fileName, String mode) {
		String dataFileName = fileName + NotifyUtility.MSG_FILE_SUFFIX;
		this.messageFileName = dataFileName;
		try {
			file = new File(dataFileName);
			randomFile = new RandomAccessFile(file, mode);
			messageChannel = randomFile.getChannel();
			curOffset.set((int)messageChannel.size()); // 文件不能过大，int类型已经满足需要
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public long getCurFileSize() {
		return file.length();
	}
	
	/**
	 * 将消息内容进行持久化
	 * 
	 * @param message
	 * @param messageOffset
	 * @return
	 */
	public boolean createMessage(ByteBuffer message, int messageOffset) {
		int length = message.limit() - message.position();
		
		/*if (log.isInfoEnabled()) {
			message.mark();
			byte[] messageBytes = new byte[message.limit() - message.position()];
			message.get(messageBytes);
			log.info("Offset :" + messageOffset + " ; message :" + new String(messageBytes));
			message.reset();
		}*/
		
		MappedByteBuffer buffer = null;
		try {
			//synchronized (messageChannel) {
			buffer = messageChannel.map(MapMode.READ_WRITE, messageOffset, length);
			//}
			//message.rewind();
			buffer.put(message);
			buffer.force();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			NotifyUtility.releaseByteBuffer(buffer);
			NotifyUtility.releaseByteBuffer(message);
		}
		return false;
	}
	
	/**
	 * 根据索引文件中记录的偏移量，找到对应的消息内容
	 * 
	 * @param messageIndexOffset
	 * @return
	 */
	public ByteBuffer getMessage(int messageOffset, int length, MapMode mode) {
		ByteBuffer message = null;
		try {
			message = messageChannel.map(MapMode.READ_ONLY, messageOffset, length);
			ByteBuffer copyMessage = ByteBuffer.allocate(message.limit());
			byte[] dataBytes = new byte[message.limit()];
			message.get(dataBytes);
			//message.put(copyMessage);
			copyMessage.put(dataBytes);
			copyMessage.flip();
			return copyMessage;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			NotifyUtility.releaseByteBuffer(message);
		}
		return null;
	}
	
	/**
	 *  获取当前的最新可用的消息偏移量
	 *  
	 * @param length
	 * @return
	 */
	public int updateOffset(int length) {
		
		return curOffset.getAndAdd(length);
	}
	
	public int getCurOffset() {
		return curOffset.get();
	}
	
	public void remove() {
		destroy();
		if (file.exists()) {
			file.delete();
		}
	}
	
	public void destroy() {
		if (messageChannel != null) {
			try {
				messageChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
		try {
			if (randomFile != null) {
				randomFile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
