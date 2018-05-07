/**
 * $
 */
package com.wade.dsf.test.filter;

import org.apache.log4j.Logger;

import com.wade.dsf.exception.DsfException;
import com.wade.dsf.filter.DsfFilterChain;
import com.wade.dsf.filter.IDsfFilter;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: MyFilter.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-7-28
 */
public class MyFilter implements IDsfFilter {

	private static final Logger log = Logger.getLogger(MyFilter.class);
	
	@Override
	public void doFilter(DsfRequest request, DsfResponse response, DsfFilterChain chain) throws DsfException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("服务%s进入过滤器", request.getServiceName()));
		}
		
		// 继续走过滤器, 如果需要终止，请抛出DsfException
		chain.doFilter(request, response);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("服务%s退出过滤器", request.getServiceName()));
		}
	}

}
