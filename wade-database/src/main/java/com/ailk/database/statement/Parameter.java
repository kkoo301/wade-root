package com.ailk.database.statement;

import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;

public class Parameter {

	private List<Object> list = new ArrayList<Object>();

	/**
	 * default construct function
	 * @throws Exception
	 */
	public Parameter() throws Exception {
		super();
	}
	
	/**
	 * construct function
	 * @param params
	 * @throws Excetion
	 */
	public Parameter(Object[] params) throws Exception {
		for (Object param : params) {
			list.add(param);
		}
	}
	
	/**
	 * get value
	 * @param index
	 * @return Object
	 * @throws Exception
	 */
	public Object get(int index) throws Exception {
		return list.get(index);
	}
	
	/**
	 * add value
	 * @param value
	 * @throws Exception
	 */
	public void add(Object value) throws Exception {
		list.add(value);
	}
	
	/**
	 * add value
	 * @param index
	 * @param value
	 * @throws Exception
	 */
	public void add(int index, Object value) throws Exception {
		list.add(index, value);
	}
	
	/**
	 * add param
	 * @param param
	 * @throws Exception
	 */
	public void addAll(Parameter param) throws Exception {		
		for (int i = 0, size = param.size(); i < size; i++) {
			list.add(param.get(i));
		}
	}
	
	/**
	 * get size
	 * @return 
	 * @throws Exception
	 */
	public int size() throws Exception {
		return list.size();
	}
	
	public String[] getValues() {
		int cnt = list.size();
		String[] values = new String[cnt];
		for (int i = 0; i < cnt; i++) {
			Object value = list.get(i);
    		if (null == value) {
    			values[i] = "";
    		} else {
    			if (value instanceof StringReader) {
    				values[i] = ((StringReader) value).toString();
    			} else if (value instanceof Integer) {
    				values[i] = "" + value;
    			} else {
    				values[i] = (String) value;
    			}
    		}
		}
		return values;
	}
	
	/**
	 * to string
	 * @return String
	 * @throws Exception
	 */
	public String toString() {
		return list.toString();
	}

}
