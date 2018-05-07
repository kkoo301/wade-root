package com.wade.log.protocal.udp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.wade.log.ILogData;
import com.wade.log.ILogHandler;
import com.wade.log.ILogServerListener;
import com.wade.log.codec.JavaDecoder;
import com.wade.log.db.DAOSession;
import com.wade.log.load.LogReadWriteFactory;
import com.wade.log.load.LogWriteHandler;

public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket>
{
	private static transient final Logger log = Logger.getLogger(UDPServerHandler.class);
	private final ILogServerListener logServerListener;
	
	public UDPServerHandler(ILogServerListener listener){
		logServerListener = listener;
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, DatagramPacket packet) throws Exception{
		ByteBuf buf = packet.content();
		
		if(buf.readableBytes() < 4)  //长度信息为4,小于4则直接返回
			return;
		
		buf.markReaderIndex(); //需要标记当前的readIndex的位置    
		
		//读取长度字节
		int dataLength = buf.readInt(); //读取长度信息   
	    if (dataLength < 0) { //数据长度小于0
	    	return;
	    }
		
		//读取到字节数组
		if(buf.readableBytes() < dataLength){
			log.error("数据长度信息：" + dataLength + " 超过数据包大小：" + buf.readableBytes() + ", 来自于:" + packet.sender().getHostName());
			return;
		}
		
		byte[] body = new byte[dataLength]; 
		//读取数据
		buf.readBytes(body);
		
		//解码
		Object obj = JavaDecoder.decode(body);
		if(null != obj){
			ILogData logData = (ILogData)obj;	
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
		}else{
			log.error("数据反序列化失败" + (body != null ? ":" + new String(body) : "") + ", 来自于:" + packet.sender().getHostName());
			return;
		}

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
		log.error("UDPServerHandler 执行异常", cause);
	}
}