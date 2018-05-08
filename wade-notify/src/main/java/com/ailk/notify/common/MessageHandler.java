/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import io.netty.channel.Channel;

/**
 * 用于客户端在接收到消息后，对消息进行处理
 * 
 * @className:MessageHandler.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-26 
 */
public interface MessageHandler {

	public void handler(Channel channel, Object msg);
}
