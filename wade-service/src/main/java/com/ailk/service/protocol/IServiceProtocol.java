package com.ailk.service.protocol;

import java.io.Serializable;
import java.util.List;

import com.ailk.service.protocol.config.IParamObject;

public interface IServiceProtocol extends Serializable {
	
	public String getName();
	
	public String getDesc();
	
	public void setDesc(String desc);
	
	public String getPath();
	
	public void setPath(String path);
	
	public String toXml();
	
	public List<IParamObject> getInputHead();
	
	public List<IParamObject> getOutputHead();
	
	public List<IParamObject> getOutput();
	
	public List<IParamObject> getInput();
	
}
