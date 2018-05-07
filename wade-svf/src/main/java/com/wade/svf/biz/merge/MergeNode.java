/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年8月1日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.merge;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.wade.svf.biz.node.ServiceNode;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * 数据合并，将两个IDataset的数据合并并且过滤出需要的结果集
 */
public class MergeNode extends ServiceNode {
	
	private static final String merge_primary_key = "merge.primary.key";
	private static final String merge_value1 = "merge.value1";
	private static final String merge_value2 = "merge.value2";
	private static final String merge_select_key = "merge.select.key";
	private static final String merge_data = "merge.data";
	
	public MergeNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_SERVICE_NAME, callback, next);
	}

	
	/**
	 * 数据合并处理
	 */
	@Override
	public ServiceResponse execute(ServiceRequest request) throws Exception {
		// 效验输入配置
		IFlowConfig config = getFlow().getConfig();
		NodeParam primaryKeyParam = config.getInParam(getName()).get(merge_primary_key);
		if (null == primaryKeyParam) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点未配置" + merge_primary_key));
		}
		NodeParam valueParam1 = config.getInParam(getName()).get(merge_value1);
		if (null == valueParam1) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点未配置" + merge_value1));
		}
		NodeParam valueParam2 = config.getInParam(getName()).get(merge_value2);
		if (null == valueParam2) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点未配置" + merge_value2));
		}
		NodeParam selectKeyParam = config.getInParam(getName()).get(merge_select_key);
		if (null == selectKeyParam) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点未配置" + merge_select_key));
		}
		
		// 效验输出配置
		NodeParam dataParam = config.getOutParam(getName()).get(merge_data);
		if (null == dataParam) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点未配置" + merge_data));
		}
		
		// 获取数据
		String[] primaryKey = getString(primaryKeyParam).split(",");
		IDataset value1 = getDataset(valueParam1);
		IDataset value2 = getDataset(valueParam2);
		String[] selectKey = getString(selectKeyParam).split(",");
		
		// 全并和过滤，优先遍历大的结果集
		if (value1.size() != value2.size()) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并的结果集大小不一致"));
		}
		IDataset out = mergeAndSelect(primaryKey[0].split("\\|"), primaryKey[1].split("\\|"), value1, value2, selectKey);
		
		ServiceResponse response = new ServiceResponse();
		response.setValue(dataParam.getKey(), out);
		return response;
	}
	
	
	/**
	 * 取NodeParam的String数据
	 * @param node
	 * @return
	 * @throws FlowException
	 */
	private String getString(NodeParam node) throws FlowException {
		// 参数转换
		parseValue(node);
		
		String key = node.getKey();
		Object value = node.getValue();
		
		if (null == value) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点数据不能为空" + key));
		}
		
		if (value instanceof String) {
			return (String) value;
		} else {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点数据格式不正确" + key));
		}
	}
	
	/**
	 * 获取NodeParam的IDataset数据
	 * @param node
	 * @return
	 * @throws FlowException
	 */
	private IDataset getDataset(NodeParam node) throws FlowException {
		// 参数转换
		parseValue(node);
		
		String key = node.getKey();
		Object value = node.getValue();
		
		if (null == value) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点数据不能为空" + key));
		}
		
		if (value instanceof IDataset) {
			return (IDataset) value;
		} else {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), "数据合并节点数据格式不正确" + key));
		}
	}
	
	/**
	 * 获取指定Key的对象数组
	 * @param data
	 * @param keys
	 * @return
	 * @throws FlowException
	 */
	private Object[] getObjects(IData data, String[] keys) throws FlowException {
		int len = keys.length;
		Object[] objs = new Object[len];
		
		for (int i = 0; i < len; i++) {
			Object obj = data.get(keys[i]);
			
			if (null == obj) {
				throw new FlowException(FlowErr.flow10016.getCode(), FlowErr.flow10016.getInfo(getFlow().getName(), getName(), "主键值不能为空:" + keys[i]));
			}
			
			objs[i] = obj;
		}
		
		return objs;
	}
	
	/**
	 * 合并 + 过滤
	 * @param key1
	 * @param key2
	 * @param source1
	 * @param source2
	 * @param cols
	 * @return
	 */
	private IDataset mergeAndSelect(String[] key1, String key2[], IDataset source1, IDataset source2, String[] cols) throws FlowException {
		IDataset out = new DatasetList();
		
		for (int i = 0, size = source1.size(); i < size; i++) {
			IData data = source1.getData(i);
			
			Object[] values = getObjects(data, key1);
			
			IData find = findByKV(source2, key2, values);
			if (null == find) {
				throw new FlowException(FlowErr.flow10016.getCode(), FlowErr.flow10016.getInfo(getFlow().getName(), getName(), "找不到关联的数据:" + key1 + "," + key2));
			}
			
			IData select = new DataMap();
			for (String col : cols) {
				String key = col;
				String alias = col;
				int index = col.indexOf("->");
				if (index != -1) {
					key = col.substring(0, index);
					alias = col.substring(index + 2);
				}
				
				if (data.containsKey(key)) {
					select.put(alias, data.get(key));
				} else if (find.containsKey(key)) {
					select.put(alias, find.get(key));
				} else {
					select.put(alias, "");
				}
			}
			
			out.add(select);
		}
		
		return out;
	}
	
	/**
	 * 查找IDataset里指定KV的IData
	 * @param source
	 * @param key
	 * @param value
	 * @return
	 */
	private IData findByKV(IDataset source, String[] keys, Object[] values) {
		for (int i = 0, size = source.size(); i < size; i++) {
			IData data = source.getData(i);
			
			boolean find = true;
			for (int j = 0, len = keys.length; j < len; j++) {
				Object tmp = data.get(keys[j]);
				if (find && null != tmp && tmp.equals(values[j])) {
					find = true;
				} else {
					find = false;
				}
			}
			
			if (find)
				return data;
		}
		
		return null;
	}
	
	@Override
	protected boolean isV5() {
		return true;
	}
}
