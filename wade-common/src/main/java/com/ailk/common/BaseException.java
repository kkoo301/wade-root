package com.ailk.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;

public class BaseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("at\\s([a-zA-Z0-9\\._\\$]+)\\.([a-zA-Z0-9_\\$]+)\\((([a-zA-Z0-9_\\$\\s]+)|([a-zA-Z0-9_\\$]+\\.java):(\\d+))\\)");
	
	public static final String SPLITE_CHART = "`";
	public static final String SPLITE_PARAM_CHART = "~";
	public static final String INFO_SPLITE_CHAR = "●";
	private IData data = null;
	
	/** message format: code$message$param1~param2~param3 */
	private String message;
	private String code;
	private String info;

	public BaseException(Throwable throwable) {
		super(throwable);
		this.message = dealMsg("" + SPLITE_CHART + throwable.getMessage() + SPLITE_CHART);
		this.info = dealMsg(throwable.getMessage());
	}
	
	public BaseException(Throwable throwable, IData data) {
		super(throwable);
		this.message = dealMsg("" + SPLITE_CHART + throwable.getMessage() + SPLITE_CHART);
		this.info = dealMsg(throwable.getMessage());
		this.data = data;
	}

	public BaseException(String code, Throwable throwable) {
		super(code, throwable);
		this.code = code;
		this.message = dealMsg(code + SPLITE_CHART + throwable.getMessage() + SPLITE_CHART);
		this.info = dealMsg(throwable.getMessage());
	}
	
	public BaseException(String code, Throwable throwable, IData data) {
		super(code, throwable);
		this.code = code;
		this.message = dealMsg(code + SPLITE_CHART + throwable.getMessage() + SPLITE_CHART);
		this.info = dealMsg(throwable.getMessage());
		this.data = data;
	}

	public BaseException(String code) {
		super(code);
		this.code = "-1";
		this.message = dealMsg(code);
		this.info = dealMsg(code);
	}
	
	public BaseException(String code, IData data) {
		super(code);
		this.code = "-1";
		this.message = dealMsg(code);
		this.info = dealMsg(code);
		this.data = data;
	}

	public BaseException(String code, String[] params, String message) {
		super(code);
		this.code = code;
		this.message = dealMsg(code + SPLITE_CHART + message + SPLITE_CHART + parseParams(params));
		this.info = dealMsg(message);
	}
	
	public BaseException(String code, String[] params, String message, IData data) {
		super(code);
		this.code = code;
		this.message = dealMsg(code + SPLITE_CHART + message + SPLITE_CHART + parseParams(params));
		this.info = dealMsg(message);
		this.data = data;
	}
	
	/**
	 * xiedx 增加stacktrace构造方法
	 * @param code
	 * @param params
	 * @param message
	 * @param data
	 * @param stacktrace
	 */
	public BaseException(String code, String[] params, String message, IData data, String stacktrace) {
		super(code);
		this.code = code;
		this.message = dealMsg(code + SPLITE_CHART + message + SPLITE_CHART + parseParams(params));
		this.info = dealMsg(message);
		this.data = data;
		
		this.setStackTrace(stacktrace);
	}
	
	private String dealMsg(String message) {
		if (null != message && message.length() > 0) {
			return message.replaceAll(",", " ");
		}
		return message;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(IData data) {
		this.data = data;
	}
	
	/**
	 * xiedx 2017/6/5 setStackTrace
	 * @param stacktrace
	 */
	public void setStackTrace(String stackTrace){
		if(stackTrace == null || "".equals(stackTrace.trim()))
			return;

		String[] stacks = stackTrace.split("\\n");
		if(stacks != null && stacks.length > 0){
			List<StackTraceElement> traces = new ArrayList<StackTraceElement>();
			for(int i = 0; i < stacks.length; i ++){
				String stack = stacks[i];
				if(stack == null || "".equals(stack.trim()))
					continue;
				
				Matcher m = STACK_TRACE_PATTERN.matcher(stacks[i]);
				if(m.find()){
					if( null != m.group(6)){
						traces.add(new StackTraceElement(m.group(1), m.group(2), m.group(5), Integer.parseInt(m.group(6))));
					}else if(null != m.group(4)){
						traces.add(new StackTraceElement(m.group(1), m.group(2), m.group(4), -1));
					}
				}else{
					int idx = stack.indexOf(this.getClass().getName());
					if(idx > -1){
						traces.add(new StackTraceElement(this.getClass().getPackage().getName(), this.getClass().getSimpleName(), stack.substring(0, idx), -1));
					}
				}
			}
			this.setStackTrace(traces.toArray(new StackTraceElement[traces.size()]));
		}	
	}
	
	/**
	 * @return the data
	 */
	public IData getData() {
		return data;
	}

	public String getMessage() {
		return this.message;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getInfo() {
		return this.info;
	}
	
	private String parseParams(String[] params) {
		if (params == null) return "";
		
		StringBuilder sb = new StringBuilder();
		for (String param : params) {
			sb.append(param).append(SPLITE_PARAM_CHART);
		}
		return sb.toString();
	}
	
	/* 空指针异常 */
	public static final String CODE_SVC_OFFLINE = "-100";
	public static final String INFO_SVC_OFFLINE = "服务已下线！";
	
	/* 空指针异常 */
	public static final String CODE_NULL_POINT = "-99";
	public static final String INFO_NULL_POINT = "空指针异常！";
	
	/* 错误编码未定义 */
	public static final String CODE_UNDEFINED = "-98";
	public static final String INFO_UNDEFINED = "错误编码未定义！";
	
	/* 服务未定义 */
	public static final String CODE_NO_SERVICE = "-97";
	public static final String INFO_NO_SERVICE = "找不到服务，服务未注册！";
	
	/* 权限拒绝，请与系统管理员联系 */
	public static final String CODE_NO_SVC_PRIV = "-96";
	public static final String INFO_NO_SVC_PRIV = "权限拒绝，请与系统管理员联系！";
	
	/* 服务返回状态异常 */
	public static final String CODE_SVC_RESPONSE_5 = "-95";
	public static final String INFO_SVC_RESPONSE_5 = "服务返回状态异常";
	
	/* 未经授权的服务 */
	public static final String CODE_SVC_NOPRIVS = "-94";
	public static final String INFO_SVC_NOPRIVS = "未经授权的服务";
	
	/* 非法请求,当前操作已记录在案 */
	public static final String CODE_SVC_TESTATTACK = "-93";
	public static final String INFO_SVC_TESTATTACK = "非法请求,当前操作已记录在案！";
	
	/* 服务调用失败 */
	public static final String CODE_SVC_INVOKEERROR = "-92";
	public static final String INFO_SVC_INVOKEERROR = "服务调用失败";
	
	/* 服务并发控制 */
	public static final String CODE_SVC_BCC = "-91";
	public static final String INFO_SVC_BCC = "服务调用已超最大阀值，系统开启自我保护";
	
	/* 服务未初始化 */
	public static final String CODE_SVC_NO_INIT = "-90";
	public static final String INFO_SVC_NO_INIT = "服务未初始化";
	
	/* 服务调用超时 */
	public static final String CODE_SVC_TIMEOUT = "-89";
	public static final String INFO_SVC_TIMEOUT = "服务调用超时";
	
}
