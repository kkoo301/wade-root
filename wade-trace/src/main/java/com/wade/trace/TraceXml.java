package com.wade.trace;

import java.io.FileInputStream;
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

import com.wade.trace.conf.EcsConf;
import com.wade.trace.conf.IbsConf;
import com.wade.trace.conf.PfConf;
import com.wade.trace.conf.UipConf;

import com.wade.trace.sample.ISample;

import com.wade.trace.sample.impl.EcsSample;
import com.wade.trace.sample.impl.IbsSample;
import com.wade.trace.sample.impl.UipSample;
import com.wade.trace.sample.impl.PfSample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TraceXml
 * @description: 追踪配置文件
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class TraceXml {
	
	private static final Logger LOG = LoggerFactory.getLogger(TraceXml.class);
	private static final String TRACE_XML = "trace.xml";
	private static final String DEFAULT_WORK_TRACE = "com.wade.trace.impl.LazyWorkTrace";
	
	private String traceClazz;
	private long exceptionSampleDenom;
	
	private String logDirectory;
	private int maxBackupIndex;
	private int bufferSize;
	
	private Map<String, EcsConf> ecsConfs = new HashMap<String, EcsConf>();
	private Map<String, IbsConf> ibsConfs = new HashMap<String, IbsConf>();
	private Map<String, UipConf> uipConfs = new HashMap<String, UipConf>();
	private Map<String, PfConf>  pfConfs  = new HashMap<String, PfConf>();
	
	private Map<String, String> mapping = new HashMap<String, String>();
		
	public String getTraceClazz() {
		return traceClazz;
	}
	
	public long getExceptionSampleDenom() {
		return exceptionSampleDenom;
	}
	
	public String getLogDirectory() {
		return logDirectory;
	}
	
	public int getMaxBackupIndex() {
		return maxBackupIndex;
	}

	public Map<String, EcsConf> getEcsConf() {
		return ecsConfs;
	}
	
	public Map<String, IbsConf> getIbsConf() {
		return ibsConfs;
	}
	
	public Map<String, UipConf> getUipConf() {
		return uipConfs;
	}
	
	public Map<String, PfConf> getPfConf() {
		return pfConfs;
	}
	
	public Map<String, String> getMapping() {
		return mapping;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	/**
	 * 加载配置文件
	 */
	public void load() {
		
		SAXBuilder builder = new SAXBuilder();
				
		InputStream is = null;
		try {
			
			String traceXmlPath = System.getProperty("wade.trace.xml");
			if (null != traceXmlPath) {
				is = new FileInputStream(traceXmlPath);
			} else {
				is = TraceXml.class.getClassLoader().getResourceAsStream(TRACE_XML);
			}
			
			Document doc = builder.build(is);
			Element root = doc.getRootElement();

			// 追踪实现类
			Element e = root.getChild("clazz");
			if (null != e) {
				traceClazz = e.getText().trim();
			} else {
				traceClazz = DEFAULT_WORK_TRACE;
			}
			
			LOG.info("clazz: {}", traceClazz);
			
			// 异常日志采样率
			Element ex = root.getChild("exception-sample-denom");
			if (null != ex) {
				exceptionSampleDenom = Long.parseLong(ex.getText().trim());
			} else {
				// 在Kafka的性情未完全摸清楚前，为防止异常日志太多，默认先按10%来采样!
				exceptionSampleDenom = 10;
			}
			LOG.info("异常日志采样率: 1/" + exceptionSampleDenom);
			
			loadLogNode(root);
			loadMapping(root);
			
			loadEcsConfig(root);
			loadIbsConfig(root);
			loadUipConfig(root);
			loadPfConfig(root);
			
		} catch (Exception e) {
			LOG.error("Load " + TRACE_XML + " failure!", e);
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				LOG.error("Load " + TRACE_XML + " failure!", e);
			}
		}
	}
	
	private void loadLogNode(Element root) {
		
		Element eLog = root.getChild("log");
		Element eDirectory = eLog.getChild("directory");
		Element eMaxBackupIndex = eLog.getChild("max-backup-index");
		Element eBufferSize = eLog.getChild("buffer-size");
		String strBufferSize = eBufferSize == null ? "8192" : eBufferSize.getText().trim();
		
		logDirectory = eDirectory.getText().trim();
		maxBackupIndex = Integer.parseInt(eMaxBackupIndex.getText().trim());
		bufferSize = Integer.parseInt(strBufferSize);
		
	}
	
	/**
	 * 加载ECS配置
	 * 
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	private void loadEcsConfig(Element root) {
		
		Element eConfig = root.getChild("config");
		if (null == eConfig) {
			return;
		}
		
		List<Element> ecsList = eConfig.getChildren("ecs");
		
		if (null == ecsList) {
			return;
		}
		
		for (Element e : ecsList) {
			
			String serviceName = e.getAttributeValue("service_name");
			String paramNames = e.getAttributeValue("param_names", "");
			String sampleDenom = e.getAttributeValue("sample_denom");
			
			String[] paramNameArray = StringUtils.split(paramNames);
			int intSampleDenom = safeCheck(serviceName, sampleDenom);
			
			ISample ecsSample = new EcsSample(intSampleDenom);
			
			EcsConf conf = new EcsConf();
			conf.setServiceName(serviceName);
			conf.setKeys(paramNameArray);
			conf.setSample(ecsSample);
			
			ecsConfs.put(serviceName, conf);
			
		}
		
	}
	
	/**
	 * 加载IBS配置
	 * 
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	private void loadIbsConfig(Element root) {
		
		Element eConfig = root.getChild("config");
		if (null == eConfig) {
			return;
		}
		
		List<Element> ibsList = eConfig.getChildren("ibs");
		
		if (null == ibsList) {
			return;
		}
		
		for (Element e : ibsList) {
			
			String serviceName = e.getAttributeValue("service_name");
			String paramNames = e.getAttributeValue("param_names", "");
			String sampleDenom = e.getAttributeValue("sample_denom");
			
			String[] paramNameArray = StringUtils.split(paramNames);
			int intSampleDenom = safeCheck(serviceName, sampleDenom);
			
			ISample ibsSample = new IbsSample(intSampleDenom);
			
			IbsConf conf = new IbsConf();
			conf.setServiceName(serviceName);
			conf.setKeys(paramNameArray);
			conf.setSample(ibsSample);
			
			ibsConfs.put(serviceName, conf);
			
		}
		
	}
	
	/**
	 * 加载UIP配置
	 * 
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	private void loadUipConfig(Element root) {
		
		Element eConfig = root.getChild("config");
		if (null == eConfig) {
			return;
		}
		
		List<Element> uipList = eConfig.getChildren("uip");
		
		if (null == uipList) {
			return;
		}
		
		for (Element e : uipList) {
			
			String serviceName = e.getAttributeValue("service_name");
			String paramNames = e.getAttributeValue("param_names", "");
			String sampleDenom = e.getAttributeValue("sample_denom");
			
			String[] paramNameArray = StringUtils.split(paramNames);
			int intSampleDenom = safeCheck(serviceName, sampleDenom);
			
			ISample uipSample = new UipSample(intSampleDenom);
			
			UipConf conf = new UipConf();
			conf.setServiceName(serviceName);
			conf.setKeys(paramNameArray);
			conf.setSample(uipSample);
			
			uipConfs.put(serviceName, conf);
			
		}
		
	}
	
	/**
	 * 加载PF配置
	 * 
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	private void loadPfConfig(Element root) {
		
		Element eConfig = root.getChild("config");
		if (null == eConfig) {
			return;
		}
		
		List<Element> pfList = eConfig.getChildren("pf");
		
		if (null == pfList) {
			return;
		}
		
		for (Element e : pfList) {
			
			String serviceName = e.getAttributeValue("service_name");
			String paramNames = e.getAttributeValue("param_names", "");
			String sampleDenom = e.getAttributeValue("sample_denom");
			
			String[] paramNameArray = StringUtils.split(paramNames);
			int intSampleDenom = safeCheck(serviceName, sampleDenom);
			
			ISample pfSample = new PfSample(intSampleDenom);
			
			PfConf conf = new PfConf();
			conf.setServiceName(serviceName);
			conf.setKeys(paramNameArray);
			conf.setSample(pfSample);
			
			pfConfs.put(serviceName, conf);
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void loadMapping(Element root) {
		
		Element eMapping = root.getChild("mapping");
		List<Element> servers = eMapping.getChildren("server");
		for (Element e : servers) {
			String name = e.getAttributeValue("name");
			String ip = e.getAttributeValue("ip");
			
			name = StringUtils.removeEnd(name, "*");
			mapping.put(name, ip);
			
		}
		
	}
	
	/**
	 * 默认控制采样率在10%以下
	 * 
	 * @param name
	 * @param sampleDenom
	 * @return
	 */
	private static final int safeCheck(String name, String sampleDenom) {
		
		int intSampleDenom = Integer.parseInt(sampleDenom);
		/*
		if (intSampleDenom < 10) {
			intSampleDenom = 10;
			log.warn(name + ", safe warning: sample ratio is too high, set ratio to 10%!");
		}*/
		
		return intSampleDenom;
		
	}
	
	public static void main(String[] args) {
		TraceXml xml = new TraceXml();
		xml.load();
		
		System.out.println(xml.getTraceClazz());
		System.out.println(xml.getLogDirectory());
		System.out.println(xml.getMaxBackupIndex());
		System.out.println(xml.getBufferSize());
		System.out.println(xml.getMapping());
		System.out.println("------------------------------");
		System.out.println(xml.getEcsConf());
		System.out.println(xml.getIbsConf());
		System.out.println(xml.getUipConf());
		System.out.println(xml.getPfConf());
	}
	
}
