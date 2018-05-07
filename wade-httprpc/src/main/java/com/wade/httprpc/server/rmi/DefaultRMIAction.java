/**
 * $
 */
package com.wade.httprpc.server.rmi;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.wade.httprpc.server.rmi.DefaultRMIAction;
import com.wade.httprpc.server.rmi.MethodParam;
import com.wade.httprpc.server.rmi.ServiceClassParser;
import com.wade.httprpc.server.rmi.ServiceImplClassInfo;
import com.wade.httprpc.server.IHttpAction;
import com.wade.httprpc.server.rmi.config.RMIServiceConfig;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestRMIAction.java
 * @description: 默认的远程调用实现方法
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public class DefaultRMIAction implements IHttpAction<HashMap<String, Serializable>, HashMap<String, Serializable>>{
	
	private static final transient Logger log = Logger.getLogger(DefaultRMIAction.class);
	
	private static final String DEFAULT_SERVICE_METHOD = "service";
	
	@Override
	public HashMap<String, Serializable> execute(HttpServletRequest url, HashMap<String, Serializable> request) {
		String serviceName = (String) request.get("SERVICE_NAME");
		
		if (log.isDebugEnabled()) {
			log.debug("接收服务名：" + serviceName);
			log.debug("接收服务入参：" + request);
		}
		
		//根据服务名获取实现类
		try {
			
			if (null == serviceName || serviceName.length() == 0) {
				throw new Exception("请求入参里找不到服务名称");
			}
			
			Class<?> clazz = RMIServiceConfig.getServiceImplClass(serviceName);
			if (null == clazz) {
				throw new Exception("找不到服务实现类" + serviceName);
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("匹配服务%s实现类%s", serviceName, clazz.getName()));
			}
			
			ServiceImplClassInfo classInfo = ServiceClassParser.parse(clazz);
			if (null == classInfo) {
				throw new Exception("服务实现类解析异常," + clazz.getName());
			}
			
			//获取服务参数列表
			Map<Method, MethodParam[]> methods = classInfo.getMethods();
			Iterator<Method> iter = methods.keySet().iterator();
			MethodParam[] params = null;
			Method serviceMethod = null;
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getName().equals(DEFAULT_SERVICE_METHOD)) {
					params = methods.get(method);
					serviceMethod = method;
					break;
				}
			}
			
			if (null == params) {
				throw new Exception("找不到服务参数信息" + clazz.getName());
			}
			
			if (null == serviceMethod) {
				throw new Exception(String.format("服务%s找不到实现方法%s", clazz.getName(), DEFAULT_SERVICE_METHOD));
			}
			
			return invoke(clazz, serviceMethod, params, request);
		} catch (Exception e) {
			return error(request, e);
		}
	}
	
	@Override
	public HashMap<String, Serializable> error(HashMap<String, Serializable> request, Exception e) {
		log.error("服务调用异常" + request, e);
		
		HashMap<String, Serializable> map = createResponse(null);
		
		map.put("X_RESULTCODE", "-1");
		map.put("X_RESULTINFO", e.getMessage());
		map.put("X_EXCEPTION", e);
		
		return map;
	}
	

	/**
	 * 反射Java方法
	 * @param clazz
	 * @param metohd
	 * @param params
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, Serializable> invoke(Class<?> clazz, Method metohd, MethodParam[] params, Map<String, Serializable> request) throws Exception {
		Object obj = clazz.newInstance();
		
		Object[] args = new Object[params.length];
		
		int i = 0;
		for (MethodParam param : params) {
			Class<?> type = param.getParamType();
			
			if ("boolean".equals(type.getName())) {
				args[i] = "true".equals((String) request.get(param.getParamName()));
				i++;
				continue;
			} else if ("int".equals(type.getName())) {
				String value = (String) request.get(param.getParamName());
				if (null == value || value.length() == 0) {
					args[i] = 0;
				} else {
					args[i] = Integer.parseInt(value);
				}
				i++;
				continue;
			} else if ("long".equals(type.getName())) {
				String value = (String) request.get(param.getParamName());
				if (null == value || value.length() == 0) {
					args[i] = 0L;
				} else {
					args[i] = Long.parseLong(value);
				}
				i++;
				continue;
			} else if ("double".equals(type.getName())) {
				String value = (String) request.get(param.getParamName());
				if (null == value || value.length() == 0) {
					args[i] = 0D;
				} else {
					args[i] = Double.parseDouble(value);
				}
				i++;
				continue;
			} else if ("float".equals(type.getName())) {
				String value = (String) request.get(param.getParamName());
				if (null == value || value.length() == 0) {
					args[i] = 0F;
				} else {
					args[i] = Float.parseFloat(value);
				}
				i++;
				continue;
			} else {
				args[i] = request.get(param.getParamName());
				i++;
				continue;
			}
		}
		
		HashMap<String, Serializable> map = createResponse((Serializable) metohd.invoke(obj, args));
		map.put("X_RESULTCODE", "0");
		map.put("X_RESULTINFO", "OK");
		return map;
	}
	
	
	private HashMap<String, Serializable> createResponse(Serializable rtn) {
		HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("X_RESULTOBJ", rtn);
		return map;
		
	}
}
