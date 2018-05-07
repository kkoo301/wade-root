/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月28日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.filter;

import com.ailk.cache.redis.RedisFactory;
import com.ailk.cache.redis.client.RedisClient;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.filter.FlowFilterChain;
import com.wade.svf.flow.filter.IFlowFilter;
import com.wade.svf.server.http.RequestHead;

/**
 * @description
 * 认证验证过滤器，需要过滤白名单流程FlowAuthSVF
 */
public class AuthFlowFilter implements IFlowFilter {

	private static final String FLOW_WHITE_LIST = "FlowAuthSVF";
	
	public static final String FLOW_TICKET = "ticket";
	
	/**
	 * 存放认证的Key和IP地址
	 */
	private static final RedisClient redis = RedisFactory.getRedisClient("sec");
	
	@Override
	public void doFilter(FlowContext context, FlowFilterChain chain) throws FlowException {
		
		String flowName = context.getFlow().getName();
		if (!FLOW_WHITE_LIST.equals(flowName)) {
			Object value = context.getInitParam().get(FLOW_TICKET);
			if (null == value) {
				throw new FlowException(FlowErr.flow10014.getCode(), FlowErr.flow10014.getInfo(flowName, FLOW_TICKET + "不能为空"));
			}
			
			if (value instanceof String) {
				String ticket = (String) value;
				if (ticket.trim().length() == 0) {
					throw new FlowException(FlowErr.flow10014.getCode(), FlowErr.flow10014.getInfo(flowName, FLOW_TICKET + "不能为空"));
				}
				
				String ip = redis.get("flow-" + ticket);
				if (null == ip || ip.trim().length() == 0) {
					throw new FlowException(FlowErr.flow10014.getCode(), FlowErr.flow10014.getInfo(flowName, FLOW_TICKET + ":" + ticket + "已失效"));
				}
				
				if (!ip.equals(context.getInitParam().get(RequestHead.ClientIp.getCode()))) {
					throw new FlowException(FlowErr.flow10014.getCode(), FlowErr.flow10014.getInfo(flowName, FLOW_TICKET + ":" + ticket + "接入地址非法!"));
				}
			} else {
				throw new FlowException(FlowErr.flow10014.getCode(), FlowErr.flow10014.getInfo(flowName, FLOW_TICKET + "格式不正确"));
			}
		}
		
		chain.doFilter(context);
	}

}
