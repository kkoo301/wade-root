package com.ailk.common.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.ailk.common.config.XMLConfig;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.BaseException;


public class XMLConfig {

	/**
	 * get root element
	 * @param file
	 * @return
	 */
	public static Element getRoot(String file) throws Exception {
		InputStream in = null;
		try {
			in = XMLConfig.class.getClassLoader().getResourceAsStream(file);
			if (in == null) {
				throw new FileNotFoundException();
			}
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			return root;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new BaseException(CodeCfg.getProperty("com.ailk.common.config.XMLConfig.notexist", new String[]{file}));
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new BaseException(CodeCfg.getProperty("com.ailk.common.config.XMLConfig.notparse", new String[]{file}));
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new BaseException(CodeCfg.getProperty("com.ailk.common.config.XMLConfig.notclose", new String[]{file}));
				}
			}
		}
	}
	
	/**
	 * get properties
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public static IData getProperties(Element node) throws Exception {
		IData properties = new DataMap();
		Iterator<?> iter = node.attributeIterator();
		while(iter.hasNext()) {
			Attribute attr = (Attribute) iter.next();
			properties.put(attr.getName(), attr.getValue());
		}

		return properties;
	}
	
	/**
	 * get property
	 * @param root
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static String getProperty(Element root, String xpath) throws Exception {
		Node node = root.selectSingleNode(xpath);
		return node.getText();
	}
	
	
	/**
	 * get elements
	 * @param root
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static IDataset getElements(Element root, String xpath) throws Exception {
		IDataset data = new DatasetList();
		List<?> nodes = root.selectNodes(xpath);
		
		for(Object obj : nodes) {
			IData attrs = getProperties(((Element) obj));
			data.add(attrs);
		}
		
		return data;
	}
	
	public static Node getNode(Element root, String xpath) throws Exception {
		return root.selectSingleNode(xpath);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Node> getNodes(Element root, String xpath) throws Exception {
		return (List<Node>)root.selectNodes(xpath);
	}
	
}
