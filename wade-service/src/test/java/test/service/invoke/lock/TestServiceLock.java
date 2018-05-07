/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.invoke.lock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 测试服务锁对象，该对象必须自己管理所有的锁的Key，以便释放
 * 
 * @className: TestServiceLock.java
 * @author: liaosheng
 * @date: 2014-5-28
 */
public class TestServiceLock {
	/**
	 * 如果对锁有顺序则使用LinkedHashMap，会根据put的顺序来遍历，但性能略差于HashMap
	 */
	private Map<String, String> lockKeys = new HashMap<String, String>(10);
	
	/**
	 * 
	 * @param lockKey
	 */
	public boolean lock(String lockKey) {
		System.out.println("lock key :" + lockKey);
		
		//use GlobalLock.lock()
		
		lockKeys.put(lockKey, lockKey);
		return true;
	}
	
	public boolean unlock(String lockKey) {
		System.out.println("unlock key :" + lockKey);
		
		//use GlobalLock.unlock()
		return false;
	}
	
	public void clean() {
		Iterator<String> iter = lockKeys.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			System.out.println("clean lock key & unlock it :" + key);
			
			//use GlobalLock.unlock();
		}
	}
}
