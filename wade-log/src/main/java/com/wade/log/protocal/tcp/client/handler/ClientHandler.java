package com.wade.log.protocal.tcp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.log4j.Logger;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	private transient static final Logger log = Logger.getLogger(ClientHandler.class);
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	//判断消息类型
    	/*    	
 		if( !(msg instanceof ILogData) )
    		return;
    	
        ILogData logData = (ILogData)msg;
        
        if( Constants.TYPE_PONG.equals(logData.getType()) ){
        	log.debug(">>>" + logData.getType());
        }
        */
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    /*
    @Override
   	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
				case WRITER_IDLE:
					//如果超过指定时间无数据发送，则发送心跳包
					ctx.writeAndFlush(new LogData(Constants.TYPE_PING));
					break;
				default:
					break;
			}
		}else{
			super.userEventTriggered(ctx, evt);
		}
    }
	*/
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error(cause.getMessage(), cause);
    }
	
}