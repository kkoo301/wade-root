package com.ailk.common.data.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;

public class DataOutput implements IDataOutput {

	private static final long serialVersionUID = 1L;

	private IData head;
	private IDataset data;
	
	public DataOutput() {
		this.head = new DataMap();
		this.data = new DatasetList();
	}
	
	public DataOutput(IData head, IDataset data) {
		this.head = head;
		this.data = data;
	}
	
	public IDataset getData() {
		return data;
	}

	public void setData(IDataset data) {
		this.data = data;
	}
	
	public IData getHead() {
		return this.head;
	}
	
	public void setHead(IData head) {
		this.head = head;
	}
	
	public long getDataCount() {
		return this.head.getLong(Pagination.X_RESULTCOUNT, 0l);
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder(100);
		str.append("{");
		if (this.head != null) {
			str.append("\"head\":" + this.head.toString());
		}
		if (this.data != null) {
			if (this.head != null) {
				str.append(",");
			}
			str.append("\"data\":" + this.data.toString());
		}
		str.append("}");
		
		return str.toString();
	}
}
