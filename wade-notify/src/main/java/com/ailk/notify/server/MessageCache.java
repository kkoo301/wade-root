/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.ailk.notify.common.FileProxy;
import com.ailk.notify.common.FileUtility;
import com.ailk.notify.common.NotifyCfg;
import com.ailk.notify.common.NotifyUtility;


/**
 * 缓存文件信息，预处理数据等
 * 
 * @className:MessageCache.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-6 
 */
public class MessageCache {
	
	private static final transient Logger log = Logger.getLogger(MessageCache.class);
	
	// Object[] : 0 文件名; 1 offset; 2 生产者传递的数据;
	private static final Map<String, BlockingQueue<CacheData>> datas = new ConcurrentHashMap<String, BlockingQueue<CacheData>>();
	// 存储 缓存数据超过限制时的 文件名和 对应的便宜量
	private static final Map<String, BlockingQueue<ByteBuffer>> tempIndexCache = new HashMap<String, BlockingQueue<ByteBuffer>>();
	// 标记队列，服务对应的临时缓存是否已经开始持久化
	private static final Map<String, Boolean> startedTempPersist = new ConcurrentHashMap<String, Boolean>();
	// 标记是否有临时缓存数据已经被持久化
	private static final Map<String, Boolean> hasDataInFile = new ConcurrentHashMap<String, Boolean>();
	// 标记是否已经开始从持久化文件中加载数据到缓存中
	private static final Map<String, Boolean> startedloadFromTempPersist = new ConcurrentHashMap<String, Boolean>();
	// 是否允许对临时缓存的数据进行持久化
	private static final Map<String, Boolean> canotPersistTemp = new ConcurrentHashMap<String, Boolean>();
	// 记录临时文件已经读取的位置
	private static final Map<String, Long> tempPersistReadPosition = new ConcurrentHashMap<String, Long>();
	// 保存需要从缓存中移除的数据，当在初次移除时没有移掉的数据
	private static final Set<String> removeCacheDatas = new HashSet<String>();  
	private static final ScheduledExecutorService removeCacheDataService = Executors.newScheduledThreadPool(1);
	private static ReentrantReadWriteLock tempFileLock = new ReentrantReadWriteLock();
	
	private static Map<String, File> tempFiles = new ConcurrentHashMap<String, File>();
	private static Map<String, FileChannel> tempFileChannels = new ConcurrentHashMap<String, FileChannel>();
	
	static {
		removeCacheDataService.schedule(new CheckRemoveCacheDatas(), 30, TimeUnit.MINUTES);
	}
	
	/**
	 *  获取队列供生产者和消费者调用
	 *  
	 * @return
	 */
	public static BlockingQueue<CacheData> getQueue(String queueName) {
		//String key = NotifyUtility.buildKey(queueName);
		BlockingQueue<CacheData> queue = datas.get(queueName);
		if (queue == null) {
			synchronized (MessageCache.class) {
				queue = datas.get(queueName);
				if (queue == null) {
					// 采用deque, 可从头部插入数据
					queue = new LinkedBlockingDeque<CacheData>();
					datas.put(queueName, queue);
				}
			}
		}
		return queue;
 	}
	
	public static BlockingQueue<ByteBuffer> getTempCacheQueue(String queueName) {
		BlockingQueue<ByteBuffer> queue = tempIndexCache.get(queueName);
		if (queue == null) {
			synchronized (MessageCache.class) {
				queue = tempIndexCache.get(queueName);
				if (queue == null) {
					queue = new LinkedBlockingDeque<ByteBuffer>();
					tempIndexCache.put(queueName, queue);
				}
			}
		}
		return queue;
	}
	
