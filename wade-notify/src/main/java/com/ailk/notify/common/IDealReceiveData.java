/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

/**
 * @className:IDealReceiveData.java
 *	
 * 用于处理接收到的BufferData信息
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-4 
 */
public interface IDealReceiveData {

	/**
	 * 处理从客户端获取的消息
	 * 
	 * @param data
	 */
	public void execute(BlockingQueue<ByteBuffer> data);
	
}
