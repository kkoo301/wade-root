/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com 
 * http://www.wadecn.com
 */
package com.ailk.jlcu;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.jlcu.mapunit.Case;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.mapunit.method.IMethod;
import com.ailk.jlcu.mapunit.method.UndoMethod;
import com.ailk.jlcu.mapunit.node.ActionNode;
import com.ailk.jlcu.mapunit.node.IFlowNode;
import com.ailk.jlcu.mapunit.node.SwitchNode;
import com.ailk.jlcu.trans.CommonFactory;
import com.ailk.jlcu.trans.ICommonDo;
import com.ailk.jlcu.undo.IUndoDo;
import com.ailk.jlcu.undo.UndoMethodsStack;
import com.ailk.jlcu.util.Busybox;
import com.ailk.jlcu.util.Constant;
import com.ailk.jlcu.util.JlcuConfig;
import com.ailk.jlcu.util.JlcuException;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;

/**
 * JLCU 核心引擎
 * 
 * @author steven zhou
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class Engine {
	
	private static final Logger log = Logger.getLogger(Engine.class);
	
	/** 脚本引擎 */
	private ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByExtension("js");

	/** 补偿方法栈 */
	private UndoMethodsStack undoMethodsStack;

	private String xTransPath;
	private String xTransCode;
	
	public Engine(String xTransPath){
		this.xTransPath = xTransPath;
	}
	
	public Engine(){
		this("/");
	}
	
	public Object executeLCU(Object ... data) throws Exception {
		
		if (null == data || 0 == data.length) {
			JlcuUtility.error(JlcuMessages.PARAM_NOT_NULL.toString());
		}
		
		Object param = data[0];
		if (param instanceof IData) {
			xTransCode = ((IData)param).getString(Constant.JLCU_NAME);
		} else if (param instanceof IDataset) {
			xTransCode = ((IDataset)param).getData(0).getString(Constant.JLCU_NAME);
		} else {
			xTransCode = null;
		}
		
		/*X_TRANS_CODE为空时异常*/
		if (null == xTransCode || xTransCode.equals("")) {
			JlcuUtility.error(JlcuMessages.JLCU_NOT_NULL.toString());
		}
		
		return executeLCU(xTransCode, data);
	}
	
	/**
	 * 流程调用(非子流程)
	 */
	public Object executeLCU(String xTransCode,Object ... data) throws Exception {
		
		/*获取逻辑流程名 */
		this.xTransCode = xTransCode;
		
		FlowChart flowChart = RuntimeContext.getIntance().getFlowChart(xTransCode, xTransPath);
		String outParamName = flowChart.getOutData().getName();

		/*构造新的数据总线 */
		Map<String, Object> databus = new HashMap<String, Object>();
		
		/*初始化补偿方法的堆栈*/
		undoMethodsStack = new UndoMethodsStack();
		
		/*初始化脚本引擎*/
		scriptEngine.put(Constant.DATABUS, databus);
		
		databus.put(FlowChart.BUFF_TYPE, flowChart.getBuffTypes());
		
		long time = System.currentTimeMillis();
		
		/*jlcu调用前处理*/
		ICommonDo commonDo = CommonFactory.getCommonDo(databus);
		if (null != commonDo) {
			data = commonDo.beginDo(xTransCode, data);
		}
		int len = flowChart.getInDatas().size() < data.length ? flowChart.getInDatas().size() : data.length;
		for (int i = 0; i < len; i++) {
			databus.put(flowChart.getInDatas().get(i).getName(), data[i]);
		}
		
		iterateFlowChart(flowChart, databus);
		
		/*jlcu调用后处理*/
		Object result = databus.get(outParamName);
		if (null != commonDo) {
			result = commonDo.endDo(xTransCode, result);
		}
		
		JlcuUtility.log(log, "JLCU " + xTransCode + " execute time: " + ((double)(System.currentTimeMillis() - time)) / 1000 + "s");
		return databus.get(outParamName);
	}
	
	
	/**
	 * 流程调用(子流程)
	 */
	public Object executeSubLCU(String xTransCode, Object ... input) throws Exception {
		
		FlowChart flowChart = RuntimeContext.getIntance().getFlowChart(xTransCode, xTransPath);
		String outParamName = flowChart.getOutData().getName();
		
		/** 构造新的数据总线 */
		//保存主流程数据总线
		Map<String, Object> mainDatabus = (Map<String, Object>)scriptEngine.get(Constant.DATABUS);
		Map<String, Object> subDatabus = new HashMap<String, Object>();
		scriptEngine.put(Constant.DATABUS, subDatabus);
		subDatabus.put(FlowChart.BUFF_TYPE, flowChart.getBuffTypes());
		
		/*子流程调用前处理*/
		ICommonDo commonSubDo = CommonFactory.getCommonSubDo(subDatabus);
		if (null != commonSubDo) {
			input = commonSubDo.beginDo(xTransCode, input);
		}
		int len = flowChart.getInDatas().size() < input.length ? flowChart.getInDatas().size() : input.length;
		for (int i = 0; i < len; i++) {
			subDatabus.put(flowChart.getInDatas().get(i).getName(), input[i]);
		}
		
		iterateFlowChart(flowChart, subDatabus);
		
		/*子流程调用后处理*/
		Object result = subDatabus.get(outParamName);
		if (null != commonSubDo) {
			result = commonSubDo.endDo(xTransCode, result);
		}
		//还原主流程数据总线
		scriptEngine.put(Constant.DATABUS, mainDatabus);
		return result;
	}
	
	/**
	 * 迭代流程图
	 */
	private void iterateFlowChart(FlowChart flowChart, Map databus) throws Exception {
		IFlowNode node = flowChart.getStartNode();
		long time = 0, _time;
		while (null != node) {
			time = System.currentTimeMillis();
			
			switch (node.getNodeType()) {
			case START:
				node = execStartNode((ActionNode) node, databus);
				break;
			case END:
				node = execEndNode((ActionNode) node, databus);
				break;
			case ACTION:
				JlcuUtility.log(log, "JLCU Node["+node.getNodeDesc()+","+node.getNodeId()+"] is running");
				node = execActionNode((ActionNode) node, databus);
				break;
			case SWITCH:
				node = execSwitchNode((SwitchNode) node, databus);
				break;
			default:
				/*对于极少出现的错误信息不必做到统一管理*/
				JlcuUtility.error("unkown node type!");
			}
			
			if (log.isDebugEnabled()) {
				_time = System.currentTimeMillis() - time;
				if (_time > Constant.SAVE_TIME) {
					JlcuUtility.log(log, "JLCU Node ["+node.getNodeDesc()+"] execution time is too long : " + ((double)(_time)) / 1000 + "s");
				}
			}
		}
	}

	/**
	 * 执行开始节点
	 */
	private IFlowNode execStartNode(ActionNode node, Map databus) throws Exception {
		for (IMethod method : node.getMethods()) {
			method.execute(this,scriptEngine,databus);
		}
		
		return node.getNext();
	}

	/**
	 * 执行结束节点
	 */
	private IFlowNode execEndNode(ActionNode node, Map databus) throws Exception {
		for (IMethod method : node.getMethods()) {
			method.execute(this,scriptEngine,databus);
		}
		return null;
	}

	/**
	 * 执行动作节点
	 */
	private IFlowNode execActionNode(ActionNode node, Map databus) throws Exception {
		IMethod errMethod = null;
		recordUndoMethods(node, databus);
		try {
			for (IMethod method : node.getMethods()) {
				errMethod = method;
				method.execute(this,scriptEngine,databus);
			}
		} catch (JlcuException e) {
			try {
				/*日志参数准备*/
				IData param = new DataMap();
				param.put("JLCU_NAME", xTransCode);
				if (null != errMethod) {
					param.put("ERROR_METHOD", errMethod.getName());
				}
				param.put("ERROR_NODE", node.getNodeId()+":"+node.getNodeDesc());
				param.put("ERROR_INFO", e.getMessage());
				param.put("ERROR_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				execUndoMethods(databus,param);
			} catch (JlcuException je) {
				JlcuUtility.log(log, "[backnode:"+node.getNodeDesc()+"]", je);
			}
			JlcuUtility.error(JlcuMessages.NODE_EXCEP.bind(node.getNodeDesc()),e);
		}
		return node.getNext();
	}

	/**
	 * 执行分支节点
	 */
	private IFlowNode execSwitchNode(SwitchNode node, Map databus) throws JlcuException {
		
		/*兼容:使得子流程的分支表达式使用子流程的数据总线*/
		scriptEngine.put(Constant.DATABUS, databus);
		
		for (Case c : node.getCases()) {
			String expr = c.getExpression().trim();
			if (expr.equals("")) {
				return c.getNext(); // 默认
			}

			try {
				JlcuUtility.log(log, "JLCU case-expression: " + expr);
				Object caseResult = scriptEngine.eval(expr);
				if (caseResult instanceof Boolean) {
					Boolean bool = (Boolean) caseResult;
					if (bool.booleanValue()) {
						return c.getNext();
					}
				} else {
					JlcuUtility.error(JlcuMessages.CASE_IS_BOOLEAN.bind(c.getLinkId()));
				}
			} catch (Exception e) {
				JlcuUtility.error(JlcuMessages.NODE_EXCEP.bind(node.getNodeDesc()),e);
			}
		}
		JlcuUtility.error(JlcuMessages.SWTICH_HAS_NO_DEFAULT.bind(node.getNodeId()));
		return null;
	}

	/**
	 * 使用UndoMethodsStack中的补偿对象来记录补偿参数
	 */
	private void recordUndoMethods(ActionNode node, Map databus) {
		
		List<IMethod> methods = node.getUndoMethods();
		UndoMethod undoMethod = null;
		for (int i = methods.size() - 1; i >= 0; i--) {
			IMethod method = methods.get(i);
			List<Varmap> inVars = method.getInVars();
			
			Object[] params = null;
			Class[] paramTypes = null;
			if (inVars != null && inVars.size() > 0) {
				params = new Object[inVars.size()];
				paramTypes = new Class[inVars.size()];
				int counter = 0;
				for (Varmap v : inVars) {
					String mapname = v.getMapname();
					/*不对PageData对象进行克隆, 否则会导致连接池问题!*/
					if(FlowChart.CONTEXT.equals(mapname)){
						continue;
					}
					
					Object inparam = databus.get(mapname);
					params[counter] = Busybox.deepClone(inparam);
					
					/*继续存放参数的类型*/
					paramTypes[counter] = ((Map<String, Class>)databus.get(FlowChart.BUFF_TYPE)).get(mapname);
					counter++;
				}
			}
			undoMethod = new UndoMethod(method, params, paramTypes);
			undoMethodsStack.push(undoMethod);
		}
	}

	/**
	 * 执行回补方法集
	 */
	private void execUndoMethods(Map databus,IData param) throws JlcuException {
		
		/*取消控制事务*/
		if (null != undoMethodsStack) {
			
			UndoMethod method = null;
			try {
				while (!undoMethodsStack.empty()) {
					method = undoMethodsStack.pop();
					method.execute(this,scriptEngine);
				}
			} catch (Exception e) {
				
				/**执行补偿方法时报错,则执行IUndoDo的undoDo方法,业务侧实现此类,通常记录异常时候的数据,后面人工干预处理*/
				JlcuUtility.log(log, "undo error method:" + method + ",undo exception:" + e.getMessage(), e);
				
				/*引入undoClass*/
				try {
					String undoClass = JlcuConfig.getUndoClass();
					if (null != undoClass) {
						Constructor cons = Class.forName(undoClass).getDeclaredConstructor();
						cons.setAccessible(true);
						IUndoDo undoDo = (IUndoDo)cons.newInstance();
						undoDo.undoDo(method);
					} else {
						JlcuUtility.log(log, "undoClass is null");
					}
				} catch (Exception e1) {
					JlcuUtility.log(log, "IUndoDo error:", e1);
				}
			}
		}
	}

	public String getXTransPath() {
		return xTransPath;
	}

	public void setXTransPath(String transPath) {
		xTransPath = transPath;
	}
	
	private void clear() {
		if (undoMethodsStack != null) {
			undoMethodsStack.clear();
		}
	}
}