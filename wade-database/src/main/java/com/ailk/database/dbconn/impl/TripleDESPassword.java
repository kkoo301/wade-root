/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年11月9日
 * 
 * Just Do IT.
 */
package com.ailk.database.dbconn.impl;

import com.ailk.database.dbconn.IDBPasswordCreator;
import com.ailk.database.util.TripleDES;

/**
 * @description
 * TODO
 */
public class TripleDESPassword implements IDBPasswordCreator {
	
	@Override
	public String getDBPassword(String userName, String serviceName, String passwd) {
		if (serviceName == null || serviceName.length() <= 0)
			return passwd;
		
		/**
		 * 判断是否采用了3DES加密
		 */
		if (passwd.startsWith("{3DES}")) {
			return TripleDES.decrypt(passwd.substring(6));
		}
		
		if (null == passwd || passwd.length() <=0) {
			throw new NullPointerException("创建统一密码[userName=" + userName + ",serviceName=" + serviceName +"]失败，返回值为空");
		}
		
		return passwd;
	}
	
	
	@Override
	public String getFtpPassword(String userName, String serviceName, String passwd) {
		return passwd;
	}

}
