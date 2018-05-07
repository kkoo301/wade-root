package com.ailk.common.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ailk.common.data.impl.DataBus;
import com.ailk.common.data.impl.DataContext;
import com.ailk.common.BaseException;
import com.ailk.common.data.IDataBus;
import com.ailk.common.data.IDataContext;

/**
 * Databus
 * 
 * @author Jack Tang
 * @date 2012-5-17
 */
public class DataBus implements IDataBus {
	private static final long serialVersionUID = -1656136640006684705L;
	
	private IDataContext context;
	private JSON data;
	private JSON rawData = null;
	private JSON submitData = null;
	private boolean isArray = false;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param data
	 */
	
	public DataBus(IDataContext context, JSONObject data) {
		this.context = context;
		this.data = data;
		this.isArray = !"".equals(context.getAttr(IDataContext.PAGE_NAME, ""));
		parse();
	}
	
	public DataBus(IDataContext context, JSONArray data) {
		this.context = context;
		this.data = data;
		this.isArray = !"".equals(context.getAttr(IDataContext.PAGE_NAME, ""));
		parse();
	}
	
	public DataBus(IDataContext context, Map data) {
		this.context = context;
		this.data = data == null ? JSONObject.fromObject(new HashMap()) : JSONObject.fromObject(data);
		this.isArray = !"".equals(context.getAttr(IDataContext.PAGE_NAME, ""));
		parse();
	}
	
	public DataBus(IDataContext context, List data) {
		this.context = context;
		this.data = data == null ? JSONArray.fromObject(new ArrayList()) : JSONArray.fromObject(data);
		this.isArray = !"".equals(context.getAttr(IDataContext.PAGE_NAME, ""));
		parse();
	}
	
	private void parse() throws BaseException {
		if (this.data == null) {
			if (isArray){
				this.data = new JSONArray();
			} else {
				this.data = new JSONObject();
			}
		}
		
		if (isArray) {
			try {
				this.submitData = (JSON)((JSONArray) this.data).get(0);
				this.rawData = (JSON)((JSONArray) this.data).get(1);
			} catch (ArrayIndexOutOfBoundsException e) {
				this.rawData = this.data;
				this.submitData = this.data;
			} catch (NullPointerException e) {
				this.rawData = this.data;
				this.submitData = this.data;
			}
		} else {
			this.rawData = this.data;
			this.submitData = this.data;
		}
	}

	public IDataContext getContext() {
		return context;
	}

	public JSON getData() {
		return this.submitData;
	}
	
	public JSON getRawData() {
		return this.rawData;
	}

	/**
	 * @see #context
	 * @param context the context to set
	 */
	protected void setContext(IDataContext context) {
		this.context = context;
	}

	public JSONArray getDataArray() throws BaseException {
		if(this.submitData.isArray()){
			return (JSONArray) this.submitData;
		}
		throw new BaseException("is not JSONArray");
	}
	
	public JSONObject getDataObject() throws BaseException {
		if(!this.submitData.isArray()){
			return (JSONObject)this.submitData;
		}
		throw new BaseException("is not JSONObject");
	}
	
	public JSONArray getRawArray() throws BaseException {
		if(this.rawData.isArray()){
			return (JSONArray)this.rawData;
		}
		throw new BaseException("is not JSONArray");
	}

	public JSONObject getRawObject() throws BaseException {
		if(!this.rawData.isArray()){
			return (JSONObject)this.rawData;
		}
		throw new BaseException("is not JSONObject");
	}
	
	public String getDataString(String key, String defval){
		if(getData()!=null&&!getData().isEmpty()){
			if(getData()instanceof JSONObject){
				try {
					String value = getDataObject().getString(key);
					return value == null ? defval : value;
				} catch (Exception e) {
					return defval;
				}
			}
			return defval;
		} else {
			return defval;
		}
	}
	
	
	public String getRawString(String key, String defval){
		if(getRawData()!=null&&!getRawData().isEmpty()){
			if(getData()instanceof JSONObject){
				try {
					String value = getRawObject().getString(key);
					return value == null ? defval : value;
				} catch (Exception e) {
					return defval;
				}
			}
			return defval;
		} else {
			return defval;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"context\":"+this.context.toString());
		sb.append(",");
		sb.append("\"data\":"+this.data.toString());
		sb.append("}");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		com.ailk.common.data.IData data = new com.ailk.common.data.impl.DataMap();
		data.put("aa", "bb");
		data.put("aabb", "bbcc");
		
		IDataBus bus = new DataBus(new DataContext(), JSONObject.fromObject(data));
		System.out.println(bus.toString());
		
		
		JSONObject data1 = new JSONObject();
		data.put("aa", "bb");
		data.put("aabb", "bbcc");
		
		IDataBus bus1 = new DataBus(new DataContext(), data);
		System.out.println(bus1.toString());
	}
	
}
