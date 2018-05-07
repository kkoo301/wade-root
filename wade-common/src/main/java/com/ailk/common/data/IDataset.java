package com.ailk.common.data;

import java.io.Serializable;
import java.util.List;

public interface IDataset extends List<Object>, Serializable {
	
	/**
	 * Sort order Constants
	 */
	public static final int	ORDER_ASCEND	= 0;

	public static final int	ORDER_DESCEND	= 1;

	/**
	 * Key type Constants
	 */
	public static final int	TYPE_STRING		= 2;

	public static final int	TYPE_INTEGER	= 3;

	public static final int	TYPE_DOUBLE		= 4;

	public static final int	MAX_RECORD		= 2000;

	/**
	 * get names
	 * 
	 * @return String[]
	 */
	public String[] getNames();
	
	/**
	 * get object
	 * @param index
	 * @return Object
	 */
	public Object get(int index);
	
	/**
	 * get object
	 * @param index
	 * @param name
	 * @return Object
	 */
	public Object get(int index, String name);
	
	/**
	 * get object
	 * @param index
	 * @param name
	 * @param def
	 * @return Object
	 */
	public Object get(int index, String name, Object def);
	
	/**
	 * get data
	 * @param index
	 * @return IData
	 */
	public IData getData(int index);
	
	/**
	 * get dataset
	 * @param index
	 * @return IDataset
	 */
	public IDataset getDataset(int index);
	
	/**
	 * first
	 * 
	 * @return IData
	 */
	public IData first();
	
	/**
	 * to data
	 * @return IData
	 */
	public IData toData();
	
	
}