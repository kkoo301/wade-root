package com.ailk.service.protocol;

import java.io.Serializable;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IResult;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.protocol.impl.ServiceEntity;

public interface IBaseService extends IResult, Serializable {
	
	public void initialize(IData input) throws Exception;
	
	public ServiceEntity getEntity() throws Exception;
	
	public void setEntity(ServiceEntity entity);
	
	public String getName();
	
	public void setName(String name);
	
	public IVisit createVisit(IData head) throws Exception;
	
	public void setGroup(String group);
	
	public Pagination getPagination();
	
	public void setPagination(Pagination pagination);
	
	public Pagination createPagination(IData head);
	
	public void destroy(IDataInput input, IDataOutput output) throws Exception ;
	
	public boolean hasPriv(String privs) throws Exception;
	
	public void startTrace();
	
	public void stopTrace(boolean success);
	
}
