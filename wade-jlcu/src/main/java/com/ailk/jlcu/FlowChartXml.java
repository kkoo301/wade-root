/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com 
 * http://www.wadecn.com
 */
package com.ailk.jlcu;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ailk.jlcu.mapunit.Buffvar;
import com.ailk.jlcu.mapunit.Case;
import com.ailk.jlcu.mapunit.Link;
import com.ailk.jlcu.mapunit.Varmap;
import com.ailk.jlcu.mapunit.method.ExpressMethod;
import com.ailk.jlcu.mapunit.method.HttpMethod;
import com.ailk.jlcu.mapunit.method.IMethod;
import com.ailk.jlcu.mapunit.method.IMethod.MethodType;
import com.ailk.jlcu.mapunit.method.JavaMethod;
import com.ailk.jlcu.mapunit.method.SubFlowMethod;
import com.ailk.jlcu.mapunit.method.WSMethod;
import com.ailk.jlcu.mapunit.node.ActionNode;
import com.ailk.jlcu.mapunit.node.IFlowNode;
import com.ailk.jlcu.mapunit.node.IFlowNode.NodeType;
import com.ailk.jlcu.mapunit.node.SwitchNode;
import com.ailk.jlcu.util.Constant;

/**
 * 流程图配置解析工具类
 * 
 * @author steven zhou
 * @since 1.0
 */
public class FlowChartXml {

	private Element root;

	/** 流程图开始节点 */
	private IFlowNode startNode;

	/** 流程图节点集合 */
	private Map<String, IFlowNode> nodes;

	/** 流程图连线集合 */
	private Map<String, Link> links;

	/** 流程入参 */
	private List<Buffvar> inDatas;

	/** 流程出参 */
	private Buffvar outData;
	
	/** buff集合 */
	private Map<String, Buffvar> buffs;

	private static final Logger LOG = Logger.getLogger(FlowChartXml.class);
	
	/**
	 * Creates a new FlowChartXml instance by xTransCode
	 * 
	 * @param xTransCode
	 * @throws Exception
	 */
	public FlowChartXml(String xTransCode, String xTransPath) throws Exception {
		
		URL url = this.getClass().getClassLoader().getResource(xTransPath);
		if (null == url) {
			
			//用于适配不同操作系统的文件路径分隔符
			xTransPath = xTransPath.replace("/", File.separator);
			if ("/".equals(xTransPath)) { // for linux root directory
				xTransPath = "./";
			}
			
			url = this.getClass().getClassLoader().getResource(xTransPath);
			if (null == url) {
				throw new IllegalArgumentException("not found the specified root directory: " + xTransPath);
			}
		}
		
		File lcudir = new File(url.toURI());
		File file = lookupLcuFile(lcudir, xTransCode);
		if (null == file) {
			throw new IllegalArgumentException("not found jlcu definition from file: " + xTransCode);
		}
		
		InputStream ins = null;
		Document document = null;
		
		try {
			ins = new FileInputStream(file);
			SAXBuilder builder = new SAXBuilder();
			document = builder.build(new InputStreamReader(ins, Constant.ENCODE));
		} finally {
			if (null != ins) {
				ins.close();
			}
		}
		
		init(document);
	}

	private void init(Document document) throws Exception {
		this.root = document.getRootElement();

		/**init*/
		this.nodes = gainNodeInfo();
		this.links = gainLinkInfo();
		inDatas = new ArrayList<Buffvar>();
		this.buffs = gainInOutBuffInfo();
		joinNodes(nodes, links);
	}

	public Map<String, IFlowNode> getNodes() {
		return nodes;
	}

