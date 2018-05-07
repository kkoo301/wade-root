/**
 * 
 */
package com.ailk.service.invoker.impl;

import org.apache.log4j.Logger;

import com.ailk.common.data.IVisit;
import com.ailk.service.invoker.IMethodIntercept;
import com.ailk.service.session.SessionManager;

/**
 * @author yifur
 *
 */
public class TransactionInvokerIntercept implements IMethodIntercept {
	
	private static final transient Logger log = Logger.getLogger(TransactionInvokerIntercept.class);
	
	/* (non-Javadoc)
	 * @see com.ailk.service.invoker.IMethodIntercept#invokeBefore(java.lang.Object[])
	 */
	@Override
	public boolean invokeBefore(Object... obj) throws Exception {
		boolean result = false;
		
		if (log.isDebugEnabled())
			log.debug("启动事务...");
		
		try {
			IVisit visit = (IVisit) obj[1];
			
			//启动模拟二阶段事务
			SessionManager.getInstance().setContext(obj[0], visit);
			
			result = true;
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	
	/**
	 * invoke
	 * @return
	 * @throws Exception
	 */
	public void invoke(Object... obj) throws Exception {
		if (log.isDebugEnabled())
			log.debug("提交事务...");
		SessionManager.getInstance().commit();
	}
	
	
	/* (non-Javadoc)
	 * @see com.ailk.service.invoker.IMethodIntercept#invokeAfter(java.lang.Object[])
	 */
	@Override
	public boolean invokeAfter(Object... obj) throws Exception {
		try {
			if (log.isDebugEnabled())
				log.debug("提交事务...");
			if (obj[0] != null && obj[0] instanceof Boolean) {
				if (!(Boolean)obj[0])
					SessionManager.getInstance().commit();
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("回滚事务...");
				e.printStackTrace();
			}
			
			//事务回滚
			SessionManager.getInstance().rollback();
			SessionManager.getInstance().destroy();
			throw e;
		} finally {
			if (log.isDebugEnabled())
				log.debug("回收资源...");
			SessionManager.getInstance().destroy();
		}
		return true;
	}

}
