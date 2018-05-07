/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.invoke;

import java.util.Map;

import test.service.invoke.so.TestServiceObject;

import com.ailk.service.session.app.AppInvoker;

/**
 * 模拟多线程并发的服务调用
 * 验证超时及数据库连接和事务
 * 
 * @className: TestInvoke.java
 * @author: liaosheng
 * @date: 2014-3-27
 */
public class TestInvoke extends Thread {
	private long id;
	
	public TestInvoke(long id) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			while(true) {
				Map<String, String> data = AppInvoker.invoke(null, new TestServiceObject(), "service", new Object[] {});
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		for (long i = 0; i < 20; i++) {
			new TestInvoke(1).start();
		}
	}

}
