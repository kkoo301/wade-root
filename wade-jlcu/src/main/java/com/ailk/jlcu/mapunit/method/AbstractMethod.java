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
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.jlcu.Engine;
import com.ailk.jlcu.FlowChart;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.util.Busybox;
import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuUtility;

/**
 * 方法(抽象类)
 * 
 * @author steven zhou
 * @since 1.0
 */
public abstract class AbstractMethod implements IMethod {
	
	private static final Logger log = Logger.getLogger(AbstractMethod.class);
	
	/** 输入参数集合 */
	private List<Varmap> inVars;

	/** 输出参数 */
	private Varmap outVar;
	
	public AbstractMethod(List<Varmap> inVars, Varmap outVar) {
		this.inVars = inVars;
		this.outVar = outVar;
	}
	
	public void execute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException {
		JlcuUtility.log(log,"method["+this.toString()+"] is running");
		Object outData = subExecute(engine,scriptEngine,databus);
		
		Map<String, Class> buffType = (Map<String, Class>)databus.get(FlowChart.BUFF_TYPE);
		if (outData != null && outVar != null && buffType.containsKey(outVar.getMapname())) {
			databus.put(outVar.getMapname(), outData);
		} else {
			if (null != outData) {
				JlcuUtility.log(log, "result is not put into the databus[" + outData.getClass() + "] ");
			}
		}
	}
	
	public abstract Object subExecute(Engine engine, ScriptEngine scriptEngine, Map databus) throws JlcuException;

	public List<Varmap> getInVars() {
		return inVars;
	}

	public Varmap getOutVar() {
		return outVar;
	}

	protected Object getDatabusValue(Map databus,Varmap inVar) {
		String mapname = inVar.getMapname();
		Object value = databus.get(mapname);
		//不对head对象进行克隆
		if (inVar.isIsclone() && !FlowChart.CONTEXT.equals(mapname)) {
			value = Busybox.deepClone(value);
		}
		return value;
	}
	
	protected Object newDatabusValue(Map databus,Class clas,Varmap inVar) throws InstantiationException,
		IllegalAccessException{
		String mapname = inVar.getMapname();
		Object value = null;
		if (!FlowChart.CONTEXT.equals(mapname)) {//不在jlcu中实例化head
			if (clas.equals(IData.class)) {
				clas = DataMap.class;
			} else if (clas.equals(IDataset.class)) {
				clas = DatasetList.class;
			}
			value = clas.newInstance();
		}
		if (!inVar.isIsclone()) {
			databus.put(mapname, value);
		}
		return value;
	}
}
