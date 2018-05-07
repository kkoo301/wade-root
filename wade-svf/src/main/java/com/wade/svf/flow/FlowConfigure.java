/**
 * $
 */
package com.wade.svf.flow;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.svf.flow.config.reader.XmlFlowReader;
import com.ailk.common.config.GlobalCfg;
import com.wade.svf.flow.config.reader.IFlowReader;
import com.wade.svf.flow.executor.FlowExecutor;
import com.wade.svf.flow.executor.IFlowExecutor;
import com.wade.svf.flow.filter.FlowExecutorFliter;
import com.wade.svf.flow.filter.IFlowFilter;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FlowConfig.java
 * @description: 流程配置，每个配置文件对应一个实例
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public final class FlowConfigure {
	
	private static final Logger log = LoggerFactory.getLogger(FlowConfigure.class);
	
	/**
	 * 流程执行过滤器
	 */
	private static List<IFlowFilter> filters = new ArrayList<IFlowFilter>(5);
	
	
	/**
	 * 流程执行引擎
	 */
	private static IFlowExecutor executor = new FlowExecutor();
	
	/**
	 * 流程配置读取器
	 */
	private static IFlowReader reader = new XmlFlowReader();
	
	
	/**
	 * 获取流程执行引擎
	 * @return
	 */
	public static IFlowExecutor getExecutor() {
		return executor;
	}
	
	public static IFlowReader getReader() {
		return reader;
	}
	
	
	/**
	 * 获取配置的流程过滤器
	 * @return
	 */
	public static IFlowFilter[] getFilters() {
		return filters.toArray(new IFlowFilter[]{});
	}
	
	/**
	 * 添加过滤器
	 * @param filter
	 */
	public static void addFilter(IFlowFilter filter) {
		if (!filters.contains(filter)) {
			filters.add(0, filter);
		}
	}
	
	
	static {
		filters.add(new FlowExecutorFliter());
		
		String readerClass = GlobalCfg.getProperty("flow.config.reader", "");
		if (null != readerClass && readerClass.length() > 0) {
			try {
				IFlowReader fr = (IFlowReader) Class.forName(readerClass).newInstance();
				reader = fr;
			} catch (Exception e) {
				reader = null;
				log.error("实例化流程配置解析对象异常", e);
			}
		}
	}
}
