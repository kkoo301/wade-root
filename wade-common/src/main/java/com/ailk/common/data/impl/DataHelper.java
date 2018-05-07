package com.ailk.common.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IVisit;

public final class DataHelper {
	
	/**
	 * sort single (default ascend)
	 */
	public static final void sort(IDataset data, String key, int keyType) {
		sort(data, key, keyType, IDataset.ORDER_ASCEND);
	}

	/**
	 * sort single
	 */
	public static final void sort(IDataset data, String key, int keyType, int order) {
		IData[] maps = new DataMap[data.size()];
		IData[] datas = data.toArray(maps);
		DataComparator c = new DataComparator(key, keyType, order);
		Arrays.sort(datas, c);

		List<IData> list = Arrays.asList(datas);

		data.clear();
		data.addAll(list);
	}

	/**
	 * sort double (default ascend)
	 */
	public static final void sort(IDataset data, String key1, int keyType1, String key2, int keyType2) {
		sort(data, key1, keyType1, IDataset.ORDER_ASCEND);
		sort(data, key2, keyType2, IDataset.ORDER_ASCEND, key1, keyType1);
	}

	/**
	 * sort double
	 */
	public static final void sort(IDataset data, String key1, int keyType1, int order1, String key2, int keyType2, int order2) {
		sort(data, key1, keyType1, order1);
		sort(data, key2, keyType2, order2, key1, keyType1);
	}

	/**
	 * sort assistant
	 */
	private static final void sort(IDataset data, String key, int type, int order, String fix, int fixType) {
		IData[] maps = new DataMap[data.size()];
		IData[] datas = data.toArray(maps);

		DataComparator c = new DataComparator(key, type, order);

		if (fix == null) {
			Arrays.sort(datas, c);
		} else {
			int[] marks = Anchor.mark(data, fix, fixType);

			for (int pre = 0, i = 1, size = marks.length; i < size; i++) {
				Arrays.sort(datas, pre, marks[i], c);
				pre = marks[i];
			}
		}
		List<IData> list = Arrays.asList(datas);

		data.clear();
		data.addAll(list);
	}
	
	
	/**
	 * filter
	 * for example:filter="KEY1=VALUE1,KEY2=VALUE2"
	 * @param filter
	 * @return IDataset
	 * @throws Exception
	 */
	public static final IDataset filter(IDataset source, String filter) throws Exception {
		if (null == filter || filter.length() == 0) return source;
		
		IData ftdt = new DataMap();
		String[] fts = filter.split(",");
		for (int i = 0, size = fts.length; i < size; i++) {
			String[] ft = fts[i].split("=");
			ftdt.put(ft[0], ft[1]);
		}
		
		IDataset subset = new DatasetList();
		for (int i = 0, size = source.size(); i < size; i++) {
			IData subdata = source.getData(i);
			boolean include = true;
			String[] ftdtNames = ftdt.getNames();
			for (int j = 0, nameSize = ftdtNames.length; j < nameSize; j++) {
				String subvalue = (String) subdata.get(ftdtNames[j]);
				if (subvalue == null || !subvalue.equals(ftdt.get(ftdtNames[j]))) {
					include = false;
					break;
				}
			}
			if (include) subset.add(subdata);
		}
		
		return subset;
	}
	
	
	/**
	 * distinct
	 * @param fieldNames
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static final IDataset distinct(IDataset source, String fieldNames, String token) throws Exception {
		if ("".equals(fieldNames)) return source;
		
		List<String> fieldValues = new ArrayList<String>();
		IDataset subset = new DatasetList();
		String theToken = token == null || "".equals(token) ? "," : token;
		
		String[] keys = fieldNames.split(theToken);
		int keySize = keys.length;
		for (int i = 0, size = source.size(); i < size; i++) {
			String fieldValue = "";
			for (int j = 0; j < keySize; j++) {
				fieldValue += (String) source.get(i, keys[j]) + theToken;
			}
			if ("".equals(fieldValue)) continue;
			if (!fieldValues.contains(fieldValue)) {
				fieldValues.add(fieldValue);
				subset.add(source.get(i));
			}
		}
		return subset;
	}

	/**
	 * 
	 * @param visit
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static final IDataInput createDataInput(IVisit visit,IData params, Pagination pagination){ 
		return createDataInput(visit,params,pagination,null,null);
	}
	
			
	/**
	 * create data input
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static final IDataInput createDataInput(IVisit visit,IData params, Pagination pagination,String[] headNames,String[] headValues){ 
		IData ctx = new DataMap();
		if(visit != null){
			ctx.putAll(visit.getAll());
		}
		if(headNames != null && headValues != null 
				&& headNames.length == headValues.length
				&& headNames.length > 0){
			
			for(int i=0, size = headNames.length; i < size; i++){
				ctx.put(headNames[i], headValues[i]);
			}
		}
		
		DataInput input = new DataInput(ctx, params == null ? new DataMap() : params);
		
		if (pagination != null) {
			input.setPagination(pagination);
		}
		
		return input;
	}
	
	/**
	 * 将Dataset转换成IData格式：{key1:[v1,v2,v3],key2:[v1,null,v3]},Dataset里的每一个元素必须是IData或可转成IData的串
	 * 1.先获取Dataset里key的全集，并生成一个IData
	 * 2.将Dataset里的数据填充到1生成的IData里
	 * @param list
	 * @param nullable true:若没有值则用null填充；false:若没有值侧用“”串填充
	 * @return
	 */
	public static final IData datasetToData(List list, boolean nullable) {
		IData data = new DataMap();
		
		IDataset value = new DatasetList();
		for (int i = 0; i<list.size(); i++) {
			value.add(nullable ? null : "");
		}
		
		int index = 0;
		for(Object obj : list) {
			IData d1 = null;
			if (obj instanceof String) {
				d1 = new DataMap((String) obj);
			} else if (obj instanceof IData) {
				d1 = (IData) obj;
			} else if (obj instanceof Map) {
				d1 = new DataMap();
				d1.putAll((Map) obj);
			}
			
			Iterator<String> iter = d1.keySet().iterator();
			
			while(iter.hasNext()) {
				String k1 = iter.next();
				IDataset v1 = (IDataset) data.get(k1);
				
				if (null == v1) {
					v1 = new DatasetList();
					v1.addAll(value);
					
					data.put(k1, value);
				}
				
				v1.set(index, d1.get(k1));
				data.put(k1, v1);
			}
			index ++;
		}
		return data;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static IDataset toDataset(IData map) throws Exception {
		int size = 0;
		IDataset dataset = new DatasetList();
		String[] names = map.getNames();
		for (int i=0; i<names.length; i++) {
			if (map.get(names[i]) instanceof List) {
				List list = (List) map.get(names[i]);
				if (size < list.size()) {
					size = list.size();
				}
			}
		}
		
		if (size == 0) {
			size = 1;
		}
		
		for (int i=0; i<size; i++) {
			IData data = new DataMap();
			for (int j=0; j<names.length; j++) {				
				Object obj = map.get(names[j]);
				if (obj instanceof List) {
					List list = (List) obj;
					if (i < list.size()) {
						data.put(names[j], list.get(i));
					} else {
						data.put(names[j], "");
					}
				} else {
					data.put(names[j], obj);
				}
			}
			dataset.add(data);
		}
		return dataset;
	}
	
	/**
	 * listToData
	 * @param list
	 * @return
	 */
	public static IDataset listToDataset(List list) {
		IDataset ds = new DatasetList();
		
		for (Object obj : list) {
			if (obj instanceof Map) {
				ds.add(mapToIData((Map<?, ?>) obj));
			} else if (obj instanceof List) {
				ds.add(listToDataset((List) obj));
			} else {
				ds.add(obj);
			}
		}
		
		return ds;
	}
	
	/**
	 * mapToIData
	 * @param map
	 * @return
	 */
	public static IData mapToIData(Map<?,?> map) {
		IData data = new DataMap();
		
		Iterator<?> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object value = map.get(key);
			
			if (value instanceof Map) {
				data.put(key, mapToIData((Map<?,?>) value));
			} else if (value instanceof List) {
				data.put(key, listToDataset((List) value));
			} else {
				data.put(key, value);
			}
		}
		
		return data;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<?, ?> idataToMap(IData map) {
		Map data = new HashMap();
		
		Iterator<?> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object value = map.get(key);
			
			if (value instanceof IData) {
				data.put(key, mapToIData((IData) value));
			} else if (value instanceof List) {
				data.put(key, datasetToList((IDataset) value));
			} else {
				data.put(key, value);
			}
		}
		
		return data;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List datasetToList(IDataset list) {
		List ds = new ArrayList();
		
		for (Object obj : list) {
			if (obj instanceof IData) {
				ds.add(idataToMap((IData) obj));
			} else if (obj instanceof IDataset) {
				ds.add(datasetToList((IDataset) obj));
			} else {
				ds.add(obj);
			}
		}
		
		return ds;
	}
	
	/**
	 * 处理JSON字符串中的特殊字符
	 * @param value
	 * @return
	 */
	public static String parseJsonString(String str){
		if(str == null) return str;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
				/*case '\\':
					sb.append("\\\\");
					break;
				case '/':
					sb.append("\\/");
					break;*/
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				/*case '\"':
					sb.append("!~a~!");
					break;*/
				default:
					sb.append(c);
			}
		}
		String ret = sb.toString();
		
