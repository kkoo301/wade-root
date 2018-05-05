package com.wade.relax.registry.consumer.lb;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: 负载均衡接口类
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public interface ILoadBalance {
	
	/**
	 * 监控检查
	 */
	public void healthCheck();
	
}
