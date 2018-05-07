package com.ailk.common.util;

import com.ailk.common.data.IData;

/**
 * 提供提示信息后台处理操作
 * 
 * @author lvchao
 *
 */
public interface ITips {
	/* 默认的操作类型    close:关闭操作;noshow:不再提示操作;ok:确定操作;*/
	public static final String ACTION_CLOSE = "close";
	public static final String ACTION_NOSHOW = "noshow";
	public static final String ACTION_OK = "ok";

	/* 用于记录前台传入的参数key值   staffId:登录工号;customData:用户自定义属性值;content:提示信息;*/
	public static final String PARAMS_CUSTOM = "customData";
	public static final String PARAMS_CONTENT = "content";
	public static final String PARAMS_LOGINID = "staffId";
	
	/**
	 * 判断指定的数据是否已经出现过，已经提示过则返回true，否则返回false
	 * 
	 * @param data 终端传入的参数,默认key值为PARAMS_CUSTOM，PARAMS_CONTENT，PARAMS_LOGINID
	 * @return 是否已经提示过该信息
	 */
	boolean hasShowContent(IData data);

	/**
	 *  根据传入的操作类型及对应的入参 进行处理
	 * 
	 * @param actionType 触发操作的类型,默认值为 ACTION_CLOSE,ACTION_NOSHOW,ACTION_OK
	 * @param data 终端传入的参数,默认key值为PARAMS_CUSTOM,PARAMS_CONTENT,PARAMS_LOGINID
	 * @return 是否操作成功
	 */
	boolean action(String actionType ,IData data);
}
