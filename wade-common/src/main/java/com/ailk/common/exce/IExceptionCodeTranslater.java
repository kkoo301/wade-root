package com.ailk.common.exce;

/**
 * 异常编码翻译接口
 * @author Shieh
 *
 */
public interface IExceptionCodeTranslater{
	
	public String translate(String code);
	
	public String translate(String code, String def);
	
	public String translate(String code, String[] params);

	public String translate(String code, String[] params, String def);
}