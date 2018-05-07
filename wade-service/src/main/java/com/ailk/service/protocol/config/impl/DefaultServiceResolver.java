package com.ailk.service.protocol.config.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ailk.service.protocol.IServiceProtocol;
import com.ailk.service.protocol.config.IParamObject;
import com.ailk.service.protocol.config.IServiceResolver;
import com.ailk.service.protocol.impl.ServiceProtocol;

public class DefaultServiceResolver implements IServiceResolver {

	private static final long serialVersionUID = 1L;
	private transient static final Logger log = Logger.getLogger(DefaultServiceResolver.class);
	public static Hashtable<String, IServiceProtocol> services = new Hashtable<String, IServiceProtocol>();
	private static final String DTD_PATH = "com/ailk/service/protocol/wade-service.dtd";
	
	private String path;
	private String suffix;
	
	public DefaultServiceResolver() {
		this.path = "service/";
		this.suffix = ".svc";
		initial();
	}
	
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	
	public void setPath(String path) {
		this.path = path;
	}
	
	private void initial() {
	}
	
	public String getName(String xml) {
		String line = "/"; //System.getProperty("file.separator","/");
		String name = xml;
		
		name = name.replaceAll("\\\\", line);
		if (name.endsWith(suffix)) {
			name = name.substring(0, name.length() - this.suffix.length());
		}
		
		int index = name.lastIndexOf("/");
		name = index != -1 ? name.substring(index + 1) : name;
		return name;
	}
	

	public IServiceProtocol resolve(String xml) throws Exception {
		synchronized (services) {
			if (services.containsKey(xml)) {
				return services.get(xml);
			} else {
				String line = "/"; //System.getProperty("file.separator","/");
				xml = xml.replaceAll("\\\\", line);
				if (!xml.endsWith(suffix)) {
					xml = xml + this.suffix;
				}
				
				IServiceProtocol service = create(this.path + xml, getName(xml));
				services.put(xml, service);
				
				//if (log.isDebugEnabled()) log.debug(">>>> resolve service object :" + xml);
				
				return service;
			}
		}
	}
	
	/**
	 * 通过XML定义文件创建对象
	 * @param xml
	 * @return
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	private IServiceProtocol create(String xml, String name) throws Exception {
		ServiceProtocol protocol = new ServiceProtocol();
		
		Element root = getRoot(xml);
		protocol.setName(name);
		//protocol.setPath(getGlobal(root, "path", ""));
		protocol.setDesc(getGlobal(root, "desc", "未定义"));
		protocol.setInputHead(getParams(root, "head/input/param", xml));
		protocol.setOutputHead(getParams(root, "head/output/param", xml));
		protocol.setInput(getParams(root, "input/param", xml));
		protocol.setOutput(getParams(root, "output/param", xml));
		
		return protocol;
	}
	
	@SuppressWarnings("unchecked")
	private List<IParamObject> getParams(Element root, String path, String xml) {
		List<IParamObject> params = new ArrayList<IParamObject>();
		
		List<Node> nodes = root.selectNodes(path);
		
		int index = 0;
		for (Node param : nodes) {
			Node name = param.selectSingleNode("name");
			Node desc = param.selectSingleNode("desc");
			Node type = param.selectSingleNode("type");
			Node value = param.selectSingleNode("value");
			
			if (desc == null || name == null) {
				if (log.isDebugEnabled())
					log.debug("the index [" + index + "] param is not validate at " + xml);
				index ++;
				continue;
			} else {
				DefaultParamObject object = new DefaultParamObject();
				object.setName(name.getText());
				object.setDesc(desc.getText());
				
				if (value != null)
					object.setValue(value.getText());
				
				if (type != null)
					object.setType(type.getText().toLowerCase());
				else
					object.setType("string");
				
				params.add(object);
				
				index ++;
			}
		}
		
		return params;
	}
	
	
	
	
	
	/**
	 * get global
	 * @param root
	 * @param name
	 * @param defval
	 * @return
	 */
	private String getGlobal(Element root, String name, String defval) {
		Node node = root.selectSingleNode(name);
		if (node == null) {
			return defval;
		}
		String value = node.getText();
		if (value == null)
			return defval;
		return value;
	}
	
	/**
	 * get attr
	 * @param root
	 * @param name
	 * @param defval
	 * @return
	 */
	protected String getAttr(Element root, String name, String defval) {
		Element node = (Element)root.selectSingleNode(name);
		if (node == null) {
			return defval;
		}
		String value = node.attributeValue(name);
		if (value == null)
			return defval;
		return value;
	}
	
	/**
	 * get root
	 * @param xml
	 * @return
	 * @throws DocumentException
	 * @throws IOException 
	 */
	private Element getRoot(String file) throws Exception {
		InputStream in = DefaultServiceResolver.class.getClassLoader().getResourceAsStream(file);
		if (in == null) {
			throw new FileNotFoundException("file not find [" + file + "]");
		}
		SAXReader reader = new SAXReader(true);
		
		//use DTD_PATH
		reader.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				
				if (log.isInfoEnabled())
					log.info("引入外部配置文件:[" + publicId + "][" + systemId + "]");
				
				if (systemId.startsWith("http")) {
					InputStream is = DefaultServiceResolver.class.getClassLoader().getResourceAsStream(DTD_PATH);
					InputSource source = new InputSource(is);
					source.setEncoding("UTF-8");
					return source;
				} else if (systemId.startsWith("classpath:")){
					String entityPath = "service/" + systemId.substring(10);
					InputStream is = DefaultServiceResolver.class.getClassLoader().getResourceAsStream(entityPath);
					InputSource source = new InputSource(is);
					source.setEncoding("UTF-8");
					return source;
				} else {
					return null;
				}
			}
		});
		
		Element root = reader.read(in).getRootElement();
		in.close();
		return root;
	}
	
}
