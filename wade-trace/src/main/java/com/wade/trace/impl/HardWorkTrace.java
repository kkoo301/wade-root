package com.wade.trace.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import com.ailk.common.data.IData;
import com.ailk.mq.util.KafkaUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.trace.ITrace;
import com.wade.trace.logsystem.LogKeys;
import com.wade.trace.logsystem.LogSystemUtil;
import com.wade.trace.probe.IProbe;
import com.wade.trace.probe.impl.AppProbe;
import com.wade.trace.probe.impl.BrowserProbe;
import com.wade.trace.probe.impl.DaoProbe;
import com.wade.trace.probe.impl.ServiceProbe;
import com.wade.trace.probe.impl.WebProbe;
import com.wade.trace.probe.impl.EcsProbe;
import com.wade.trace.probe.impl.IbsProbe;
import com.wade.trace.probe.impl.PfProbe;
import com.wade.trace.probe.impl.UipProbe;
import com.wade.trace.util.DynaBindUtil;
import com.wade.trace.util.IOUtil;
import com.wade.trace.util.SampleUtil;
import com.wade.trace.util.SystemUtil;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HardWorkTrace
 * @description: 老实工作型
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class HardWorkTrace implements ITrace {
	
	private static Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
	
	/**
	 * 放置于线程上下文中的探针堆栈
	 */
	private static final ThreadLocal<Stack<IProbe>> PROBE_STACK = new ThreadLocal<Stack<IProbe>>();
	
	/**
	 * 清理线程上下文中的探针堆栈，创建一个新的探针堆栈。
	 * 
	 * @return
	 */
	private static final Stack<IProbe> clearAndGetProbeStack() {
		
		PROBE_STACK.remove(); // 避免内存泄漏
		
		Stack<IProbe> stack = new Stack<IProbe>();
		PROBE_STACK.set(stack);
		
		return stack;
		
	}
	
	/**
	 * 采集异常日志信息
	 */
	@Override
	public void collectException(String staffId, String serviceName, String errinfo) {
		Map<String, Object> errInfo = new HashMap<String, Object>();
		errInfo.put(LogKeys.OPER_ID, staffId);
		errInfo.put(LogKeys.ERR_TIME, String.valueOf(System.currentTimeMillis()));
		errInfo.put(LogKeys.ERR_INFO, errinfo);
		errInfo.put(LogKeys.SERVER_NAME, SystemUtil.getServerName());
		errInfo.put(LogKeys.SERVICE_NAME, serviceName);
		
    	byte[] payload = IOUtil.encode(errInfo);
		producer.send(new KeyedMessage<byte[], byte[]>(LogKeys.TOPIC_ERROR, staffId.getBytes(), payload));
	}
	
	/**
	 * 记录浏览器探针日志
	 */
	@Override
	public void logBrowserProbe(String id, String traceid, String statuscode, String starttime, String endtime, String ieVer) {
		
		BrowserProbe probe = new BrowserProbe();
		
		probe.setId(id);
		probe.setParentId("root");
		probe.setTraceId(traceid);
		probe.setStatuscode(statuscode);
		probe.setStarttime(starttime);
		probe.setEndtime(endtime);
		probe.setIeVer(ieVer);
		probe.logging();
		
	}
	
	/**
	 * 开启一个Web探针
	 * 
	 * @param bizId
	 * @param operId
	 * @param sessionId
	 * @param clientIp
	 * @param url
	 * @param menuId
	 * @throws Exception 
	 */
	@Override
	public void startWebProbe(String bizId, String operId, String sessionId, String clientIp, String url, String menuId, IData param) {
		
		/** 根据 菜单ID 判断是否采样 */
		if (!SampleUtil.isWebSample(menuId)) {
			return;
		}
		
		Stack<IProbe> stack = clearAndGetProbeStack();
		
		WebProbe probe = new WebProbe();
		
		probe.setTraceId(SystemUtil.createWebTraceId());
		probe.setParentId(SystemUtil.uuid()); // WEB层生成父探针ID，然后通过 getBrowserId() 回传给浏览器端。
		probe.setBizId(bizId);
		probe.setOperId(operId);
		
		probe.setSessionId(sessionId);
		probe.setClientIp(clientIp);
		probe.setUrl(url);
		probe.setMenuid(menuId);
		probe.setExt(DynaBindUtil.webBinding(menuId, param));
		
		probe.start();
		
		stack.push(probe);
		
	}
	
	/**
	 * 关闭一个Web探针
	 */
	@Override
	public void stopWebProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return; // 不采样
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}
		
	/**
	 * 开启一个App探针
	 * 
	 * @param traceId
	 * @param parentId
	 * @param bizId
	 * @param operId
	 * @param mainServiceName
	 * @throws Exception 
	 */
	@Override
	public void startAppProbe(String traceId, String parentId, String bizId, String operId, String mainServiceName, IData param) {
		
		if (!SystemUtil.isValidTraceId(traceId)) {
			return;
		}
				
		Stack<IProbe> stack = clearAndGetProbeStack();
			
		AppProbe probe = new AppProbe();
			
		probe.setTraceId(traceId);
		probe.setParentId(parentId);
		probe.setBizId(bizId);
		probe.setOperId(operId);
		probe.setExt(DynaBindUtil.appBinding(mainServiceName, param));
		probe.start();
			
		stack.push(probe);
		
	}
	
	/**
	 * 关闭一个App探针
	 */
	@Override
	public void stopAppProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return; // 不采样
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}
	
	/**
	 * 开启一个主服务探针
	 * 
	 * @param traceId
	 * @param operId
	 * @param serviceName
	 */
	@Override
	public void startMainServiceProbe(String traceId, String parentId, String bizId, String operId, String serviceName) {
		
		if (!SystemUtil.isValidTraceId(traceId)) {
			return;
		}
		
		// 注: 主服务是在新线程里
		Stack<IProbe> stack = clearAndGetProbeStack();
		
		ServiceProbe probe = new ServiceProbe();
		
		probe.setServiceName(serviceName);
		probe.setTraceId(traceId);
		probe.setParentId(parentId);
		probe.setBizId(bizId);
		probe.setOperId(operId);
		probe.setMainService(true);
		probe.start();
		
		stack.push(probe);
		
	}
	
	/**
	 * 开启一个子服务探针
	 * 
	 * @param serviceName
	 */
	@Override
	public void startSubServiceProbe(String serviceName) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return;
		}
		
		String traceId = null;
		String parentId = null;
		String bizId = null;
		String operId = null;
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe parentProbe = stack.peek();
			traceId = parentProbe.getTraceId();
			parentId = parentProbe.getId();
			bizId = parentProbe.getBizId();
			operId = parentProbe.getOperId();
		}
		
		ServiceProbe probe = new ServiceProbe();
		probe.setServiceName(serviceName);
		probe.setTraceId(traceId);
		probe.setParentId(parentId);
		probe.setBizId(bizId);
		probe.setOperId(operId);
		probe.start();
		
		stack.push(probe);
		
	}
	
	/**
	 * 关闭一个服务探针
	 */
	@Override
	public void stopServiceProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		if (null == stack) {
			return;
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}
	
	/**
	 * 开启一个Dao探针
	 * 
	 * @param dataSource
	 * @param sqlName
	 * @param sql
	 * @param param
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void startDaoProbe(String dataSource, long dccost, String sqlName, String sql, IData param) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return;
		}
		
		String traceId = null;
		String parentId = null;
		String bizId = null;
		String operId = null;
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe parentProbe = stack.peek();
			traceId = parentProbe.getTraceId();
			parentId = parentProbe.getId();
			bizId = parentProbe.getBizId();
			operId = parentProbe.getOperId();
		}
		
		DaoProbe probe = new DaoProbe();
		probe.setDataSource(dataSource);
		probe.setDccost(dccost);
		probe.setTraceId(traceId);
		probe.setParentId(parentId);
		probe.setBizId(bizId);
		probe.setOperId(operId);
		probe.setSqlName(sqlName);
		probe.setSql(sql);
		probe.setParams((HashMap<String, Object>)param);
		probe.start();
		
		stack.push(probe);
	}
	
	/**
	 * 关闭一个Dao探针
	 */
	@Override
	public void stopDaoProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		if (null == stack) {
			return;
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}

	@Override
	public void startEcsProbe(String serviceName, String operId) {
		
		/** 根据 调用的服务名 判断是否采样 */
		if (!SampleUtil.isEcsSample(serviceName)) {
			return;
		}
		
		Stack<IProbe> stack = clearAndGetProbeStack();
		
		EcsProbe probe = new EcsProbe();
		probe.setServiceName(serviceName);
		probe.setTraceId(SystemUtil.createEcsTraceId());
		probe.setParentId("root");
		probe.setOperId(operId);
		
		probe.start();
		
		stack.push(probe);
		
	}

	@Override
	public void stopEcsProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return; // 不采样
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}

	@Override
	public void startIbsProbe(String serviceName, String operId) {
		
		/** 判断是否采样 */
		if (!SampleUtil.isIbsSample(serviceName)) {
			return;
		}
		
		Stack<IProbe> stack = clearAndGetProbeStack();
		
		IbsProbe probe = new IbsProbe();
		probe.setServiceName(serviceName);
		probe.setTraceId(SystemUtil.createIbsTraceId());
		probe.setParentId("root"); // 你是根
		probe.setOperId(operId);
		
		probe.start();
		
		stack.push(probe);
	
	}

	@Override
	public void stopIbsProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return; // 不采样
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}

	@Override
	public void startPfProbe(String serviceName, String traceId, String parentId, String operId) {
		
		Stack<IProbe> stack = clearAndGetProbeStack();
		
		PfProbe probe = new PfProbe();
		probe.setServiceName(serviceName);
		probe.setTraceId(traceId);
		probe.setParentId(parentId);
		probe.setOperId(operId);
		probe.start();
		
		stack.push(probe);
		
	}

	@Override
	public void stopPfProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return; // 不采样
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}

	@Override
	public void startUipProbe(String serviceName, String traceId, String parentId, String operId) {
		
		String _traceId = null;
		String _parentId = null;
		
		if (StringUtils.isNotBlank(traceId)) { // UIP不是源头
			_traceId = traceId;
			_parentId = parentId;
		} else { // UIP是源头
			if (!SampleUtil.isUipSample(serviceName)) {
				return;
			}
			_traceId = SystemUtil.createUipTraceId();
			_parentId = "root";
		}
		
		Stack<IProbe> stack = clearAndGetProbeStack();
		
		UipProbe probe = new UipProbe();
		probe.setTraceId(_traceId);
		probe.setParentId(_parentId);
		probe.setServiceName(serviceName);
		probe.setOperId(operId);
		probe.start();
		
		stack.push(probe);
		
	}

	@Override
	public void stopUipProbe(boolean success) {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return; // 不采样
		}
		
		if (stack.isEmpty()) {
			return;
		} else {
			IProbe probe = stack.pop();
			probe.stop(success);
			probe.logging();
		}
		
	}

	@Override
	public String getTraceId() {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return "";
		}
		
		if (stack.isEmpty()) {
			return "";
		} else {
			IProbe probe = stack.peek();
			return probe.getTraceId();
		}

	}
	
	@Override
	public String getId() {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return "";
		}
		
		if (stack.isEmpty()) {
			return "";
		} else {
			IProbe probe = stack.peek();
			return probe.getId();
		}
		
	}
	
	@Override
	public String getBrowserId() {
		
		Stack<IProbe> stack = (Stack<IProbe>) PROBE_STACK.get();
		
		if (null == stack) {
			return "";
		}
		
		if (stack.isEmpty()) {
			return "";
		} else {
			IProbe probe = stack.peek();
			return probe.getParentId();
		}
	}
	
	@Override
	public void menuClick(String timestamp, String staffId, String menuId) {
		Map<String, Object> loginfo = new HashMap<String, Object>();
		loginfo.put("probetype", "menuclick");
		loginfo.put("timestamp", timestamp);
		loginfo.put("operid", staffId);
		loginfo.put("menuid", menuId);
		LogSystemUtil.send(loginfo);
	}

}
