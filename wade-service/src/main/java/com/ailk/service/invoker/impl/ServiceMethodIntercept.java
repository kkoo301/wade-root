/**
 * 
 */
package com.ailk.service.invoker.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IResult;
import com.ailk.service.invoker.IMethodIntercept;
import com.ailk.service.session.SessionManager;

/**
 * 服务方法调用拦截器
 * 
 * @author yifur
 *
 */
public abstract class ServiceMethodIntercept implements IMethodIntercept {
	
	public void setResultInfo(String info) {
		((IResult)SessionManager.getInstance().peek()).setResultInfo(info);
	}
	
	public void setResultCode(String code) {
		((IResult)SessionManager.getInstance().peek()).setResultCode(code);
	}
	
	
	@Override
	public final boolean invokeBefore(Object... obj) throws Exception {
		return before((String) obj[0], (IData) obj[1], (IData) obj[2]);
	}

	
	/**
	 * 服务方法调用前触发
	 * @param visit
	 * @param input
	 * @throws Exception
	 */
	public abstract boolean before(String svcname, IData head, IData data) throws Exception;
	
	@Override
	public final boolean invokeAfter(Object... obj) throws Exception {
		return after((String) obj[0], (IData) obj[1], (IData) obj[2], (IDataset) obj[3]);
	}
	
	
	/**
	 * 服务方法调用后触发
	 * @param visit
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	public abstract boolean after(String svcname, IData head, IData input, IDataset output) throws Exception;

}
