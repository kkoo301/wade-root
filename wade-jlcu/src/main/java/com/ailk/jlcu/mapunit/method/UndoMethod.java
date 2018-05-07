package com.ailk.jlcu.mapunit.method;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import com.ailk.jlcu.Engine;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.util.JlcuException;

public class UndoMethod implements IMethod{
	private IMethod method;
	private Object[] params;
	private Class[] paramTypes;

	public UndoMethod(IMethod method,Object[] params, Class[] paramTypes) {
		this.method = method;
		this.params = params;
		this.paramTypes = paramTypes;
	}
	
	@Override
	public MethodType getMethodType() {
		// TODO Auto-generated method stub
		return method.getMethodType();
	}

	@Override
	public List<Varmap> getInVars() {
		// TODO Auto-generated method stub
		return method.getInVars();
	}

	@Override
	public Varmap getOutVar() {
		// TODO Auto-generated method stub
		return method.getOutVar();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return method.getName();
	}

	@Override
	public void execute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException {
		// TODO Auto-generated method stub
		throw new JlcuException("补偿方法不能执行该方法");
	}

	@Override
	public Object execute(Engine engine, ScriptEngine scriptEngine,	Object[] params, Class[] paramTypes) throws Exception {
		// TODO Auto-generated method stub
		throw new JlcuException("补偿方法不能执行该方法");
	}
	
	public Object execute(Engine engine, ScriptEngine scriptEngine) throws Exception {
		return method.execute(engine, scriptEngine, params, paramTypes);
	}
	
	public IMethod getMethod() {
		return method;
	}
	
	public Object[] getParams() {
		return params;
	}
	
	public Class[] getParamTypes() {
		return paramTypes;
	}
}
