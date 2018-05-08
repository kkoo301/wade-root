/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.log;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ailk.notify.common.FileProxy;
import com.ailk.notify.common.FileUtility;
import com.ailk.notify.common.NotifyUtility;

/**
 * 对客户端发送的日志信息进行处理
 * 
 * @className:LogFileProxy.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-6 
 */
public class LogFileProxy extends FileProxy {
	private static final transient Logger log = Logger.getLogger(LogFileProxy.class);

	/**
	 * 目前仅处理服务端发送的日志消息
	 * 
	 * @return
	 * @throws Exception 
	 */
	public void run() {
		while (true) {
			try {
				ByteBuffer data = LogFileDealPool.getData();
				if (data == null) {
					continue;
				}
				
				byte type = data.get();
				byte[] queueBytes = new byte[NotifyUtility.getMaxQueueNameLength()];
				data.get(queueBytes);
				String queueName = NotifyUtility.transferByteArrayToStr(queueBytes);
				byte[] serverAddrBytes = new byte[NotifyUtility.getMaxServerNameLength()];
				data.get(serverAddrBytes);
				String serverAddrName = NotifyUtility.transferByteArrayToStr(serverAddrBytes);
				long fileName = data.getLong();
				int indexOffset = data.getInt();
				FileUtility utility = getFileUtility(queueName, serverAddrName, fileName);

				/*//TODO
				log.fatal("data.limit :" + data.limit() + "; data.pos :" + data.position() + "; queueName ：" + queueName + 
						"; serverName :" + serverAddrName + " ; fileName :" + fileName + " ; indexOffset :" + indexOffset);
				//TODO
*/				
				utility.writeLog(data, indexOffset, type);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}
	
}
