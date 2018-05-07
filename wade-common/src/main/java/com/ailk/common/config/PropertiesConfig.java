package com.ailk.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ailk.common.config.PropertiesConfig;
import com.ailk.common.BaseException;


public class PropertiesConfig {

	private Properties	props		= null;

	/**
	 * construct function
	 * 
	 * @param in
	 * @throws Exception
	 */
	public PropertiesConfig(InputStream in) {
		props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseException("config-10000", e);
		}
		
		if (props == null) {
			throw new BaseException("config-10001");
		}
	}

	/**
	 * construct function
	 * 
	 * @param file
	 * @throws Exception
	 */
	public PropertiesConfig(String file) {
		this(PropertiesConfig.class.getClassLoader().getResourceAsStream(file));
	}

	/**
	 * construct function
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public PropertiesConfig(File file) {
		props = new Properties();
		try {
			InputStream in = new FileInputStream(file);
			props.load(in);
		} catch (FileNotFoundException e) {
			throw new BaseException("config-10001", e);
		} catch (IOException e) {
			throw new BaseException("config-10002", e);
		}
	}

	public String getProperty(String prop) {
		String value = props.getProperty(prop);
		try {
			return new String(value.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			
		}
		return value;
	}

	public String getProperty(String prop, String defval) {
		String value = getProperty(prop);
		if (value == null) {
			return defval;
		}
		return value;
	}

	/**
	 * get properties
	 * 
	 * @return IData
	 * @throws Exception
	 */
	public Map<String, String> getProperties() {
		Map<String, String> data = new HashMap<String, String>();

		Enumeration<Object> e = props.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			data.put(key, getProperty(key));
		}

		return data;
	}
}