	public synchronized static File getTempFile(String queueName) {
		String rootPath = NotifyCfg.getRootPath();
		NotifyUtility.checkDir(rootPath);
		String queuePath = NotifyUtility.buildPath(rootPath, queueName);
		NotifyUtility.checkDir(queuePath);
		String tempFilePath = NotifyUtility.buildPath(queuePath, "cache");
		File file = new File(tempFilePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
		return file;
	}
	
	public static FileChannel getTempFileChannel(File file, String mode) {
		try {
			return new RandomAccessFile(file, mode).getChannel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将临时缓存转换为持久化临时文件
	 *  
	 * @param queueName
	 * @param serverName
	 */
	public static void beginPersistTempCache(String queueName) {
		Boolean cannotPersist = canotPersistTemp.get(queueName);
		
		if (cannotPersist == null || !cannotPersist) {
			Boolean hasPersist = startedTempPersist.get(queueName);
			if (hasPersist == null || !hasPersist) {
				synchronized(MessageCache.class) {
					hasPersist = startedTempPersist.get(queueName);
					if (hasPersist == null || !hasPersist) {
						startedTempPersist.put(queueName, true);
						new Thread(new PersistUnCacheIndex(queueName)).start();
					}
				}
			}
		}
	}
	
	/**
	 * 判断是否存在临时缓存数据，若存在，则不允许写入缓存数据 
	 * @param queueName
	 * @param serverName
	 * @return
	 */
	public static boolean hasTempCache(String queueName) {
		BlockingQueue<ByteBuffer> tempCache = tempIndexCache.get(queueName);
		boolean isTempCache =  tempCache != null && !tempCache.isEmpty();
		if (!isTempCache) {
			Boolean hasData = hasDataInFile.get(queueName);
			isTempCache = hasData == null ? false : hasData;
		}
		return isTempCache;
	}

	/**
	 * 从临时缓存中加载数据到缓存中
	 *  
	 * @param queueName
	 * @param serverName
	 */
	public static void loadCacheFromTemp(String queueName) {
		if (!hasTempCache(queueName)) {
			return ;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Begin load cache from temp file !");
		}
		
		Boolean isLoading = startedloadFromTempPersist.get(queueName);
		if (isLoading == null || !isLoading) {
			synchronized (MessageCache.class) {
				isLoading = startedloadFromTempPersist.get(queueName);
				if (isLoading == null || !isLoading) {
					startedloadFromTempPersist.put(queueName, true);
					new Thread(new ReloadCacheData(queueName)).start();
					return ;
				}
			}
		}
	}
	
	/**
	 * 将临时缓存数据加载到缓存中 
	 * @param queueName
	 * @param serverName
	 * @param fileName
	 * @param offset
	 * @param mustAddCache
	 * @return
	 */
	public static boolean loadCacheByTemp(String queueName, String serverName, long fileName, int offset, boolean mustAddCache) {
		FileUtility fileUtility = ServerFileProxy.getFileUtility(queueName, serverName, fileName);
		
		ByteBuffer index = fileUtility.getIndex(offset);
		if (index != null) {
			short state = index.getShort();
			NotifyUtility.releaseByteBuffer(index);
			String key = buildRemoveKey(queueName, serverName, fileName, offset);
			if (NotifyUtility.MESSSAGE_STATE.isOverState(state)) {
				if (!removeCacheDatas.isEmpty()) {
					removeCacheDatas.remove(key);
				}
				return true;
			} else {
				if (removeCacheDatas.contains(key)) {
					removeCacheDatas.remove(key);
					return true;
				}
			}
		}
		
		ByteBuffer indexOffset = NotifyUtility.buildMessageOffset(queueName, serverName, fileName, offset);
		ByteBuffer data = fileUtility.getMessageByIndexOffset(offset);
		boolean addSuccess = addCache(queueName, serverName, fileName, indexOffset, -1, data, true, mustAddCache);
		NotifyUtility.releaseByteBuffer(indexOffset);
		return addSuccess;
	}
	/**
	 * 添加要缓存的数据
	 * 
	 * @param indexOffset 信息的偏移量
	 * @param ignoreTempCache 是否忽略临时缓存的影响
	 * @param mustAddQueue 忽略其他判断条件，强制写入缓存中, 目前仅在临时缓存文件还有一小部分徐同步到缓存中时使用，其他情况禁止使用
	 * @param data 消息内容
	 */
	public static boolean addCache(String queueName, String serverName, long fileName, ByteBuffer indexOffset, int offset, ByteBuffer data, boolean ignoreTempCache, boolean mustAddQueue) {
		try {
			if (offset <= 0 && indexOffset != null) {
				indexOffset.rewind();
				indexOffset.position(NotifyUtility.INDEX_OFFSET_PREFIX_LENGTH);
				offset = indexOffset.getInt();
				indexOffset.rewind();
			}
			if (offset < 0) {
				return false;
			}
			if (mustAddQueue) {
				addQueue(queueName, serverName, fileName, offset, data);
				return true;
			}
			// 当ignoreTempCache为true: 即不考虑临时缓存的影响时，则如果缓存可写入则写入，不可写入就返回false
			// 当ignoreTempCache为false: 即考虑临时缓存的影响时, 如果临时缓存中存在数据则不向缓存中增加数据
			if ((ignoreTempCache || !MessageCache.hasTempCache(queueName)) && (MessageCache.getQueue(queueName).size() < NotifyUtility.CACHE_DATA_MAX_SIZE)) {
				addQueue(queueName, serverName, fileName, offset, data);
			} else {
				if (ignoreTempCache) {
					return false;
				}
				ByteBuffer tempData = ByteBuffer.allocate(NotifyUtility.getMaxServerNameLength() + NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength());
				tempData.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
				tempData.putLong(fileName);
				tempData.putInt(offset);
				tempData.flip();
				BlockingQueue<ByteBuffer> tempQueue = MessageCache.getTempCacheQueue(queueName);
				tempQueue.add(tempData);
				
				if (tempQueue.size() >= NotifyUtility.TEMP_CACHE_DATA_MAX_SIZE) {
					MessageCache.beginPersistTempCache(queueName);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			NotifyUtility.releaseByteBuffer(data);
		}
		return false;
	}

	public static void removeCache(String queueName, String serverName, long fileName, int indexOffset) {
		BlockingQueue<CacheData> queue = MessageCache.getQueue(queueName);
		CacheData data;
		boolean isRemoved = false;
		while ((data = queue.peek()) != null ) {
			if (serverName.equals(data.getServerName()) && data.getFileName() == fileName && data.getIndexOffset() == indexOffset) {
				NotifyUtility.releaseByteBuffer(data.getData());
				queue.remove(data);
				
				if (queue.size() < (NotifyUtility.CACHE_DATA_MAX_SIZE / 2)) {
					MessageCache.loadCacheFromTemp(queueName);
				}
				
				isRemoved = true;
				break;
			}
		}
		
		if (!isRemoved) {
			String removeKey = buildRemoveKey(queueName, serverName, fileName, indexOffset);
			removeCacheDatas.add(removeKey);
		}
	}
	
	public static String buildRemoveKey(String queueName, String serverName, Object fileName, Object indexOffset) {
		String removeKey = NotifyUtility.buildKey(queueName, serverName);
		removeKey = NotifyUtility.buildKey(removeKey, fileName);
		removeKey = NotifyUtility.buildKey(removeKey, indexOffset);
		return removeKey;
	}
	
	public static CacheData getData(BlockingQueue<CacheData> queue, String queueName) throws InterruptedException {
		if (queue.size() < (NotifyUtility.CACHE_DATA_MAX_SIZE - NotifyUtility.CACHE_DATA_MAX_SIZE/5)) {
			MessageCache.loadCacheFromTemp(queueName);
		}
		
		CacheData data = queue.poll(10, TimeUnit.MILLISECONDS);//.take();
		
		if (data == null) {
			if (log.isDebugEnabled()) {
				log.debug("Get data from queue is null!");
			}
			return data;
		}
		
		if (!removeCacheDatas.isEmpty()) {
			String removeKey = buildRemoveKey(queueName, data.getServerName(), data.getFileName(), data.getIndexOffset());
			
			if (removeCacheDatas.contains(removeKey)) {
				removeCacheDatas.remove(removeKey);
				NotifyUtility.releaseByteBuffer(data.getData());
				if (log.isDebugEnabled()) {
					log.debug("Get cache data is consumered! queueName :" + queueName + "; serverName : " + data.getServerName() + 
							"; fileName : " + data.getFileName() + "; indexoffset :" + data.getIndexOffset() + "; queue size :" + queue.size());
				}
				return getData(queue, queueName);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Get cache data ! queueName :" + queueName + "; serverName : " + data.getServerName() + 
					"; fileName : " + data.getFileName() + "; indexoffset :" + data.getIndexOffset() + "; queue size :" + queue.size());
		}
		return data;
	}
	public static void addQueue(String queueName, String serverName, long fileName, int offset, ByteBuffer data) {
		BlockingQueue<CacheData> queue = MessageCache.getQueue(queueName);
		queue.add(new CacheData(serverName, fileName, offset, data));
		if (log.isDebugEnabled()) {
			log.debug("Add data to cache queue! queueName :" + queueName + "; serverName :" + serverName + 
					"; fileName :" + fileName + "; offset:" + offset + "; queue size :" + queue.size());
		}
	}
	
	public static void addQueueFirst(String queueName, String serverName, long fileName, int offset, ByteBuffer data) {
		BlockingQueue<CacheData> queue = MessageCache.getQueue(queueName);
		((LinkedBlockingDeque<CacheData>)queue).offerFirst(new CacheData(serverName, fileName, offset, data));
		
		if (log.isDebugEnabled()) {
			log.debug("Add data to cache queue first! queueName :" + queueName + "; serverName :" + serverName + 
					"; fileName :" + fileName + "; offset:" + offset + "; queue size :" + queue.size());
		}
	}
	
	/**
	 * 将未缓存的数据进行持久化
	 * 
	 * @className:MessageCache.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-3-18
	 */
	static class PersistUnCacheIndex implements Runnable {
		
		private String queueName;
		
		public PersistUnCacheIndex(String queueName) {
			this.queueName = queueName;
		}
		
		public void run() {
			File file = tempFiles.get(queueName);
			FileChannel channel = tempFileChannels.get(queueName);
			if (file == null) {
				file = getTempFile(queueName);
				channel = getTempFileChannel(file, "rw");
				tempFiles.put(queueName, file);
				tempFileChannels.put(queueName, channel);
			}
			BlockingQueue<ByteBuffer> queue = tempIndexCache.get(queueName);
			boolean hasDataPersist = false;
			while (true) {
				ByteBuffer index = null;
				try {
					tempFileLock.readLock().lock();
					Boolean cannotPersistTemp = canotPersistTemp.get(queue);
					if (cannotPersistTemp != null && cannotPersistTemp) {
						break;
					}
					if (queue.size() < NotifyUtility.TEMP_CACHE_DATA_MAX_SIZE / 2) {
						break;
					}
					index = queue.poll();
					
					if (index != null) {
						if (!hasDataPersist) {
							hasDataInFile.put(queueName, true);
							hasDataPersist = true;
						}
						channel.write(index);
						//channel.force(true);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					log.error(e.getMessage());
					if (index != null) {
						index.rewind();
						((LinkedBlockingDeque<ByteBuffer>)queue).addFirst(index);
					}
					break;
				} finally {
					NotifyUtility.releaseByteBuffer(index);
					tempFileLock.readLock().unlock();
				}
			}
			startedTempPersist.put(queueName, false);
		}
	}
	
	/**
	 * 读取未处理的消息并加入到数据缓存中
	 * 
	 * @className:MessageCache.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-3-18
	 */
	static class ReloadCacheData implements Runnable {

		private String queueName;
		
		public ReloadCacheData(String queueName) {
			this.queueName = queueName;
		}
		
		public void run() {
			Boolean hasData = hasDataInFile.get(queueName);
			boolean isPersisted = hasData == null ? false : hasData;
			
			if (isPersisted) {
				File file = tempFiles.get(queueName);
				FileChannel channel = tempFileChannels.get(queueName);//getTempFileChannel(file, "r");
				// 获取前一次读取信息的位置
				Long position = tempPersistReadPosition.get(queueName);
				position = position == null ? 0L : position;
				long fileSize = file.length();
				int tempDataSize = NotifyUtility.getMaxServerNameLength() + NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength();
				ByteBuffer tempData = ByteBuffer.allocate(tempDataSize);
				boolean mustAddCache = false;
				try {
					long i = 0;
					for (i = position; i < fileSize; ) {
						channel.read(tempData, i);
						tempData.rewind();
						byte[] serverNameBytes = new byte[NotifyUtility.getMaxServerNameLength()];
						tempData.get(serverNameBytes);
						String serverName = new String(serverNameBytes);
						long fileName = tempData.getLong();
						int offset = tempData.getInt();
						tempData.clear();
						if (!mustAddCache) {
							fileSize = file.length();
							long remainDataSize = (fileSize - i) / tempDataSize;
							// 当临时缓存文件中剩余的数据量 不大于 MOST_REMAIN_PERSIST_SIZE 
							// 且
							// 临时缓存中的数据量不大于其最大存储量的一半时
							// 此时将停止向临时缓存文件中同步数据，并将临时缓存文件中的数据强制写入到缓存数据中
							if ((remainDataSize <= NotifyUtility.MOST_REMAIN_PERSIST_SIZE) && 
									(tempIndexCache.get(queueName).size() <= NotifyUtility.TEMP_CACHE_DATA_MAX_SIZE / 2)) {
								tempFileLock.writeLock().lock();
								canotPersistTemp.put(queueName, true);
								mustAddCache = true;
								fileSize = file.length();
								
							}
						}
						boolean isAddCacheSuccess = loadCacheByTemp(queueName, serverName, fileName, offset, mustAddCache);
						if (!isAddCacheSuccess) {							
							break;
						}
						
						i += tempDataSize;
						if (i >= fileSize) {
							fileSize = file.length();
						}
					}
					
					fileSize = file.length();
					long remainDataSize = (fileSize - i) / tempDataSize;
					if (!mustAddCache && remainDataSize <= NotifyUtility.MOST_REMAIN_PERSIST_SIZE) {
						tempFileLock.writeLock().lock();
						canotPersistTemp.put(queueName, true);
						mustAddCache = true;
					}
					tempPersistReadPosition.put(queueName, i);
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
				
				/*try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				if (mustAddCache) {
					try {
						channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					file.delete();
					tempFiles.remove(queueName);
					tempFileChannels.remove(queueName);
					
					hasDataInFile.put(queueName, false);
					tempPersistReadPosition.put(queueName, 0L);
					canotPersistTemp.put(queueName, false);
					tempFileLock.writeLock().unlock();
				}
				NotifyUtility.releaseByteBuffer(tempData);
			} else {
				BlockingQueue<ByteBuffer> tempCache = tempIndexCache.get(queueName);
				while (true) {
					ByteBuffer tempData = tempCache.poll();
					if (tempData == null && tempCache.isEmpty()) {
						break;
					}
					byte[] serverNameBytes = new byte[NotifyUtility.getMaxServerNameLength()];
					tempData.get(serverNameBytes);
					String serverName = new String(serverNameBytes);
					long fileName = tempData.getLong();
					int offset = tempData.getInt();
					
					boolean addCacheSuccess = loadCacheByTemp(queueName, serverName, fileName, offset, false);
					if (!addCacheSuccess) {
						NotifyUtility.releaseByteBuffer(tempData);
						break;
					}
					NotifyUtility.releaseByteBuffer(tempData);
				}
			}
			startedloadFromTempPersist.put(queueName, false);
		}
	}
	
	/**
	 * 在应用启动时通过扫面索引文件，加载待消费的信息
	 * 
	 * @className:MessageCache.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-3-19
	 */
	static class ReloadUnConsumerData implements Runnable {

		@SuppressWarnings("static-access")
		public void run() {
			log.info("Begin check if can load unconsumer persist data to cache!");
			// 当服务未标记为可接收数据时，暂不加载持久化的数据，避免主备数据不一致
			List<String[]> queueAndServers = ServerFileProxy.getQueueAndServers();
			for (String[] queueAndServer : queueAndServers) {
				String queueName = queueAndServer[0];
				String serverName = queueAndServer[1];
				
				// 清除临时缓存文件
				File file = getTempFile(queueName);
				if (file.exists()) {
					log.info("Remove temp cache file which queue name is " + queueName + " ; server name is " + serverName);
					file.delete();
				}
				
				while (true) {
					if (!NotifyServer.canCachePersist(queueName, serverName)) {
						try {
							Thread.currentThread().sleep(1000);
							log.info("Server can not cache the persist now!");
						} catch (InterruptedException e) {
							e.printStackTrace();
							log.error(e.getMessage());
						}
					} else {
						break;
					}
				}
			}
			
			log.info("Begin load unconsumer persist data to cache!");
			// 1: 删除临时缓存文件
			// 2: 遍历文件句柄，找出待消费信息，通过DealProducerRequest的addCache方法将信息加入缓存
			Set<String> files = FileProxy.getFileSet();
			for (String filePath : files) {
				String[] filePathArr = NotifyUtility.splitKey(filePath);
				String queueName = filePathArr[0];
				String serverName = filePathArr[1];
				// 加载待处理信息
				long fileName = Long.valueOf(filePathArr[2]);
				FileUtility fileUtility = FileProxy.getFileUtility(queueName, serverName, fileName);
				int [] indexRange = NotifyServer.getIndexRangeBeforeServer(queueName, serverName, fileName);
				for (int i = indexRange[0]; i <= indexRange[1]; ) {
					if (i < 0) {
						i = 0;
					}
					ByteBuffer index = fileUtility.getIndex(i);
					short state = index.getShort();
					if (NotifyUtility.MESSSAGE_STATE.hasState(state) && !NotifyUtility.MESSSAGE_STATE.isOverState(state)) {
						int messageOffset = index.getInt();
						int messageLength = index.getInt();
						index.rewind();
						addCache(queueName, serverName, fileName, null, i, fileUtility.getMessage(messageOffset, messageLength), false, false);
					}
					NotifyUtility.releaseByteBuffer(index);
					i += NotifyUtility.INDEX_LENGTH;
				}
			}
			
			log.info("Unconsumer persist file is loaded to cache !");
		}
	}
	
	/**
	 * 校验需要冲缓存中清楚，但是未在缓存中找到的缓存数据 是否需要清理掉 
	 * @className:MessageCache.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-3-30
	 */
	static class CheckRemoveCacheDatas implements Runnable {

		public void run() {
			Iterator<String> removeIter = removeCacheDatas.iterator();
			while (removeIter.hasNext()) {
				String data = removeIter.next();
				String[] datas = NotifyUtility.splitKey(data);
				if (datas == null) {
					removeIter.remove();
					continue;
				}
				String queueName = datas[0];
				String serverName = datas[1];
				long fileName = Long.valueOf(datas[2]);
				int indexOffset = Integer.valueOf(datas[3]);
				FileUtility fileUtility = ServerFileProxy.getFileUtility(queueName, serverName, fileName);
				ByteBuffer index = fileUtility.getIndex(indexOffset);
				
				if (index == null || NotifyUtility.MESSSAGE_STATE.isOverState(index.getShort())) {
					removeIter.remove();
				}
				NotifyUtility.releaseByteBuffer(index);
			}
		}
		
	}

}
