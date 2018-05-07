/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月22日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.Node;

/**
 * @description
 * 节点执行过滤器
 */
public class NodeExecutorFilter implements INodeFilter {
	
	private static final Logger log = LoggerFactory.getLogger(NodeExecutorFilter.class);
	

	/**
	 * 节点过滤器链执行<br>
	 * 1.创建ServiceRequest对象<br>
	 * 2.初始化请求数据，将inparam的数据添加到请求里，如果有ref引用，则从上下文对旬里获取，否则直接使用配置数据<br>
	 * 3.触发节点的execute方法<br>
	 * 4.从返回的ServiceResponse对象里获取outparam的数据，并设置到上下文对象的Response里<br>
	 * @param context
	 * @param node
	 * @param chain
	 * @return
	 * @throws FlowException
	 */
	@Override
	public void doFilter(FlowContext context, Node<ServiceRequest, ServiceResponse> node, NodeFilterChain chain) throws FlowException {
		String flowName = context.getFlow().getName();
		String nodeName = node.getName();
		
		long start = System.currentTimeMillis();
		
		if (log.isDebugEnabled())
			log.debug("流程{}节点{}执行开始，流程上下文内容：{}", new String[] {flowName, nodeName, context.getInitParam().toString()});
		
		ServiceRequest request = node.createRequest();
		if (null == request) {
			throw new FlowException(FlowErr.flow10001.getCode(), FlowErr.flow10001.getInfo(flowName + "@" + nodeName + ",创建请求对象为空"));
		}
		
		// 执行流程节点前置处理，包括入参效验
		node.executeBefore(request);
		if (log.isDebugEnabled()) {
			log.debug("流程{}节点{}执行前参数合法性效验通过，请求内容：{}", new String[] {flowName, nodeName, request.toString()});
		}
		
		ServiceResponse response = executeNode(flowName, node, request);
		
		// 执行节点调用后置处理
		node.executeAfter(request, response);
		if (log.isDebugEnabled()) {
			long cost = System.currentTimeMillis() - start;
			
			log.debug("流程{}节点{}执行后参数合法性效验通过", new String[] {flowName, nodeName});
			log.debug("流程{}节点{}执行完成，耗时{}毫秒", new String[] {flowName, nodeName, String.valueOf(cost)});
		}
	}
	
	/**
	 * Node执行，并包装异常
	 * @param flow
	 * @param node
	 * @param serviceRequest
	 * @return
	 * @throws FlowException
	 */
	private ServiceResponse executeNode(String flow, Node<ServiceRequest, ServiceResponse> node, ServiceRequest serviceRequest) throws FlowException {
		ServiceResponse serviceResponse = null;
		
		try {
			serviceResponse = node.execute(serviceRequest);
		} catch (FlowException e) {
			throw e;
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10001.getCode(), FlowErr.flow10001.getInfo(flow + "@" + node.getName()), e);
		}
		
		return serviceResponse;
	} 

}
