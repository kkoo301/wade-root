package com.ailk.common.data;

import java.io.Serializable;
import java.util.Map;

public interface IVisit extends Serializable {
	
	public Map<String, String> getAll();
	
	public String get(String key);
	
	public void set(String key, String value);
	
	public boolean isValidate();

	public void setValidate(boolean validate);
	
	public boolean isDirty();
	
	public void setDirty(boolean dirty);
	
	public void clearDirty();
}
