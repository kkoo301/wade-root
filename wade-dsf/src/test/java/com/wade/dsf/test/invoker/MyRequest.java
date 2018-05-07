/**
 * $
 */
package com.wade.dsf.test.invoker;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: MyRequest.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-20
 */
public class MyRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 734133694868680961L;
	
	private boolean throwEcxeption = false;
	
	
	public MyRequest() {
		
	}
	
	/**
	 * @return the throwEcxeption
	 */
	public boolean isThrowEcxeption() {
		return throwEcxeption;
	}
	
	/**
	 * @param throwEcxeption the throwEcxeption to set
	 */
	public void setThrowEcxeption(boolean throwEcxeption) {
		this.throwEcxeption = throwEcxeption;
	}

}
