/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年8月15日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.reader;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.GlobalCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.wade.svf.flow.config.cache.XmlCache;
import com.wade.svf.flow.config.reader.XmlFlowReader;

/**
 * @description
 * 从数据库读取流程配置信息
 */
public final class BizFlowReader extends XmlFlowReader {
	
	private static final Logger log = LoggerFactory.getLogger(BizFlowReader.class);
	
	private static final String FLOW_DATASOURCE = GlobalCfg.getProperty("flow.datasource.name", "cen1");
	
	private static final String SQL = "select FLOW_NAME, FLOW_XML from wd_m_flow where STATUS = 'U'"; 
	
	private Map<String, String> xmls = new HashMap<String, String>(2000);
	
	public BizFlowReader() {
		
	}
	
	/**
	 * 获取所有流程名称
	 * @return
	 */
	public Set<String> getFlowNames() {
		return xmls.keySet();
	}
	
	
	/**
	 * 从数据库加载到内存
	 */
	public void loadFlow() throws Exception {
		Connection conn = null;
		try {
			conn = ConnectionManagerFactory.getConnectionManager().getConnection(FLOW_DATASOURCE);
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				String name = rs.getString("FLOW_NAME");
				String xml = rs.getString("FLOW_XML");
				
				xmls.put(name, xml);
			}
			
			for (Map.Entry<String, String> item : xmls.entrySet()) {
				XmlCache.getInstance().getItem(item.getKey());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e2) {
					log.error("从数据库加载流程配置异常", e2);
				}
			}
		}
	}
	
	
	@Override
	protected Element getRoot(String name) throws Exception {
		try {
			String xml = xmls.get(name);
			if (null == xml || xml.trim().length() == 0) {
				throw new Exception("配置内容为空");
			}
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new StringReader(xml));
			return doc.getRootElement();
		} catch (DocumentException e) {
			throw e;
		}
	}

}
