package com.ailk.service;

import java.io.Serializable;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.session.SessionManager;

public class Context implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7852695635006328486L;
	
	private Pagination pagin;
	
	
	/**
	 * construct function
	 * @param visit
	 */
	public Context(IData head) {
		
	}
	
	public Context() {
	}
	
	/**
	 * get visit
	 * @param cycle
	 */
	public IVisit getVisit() {
		return SessionManager.getInstance().getVisit();
	}
	
	public Pagination getPagination() {
		return this.pagin;
	}
	
	public void setPagination(Pagination pagin) {
		this.pagin = pagin;
	}
	
	/**
	 * create data input
	 * @param data
	 * @return IDataInput
	 */
	public IDataInput createDataInput(IData data) {
		IData ctx = new DataMap();
		ctx.putAll(getVisit().getAll());
		return new DataInput(ctx, data);
	}
}
