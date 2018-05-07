/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.util.map;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 测试LinkedHashMap和HashMap在存取时的性能
 * 
 * @className: TestMap.java
 * @author: liaosheng
 * @date: 2014-4-8
 */
public class TestMap {
	
	public static void main(String[] args) throws ParseException {
		TestMap t = new TestMap();
		t.testSet();
		/*new Thread() {
			@Override
			public void run() {
				TestMap test = new TestMap();
				test.testLinkedHashMap();
				
			}
		}.start();
		
		new Thread() {
			@Override
			public void run() {
				TestMap test = new TestMap();
				test.testHashMap();
				
			}
		}.start();*/
	}
	
	
	public void testSet() {
		Map<String, String>	map = new LinkedHashMap<String, String>();
		map.put("1", "1");
		map.put("2", "1");
		map.put("3", "1");
		map.put("4", "1");
		String[] keys = map.keySet().toArray(new String[]{});
		System.out.println(keys.length);
	}
	
	
	public void testLinkedHashMap() {
		Map<String, String>	map = new LinkedHashMap<String, String>();
		
		long index = 0;
		long max = 10000L;
		while (true) {
			String obj = String.format("%d", index);
			map.get(obj);
			
			long cur = max - index;
			obj = String.format("%d", cur);
			map.put(obj, obj);
			
			if (cur == 0) {
				index = 0;
			} else {
				index ++;
			}
		}
	}
	
	public void testHashMap() {
		Map<String, String>	map = new HashMap<String, String>();
		
		long index = 0;
		long max = 10000L;
		while (true) {
			String obj = String.format("%d", index);
			map.get(obj);
			
			long cur = max - index;
			obj = String.format("%d", cur);
			map.put(obj, obj);
			
			if (cur == 0) {
				index = 0;
			} else {
				index ++;
			}
		}
	}

}
