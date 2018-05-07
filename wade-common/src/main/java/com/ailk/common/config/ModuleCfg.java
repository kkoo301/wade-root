package com.ailk.common.config;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import com.ailk.common.config.ModuleCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class ModuleCfg {
	
	private static transient final Logger log = Logger.getLogger(ModuleCfg.class);
	private static final String MODULE_CONFIG_FILE = "module.xml";
	private static Element root = null;
	private static ModuleCfg config = new ModuleCfg();

	private ModuleCfg() {}
	
	static {
		try {
			root = XMLConfig.getRoot(MODULE_CONFIG_FILE);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static ModuleCfg getInstance() {
		if (config == null) {
			config = new ModuleCfg();
		}
		return config;
	}
	
	
	/**
	 * 将最后一个"/"转换成"/@"后，以XPATH语法进行查找
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String getProperty(String path) throws Exception {
		String realPath = "";
		int index = path.lastIndexOf("/");
		if (index != -1) {
			realPath = path.substring(0, index) + "/@" + path.substring(index + 1);
		}
		
		Node node = root.selectSingleNode(realPath);
		return node.getText();
	}
	
	
	/**
	 * 直接以XPATH方式查找
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static String getPropByPath(String xpath) throws Exception {
		return XMLConfig.getProperty(root, xpath);
	}
	
	/**
	 * 将path对应元素的所有属性以key=value的方式存在Map里，并返回Map对象
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static IData getProperties(String path) throws Exception {
		Element node = (Element) root.selectSingleNode(path);
		return XMLConfig.getProperties(node);
	}
	
	/**
	 * get elements
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static IDataset getElements(String path) throws Exception {
		String realPath = "";
		if (path.endsWith("/")) {
			realPath = path + "*";
		} else if (path.endsWith("*")) {
			realPath = path;
		} else {
			realPath = path + "/*";
		}
		return XMLConfig.getElements(root, realPath);
	}
	
	public static Element getElementByPath(String xpath) throws Exception {
		return (Element) XMLConfig.getNode(root, xpath);
	}
	
	public static List<Node> getElementsByPath(String xpath) throws Exception {
		return XMLConfig.getNodes(root, xpath);
	}
	
	public static void main(String[] args) throws Exception {
		ModuleCfg.getInstance();
		IData s = ModuleCfg.getProperties("database/eparchy");
		System.out.println(s);
	}

}
