/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;

import com.ailk.notify.common.MessageHandler;

/**
 * 处理服务端接收到的请求数据
 * 
 * @className:NotifyServerMessageHandler.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-26 
 */
public class NotifyServerMessageHandler implements MessageHandler {
	
	/**
	 *  
	 * @param channel
	 * @param msg 
	 * @see com.ailk.notify.common.MessageHandler#handler(io.netty.channel.Channel, java.lang.Object) 
	 */
	public void handler(Channel channel, Object msg) {
		ServerFileDealPool.putChannelMap(new ChannelMap((ByteBuffer) msg, channel));
	}

}
