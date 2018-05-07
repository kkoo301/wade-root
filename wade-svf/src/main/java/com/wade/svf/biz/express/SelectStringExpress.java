/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年8月1日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.express;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.wade.svf.biz.node.AbstractNode;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.express.IValueExpress;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * 针对输出为IDataset结果集时，用来选择指定的字段，并用“,”号拼成一个字符串
 */
public class SelectStringExpress implements IValueExpress {

	/**
	 * 解析@selstr:SERVICE_ID
	 */
	@Override
	public boolean getValue(NodeParam node, String config) throws FlowException {
		FlowContext context = FlowContext.getContext();
		
		String flowName = context.getFlow().getName();
		String nodeName = node.getNodeName();
		
		StringBuilder rtn = new StringBuilder(100);
		
		int index = config.indexOf(IFlowConfig.CFG_PARAM_TAG);
		if (index == -1) {
			throw new FlowException(FlowErr.flow10003.getCode(), FlowErr.flow10003.getInfo(flowName, nodeName, node.getValue() + "找不到参数类型分隔符"));
		}
		
		String key = config.substring(1, index);
		
		if (node.isOutparam()) {
			Object obj = context.getOutParam(node.getNodeName()).get(AbstractNode._response);
			if (null == obj) {
				node.setValue(null);
				return true;
			}
			
			if (obj instanceof IDataset) {
				IDataset dataset = (IDataset) obj;
				for (int i = 0, size = dataset.size(); i < size; i++) {
					IData data = dataset.getData(i);
					String value = data.getString(key);
					
					rtn.append(value);
					if (i + 1 < size) {
						rtn.append(",");
					}
				}
				
				node.setValue(rtn.toString());
				return true;
			} else {
				node.setValue(null);
				return true;
			}
		}
		
		return false;
	}

}
