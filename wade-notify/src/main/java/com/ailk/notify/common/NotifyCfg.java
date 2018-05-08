/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @className:NotifyCfg.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-3 
 */
public class NotifyCfg {
	private static final transient Logger log = Logger.getLogger(NotifyCfg.class);
	
	private static final String NOTIFY_CONFIG_FILE = "notify.xml";
	private static final String QUEUE_NAME = "queue";
	private static final String ADDRESS_NAME = "address";
	private static final String QUEUE_THREAD_SIZE_NAME = "threadsize";
	private static final String SERVER_NAME = "server";
	private static final String DATA_CENTER_NAME = "datacenter";
	private static final String DEFAULT_DATA_CENTER_NAME = "default-datacenter";
	private static final String LOGSERVER_NAME = "logserver";
	private static final String ROOT_PATH_NAME = "root_path";
	
	// <主题名, <服务地址名, [服务主机地址, 服务备机地址, Ha端口]>> 
	// Ha服务部署在备机，当备机可用时 主机通过端口访问
	private static Map<String, Map<String, String[]>> notifyCfg = new HashMap<String, Map<String, String[]>>();
	private static Map<String, Integer> queueThreadSizes = new HashMap<String, Integer>();
	private static String logServerAddress = "";
	private static String fileRootPath = "";
	
