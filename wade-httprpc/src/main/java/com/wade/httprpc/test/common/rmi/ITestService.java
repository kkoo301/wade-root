/**
 * $
 */
package com.wade.httprpc.test.common.rmi;

import java.util.Date;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ITestService.java
 * @description: 测试接口
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public interface ITestService {
	
	
	/**
	 * 测试接口
	 * @param name
	 * @param status
	 * @param createTime
	 * @param length
	 * @return
	 */
	public abstract String service(String name, boolean status, Date createTime, int length) throws Exception ;

}
