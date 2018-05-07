/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月24日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.node;

/**
 * @description
 * 节点参数检查接口
 */
public interface IParamInspector {
	
	
	/**
	 * 参数检查
	 * @param param
	 * @return
	 */
	public boolean inspect(Node<?, ?> node, NodeParam param);

}
