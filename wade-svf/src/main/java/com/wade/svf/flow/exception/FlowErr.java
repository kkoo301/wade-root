/**
 * $
 */
package com.wade.svf.flow.exception;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FlowErr.java
 * @description: 框架异常定义
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-3
 */
public enum FlowErr {

	/**
	 * flow10000:流程初始化失败,%s
	 */
	flow10000("flow10000", "流程初始化失败,%s"),

	/**
	 * flow10001:流程执行异常,%s
	 */
	flow10001("flow10001", "流程执行异常,%s"),
	/**
	 * flow10002:流程%s重复定义的节点名称%s
	 */
	flow10002("flow10002", "流程%s重复定义的节点名称%s"),
	/**
	 * flow10003:流程%s的节点%s参数配置异常，找不到引用对象%s
	 */
	flow10003("flow10003", "流程%s的节点%s参数配置异常，找不到引用对象，%s"),
	/**
	 * flow10004:流程%s的节点%s参数配置异常，非法的参数名称，%s
	 */
	flow10004("flow10004", "流程%s的节点%s参数配置异常，非法的参数名称，%s"),
	/**
	 * flow10005:流程%s的节点%s参数配置异常，参数类型不匹配[inparam|outparam]，%s
	 */
	flow10005("flow10005", "流程%s的节点%s参数配置异常，参数类型%s不匹配[inparam|outparam]，%s"),
	/**
	 * flow10006:流程%s的rule节点%s输入参数配置异常，%s
	 */
	flow10006("flow10006", "流程%s的rule节点%s输入参数配置异常，%s"),
	/**
	 * flow10007:流程%s配置异常，%s
	 */
	flow10007("flow10007", "流程%s配置异常，%s"), 
	/**
	 * flow10008:流程%s调用异常，服务%s输入参数效验失败，%s
	 */
	flow10008("flow10008", "流程%s调用异常，节点%s输入参数效验失败，%s"),
	/**
	 * flow10009:流程%s调用异常，服务%s输出参数效验失败，%s
	 */
	flow10009("flow10009", "流程%s调用异常，节点%s输出参数效验失败，%s"),
	/**
	 * flow10010:流程%s调用异常，%s
	 */
	flow10010("flow10010", "流程%s调用异常，%s"),
	/**
	 * flow10011:流程%s发现未识别的请求数据类型，Content-Type:%s
	 */
	flow10011("flow10011", "流程%s发现未识别的请求数据类型，Content-Type:%s"),
	/**
	 * flow10012:转换数据格式异常，%s
	 */
	flow10012("flow10012", "转换数据格式异常，%s"),
	/**
	 * flow10013:流程%s读取数据流异常，%s
	 */
	flow10013("flow10013", "流程%s读取数据流异常，%s"),
	/**
	 * flow10014:流程%s接入认证异常，%s
	 */
	flow10014("flow10014", "流程%s接入认证异常，%s"),
	/**
	 * flow10015:流程%s值表达式创建异常，%s
	 */
	flow10015("flow10015", "值表达式创建异常，%s"),
	/**
	 * flow10016:流程%s数据合并节点%s异常，%s
	 */
	flow10016("flow10016", "流程%s数据合并节点%s异常，%s");

	private String code;
	private String info;

	private FlowErr(String code, String info) {
		this.code = code;
		this.info = info;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	public String getInfo(Object... params) {
		return String.format(info, params);
	}

}
