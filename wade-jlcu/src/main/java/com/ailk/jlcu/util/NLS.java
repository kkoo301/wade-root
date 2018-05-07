package com.ailk.jlcu.util;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class NLS {
	
	private static final Logger log = Logger.getLogger(NLS.class);
	
	static {
		NLS.initializeMessages(JlcuMessages.class.getName(), NLS.class.getName());
	}
	
	static void initializeMessages(String bundleName, String className) {
		//locale.getLanguage()
		ResourceBundle messages = ResourceBundle.getBundle(bundleName, Locale.getDefault());
		try {
			Class cls = Class.forName(className);
			Field fields[] = cls.getDeclaredFields();
			String name = null, value = null;
			for(int i = 0, len = fields.length; i < len; i++){
				try{
					name = fields[i].getName();
					value = messages.getString(name);
					value = new String(value.getBytes(Constant.ISO_8859_1), Constant.ENCODE);
					fields[i].set(name, value);
				}catch(MissingResourceException me){
					value = null;
				}
			}
		} catch (Exception e) {
			JlcuUtility.log(log, e);
		}
    }
	
	//EngineFactory
	static String INSTANCE_EXIST_FIELD;
	static String INSTANTIATION_EXCEP;
	static String ILLEGAL_ACCESS_EXCEP;
	
	//Engine
	static String PARAM_NOT_NULL;
	static String JLCU_NOT_NULL;
	static String CASE_IS_BOOLEAN;
	static String SWTICH_HAS_NO_DEFAULT;
	
	//JavaMethod
	static String BUFF_IS_NULL;
	
	static String NODE_EXCEP;
	static String JAVA_EXCEP;
	static String EXPRESS_EXCEP;
	static String SUBFLOW_EXCEP;
	static String HTTP_EXCEP;
	static String WS_EXCEP;
	
	public static void main(String[] args) {
		System.out.print(JAVA_EXCEP);
	}
}
