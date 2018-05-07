/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.process;

import test.service.so.TestDmlService;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.process.BaseProcess;

/**
 * TODO
 * 
 * @className: TestDmlProcess.java
 * @author: liaosheng
 * @date: 2014-5-21
 */
public class TestDmlProcess extends BaseProcess {
	
	public static void main(String[] args) {
		System.setProperty("wade.server.name", "database-test");
		
		TestDmlProcess process = new TestDmlProcess();
		process.setGroup("quickstart");
		process.setName("");
		process.setTimeout(30);
		
		IData input = new DataMap();
		if (process.start(input)) {
			System.out.println(process.getOutput());
		};
	}

	
	/* (non-Javadoc)
	 * @see com.ailk.service.process.BaseProcess#run()
	 */
	@Override
	public void run() throws Exception {
		IData input = getInput();
		System.out.println(">>>输入参数:" + input);
		TestDmlService dml = new TestDmlService();
		dml.query();
	}
}
