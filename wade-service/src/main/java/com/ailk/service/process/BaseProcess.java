/**
 * 后台进程服务
 */
package com.ailk.service.process;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.common.Constants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.data.impl.Visit;
import com.ailk.common.logger.AbstractLogger;
import com.ailk.common.logger.ILogger;
import com.ailk.service.invoker.ServiceInvoker;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.session.SessionManager;
import com.ailk.service.session.app.AppInvoker;
import com.ailk.service.session.app.AppSession;

/**
 * @author $Id: BaseProcess.java 2829 2015-05-27 07:59:56Z liaos $
 * 
 */
public class BaseProcess implements IProcess {
	
	private static final Logger log = Logger.getLogger(BaseProcess.class);
	
	private static final long serialVersionUID = 2527888870781746296L;
	
	private String resultCode = "0";
	private String resultInfo = "ok";
	private long resultCount = 0;
	private String group = "";
	private Pagination pagination = null;
	private String name = "";
	private IData input;
	private long timeout = 60 * 10 * 1000;
	private IDataOutput output = null;

	public BaseProcess() {

	}

	@Override
	public void initialize(IData input) throws Exception {

	}
	
	public void process(IData input) throws Exception {
		initialize(input);
		AppSession.getSession().setContext(createVisit(input));
		run();
	}

	public void run() throws Exception {

	}

	/**
	 * start
	 * 
	 * @param visit
	 * @throws Exception
	 */
	public boolean start(IData input) {
		this.input = input;

		boolean result = false;
		
		long start = System.currentTimeMillis();

		try {
			Object object = AppInvoker.invoke(null, this, "process", new Object[] {input});
			output = ServiceInvoker.objectToDataOutput(object, this);
			result = true;
		} catch (Exception e) {
			log.error("ERROR: Process执行异常", e);
		} finally {
			try {
				CacheFactory.destroy();
			} catch (Exception e) {
				log.error("ERROR: 缓存注销异常，不影响业务正常运行", e);
			} finally {
				try {
					AppInvoker.shutdown();
				} catch (Exception e) {
					log.error("ERROR: 线程池注销异常", e);
				} finally {
					ILogger logger = AbstractLogger.getLogger(getClass());
					
					if (null != logger)
						logger.log(this, getClass().getName(), start, (System.currentTimeMillis() - start), null);
				}
			}
			
		}

		return result;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}

	@Override
	public long getResultCount() {
		return this.resultCount;
	}

	@Override
	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IData getInput() {
		return this.input;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getTimeout() {
		return this.timeout;
	}

	@Override
	public IVisit createVisit(IData head) throws Exception {
		IVisit visit = new Visit();

		if (head == null)
			return visit;

		Iterator<?> keys = head.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			visit.set(key, head.getString(key));
		}
		return visit;
	}

	public Pagination createPagination(IData head) {
		Pagination pagination = new Pagination();

		pagination.setCount(head.getLong(Constants.X_PAGINCOUNT));
		pagination.setCurrent(head.getInt(Constants.X_PAGINCURRENT));
		pagination.setPageSize(head.getInt(Constants.X_PAGINSIZE));

		return pagination;
	}

	public static IVisit getVisit() {
		return SessionManager.getInstance().getVisit();
	}

	@Override
	public void destroy() throws Exception {

	}

	/**
	 * has priv
	 */
	public boolean hasPriv(String privs) throws Exception{
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ailk.service.protocol.IBaseService#getEntity()
	 */
	@Override
	public ServiceEntity getEntity() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ailk.service.protocol.IBaseService#setEntity(com.ailk.service.protocol.impl.ServiceEntity)
	 */
	@Override
	public void setEntity(ServiceEntity entity) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ailk.service.protocol.IBaseService#destroy(com.ailk.common.data.IDataInput, com.ailk.common.data.IDataOutput)
	 */
	@Override
	public void destroy(IDataInput input, IDataOutput output) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the output
	 */
	public IDataOutput getOutput() {
		return output;
	}
	
	@Override
	public void startTrace() {
	}
	
	@Override
	public void stopTrace(boolean success) {
		
	}

}
