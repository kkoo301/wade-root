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

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.jlcu.Engine;
import com.ailk.jlcu.FlowChart;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;
import com.ailk.service.Context;
import com.ailk.service.client.ServiceFactory;

/**
 * HTTP方法
 * 
 * @author steven zhou
 * @since 1.0
 */
public class HttpMethod extends AbstractMethod {
	
	private static final Logger log = Logger.getLogger(HttpMethod.class);
	
	private String xTransCode;

	public HttpMethod(List inVars, Varmap outVar, String xTransCode) {
		super(inVars, outVar);
		this.xTransCode = xTransCode;
	}

	public MethodType getMethodType() {
		return MethodType.HTTP;
	}

	/**
	 * 执行HTTP方法
	 */
	public Object subExecute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException {
		try {
			
			IData param = null;
			List<Varmap> inVars = this.getInVars();
			
			String mapname = null; 
			for (Varmap v : inVars) {
				mapname = v.getMapname();
				Object obj = null;
				//判断传参方式:引用还是值
				obj = databus.get(mapname);
				
				if (obj instanceof DataMap) {
					param = (IData) obj;
				} else{
					throw new JlcuException("the parameter types of http method are wrong");
				}
			}
			log(param);
			
			Context ctx = (Context)databus.get(FlowChart.CONTEXT);
			Object result =  ServiceFactory.call(xTransCode, ctx.createDataInput(param));
			return result;
		} catch (Exception e) {
			JlcuUtility.error(JlcuMessages.HTTP_EXCEP.bind(xTransCode), e);
		}
		return null;
	}
	
	@Override
	public Object execute(Engine engine, ScriptEngine scriptEngine, Object[] params, Class[] paramTypes) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void log(IData param) {
		if (!log.isDebugEnabled()) {
			return;
		}
		
		JlcuUtility.log(log, "JLCU execute Http: " + xTransCode);
		JlcuUtility.log(log, "PARAM " + param);
	}
	
	public String getName(){
		return xTransCode;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
}
