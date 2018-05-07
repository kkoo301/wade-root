/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.invoke;

import test.service.invoke.so.TestServiceObject;

import com.ailk.common.BaseException;
import com.ailk.service.session.app.AppInvoker;

/**
 * TODO
 * 
 * @className: TestInvokeService.java
 * @author: liaosheng
 * @date: 2014-3-28
 */
public class TestInvokeService {
	
	public static void main(String[] args) throws Exception {
		TestInvokeService tis = new TestInvokeService();
		tis.test1();
	}
	
	
	/**
	 * 验证Exception
	 */
	public void test1() {
		System.setProperty("wade.server.name", "database-test");
		
		try {
			AppInvoker.invoke(null, new TestServiceObject(), "query", new Object[] {"2"});
		} catch (BaseException e) {
			System.out.println(">>>>>>>>>>>>>>BaseException" + e.getData());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>OtherException");
			e.printStackTrace();
		}
	}

	public void test2() {
		System.setProperty("wade.server.name", "database-test");
		
		try {
			AppInvoker.invoke(null, new TestServiceObject(), "query", new Object[] {"1"});
			for (int i = 0; i < 10; i++) {
				new Thread() {
					public void run() {
						while (true) {
							try {
								Thread.sleep(5000);
								AppInvoker.invoke(null, new TestServiceObject(), "query", new Object[] {"2"});
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
					};
				}.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//AppInvoker.shutdown();
		}
	}
}
