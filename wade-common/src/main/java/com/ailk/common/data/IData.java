package com.ailk.common.data;

import java.util.Map;
import java.io.Serializable;
import com.ailk.common.data.IData;

public interface IData extends Map<String, Object>, Serializable {
	
	/**
	 * name is null or not exist
	 * @param name
	 * @return boolean
	 */
	public boolean isNoN(String name);
	
	/**
	 * get names
	 * @return String[]
	 */
	public String[] getNames();
	
	
	/**
	 * get
	 * @param name
	 * @return
	 */
	public String getString(String name);
	
	/**
	 * get string
	 * @param name
	 * @param defaultValue
	 * @return String
	 */
	public String getString(String name, String defaultValue);
	
	/**
	 * get boolean
	 * @param name
	 * @return boolean
	 */
	public boolean getBoolean(String name);
	
	/**
	 * get boolean
	 * @param name
	 * @param defaultValue
	 * @return boolean
	 */
	public boolean getBoolean(String name, boolean defaultValue);
	
	/**
	 * get int
	 * @param name
	 * @return int
	 */
	public int getInt(String name);
	
	/**
	 * get int
	 * @param name
	 * @param defaultValue
	 * @return int
	 */
	public int getInt(String name, int defaultValue);
	
	/**
	 * get long
	 * @param name
	 * @return long
	 */
	public long getLong(String name);
	
	/**
	 * get long
	 * @param name
	 * @param defaultValue
	 * @return long
	 */
	public long getLong(String name, long defaultValue);
	
	/**
	 * get double
	 * @param name
	 * @return double
	 */
	public double getDouble(String name);
	
	/**
	 * get double
	 * @param name
	 * @param defaultValue
	 * @return double
	 */
	public double getDouble(String name, double defaultValue);
	
	
	/**
	 * get dataset
	 * @param name
	 * @return
	 */
	public IDataset getDataset(String name);
	
	/**
	 * get dataset
	 * @param name
	 * @return
	 */
	public IDataset getDataset(String name, IDataset def);
	
	/**
	 * get data
	 * @param name
	 * @return
	 */
	public IData getData(String name);
	
	/**
	 * get data
	 * @param name
	 * @return
	 */
	public IData getData(String name, IData def);
	
	/**
	 * sub data
	 * @param group
	 * @return IData
	 * @throws Exception
	 */
	public IData subData(String group) throws Exception;
	
	/**
	 * sub data
	 * @param group
	 * @param istrim
	 * @return IData
	 * @throws Exception
	 */
	public IData subData(String group, boolean istrim) throws Exception;
	
	/**
	 * to string
	 * @return String
	 */
	public String toString();
	
}