	public Map<String, Buffvar> getBuffs() {
		return buffs;
	}
	/**
	 * 获取节点信息
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String, IFlowNode> gainNodeInfo() throws Exception {
		
		Map<String, IFlowNode> mNode = new HashMap<String, IFlowNode>();

		Iterator iter = getList(Constant.JLCU_NODE_PATH).iterator();
		while (iter.hasNext()) {
			Element eNode = (Element) iter.next();
			String id = eNode.getAttributeValue(Constant.ATTR_ID);
			String type = eNode.getAttributeValue(Constant.ATTR_TYPE);
			String desc = eNode.getAttributeValue(Constant.ATTR_DESC);
			
			NodeType TYPE = Enum.valueOf(NodeType.class, type.toUpperCase());
			switch (TYPE) {
			case ACTION:
				ActionNode node = new ActionNode(id, desc);
				node.setNodeType(NodeType.ACTION);
				node.setMethods(gainDoMethodInfo(eNode));
				node.setUndoMethods(gainUndoMethodInfo(eNode));
				mNode.put(id, node);
				break;
			case START:
				node = new ActionNode(id, desc);
				node.setNodeType(NodeType.START);
				node.setMethods(gainDoMethodInfo(eNode));
				mNode.put(id, node);
				startNode = node;
				break;
			case END:
				node = new ActionNode(id, desc);
				node.setNodeType(NodeType.END);
				node.setMethods(gainDoMethodInfo(eNode));
				mNode.put(id, node);
				break;
			case SWITCH:
				SwitchNode switchNode = new SwitchNode(id, desc);
				switchNode.setNodeType(NodeType.SWITCH);
				switchNode.setCases(gainSwitchCaseInfo(eNode));
				mNode.put(id, switchNode);
				break;
			default:
				throw new Exception("invalid node type!");
			}
		}
		return mNode;
	}

	/**
	 * 获取链接信息
	 * 
	 * @return
	 */
	private Map<String, Link> gainLinkInfo() {
		Map<String, Link> mLinker = new HashMap<String, Link>();

		Iterator iter = getList(Constant.JLCU_LINK_PATH).iterator();
		while (iter.hasNext()) {
			Element eLink = (Element) iter.next();
			String id = eLink.getAttributeValue(Constant.ATTR_ID);
			String desc = eLink.getAttributeValue(Constant.ATTR_DESC);
			String from = eLink.getAttributeValue(Constant.ATTR_FROM);
			String to = eLink.getAttributeValue(Constant.ATTR_TO);
			mLinker.put(id, new Link(id, desc, from, to));
		}

		return mLinker;
	}

	/**
	 * 获取流程输入、输出参数
	 */
	private Map<String, Buffvar> gainInOutBuffInfo() {
		Map<String, Buffvar> mBuff = new HashMap<String, Buffvar>();
		List vars = getList(Constant.JLCU_BUFFVAR_PATH);
		Iterator iter = vars.iterator();
		while (iter.hasNext()) {
			Element eVar = (Element) iter.next();
			String name = eVar.getAttributeValue(Constant.ATTR_NAME);
			String type = eVar.getAttributeValue(Constant.ATTR_TYPE);
			String iotype = eVar.getAttributeValue(Constant.ATTR_IOTYPE);

			Buffvar buf = new Buffvar(name,type,iotype);

			if (Constant.ATTR_IN.equals(iotype)) {
				inDatas.add(buf);
			} else if (Constant.ATTR_OUT.equals(iotype) && null == outData) {
				outData = buf;
			}else{
				mBuff.put(name, buf);
			}
		}
		return mBuff;
	}

	/**
	 * 获取开始节点
	 * 
	 * @return
	 */
	public IFlowNode getStartNode() {
		return startNode;
	}

