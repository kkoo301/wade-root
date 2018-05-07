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
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.jlcu.Engine;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;
import com.ailk.service.client.ServiceFactory;
import com.ailk.service.session.SessionManager;

/**
 * WebService方法
 * 
 * @author steven zhou
 * @since 1.0
 */
public class WSMethod extends AbstractMethod {
	
	private static final Logger log = Logger.getLogger(WSMethod.class);
	private static final String _SERVICE_RETURN_ = "_SERVICE_RETURN_";

	/** WebService方法逻辑服务名 */
	private String xTransCode;
	
	public WSMethod(List inVars, Varmap outVar, String xTransCode) {
		super(inVars, outVar);
		this.xTransCode = xTransCode;
	}

	public MethodType getMethodType() {
		return MethodType.SERVICE;
	}

	/**
	 * 执行WebService方法调用
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
				
				if(obj instanceof DataMap) {
					param = (IData) obj;
				} else{
					throw new JlcuException("the parameter types of service method are wrong");
				}
			}
			log(param);
			
			IDataInput dataInput = DataHelper.createDataInput(SessionManager.getInstance().getVisit(), param, null);
			IDataOutput dataOutput = ServiceFactory.call(xTransCode, dataInput);
			
			if ("1".equals(param.getString(_SERVICE_RETURN_))) {
				return dataOutput;
			} else {
				return dataOutput.getData();
			}
		} catch (Exception e) {
			JlcuUtility.error(JlcuMessages.WS_EXCEP.bind(xTransCode), e);
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
		
		JlcuUtility.log(log, "JLCU execute service: " + xTransCode);
		JlcuUtility.log(log, "call service param : " + param);
		
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
