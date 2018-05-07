package com.ailk.service.protocol.config.impl;

import com.ailk.service.protocol.config.IObfuscator;
import com.ailk.service.protocol.config.IParamObject;
import com.ailk.service.protocol.config.ITranslator;
import com.ailk.service.protocol.config.IValidator;

public class DefaultParamObject implements IParamObject {

	private static final long serialVersionUID = 1L;
	private String name;
	private String desc;
	private IValidator validator;
	private IObfuscator obfuscator;
	private ITranslator translator;
	private String type;
	private String alias;
	private String value;

	public DefaultParamObject() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setValidator(IValidator validator) {
		this.validator = validator;
	}

	public void setObfuscator(IObfuscator obfuscator) {
		this.obfuscator = obfuscator;
	}
	
	public ITranslator getTranslator() {
		return translator;
	}

	public void setTranslator(ITranslator translator) {
		this.translator = translator;
	}

	public void setType(String type) {
		this.type = type;
	}

	public IObfuscator getObfuscator() {
		return this.obfuscator;
	}

	public IValidator getValidator() {
		return this.validator;
	}

	public String getType() {
		return this.type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
