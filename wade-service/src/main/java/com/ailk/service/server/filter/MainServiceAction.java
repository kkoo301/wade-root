/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月13日
 * 
 * Just Do IT.
 */
package com.ailk.service.server.filter;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.BaseException;
import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.filter.DsfRemoteFilterChain;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;
import com.wade.dsf.server.DsfContext;

/**
 * @description
 * 主服务执行
 */
public final class MainServiceAction {
	
	private static final Logger log = LoggerFactory.getLogger(MainServiceAction.class);
	
	/**
	 * 服务执行
	 * @param serviceName
	 * @param header
	 * @param body
	 * @return
	 * @throws DsfException
	 */
	public Serializable execute(String serviceName, Map<String, String> header, Serializable body) throws BaseException {
		log.debug("主服务过滤器...开始:" + serviceName);
		try {
			// 初始化Dsf配置
			DsfConfigure.getInstance();
			
			// 创建请求、响应对象
			DsfRequest dreq = new DsfRequest(serviceName, header, body);
			DsfResponse dres = new DsfResponse();
			
			// 创建线程上下文对象, 线程退出时需清除
			DsfContext context = DsfContext.getContext();
			context.setServiceName(serviceName);
			context.setRequest(dreq);
			context.setResponse(dres);
			
			// 执行过滤器链
			DsfRemoteFilterChain chain = new DsfRemoteFilterChain();
			chain.doFilter(dreq, dres);
			
			// 返回业务对象
			Serializable response = context.getResponse().getResponse();
			
			return response;
		} catch (DsfException e) {
			BaseException be = new BaseException(e.getErrInfo(), e);
			be.setData(e.getData());
			throw be;
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BaseException(e);
		} finally {
			log.debug("主服务过滤器...结束:" + serviceName);
			DsfContext.destory();
		}
	}

}
