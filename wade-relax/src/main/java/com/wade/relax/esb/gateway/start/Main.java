package com.wade.relax.esb.gateway.start;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;

import com.wade.relax.esb.gateway.EsbXml;
import com.wade.relax.esb.gateway.ServiceGateway;
import com.wade.relax.esb.gateway.acl.SourceAddressControl;
import com.wade.relax.registry.consumer.ConsumerRuntime;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 *
 * @desc: 服务透传网关启动类
 * @auth: steven.zhou
 * @date: 2016-11-24
 */
public class Main {
	
	/**
	 * 加载资源
	 * 
	 * @throws Exception
	 */
	private void load() throws Exception {

		ProtectionDomain domain = Main.class.getProtectionDomain();
		CodeSource codeSource = domain.getCodeSource();
		URL loc = codeSource.getLocation();
		
		if (null == loc) {
			throw new NullPointerException("获取启动位置发生错误!");
		}
		
		String absolutePath = null;
		File startJarFile = new File(loc.getFile());
		File startJarDirectory = null;
		if (startJarFile.isFile()) {
			absolutePath = startJarFile.getAbsolutePath();
						
			int idx = absolutePath.lastIndexOf(File.separatorChar);
			if (idx > -1) {
				startJarDirectory = new File(absolutePath.substring(0, idx));
				
			}
		}

		System.out.println("启动位置为:    " + absolutePath);
		System.out.println("jar包所在目录: " + startJarDirectory.toString());
		
		if (null == startJarDirectory || !startJarDirectory.isDirectory()) {
			return;
		}
		
		URLClassLoader loader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		Class<URLClassLoader> loaderClass = URLClassLoader.class;
		Method method = loaderClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);

		File[] files = startJarDirectory.listFiles();
		if (null == files || files.length <= 0) {
			return;
		}
		
		System.out.println("开始加载jar包文件...");

		Arrays.sort(files);
		for (File file : files) {
			String filePath = file.getAbsolutePath();
			if (filePath.endsWith(".jar")) {
				
				if (filePath.equals(absolutePath)) {
					continue;
				}
				
				URL url = file.toURI().toURL();
				method.invoke(loader, url);
				System.out.println("loading " + filePath);
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		
		Main main = new Main();
		main.load();
		
		EsbXml xml = new EsbXml();
		xml.load();
		
		String wadeGatewayPort = System.getProperty("wade.esb.gateway.port");
		String wadeGatewayACL = System.getProperty("wade.esb.gateway.acl");
		
		// 初始化源地址控制插件
		SourceAddressControl.initialize(wadeGatewayACL);
		
		// 启动消费者运行时环境
		ConsumerRuntime.start();
		
		int port = Integer.parseInt(wadeGatewayPort);
		if (port < 1024 || port > 0xFFFF) {
			throw new IllegalArgumentException("端口值不合法，必须介于 1025 -> 65535 之间！");
		}
		
		ServiceGateway server = new ServiceGateway();
		server.start(port);
		
	}
	
}
