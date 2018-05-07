/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年8月1日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.express;

import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * 解析value值的表达式
 */
public interface IValueExpress {
	
	/**
	 * 流程运行时执行，用来解析配置里value属性的内置对象，在该方法里可获取流程上下文的所有内容<br>
	 * 当value的内容被解析时返回true，且需要执行param.setValue(parsedvalue)<br>
	 * config为xml配置的内容，标准格式如下：@xxxx:[inparam|outparam].key
	 * @param node
	 * @param config
	 * @return
	 * @throws
	 */
	public boolean getValue(NodeParam node, String config) throws FlowException ;
	

}
