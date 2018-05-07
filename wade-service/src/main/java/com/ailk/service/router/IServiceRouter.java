package com.ailk.service.router;

import com.ailk.common.data.IData;

public interface IServiceRouter {
	
	/**
	 * 获取服务路由
	 * @param svcname
	 * @param visit
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String getRoute(IData data) throws Exception ;
	
}
