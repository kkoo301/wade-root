package com.wade.log.protocal.udt.client;

import java.net.InetAddress;

import com.wade.log.ILogClient;
import com.wade.log.ILogData;

public class UDTClient implements ILogClient
{
	
	public UDTClient(InetAddress addr, int port)
	{
		
	}
	
	@Override
	public void sendLog(ILogData logData) {
	
	}
}