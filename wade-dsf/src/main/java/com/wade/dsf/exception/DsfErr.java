/**
 * $
 */
package com.wade.dsf.exception;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfErr.java
 * @description: 框架异常定义
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-3
 */
public enum DsfErr {

	/**
	 * dsf10000:DSF服务初始化失败,%s
	 */
	dsf10000("dsf10000", "DSF服务初始化失败,%s"),

	/**
	 * dsf10001:找不到服务名
	 */
	dsf10001("dsf10001", "找不到服务名"),

	/**
	 * dsf10002:找不到服务注册信息, %s
	 */
	dsf10002("dsf10002", "找不到服务注册信息, %s"),

	/**
	 * dsf10003:服务调用异常, %s
	 */
	dsf10003("dsf10003", "服务调用异常, %s"),

	/**
	 * dsf10004:封装服务请求对象为空, 服务名=%s, 请求类型=%s
	 */
	dsf10004("dsf10004", "封装服务请求对象为空, 服务名=%s, 请求类型=%s"),

	/**
	 * dsf10005:根据服务名找不到服务实体%s
	 */
	dsf10005("dsf10005", "根据服务名找不到服务实体%s"),

	/**
	 * dsf10006:服务%s实现类解析异常%s
	 */
	dsf10006("dsf10006", "服务%s实现类解析异常%s"),

	/**
	 * dsf10007:非法的服务请求%s
	 */
	dsf10007("dsf10007", "非法的服务请求%s"),

	/**
	 * dsf10008:服务响应对象不可序列化, %s
	 */
	dsf10008("dsf10008", "服务响应对象不可序列化, %s"),

	/**
	 * dsf10009:服务%s调用时抛出非标准的异常%s
	 */
	dsf10009("dsf10009", "服务%s调用时抛出非标准的异常%s"),

	/**
	 * dsf10010:服务%s调用时，发现未定义的请求类型%s
	 */
	dsf10010("dsf10010", "服务%s调用时，发现未定义的请求类型%s"),

	/**
	 * dsf10011:当前进程%s未预热
	 */
	dsf10011("dsf10011", "当前进程%s未预热"),

	/**
	 * dsf10012:服务请求反序列化失败，服务名[%s]
	 */
	dsf10012("dsf10012", "服务请求反序列化失败，服务名[%s]"),

	/**
	 * dsf10013:重复注册的服务%s,%s
	 */
	dsf10013("dsf10013", "重复注册的服务%s,%s"),
	/**
	 * dsf10014:数据适配发现异常格式,%s,%s
	 */
	dsf10014("dsf10014", "数据适配发现异常格式,%s,%s"),
	/**
	 * dsf10015:执行线程池初始化失败,%s
	 */
	dsf10015("dsf10015", "执行线程池初始化失败,%s"),
	/**
	 * dsf10016:事务提交等待%s毫秒仍未完成，强制中断，%s
	 */
	dsf10016("dsf10016", "事务提交等待%s毫秒仍未完成，强制中断，%s"),
	/**
	 * dsf10017:数据库操作异常%s
	 */
	dsf10017("dsf10017", "数据库操作异常%s"),
	/**
	 * dsf10018:主服务已终止, %s
	 */
	dsf10018("dsf10018", "主服务已终止, %s"),
	/**
	 * dsf10019:服务方法反射异常, %s
	 */
	dsf10019("dsf10019", "服务方法反射异常, %s"),
	/**
	 * dsf10020:服务线程繁忙，请调整并发线程数，%s
	 */
	dsf10020("dsf10020", "服务线程繁忙，请调整并发线程数，%s"),
	/**
	 * dsf10021:主服务已超时，子线程终止，事务已回滚，%s
	 */
	dsf10021("dsf10021", "主服务已超时，子线程终止，事务已回滚，%s执行耗时%dms"),
	/**
	 * dsf10022:初始化数据源失败
	 */
	dsf10022("dsf10022", "初始化数据源失败"),
	/**
	 * dsf10023:初始化缓存失败
	 */
	dsf10023("dsf10023", "初始化缓存失败"), ;

	private String code;
	private String info;

	private DsfErr(String code, String info) {
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
