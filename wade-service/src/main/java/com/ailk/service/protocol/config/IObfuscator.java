package com.ailk.service.protocol.config;

import java.io.Serializable;
import java.util.Map;

public interface IObfuscator extends Serializable {
	
	public String getName();

	public String getType();
	
	public void setType(String type);

	public String obfuscate(IParamObject po, String value);
	
	public Map<String, String> getAttrs();
	
	public String getAttr(String name);
	
	public void setAttr(Map<String, String> attrs);

}
