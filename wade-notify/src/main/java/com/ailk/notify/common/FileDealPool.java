/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.ailk.notify.server.NotifyServer;

/**
 * 根据该进程的端口号，和其对应的主机的ip，在notify.xml中找到对应的那些topic和serverAddrName配置，
 * 以及notify.xml的threadsize的配置来进行初始化；
 *
 * 日志服务需要读取notify.xml中所有的topic和serverAddr进行初始化；
 * 消息服务端只需要初始化端口对应的配置组；
 * 日志服务和消息服务通过各自启动的时候增加参数来区分.
 * 
 * 当服务端接收到消息时，根据传入的topic 和 serverAddrName将消息传入对应的处理线程，可根据需要决定是否将处理结果返回；
 * 
 * @className:FileDealPool.java
 * 
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-5 
 */
public abstract class FileDealPool {
	
	private static final transient Logger log = Logger.getLogger(FileDealPool.class);
	
	private static final String localIp = NotifyUtility.getLocalIp();
	private static final int localPort = NotifyUtility.getServerPort();
	
	private static List<String[]> queueAndAddrNames;
	private static Map<String, ExecutorService> dealService = new HashMap<String, ExecutorService>();
	
	public void init() {
		// 消息服务端 初始化 端口对应的文件目录
		if (NotifyUtility.SERVER_TYPE.SERVER.equals(NotifyUtility.getServerType())) {
			queueAndAddrNames = NotifyCfg.getQueueAndServerAddrNameByAddr(localIp, localPort);
		}
		
		// 日志服务端 初始化所有的文件目录
		if (NotifyUtility.SERVER_TYPE.LOG.equals(NotifyUtility.getServerType())) {
			queueAndAddrNames = NotifyCfg.getAllQueueAndServerAddrNames();
		}
		
		// 初始化当前服务可处理的队列线程池
		for (String[] addrs : queueAndAddrNames) {
			String queueName = addrs[0];
			String serverAddrName = addrs[1];
			String key = queueName + serverAddrName;
			
			if (log.isInfoEnabled()) {
				log.info("Begin init Data deal thread! queue name : " + queueName + " ; server name : " + serverAddrName);
			}
			
			int threadSize = NotifyCfg.getDealFileThreadSize(queueName);
			ExecutorService service = null;
			for (int i = 0; i < threadSize; i++) {
				if (NotifyUtility.getServerType().equals(NotifyUtility.SERVER_TYPE.LOG)) {
					service = Executors.newFixedThreadPool(threadSize);
					service.submit(getProxy((short)-1, queueName, serverAddrName));
				} else if (NotifyUtility.getServerType().equals(NotifyUtility.SERVER_TYPE.SERVER)) {
					service = Executors.newFixedThreadPool(threadSize * 2);
					service.submit(getProxy(NotifyUtility.CLIENT_TYPE.PRODUCER.getType(), queueName, serverAddrName));
					service.submit(getProxy(NotifyUtility.CLIENT_TYPE.CONSUMER.getType(), queueName, serverAddrName));
				}
			}
			if (service != null) {
				dealService.put(key, service);
			}
			
			if (NotifyServer.isHaServer()) {
				NotifyServer.setCanAcceptData(queueName, serverAddrName, true);
				NotifyServer.setCachePersist(queueName, serverAddrName, true);
			}
		}
	}
	
	public abstract FileProxy getProxy(short type, String queueName, String serverName);
}
