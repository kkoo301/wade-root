/**
 * $
 */
package com.wade.svf.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.wade.svf.flow.node.Node;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FlowContext.java
 * @description: 流程调用上下文对象
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public final class FlowContext {
	/**
	 * 流程开始时间
	 */
	public static final String FLOW_START_TIME = "{flow.start.time}";
	
	/**
	 * 流程结束时间
	 */
	public static final String FLOW_END_TIME = "{flow.end.time}";
	
	/**
	 * 流程运行时间
	 */
	public static final String FLOW_COST_TIME = "{flow.cost.time}";
	
	/**
	 * 流程名称
	 */
	public static final String FLOW_NAME = "{flow.name}";
	
	/**
	 * 流程线程号
	 */
	public static final String FLOW_THREAD = "{flow.thread}";
	
	/**
	 * 流程会话标识
	 */
	public static final String FLOW_SESSION = "{flow.session}";
	
	
	/**
	 * 线程上下文对象
	 */
	private static ThreadLocal<FlowContext> context = new ThreadLocal<FlowContext>();
	
	/**
	 * 流程对象
	 */
	private IFlow<?,?> flow = null;
	
	/**
	 * 记录每个节点的的inparam数据，以便其它节点获取
	 */
	private Map<String, Map<String, Object>> response = new HashMap<String, Map<String, Object>>(100);
	
	/**
	 * 记录每个节点的outparam数据，并形成最终返回的结果集
	 */
	private Map<String, Map<String, Object>> request = new HashMap<String, Map<String, Object>>(100);
	
	/**
	 * 初始化参数
	 */
	private Map<String, Object> initParam = new HashMap<String, Object>(30);
	
	/**
	 * 记录流程的调用链
	 */
	private List<String> link = new ArrayList<String>(20);
	
	/**
	 * 流程开始时间
	 */
	private long startTime = System.currentTimeMillis();
	
	/**
	 * 会话ID
	 */
	private String sessionId = UUID.randomUUID().toString();
	
	/**
	 * 创建流程上下文对象
	 * @param flow
	 * @return
	 */
	public static FlowContext newContext(IFlow<?, ?> flow) {
		FlowContext fc = new FlowContext(flow);
		context.set(fc);
		return fc;
	}
	
	/**
	 * 获取流程上下文对象
	 * @return
	 */
	public static FlowContext getContext() {
		return context.get();
	}
	
	
	private FlowContext(IFlow<?,?> flow) {
		this.flow = flow;
	}
	
	
	/**
	 * 获取当前IFlow对象
	 * @return
	 */
	public IFlow<?,?> getFlow() {
		return this.flow;
	}
	
	/**
	 * 获取流程执行的开始时间
	 * @return
	 */
	public long getStartTime() {
		return startTime;
	}
	
	/**
	 * 获取流程执行的开始时间
	 * @return
	 */
	public long getCostTime() {
		return (System.currentTimeMillis() - startTime);
	}
	
	/**
	 * 获取流程的会话标识
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * 获取流程当前节点
	 * @return
	 */
	public Node<?, ?> getNode() {
		return getFlow().getNode(link.get(link.size() - 1));
	}
	
	
	/**
	 * 获取流程的线程
	 * @return
	 */
	public String getThread() {
		return String.valueOf(Thread.currentThread().getId());
	}
	
	/**
	 * 获取流程请求对象
	 * @return
	 */
	public Map<String, Map<String, Object>> getRequest() {
		return this.request;
	}
	
	public Map<String, Map<String, Object>> getResponse() {
		return this.response;
	}
	
	public Map<String, Object> getInitParam() {
		return this.initParam;
	}
	
	
	/**
	 * 获取指定节点的输入数据
	 * @param key
	 * @return
	 */
	public Map<String, Object> getInParam(String key) {
		Map<String, Object> res = this.request.get(key);
		
		if (null == res) {
			res = new HashMap<String, Object>(10);
			this.request.put(key, res);
		}
		
		return this.request.get(key);
	}
	
	/**
	 * 获取指定节点的输出数据
	 * @param key
	 * @return
	 */
	public Map<String, Object> getOutParam(String key) {
		Map<String, Object> res = this.response.get(key);
		
		if (null == res) {
			res = new HashMap<String, Object>(10);
			this.response.put(key, res);
		}
		
		return this.response.get(key);
	}
	
	/**
	 * 将Node添加到调用链
	 * @param nodeName
	 */
	public void addNode(String nodeName) {
		this.link.add(nodeName);
	}
	
	/**
	 * 获取流程节点的调用链
	 * @return
	 */
	public String getLink() {
		StringBuilder info = new StringBuilder(1000);
		Iterator<String> iter = link.iterator();
		while(iter.hasNext()) {
			info.append(iter.next());
			if (iter.hasNext()) {
				info.append("->");
			}
		}
		
		return info.toString();
	}
	
	/**
	 * 释放资源
	 */
	public void clear() {
		FlowContext fc = context.get();
		if (null != fc) {
			fc = null;
		}
		context.set(null);
	}
}
