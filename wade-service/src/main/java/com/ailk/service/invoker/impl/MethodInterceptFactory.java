/**
 * 
 */
package com.ailk.service.invoker.impl;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.util.ClazzUtil;
import com.ailk.service.invoker.IMethodIntercept;

/**
 * @author yifur
 *
 */
public final class MethodInterceptFactory {
	
	private static Map<String, IMethodIntercept> methods = new HashMap<String, IMethodIntercept>();
	
	static MethodInterceptFactory factory = new MethodInterceptFactory();
	
	private MethodInterceptFactory(){
		
	}
	
	
	/**
	 * 获取方法拦截器
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static IMethodIntercept getMethodIntercept(String clazz) throws Exception {
		if (methods.containsKey(clazz))
			return methods.get(clazz);
		else {
			IMethodIntercept intercept = (IMethodIntercept) ClazzUtil.load(clazz, null);
			if (intercept == null) {
				throw new Exception ("找不到Method拦截器[" + clazz + "]");
			}
			return intercept;
		}
	}

}
