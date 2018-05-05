package com.wade.relax;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.relax.registry.Constants;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: relax.xml解析类
 * @auth: zhoulin2
 * @date: 2016-02-01
 */
public final class RelaxXml {

	private static final Logger LOG = LoggerFactory.getLogger(RelaxXml.class);
	private static final String RELAX_XML = "relax.xml";
	private static final String SERVER_NAME = System.getProperty(Constants.SERVER_NAME, "");
	private static final Map<String, Instance> INSTANCES = new HashMap<String, Instance>();
	private static int xTimeoutMsec;
	
	public static int getxTimeoutMsec() {
		return xTimeoutMsec;
	}

	/**
	 * 获取实例归属的中心名
	 * 
	 * @return
	 */
	public static final String getCenterName() {
		Instance ins = INSTANCES.get(SERVER_NAME);
		if (null == ins) {
			throw new NullPointerException("根据 " + SERVER_NAME + ", 在relax.xml文件中未找到该实例的配置!");
		}
		return ins.clusterName;
	}
	
	/**
	 * 获取实例监听的地址
	 * 
	 * @return
	 */
	public static final String getListenAddress() {
		Instance ins = INSTANCES.get(SERVER_NAME);
		if (null == ins) {
			throw new NullPointerException("根据 " + SERVER_NAME + ", 在relax.xml文件中未找到该实例的配置!");
		}
		return ins.listen;
	}
	
	private static class Instance {
		
		/**
		 * 归属集群
		 */
		private String clusterName;
		
		/**
		 * 监听地址
		 */
		private String listen;
		
		public Instance(String clusterName, String listen) {
			this.clusterName = clusterName;
			this.listen = listen;
		}
		
	}
	
	public void load() {
		
		SAXBuilder builder = new SAXBuilder();

		InputStream is = null;
		try {
			is = RelaxXml.class.getClassLoader().getResourceAsStream(RELAX_XML);
			Document doc = builder.build(is);
			Element root = doc.getRootElement();

			Element e = root.getChild("dtm-timeout-sec");
			if (null != e) {
				String strDtmTimeoutSec = e.getText().trim();
				xTimeoutMsec = Integer.parseInt(strDtmTimeoutSec) * 1000;
			} else {
				xTimeoutMsec = 300 * 1000;
			}
			
			loadCenter(root);
		} catch (Exception e) {
			LOG.error("加载" + RELAX_XML + "配置文件出错!", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				LOG.error("加载" + RELAX_XML + "配置文件出错!", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadCenter(Element root) {
		List<Element> datacenters = root.getChildren("center");
		for (Element center : datacenters) {
			String name = center.getAttributeValue("name");
			if (StringUtils.isBlank(name)) {
				throw new NullPointerException("中心名称不能为空!");
			}
			
			loadInstance(center, name);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadInstance(Element e, String clusterName) {
		List<Element> eInstances = e.getChildren("instance");
		for (Element eInstance : eInstances) {
			String name = eInstance.getAttributeValue("name");
			if (StringUtils.isBlank(name)) {
				throw new NullPointerException("实例名称不能为空!");
			}
			
			String listen = eInstance.getAttributeValue("listen");
			if (StringUtils.isBlank(listen)) {
				throw new NullPointerException("实例监听地址不能为空!");
			}
			
			if (INSTANCES.containsKey(name)) {
				throw new IllegalArgumentException("重复的实例名: " + name);
			}
			
			Instance ins = new Instance(clusterName, listen);
			INSTANCES.put(name, ins);
			
		}
	}
	
	public static void main(String[] args) {
		RelaxXml xml = new RelaxXml();
		xml.load();
		System.out.println("中心名" + RelaxXml.getCenterName());
	}
	
}
