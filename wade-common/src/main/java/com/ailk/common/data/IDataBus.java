package com.ailk.common.data;

import java.io.Serializable;

import com.ailk.common.data.IDataContext;
import com.ailk.common.BaseException;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface IDataBus extends Serializable {
	
	public IDataContext getContext();
	
	public JSON getData();

	public JSONArray getDataArray() throws BaseException;
	
	public JSONObject getDataObject() throws BaseException;
	
	public JSON getRawData();
	
	public JSONArray getRawArray() throws BaseException;
	
	public JSONObject getRawObject() throws BaseException;
	
	public String getDataString(String key, String defval);
	
	public String getRawString(String key, String defval);
	
	public String toString();

}
