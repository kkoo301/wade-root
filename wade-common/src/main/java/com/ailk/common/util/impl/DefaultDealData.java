/**  
*
* Copyright: Copyright (c) 2014 Asiainfo-Linkage
*
*/
package com.ailk.common.util.impl;

import com.ailk.common.data.IData;
import com.ailk.common.util.IDealData;

/**
 * @className:DefaultDealData.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2014-9-26 
 */
public class DefaultDealData implements IDealData {

	private long dataCount = 0;
	
	/**
	 *  
	 *  
	 * @param sheetName
	 * @param sheetAttrs 
	 * @see com.ailk.common.util.IDealData#begin(java.lang.String, com.ailk.common.data.IData) 
	 */
	@Override
	public void begin(String sheetName, IData sheetAttrs) {
		
	}

	/**
	 *  
	 *  
	 * @param data
	 * @param right
	 * @param info
	 * @return 
	 * @see com.ailk.common.util.IDealData#execute(com.ailk.common.data.IData, boolean, java.lang.String) 
	 */
	@Override
	public boolean execute(IData data, boolean right, String info) {
		dataCount++;
		return true;
	}

	/**
	 *  
	 *  
	 * @param data
	 * @param right
	 * @param info
	 * @return 
	 * @see com.ailk.common.util.IDealData#execute(java.lang.Object[], boolean, java.lang.String) 
	 */
	@Override
	public boolean execute(Object[] data, boolean right, String info) {
		dataCount++;
		return true;
	}

	/**
	 *  
	 *  
	 * @param sheetName 
	 * @see com.ailk.common.util.IDealData#end(java.lang.String) 
	 */
	@Override
	public void end(String sheetName) {
		
	}

	/**
	 *  
	 *   
	 * @see com.ailk.common.util.IDealData#over() 
	 */
	@Override
	public void over() {
		
	}

	/**
	 *  
	 *  
	 * @return 
	 * @see com.ailk.common.util.IDealData#getCount() 
	 */
	@Override
	public long getCount() {
		return dataCount;
	}

}
