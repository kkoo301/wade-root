package com.ailk.common.data.impl;

import java.util.Comparator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

import net.sf.json.JSONObject;

public class DataComparator implements Comparator<Object> {
	private String key;
	private int keyType;
	private int order;

	public DataComparator(String key, int keyType, int order) {
		this.key = key;
		this.keyType = keyType;
		this.order = order;
	}

	public int compare(Object o1, Object o2) {
		if (o1 instanceof JSONObject && o2 instanceof JSONObject) {
			JSONObject data1 = (JSONObject) o1;
			JSONObject data2 = (JSONObject) o2;
			return jsonobjCompare(data1, data2);
		} else if (o1 instanceof IData && o2 instanceof IData) {
			IData data1 = (IData) o1;
			IData data2 = (IData) o2;
			return idataCompare(data1, data2);
		}
		return 0;
	}
	
	/**
	 * IData Compare
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	private int idataCompare(IData data1, IData data2) {
		if (order == IDataset.ORDER_ASCEND) {
			if (keyType == IDataset.TYPE_STRING) {
				String value1 = data1.getString(key);
				String value2 = data2.getString(key);
				return value1.compareTo(value2);
			} else if (keyType == IDataset.TYPE_INTEGER) {
				int value1 = data1.getInt(key, 0);
				int value2 = data2.getInt(key, 0);
				return value1 < value2 ? -1 : (value1 == value2 ? 0 : 1);
			} else if (keyType == IDataset.TYPE_DOUBLE) {
				double value1 = data1.getDouble(key, 0);
				double value2 = data2.getDouble(key, 0);
				return value1 < value2 ? -1 : (value1 == value2 ? 0 : 1);
			}
		} else {
			if (keyType == IDataset.TYPE_STRING) {
				String value1 = data1.getString(key);
				String value2 = data2.getString(key);
				return value2.compareTo(value1);
			} else if (keyType == IDataset.TYPE_INTEGER) {
				int value1 = data1.getInt(key, 0);
				int value2 = data2.getInt(key, 0);
				return value1 > value2 ? -1 : (value1 == value2 ? 0 : 1);
			} else if (keyType == IDataset.TYPE_DOUBLE) {
				double value1 = data1.getDouble(key, 0);
				double value2 = data2.getDouble(key, 0);
				return value1 > value2 ? -1 : (value1 == value2 ? 0 : 1);
			}
		}
		return 0;
	}
	
	/**
	 * JSONObject Compare
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	private int jsonobjCompare(JSONObject data1, JSONObject data2) {
		if (order == IDataset.ORDER_ASCEND) {
			if (keyType == IDataset.TYPE_STRING) {
				String value1 = data1.getString(key);
				String value2 = data2.getString(key);
				return value1.compareTo(value2);
			} else if (keyType == IDataset.TYPE_INTEGER) {
				int value1 = data1.getInt(key);
				int value2 = data2.getInt(key);
				return value1 < value2 ? -1 : (value1 == value2 ? 0 : 1);
			} else if (keyType == IDataset.TYPE_DOUBLE) {
				double value1 = data1.getDouble(key);
				double value2 = data2.getDouble(key);
				return value1 < value2 ? -1 : (value1 == value2 ? 0 : 1);
			}
		} else {
			if (keyType == IDataset.TYPE_STRING) {
				String value1 = data1.getString(key);
				String value2 = data2.getString(key);
				return value2.compareTo(value1);
			} else if (keyType == IDataset.TYPE_INTEGER) {
				int value1 = data1.getInt(key);
				int value2 = data2.getInt(key);
				return value1 > value2 ? -1 : (value1 == value2 ? 0 : 1);
			} else if (keyType == IDataset.TYPE_DOUBLE) {
				double value1 = data1.getDouble(key);
				double value2 = data2.getDouble(key);
				return value1 > value2 ? -1 : (value1 == value2 ? 0 : 1);
			}
		}
		return 0;
	}
}
