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
 * @className:IndexUtility.java
 *	
 * 提供关于索引文件的帮助信息
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-4 
 */
public class IndexUtility {

	private static final transient Logger log = Logger.getLogger(IndexUtility.class);

	private FileChannel indexChannel = null;
	private RandomAccessFile randomFile = null;
	private File file = null;
	
	private AtomicInteger curOffset = new AtomicInteger(0);
	private String indexFileName ;

	public IndexUtility(String fileName, String mode) {
		String indexFileName = fileName + NotifyUtility.INDEX_FILE_SUFFIX;
		this.indexFileName = indexFileName;
		try {
			file = new File(indexFileName);
			randomFile = new RandomAccessFile(file, mode);
			indexChannel = randomFile.getChannel();
			curOffset.set((int)indexChannel.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 判断该组文件是否允许继续添加
	 *  
	 * @return
	 */
	public boolean canAddData() {
		int indexDataSize = curOffset.get()/NotifyUtility.INDEX_LENGTH;
		// 不采取强制限制，允许适量的冗余
		return indexDataSize < NotifyUtility.INDEX_DATA_MAX_SIZE;
	}
	
	public int getCapacity() {
		return curOffset.get();
	}
	
	public void setCapacity(int capacity) {
		curOffset.set(capacity);
	}
	
	/**
	 * 根据消息索引对索引进行持久化 
	 * 
	 * @param index 索引内容
	 * @param offset 索引偏移量
	 * @return
	 */
	public boolean createIndex(ByteBuffer index, int offset) {
		MappedByteBuffer buffer = null;
		try {
			//synchronized (indexChannel) {
			buffer = indexChannel.map(MapMode.READ_WRITE, offset, NotifyUtility.INDEX_LENGTH);
			//}	
			index.rewind();
			buffer.put(index);
			buffer.force();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			NotifyUtility.releaseByteBuffer(index);
			NotifyUtility.releaseByteBuffer(buffer);
		}
		return false;
	}
		
	/**
	 * 根据消息偏移量判断该消息是否已经持久化
	 * @param messageOffset
	 * @return
	 */
	public boolean isPersisted(int offset) {
		MappedByteBuffer buffer = null;
		try {
			buffer = indexChannel.map(MapMode.READ_ONLY, offset, NotifyUtility.INDEX_LENGTH);
			short state = buffer.getShort();
			return state > 0;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			if (buffer != null) {
				NotifyUtility.releaseByteBuffer(buffer);
			}
		}
		return false;
	}
	
	/**
	 * 获取消息索引信息，包含消息的偏移量和消息的长度
	 * @param messageOffset
	 * @return
	 */
	public ByteBuffer getMessageIndex(int offset) {
		try {
			return indexChannel.map(MapMode.READ_ONLY, offset, NotifyUtility.INDEX_LENGTH);
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 更新索引状态
	 * 
	 * @param messagOffset
	 * @param status
	 * @return
	 */
	public boolean updateMessageStatus(int offset, short state) {
		MappedByteBuffer buffer = null;
		try {
			buffer = indexChannel.map(MapMode.READ_WRITE, offset, NotifyUtility.INDEX_LENGTH);
			buffer.putShort(state);
			buffer.force();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			NotifyUtility.releaseByteBuffer(buffer);
		}
		return false;
	}
	
	public int updateOffset() {
		return curOffset.getAndAdd(NotifyUtility.INDEX_LENGTH);
	}
	
	public long getCurFileSize() {
		return file.length();
	}
	
	public static void main(String[] args) {
		File file = new File("C:/Users/lvchao/Desktop/test.123");
		try {
			FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
			MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 8);
			/*buffer.putShort((short)123);
			buffer.putInt(999);
			buffer.force();*/
			buffer.putShort((short)123);
			buffer.putInt(1);
			System.out.println(buffer.limit());
			
			ByteBuffer buf = ByteBuffer.allocate(18);
			buf.putShort((short)123);
			buf.putInt(99999999);
			buf.put("8888".getBytes());
			buf.putLong(System.currentTimeMillis());
			System.out.println(buf.position());
			buf.rewind();
			System.out.println(buf.getShort() + "---- " + buf.getInt());
			
			/*int b = buffer.getInt();
			System.out.println( a + " +++++ " + b);*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void remove() {
		destroy();
		//File file = new File(this.indexFileName);
		if (file.exists()) {
			file.delete();
		}
	}
	
	public void destroy() {
		if (indexChannel != null) {
			try {
				indexChannel.close();
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
