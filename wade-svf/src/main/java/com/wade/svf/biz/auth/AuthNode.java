/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月28日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.auth;

import java.util.UUID;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.cache.redis.RedisFactory;
import com.ailk.cache.redis.client.RedisClient;
import com.wade.svf.biz.filter.AuthFlowFilter;
import com.wade.svf.biz.node.ServiceNode;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.server.http.RequestHead;

/**
 * @description
 * 认证服务
 */
public class AuthNode extends ServiceNode {

	/**
	 * 存放认证的Key和IP地址
	 */
	private static final RedisClient redis = RedisFactory.getRedisClient("sec");
	
	/**
	 * @param flow
	 * @param name
	 * @param type
	 * @param callback
	 * @param next
	 */
	public AuthNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_SERVICE_NAME, callback, next);
	}

	@Override
	public ServiceResponse execute(ServiceRequest request) throws Exception {
		FlowContext context = FlowContext.getContext();
		
		String uuid = UUID.randomUUID().toString();
		String ip = (String) context.getInitParam().get(RequestHead.ClientIp.getCode());
		
		if (!redis.set("flow-" + uuid, ip, 3600)) {
			throw new FlowException(FlowErr.flow10014.getCode(), FlowErr.flow10014.getInfo(getFlow().getName(), "认证服务器繁忙"));
		}
		
		ServiceResponse response = new ServiceResponse();
		response.setValue(AuthFlowFilter.FLOW_TICKET, uuid);
		return response;
	}
	
	@Override
	protected boolean isV5() {
		return true;
	}
	
}
