package com.ailk.jlcu.mapunit.method;

import java.util.Map;

import javax.script.ScriptEngine;

import org.apache.log4j.Logger;

import com.ailk.jlcu.Engine;
import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;

public class ExpressMethod extends AbstractMethod {
	private static final Logger log = Logger.getLogger(ExpressMethod.class);
	
	private String scripts;
	
	public ExpressMethod(String scripts) {
		super(null,null);
		this.scripts = scripts;
	}

	@Override
	public Object subExecute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException {
		try {
			log();
			return execute(engine, scriptEngine, null, null);
		} catch (Exception e) {
			String s = getName();
			JlcuUtility.error(JlcuMessages.EXPRESS_EXCEP.bind(s), e);
		}
		return null;
	}
	
	@Override
	public Object execute(Engine engine, ScriptEngine scriptEngine, Object[] params, Class[] paramTypes) throws Exception {
		// TODO Auto-generated method stub
		return scriptEngine.eval(scripts);
	}

	public MethodType getMethodType() {
		return MethodType.EXPRESS;
	}
	
	private void log() {
		if (!log.isDebugEnabled()) {
			return;
		}
		JlcuUtility.log(log,"JLCU execute expression: " + scripts);
	}
	
	public String getName(){
		return scripts.length()<=10?scripts:scripts.substring(0,10)+"...";
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
}
