package com.ailk.service.protocol.config.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ailk.service.protocol.config.CenterInfo;
import com.ailk.service.protocol.config.ICenterResolver;

public class DefaultCenterResolver implements ICenterResolver {
	
	private static final transient Logger log = Logger.getLogger(DefaultCenterResolver.class);
	
	private static final String DTD_PATH = "com/ailk/service/protocol/wade-center.dtd";
	
	private static Map<String, CenterInfo> centerInfos = null;
	

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, CenterInfo> resolve() throws Exception {
		if (null != centerInfos) {
			return centerInfos;
		}
		
		centerInfos = new HashMap<String, CenterInfo>(20);
		
		Element root = getRoot("service/centerservice.xml");
		if (null == root)
			return centerInfos;
		
		List<Element> centers = root.selectNodes("/centerservice/center");
		for (Element center : centers) {
			List<Element> groups = center.selectNodes("group");
			String centerName = center.attributeValue("name");
			String centerDesc = center.attributeValue("desc");
			for (Element group : groups) {
				CenterInfo config = new CenterInfo();
				
				String groupName = group.attributeValue("name");
				String version = group.attributeValue("version");
				
				config.setCenterDesc(centerDesc);
				config.setCenterName(centerName);
				config.setGroupName(groupName);
				config.setVersion(version);
				
				centerInfos.put(groupName, config);
			}
		}
		
		return centerInfos;
	}
	
	
	private Element getRoot(String file) {
		InputStream in = null;
		Element root = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream(file);
			if (in == null) {
				throw new FileNotFoundException();
			}
			SAXReader reader = new SAXReader(true);

			// use DTD_PATH
			reader.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (log.isInfoEnabled())
						log.info("引入外部配置文件:[" + publicId + "][" + systemId + "]");
					
					if (systemId.startsWith("http")) {
						InputStream is = DefaultCenterResolver.class.getClassLoader().getResourceAsStream(DTD_PATH);
						InputSource source = new InputSource(is);
						source.setEncoding("UTF-8");
						return source;
					} else if (systemId.startsWith("classpath:")) {
						String entityPath = "service/" + systemId.substring(10);
						InputStream is = DefaultCenterResolver.class.getClassLoader().getResourceAsStream(entityPath);
						InputSource source = new InputSource(is);
						source.setEncoding("UTF-8");
						return source;
					} else {
						return null;
					}
				}
			});
			root = reader.read(in).getRootElement();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return root;
	}
	
	public static void main(String[] args) throws Exception {
		DefaultCenterResolver resolver = new DefaultCenterResolver();
		Map<String, CenterInfo> centers = resolver.resolve();
		
		Iterator<String> iter = centers.keySet().iterator();
		while(iter.hasNext()) {
			String name = iter.next();
			CenterInfo center = centers.get(name);
			System.out.println(name + ":" + center.getCenterName());
		}
	}
}