	static {
		try {
			getRoot(NOTIFY_CONFIG_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取队列对应的并发处理文件的线程数 
	 * @param queueName
	 * @return
	 */
	public static int getDealFileThreadSize(String queueName) {
		return queueThreadSizes.get(queueName);
	}
	
	/**
	 * 获取当前配置的全部 queue和 serverAddrName的对应信息 
	 * 
	 * @return
	 */
	public static List<String[]> getAllQueueAndServerAddrNames() {
		List<String[]> datas = new LinkedList<String[]>();
		Set<String> queues = notifyCfg.keySet();
		for (String queue : queues) {
			Map<String, String[]> serverCfgs = notifyCfg.get(queue);
			Set<String> serverAddrs = serverCfgs.keySet();
			for (String serverAddr : serverAddrs) {
				datas.add(new String[]{queue, serverAddr});
			}
		}
		// 如果当前服务端调用该方法后没法返回数据, 则直接抛出异常
		if (datas.isEmpty()) {
			throw new RuntimeException("There is no data defined in notify.xml!");
		}
		return datas;
	}
	
	/**
	 * 根据ip，端口信息获取到对应的queue和serverAddrName信息 
	 * @param ip
	 * @param port
	 * @return
	 */
	public static List<String[]> getQueueAndServerAddrNameByAddr(String ip, int port) {
		String addr = ip + ":" + port;
		List<String[]> datas = new LinkedList<String[]>();
		Set<String> queues = notifyCfg.keySet();
		for (String queue : queues) {
			Map<String, String[]> serverCfgs = notifyCfg.get(queue);
			
			Set<String> serverAddrs = serverCfgs.keySet();
			for (String serverAddr : serverAddrs) {
				String [] addrs = serverCfgs.get(serverAddr);
				if (log.isDebugEnabled()) {
					log.debug("queueName : " + queue + "; serverName :" + serverAddr + "; local addr:" + addr + "; master :" + addrs[0] + "; slave :" + (addrs.length > 1 ? addrs[1] : null));
				}
				if (addr.equals(addrs[0]) || (addrs.length > 1 && addr.equals(addrs[1]))) {
					datas.add(new String[]{queue, serverAddr});
				}
			}
		}
		// 如果当前服务端调用该方法后没法返回数据, 则直接抛出异常
		if (datas.isEmpty()) {
			throw new RuntimeException("The server addr " + addr + " is not defined in notify.xml!");
		}
		return datas;
	}
	
	public static Set<String> getQueueNames() {
		return notifyCfg.keySet();
	}
	
	public static Set<String> getServerNames(String queueName) {
		Map<String, String[]> servers = notifyCfg.get(queueName);
		if (servers != null && !servers.isEmpty()) {
			return servers.keySet();
		}
		return null;
	}
	
	/**
	 * 供主机获取备机的HA服务地址，因为主机可供多个queue或server调用，所以返回的HA地址也会存在多个
	 * @return
	 */
	public static Map<String, String> getHaServerAddrs() {
		String serverIp = NotifyUtility.getLocalIp();
		int serverPort = NotifyUtility.getServerPort();
		if (log.isDebugEnabled()) {
			log.debug("Get ha server addrs, server ip :" + serverIp + "; server port : " + serverPort);
		}
		String addr = serverIp + ":" +  serverPort;
		Map<String, String> datas = new HashMap<String, String>();
		Set<String> queues = notifyCfg.keySet();
		for (String queue : queues) {
			Map<String, String[]> serverCfgs = notifyCfg.get(queue);
			Set<String> serverAddrs = serverCfgs.keySet();
			for (String serverAddr : serverAddrs) {
				String [] addrs = serverCfgs.get(serverAddr);
				if (addrs.length <= 1 || addrs[1] == null) {
					continue ;
				}
				if (addr.equals(addrs[0])) {
					String salveAddr = addrs[1];
					String haPort = addrs[2];
					String salveIp = NotifyUtility.splitHost(salveAddr)[0];
					datas.put(NotifyUtility.buildKey(queue, serverAddr), salveIp + ":" + haPort);
				}
			}
		}
		return datas;
	}
	
	
	public static String[] getAddrByQueueAndServer(String queueName, String serverName) {
		return notifyCfg.get(queueName).get(serverName);
	}
	
	public static String getLogServerAddr() {
		return logServerAddress;
	}
	
	public static String getRootPath() {
		return fileRootPath;
	}
	
	/**
	 * 获取配置文件中的数据
	 * @param root
	 */
	private static void loadCfg(Element root) {
		
		Element defaultDataCenterEle = root.element(DEFAULT_DATA_CENTER_NAME);
		String defaultDataCenter = null;
		if (defaultDataCenterEle != null) {
			defaultDataCenter = defaultDataCenterEle.getTextTrim();
		}
		
		if (StringUtils.isBlank(defaultDataCenter)) {
			// 采用默认数据中心
			
			String serverName = System.getProperty("wade.server.name");
			if (StringUtils.isBlank(serverName)) {
				throw new NullPointerException("生产模式下必须配置wade.server.name启动参数!");
			}
			
			@SuppressWarnings("unchecked")
			List<Element> servers = root.elements(SERVER_NAME);
			for (Element server : servers) {
				String serverNameCfg = server.attributeValue("name");
				if (serverName.equals(serverNameCfg)) {
					defaultDataCenter = server.attributeValue("connect");
					break;
				}
			}
			if (StringUtils.isBlank(defaultDataCenter)) {
				throw new RuntimeException("The Data Center is Not Configured For Server Name " + serverName);
			}
		}
		
		loadRootPath(root);
		loadLogServer(root);
		loadCenterCfg(root, defaultDataCenter);
	}

	private static void loadRootPath(Element root) {
		if (root != null) {
			fileRootPath = root.attributeValue(ROOT_PATH_NAME);
		}
	}
	
	private static void loadLogServer(Element root) {
		Element logEle = root.element(LOGSERVER_NAME);
		if (logEle != null) {
			logServerAddress = logEle.attributeValue("address");
		}
	}
	
	private static void loadCenterCfg(Element root, String dataCenterName) {
		@SuppressWarnings("unchecked")
		List<Element> centers = root.elements(DATA_CENTER_NAME);
		for (Element center : centers) {
			String centerName = center.attributeValue("name");
			if (dataCenterName.equals(centerName)) {
				loadQueueCfg(center);
				break;
			}
		}
	}

	private static void loadQueueCfg(Element dataCenter) {
		@SuppressWarnings("unchecked")
		List<Element> queues = dataCenter.elements(QUEUE_NAME);
		for (Element queue : queues) {
			String queueName = queue.attributeValue("name");
			if (StringUtils.isBlank(queueName)) {
				throw new RuntimeException("The Queue Name Defined in Nofity Config File can not be Empty!");
			}
			if (queueName.length() > NotifyUtility.getMaxQueueNameLength()) {
				throw new RuntimeException("The Size of Queue Name Defined in Nofity Config File Must less than " + NotifyUtility.getMaxQueueNameLength());
			}
			
			String threadSize = queue.attributeValue(QUEUE_THREAD_SIZE_NAME);
			if (StringUtils.isNotBlank(threadSize)) {
				queueThreadSizes.put(queueName, Integer.valueOf(threadSize));
			} else {
				queueThreadSizes.put(queueName, NotifyUtility.DEFAULT_SERVER_MAX_THREAD_SIZE);
			}
			
			Map<String, String[]> serversMap = new HashMap<String, String[]>();
			notifyCfg.put(queueName, serversMap);
			@SuppressWarnings("unchecked")
			List<Element> servers = queue.elements(ADDRESS_NAME);
			for (Element server : servers) {
				String serverName = server.attributeValue("name");
				
				if (StringUtils.isBlank(serverName)) {
					throw new RuntimeException("The Server Name in Queue " + queueName + " Defined in Nofity Config File can not be Empty!");
				}
				if (serverName.length() > NotifyUtility.getMaxServerNameLength()) {
					throw new RuntimeException("The Size of Server Name in Queue " + queueName + " Defined in Nofity Config File Must less than " + NotifyUtility.getMaxServerNameLength());
				}
				
				String serverMaster = server.attributeValue("master");
				String serverSalve = server.attributeValue("salve");
				String haPort = server.attributeValue("haport");
				serversMap.put(serverName, new String[] {serverMaster, serverSalve, haPort});
				if (log.isDebugEnabled()) {
					log.debug("queueName : " + queueName + "; serverName :" + serverName + "; master : " + serverMaster + "; salve : " + serverSalve + "; haport :" + haPort);
				}
			}
		}
	}
	
	
	private static void getRoot(String file) throws FileNotFoundException, DocumentException, IOException {
		InputStream in = null;
		try {
			in = NotifyCfg.class.getClassLoader().getResourceAsStream(file);
			if (in == null) {
				throw new FileNotFoundException(file);
			}
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			loadCfg(root);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (DocumentException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		/*Set<String> queueCfg = notifyCfg.keySet();
		for (String queueName : queueCfg) {
			System.out.println("==========================" + queueName + "================");
			Map<String, String[]> servers = notifyCfg.get(queueName);
			Set<String> serverNames = servers.keySet();
			for (String serverName : serverNames) {
				String[] serverData = servers.get(serverName);
				System.out.println(" server name : " + serverName + "; master = " + serverData[0] + "; salve = " + serverData[1]);
			}
		}*/
		Set<String> queues = getQueueNames();
		for (String queue : queues) {
			Set<String> servers = getServerNames(queue);
			for (String server : servers) {
				System.out.println(server);
			}
		}
	}
	
}
