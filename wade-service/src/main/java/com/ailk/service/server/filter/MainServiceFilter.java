/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月13日
 * 
 * Just Do IT.
 */
package com.ailk.service.server.filter;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.common.BaseException;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.service.invoker.ServiceInvoker;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.filter.IDsfFilter;
import com.wade.dsf.filter.IDsfFilterChain;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * @description
 * 主服务调用过虑器
 */
public class MainServiceFilter implements IDsfFilter {
	
	@Override
	public void doFilter(DsfRequest request, DsfResponse response, IDsfFilterChain chain) throws DsfException {
		String serviceName = request.getServiceName();
		IDataInput input = (IDataInput) request.getRequest();
		
		try {
			if (isV5(serviceName)) {
				IDataOutput output = ServiceInvoker.remoteServiceInvoke(serviceName, createServiceRequest(input));
				response.setResponse(convertServiceResponse(output));
			} else {
				IDataOutput output = ServiceInvoker.remoteServiceInvoke(serviceName, input);
				response.setResponse(output);
			}
		} catch (BaseException e) {
			DsfException dsf = new DsfException(e.getCode(), e.getInfo(), e);
			dsf.setData(e.getData());
			throw dsf;
		} catch (Exception e) {
			throw new DsfException("REMOTE-SERVICE-FILTER", e.getMessage(), e);
		}
	}
	
	/**
	 * 将IDataInput转换成ServiceRequest对象
	 * @param input
	 * @return
	 */
	private IDataInput createServiceRequest(IDataInput input) {
		ServiceRequest request = new ServiceRequest();
		request.getHead().putAll(input.getHead());
		request.getBody().putAll(input.getData());
		request.setPagination(input.getPagination());
		return request;
	}
	
	/**
	 * 将返回的ServiceResponse转换成IDataOutput对象
	 * @param output
	 * @return
	 */
	private IDataOutput convertServiceResponse(IDataOutput output) {
		IDataOutput response = new DataOutput();
		response.getHead().putAll(output.getHead());
		response.getData().addAll(output.getData());
		return response;
	}
	
	/**
	 * 根据服务名前缀判断是否为V5版本，是否需要转换输入输出
	 * @param serviceName
	 * @return
	 */
	private boolean isV5(String serviceName) {
		if (null == serviceName || serviceName.trim().length() == 0)
			return false;
		
		int index = serviceName.indexOf(".");
		if (index == -1) {
			return false;
		}
		String subsys = serviceName.substring(0, index);
		
		String ver = GlobalCfg.getProperty("service." + subsys + ".version", "v4");
		if ("v5".equals(ver.trim())) {
			return true;
		}
		
		return false;
	}

}
