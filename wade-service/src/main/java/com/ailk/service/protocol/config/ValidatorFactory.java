package com.ailk.service.protocol.config;

import java.util.HashMap;
import java.util.Map;

import com.ailk.service.protocol.config.impl.DefaultParamValidator;

public class ValidatorFactory {
	public static final String TYPE_PSPT = "pspt";
	public static final String TYPE_IP = "ip";
	public static final String TYPE_MAIL = "mail";
	public static final String TYPE_NUMBER = "number";
	public static final String TYPE_STRING = "string";
	public static final String TYPE_MAC = "mac";
	public static final String TYPE_DATE = "date";
	public static final String TYPE_MBPHONE = "mbphone";

	public static final String DEFAULT = DefaultParamValidator.class.getName();

	private static Map<String, IValidator> validators = new HashMap<String, IValidator>();

	public static IValidator create(String className) {
		if (validators.containsKey(className)) {
			return validators.get(className);
		} else {
			try {
				if (className == null)
					return null;

				if (useDefault(className))
					className = DEFAULT;

				Class<?> c = Class.forName(className);
				Object object = c.newInstance();
				IValidator validator = (IValidator) object;
				validators.put(className, validator);
				return validator;
			} catch (ClassCastException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
		return type != null
				&& (type.equals(TYPE_IP) || type.equals(TYPE_MAIL) || type.equals(TYPE_PSPT)
						|| type.equals(TYPE_STRING) || type.equals(TYPE_MAC) || type.equals(TYPE_DATE)
						|| type.equals(TYPE_MBPHONE) || type.equals(TYPE_NUMBER));
	}
}
