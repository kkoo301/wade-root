package com.wade.relax.esb.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;

import com.wade.relax.registry.SystemUtil;
import com.wade.relax.registry.Constants;
import com.wade.relax.registry.SystemRuntime;
import com.wade.zkclient.IZkClient;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 *
 * @desc: 服务透传网关
 * @auth: steven.chow
 * @date: 2016-11-24
 */
public class ServiceGateway {
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceGateway.class);
	private static final String CLUSTER_NAME = EsbXml.getClusterName();

	public static final String PATH = Constants.ZK_RELAX_ESB + "/cluster/" + CLUSTER_NAME + "/instances";
	public static boolean isOpenState = false;
	public static boolean isOfflineEnable = false;

	public void start(int port) throws Exception {
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup(SystemUtil.getWorkGroupSize());

		try {
			
			ServerBootstrap boot = new ServerBootstrap();

			boot.group(bossGroup, workGroup);
			boot.channel(NioServerSocketChannel.class);
			boot.handler(new LoggingHandler(LogLevel.INFO));
			boot.childHandler(new ServiceGatewayChannelInitializer());
			boot.option(ChannelOption.SO_BACKLOG, 2048);
			boot.option(ChannelOption.TCP_NODELAY, true);
			boot.childOption(ChannelOption.SO_KEEPALIVE, true);
			Channel channel = boot.bind(port).sync().channel();
			
			online();			
			isOpenState = true;
			
			String serverName = System.getProperty("wade.server.name");
			LOG.info("--------------------------------------------------------");
			LOG.info(serverName + " listen on port: " + port + ", start success!");
			LOG.info(String.format("%s: %d", SystemUtil.WADE_RELAX_SLEEP_INTERVAL, SystemUtil.getSleepInterval()));
			LOG.info(String.format("%s: %d", SystemUtil.ESB_GATEWAY_WORKGROUP_SIZE, SystemUtil.getWorkGroupSize()));
			LOG.info(String.format("%s: %d", SystemUtil.ESB_GATEWAY_MAXCONTENTLENGTH, SystemUtil.getMaxContentLength()));
			LOG.info("--------------------------------------------------------");
			
			channel.closeFuture().sync();
			
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
		
	}

	/**
	 * 通知注册中心ESB实例上线
	 */
	private static final void online() {
				
		IZkClient zkClient = SystemRuntime.getZkClient();
		
		String ip = System.getProperty(Constants.ESB_GATEWAY_IP, "");
		String port = System.getProperty(Constants.ESB_GATEWAY_PORT, "");
		String serverName = System.getProperty(Constants.SERVER_NAME, "");
		
		if (!zkClient.exists(PATH)) {
			zkClient.createPersistent(PATH, true);
		}
		
		String address = ip + ":" + port;
		String ephemeralPath = PATH + "/" + address;
		if (zkClient.exists(ephemeralPath)) {
			zkClient.delete(ephemeralPath);
		}
		zkClient.createEphemeral(ephemeralPath, "".getBytes());
		LOG.info("instance {} online, listening on {}", serverName, address);
		
		System.setProperty("isPrepared", DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
		
	}

	/**
	 * 检查ESB实例是否在线，如果不在线重新上线。
	 */
	public static final void checkOnline() {
		
		IZkClient zkClient = SystemRuntime.getZkClient();
		String ip = System.getProperty(Constants.ESB_GATEWAY_IP, "");
		String port = System.getProperty(Constants.ESB_GATEWAY_PORT, "");
		String serverName = System.getProperty(Constants.SERVER_NAME, "");
		
		String address = ip + ":" + port;
		String ephemeralPath = PATH + "/" + address;
		
		if (isOfflineEnable) {
			LOG.info("{}实例已被强制下线", serverName);
			return;
		}
		
		if (!zkClient.exists(ephemeralPath)) {
			zkClient.createEphemeral(ephemeralPath, "".getBytes());
			LOG.info("instance {} online again, listening on {}", serverName, address);
		}
		
	}
	
	/**
	 * 通知注册中心ESB实例下线 优雅下线
	 */
	public static final void offline() {
		
		IZkClient zkClient = SystemRuntime.getZkClient();
		
		String ip = System.getProperty(Constants.ESB_GATEWAY_IP, "");
		String port = System.getProperty(Constants.ESB_GATEWAY_PORT, "");
		String serverName = System.getProperty(Constants.SERVER_NAME, "");
						
		String address = ip + ":" + port;
		zkClient.delete(PATH + "/" + address);
		LOG.info("instance {} offline, listening on {}", serverName, address);
		isOfflineEnable = true;
	}
	
}
