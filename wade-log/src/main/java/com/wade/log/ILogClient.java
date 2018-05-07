package com.wade.log;

public interface ILogClient
{
	public void sendLog(ILogData logData) throws Exception;
}