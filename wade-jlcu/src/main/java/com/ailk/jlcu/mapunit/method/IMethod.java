/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit.method;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import com.ailk.jlcu.Engine;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.util.JlcuException;

/**
 * 方法(接口)
 * 
 * @author steven zhou
 * @since 1.0
 */
public interface IMethod {

	/**
	 * 方法类型枚举
	 * 
	 * @author steven zhou
	 * @since 1.0
	 */
	public static enum MethodType {
		JAVA, SUBFLOW, HTTP, SERVICE, EXPRESS, RULEFLOW
	}

	/** 获取方法类型 */
	public abstract MethodType getMethodType();

	/** 获取方法入参集合 */
	public List<Varmap> getInVars();

	/** 获取方法出参 */
	public Varmap getOutVar();
	
	public String getName();

	/** 正向方法执行 */
	public void execute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException;
	
	/** 补偿方法执行,和正向共用逻辑 */
	public Object execute(Engine engine, ScriptEngine scriptEngine, Object[] params, Class[] paramTypes) throws Exception;
}
