/**
 * $
 */
package com.wade.httprpc.server.rmi;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.wade.httprpc.server.rmi.MethodParam;
import com.wade.httprpc.server.rmi.ServiceClassParser;
import com.wade.httprpc.server.rmi.ServiceImplClassInfo;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: InterfaceParser.java
 * @description: 解析Service实现类，遍历每个方法名，并获取方法形参信息
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public final class ServiceClassParser {
	
	private static Map<Class<?>, ServiceImplClassInfo> serviceClassPool = new HashMap<Class<?>, ServiceImplClassInfo>(10000);

	private static ClassPool pool = ClassPool.getDefault();
	
	private static Object lock = new Object();
	
	private ServiceClassParser() {
		
	}
	
	
	/**
	 * 解析服务实现类
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static ServiceImplClassInfo parse(Class<?> clazz) throws Exception {
		ServiceImplClassInfo classInfo = serviceClassPool.get(clazz);
		
		if (null != classInfo)
			return classInfo;
		
		synchronized (lock) {
			classInfo = serviceClassPool.get(clazz);
			if (null == classInfo) {
				
				classInfo = new ServiceImplClassInfo();
				classInfo.setClassName(clazz.getName());
				
				Map<Method, MethodParam[]> methodParams = new HashMap<Method, MethodParam[]>(20);
				
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					MethodParam[] params = methodParams(clazz, method);
					methodParams.put(method, params);
				}
				
				classInfo.setMethods(methodParams);
				
				serviceClassPool.put(clazz, classInfo);
			}
		}
		
		return classInfo;
	}
	
	
	/**
	 * 解析方法形参
	 * @param clazz
	 * @param method
	 * @return
	 * @throws Exception
	 */
	private static MethodParam[] methodParams(Class<?> clazz, Method method) throws Exception {
		CtClass ctClass = pool.get(clazz.getName());
		CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
		
		MethodInfo methodInfo = ctMethod.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
		
		Class<?>[] types = method.getParameterTypes();
		
		MethodParam[] params = new MethodParam[types.length];
		
		int i = 0;
		for (Class<?> type : types) {
			MethodParam param = new MethodParam();
			param.setParamName(attr.variableName(i + 1));
			param.setParamType(type);
			
			params[i] = param;
			i++;
		}
		
		return params;
	}
	
	static {
		pool.insertClassPath(new ClassClassPath(ServiceClassParser.class));
	}

	
	public static void main(String[] args) throws Exception {
		
	}
}
