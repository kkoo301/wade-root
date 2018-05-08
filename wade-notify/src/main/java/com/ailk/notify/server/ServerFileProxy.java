/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ailk.notify.common.FileProxy;
import com.ailk.notify.common.FileUtility;
import com.ailk.notify.common.NotifyCfg;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.server.impl.DealConsumerRequest;
import com.ailk.notify.server.impl.DealProducerRequest;

/**
 * 初始化生产者和消费者的处理线程，并对实时可用的文件进行监控（包括生成备用的文件内容，监控可用文件，监控是否要将文件搬到历史表等）
 * 
 * @className:ServerFileProxy.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-6 
 */
public class ServerFileProxy extends FileProxy {

	private static final transient Logger log = Logger.getLogger(ServerFileProxy.class);
	
	private BlockingQueue<ChannelMap> queue;
	private DealRequest dealRequest = null;

	// 存储当前可用的文件名
	private static ConcurrentHashMap<String, BlockingQueue<Long>> availFileNames = new ConcurrentHashMap<String, BlockingQueue<Long>>();
	public static ScheduledExecutorService cacheFileService = Executors.newScheduledThreadPool(1);
	public static ScheduledExecutorService moveHistoryService = Executors.newScheduledThreadPool(1);
	
	private static Map<String, List<Long>> historyFileNames = new ConcurrentHashMap<String, List<Long>>();
	private static Map<String, List<Long>> activeFileNames = new ConcurrentHashMap<String, List<Long>>();
	
	private String queueName;
	private String serverName;
	private short type;
	
	public ServerFileProxy(short type, String queueName, String serverName) {
		if (type == NotifyUtility.CLIENT_TYPE.PRODUCER.getType()) {
			dealRequest = new DealProducerRequest(queueName, serverName, this);
		} else if (type == NotifyUtility.CLIENT_TYPE.CONSUMER.getType()) {
			dealRequest = new DealConsumerRequest(queueName, serverName, this);
		} else {
			throw new RuntimeException("The type value " + type + " is invalid!");
		}
		this.type = type;
		this.queueName = queueName;
		this.serverName = serverName;
		queue = ServerFileDealPool.getChannelQueue(type, queueName, serverName);
	}
	
	public static void init() {
		loopCurFiles();
		
		// 1s校验一次是否需要继续增加缓存的文件名
		cacheFileService.schedule(new CheckAvailableFiles(), 1, TimeUnit.SECONDS);
		
		// 校验文件是否要搬迁到历史目录中
		moveHistoryService.schedule(new CheckUnAvailableFiles(), 30, TimeUnit.MINUTES);
		
		new CheckUnAvailableFiles().run();
		new CheckAvailableFiles().run();
	}
	
	public static void reloadUnConsumerData() {
		// 加载缓存数据(服务启动时从索引文件中判断是否要加载进行消费)
		new Thread(new MessageCache.ReloadUnConsumerData()).start();
	}
	
