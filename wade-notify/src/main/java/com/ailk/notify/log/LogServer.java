/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.notify.common.NotifyCfg;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.NotifyUtility.SERVER_TYPE;

/**
 * @className:LogServer.java
 *
 * 采用UDP方式 接受客户端发送的日志文件，并根据日志中的偏移量生成到指定的日志索引中
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-2 
 */
public class LogServer {
		
	private static final transient Logger log = Logger.getLogger(LogServer.class);
	
	//TODO
	private static AtomicInteger count = new AtomicInteger(0);
	//TODO
	
	public static void openServer() throws IOException {
		int logPort = NotifyUtility.getServerPort();
		DatagramSocket ds = new DatagramSocket(logPort);
		log.fatal("LogServer is open! port :" + logPort);
		while (true) {
			byte[] datas = new byte[NotifyUtility.LOG_SERVER_LENGTH];
			DatagramPacket packet = new DatagramPacket(datas, datas.length);
			ds.receive(packet);
			
			byte[] data = packet.getData();
			ByteBuffer bufferData = ByteBuffer.allocate(data.length);
			bufferData.put(data);
			bufferData.flip();
			LogFileDealPool.addData(bufferData);
			
			//TODO
			log.fatal("count :" + count.incrementAndGet());
			//TODO
		}
	}
	
	public static void main(String[] args) {
		try {
			NotifyUtility.NEED_BACKUP_MASTER = false;
			
			// 标记当前为日志服务器
			NotifyUtility.setServerType(SERVER_TYPE.LOG);
			new LogFileDealPool().init();
			
			openServer();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
