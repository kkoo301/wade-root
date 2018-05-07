package com.wade.log.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.wade.log.ILogHandler;
import com.wade.log.ILogServerListener;
import com.wade.log.impl.LogServerListener;
import com.wade.log.util.Util;

/**
 * 日志服务端配置解析类
 * @author Shieh
 *
 */
public class LogServerXml
{
	private static transient final Logger log = Logger.getLogger(LogServerXml.class);
	private static final String LOG_SERV_FILENAME = "log-server.xml";
	
	private static LogServerXml instance = new LogServerXml();
	private static Element root;
	private static Document document;

	private LogServerXml(){
		
		SAXBuilder builder = new SAXBuilder();
		InputStream ins = null;
		
		try{
			String xmlFilePath = System.getProperty("wade.log.server.xml");
			if(null != xmlFilePath){
				ins = new FileInputStream(xmlFilePath);
			}else{
				ins = LogServerXml.class.getClassLoader().getResourceAsStream(LOG_SERV_FILENAME);
			}
			
			if (null == ins) {
				throw new FileNotFoundException(xmlFilePath);
			}
			
			document = builder.build(ins);
			root = document.getRootElement();
			
		}catch(Exception ex){
			log.error("日志服务配置文件解析错误", ex);
		}finally{
			if (null != ins) {
				try {
					ins.close();
				} catch (IOException e) {
					log.error("关闭日志服务配置文件解析句柄错误!", e);
				}
			}
		}
	}
	
	public static final LogServerXml getInstance(){
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	private List getList(Element from, String propPath) {
		Element element = from;
		String[] nodes = propPath.split("/");
		for (int i = 0; i < nodes.length - 1; i++) {
			element = element.getChild(nodes[i]);
		}
		if (null != element) {
			return element.getChildren(nodes[(nodes.length - 1)]);
		}
		return new ArrayList();
	}
	
	private String logDirectory;
	public String getLogDirectory(){
		if(logDirectory != null)
			return logDirectory;
		
		String ret = null;
		Element elem = root.getChild("log-directory");
		if(elem != null){
			ret = elem.getValue();
		}
		if(ret != null){
			ret = ret.replace("${HOME}", System.getProperty("user.home"));
		}else{
			ret = System.getProperty("user.dir");
		}
		if(ret == null){
			ret = System.getProperty("java.io.tmpdir");
		}
		
		logDirectory = ret;
		return ret;
	}
	
	List<ILogServerListener> logServerListeners = null;
	
	public List<ILogServerListener> getLogServerListeners() {
		if(logServerListeners != null)
			return logServerListeners;
		
		List<ILogServerListener> listeners = new ArrayList<ILogServerListener>();
		Iterator iter = getList(root, "listener").iterator();
		while (iter.hasNext()) {
			Element elem = (Element) iter.next();
			String port = elem.getAttributeValue("port");
			String protocal = elem.getAttributeValue("protocal");
			
			if(port == null || "".equals(port)){	
				log.error("日志服务监听端口不能为空：" + elem.toString());
				continue;
			}
			
			if(protocal == null || "".equals(protocal)){
				log.error("日志服务监听协议不能为空：" + elem.toString());
				continue;
			}
			
			int servPort = 0;
			try{
				servPort = Integer.parseInt(port);
			}catch(Exception pex0){
				continue;
			}
			
			if(servPort < 0){
				continue;
			}
			
			ILogServerListener listener = new LogServerListener(servPort, Util.getProtocal(protocal));			
			Iterator it = getList(elem, "logger/handler").iterator();
			while(it.hasNext()){
				Element el = (Element) it.next();
				String clazz = el.getAttributeValue("clazz");
				String type = el.getAttributeValue("type");
				String cron = el.getAttributeValue("cron");  //适配jdom老版本 ,不使用 getAttributeValue(name, def)方法
				String spf = el.getAttributeValue("spf");
				
				if(cron == null || "".equals(cron))  //默认值
					cron = "";
				
				if(clazz == null || "".equals(clazz)){
					log.error("日志服务Handler类名不能为空：" + el.toString());
					continue;
				}
				
				if(type == null || "".equals(type)){
					log.error("日志服务Handler类型不能为空：" + el.toString());
					continue;
				}
				
				int spfVal = 0;
				if(spf != null && !"".equals(spf)){
					try{
						spfVal = Integer.parseInt(spf);
					}catch(Exception pex1){
						
					}
				}
				
				try{
					Class<ILogHandler> inst = (Class<ILogHandler>) Class.forName(clazz);
					if(inst != null){
						ILogHandler handler = inst.newInstance();
						handler.setType(type);
						handler.setCron(cron);
						if(spfVal > 0){
							handler.setSPF(spfVal);
						}
						listener.addHandler(type, handler);
					}
				}catch(Exception ex){
					log.error("初始化LogHandler[" + clazz + "]发生错误：", ex);
				}
			}	
			listeners.add(listener);
		}
		
		logServerListeners = listeners;
		
		return listeners;
	}
}