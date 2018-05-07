package com.ailk.ant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CheckCrm6xServiceConfigTask extends Task {
	private static final String DTD_PATH = "/com/ai/appframe2/rpc/pageframe/server/services/serviceconfig.dtd";
	
	private static final String req = "com.ai.ipaas.busiframe.service.ServiceRequest";
	private static final String res = "com.ai.ipaas.busiframe.service.ServiceResponse";

	private Element resolve() throws Exception {
		
		String file = "/service/serviceconfig.xml";
		
		InputStream in = null;
		Element root = null;
		
		try {
		
			in = getClass().getClassLoader().getResourceAsStream(file);
			if (null == in) {
				throw new FileNotFoundException("/service/serviceconfig.xml");
			}
			
			SAXReader reader = new SAXReader(true);

			reader.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					
					System.out.println("引入外部配置文件:[" + publicId + "][" + systemId + "]");
					
					if (systemId.startsWith("http")) {
						System.out.println("    uri" + getClass().getResource("/com/ai/appframe2/rpc/pageframe/server/services/serviceconfig.dtd"));
						InputStream is = getClass().getResourceAsStream("/com/ai/appframe2/rpc/pageframe/server/services/serviceconfig.dtd");
						InputSource source = new InputSource(is);
						source.setEncoding("UTF-8");
						return source;
					}
					
					if (systemId.startsWith("classpath:")) {
						String entityPath = "/service/"	+ systemId.substring(10);
						System.out.println("    uri" + getClass().getResource(entityPath));
						InputStream is = getClass().getResourceAsStream(entityPath);
						InputSource source = new InputSource(is);
						source.setEncoding("UTF-8");
						return source;
					}
					
					return null;
					
				}
			});
			
			Document doc = reader.read(in);
			root = doc.getRootElement();
			
			Class reqClass = Class.forName(req);
			
			List<Element> entities = root.selectNodes("/serviceconfig/service/entity");
			for (Element entity : entities) {
				String svcname = entity.attributeValue("name");
				String path = entity.attributeValue("path");
				int index = path.indexOf("@");
				if (index == -1) {
					throw new Exception("服务配置异常, path 属性不合法." + svcname);
				}
				String clazzName = path.substring(0, index);
				String methodName = path.substring(index + 1);
				
				Class clazz = Class.forName(clazzName);
				Method method = clazz.getMethod(methodName, new Class[] {reqClass});
				if (null == method) {
					throw new Exception("服务配置异常, method 输入类型不是ServiceRequest." + svcname);
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
		return root;
	}

	public void execute() throws BuildException {
		try {
			resolve();
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
}