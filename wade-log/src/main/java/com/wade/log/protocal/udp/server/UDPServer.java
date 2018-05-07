package com.wade.log.protocal.udp.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.apache.log4j.Logger;

import com.wade.log.ILogServer;
import com.wade.log.ILogServerListener;
import com.wade.log.protocal.udp.server.handler.UDPServerHandler;

public class UDPServer implements ILogServer{

	private static transient final Logger log = Logger.getLogger(UDPServer.class);
	
	final ILogServerListener serverListener;
	
	public UDPServer(ILogServerListener listener){
		serverListener = listener;
	}
	
	public void run() {	
		EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final Bootstrap boot = new Bootstrap();
            boot.group(workerGroup)
                    .channel(NioDatagramChannel.class)
                    //.option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(final NioDatagramChannel ch) throws Exception {
                        	ChannelPipeline p = ch.pipeline(); 
                            p.addLast(
                                    new UDPServerHandler(serverListener));
                        }
                    });
 
           final ChannelFuture future = boot.bind(serverListener.getPort()).sync();
           future.channel().closeFuture().await();         
        }catch(Exception ex){
        	log.error(ex);
		} finally {
            workerGroup.shutdownGracefully();
        }	
	}
}