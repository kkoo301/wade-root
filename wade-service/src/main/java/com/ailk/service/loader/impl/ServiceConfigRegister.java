package com.ailk.service.loader.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ailk.common.data.IData;
import com.ailk.service.protocol.IBaseService;
import com.ailk.service.protocol.IServiceProtocol;
import com.ailk.service.protocol.config.IServiceResolver;
import com.ailk.service.protocol.config.ServiceResolverFactory;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.protocol.impl.ServiceProtocol;

public class ServiceConfigRegister extends AbstractServiceRegister {
	private transient static final Logger log = Logger.getLogger(ServiceConfigRegister.class);
	private static final String DTD_PATH = "com/ailk/service/loader/impl/serviceconfig.dtd";

	
	
	@Override
	public Map<String, ServiceEntity> loadService() throws Exception {
		long start = System.currentTimeMillis();
		Map<String, ServiceEntity> services = resolve();
		
		log.info("成功注册服务：" + services.size() + "个, 耗时:" + (System.currentTimeMillis() - start) + "毫秒");
		
		return services;
	}
	
	private Map<String, ServiceEntity> resolve() {
		Map<String, ServiceEntity> entity = null;
		IServiceResolver resolver = ServiceResolverFactory.getResolver();
		
		// read serviceconfig.xml
		Element root = getRoot("service/serviceconfig.xml");
		if (root == null) return new HashMap<String, ServiceEntity>(0);
		
		// parse config package
		@SuppressWarnings("unchecked")
		List<Element> svccfg = root.selectNodes("/serviceconfig/config/package");
		Map<String, String> svccfgmap = new HashMap<String, String>(svccfg.size());
		for (Element element : svccfg) {
			svccfgmap.put(element.attributeValue("name"), element.attributeValue("dir"));
		}
		
		// parse service entity
		@SuppressWarnings("unchecked")
		List<Element> svcs = root.selectNodes("/serviceconfig/service/entity");
		if (svcs == null || svcs.isEmpty()) {
			return new HashMap<String, ServiceEntity>(0);
		}
		
		entity = new HashMap<String, ServiceEntity>(svcs.size());
		for (Element element : svcs) {
			ServiceEntity service = new ServiceEntity();
			String svcname = element.attributeValue("name");
			try {
				
				//添加服务属性
				service.getAttributes().putAll(getAttributes(element));
				
				String info = setServiceProtocol(resolver, service, element, svccfgmap);
				
				if ("0".equals(info)) {
					service.setStatus(ServiceEntity.STATUS_ENABLED);
				} else {
					service.setStatus(ServiceEntity.STATUS_DISABLED);
					service.setFileName(info);
					log.error("服务注册失败[" + service.getName() + "]," + service.getFileName());
					continue;
				}
			} catch (Exception e) {
				service.setStatus(-1);
				service.setFileName(e.getMessage());
				log.info("注册服务[" + svcname+ "]失败," + e.getMessage());
				continue;
			}
			
			entity.put(svcname, service);
		}
		
		return entity;
	}
	
	
	/**
	 * set service protocol
	 * @param resolver
	 * @param service
	 * @param element
	 * @param svccfgmap
	 * @return
	 * @throws Exception
	 */
	private String setServiceProtocol(IServiceResolver resolver, ServiceEntity service, Element element, Map<String, String> svccfgmap) throws Exception {
		IServiceProtocol protocol = null;
		
		String group = element.getParent().attributeValue("subsys");
		String svcname = element.attributeValue("name");
		String svcpath = element.attributeValue("svc");
		String classpath = element.attributeValue("path");
		String svcdesc = element.attributeValue("desc");

		if (svcname != null && !"".equals(svcname) && classpath != null && !"".equals(classpath)) {
			if (svcpath != null && !"".equals(svcpath) && svcpath.endsWith(".svc")) {
				svcpath.replaceFirst("/", "");
				int index = svcpath.indexOf("@");
				if (index != -1) {
					String svccfgpath = svccfgmap.get(svcpath.subSequence(0, index));
					if (svccfgpath != null && !"".equals(svccfgpath)) {
						svcpath = svccfgpath + "/" + svcpath.substring(index + 1);
					} else {
						return svccfgpath + " not find";
					}
				}
				protocol = resolver.resolve(svcpath);
				protocol.setPath(classpath);
				protocol.setDesc(svcdesc);
			} else {
				ServiceProtocol svcpro = new ServiceProtocol();
				svcpro.setDesc(svcdesc);
				svcpro.setName(svcname);
				svcpro.setPath(classpath);
				protocol = svcpro;
			}
		} else {
			return svcname + ":" + classpath + " config error";
		}
		
		
		int index = classpath.indexOf("@");
		if (index != -1) {
			String info = setServiceEntityPath(service, classpath.substring(0, index), classpath.substring(index + 1));
			if ("0".equals(info)) {
				service.setFileName(svcpath);
			} else {
				return info;
			}
		} else {
			return svcname + ":" + classpath + " config error";
		}
		service.setName(group + ":" + svcname);
		service.setProtocol(protocol);
		
		return "0";
	}
	
	
	/**
	 * set service entity path
	 * @param service
	 * @param className
	 * @param methodName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private String setServiceEntityPath(ServiceEntity service, String className, String methodName) throws Exception {
		try {
			Class<IBaseService> entityClass = (Class<IBaseService>) Class.forName(className);
			Method entityMethod = entityClass.getMethod(methodName, new Class<?>[] { IData.class });
			
			if (null == entityMethod) {
				throw new Exception("找不到方法名 " + className + "@" + methodName);
			}
			
			entityMethod.setAccessible(true);
			
			service.setEntityClass(entityClass);
			service.setEntityMethod(entityMethod);
			return "0";
		} catch (ClassNotFoundException e) {
			return "class not find " + className + "@" + methodName;
		}
	}

	/**
	 * get root
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	private Element getRoot(String file) {
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream(file);
			if (in == null) {
				throw new FileNotFoundException();
			}
			SAXReader reader = new SAXReader(true);

			// use DTD_PATH
			reader.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId.startsWith("http")) {
						InputStream is = ServiceConfigRegister.class.getClassLoader().getResourceAsStream(DTD_PATH);
						InputSource source = new InputSource(is);
						source.setEncoding("UTF-8");
						return source;
					} else if (systemId.startsWith("classpath:")) {
						String entityPath = "service/" + systemId.substring(10);
						InputStream is = ServiceConfigRegister.class.getClassLoader().getResourceAsStream(entityPath);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * get attributes
	 * @param element
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> getAttributes(Element element) throws Exception {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> attributes = element.attributeIterator();
		Map<String, String> map = new HashMap<String, String>(element.attributeCount());
		while(attributes.hasNext()) {
			Attribute attribute = attributes.next();
			map.put(attribute.getName(), attribute.getValue());
		}
		return map;
	} 
}


