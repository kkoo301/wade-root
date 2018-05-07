package com.ailk.service.protocol.impl;

import java.util.ArrayList;
import java.util.List;

import com.ailk.service.protocol.IServiceProtocol;
import com.ailk.service.protocol.config.IParamObject;

public class ServiceProtocol implements IServiceProtocol {
	
	private static final long serialVersionUID = 1L;
	private String protocol = null;
	private String name;
	private String desc;
	private String path;
	private List<IParamObject> input = new ArrayList<IParamObject>();
	private List<IParamObject> output = new ArrayList<IParamObject>();
	private List<IParamObject> inputHead = new ArrayList<IParamObject>();
	private List<IParamObject> outputHead = new ArrayList<IParamObject>();
	
	public ServiceProtocol() {
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getPath() {
		return path;
	}
	
	public List<IParamObject> getInput() {
		return input;
	}
	
	public List<IParamObject> getOutput() {
		return output;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setInput(List<IParamObject> input) {
		this.input = input;
	}

	public void setOutput(List<IParamObject> output) {
		this.output = output;
	}
	
	public List<IParamObject> getInputHead() {
		return this.inputHead;
	}
	
	public void setInputHead(List<IParamObject> inputHead) {
		this.inputHead = inputHead;
	}
	
	public List<IParamObject> getOutputHead() {
		return this.outputHead;
	}
	
	public void setOutputHead(List<IParamObject> outputHead) {
		this.outputHead = outputHead;
	}
	
	public String toXml() {
		return toXml(null);
	}

	public String toXml(String[] attrs) {
		if (this.protocol == null || "".equals(protocol)) {
			StringBuilder sb = new StringBuilder();
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append("\n");
			sb.append("\t").append("<!DOCTYPE service PUBLIC \"-//AILK WADE//WADE 4.0//CN\"").append("\n");
			sb.append("\t").append("\"http://www.wade.com/service/dtd/wade-service.dtd\">").append("\n");
			sb.append("<service>").append("\n");
			
			sb.append("\t").append("<description>"+getDesc()+"</description>").append("\n");
			sb.append("\t").append("<path>"+getPath()+"</path>").append("\n");
			sb.append("\t").append("<head>").append("\n");
			
			if (attrs != null && attrs.length > 0) {
				for (String attr : attrs) {
					sb.append("\t").append(attr).append("\n");
				}
			}
			
			sb.append("\t").append("\t").append("<input>").append("\n");
			for(IParamObject object : this.inputHead) {
				sb.append("\t").append("\t").append("\t").append("<param>").append("\n");
				sb.append("\t").append("\t").append("\t").append("\t").append("<name>").append(object.getName()).append("</name>").append("\n");
				sb.append("\t").append("\t").append("\t").append("\t").append("<type>").append(object.getType()).append("</type>").append("\n");
				if (object.getValue() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<value>").append(object.getValue()).append("</value>").append("\n");
				if (object.getAlias() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<alias>").append(object.getAlias()).append("</alias>").append("\n");
				if (object.getValidator() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<validator>").append(object.getValidator().getType()).append("</validator>").append("\n");
				if (object.getObfuscator() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<obfuscator>").append(object.getObfuscator().getType()).append("</obfuscator>").append("\n");
				sb.append("\t").append("\t").append("\t").append("</param>").append("\n");
			}
			sb.append("\t").append("\t").append("</input>").append("\n");
			
			sb.append("\t").append("\t").append("<output>").append("\n");
			for(IParamObject object : this.outputHead) {
				sb.append("\t").append("\t").append("\t").append("<param>").append("\n");
				sb.append("\t").append("\t").append("\t").append("\t").append("<name>").append(object.getName()).append("</name>").append("\n");
				sb.append("\t").append("\t").append("\t").append("\t").append("<type>").append(object.getType()).append("</type>").append("\n");
				if (object.getValue() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<value>").append(object.getValue()).append("</value>").append("\n");
				if (object.getAlias() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<alias>").append(object.getAlias()).append("</alias>").append("\n");
				if (object.getValidator() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<validator>").append(object.getValidator().getType()).append("</validator>").append("\n");
				if (object.getObfuscator() != null)
					sb.append("\t").append("\t").append("\t").append("\t").append("<obfuscator>").append(object.getObfuscator().getType()).append("</obfuscator>").append("\n");
				sb.append("\t").append("\t").append("\t").append("</param>").append("\n");
			}
			sb.append("\t").append("\t").append("</output>").append("\n");
			
			sb.append("\t").append("</head>").append("\n");
			
			sb.append("\t").append("<input>").append("\n");
			for(IParamObject object : this.input) {
				sb.append("\t").append("\t").append("<param>").append("\n");
				sb.append("\t").append("\t").append("\t").append("<name>").append(object.getName()).append("</name>").append("\n");
				sb.append("\t").append("\t").append("\t").append("<type>").append(object.getType()).append("</type>").append("\n");
				if (object.getAlias() != null)
					sb.append("\t").append("\t").append("\t").append("<alias>").append(object.getAlias()).append("</alias>").append("\n");
				if (object.getValidator() != null)
					sb.append("\t").append("\t").append("\t").append("<validator>").append(object.getValidator().getType()).append("</validator>").append("\n");
				if (object.getObfuscator() != null)
					sb.append("\t").append("\t").append("\t").append("<obfuscator>").append(object.getObfuscator().getType()).append("</obfuscator>").append("\n");
				sb.append("\t").append("\t").append("</param>").append("\n");
			}
			sb.append("\t").append("</input>").append("\n");
			
			sb.append("\t").append("<output>").append("\n");
			for(IParamObject object : this.output) {
				sb.append("\t").append("\t").append("<param>").append("\n");
				sb.append("\t").append("\t").append("\t").append("<name>").append(object.getName()).append("</name>").append("\n");
				sb.append("\t").append("\t").append("\t").append("<type>").append(object.getType()).append("</type>").append("\n");
				if (object.getAlias() != null)
					sb.append("\t").append("\t").append("\t").append("<alias>").append(object.getAlias()).append("</alias>").append("\n");
				if (object.getValidator() != null)
					sb.append("\t").append("\t").append("\t").append("<validator>").append(object.getValidator().getType()).append("</validator>").append("\n");
				if (object.getObfuscator() != null)
					sb.append("\t").append("\t").append("\t").append("<obfuscator>").append(object.getObfuscator().getType()).append("</obfuscator>").append("\n");
				sb.append("\t").append("\t").append("</param>").append("\n");
			}
			sb.append("\t").append("</output>").append("\n");
			
			sb.append("</service>").append("\n");
			this.protocol = sb.toString();
		}
		
		return this.protocol;
	}
	
}
