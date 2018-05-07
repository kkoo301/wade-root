/**
 * $
 */
package com.wade.dsf.filter;

import com.wade.dsf.exception.DsfException;
import com.wade.dsf.filter.IDsfFilter;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfServerFilter.java
 * @description: 服务第一层处理的过滤器
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class DsfServerFilter implements IDsfFilter {

	@Override
	public void doFilter(DsfRequest request, DsfResponse response, IDsfFilterChain chain) throws DsfException {
		try {
			chain.doFilter(request, response);
		} catch (DsfException e) {
			throw e;
		}
	}

}
