package com.wade.log.start;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.wade.log.ILogHandler;
import com.wade.log.ILogServer;
import com.wade.log.ILogServerListener;
import com.wade.log.Protocal;
import com.wade.log.config.LogServerXml;
import com.wade.log.load.LogReadWriteFactory;
import com.wade.log.protocal.tcp.server.TCPServer;
import com.wade.log.protocal.udp.server.UDPServer;

/**
 * 日志服务启动类
 * @author Shieh
 *
 */
public class Main
{	
	private static transient final Logger log = Logger.getLogger(Main.class);
	private static ExecutorService executor = Executors.newFixedThreadPool(200);
	private static Map<ILogServerListener, ILogServer> servers = new HashMap<ILogServerListener, ILogServer>();
	
	/**
	 * 初始化监听组
	 * @param listener
	 * @throws Exception
	 */
	private static void initializeListener(final ILogServerListener listener) throws Exception{
		/**
		 * 加入定时任务
		 */
		for(Entry<String, ILogHandler> entry : listener.handlers().entrySet()){
			ILogHandler handler = entry.getValue();

			LogReadWriteFactory.createLogWriteHandler(listener, handler);
			
			LogReadWriteFactory.createAutoLoadJob(listener, handler);
		}
	}
	
	/**
	 * 启动监听
	 * @param listener
	 * @throws Exception
	 */
	private static void startListener(final ILogServerListener listener) throws Exception{
		Protocal protocal = listener.getProtocal();
		
		switch(protocal){
			case UDP:
				executor.execute(new Runnable(){
					@Override
					public void run() {
						ILogServer server = new UDPServer(listener);
						servers.put(listener, server);
						server.run();
					}				
				});
				log.info(protocal.getValue() + " server 0.0.0.0:" + listener.getPort() + " started!");
				break;
			case UDT:
				break;
			case TCP:
				executor.execute(new Runnable(){
					@Override
					public void run() {
						ILogServer server = new TCPServer(listener);
						servers.put(listener, server);
						server.run();
					}				
				});
				log.info(protocal.getValue() + " server 0.0.0.0:" + listener.getPort() + " started!");
				break;
		}

	}
	
	public static void main(String[] args) throws Exception{
		
		//初始化调度器
		LogReadWriteFactory.initSecheduler();
		
		//读取服务配置文件
		List<ILogServerListener> listeners = LogServerXml.getInstance().getLogServerListeners();
				
		Iterator<ILogServerListener> iter = listeners.iterator();	
		while(iter.hasNext()){
			final ILogServerListener listener = iter.next();
			
			//初始化监听组
			initializeListener(listener);	

			//启动监听
			startListener(listener);
	
		}
		
		//启动调度器
		LogReadWriteFactory.startSecheduler();
	}	
	
}