/**
 * $
 */
package com.wade.svf.flow.executor;

import java.util.Map;

import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.exception.FlowException;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IFlowExecutor.java
 * @description: 流程执行接口定义
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public interface IFlowExecutor {
	
	/**
	 * 执行流程对象
	 * @throws FlowException
	 */
	public Map<String, Object> execute(IFlow<?, ?> name, Map<String, Object> request) throws FlowException;

}
