package com.wade.log.test;

import com.wade.log.ILogData;
import com.wade.log.impl.AbstractLogHandler;

public class TestLogHandler extends AbstractLogHandler
{
	@Override
	public void execute(ILogData data) throws Exception {
		
		System.out.println(">>>>" + data.getType());
		System.out.println(">>>>" + data.getContent());
	}
	
}