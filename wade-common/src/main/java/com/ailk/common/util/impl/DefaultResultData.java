/**  
*
* Copyright: Copyright (c) 2014 Asiainfo-Linkage
*
*/
package com.ailk.common.util.impl;

import com.ailk.common.util.IResultData;

/**
 * @className:DefaultResultData.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2014-9-26 
 */
public class DefaultResultData implements IResultData {

	private long dataCount = 0;
	
	/**
	 *  
	 *  
	 * @return 
	 * @see com.ailk.common.util.IResultData#hasNext() 
	 */
	@Override
	public boolean hasNext() {
		dataCount++;
		return true;
	}

	/**
	 *  
	 *  
	 * @param columnName
	 * @return 
	 * @see com.ailk.common.util.IResultData#get(java.lang.String) 
	 */
	@Override
	public Object get(String columnName) {
		return null;
	}

	/**
	 *  
	 *   
	 * @see com.ailk.common.util.IResultData#close() 
	 */
	@Override
	public void close() {
		
	}

	/**
	 *  
	 *  
	 * @return 
	 * @see com.ailk.common.util.IResultData#getRightCount() 
	 */
	@Override
	public long getCount() {
		return dataCount;
	}

}