	/**
	 * 获取do类型的方法集
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private List<IMethod> gainDoMethodInfo(Element node) throws Exception {
		return gainMethodInfo(node, Constant.JLCU_METHOD_PATH);
	}

	/**
	 * 获取undo类型的方法集
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private List<IMethod> gainUndoMethodInfo(Element node) throws Exception {
		return gainMethodInfo(node, Constant.JLCU_UNDO_METHOD_PATH);
	}

	/**
	 * 获取指定类型的方法集信息
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private List<IMethod> gainMethodInfo(Element node, String methodPath) throws Exception {
		
		List<IMethod> mMethod = new ArrayList<IMethod>();

		List methods = getList(node, methodPath);
		if (null == methods) {
			return mMethod; // 此节点无Method方法
		}
		
		Iterator iter = methods.iterator();
		while (iter.hasNext()) {
			Element eMethod = (Element) iter.next();
			String type = eMethod.getAttributeValue(Constant.ATTR_TYPE);
			List<Varmap> inVars = gainInVarInfo(eMethod);
			Varmap outVar = gainOutVarInfo(eMethod);

			IMethod method = null;
			MethodType TYPE = Enum.valueOf(MethodType.class, type.toUpperCase());
			switch (TYPE) {
			case JAVA:
				String className = eMethod.getAttributeValue(Constant.ATTR_CLASS_NAME);
				String methodName = eMethod.getAttributeValue(Constant.ATTR_METHOD_NAME);
				method = new JavaMethod(inVars, outVar, className, methodName);
				break;
			case EXPRESS:
				String scripts = eMethod.getChild(Constant.ATTR_EXPRESS).getTextTrim();
				method = new ExpressMethod(scripts);
				break;
			case SUBFLOW:
				String path = eMethod.getAttributeValue(Constant.ATTR_PATH);
				method = new SubFlowMethod(inVars, outVar, path);
				break;
			case HTTP:
				String xTransCode = eMethod.getAttributeValue(Constant.ATTR_XTRANSCODE);
				method = new HttpMethod(inVars, outVar, xTransCode);
				break;
			case SERVICE:
				xTransCode = eMethod.getAttributeValue(Constant.ATTR_XTRANSCODE);
				method = new WSMethod(inVars, outVar, xTransCode);
				break;
			default:
				throw new Exception("invalid method type!");
			}
			mMethod.add(method);
		}
		return mMethod;
	}

	/**
	 * 获取输入参数信息
	 * 
	 * @param node
	 * @return
	 */
	private List<Varmap> gainInVarInfo(Element node) {
		
		List<Varmap> ivars = new ArrayList<Varmap>();
		Iterator iter = getList(node, Constant.JLCU_IVAR_PATH).iterator();
		
		while (iter.hasNext()) {
			Element eVar = (Element) iter.next();
			String name = eVar.getAttributeValue(Constant.ATTR_NAME);
			String mapname = eVar.getAttributeValue(Constant.ATTR_MAPNAME);
			String _isclone = eVar.getAttributeValue(Constant.ATTR_ISCLONE);
			boolean isclone = _isclone == null ? false : Boolean.valueOf(_isclone);

			/*//屏蔽表达式操作
			List<String> opers = gainInVarOpers(eVar);*/
			Varmap v = new Varmap(name, mapname);
			v.setIsclone(isclone);
			/*//屏蔽表达式操作
			v.setOpers(opers);*/
			ivars.add(v);
		}

		return ivars;
	}

	/**
	 * 获取输入参数预处理动作
	 * 
	 * @param node
	 * @return
	 */
	private List<String> gainInVarOpers(Element node) {
		List<String> opers = null;
		Iterator iter = getList(node, Constant.JLCU_OPER_PATH).iterator();
		while (iter.hasNext()) {
			if (null == opers) {
				opers = new ArrayList<String>();
			}
			Element eOper = (Element) iter.next();
			String expression = eOper.getChild(Constant.ATTR_EXPRESS).getTextTrim();
			opers.add(expression);
		}

		return opers;
	}

	/**
	 * 获取输出参数信息
	 * 
	 * @param node
	 * @return
	 */
	private Varmap gainOutVarInfo(Element node) {
		
		Element e = getElement(node, Constant.JLCU_OVAR_PATH);
		if (null == e) {
			return null;
		}
		
		String name = e.getAttributeValue(Constant.ATTR_NAME);
		String mapname = e.getAttributeValue(Constant.ATTR_MAPNAME);
		return new Varmap(name, mapname);
	}

