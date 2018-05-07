package com.wade.container.start;


/**
 * CP 方式 服务启动类
 * 
 * @author xiedx@asiainfo.com
 * @date 2015-11-11
 */
public class MainCP {

	private static final int ERR_INVOKE_MAIN = -2;
	private static final int ERR_LOAD = -3;
	private static final int ERR_START = -4;
	private static final int ERR_UNKNOWN = -5;

	public static void main(String args[]) throws Exception {
		MainCP main = new MainCP();
		main.start();
	}

	MainCP() {

	}

	private void start() throws Exception {
		if (Config.RESOURCE_BASE == null || "".equals(Config.RESOURCE_BASE)) {
			System.err.println("get init parameter \"resourceBase\" error");
			System.exit(ERR_START);
		}
		
		//Logo.print();
		Booter.start();
	}
}