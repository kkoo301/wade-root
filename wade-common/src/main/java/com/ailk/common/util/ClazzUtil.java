package com.ailk.common.util;

import org.apache.log4j.Logger;

import com.ailk.common.util.ClazzUtil;

public final class ClazzUtil {
	
	private static transient final Logger log = Logger.getLogger(ClazzUtil.class);

	private ClazzUtil() {}
	
	/**
	 * load
	 * @param clazzName
	 * @param defaultObj
	 * @return
	 */
	public static Object load(String clazzName, Object defaultObj) {
		try {
			if (null == clazzName || clazzName.length() == 0) {
				throw new ClassNotFoundException("class is empty");
			}

			Class<?> clazz = ClazzUtil.class.getClassLoader().loadClass(clazzName);
			return clazz.newInstance();
		} catch (ClassNotFoundException e) {
			if (log.isInfoEnabled())
				log.info("load class [" + clazzName + "], use default " + defaultObj.getClass().getName());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return defaultObj;
	}

}