		/*
		StringUtils.replace(ret, "{!~a~!", "{\"");  //处理 {"
		StringUtils.replace(ret, "!~a~!}", "\"}");  //处理 "}
		StringUtils.replace(ret, "!~a~!,!~a~!", "\",\""); //处理 ","
		StringUtils.replace(ret, "!~a~!:!~a~!", "\":\""); //处理 ":"
		StringUtils.replace(ret, "!~a~!:[", "\":[");  //处理 ":[
		StringUtils.replace(ret, "],!~a~!", "],\"");  //处理 ],"
		StringUtils.replace(ret, "!~a~!:{", "\":{");  //处理 ":{
		StringUtils.replace(ret, "},!~a~!", "},\"");  //处理 },"
		*/
		
		return ret;
	}
	
	public static void main(String[] args) {
		IDataset ds = new DatasetList();
		IData data = new DataMap();
		data.put("k1", "v1");
		data.put("k2", "v2");
		data.put("k3", "v3");
		ds.add(data);
		
		data = new DataMap();
		data.put("k1", "v1");
		ds.add(data);
		
		IDataset ds1 = new DatasetList();
		ds1.add(data);
		
		data = new DataMap();
		data.put("k4", "v4");
		data.put("k7", ds1);
		ds.add(data);
		
		data = new DataMap();
		data.put("k1", "v1");
		data.put("k2", "v2");
		data.put("k5", "v5");
		ds.add(data);
		
		System.out.println(datasetToData(ds, false));
	}
}
