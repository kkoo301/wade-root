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

import com.ailk.jlcu.Engine;
import com.ailk.jlcu.FlowChart;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.util.Busybox;
import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;


/**
 * 子流程方法
 * 
 * @author steven zhou
 * @since 1.0
 * 子流程方法作为补偿方法的时候，其内部不能再次存在补偿方法。否则此补偿方法会被执行。
 */
public class SubFlowMethod extends AbstractMethod {
	
	private static final Logger LOG = Logger.getLogger(SubFlowMethod.class);
	
	/** 子流程逻辑服务名 */
	private String path;

	public SubFlowMethod(List<Varmap> inVars, Varmap outVar, String path) {
		super(inVars, outVar);
		this.path = path;
	}

	public MethodType getMethodType() {
		return MethodType.SUBFLOW;
	}

	public String getPath() {
		return path;
	}

	/** 执行子流程 */
	@SuppressWarnings("unchecked")
	public Object subExecute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException {
		try {
			List<Varmap> inVars = getInVars();
			Object[] params = null;
			if (inVars.size() > 0) {
				params = new Object[inVars.size()];
			}
			
			int i = 0;
			String mapname = null;
			for (Varmap v : inVars) {
				mapname = v.getMapname();
				params[i] = getDatabusValue(databus, v);
				
				if (null == params[i]) {
					Class buffType = ((Map<String, Class>)databus.get(FlowChart.BUFF_TYPE)).get(mapname);
					if (null == buffType) {
						throw new JlcuException("buff[" + mapname + "] is null");
					}
					params[i] = newDatabusValue(databus, buffType, v);
				}
				i++;
			}
			
			return execute(engine, scriptEngine, params, null);
		} catch (Exception e) {
			JlcuUtility.error(JlcuMessages.SUBFLOW_EXCEP.bind(path), e);
		}
		return null;
	}
	
	@Override
	public Object execute(Engine engine, ScriptEngine scriptEngine,	Object[] params, Class[] paramTypes) throws Exception {
		// TODO Auto-generated method stub
		Object obj = engine.executeSubLCU(path,params);
		/**如果出参和为入参中的一个,则深度clone*/
		for (Object param : params) {
			if (param == obj) {
				obj = Busybox.deepClone(param);
				break;
			}
		}
		engine = null;
		return obj;
	}
	
	public String getName(){
		return path;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
}