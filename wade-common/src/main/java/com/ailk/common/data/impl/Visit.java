package com.ailk.common.data.impl;

import java.util.HashMap;
import java.util.Map;
import com.ailk.common.data.IVisit;

public class Visit implements IVisit {

	private static final long serialVersionUID = -4452589661660566436L;
	private boolean validate;
	private transient boolean _dirty;
	
	private Map<String, String> attr = new HashMap<String, String>(200);
	
	public Map<String, String> getAll() {
		return attr;
	}
	
	public void putAll(Map<String, String> map) {
		attr.putAll(map);
		_dirty=true;
	}
	
	public void clear(){
		attr.clear();
		validate=false;
		_dirty=true;
	}
	
	public String get(String key) {
		return attr.get(key);
	}
	
	public void set(String key, String value) {
		attr.put(key, value);
		_dirty=true;
	}
	
	public boolean isValidate() {
		return validate;
	}
	
	public void setValidate(boolean validate) {
		this.validate = validate;
		_dirty=true;
	}
	
	public boolean isDirty(){
		return _dirty;
	}
	
	public void setDirty(boolean dirty){
		_dirty=dirty;
	}
	
	public void clearDirty(){
		_dirty=false;
	}
	
	public String toString() {
		return "{validate=" + validate + ",dirty=" + _dirty + ",attrbutes=" + attr.toString() +"}";
	}
	
}
