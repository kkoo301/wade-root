/**  
*
* Copyright: Copyright (c) 2014 Asiainfo-Linkage
*
*/
package com.ailk.common.util;

import com.ailk.common.data.IData;

/**
 * 文件处理回调方法, 每获取一次数据即调用一次该对象
 * 
 * @className:IDealData.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2014-5-23 
 */
public interface IDealData {

	/**
	 * 开始处理新的sheet页
	 * @param sheetName 标签页的名字
	 * @param sheetAttrs xml配置文件中设置的参数变量
	 */
	public void begin(String sheetName, IData sheetAttrs);
	
	/**
	 * 处理要操作的数据
	* @param data 要操作的数据
	* @param right 数据是否符合xml的校验配置
	* @param info 当数据不符合校验信息时 对应的提示内容
	* @return 返回false时 则中断文件的读取操作
	 */
	public boolean execute(IData data, boolean right, String info);
	
	/**
	 * 处理要操作的数据 (针对txt或csv格式读取数据类型数据时调用)
	* @param data 要操作的数据
	* @param right 数据是否符合xml的校验配置
	* @param info 当数据不符合校验信息时 对应的提示内容
	* @return 返回false时 则中断文件的读取操作
	 */
	public boolean execute(Object[] data, boolean right, String info);
	
	/**
	 * 当前sheet页读取完毕
	 * @param sheetName
	 */
	public void end(String sheetName);
	
	/**
	 * 当前文件执行完毕
	 */
	public void over();
	
	/**
	 * 获取处理的数据量
	 * @return
	 */
	public long getCount();
}
