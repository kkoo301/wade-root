package com.wade.log;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wade.log.protocal.tcp.client.TCPClient;
import com.wade.log.protocal.udp.client.UDPClient;
import com.wade.log.protocal.udt.client.UDTClient;

public class LogFactory
{
	private static Map<String, ILogClient> clientMap = new ConcurrentHashMap<String, ILogClient>();
	
	/**
	 * 发送日志
	 * @param addr
	 * @param port
	 * @param protocal
	 * @param logData
	 */
	public static void sendLog(InetAddress addr, int port, Protocal protocal, ILogData logData) throws Exception{
		ILogClient client = getClient(addr, port, protocal);
		if(client != null){
			client.sendLog(logData);
		}
	}
	
	private static ILogClient getClient(InetAddress addr, int port, Protocal protocal) throws Exception{
		if(addr == null || port < 0 || protocal == null)
			return null;
		
		String key = addr.getHostName() + "_" + port + "_" + protocal;
		ILogClient client = clientMap.get(key);	
		if(client == null){
			synchronized(LogFactory.class){
				switch(protocal){
					case UDP:
						client = new UDPClient(addr, port);
						break;
					case UDT:
						client = new UDTClient(addr, port);
						break;
					case TCP:
						client = new TCPClient(addr, port);
						break;
				}
				clientMap.put(key, client);
			}
		}
		
		return client;
	}
}