package com.wade.log.protocal.udp.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.wade.log.ILogClient;
import com.wade.log.ILogData;
import com.wade.log.codec.JavaEncoder;

public class UDPClient implements ILogClient
{
	private InetAddress addr;
	private int port;
	private DatagramSocket client;
	
	public UDPClient(InetAddress addr, int port) throws Exception{
		this.addr = addr;
		this.port = port;
		this.client = new DatagramSocket();
	}
	
	@Override
	public void sendLog(ILogData logData) throws Exception {
		if(null == logData)
			return;
		
		byte[] bytes = JavaEncoder.encodeByLen(logData);
		
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr , port);
		
		client.send(packet);
	}
}