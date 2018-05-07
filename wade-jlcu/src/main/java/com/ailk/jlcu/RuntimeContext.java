/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com 
 * http://www.wadecn.com
 */
package com.ailk.jlcu;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 流程引擎运行时上下文
 * 
 * @author steven zhou
 * @since 1.0
 */
public class RuntimeContext {
	
	private static final Logger log = Logger.getLogger(RuntimeContext.class);
	
	/** 上下文实例 */
	private static final RuntimeContext rtctx = new RuntimeContext();

	/** 用于缓存流程图集合 */
	private static Map cache;

	private RuntimeContext() {
		try {
			cache = new HashMap();
		} catch (Exception e) {
			cache = null;
			log.error(e);
		}
	}

	public static RuntimeContext getIntance() {
		return rtctx;
	}

	/**
	 * 根据逻辑服务名获取流程图对象 <BR>
	 * 
	 * 生产模式: Cache->DB 开发模式: Cache->File
	 * 
	 * @param xTransCode
	 * @return
	 * @throws Exception
	 */
	public FlowChart getFlowChart(String xTransCode, String xTransPath) throws Exception {
		
		FlowChart flowChart = null;
		if (null == cache) {
			flowChart = new FlowChart(xTransCode, xTransPath);
		} else {
			Object element = cache.get(xTransCode);
			if (null == element) {
				flowChart = new FlowChart(xTransCode,xTransPath);
				if (null != flowChart) {
					cache.put(xTransCode, flowChart);
				}
			} else {
				flowChart = (FlowChart) element;
			}
		}

		return flowChart;
	}

}