package com.ailk.database.orm.err;

/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月20日
 * 
 *        Just Do IT.
 */
public enum BOError {

	/**
	 * bo10000:找不到BO表的元数据,%s
	 */
	bo10000("bo10000", "找不到BO表的元数据,%s"),

	/**
	 * bo10001:BO属性未定义,%s
	 */
	bo10001("bo10001", "BO属性未定义,%s"),

	/**
	 * bo10002:实例化BO异常%s,%s
	 */
	bo10002("bo10002", "实例化BO异常%s,%s"),

	/**
	 * bo10003:BO对象为NULL，无法获取属性值%s
	 */
	bo10003("bo10003", "BO对象为NULL，无法获取属性值%s"),

	/**
	 * bo10004:BO对象为NULL，无法设置属性值%s=%s
	 */
	bo10004("bo10004", "BO对象为NULL，无法设置属性值%s=%s"),
	/**
	 * bo10005:属性%s.%s转换异常，输入类型：%s
	 */
	bo10005("bo10005", "属性%s.%s转换异常，输入类型：%s"),

	/**
	 * bo10006:不能获取数据类型%s
	 */
	bo10006("bo10006", "不能获取数据类型%s"),

	/**
	 * bo10007:BO日期类型转换异常%s
	 */
	bo10007("bo10007", "BO日期类型转换异常%s"),
	/**
	 * bo10008:创建BO对象时主键字段个数不匹配，%s
	 */
	bo10008("bo10008", "创建BO对象时主键字段个数不匹配，%s"),

	/**
	 * bo10009:重复的WHERE语句
	 */
	bo10009("bo10009", "重复的WHERE语句"),

	/**
	 * bo10010:AND必须在WHERE语句之后
	 */
	bo10010("bo10010", "AND必须在WHERE语句之后"),

	/**
	 * bo10011:指定的字段%未定义%s
	 */
	bo10011("bo10011", "指定的字段%未定义%s"),

	/**
	 * bo10012:不支持的日期格式，%s
	 */
	bo10012("bo10012", "不支持的日期格式，%s"),

	/**
	 * bo10013:主表%s未定义子表规则
	 */
	bo10013("bo10013", "主表%s未定义子表规则"),
	/**
	 * bo10014:获取BO对象的分表规则异常,%s
	 */
	bo10014("bo10014", "获取BO对象的分表规则异常,%s"),

	/**
	 * bo10015:获取BO对象的表名异常，%s
	 */
	bo10015("bo10015", "获取BO对象的表名异常，%s"),
	/**
	 * bo10016:创建BO对象%s时主键字段%s值为NULL
	 */
	bo10016("bo10016", "创建BO对象%s时主键字段%s值为NULL"),;

	private String code;
	private String info;

	private BOError(String code, String info) {
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
