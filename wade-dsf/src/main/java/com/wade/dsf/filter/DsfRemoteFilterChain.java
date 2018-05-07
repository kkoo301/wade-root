/**
 * $
 */
package com.wade.dsf.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.filter.IDsfFilter;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfFilterChain.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class DsfRemoteFilterChain implements IDsfFilterChain {
	
	private static final Logger log = LoggerFactory.getLogger(DsfRemoteFilterChain.class);
	
	private static IDsfFilter[] filters = DsfConfigure.getInstance().getRemoteFilters();
	
	/**
	 * 用来迭代Filter的游标
	 */
	private int filterIndex = 0;
	
	public DsfRemoteFilterChain() {
		
	}
	
	public void doFilter(DsfRequest request, DsfResponse response) throws DsfException {
		if (null == filters || filters.length == 0) {
			throw new DsfException(DsfErr.dsf10000.getCode(), DsfErr.dsf10000.getInfo("远程过滤器配置信息不能为空"));
		}
		
		if (filterIndex < filters.length) {
			IDsfFilter filter = filters[filterIndex];
			
			log.debug(String.format("主服务过滤器执行开始:%s->%s", new Object[] {filter.getClass(), request.getServiceName()}));
			
			filterIndex ++;
			
			long start = System.currentTimeMillis();
			try {
				filter.doFilter(request, response, this);
			} catch (DsfException e) {
				throw e;
			} finally {
				String cost = String.valueOf((System.currentTimeMillis() - start));
				log.debug(String.format("主服务过滤器执行结束:%s->%s, 耗时:%s.", new Object[] {filter.getClass(), request.getServiceName(), cost}));
			}
			
		}
	}
	
}
