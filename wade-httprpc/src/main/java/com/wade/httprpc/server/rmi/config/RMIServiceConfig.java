/**
 * $
 */
package com.wade.httprpc.server.rmi.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wade.httprpc.server.rmi.config.RMIServiceConfig;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: RMIServiceConfig.java
 * @description: 远程服务解析类，解析service-beans.xml文件
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-28
 */
public final class RMIServiceConfig {
	
	private static transient final Logger log = Logger.getLogger(RMIServiceConfig.class);
	private static final String SERVICE_BEANS_XML = "service-beans.xml";
	
	private static Map<String, Class<?>> services = new HashMap<String, Class<?>>(10000);
	
	private RMIServiceConfig () {
		
	}
	
	
	/**
	 * 根据服务名获取服务实现类
	 * @param serviceName
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static Class<?> getServiceImplClass(String serviceName) throws ClassNotFoundException {
		Class<?> className = services.get(serviceName);
		if (null == className) {
			throw new ClassNotFoundException(String.format("未定义的服务名%s，请检查配置",serviceName));
		}
		return className;
	}
	
	
	
	/**
	 * 解析配置文件service-beans.xml
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static Element getRoot(String file) throws Exception {
		InputStream in = null;
		try {
			in = RMIServiceConfig.class.getClassLoader().getResourceAsStream(file);
			if (in == null) {
				throw new FileNotFoundException();
			}
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			return root;
		} catch (FileNotFoundException e) {
			log.error("配置文件不存在!" + file, e);
			throw e;
		} catch (DocumentException e) {
			log.error("配置文件格式错误!" + file, e);
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error("配置文件读写异常!" + file, e);
				}
			}
		}
	}
	
	
	static {
		try {
			Element root = getRoot(SERVICE_BEANS_XML);
			
			@SuppressWarnings("unchecked")
			List<Element> beans = root.elements();
			for (Element bean : beans) {
				String id = bean.attributeValue("id");
				if (null == id || id.length() == 0) {
					log.info("服务名不能为空");
					
					continue;
				}
				
				
				Class<?> className = services.get(id);
				if (null != className) {
					log.info(String.format("服务名%s已存在，重复的实现类%s", id, className));
					
					continue;
				}
				
				String name = bean.attributeValue("class");
				if (null == name || name.length() == 0) {
					log.info(String.format("服务%s实现类%s不能为空！", id, name));
					
					continue;
				}
				
				try {
					className = Class.forName(name);
				} catch (Exception e) {
					log.info(String.format("服务%s找不到实现类%s！", id, name));
					continue;
				}
				
				services.put(id, className);
			}
			
		} catch (Exception e) {
			log.error("配置文件加载异常!" + SERVICE_BEANS_XML, e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(services);
	}

}
