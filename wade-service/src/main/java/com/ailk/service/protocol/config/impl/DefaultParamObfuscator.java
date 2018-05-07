package com.ailk.service.protocol.config.impl;

import java.util.HashMap;
import java.util.Map;

import com.ailk.service.protocol.config.IObfuscator;
import com.ailk.service.protocol.config.IParamObject;

public class DefaultParamObfuscator implements IObfuscator {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private Map<String, String> attrs = new HashMap<String, String>();

	private String replaceFlag = "*";

	public void setReplaceFlag(String flag) {
		this.replaceFlag = flag;
	}

	public DefaultParamObfuscator() {
		this.name = getClass().getName();
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String obfuscate(IParamObject po, String value) {
		// type=pspt|name|mbphone
		String type = po.getObfuscator().getType();
		if (value == null || "".equals(value.trim())) {
			return value;
		} else if ("pspt".equals(type)) {
			if (value.length() == 15) {
				value = replace(value, 13, 15);
			}
			if (value.length() == 18) {
				value = replace(value, 15, 18);
			}
		} else if ("name".equals(type) && value.length() >= 2) {
			value = replace(value, 2, value.length());
		} else if ("mbphone".equals(type) && value.length() > 8) {
			value = replace(value, 5, 8);
			;
		}
		return value;
	}

	private String replace(String value, int begin, int end) {
		StringBuilder out = new StringBuilder();
		out.append(value.substring(0, begin - 1));
		for (int i = begin; i <= end; i++) {
			out.append(replaceFlag);
		}
		out.append(value.substring(end));
		return out.toString();
	}

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public String getAttr(String name) {
		return this.attrs.get(name);
	}

	public void setAttr(Map<String, String> attrs) {
		this.attrs = attrs;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{name=" + getName() + ",");
		sb.append("type=" + getType() + ",");
		sb.append("attrs=" + getAttrs() + "}");
		return sb.toString();
	}
}
