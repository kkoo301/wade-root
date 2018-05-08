/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.producer;

import com.ailk.common.data.IDataInput;

/**
 * @className:CallBackHandler.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-27 
 */
public abstract class CallBackHandler {
	
	private IDataInput input;

	public void setDataInput(IDataInput input) {
		this.input = input;
	}

	public IDataInput getDataInput() {
		return this.input;
	}
	
	public abstract void callback(String index);
		
}
