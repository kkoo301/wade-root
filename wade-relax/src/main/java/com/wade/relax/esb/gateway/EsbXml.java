package com.wade.relax.esb.gateway;

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
 * Copyright: Copyright (c) 2017 Asiainfo
 *
 * @desc: ESB配置文件解析
 * @auth: steven.zhou
 * @date: 2017-04-07
 */
public final class EsbXml {
	
	private static final Logger LOG = LoggerFactory.getLogger(EsbXml.class);
	
	private static final String ESB_XML = "esb.xml";
	
	private static final Map<String, String> MAPPING = new HashMap<String, String>();
	
	public static final String getClusterName() {
		String serverName = System.getProperty(Constants.SERVER_NAME, "");
		return MAPPING.get(serverName);
	}
	
	public void load() {
		
		SAXBuilder builder = new SAXBuilder();

		InputStream is = null;
		try {
			is = EsbXml.class.getClassLoader().getResourceAsStream(ESB_XML);
			Document doc = builder.build(is);
			Element root = doc.getRootElement();
			
			loadCluster(root);
		} catch (Exception e) {
			LOG.error("加载" + ESB_XML + "配置文件出错!", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				LOG.error("加载" + ESB_XML + "配置文件出错!", e);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void loadCluster(Element root) {
		List<Element> clusters = root.getChildren("cluster");
		for (Element cluster : clusters) {
			String clusterName = cluster.getAttributeValue("name");
			
			if (StringUtils.isBlank(clusterName)) {
				throw new NullPointerException("ESB集群名不能为空!");
			}
			
			loadInstance(cluster, clusterName);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadInstance(Element e, String clusterName) {
		List<Element> eInstances = e.getChildren("instance");
		for (Element eInstance : eInstances) {
			String name = eInstance.getAttributeValue("name");
			if (StringUtils.isBlank(name)) {
				throw new NullPointerException("ESB实例名称不能为空!");
			}
			
			MAPPING.put(name, clusterName);
			
		}
	}
	
	public static void main(String[] args) {
		EsbXml xml = new EsbXml();
		xml.load();
		System.out.println("-------------------------");
		System.out.println(MAPPING);
		System.out.println("-------------------------");
	}
}
