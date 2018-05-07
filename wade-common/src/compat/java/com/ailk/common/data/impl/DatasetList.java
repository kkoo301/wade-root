package com.ailk.common.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class DatasetList extends ArrayList<Object> implements IDataset {

	private static final long serialVersionUID = 8302984775243577040L;

	public DatasetList() {
		super(Pagination.DEFAULT_PAGE_SIZE);
	}
	
	public DatasetList(int size) {
		super(size);
	}

	/**
	 * construct function
	 * 
	 * @param data
	 */
	public DatasetList(IData data) {
		super(Pagination.DEFAULT_PAGE_SIZE);
		add(data);
	}
	
	/**
	 * construct function
	 * 
	 * @param data
	 */
	public DatasetList(IData[] datas) {
		super(Pagination.DEFAULT_PAGE_SIZE);
		for (IData data : datas) {
			add(data);
		}
	}
	
	/**
	 * construct function
	 * 
	 * @param list
	 */
	public DatasetList(IDataset list) {
		super(Pagination.DEFAULT_PAGE_SIZE);
		addAll(list);
	}
	
	
	/**
	 * construct function
	 * 
	 * @param jsonArray
	 */
	public DatasetList(String jsonArray) {
		super(Pagination.DEFAULT_PAGE_SIZE);
		if (jsonArray != null && jsonArray.indexOf("\"class\":") != -1) {
			jsonArray = StringUtils.replace(jsonArray, "\"class\":", "\"__classChangedByFrameWork__\":");
		}
		JSONArray array = JSONArray.fromObject(DataHelper.parseJsonString(jsonArray));
		addAll(DatasetList.fromJSONArray(array));
	}
	
	/**
	 * construct function
	 * 
	 * @param data
	 */
	public DatasetList(JSONArray array) {
		super(Pagination.DEFAULT_PAGE_SIZE);
		this.addAll(DatasetList.fromJSONArray(array));
	}	
	
	public static DatasetList fromJSONArray(JSONArray array) {
		if (array != null) {
			DatasetList list = new DatasetList();
			Iterator<?> it = array.iterator();
			while (it.hasNext()) {
				Object value = it.next();
				if (value != null) {
					if (value instanceof JSONObject) {
						list.add(JSONUtils.isNull(value) ? null : DataMap
								.fromJSONObject((JSONObject) value));
					} else if (value instanceof DataMap) {
						list.add((IData) value);
					}else if (value instanceof String) {
						if(value!=null && ((String)value).indexOf("__classChangedByFrameWork__")!=-1){
							value = StringUtils.replace((String)value, "__classChangedByFrameWork__", "class");
						}
						
						if (JSONUtils.mayBeJSON((String) value)) {
							if (((String) value).startsWith("{")) {
								list.add(new DataMap(((String) value)));
							} else if (((String) value).startsWith("[")) {
								list.add(new DatasetList(((String) value)));
							} else {
								list.add(value);
							}
						}
					}else{
						list.add(value);
					}
				} else {
					list.add(null);
				}
			}
			return list;
		}
		return null;
	}
	
	/**
	 * get names
	 * 
	 * @return String[]
	 */
	public String[] getNames() {
		return size() > 0 ? ((IData) get(0)).getNames() : null;
	}
	
	public Object get(int index){
		return super.get(index);
	}
	
	/**
	 * get object
	 * @param index
	 * @param name
	 * @return Object
	 */
	public Object get(int index, String name) {
		Object data = get(index);
		if (null == data)
			return null;
		
		if (data instanceof Map<?, ?>) {
			IData map = new DataMap();
			map.putAll((HashMap) data);
			return map.get(name);
		}
		
		return null;
	}
	
	public Object get(int index, String name, Object def) {
		Object value = get(index, name);
		return value == null ? def : value;
	}

	/**
	 * get data
	 * @param index
	 * @return IData
	 */
	@SuppressWarnings("unchecked")
	public IData getData(int index){
		Object value = get(index);
		if (value == null)
			return null;
		if (value instanceof String) {
			return new DataMap((String)value);
		} else if (value instanceof JSONObject) {
			IData data = new DataMap();
			data.putAll((JSONObject) value );
			return data;
		} else {
			return (IData)value;
		}
	}
	
	/**
	 * get dataset
	 * @param index
	 * @return IDataset
	 */
	public IDataset getDataset(int index) {
		Object value = get(index);
		if (value == null)
			return null;
		
		if (value instanceof String) {
			return new DatasetList((String)value);
		} else if (value instanceof JSONArray) {
			return DatasetList.fromJSONArray((JSONArray)value);
		}else{
			return (IDataset)value;
		}
	}
	
	/**
	 * first
	 * 
	 * @return IData
	 */
	public IData first() {
		return size() > 0 ? (IData)get(0) : null;
	}
	
	/**
	 * to data
	 * @return IData
	 */
	public IData toData() {
		IData data = new DataMap();
		
		Iterator<Object> it = iterator();
		while (it.hasNext()) {
			IData element = (IData) it.next();
			Iterator<String> iterator = element.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (data.containsKey(key)) {
					IDataset list = (IDataset) data.get(key);
					list.add(element.get(key));
				} else {
					IDataset list = new DatasetList();
					list.add(element.get(key));
					data.put(key, list);
				}
			}			
		}

		return data;
	}
	
	/**
	 * to string
	 * @return String
	 */
	public String toString() {
		return JSONArray.fromObject(this).toString();
	}
	
}