package com.ailk.common.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.ailk.common.config.CodeCfg;
import com.ailk.common.data.impl.TreeItem;
import com.ailk.common.data.impl.TreeItemComparator;
import com.ailk.common.BaseException;

public class TreeItem implements Serializable, Comparable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2660162042542940097L;

	private static final String[] UNSAFE_NAMES = new String[]{"id", "dataid", "groupid", "text", "href",
		"value", "haschild", "showcheck", "order", "checked",
		"disabled", "expand", "complete"};
	
	private List<TreeItem> items = null;
	private HashMap<String ,String> attributes = null;
	private Hashtable<String, TreeItem> table = null ;
	
	private final TreeItem parent;

	private String id;
	
	private String text;
	
	private String value;

	private String href;

	private String icon;
	
	private String groupid;
	
	private int order;
	
	private boolean hasChild =false;
	
	private boolean expand = false;

	private boolean showCheckBox = false;
	
	//private String checkBoxName;
	
	//private CheckBoxType checkBoxType = CheckBoxType.CheckBox;
	
	private boolean checked = false;
	
	private boolean disabled = false;
	
	private boolean sorted = false;
	
	private TreeItemComparator comparator;
	
	public TreeItem(String id, TreeItem parent, String text) {
		this(id, parent, text, null);
	}
	
	public TreeItem(String id, TreeItem parent, String text, boolean showcheck) {
		this(id, parent, text, null, showcheck);
	}
	public TreeItem(String id, TreeItem parent, String text, boolean showcheck, boolean disabled) {
		this(id, parent, text, null, showcheck, disabled);
	}
	
	public TreeItem(String id, TreeItem parent, String text, String value) {
		this(id, parent, text, value, null);
	}
	
	public TreeItem(String id, TreeItem parent, String text, String value, boolean showcheck) {
		this(id, parent, text, value, null, showcheck);
	}
	public TreeItem(String id, TreeItem parent, String text, String value, boolean showcheck, boolean disabled) {
		this(id, parent, text, value, null, showcheck, disabled);
	}
	public TreeItem(String id, TreeItem parent, String text, String value, String href) {
		this(id, parent, text, value, href, 0);
	}
	
	public TreeItem(String id, TreeItem parent, String text, String value, String href, boolean showcheck) {
		this(id, parent, text, value, href, 0, null, showcheck);
	}
	public TreeItem(String id, TreeItem parent, String text, String value, String href, boolean showcheck, boolean disabled) {
		this(id, parent, text, value, href, 0, null, showcheck, disabled);
	}
	
	public TreeItem(String id, TreeItem parent, String text, String value , String href, int order) {
		this(id, parent, text, value, href, 0, null ,false);
	}
	
	public TreeItem(String id, TreeItem parent, String text, String value , String href, int order ,boolean showcheck) {
		this(id, parent, text, value, href, 0, null, showcheck);
	}

	public TreeItem(String id, TreeItem parent, String text, String value, String href, int order, String icon, boolean showcheck){
		this(id, parent,text, value, href, order, icon, showcheck, false);
	}

	public TreeItem(String id, TreeItem parent, String text, String value, String href, int order, String icon, boolean showcheck, boolean disabled){
		
		if(id==null || "".equals(id)){
			throw new BaseException(CodeCfg.getProperty("com.ailk.common.data.impl.TreeItem.notnull"));
		}
		
		this.id=id;
		this.parent = parent;
		this.text = text;
		this.value = value;
		this.href=href;
		this.order=order;
		this.icon = icon;
		this.showCheckBox=showcheck;
		this.disabled=disabled;
		if (parent != null){
			if(parent.items == null){
				parent.items = new ArrayList<TreeItem>();
			}
			parent.items.add(this);
			this.table = parent.table;
		}else{
			this.table = new Hashtable<String, TreeItem>();
		}
		this.table.put(id, this);
	}
	
	public TreeItem getItemOfTree(String id) {
		if(this.table != null){
			return this.table.get(id);
		}
		return null;
	}
	
	public TreeItem getParent() {
		return parent;
	}
	
	public List<TreeItem> getItems(){
		return items;
	}
	
	public void sortItems(){
		if(!this.sorted){
			if(this.items == null) return;
			if(this.comparator == null){
				comparator = new TreeItemComparator();
			}
			Collections.sort(this.items, comparator);
		}
	}
	
	public String getId(){
		return id;
	}
	
	/*public void setId(String id){
		this.id=id;
	}*/
	
	public String getText() {
		return text;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getValue(){
		return value == null || "".equals(value) ? this.id : value;
	}
	
	public void setValue(String value){
		this.value = value;
	}

	public String getHref() {
		return href;
	}
	
	public void setHref(String href){
		this.href = href;
	}
	
	public boolean isHasChild(){
		return (this.items != null && this.items.size() > 0) || hasChild;
	}
	
	public void setHasChild(boolean hasChild){
		this.hasChild = hasChild;
	}

	public boolean isExpand(){
		return expand;
	}
	
	public void setExpand(boolean expand){
		this.expand = expand;
	}
	
	public boolean isShowCheckBox(){
		return showCheckBox;
	}
	
	public void setShowCheckBox(boolean showCheckBox){
		this.showCheckBox = showCheckBox;
	}
	
	/*
	public String getCheckBoxName(){
		return this.checkBoxName;
	}
	
	public void setCheckBoxName(String checkBoxName){
		this.checkBoxName=checkBoxName;
	}*/
	
	
	public boolean isChecked(){
		return checked;
	}
	
	public void setChecked(boolean checked){
		this.checked = checked;
	}
	
	public boolean isDisabled(){
		return disabled;
	}
	
	public void setDisabled(boolean disabled){
		this.disabled = disabled;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon){
		this.icon = icon;
	}
	
	public String getGroupId(){
		return this.groupid;
	}
	
	public void setGroupId(String groupid){
		this.groupid = groupid;
	}
	
	public int getOrder(){
		return order;
	}
	
	public void setOrder(int order){
		this.order = order;
	}
	

	private static boolean isSafeAttributeName(String name){
		if(name != null && !"".equals(name)){
			boolean found = false;
			for(String n:UNSAFE_NAMES){
				if(n.equals(name))
					found = true;
			}
			return !found;
		}
		return false;
	}
	
	public HashMap<String,String> getAttributes(){
		return this.attributes;
	}
	
	public void setAttribute(String name,String value){
		if(name == null || "".equals(name))return;
		if(value == null || "".equals(value))return;
		
		if(TreeItem.isSafeAttributeName(name)){
			if(this.attributes == null){
				this.attributes = new HashMap<String,String>();
			}
			this.attributes.put(name, value);
		}
	}

	public String toString() {
		return "TreeItem:text-" + getText() + ",href " + getHref() + ",icon-" + getIcon() + ",order-"  +getOrder();
	}
	
	public int compareTo(Object obj){
		if(obj instanceof TreeItem){
			if(this.comparator == null){
				this.comparator = new TreeItemComparator();
			}
	
			return this.comparator.compare(this, obj);
		}
		return -1;
	}
}