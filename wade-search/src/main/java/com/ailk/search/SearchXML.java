package com.ailk.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SearchXML
 * @description: 搜索配置类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-8-5
 */
public class SearchXML {
	
	private static final String SEARCH_XML = "search.xml";
	private static final Logger log = Logger.getLogger(SearchXML.class);
	
	/**
	 * 搜索编码与集群的对应关系
	 */
	private Map<String, String> searchCodes = new TreeMap<String, String>();
	
	/**
	 * 集群与成员的对应关系
	 */
	private Map<String, SortedSet<String>> clusters = new HashMap<String, SortedSet<String>>();
	
	/**
	 * 搜索客户端心跳周期
	 */
	private int hbsec = 5;
	
	/**
	 * 搜索客户端连接池大小
	 */
	private int poolSize = 1; 
	
	public void load() {
		
		SAXBuilder builder = new SAXBuilder();
		try {
			
			Document doc = builder.build(SearchXML.class.getClassLoader().getResourceAsStream(SEARCH_XML));
			Element root = doc.getRootElement();
			loadHeartBeatSecond(root);
			loadPoolSize(root);
			loadCode(root);
			loadCluster(root);
			
		} catch (Exception e) {
			log.error("加载" + SEARCH_XML + "配置文件出错!", e);
		}
	}
	
	/**
	 * 获取心跳周期参数
	 * 
	 * @param root
	 */
	public void loadHeartBeatSecond(Element root) {
		Element element = root.getChild("heartbeat-second");
		if (null != element) {
			String hbsec = element.getText();
			this.hbsec = Integer.parseInt(hbsec);
			if (this.hbsec < 2 || this.hbsec > 50) {
				log.error("hbsec illegal, hbsec:" + this.hbsec);
				this.hbsec = 5;
			}
		}
	}
	
	/**
	 * 获取连接数配置参数
	 * 
	 * @param root
	 */
	public void loadPoolSize(Element root) {
		Element element = root.getChild("pool-size");
		if (null != element) {
			String strPoolSize = element.getText();
			this.poolSize = Integer.parseInt(strPoolSize);
			if (this.poolSize < 1 || this.poolSize > 10) {
				log.error("poolSize illegal, poolsize:" + poolSize);
				this.poolSize = 1;
			}
		}
	}
	
	/**
	 * 加载搜索编码与集群的对应关系
	 * 
	 * @param root
	 */
	public void loadCode(Element root) {
		List<Element> elements = root.getChildren("code");
		for (Element element : elements) {
			
			String searchCode = element.getAttributeValue("name"); // 搜索编码
			String connect = element.getAttributeValue("connect"); // 对应集群
			
			if (StringUtils.isBlank(searchCode)) {
				throw new NullPointerException("搜索编码不能为空!");
			}
			
			if (StringUtils.isBlank(connect)) {
				throw new NullPointerException("搜索编码归属的集群配置不能为空!");
			}
			
			searchCodes.put(searchCode, connect);
		}
	}
	
	/**
	 * 加载集群与成员的对应关系
	 * 
	 * @param root
	 */
	public void loadCluster(Element root) {
		List<Element> elements = root.getChildren("cluster"); // 集群列表
		for (Element element : elements) {
			String clusterName = element.getAttributeValue("name"); // 集群名称
			
			if (StringUtils.isBlank(clusterName)) {
				throw new NullPointerException("集群名不能为空!");
			}
			
			SortedSet<String> instances = new TreeSet<String>();
			clusters.put(clusterName, instances);
			
			List<Element> address = element.getChildren("address"); // 集群地址
			for (Element addr : address) {
				String text = addr.getText();
				instances.add(text);
			}
		}
	}

	public Map<String, String> getSearchCodes() {
		return searchCodes;
	}
	
	public Map<String, SortedSet<String>> getClusters() {
		return clusters;
	}

	public int getHbsec() {
		return hbsec;
	}

	public int getPoolSize() {
		return poolSize;
	}
	
	public static void main(String[] args) {
		SearchXML xml = new SearchXML();
		xml.load();
		System.out.println(xml.getSearchCodes());
		System.out.println(xml.getClusters());
	}

}
