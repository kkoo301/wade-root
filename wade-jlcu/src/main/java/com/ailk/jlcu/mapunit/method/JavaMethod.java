/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com 
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit.method;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.apache.log4j.Logger;

import com.ailk.jlcu.Engine;
import com.ailk.jlcu.EngineFactory;
import com.ailk.jlcu.FlowChart;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.util.Busybox;
import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;

/**
 * Java方法
 * 
 * @author steven zhou
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class JavaMethod extends AbstractMethod {
	
	private static final Logger log = Logger.getLogger(JavaMethod.class);
	
	/** 类路径 */
	private String className;
	
	/** 方法名 */
	private String methodName;

	public JavaMethod(List<Varmap> inVars, Varmap outVar, String className, String methodName) {
		super(inVars, outVar);
		this.className = className;
		this.methodName = methodName;
	}

	public MethodType getMethodType() {
		return MethodType.JAVA;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	/** 基于反射机制执行Java方法 */
	public Object subExecute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException {
		try {
			List<Varmap> inVars = this.getInVars();
			Class[] paramsType = null;
			Object[] params = null;
			if (inVars.size() > 0) {
				paramsType = new Class[inVars.size()];
				params = new Object[inVars.size()];
			}
			
			int i = 0;
			String mapname;
			Class buffType;
			for (Varmap v : inVars) {
				mapname = v.getMapname();
				/*判断传参方式:引用还是值*/
				params[i] = getDatabusValue(databus, v);
				buffType = ((Map<String, Class>)databus.get(FlowChart.BUFF_TYPE)).get(mapname);

				/*使用数据总线中的类型来决定入参类型*/
				if (buffType == null) {
					JlcuUtility.error(JlcuMessages.BUFF_IS_NULL.bind(mapname));
				}
				paramsType[i] = buffType;
				if (params[i] == null) {
					/*对知道入参类型的buff初始化*/
					params[i] = newDatabusValue(databus, buffType, v);
				}
				i++;
			}
			
			return execute(engine, scriptEngine, params, paramsType);
		} catch (Exception e) {
			JlcuUtility.error(JlcuMessages.JAVA_EXCEP.bind(methodName), e);
		}
		return null;
	}
	
	/**
	 * 该方法可以提供给补偿方法使用
	 */
	public Object execute(Engine engine, ScriptEngine scriptEngine, Object[] params, Class[] paramsType) throws Exception {
		Class cls = EngineFactory.getClass(className);
		
		if (log.isDebugEnabled()) {
			log.debug("[JLCU]" + className + "@" + methodName + " params...");
			if (paramsType != null) {
				for (Class c : paramsType) {
					log.debug("paramsType:" + c.getName());
				}
			} else {
				log.debug("paramsType is Empty");
			}
		}
		
		Method method = cls.getMethod(methodName, paramsType);
		
		Object instance = EngineFactory.createInstance(className);
		Object result = method.invoke(instance, params);
		/**如果出参和入参中某一个引用相同,则深度clone*/
		for (Object param : params) {
			if (param == result) {
				result = Busybox.deepClone(param);
				break;
			}
		}
		
		log(params);
		return result;
	}

	public String toString() {
		String display = "JavaMethod: className=" + className + " methodName=" + methodName;
		return display;
	}

	private void log(Object[] params) {
		if (!log.isDebugEnabled()) {
			return;
		}
		
		JlcuUtility.log(log,"JLCU execute Java: " + className + "@" + methodName);
		for (Object obj : params) {
			JlcuUtility.log(log,"PARAM:" + obj);
		}
	}
	
	public String getName(){
		return getClassName()+":"+getMethodName();
	}
}