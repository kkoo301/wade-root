/**  
*
* Copyright: Copyright (c) 2014 Asiainfo-Linkage
*
*/
package com.ailk.common.util;



/**
 * @className:ResultData.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2014-5-22 
 */
public interface IResultData {
	
	/**
	 * 比较是否存在下一行数据
	 * @return
	 */
	public boolean hasNext();
	
	/**
	 * 根据配置文件中的列名 获取 对应列的数据
	 * @param columnName
	 * @return
	 */
	public Object get(String columnName);
	
	/**
	 * 该sheet页的数据处理完毕
	 */
	public void close();
	
	/**
	 * 获取该sheet页接收到的数据量 
	 * @return
	 */
	public long getCount();
	
}
