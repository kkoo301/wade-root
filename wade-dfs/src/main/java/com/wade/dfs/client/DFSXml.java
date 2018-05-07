package com.wade.dfs.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.wade.dfs.client.util.StringUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DFSXml
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-2-5
 */
public final class DFSXml {
	
	private static final Logger log = Logger.getLogger(DFSXml.class);
	private static final String DSF_XML = "dfs.xml";

	private int timeout;
	
	private Map<String, List<InetSocketAddress>> mapping = new HashMap<String, List<InetSocketAddress>>();
	
	public Map<String, List<InetSocketAddress>> getMapping() {
		return mapping;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void load() {
		
		SAXBuilder builder = new SAXBuilder();
		
		InputStream is = null;
		
		try {
			
			is = DFSXml.class.getClassLoader().getResourceAsStream(DSF_XML);
			Document doc = builder.build(is);
			Element root = doc.getRootElement();
			
			Element e = root.getChild("timeout");
			if (null != e) {
				String sTimeout = e.getTextTrim();
				setTimeout(Integer.parseInt(sTimeout));
			}
			
			loadTrack(root);
			
		} catch (Exception e) {
			log.error("load " + DSF_XML + " failure!", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				log.error("close " + DSF_XML + " failure!", e);	
			}
		}
	}
	
	private void loadTrack(Element parent) {
		
		List<Element> tracks = parent.getChildren("track");
		
		for (Element track : tracks) {
			String name = track.getAttributeValue("name");
			List<Element> addrs = track.getChildren("address");		
			
			List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
			
			for (Element e : addrs) {
		
				String address = e.getTextTrim();
				String[] part = StringUtils.split(address, ':');
				InetSocketAddress isa = new InetSocketAddress(part[0], Integer.parseInt(part[1]));
				list.add(isa);
				
			}
			
			mapping.put(name, list);
		}
		
	}
	
	public static void main(String[] args) {
		DFSXml xml = new DFSXml();
		xml.load();
		
		System.out.println(xml.getMapping());
	}
}
