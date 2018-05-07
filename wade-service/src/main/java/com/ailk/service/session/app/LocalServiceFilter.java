/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月13日
 * 
 * Just Do IT.
 */
package com.ailk.service.session.app;

import com.ailk.common.BaseException;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.service.ServiceManager;
import com.ailk.service.invoker.ServiceInvoker;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.filter.IDsfFilter;
import com.wade.dsf.filter.IDsfFilterChain;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * @description
 * 主服务调用过虑器
 */
public class LocalServiceFilter implements IDsfFilter {

	
	@Override
	public void doFilter(DsfRequest request, DsfResponse response, IDsfFilterChain chain) throws DsfException {
		String serviceName = request.getServiceName();
		
		String name = request.getHeader().get("name");
		String group = request.getHeader().get("group");
		ServiceEntity entity = ServiceManager.find(serviceName);
		IDataInput input = (IDataInput) request.getRequest();
		
		try {
			IDataOutput output = ServiceInvoker.localServiceinvoke(name, group, entity, input);
			response.setResponse(output);
		}  catch (BaseException e) {
			DsfException dsf = new DsfException(e.getCode(), e.getInfo(), e);
			dsf.setData(e.getData());
			throw dsf;
		} catch (Exception e) {
			throw new DsfException("LOCAL-SERVICE-FILTER", e.getMessage(), e);
		}
	}

}
