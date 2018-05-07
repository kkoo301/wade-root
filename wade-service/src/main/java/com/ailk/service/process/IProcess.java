/**
 * 
 */
package com.ailk.service.process;

import com.ailk.common.data.IData;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.protocol.IBaseService;

/**
 * @author yifur
 *
 */
public interface IProcess extends IBaseService {
	
	public void initialize(IData input) throws Exception;
	
	public String getName();
	
	public void setName(String name);
	
	public IVisit createVisit(IData head) throws Exception;
	
	public void setGroup(String group);
	
	public String getGroup();
	
	public Pagination getPagination();
	
	public void setPagination(Pagination pagination);
	
	public Pagination createPagination(IData head);
	
	public void destroy() throws Exception ;
	
	public void run () throws Exception;
	
	public IData getInput();
	
	public void setTimeout(long timeout);
	
	public long getTimeout();
	
	public boolean hasPriv(String privs) throws Exception;
	
}
