package com.ailk.common.data.impl;

import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;

public class DataInput implements IDataInput {
	private static final long serialVersionUID = 1L;

	private IData data;
	private IData head;
	private Pagination pagin = null;
	
	public DataInput() {
		this.head = new DataMap();
		this.data = new DataMap();
	}
	
	public DataInput(IData head, IData data) {
		this.head = head;
		this.data = data;
	}

	public IData getHead() {
		return this.head;
	}
	
	public void setHead(IData head) {
		this.head = head;
	}

	public IData getData() {
		return this.data;
	}
	
	public void setData(IData data) {
		this.data = data;
	}
	
	public Pagination getPagination() {
		return pagin;
	}
	
	public void setPagination(Pagination pagin) {
		this.pagin = pagin;
		if (pagin != null) {
			this.head.put(Pagination.X_PAGINCOUNT, String.valueOf(pagin.getCount()));
			this.head.put(Pagination.X_PAGINCURRENT, String.valueOf(pagin.getCurrent()));
			this.head.put(Pagination.X_PAGINSELCOUNT, String.valueOf(pagin.isNeedCount()));
			this.head.put(Pagination.X_PAGINSIZE, String.valueOf(pagin.getPageSize()));
		}
	}

	public String toString(){
		StringBuilder str = new StringBuilder(100);
		str.append("{");
		if(this.head!=null){
			str.append("\"head\":" + this.head.toString());
		}
		if(this.data!=null){
			if(this.head!=null){
				str.append(",");
			}
			str.append("\"data\":" + this.data.toString());
		}
		str.append("}");
		
		return str.toString();
	}
}
