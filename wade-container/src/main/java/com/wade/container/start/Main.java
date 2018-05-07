package com.wade.container.start;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 服务启动类
 * 
 * @author xiedx@asiainfo.com
 * @date 2015-04-17
 */
public class Main {

	private static final int ERR_INVOKE_MAIN = -2;
	private static final int ERR_LOAD = -3;
	private static final int ERR_START = -4;
	private static final int ERR_UNKNOWN = -5;

	public static void main(String args[]) throws Exception {
		Main main = new Main();
		main.load();
		main.start();
	}

	Main() {

	}

	/**
	 * 加载资源
	 * 
	 * @throws Exception
	 */
	private void load() throws Exception {

		// 启动位置
		URL loc = getClass().getProtectionDomain().getCodeSource().getLocation();
		if (loc == null) {
			System.err.println("error get start location");
			System.exit(ERR_LOAD);
		}

		//如果启动路径里有空格，则需要替换空格
		String locFilePath = loc.getPath().replaceAll("%20", " ");

		File source = new File(locFilePath);
		String startJarFile = null;
		if (source.isFile()) {
			startJarFile = source.getAbsolutePath();
			int idx = startJarFile.lastIndexOf(File.separatorChar);
			if (idx > -1) {
				source = new File(startJarFile.substring(0, idx));
			}
		}

		if (source != null && source.isDirectory()) {

			URLClassLoader loader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			Class<URLClassLoader> loaderClass = URLClassLoader.class;
			Method method = loaderClass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);

			File[] files = source.listFiles();
			if (files != null && files.length > 0) {
				String filePath = null;
				for (File file : files) {
					filePath = file.getAbsolutePath();
					if (file.exists() && file.isFile() && (filePath.endsWith(".jar") || filePath.endsWith(".zip")) && !filePath.equals(startJarFile)) {

						method.invoke(loader, file.toURI().toURL());
					}
				}
			}
		}
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