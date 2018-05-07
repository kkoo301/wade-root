package com.ailk.service.protocol.config;

import java.util.HashMap;
import java.util.Map;

import com.ailk.service.protocol.config.impl.DefaultParamObfuscator;

public class ObfuscatorFactory {

	public static final String TYPE_PSPT = "pspt";
	public static final String TYPE_NAME = "name";
	public static final String TYPE_MBPHONE = "mbphone";

	public static final String DEFAULT = DefaultParamObfuscator.class.getName();

	private static Map<String, IObfuscator> obfuscators = new HashMap<String, IObfuscator>();

	public static IObfuscator create(String className) {
		if (obfuscators.containsKey(className)) {
			return obfuscators.get(className);
		} else {
			try {
				if (className == null)
					return null;

				if (useDefault(className))
					className = DEFAULT;

				Class<?> c = Class.forName(className);
				Object object = c.newInstance();
				IObfuscator obfuscator = (IObfuscator) object;
				obfuscators.put(className, obfuscator);
				return obfuscator;
			} catch (ClassCastException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	private static boolean useDefault(String type) {
		return type != null && (type.equals(TYPE_MBPHONE) || type.equals(TYPE_NAME) || type.equals(TYPE_PSPT));
	}

}
