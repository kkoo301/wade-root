package com.ailk.database.dbconn;

public interface IDBPasswordCreator {
	
	public String getDBPassword(String name, String serviceName, String passwd);
	
	public String getFtpPassword(String name, String site, String passwd);
	

}
