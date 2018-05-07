/**
 * 
 */
package com.ailk.service.process;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IVisit;
import com.ailk.common.util.Utility;
import com.ailk.service.session.SessionManager;

/**
 * @author liaos
 * 
 */
public class ProcessCallable implements Callable<Boolean> {

	private static final transient Logger log = Logger.getLogger(ProcessCallable.class);

	private IProcess process;
	private IVisit visit;
	private boolean cancel = false;

	public ProcessCallable(IProcess process) throws Exception {
		this.process = process;

		IData input = process.getInput();
		visit = process.createVisit(input);

		process.setPagination(process.createPagination(input));
	}

	@Override
	public Boolean call() throws Exception {
		Boolean result = Boolean.FALSE;
		try {
			// 激活会话,并设置上下文对象，
			SessionManager.getInstance().start();
			SessionManager.getInstance().setContext(this.process, this.visit);
			
			// 进初始化
			this.process.initialize(this.process.getInput());

			this.process.run();

			result = Boolean.TRUE;
		} catch (BaseException e) {
			cancel(true);

			this.process.setResultCode(e.getCode());
			this.process.setResultInfo(e.getInfo());

			if (log.isDebugEnabled())
				log.debug("后台进程异常:[name:" + process.getClass() + "][message:" + e.getClass() + "@" + e.getMessage()
						+ "]");

			result = Boolean.FALSE;
		} catch (Exception e) {
			cancel(true);

			this.process.setResultCode("-1");
			this.process.setResultInfo(Utility.parseExceptionMessage(e));

			if (log.isDebugEnabled())
				log.debug("后台进程异常:[name:" + process.getClass() + "][message:" + e.getClass() + "@" + e.getMessage()
						+ "]");

			result = Boolean.FALSE;
			
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (!isCancel()) {
					SessionManager.getInstance().commit();
				} else {
					SessionManager.getInstance().rollback();
				}
				SessionManager.getInstance().destroy();

				result = Boolean.TRUE;
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("后台进程事务异常:[name:" + process.getClass() + "][message:" + e.getClass() + "@"
							+ e.getMessage() + "]");

				this.process.setResultInfo(Utility.parseExceptionMessage(e));

				result = Boolean.FALSE;
				
				log.error(e.getMessage(), e);
			}
		}

		return result;
	}

	public String getProcessName() {
		return this.process.getClass().getName();
	}

	public boolean isCancel() {
		return this.cancel;
	}

	public void cancel(boolean cancel) {
		this.cancel = cancel;
	}
	
	public String getGroup() {
		return this.process.getGroup();
	}
}
