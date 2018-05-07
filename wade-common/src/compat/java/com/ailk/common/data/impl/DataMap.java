package com.ailk.common.data.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

public class DataMap extends HashMap<String, Object> implements IData {
	
	private static final long serialVersionUID = 5728540280422795959L;
	private static final String CLASS_STRING1 = "\"class\":";
	private static final String CLASS_REP_STRING1 = "\"_^CCBW^_\":";
	private static final String CLASS_STRING2 = "class";
	private static final String CLASS_REP_STRING2 = "_^CCBW^_";

	public DataMap() {
		super();
	}
	
	public DataMap(int size) {
		super(size);
	}
	
	public DataMap(Map<String, Object> map) {
		super(map);
	}
	
	
	public DataMap(String jsonObject) {
		if (jsonObject != null && jsonObject.indexOf(CLASS_STRING1) != -1) {
			jsonObject = StringUtils.replace(jsonObject, CLASS_STRING1, CLASS_REP_STRING1);
		}
		
		JSONObject map = JSONObject.fromObject(DataHelper.parseJsonString(jsonObject));
		if (map != null) {
			this.putAll(fromJSONObject(map));
		}
	}
	
	public static DataMap fromJSONObject(JSONObject object) {
		if (object != null) {
			DataMap data = new DataMap();
			Iterator<?> keys = object.keys();
			while (keys.hasNext()) {
				Object key = keys.next();
				Object value = object.get(key);
				
				if(((String)key).indexOf(CLASS_REP_STRING2)!=-1){
					key = StringUtils.replace(((String)key), CLASS_REP_STRING2, CLASS_STRING2);
				}
				
				if (value != null) {
					if (value instanceof JSONObject) {
						data.put((String) key, JSONUtils.isNull(value) ? null
								: DataMap.fromJSONObject((JSONObject) value));
					} else if (value instanceof JSONArray) {
						data.put((String) key, JSONUtils.isNull(value) ? null
								: DatasetList.fromJSONArray((JSONArray) value));
					} else if (value instanceof String) {
						/*
						 * if(JSONUtils.mayBeJSON((String)value)){
						 * if(((String)value).startsWith("{")){
						 * data.put((String)key, new DataMap(((String)value)));
						 * }else if(((String)value).startsWith("[")){
						 * data.put((String)key, new
						 * DatasetList(((String)value))); }else{
						 * data.put((String)key, value); } }else{
						 */
						data.put((String) key, value);
						// }
					} else {
						data.put((String) key, value);
					}
				} else {
					data.put((String) key, value);
				}
			}
			return data;
		}
		return null;
	}
	
	/**
	 * get names
	 * @return String[]
	 */
	public String[] getNames() {
		String[] names = new String[size()];
		Iterator<String> keys = keySet().iterator();
		int index = 0;
		while(keys.hasNext()) {
			names[index] = keys.next();
			index++;
		}
		return names;
	}

	/**
	 * name is null or not exist
	 * @param name
	 * @return boolean
	 */
	public boolean isNoN(String name) {
		return name == null || !containsKey(name);
	}	
	
	/**
	 * get
	 * @param name
	 * @return boolean
	 */
	public String getString(String name) {
		Object value = super.get(name);
		if (value == null) return null;
		return value.toString();
	}
	
	/**
	 * get string
	 * @param name
	 * @param defaultValue
	 * @return String
	 */
	public String getString(String name, String defaultValue) {
		String value = getString(name);
		if (value == null) return defaultValue;
		return value;
	}

	/**
	 * get boolean
	 * @param name
	 * @return boolean
	 */
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}
	
	/**
	 * get boolean
	 * @param name
	 * @param defaultValue
	 * @return boolean
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		Object value = get(name);
		if (null == value)
			return defaultValue;
		
		return "true".equalsIgnoreCase(value.toString());
	}
	
	/**
	 * get double
	 * @param name
	 * @return double
	 */
	public double getDouble(String name) {
		return getDouble(name, 0);
	}
	
	/**
	 * get double
	 * @param name
	 * @param defaultValue
	 * @return double
	 */
	public double getDouble(String name, double defaultValue) {
		Object value = super.get(name);
		if (value == null) return defaultValue;
		
		return Double.parseDouble(value.toString());
	}
	
	/**
	 * get int
	 * @param name
	 * @return int
	 */
	public int getInt(String name) {
		return getInt(name, 0);
	}
	
	/**
	 * get int
	 * @param name
	 * @param defaultValue
	 * @return int
	 */
	public int getInt(String name, int defaultValue) {
		Object value = super.get(name);
		if (value == null)return defaultValue;
		return Integer.parseInt(value.toString());
	}
	
	/**
	 * get long
	 * @param name
	 * @return long
	 */
	public long getLong(String name) {
		return getLong(name, 0);
	}
	
	/**
	 * get long
	 * @param name
	 * @param defaultValue
	 * @return long
	 */
	public long getLong(String name, long defaultValue) {
		Object value = super.get(name);
		if (value == null) return defaultValue;
		return Long.parseLong(value.toString());
	}
	
	/**
	 * get data
	 * @param name
	 * @return IData
	 */
	public IData getData(String name) {
		Object value = super.get(name);
		if (value == null)
			return null;
		
		if (value instanceof IData) {
			return (IData)value;
		} else {
			return null;
		}
	}
	
	/**
	 * get data
	 * @param name
	 * @param def
	 * @return
	 */
	public IData getData(String name, IData def) {
		Object value = super.get(name);
		if (value == null)
			return def;
		
		if (value instanceof IData) {
			return (IData)value;
		} else {
			return def;
		}
	}
	
	/**
	 * get dataset
	 * @param name Key
	 * @param def 默认值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IDataset getDataset(String name, IDataset def) {
		Object value = super.get(name);
		if (value == null)
			return def;
		
		if (value instanceof IDataset) {
			return (IDataset)value;
		} if (value instanceof JSONArray) {
			IDataset ds = new DatasetList();
			ds.addAll((JSONArray) value);
			return ds;
		} else {
			return def;
		}	
	}
	
	/**
	 * get data
	 * @param name
	 * @return IDataset
	 */
	public IDataset getDataset(String name) {
		return getDataset(name, null);
	}
	
	/**
	 * sub data
	 * @param group
	 * @return IData
	 * @throws Exception
	 */
	public IData subData(String group) throws Exception {
		return subData(group, false);
	}
	
	/**
	 * sub data
	 * @param group
	 * @param istrim
	 * @return IData
	 * @throws Exception
	 */
	public IData subData(String group, boolean istrim) throws Exception {
		IData element = new DataMap();
		
		String[] names = getNames();
		String prefix = group + "_";
		for (String name : names) {
			if (name.startsWith(prefix)) {
				element.put(istrim ? name.substring((prefix).length()) : name, get(name));
			}
		}
		
		return element;
	}
	
	public String put(String key, String value){
		return (String)super.put(key, value);
	}
	
	public IData put(String key, IData value){
		return (IData)super.put(key, value);
	}
	
	public IDataset put(String key, IDataset value){
		return (IDataset)super.put(key, value);
	}
	
	/**
	 * to string
	 * @return String
	 */ 
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}	
}