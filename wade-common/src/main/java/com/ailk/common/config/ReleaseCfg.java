package com.ailk.common.config;

import java.util.Map;

public class ReleaseCfg{
	private static final String WADE_FILE_NAME = "wade-release.txt";
	private static final String FILE_NAME = "release.txt";
	
	private static String WADE_RELEASE_NUMBER = "4.0";
	private static String RELEASE_NUMBER = "1";
	
	static {
		try {
			Map<String,String> data = null;
			data = TextConfig.getProperties(WADE_FILE_NAME);
			if(data != null){
				String number = (String)data.get("number");
				if(number !=null && !"".equals(number)){
					WADE_RELEASE_NUMBER = number;
				}
			}
			data = TextConfig.getProperties(FILE_NAME);
			if(data != null){
				String number = (String)data.get("number");
				if(number !=null && !"".equals(number)){
					RELEASE_NUMBER = number;
				}
			}
		} catch (Exception e) {
		}
	}

	public static String getWadeReleaseNumber(){
		return WADE_RELEASE_NUMBER;
	}
	
	public static String getReleaseNumber(){
		return RELEASE_NUMBER;
	}
}