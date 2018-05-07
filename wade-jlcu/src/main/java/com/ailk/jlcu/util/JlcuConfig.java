package com.ailk.jlcu.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public final class JlcuConfig {
	private static final Logger log = Logger.getLogger(JlcuConfig.class);
	private static Properties config = null;
	
	static {
		config = new Properties();
		synchronized (config) {
			//加载默认配置文件
			InputStream is = null;
			try {
				is = JlcuConfig.class.getClassLoader().getResourceAsStream(Constant.JLCU_DEFAULT_CONFIG);
				if(is!=null){
					config.load(is);
				}
				//加载业务侧配置,覆盖默认配置
				is = JlcuConfig.class.getClassLoader().getResourceAsStream(Constant.JLCU_CONFIG);
				if(is!=null){
					Properties p = new Properties();
					p.load(is);
					config.putAll(p);
				}
			} catch (Exception e) {
				// TODO: handle exception
				JlcuUtility.log(log, e);
			} finally {
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						JlcuUtility.log(log, e);
					}
				}
			}
		}
	}
	
	public static String getUndoClass() throws IOException {
		// TODO Auto-generated method stub
		return config.getProperty(Constant.UNDO_CLASS);
	}
	
	public static String getCommonClass() throws IOException {
		return config.getProperty(Constant.COMMON_CLASS);
	}
	
	public static String getCommonSubClass() throws IOException {
		return config.getProperty(Constant.COMMON_SUBFLOW_CLASS);
	}
	
	public static void main(String[] args) {
		System.out.println(new JlcuConfig().config);
	}
}
