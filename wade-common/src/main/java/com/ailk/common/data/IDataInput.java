package com.ailk.common.data;

import java.io.Serializable;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.Pagination;

public interface IDataInput extends Serializable {
	
	public IData getHead();
	
	public IData getData();
	
	public Pagination getPagination();
	
	public void setPagination(Pagination pagination);

	public String toString();
}
