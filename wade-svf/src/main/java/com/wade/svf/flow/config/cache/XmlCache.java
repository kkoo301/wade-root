/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月18日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.config.cache;

import java.util.HashMap;
import java.util.Map;

import com.wade.svf.flow.FlowConfigure;
import com.wade.svf.flow.config.reader.IFlowReader;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;

/**
 * @description
 * 配置缓存
 */
public final class XmlCache {
	
	// 单例对象
	private static XmlCache instance = new XmlCache();
	
	// 配置缓存
	private static Map<String, XmlItem> items = new HashMap<String, XmlItem>(200);
	
	// 配置解析锁，避免并发解析同一个配置文件
	private Object lock = new Object();
	
	// 读取xml文件，生成XmlItem对象
	private IFlowReader reader = FlowConfigure.getReader();
	
	private XmlCache() {
		
	}
	
	public static final XmlCache getInstance() {
		return instance;
	}
	
	/**
	 * 获取缓存里的XmlItem对象
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public XmlItem getItem(String name) throws FlowException {
		XmlItem item = items.get(name);
		if (null == item) {
			synchronized (lock) {
				item = items.get(name);
				if (null == item) {
					
					if (null == reader) {
						throw new FlowException(FlowErr.flow10000.getCode(), FlowErr.flow10000.getInfo("流程读取对象为空"));
					}
					
					item = reader.readXml(name);
					items.put(name, item);
				}
			}
		}
		
		// 处理配置解析异常的场景
		if (item instanceof EmptyXmlItem) {
			EmptyXmlItem empty = (EmptyXmlItem) item;
			throw new FlowException(FlowErr.flow10000.getCode(), FlowErr.flow10000.getInfo(empty.getError()), empty.getException());
		}
		
		return item;
	}
	
}
