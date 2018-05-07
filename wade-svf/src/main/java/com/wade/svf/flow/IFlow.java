/**
 * $
 */
package com.wade.svf.flow;

import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.node.Node;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IFlow.java
 * @description: 流程实例
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public interface IFlow<Req, Res> {
	
	/**
	 * 获取流程名称
	 * @return
	 */
	public String getName();
	
	/**
	 * 流程执行
	 * @param context
	 * @throws Exception
	 */
	public void execute(FlowContext context) throws Exception;
	
	/**
	 * 获取节点对象
	 * @param nodeName
	 * @return
	 */
	public Node<Req, Res> getNode(String nodeName);
	
	/**
	 * 获取流程配置对象
	 * @return
	 */
	public IFlowConfig getConfig();
	
}
