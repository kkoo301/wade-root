/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月18日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.config.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.svf.flow.config.FlowConfig;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.config.cache.EmptyXmlItem;
import com.wade.svf.flow.config.cache.XmlItem;

/**
 * @description
 * 配置缓存
 */
public class XmlFlowReader implements IFlowReader {
	
	private static final Logger log = LoggerFactory.getLogger(XmlFlowReader.class);
	
	
	/**
	 * 读取xml文件，生成XmlItem对象
	 * @param name
	 * @return
	 */
	@Override
	public XmlItem readXml(String name) {
		try {
			return readXml(name, getRoot(name));
		} catch (FileNotFoundException e) {
			String error = "找不到流程配置文件:" + name;
			log.error(error, e);
			return new EmptyXmlItem(error, e);
		} catch (Exception e) {
			String error = "流程文件解析异常, " + name + ":" + e.getMessage();
			log.error(error, e);
			return new EmptyXmlItem(error, e);
		}
	}
	
	
	/**
	 * 获取xml的Root节点
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	protected Element getRoot(String name) throws Exception {
		String fileName = IFlowConfig.CFG_FLOW_NAME + "/" + name.replaceAll("\\.", "/") + ".xml";
		InputStream in = null;
		try {
			in = FlowConfig.class.getClassLoader().getResourceAsStream(fileName);
			if (in == null) {
				throw new FileNotFoundException(fileName);
			}
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			return doc.getRootElement();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (DocumentException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error("流程配置文件读取失败，" + fileName, e);
				}
			}
		}
	}
	
	
	/**
	 * 在classes/flow下查找文件名为流程名的*.xml文件
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private XmlItem readXml(String name, Element root) throws Exception {
		XmlItem item = new XmlItem();
		item.setName(name);
		
		/* xpath="/flow//name" */
		List<Node> elements = root.selectNodes(new StringBuilder(20)
				.append("/").append(IFlowConfig.CFG_FLOW_NAME).append("//")
				.append(IFlowConfig.CFG_NODE_NAME).toString());
		
		item.setNodes(new String[elements.size()]);
		String[] nodes = item.getNodes();
		for (int i = 0, size = elements.size(); i < size; i++) {
			Node node = elements.get(i);

			String nodeType = node.getParent().getName();
			String nodeName = node.getText();
			nodes[i] = nodeType + "/" + nodeName;

			Node parentNode = node.getParent();

			/* xpath="impl" */
			item.getNodeImpls().put(
					nodeName,
					parseNodeImpl(name, parentNode,
							new StringBuilder(10).append(IFlowConfig.CFG_NODE_IMPL).toString()));

			/* xpath="inparam/param" */
			item.getInparams().put(
					nodeName,
					parseParams(name, parentNode,
							new StringBuilder(10).append(IFlowConfig.CFG_NODE_INPARAM)
									.append("/").append(IFlowConfig.CFG_PARAM_NAME)
									.toString()));
			
			
			/* switch节点特殊处理，xpath="case" */
			if (IFlowConfig.CFG_SWITCH_NAME.equals(nodeType)) {
				item.getSwitchcases().put(nodeName, parseCases(name, parentNode));
				continue;
			}

			/* xpath="outparam/param" */
			item.getOutparams().put(
					nodeName,
					parseParams(name, parentNode,
							new StringBuilder(10).append(IFlowConfig.CFG_NODE_OUTPARAM)
									.append("/").append(IFlowConfig.CFG_PARAM_NAME)
									.toString()));

			
			/* xpath="next */
			Node nextNode = parentNode.selectSingleNode(new StringBuilder(10).append(IFlowConfig.CFG_NEXT_NAME).toString());
			if (null == nextNode)
				item.getNexts().put(nodeName, IFlowConfig.CFG_END_NAME);
			else {
				String nextNodeText = nextNode.getText().trim();
				if (nextNodeText.length() > 0) {
					item.getNexts().put(nodeName, nextNodeText);
				} else {
					throw new Exception("next内容不能为空");
				}
			}

			/* xpath="callback */
			Node callbackNode = parentNode.selectSingleNode(new StringBuilder(10).append(IFlowConfig.CFG_CALLBACK_NAME).toString());
			if (null == callbackNode)
				item.getCallbacks().put(nodeName, "");
			else {
				String callbackNodeText = nextNode.getText().trim();
				if (callbackNodeText.startsWith(IFlowConfig.CFG_REF_TAG)) {
					item.getCallbacks().put(nodeName, callbackNodeText.substring(1));
				} else {
					throw new Exception(String.format("callbacks格式不正确，找不到引用的节点%s", callbackNodeText));
				}
			}
		}

