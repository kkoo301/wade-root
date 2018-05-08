/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import com.ailk.notify.common.FileUtility;

/**
 * 处理客户端发起的请求，并给出返回信息
 * 
 * @className:DealRequest.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-17 
 */
public abstract class DealRequest {
	
	protected ServerFileProxy fileProxy;
	protected FileUtility fileUtility;
	protected String queueName;
	protected String serverName;
	protected long fileName;
	
	public DealRequest(String queueName, String serverName, ServerFileProxy fileProxy) {
		this.queueName = queueName;
		this.serverName = serverName;
		this.fileProxy = fileProxy;
	}
	
	public abstract void execute(ChannelMap channelMap);
	
	@SuppressWarnings("static-access")
	public void initFileUtility() {
		this.fileName = this.fileProxy.createFileName(queueName, serverName);
		this.fileUtility = this.fileProxy.getFileUtility(queueName, serverName, fileName);
		this.fileUtility.setPrepareState(false);
	}
	
}
