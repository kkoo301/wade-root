package com.ailk.service.protocol.config;

import java.io.Serializable;

public interface IParamObject extends Serializable {

	public String getName();

	public String getDesc();

	public IValidator getValidator();

	public IObfuscator getObfuscator();
	
	public ITranslator getTranslator();

	public String toString();
	
	public String getType();
	
	public String getAlias();
	
	public String getValue();
}
