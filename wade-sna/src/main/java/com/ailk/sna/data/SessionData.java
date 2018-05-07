package com.ailk.sna.data;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionData implements Serializable{
	
	private static final long serialVersionUID = -2202627119364707280L;
	private transient boolean _dirty;
	
	private ConcurrentHashMap<String,Serializable> attr = new ConcurrentHashMap<String,Serializable>(); 
	
	public Serializable get(String key){
		return attr.get(key);
	}
	
	public Map<String,Serializable> getAll(){
		return attr;
	}
	
	public void put(String key, Serializable value){
		attr.put(key, value);
		_dirty=true; 
	}
	
	public void putAll(Map<? extends String, ? extends Serializable> m){
		attr.putAll(m);
		_dirty=true;
	}
	
	public void remove(String key){
		attr.remove(key);
		_dirty=true;
	}
	
	public void clear(){
		attr.clear();
		_dirty=true;
	}
	
	public boolean contains(Serializable value){
		return attr.contains(value);
	}
	
	public boolean containsKey(String key){
		return attr.containsKey(key);
	}
	
	public Enumeration<String> keys(){
		return attr.keys();
	}
	
	public Enumeration<Serializable> elements(){
		return attr.elements();
	}
	
	public Set<String> keySet(){
		return attr.keySet();
	}
	
	public Set<Entry<String, Serializable>> entrySet(){
		return attr.entrySet();
	}

	public boolean isDirty(){
		return _dirty;
	}
	
	public void setDirty(boolean dirty){
		_dirty=dirty;
	}
	
	public void clearDirty(){
		_dirty = false;
	}
}