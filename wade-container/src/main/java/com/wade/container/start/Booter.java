package com.wade.container.start;

import com.wade.container.ajp.Ajp13SocketConnector;
import com.wade.container.server.AbstractConnector;
import com.wade.container.server.Server;
import com.wade.container.server.bio.SocketConnector;
import com.wade.container.server.nio.SelectChannelConnector;
import com.wade.container.server.ssl.SslSelectChannelConnector;
import com.wade.container.server.ssl.SslSocketConnector;
import com.wade.container.util.security.Password;
import com.wade.container.util.ssl.SslContextFactory;
import com.wade.container.util.thread.QueuedThreadPool;
import com.wade.container.webapp.WebAppContext;

public class Booter {

	public static void start() throws Exception {

		Server server = new Server();
		
		AbstractConnector connector = null;
		if (Config.NIO) {
			if(Config.SSL){
				SslContextFactory sslContextFactory =  new SslContextFactory();
				
				//设置SSL证书参数
				if( null != Config.SSL_KEYSTORE_PATH && !"".equals(Config.SSL_KEYSTORE_PATH.trim())){
					sslContextFactory.setKeyStorePath(Config.SSL_KEYSTORE_PATH);
					sslContextFactory.setKeyStorePassword(  Password.deobfuscate(Config.SSL_KEYSTORE_PASSWD) );
					
					//manager passwd
					if(Config.SSL_KEYMANAGER_PASSWD != null && !"".equals(Config.SSL_KEYMANAGER_PASSWD.trim())){
						sslContextFactory.setKeyManagerPassword( Password.deobfuscate(Config.SSL_KEYMANAGER_PASSWD) );
					}
				}else if(null != Config.SSL_TRUSTSTORE_PATH && !"".equals(Config.SSL_TRUSTSTORE_PATH.trim())){
					sslContextFactory.setTrustStore(Config.SSL_TRUSTSTORE_PATH);
					sslContextFactory.setTrustStorePassword( Password.deobfuscate(Config.SSL_TRUSTSTORE_PASSWD) );
				}
				
				SslSelectChannelConnector sscc = new SslSelectChannelConnector(sslContextFactory);
				sscc.setAcceptors(Config.ACCEPTORS);
				sscc.setAcceptQueueSize(Config.ACCEPT_QUEUESIZE);
				connector = (AbstractConnector) sscc;
			}else{
				SelectChannelConnector scc = new SelectChannelConnector();
				scc.setAcceptors(Config.ACCEPTORS);
				scc.setAcceptQueueSize(Config.ACCEPT_QUEUESIZE);
				connector = (AbstractConnector) scc;
			}
		} else {
			if(Config.AJP){
				Ajp13SocketConnector asc = new Ajp13SocketConnector();
				asc.setAcceptors(Config.ACCEPTORS);
				asc.setAcceptQueueSize(Config.ACCEPT_QUEUESIZE);
				connector = (AbstractConnector) asc;
			}else{
				if(Config.SSL){
					SslContextFactory sslContextFactory =  new SslContextFactory();
					
					//设置SSL证书参数
					if( null != Config.SSL_KEYSTORE_PATH && !"".equals(Config.SSL_KEYSTORE_PATH.trim())){
						sslContextFactory.setKeyStorePath(Config.SSL_KEYSTORE_PATH);
						sslContextFactory.setKeyStorePassword( Password.deobfuscate(Config.SSL_KEYSTORE_PASSWD) );
						//manager passwd
						if(Config.SSL_KEYMANAGER_PASSWD != null && !"".equals(Config.SSL_KEYMANAGER_PASSWD.trim())){
							sslContextFactory.setKeyManagerPassword( Password.deobfuscate(Config.SSL_KEYMANAGER_PASSWD) );
						}
					}else if(null != Config.SSL_TRUSTSTORE_PATH && !"".equals(Config.SSL_TRUSTSTORE_PATH.trim())){
						sslContextFactory.setTrustStore(Config.SSL_TRUSTSTORE_PATH);
						sslContextFactory.setTrustStorePassword( Password.deobfuscate(Config.SSL_TRUSTSTORE_PASSWD) );
					}
					
					SslSocketConnector ssc = new SslSocketConnector(sslContextFactory);
					ssc.setAcceptors(Config.ACCEPTORS);
					ssc.setAcceptQueueSize(Config.ACCEPT_QUEUESIZE);
					connector = (AbstractConnector) ssc;
				}else{
					SocketConnector sc = new SocketConnector();
					sc.setAcceptors(Config.ACCEPTORS);
					sc.setAcceptQueueSize(Config.ACCEPT_QUEUESIZE);
					connector = (AbstractConnector) sc;
				}
			}
		}
		
		
		//设置forwarded xiedx 2017/9/15
		if(Config.FORWARDED){
			connector.setForwarded(Config.FORWARDED);
			
			if(Config.FORWARDED_CIPHER_SUITE_HEADER != null && !"".equals(Config.FORWARDED_CIPHER_SUITE_HEADER)){
				connector.setForwardedCipherSuiteHeader(Config.FORWARDED_CIPHER_SUITE_HEADER);
			}
			
			if(Config.FORWARDED_FOR_HEADER != null && !"".equals(Config.FORWARDED_FOR_HEADER)){
				connector.setForwardedForHeader(Config.FORWARDED_FOR_HEADER);
			}
			
			if(Config.FORWARDED_HOST_HEADER != null && !"".equals(Config.FORWARDED_HOST_HEADER)){
				connector.setForwardedHostHeader(Config.FORWARDED_HOST_HEADER);
			}
			
			if(Config.FORWARDED_PROTO_HEADER != null && !"".equals(Config.FORWARDED_PROTO_HEADER)){
				connector.setForwardedProtoHeader(Config.FORWARDED_PROTO_HEADER);
			}
				
			if(Config.FORWARDED_SERVER_HEADER != null && !"".equals(Config.FORWARDED_SERVER_HEADER)){
				connector.setForwardedServerHeader(Config.FORWARDED_SERVER_HEADER);
			}
			
			if(Config.FORWARDED_SSL_SESSION_ID != null && !"".equals(Config.FORWARDED_SSL_SESSION_ID)){
				connector.setForwardedSslSessionIdHeader(Config.FORWARDED_SSL_SESSION_ID);
			}
		}
		
		connector.setPort(Config.PORT);
		connector.setMaxIdleTime(Config.MAX_IDLETIME);
		connector.setLowResourceMaxIdleTime(Config.LOW_RESOURCES_MAX_IDLETIME);
		connector.setStatsOn(false);
		
		//addConnector
		server.addConnector(connector);
				
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		WebAppContext context = new WebAppContext();
		context.setResourceBase(Config.RESOURCE_BASE);
		context.setDescriptor(Config.RESOURCE_BASE);
		context.setContextPath(Config.CONTEXT_PATH);
		if (Config.DEFAULTS_DESCRIPTOR != null && !"".equals(Config.DEFAULTS_DESCRIPTOR)) {
			context.setDefaultsDescriptor(Config.DEFAULTS_DESCRIPTOR);
		}
		context.setClassLoader(loader);
		
		//setHandler
		server.setHandler(context);

		QueuedThreadPool pool = new QueuedThreadPool();
		pool.setMinThreads(Config.MIN_THREADS);
		pool.setMaxThreads(Config.MAX_THREADS);
		pool.setDetailedDump(false);
		
		//setThreadPool
		server.setThreadPool(pool);
		
		server.setStopAtShutdown(true);
		server.setGracefulShutdown(1000);
		server.setSendServerVersion(false);
		server.setSendDateHeader(false);
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);

		server.start();
		server.join();
	}
}