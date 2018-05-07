/**
 * 
 */
package com.ailk.service.invoker;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.util.ClazzUtil;

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
		IMethodIntercept intercept = methods.get(clazz);
		
		if (intercept == null) {
			intercept = (IMethodIntercept) ClazzUtil.load(clazz, null);
			if (intercept == null) {
				throw new Exception ("找不到Method拦截器[" + clazz + "]");
			}
			methods.put(clazz, intercept);
		}
		
		return intercept;
	}

}
