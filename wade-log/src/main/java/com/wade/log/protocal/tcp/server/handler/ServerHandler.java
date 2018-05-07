package com.wade.log.protocal.tcp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.wade.log.Constants;
import com.wade.log.ILogData;
import com.wade.log.ILogHandler;
import com.wade.log.ILogServerListener;
import com.wade.log.db.DAOSession;
import com.wade.log.load.LogReadWriteFactory;
import com.wade.log.load.LogWriteHandler;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	
	private transient static final Logger log = Logger.getLogger(ServerHandler.class);
	private final ILogServerListener logServerListener;
	
	public ServerHandler(ILogServerListener listener){
		logServerListener = listener;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	//判断消息类型
    	if( !(msg instanceof ILogData) )
    		return;
    	
        ILogData logData = (ILogData)msg;
        
        //心跳包
        if( Constants.TYPE_PING.equals(logData.getType()) ){
        	return;
        }	
        
        ILogHandler handler = logServerListener.getHandler(logData.getType());
		if(handler != null){
			LogWriteHandler writer = LogReadWriteFactory.getLogWriteHandler(logServerListener, handler);
			if(writer != null){
				writer.write(logData);
			}else{
				try{
					handler.execute(logData);
					//提交事务
					DAOSession.commit();
				}catch(Exception hex){
					try {
						DAOSession.rollback();
					} catch (SQLException ex1) {
						log.error("数据库事务回滚失败", ex1);
					}
					log.error("LogHandler执行失败[" + handler.getClass().getName() + "]:", hex);
					throw hex;
				}finally{
					try {
						DAOSession.close();
					} catch (Exception ex2) {
						log.error("数据库连接销毁失败", ex2);
					}
				}
			}
		}
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
				case READER_IDLE:
					//ctx.writeAndFlush(new LogData("PONG"));
					//throw new Exception("客户端心跳间隔超时");
					//超过指定时间无数据交互，则关闭链路
					ctx.close();
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