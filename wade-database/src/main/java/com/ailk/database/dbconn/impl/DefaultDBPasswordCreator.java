package com.ailk.database.dbconn.impl;

import com.ailk.database.dbconn.IDBPasswordCreator;


public class DefaultDBPasswordCreator implements IDBPasswordCreator {

	public String getDBPassword(String userName, String serviceName, String passwd) {
		return passwd;
	}

	public String getFtpPassword(String userName, String serviceName, String passwd) {
		return passwd;
	}

}