		return item;
	}
	
	
	/**
	 * 替换内置变量
	 * @param value
	 * @return
	 */
	private String parseInnerParam(String name, String value) {
		/*value = value.replaceAll("\\{system.time}", String.valueOf(System.currentTimeMillis()));
		value = value.replaceAll("\\{system.thread}", String.valueOf(Thread.currentThread().getId()));
		value = value.replaceAll("\\{flow.name}", name);*/
		return value;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, String>> parseCases(String name, Node node) throws Exception {
		List<Node> valueNodes = node.selectNodes("case/value");
		List<Node> nextNodes = node.selectNodes("case/next");
		List<Map<String, String>> list = new ArrayList<Map<String, String>>(10);

		for (int i = 0, size = valueNodes.size(); i < size; i++) {
			Node valueNode = valueNodes.get(i);
			Node nextNode = nextNodes.get(i);

			Map<String, String> caseValue = new HashMap<String, String>(3);

			if (null != valueNode) {
				caseValue.put(IFlowConfig.CFG_VALUE_NAME, parseInnerParam(name, valueNode.getText().trim()));

				String nextValue = null == nextNode ? "end" : nextNode.getText().trim();
				if (nextValue.startsWith(IFlowConfig.CFG_REF_TAG)) {
					caseValue.put(IFlowConfig.CFG_NEXT_NAME, nextValue.substring(1));
				} else {
					throw new Exception(String.format("switch/case格式不正确，找不到引用的节点%s", nextValue));
				}
			}

			list.add(caseValue);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends com.wade.svf.flow.node.Node<?, ?>> parseNodeImpl(String name, Node node, String xpath) throws Exception {
		Node implNode = node.selectSingleNode(xpath);
		if (null == implNode) {
			return null;
		}
		
		String implClass = implNode.getText();
		if (null == implClass || implClass.trim().length() == 0) {
			return null;
		}

		Class<? extends com.wade.svf.flow.node.Node<?, ?>> clazz = null;
		try {
			clazz = (Class<? extends com.wade.svf.flow.node.Node<?, ?>>) Class.forName(implClass);
		} catch (Exception e) {
			log.error("解析流程节点失败,无法创建指定的节点类型,{}.{}.{}", new String[] {name, xpath, implClass }, e);
			throw e;
		}

		return clazz;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, String>> parseParams(String name, Node node, String xpath) {
		List<Node> inparamNodes = node.selectNodes(xpath);
		Map<String, Map<String, String>> inparam = new HashMap<String, Map<String, String>>(10);
		for (Node paramNode : inparamNodes) {
			Node keyNode = paramNode.selectSingleNode(IFlowConfig.CFG_KEY_NAME);
			Node valueNode = paramNode.selectSingleNode(IFlowConfig.CFG_VALUE_NAME);

			if (null != keyNode) {
				String key = keyNode.getText();
				if (null != key && key.length() > 0) {
					String value = (null == valueNode) ? "" : parseInnerParam(name, valueNode.getText().trim());
					
					Map<String, String> param = new HashMap<String, String>(10);
					param.put(IFlowConfig.CFG_KEY_NAME, key);
					param.put(IFlowConfig.CFG_VALUE_NAME, value);
					
					inparam.put(key.trim(), param);
				}
			}

		}
		return inparam;
	}

}
