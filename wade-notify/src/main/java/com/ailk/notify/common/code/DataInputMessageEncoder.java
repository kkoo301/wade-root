/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.ailk.common.data.IDataInput;
import com.ailk.notify.common.NotifyUtility;

/**
 * @className:DataInputMessageEncoder.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-26 
 */
public class DataInputMessageEncoder extends MessageToByteEncoder<IDataInput> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IDataInput msg, ByteBuf out)
			throws Exception {
		byte[] datas = NotifyUtility.encodeHessian(msg);
		int length = datas.length;
		out.writeInt(length);
		out.writeBytes(datas);
	}

}
