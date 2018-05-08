/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.log;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ailk.notify.common.FileDealPool;
import com.ailk.notify.common.FileProxy;

/**
 * @className:LogFileDealPool.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-4 
 */
public class LogFileDealPool extends FileDealPool {

	private static final transient Logger log = Logger.getLogger(LogFileDealPool.class);

	private static BlockingQueue<ByteBuffer> datas = new LinkedBlockingQueue<ByteBuffer>();
	
	public static void addData(ByteBuffer data) {
		datas.add(data);
	}
	
	public static ByteBuffer getData() {
		try {
			return datas.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}

	@Override
	public FileProxy getProxy(short type, String queueName, String serverName) {
		return new LogFileProxy();
	}
}
