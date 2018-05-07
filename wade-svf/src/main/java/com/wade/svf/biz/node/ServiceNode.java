/**
 * $
 */
package com.wade.svf.biz.node;

import java.util.Map;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.BaseException;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.service.client.ServiceFactory;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.IParamInspector;
import com.wade.svf.flow.node.NodeParam;
import com.wade.svf.flow.node.ParamInspectorFactory;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AbstractNode.java
 * @description: 服务节点对象，实现服务远程调用，实现服务输入、输出参数效验
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class ServiceNode extends AbstractNode {
	
	
	public ServiceNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String type, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_SERVICE_NAME, callback, next);
	}
	
	/**
	 * @param flow
	 * @param name
	 * @param callback
	 * @param next
	 */
	public ServiceNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		this(flow, name, IFlowConfig.CFG_SERVICE_NAME, callback, next);
	}
	
	
	/**
	 * 流程调用的前置处理<br>
	 * 
	 * 1、初始化请求头；<br>
	 * 2、处理流程配置输入参数里的变量，并效验参数；<br>
	 * 3、将请求数据添加到流程上下文；<br>
	 */
	@Override
	public void executeBefore(ServiceRequest request) throws FlowException {
		// 初始化请求上下文对象
		FlowContext context = FlowContext.getContext();
		request.getHead().putAll(context.getInitParam());
		request.getBody().putAll(context.getInitParam());
		
		// 处理流程配置输入参数里的变量，并效验参数类型
		IFlowConfig config = getFlow().getConfig();
		for (Map.Entry<String, NodeParam> item : config.getInParam(getName()).entrySet()) {
			String key = item.getKey();
			NodeParam param = item.getValue();
			
			// 内置变量替换
			boolean parsed = parseValue(param);
			
			// 参数效验
			String className = param.getInspector();
			IParamInspector inspect = ParamInspectorFactory.getInstance().get(className);
			if (null != inspect) {
				if (!inspect.inspect(this, param)) {
					throw new FlowException(FlowErr.flow10008.getCode(), FlowErr.flow10008.getInfo(getFlow().getName(), getName(), param.getInspectMessage()));
				}
			}
			
			// 将参数添加到服务请求里
			if (parsed)
				request.getBody().put(key, param.getValue());
		}
		
		// 将请求参数添加到流程上下文
		context.getInParam(getName()).putAll(request.getBody());
	}
	
	/**
	 * 根据服务版本设置输出参数
	 * @param response
	 */
	private void setContextOutparam(ServiceResponse response) {
		FlowContext context = FlowContext.getContext();
		String serviceName = getName();
		
		if (getFlow().getName().startsWith("test.")) {
			context.getOutParam(serviceName).putAll(response.getBody());
		} else {
			if (isV5()) {
				context.getOutParam(serviceName).putAll(response.getBody());
			} else {
				context.getOutParam(serviceName).put(_response, response.getData());
			}
		}
	}
	
	/**
	 * 服务调用后置处理<br>
	 * 
	 * 1、处理流程配置输入参数里的变量，并效验参数；<br>
	 * 2、将服务输出参数添加到流程上下文；<br>
	 */
	@Override
	public void executeAfter(ServiceRequest request, ServiceResponse response) throws FlowException {
		// 将响应参数添加到流程上下文
		FlowContext context = FlowContext.getContext();
		setContextOutparam(response);
		
		IFlowConfig config = getFlow().getConfig();
		for (Map.Entry<String, NodeParam> item : config.getOutParam(getName()).entrySet()) {
			String key = item.getKey();
			NodeParam param = item.getValue();
			
			// 内置变量替换
			boolean parsed = parseValue(param);
			
			// 效验出参是否有返回
			boolean checked = response.getBody().containsKey(key);
			if (!parsed && !checked) {
				throw new FlowException(FlowErr.flow10009.getCode(), FlowErr.flow10009.getInfo(getFlow().getName(), getName(), "未返回的参数名:" + key));
			}
			
			// 参数效验
			String className = param.getInspector();
			IParamInspector inspect = ParamInspectorFactory.getInstance().get(className);
			if (null != inspect) {
				if (!inspect.inspect(this, param)) {
					throw new FlowException(FlowErr.flow10009.getCode(), FlowErr.flow10009.getInfo(getFlow().getName(), getName(), param.getInspectMessage()));
				}
			}
			
			// 将节点输出配置参数添加到流程上下文
			if (parsed)
				context.getOutParam(getName()).put(key, param.getValue());
		}
		
	}
	
	/**
	 * 服务节点执行
	 */
	@Override
	public ServiceResponse execute(ServiceRequest request) throws Exception {
		ServiceResponse response = remoteCall(getName(), request);
		return response;
	}
	
	/**
	 * 远程调用服务
	 * @param serviceName
	 * @param request
	 * @return
	 * @throws FlowException
	 */
	protected ServiceResponse remoteCall(String serviceName, ServiceRequest request) throws FlowException {
		if (getFlow().getName().startsWith("test.")) {
			return testCall(serviceName, request);
		} else {
			if (isV5()) {
				return callV5(serviceName, request);
			} else {
				return callV4(serviceName, request);
			}
		}
	}
	
	/**
	 * 调用产商品服务
	 * @param serviceName
	 * @param request
	 * @return
	 * @throws FlowException
	 */
	private ServiceResponse callV5(String serviceName, ServiceRequest request) throws FlowException {
		try {
			return BizServiceFactory.call(serviceName, request, null, false, true);
		} catch (FlowException e) {
			throw e;
		} catch (BaseException e) {
			throw new FlowException(e.getCode(), e.getInfo(), e);
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10010.getCode(), FlowErr.flow10010.getInfo(getFlow().getName(), "未定义的错误编码"), e);
		}
	}
	
	/**
	 * 调用订单及其它中心的服务，需要将IDataOutput,IDataInput转换成ServiceResponse, ServiceRequest
	 * @param serviceName
	 * @param request
	 * @return
	 * @throws FlowException
	 */
	private ServiceResponse callV4(String serviceName, ServiceRequest request) throws FlowException {
		try {
			IDataInput input = new DataInput();
			input.getHead().putAll(request.getHead());
			input.getData().putAll(request.getData());
			
			IDataOutput output = ServiceFactory.call(serviceName, input);
			
			ServiceResponse response = new ServiceResponse();
			response.getHead().putAll(output.getHead());
			response.getData().addAll(output.getData());
			
			System.out.println(response.toString());
			
			return response;
		} catch (FlowException e) {
			throw e;
		} catch (BaseException e) {
			throw new FlowException(e.getCode(), e.getInfo(), e);
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10010.getCode(), FlowErr.flow10010.getInfo(getFlow().getName(), "未定义的错误编码"), e);
		}
	}
	
	
	/**
	 * 测试服务调用，需要有TestSVF.xml
	 * @param serviceName
	 * @param request
	 * @return
	 * @throws FlowException
	 */
	private ServiceResponse testCall(String serviceName, ServiceRequest request) throws FlowException {
		ServiceResponse response = new ServiceResponse();
		
		if (null == serviceName || serviceName.trim().length() == 0) {
			throw new FlowException(FlowErr.flow10008.getCode(), FlowErr.flow10008.getInfo(getFlow().getName(), serviceName, "服务名不能为空"));
		}
		
		if ("centerA.query.service".equals(serviceName)) {
			response.setValue("type", "2");
		}
		
		if ("centerB.update.service".equals(serviceName)) {
			response.setValue("result", "1");
		}
		
		
		return response;
	}
	
	/**
	 * 判断服务版本
	 * @return
	 */
	protected boolean isV5() {
		if (getName().startsWith("UPC.")) {
			return true;
		} else {
			return false;
		}
	}
}
