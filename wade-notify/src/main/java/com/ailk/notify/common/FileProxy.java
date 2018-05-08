/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ailk.notify.server.ServerFileProxy;

/**
 * @className:IFileProxy.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-6 
 */
public abstract class FileProxy implements Runnable{
	
	private static final transient Logger log = Logger.getLogger(FileProxy.class);

	protected static ConcurrentHashMap<String, FileUtility> fileUtilityMaps = new ConcurrentHashMap<String, FileUtility>();

	public static Set<String> getFileSet() {
		return fileUtilityMaps.keySet();
	}
	
	public static FileUtility getFileUtility(String key) {
		return fileUtilityMaps.get(key);
	}
	
	public static FileUtility getFileUtility(String queueName, String serverName, long fileName) {
		return getFileUtility(queueName, serverName, fileName, false);
	}
	
	public static FileUtility getFileUtility(String queueName, String serverName, long fileName, boolean addAvailFileName) {
		String key = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), String.valueOf(fileName));
		FileUtility utility = fileUtilityMaps.get(key);
		if (utility == null) {
			synchronized (fileUtilityMaps) {
				utility = fileUtilityMaps.get(key);
				if (utility == null) {
					if (!NotifyCfg.getQueueNames().contains(queueName)) {
						if (log.isInfoEnabled()) {
							log.info("The queue named " + queueName + " is undefined in notify.xml!");
						}
					}
					if (!NotifyCfg.getServerNames(queueName).contains(serverName)) {
						log.info("The server named " + serverName + " in queue " + queueName + " is undefined in notify.xml!");
					}
					utility = new FileUtility(queueName, serverName, fileName, "rw");
					fileUtilityMaps.put(key, utility);
					if (addAvailFileName) {
						ServerFileProxy.addAvailFileName(queueName, serverName, fileName);
					}
				}
			}
		}
		return utility;
	}

}
