/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年8月1日
 * 
 * Just Do IT.
 */
package test.com.wade.svf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * TODO
 */
public class TestMerg {
	
	public static void main(String[] args) throws Exception {
		TestMerg tm = new TestMerg();
		
		String[] primaryKey = "SERVICE_ID|END_DATE,OFFER_CODE|END_DATE".split(",");
		IDataset source1 = tm.getOrder();
		IDataset source2 = tm.getOffer();
		String[] selectKey = "SERVICE_ID->service,START_DATE,END_DATE,OFFER_NAME".split(",");
		
		IDataset out = tm.mergeAndSelect(primaryKey[0], primaryKey[1], source1, source2, selectKey);
		
		System.out.println(out);
	}
	
	private IDataset getOffer() {
		IDataset ds = new DatasetList();
		
		IData data = new DataMap();
		data.put("OFFER_CODE", "1");
		data.put("OFFER_NAME", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		data = new DataMap();
		data.put("OFFER_CODE", "2");
		data.put("OFFER_NAME", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		data = new DataMap();
		data.put("OFFER_CODE", "3");
		data.put("OFFER_NAME", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		data = new DataMap();
		data.put("OFFER_CODE", "4");
		data.put("OFFER_NAME", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		return ds;
	}
	
	private IDataset getOrder() {
		IDataset ds = new DatasetList();
		
		IData data = new DataMap();
		data.put("SERVICE_ID", "1");
		data.put("START_DATE", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		data = new DataMap();
		data.put("SERVICE_ID", "2");
		data.put("START_DATE", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		data = new DataMap();
		data.put("SERVICE_ID", "3");
		data.put("START_DATE", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		data = new DataMap();
		data.put("SERVICE_ID", "4");
		data.put("START_DATE", "s1");
		data.put("END_DATE", "e1");
		ds.add(data);
		
		
		return ds;
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
				throw new FlowException(FlowErr.flow10016.getCode(), FlowErr.flow10016.getInfo("flow", "node", "主键值不能为空:" + keys[i]));
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
	private IDataset mergeAndSelect(String key1, String key2, IDataset source1, IDataset source2, String[] cols) throws FlowException {
		IDataset out = new DatasetList();
		
		for (int i = 0, size = source1.size(); i < size; i++) {
			IData data = source1.getData(i);
			
			Object[] values = getObjects(data, key1.split("\\|"));
			
			IData find = findByKV(source2, key2.split("\\|"), values);
			if (null == find) {
				throw new FlowException(FlowErr.flow10016.getCode(), FlowErr.flow10016.getInfo("flow", "node", "找不到关联的数据:" + key1 + "," + key2));
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

}
