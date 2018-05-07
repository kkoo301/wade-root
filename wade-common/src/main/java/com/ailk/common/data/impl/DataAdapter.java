package com.ailk.common.data.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 新老JSONObject和 JSONArray对象兼容处理类
 * @author xiedx@aisinfo.com
 *
 */
public class DataAdapter
{
	private static transient final Logger log = Logger.getLogger(DataAdapter.class);
	
	private static Class<?> wadeJSONArrayClass = null;
	private static Class<?> wadeJSONObjectClass = null;
	
	private static Class<?> datasetClass = null;
	private static Class<?> datamapClass = null;
	
	private static Constructor<?> wadeJSONObjectStringConstructor = null;
	private static Constructor<?> wadeJSONObjectMapConstructor = null;
	
	private static Method wadeJSONArrayToStringMethod = null;
	private static Method wadeJSONObjectToStringMethod = null;
	
	private static Method fromWadeJSONArrayMethod = null;
	private static Method fromWadeJSONObjectMethod = null;

	/**
	 * 反射获取类，构造方法和调用方法
	 */
	static
	{
		try {
			wadeJSONArrayClass = Class.forName("com.ailk.common.json.JSONArray");
			try {
				wadeJSONArrayToStringMethod = wadeJSONArrayClass.getMethod("toString", new Class[]{});
			} catch (SecurityException e) {
				log.error(e);
			} catch (NoSuchMethodException e) {
				log.debug(e);
			}
		} catch (ClassNotFoundException e) {
			log.debug(e);
		}
		
		try {
			wadeJSONObjectClass = Class.forName("com.ailk.common.json.JSONObject");
			try {
				wadeJSONObjectStringConstructor = wadeJSONObjectClass.getConstructor(new Class[]{String.class});
				wadeJSONObjectMapConstructor = wadeJSONObjectClass.getConstructor(new Class[]{Map.class});
				wadeJSONObjectToStringMethod = wadeJSONObjectClass.getMethod("toString", new Class[]{});
			} catch (SecurityException e) {
				log.error(e);
			} catch (NoSuchMethodException e) {
				log.debug(e);
			}
		} catch (ClassNotFoundException e) {
			log.debug(e);
		}
		
		try {
			datasetClass = Class.forName("com.ailk.common.data.impl.DatasetList");
			try {
				if(null != wadeJSONArrayClass ){
					fromWadeJSONArrayMethod = datasetClass.getMethod("fromJSONArray", new Class[]{wadeJSONArrayClass});
				}
			} catch (SecurityException e) {
				log.error(e);
			} catch (NoSuchMethodException e) {
				log.debug(e);
			}
		} catch (ClassNotFoundException e) {
			log.error(e);
		}
		
		try {
			datamapClass = Class.forName("com.ailk.common.data.impl.DataMap");
			try {
				if(null != wadeJSONObjectClass){
					fromWadeJSONObjectMethod = datamapClass.getMethod("fromJSONObject", new Class[]{wadeJSONObjectClass});
				}
			} catch (SecurityException e) {
				log.error(e);
			} catch (NoSuchMethodException e) {
				log.debug(e);
			}
		} catch (ClassNotFoundException e) {
			log.error(e);
		}
	}
	
	/**
	 * 判断DatasetList是否使用的com.ailk.common.json.JSONArray类
	 * @return
	 */
	public static boolean isDatasetListUseWadeJSONArray(){
		return wadeJSONArrayClass != null && fromWadeJSONArrayMethod != null;
	}
	
	/**
	 * 判断DataMap是否使用的com.ailk.common.json.JSONObject类
	 * @return
	 */
	public static boolean isDataMapUseWadeJSONObject(){
		return wadeJSONObjectClass != null && fromWadeJSONObjectMethod != null;
	}
	
	/**
	 * 判断object是否是 com.ailk.common.json.JSONArray的实例
	 * @param object
	 * @return
	 */
	public static boolean isWadeJSONArrayInstance(Object obj){
		return wadeJSONArrayClass != null &&  obj != null && wadeJSONArrayClass.isInstance(obj);
	}
	
	/**
	 * 判断object是否是 com.ailk.common.json.JSONObject的实例
	 * @param object
	 * @return
	 */
	public static boolean isWadeJSONObjectInstance(Object obj){
		return wadeJSONObjectClass != null && obj != null && wadeJSONObjectClass.isInstance(obj);
	}
	
	/**
	 * 使用String 构造 com.ailk.common.json.JSONObject的实例
	 * @param map
	 * @return
	 */
	public static Object constructWadeJSONObject(String json){
		if( wadeJSONObjectClass != null && wadeJSONObjectStringConstructor != null ){
			try {
				return wadeJSONObjectStringConstructor.newInstance(new Object[]{json});
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (InstantiationException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			}
		}
		return null;
	}
	
	/**
	 * 使用Map<String, Object> 构造 com.ailk.common.json.JSONObject的实例
	 * @param map
	 * @return
	 */
	public static Object constructWadeJSONObject(Map<String, Object> map){
		if( wadeJSONObjectClass != null && wadeJSONObjectMapConstructor != null ){
			try {
				return wadeJSONObjectMapConstructor.newInstance(new Object[]{map});
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (InstantiationException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			}
		}
		return null;
	}
	
	/**
	 * 调用com.ailk.common.json.JSONArray对象实例的toString方法
	 * @param obj
	 * @return
	 */
	public static String wadeJSONArrayToString(Object obj){
		if( obj != null && isWadeJSONArrayInstance(obj)){
			try {
				return (String)wadeJSONArrayToStringMethod.invoke(obj, new Object[]{});
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			}
		}
		return null;
	}
	
	/**
	 * 调用com.ailk.common.json.JSONObject对象实例的toString方法
	 * @param obj
	 * @return
	 */
	public static String wadeJSONObjectToString(Object obj){
		if( obj != null && isWadeJSONObjectInstance(obj)){
			try {
				return (String)wadeJSONObjectToStringMethod.invoke(obj, new Object[]{});
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			}
		}
		return null;
	}
	
	/**
	 * 从com.ailk.common.json.JSONArray构造DatasetList
	 * @param jsonArray 必须是com.ailk.common.json.JSONArray对象的实例
	 * @return
	 */
	public static DatasetList fromWadeJSONArray(Object jsonArray){
		if( isDatasetListUseWadeJSONArray() ){
			try {
				return (DatasetList)fromWadeJSONArrayMethod.invoke(datasetClass, new Object[]{jsonArray});
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			}
		}
		return null;
	}
	
	/**
	 * 从com.ailk.common.json.JSONObject构造DataMap
	 * @param jsonObject 必须是com.ailk.common.json.JSONObject对象的实例
	 * @return
	 */
	public static DataMap fromWadeJSONObject(Object jsonObject){
		if( isDataMapUseWadeJSONObject() ){
			try {
				return (DataMap)fromWadeJSONObjectMethod.invoke(datamapClass, new Object[]{jsonObject});
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			}
		}
		return null;
	}
}
