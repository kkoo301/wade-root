package com.ailk.jlcu;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;

public class EngineFactory {
	
	private static final Logger log = Logger.getLogger(EngineFactory.class);
	private static Map<String, Class> classMap = new HashMap<String, Class>();
	private static Map beans = new HashMap();
	private static String FINAL = "final ";
	
	public static Engine createEngine(){
		return new Engine(); 
	}
	
	public static Object createInstance(String className) throws JlcuException, ClassNotFoundException {
		Class clazz = EngineFactory.getClass(className);
		
		if (beans.containsKey(className)) {
			return beans.get(className);
		} else {
			try {
				Object instance = clazz.newInstance();
				/*校验是否有全局变量的的逻辑*/
				if (checkExistField(clazz)) {
					JlcuUtility.log(log, clazz.getName()+JlcuMessages.INSTANCE_EXIST_FIELD);
				} else {
					beans.put(className, clazz.newInstance());
					JlcuUtility.log(log, clazz.getName()+"的实例被缓存");
				}
				
				return instance;
			} catch (InstantiationException e) {
				JlcuUtility.error(JlcuMessages.INSTANTIATION_EXCEP.bind(clazz.getName()));
			} catch (IllegalAccessException e) {
				JlcuUtility.error(JlcuMessages.ILLEGAL_ACCESS_EXCEP.bind(clazz.getName()));
			}
		}
		return null;
	}
	
	/**
	 * 判断是否存在全局变量
	 */
	public static boolean checkExistField(Class clazz) {
		// TODO Auto-generated method stub
		if (isExistField(clazz)) {
			return true;
		}
		
		if (clazz.getSuperclass() != null) {
			if (checkExistField(clazz.getSuperclass())) {
				return true;
			}
		}
		
		Class classes[] = clazz.getInterfaces();
		for (Class intClass : classes) {
			if (isExistField(intClass)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isExistField(Class clazz) {
		// TODO Auto-generated method stub
		Field fields[] = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!field.toString().contains(FINAL)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 得到类对象
	 */
	public static Class getClass(String key) throws ClassNotFoundException {
		Class clas = classMap.get(key);
		if (null == clas) {
			clas = Class.forName(key);
			putClass(key, clas);
		}
		return clas;
	}
	
	private static void putClass(String key,Class clas) {
		if (classMap.size() < 1000) {
			classMap.put(key,clas);
		}
	}
}