	/**
	 *  处理通道流中传递的数据
	 *  
	 * @return
	 * @throws Exception 
	 */
	public void run() {
		while (true) {
			try {
				if (log.isDebugEnabled()) {
					log.debug("File proxy begin take from queue, time :"  + System.currentTimeMillis() + "; queueName :" + queueName + "; serverName :" + serverName);
				}
				ChannelMap channelMap = queue.poll(5, TimeUnit.SECONDS);
				if (channelMap == null) {
					continue;
				}
				if (log.isDebugEnabled()) {
					log.debug("File proxy get requtest, time :" + System.currentTimeMillis() + "; this :" + dealRequest.toString() + "; client :" + channelMap.getChannel().toString());
				}
				if (!channelMap.isChannelActive()) {
					continue;
				}
				
				if (log.isDebugEnabled()) {
					log.debug("Get Request from " + (type == NotifyUtility.CLIENT_TYPE.PRODUCER.getType() ? "producer" : "consumer") + "! queueName : " + queueName + "; serverName :" + serverName);
				}
				
				channelMap.setQueueName(queueName);
				channelMap.setServerName(serverName);
				dealRequest.execute(channelMap);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}
	
	/**
	 * 获取缓存的文件名
	 * 
	 * @return
	 */
	public long createFileName(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		BlockingQueue<Long> namesQueue = availFileNames.get(key);
		long fileName = -1;
		try {
			/*long newFileName = createFileName(namesQueue);
			getFileUtility(queueName, serverName, newFileName);*/
			fileName = namesQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return fileName;
	}
	
	public static List<String[]> getQueueAndServers() {
		String ip = NotifyUtility.getLocalIp();
		int port = NotifyUtility.getServerPort();
		
		List<String[]> queueAndServers = null;
		if (NotifyUtility.getServerType().equals(NotifyUtility.SERVER_TYPE.SERVER)) {
			queueAndServers = NotifyCfg.getQueueAndServerAddrNameByAddr(ip, port);
		} else if (NotifyUtility.getServerType().equals(NotifyUtility.SERVER_TYPE.LOG)) {
			queueAndServers = NotifyCfg.getAllQueueAndServerAddrNames();
		}
		if (log.isDebugEnabled()) {
			for (String [] queueAndServer : queueAndServers) {
				log.debug("queueName : " + queueAndServer[0] + "; serverName :" +queueAndServer[1]);
			}
		}
		
		return queueAndServers;
	}
	
	
	/**
	 * 遍历当前目录的所有文件，记录
	 */
	public static void loopCurFiles() {
		String rootPath = NotifyCfg.getRootPath();
		NotifyUtility.checkDir(rootPath);
		
		List<String[]> queueAndServers = getQueueAndServers();
		
		if (log.isInfoEnabled()) {
			log.info("Queue And Servers : " + queueAndServers.toString() + "; size :" + queueAndServers.size());
		}
		
		for (String [] queueAndServer : queueAndServers) {
			String queueName = queueAndServer[0];
			String serverName = queueAndServer[1];
			
			loadHistoryFiles(queueName, serverName);
			
			String queuePath = NotifyUtility.buildPath(rootPath, queueName);
			NotifyUtility.checkDir(queuePath);
			String serverPath = NotifyUtility.buildPath(queuePath, serverName);
			NotifyUtility.checkDir(serverPath);
			
			Long[] fileNames = NotifyUtility.listFileNames(serverPath);
			
			if (log.isInfoEnabled()) {
				log.info("list files : queueName : " + queueName + "; serverName :" + serverName + " ; path :" + serverPath + "; fileNames " + fileNames.toString() + " ; size : " + fileNames.length);
			}
			
			if (fileNames != null) {
				for (Long fileName : fileNames) {
										
					// 启动过程中，对于已经有数据的文件，标记为不可继续写入，避免HA过程中出现数据冲突
					FileUtility fileUtility = getFileUtility(queueName, serverName, fileName);
					
					if (fileUtility != null && fileUtility.getStreamFileSize() == 0 && fileUtility.getMessageOffset() == 0) {
						String key = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), String.valueOf(fileName));
						fileUtilityMaps.remove(key, fileUtility);
						fileUtility.setAvailable(false);
						fileUtility.remove();
						continue;
					}
					
					if (fileUtility != null && fileUtility.getIndexLength() > 0) {
						fileUtility.setAvailable(false);
					}
					addActiveFileName(queueName, serverName, fileName);
					addAvailFileName(queueName, serverName, fileName);
				}
			}
			checkAvailFile(queueName, serverName);
		}
	}
	
	/**
	 * 在服务启动或HA链接断开是调用，将现有的所有有数据的文件标记为不可继续写入（可更新状态）,
	 * 避免HA同步过程中出现数据冲突
	 * 
	 */
	public static void setAllUnBlankFileUnAvailable() {
		Set<String> keys = getFileSet();
		for (String key : keys) {
			FileUtility fileUtility = getFileUtility(key);
			if (fileUtility != null && fileUtility.getIndexLength() > 0) {
				fileUtility.setAvailable(false);
			}
		}
		
		List<String[]> queueAndServers = getQueueAndServers();//getAllQueueAndServerAddrNames();
		for (String[] queueAndServer : queueAndServers) {
			checkAvailFile(queueAndServer[0], queueAndServer[1]);
		}
	}
	
	/**
	 * 校验是否需要更新可写文件列表
	 *  
	 * @param queueName
	 * @param serverName
	 */
	public static synchronized void checkAvailFile(String queueName, String serverName) {
		if (NotifyUtility.getServerType().equals(NotifyUtility.SERVER_TYPE.SERVER)) {
			checkCanWriteFile(queueName, serverName);
			initAvailFileNames(queueName, serverName);
		}
	}
	
	/**
	 * 获取对应的历史文件名字集合
	 * 
	 * @param queueName
	 * @param serverName
	 */
	public static void loadHistoryFiles(String queueName, String serverName) {
		String rootPath = NotifyCfg.getRootPath();
		NotifyUtility.checkDir(rootPath);
		String historyPath = NotifyUtility.buildPath(rootPath, NotifyUtility.HISTORY_DIR);
		NotifyUtility.checkDir(historyPath);
		String hisQueuePath = NotifyUtility.buildPath(historyPath, queueName);
		NotifyUtility.checkDir(hisQueuePath);
		String hisServerPath = NotifyUtility.buildPath(hisQueuePath, serverName);
		NotifyUtility.checkDir(hisServerPath);
		
		if (log.isInfoEnabled()) {
			log.info("History file path :" + hisServerPath + "; queueName :" + queueName + "; serverName :" + serverName);
		}
		
		Long[] fileNames = NotifyUtility.listFileNames(hisServerPath);
		if (fileNames == null) {
			return;
		}
		for (Long fileName : fileNames) {
			addHistoryFileName(queueName, serverName, fileName);
		}
	}
	
	/**
	 * 设置文件状态为等待使用状态
	 * 
	 * @param queueName
	 * @param serverName
	 * @param fileName
	 */
	public static void addAvailFileName(String queueName, String serverName, long fileName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		BlockingQueue<Long> nameQueue = availFileNames.get(key);
		if (nameQueue == null) {
			nameQueue = new LinkedBlockingQueue<Long>();
		}
		if (!nameQueue.contains(fileName)) {
			nameQueue.add(fileName);
		}
	}
	
	/**
	 * 检测文件是否可继续写入 
	 * 
	 * @param queueName
	 * @param serverName
	 */
	public static void checkCanWriteFile(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		
		BlockingQueue<Long> fileNames = availFileNames.get(key);
		if (log.isDebugEnabled()) {
			log.debug("queueName : " + queueName + "; serverName :" + serverName + "; fileNames :" + fileNames);
		}
		if (fileNames == null) {
			return ;
		}
		int size = fileNames.size();
		for (int i = 0; i < size; i++) {
			Long fileName = fileNames.peek();
			FileUtility fileUtility = fileUtilityMaps.get(NotifyUtility.buildKey(key, fileName.toString()));
			if (!fileUtility.isAvailable()) {
				fileNames.remove(fileName);
			}
		}
	}
	
	/**
	 * 校验queue和serverName对应的文件对是否满足当前线程的需要，不满足则创建新的文件，并放入可用队列中
	 * @param queueName
	 * @param serverName
	 */
	public static void initAvailFileNames(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		BlockingQueue<Long> nameQueue = availFileNames.get(key);
		int size = 0;
		if (nameQueue == null) {
			size = NotifyCfg.getDealFileThreadSize(queueName);
			nameQueue = new LinkedBlockingQueue<Long>();
			availFileNames.put(key, nameQueue);
		} else {
			size = NotifyCfg.getDealFileThreadSize(queueName) - nameQueue.size();
		}
		
		if (log.isDebugEnabled()) {
			log.debug("init avail file name, queueName : " + queueName + "; serverName :" + serverName + "; size :" + size);
		}
		
		for (int i = 0; i < size; i++) {
			long fileName = createFileName(nameQueue);
			getFileUtility(queueName, serverName, fileName).setPrepareState(true);
		}
	}
	
	@SuppressWarnings("static-access")
	private static synchronized long createFileName(BlockingQueue<Long> nameQueue) {
		long fileName = System.currentTimeMillis();
		nameQueue.add(fileName);
		try {
			// 避免数据重复
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	public static List<Long> getHistoryFileNames(String queueName, String serverName) {
		return historyFileNames.get(NotifyUtility.buildKey(queueName, serverName));
	}
	
	public static List<Long> getActiveFileNames(String queueName, String serverName) {
		return activeFileNames.get(NotifyUtility.buildKey(queueName, serverName));
	}
	
	public static void addActiveFileName(String queueName, String serverName, long fileName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		List<Long> fileList = activeFileNames.get(key);
		if (fileList == null) {
			synchronized (activeFileNames) {
				fileList = activeFileNames.get(key);
				if (fileList == null) {
					fileList = new ArrayList<Long>();
					activeFileNames.put(key, fileList);
				}
			}
		}
		fileList.add(fileName);
	}
	
	public static void removeActiveFileName(String queueName, String serverName, Long fileName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		List<Long> fileList = activeFileNames.get(key);
		if (fileList != null) {
			fileList.remove(fileName);
		}
	}
	
	public static void addHistoryFileName(String queueName, String serverName, long fileName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		List<Long> fileList = historyFileNames.get(key);
		if (fileList == null) {
			synchronized (historyFileNames) {
				fileList = historyFileNames.get(key);
				if (fileList == null) {
					fileList = new ArrayList<Long>();
					historyFileNames.put(key, fileList);
				}
			}
		}
		fileList.add(fileName);
	}
	
	/**
	 *  
	 * @className:FileProxy.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-3-17
	 */
	static class CheckAvailableFiles implements Runnable {

		public void run() {
			Set<String> keys = availFileNames.keySet();
			for (String key : keys) {
				String[] queueAndServerName = NotifyUtility.splitKey(key);
				String queueName = queueAndServerName[0];
				String serverName = queueAndServerName[1];
				
				// 当服务端不能接收数据时， 不进行文件名自动生成
				// 避免HA过程造成文件过多
				// 且在服务作为备用时, 可减少资源消耗
				if (!NotifyServer.canAcceptData(queueName, serverName)) {
					continue;
				}
				
				BlockingQueue<Long> fileNameQueue = availFileNames.get(key);
				
				int threadSize = NotifyCfg.getDealFileThreadSize(queueName);
				int curSize = fileNameQueue.size();
				// availFileNames 存储的是备用的文件名, 目前设置保持与线程个数一致，在文件创建频率极高，当前设置不满足的情况下，可增加缓存的文件数目
				for (int i = curSize; i < threadSize; i++) {
					long fileName = createFileName(fileNameQueue);
					getFileUtility(queueName, serverName, fileName).setPrepareState(true);
				}
			}
		}
	}
	
	/**
	 * 检查已经处理完毕的文件，并转移到历史目录中
	 * 
	 * @className:FileProxy.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-3-17
	 */
	static class CheckUnAvailableFiles implements Runnable {
		
		public void run() {
			Set<String> keys = fileUtilityMaps.keySet();
			if (log.isInfoEnabled()) {
				log.info("begin check un available files! fileMaps : " + keys.size());
			}
			for (String key : keys) {
				FileUtility fileUtility = fileUtilityMaps.get(key);
				if (fileUtility.isAvailable()) {
					continue;
				}
				
				if (log.isInfoEnabled()) {
					log.info("begin check file, key :" + key);
				}
 				
				if (fileUtility.dealIfAllConsumered()) {
					String[] data = NotifyUtility.splitKey(key);
					removeActiveFileName(data[0], data[1], Long.valueOf(data[2]));
					addHistoryFileName(data[0], data[1], Long.valueOf(data[2]));
					moveUnAvailableFileToHistory(data[0], data[1], data[2]);
					fileUtilityMaps.remove(key, fileUtility);
					fileUtility.destroy();
				}
			}
			
			if (log.isInfoEnabled()) {
				log.info("end check un available files!");
			}
		}
	}
	
	private static void moveUnAvailableFileToHistory(String queueName, String serverName, String fileName) {
		String rootPath = NotifyCfg.getRootPath();
		NotifyUtility.checkDir(rootPath);
		String historyPath = NotifyUtility.buildPath(rootPath, NotifyUtility.HISTORY_DIR);
		NotifyUtility.checkDir(historyPath);
		String hisQueuePath = NotifyUtility.buildPath(historyPath, queueName);
		NotifyUtility.checkDir(hisQueuePath);
		String hisServerPath = NotifyUtility.buildPath(hisQueuePath, serverName);
		NotifyUtility.checkDir(hisServerPath);
		String hisFilePath = NotifyUtility.buildPath(hisServerPath, fileName);
		String hisIndexFilePath = hisFilePath + NotifyUtility.INDEX_FILE_SUFFIX; 
		String hisMsgFilePath = hisFilePath + NotifyUtility.MSG_FILE_SUFFIX;
		
		String queuePath = NotifyUtility.buildPath(rootPath, queueName);
		String serverPath = NotifyUtility.buildPath(queuePath, serverName);
		String filePath = NotifyUtility.buildPath(serverPath, fileName);
		String indexFilePath = filePath + NotifyUtility.INDEX_FILE_SUFFIX;
		String msgFilePath = filePath + NotifyUtility.MSG_FILE_SUFFIX;
		String streamFilePath = filePath + NotifyUtility.STREAM_FILE_SUFFIX;
		String streamPosFilePath = filePath + NotifyUtility.STREAM_POSITION_FILE_SUFFIX;
		
		File indexFile = new File(indexFilePath);
		File hisIndexFile = new File(hisIndexFilePath);
		
		File streamFile = new File(streamFilePath);
		File streamPosFile = new File(streamPosFilePath);
		
		File msgFile = new File(msgFilePath);
		boolean indexSuccess = true;
		if (indexFile.exists()) {
			if (!msgFile.exists() || msgFile.length() == 0) {
				indexSuccess = indexFile.delete();
			} else {
				indexSuccess = indexFile.renameTo(hisIndexFile);
			}
		}
		if (indexSuccess) {
			boolean msgSuccess = true;
			if (msgFile.exists()) {
				if (msgFile.length() == 0) {
					msgSuccess = msgFile.delete();
				} else {
					File hisMsgFile = new File(hisMsgFilePath);
					msgSuccess = msgFile.renameTo(hisMsgFile);
				}
			}
			
			if (log.isInfoEnabled()) {
				log.info("delete file on move to history!");
			}
			if (msgSuccess) {
				if (streamFile.exists()) {
					streamFile.delete();
				}
				if (streamPosFile.exists()) {
					streamPosFile.delete();
				}
			}
			if (!msgSuccess) {
				log.error("Msg File Move to History Failed ! Queue : " + queueName + "; Server name : " + serverName + "; File name :" + fileName);
			}
		} else {
			log.error("Index File Move to History Failed ! Queue : " + queueName + "; Server name : " + serverName + "; File name :" + fileName);
		}
		
		/*if (!msgFile.exists() || msgFile.length() == 0) {
			getFileUtility(queueName, serverName, Long.valueOf(fileName)).remove();
		}*/
	}
	
}
