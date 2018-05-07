package com.ailk.service.bean;

import java.util.HashMap;
import java.util.Map;

import com.ailk.service.protocol.ServiceException;

public class BeanManager {

	BeanManager mananger = new BeanManager();
	private static Map<String, IBaseBean> beans = new HashMap<String, IBaseBean>(100000);
	
	private BeanManager() {
		
	}
	
	@SuppressWarnings("unchecked")
	public static <Type extends IBaseBean>Type createBean(Class<Type> clazz) throws Exception {
		String clazzName = clazz.getName();
		if (beans.containsKey(clazzName)) {
			return (Type)beans.get(clazzName);
		} else {
			try {
				Type bean = (Type) clazz.newInstance();
				beans.put(clazzName, bean);
				return bean;
			} catch (InstantiationException e) {
				throw new ServiceException("无法创建服务实例[" + clazzName + "]", e);
			} catch (IllegalAccessException e) {
				throw new ServiceException("服务实例访问异常[" + clazzName + "]", e);
			}
		}
	}
	
}
