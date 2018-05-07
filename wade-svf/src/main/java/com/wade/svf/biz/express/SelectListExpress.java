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
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.wade.svf.biz.node.AbstractNode;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.express.IValueExpress;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * 针对输出为IDataset结果集时，用来选择指定的字段，并返回IDataset
 */
public class SelectListExpress implements IValueExpress {

	/**
	 * 解析@sellist:SERVICE_ID->OFFER_CODE,"OFFER_TYPE=S"
	 */
	@Override
	public boolean getValue(NodeParam node, String config) throws FlowException {
		FlowContext context = FlowContext.getContext();
		
		IDataset rtn = new DatasetList();
		String flowName = context.getFlow().getName();
		String nodeName = node.getNodeName();
		
		int keyIndex = config.indexOf(IFlowConfig.CFG_PARAM_TAG);
		if (keyIndex == -1) {
			throw new FlowException(FlowErr.flow10003.getCode(), FlowErr.flow10003.getInfo(flowName, nodeName, node.getValue() + "找不到参数类型分隔符"));
		}
		
		String keys = config.substring(keyIndex + 1);
		String[] cols = keys.split(",");
		
		if (node.isOutparam()) {
			Object obj = context.getOutParam(nodeName).get(AbstractNode._response);
			if (null == obj) {
				node.setValue(null);
				return true;
			}
			
			if (obj instanceof IDataset) {
				IDataset dataset = (IDataset) obj;
				for (int i = 0, size = dataset.size(); i < size; i++) {
					IData data = dataset.getData(i);
					
					IData tmp = new DataMap();
					for (String col : cols) {
						
						if (col.startsWith("\"") && col.endsWith("\"")) {
							col = col.substring(1, col.length() - 1);
							
							int index = col.indexOf("=");
							if (index == -1) {
								tmp.put(col, "");
							} else {
								tmp.put(col.substring(0, index), col.substring(index + 1));
							}
						} else {
							int index = col.indexOf("->");
							if (index == -1) {
								tmp.put(col, data.get(col));
							} else {
								tmp.put(col.substring(index + 2), data.get(col.substring(0, index)));
							}
						}
					}
					
					rtn.add(tmp);
				}
				
				node.setValue(rtn);
				return true;
			} else {
				node.setValue(null);
				return true;
			}
		}
		return false;
	}

}