	/**
	 * 获取分支条件信息
	 * 
	 * @param node
	 * @return
	 */
	private List<Case> gainSwitchCaseInfo(Element node) {
		List<Case> cases = new ArrayList<Case>();

		Iterator iter = getList(node, Constant.JLCU_SWITCH_PATH).iterator();
		while (iter.hasNext()) {
			Element eCase = (Element) iter.next();
			String linkId = eCase.getAttributeValue(Constant.ATTR_LINKID);
			String expression = eCase.getChild(Constant.ATTR_EXPRESS).getTextTrim();
			cases.add(new Case(linkId, expression));
		}

		return cases;
	}

	/**
	 * 建立节点之间的关系
	 * 
	 * @param nodeMap
	 * @param linkMap
	 */
	private void joinNodes(Map<String, IFlowNode> nodeMap, Map<String, Link> linkMap) {
		
		/** 首先建立所有switch节点下各case后续节点 */
		for (String key : nodeMap.keySet()) {
			IFlowNode node = nodeMap.get(key);
			if (NodeType.SWITCH == node.getNodeType()) {
				SwitchNode switchNode = (SwitchNode) node;

				for (Case c : switchNode.getCases()) {
					Link link = linkMap.get(c.getLinkId());
					if (null == link) {
						throw new NullPointerException("Link not found! id=" + c.getLinkId());
					}
					c.setNext(nodeMap.get(link.getTo()));
					linkMap.remove(c.getLinkId());
				}
			}
		}

		/** 再将剩余连线绑定到对应的节点上 */
		for (String key : linkMap.keySet()) {
			
			Link link = linkMap.get(key);
			if (null == link) {
				throw new NullPointerException("Link not found! id=" + key);
			}
			
			ActionNode fromNode = (ActionNode) nodeMap.get(link.getFrom());
			if (null == fromNode) {
				throw new NullPointerException("Node not found! id=" + link.getFrom());
			}
			
			IFlowNode toNode= nodeMap.get(link.getTo());
			if (null == toNode) {
				throw new NullPointerException("Node not found! id=" + link.getTo());
			}
			
			fromNode.setNext(toNode);
		}
	}

	/**
	 * getList
	 * 
	 * @param propPath
	 * @return
	 */
	private List getList(String propPath) {
		return getList(root, propPath);
	}

	/**
	 * getList
	 * 
	 * @param from
	 * @param propPath
	 * @return
	 */
	private List getList(Element from, String propPath) {
		Element element = from;
		String nodes[] = propPath.split("/");
		for (int i = 0; i < nodes.length - 1; i++) {
			element = element.getChild(nodes[i]);
		}
		if (null != element) {
			return element.getChildren(nodes[nodes.length - 1]);
		} else {
			return new ArrayList();
		}
	}

	/**
	 * getElement
	 * 
	 * @param propPath
	 * @return
	 */
	private Element getElement(String propPath) {
		return getElement(root, propPath);
	}

	/**
	 * getElement
	 * 
	 * @param from
	 * @param propPath
	 * @return
	 */
	private Element getElement(Element from, String propPath) {
		Element element = from;
		String nodes[] = propPath.split("/");
		for (int i = 0; i < nodes.length - 1; i++) {
			element = element.getChild(nodes[i]);
		}
		
		if (null == element) {
			return null;
		}
		return element.getChild(nodes[nodes.length - 1]);
	}

	public List<Buffvar> getInDatas() {
		return inDatas;
	}

	public Buffvar getOutData() {
		return outData;
	}

	/**
	 * 在LCU目录下寻找流程定义文件
	 * 
	 * @param dir
	 * @param xTransCode
	 * @return
	 */
	private File lookupLcuFile(File dir, String xTransCode) {
		File[] files = dir.listFiles();
		if (null == files)
			return null;
		for (File file : files) {
			if (file.isDirectory()) {
				File lcufile = lookupLcuFile(file, xTransCode);
				if (null != lcufile) {
					return lcufile;
				}
			} else if (file.getName().equals(xTransCode + Constant.SUFFIX)) {
				return file;
			}
		}

		return null;
	}
}