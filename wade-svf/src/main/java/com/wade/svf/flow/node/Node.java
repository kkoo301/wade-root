/**
 * $
 */
package com.wade.svf.flow.node;

import java.util.Map;

import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.exception.FlowException;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: Node.java
 * @description: 流程节点接口
 * 
 * @version: 1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public interface Node<Req, Res> {
	
	/**
	 * 获取流程
	 * @return
	 */
	public IFlow<Req, Res> getFlow();
	
	/**
	 * 节点名称
	 * @return
	 */
	public String getName();
	
	/**
	 * 输入参数
	 * @return
	 */
	public Map<String, String> getInParams();
	
	/**
	 * 输出参数
	 * @return
	 */
	public Map<String, String> getOutParams();
	
	
	/**
	 * 处理节点执行前逻辑
	 * @param request
	 * @throws FlowException
	 */
	public void executeBefore(Req request) throws FlowException;
	
	/**
	 * 处理节点执行后逻辑
	 * @param request
	 * @param response
	 * @throws FlowException
	 */
	public void executeAfter(Req request, Res response) throws FlowException;
	
	/**
	 * 节点执行
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Res execute(Req request) throws Exception;
	
	/**
	 * 返回结果编码
	 * @return
	 */
	public int getResultCode();
	
	/**
	 * 设置结果编码
	 * @param resultCode
	 */
	public void setResultCode(int resultCode);

	/**
	 * 下一节点名称
	 * @return
	 */
	public Node<Req, Res> getNext();
	
	/**
	 * 回调节点名称
	 * @return
	 */
	public Node<Req, Res> getCallback();
	
	/**
	 * 节点类型
	 * @return
	 */
	public String getType();
	
	/**
	 * 根据上下文获取参数值
	 * @param param
	 * @return
	 * @throws FlowException
	 */
	public boolean parseValue(NodeParam param) throws FlowException;
	
	/**
	 * 创建请求对象
	 * @return
	 */
	public Req createRequest() throws FlowException;
	
}
