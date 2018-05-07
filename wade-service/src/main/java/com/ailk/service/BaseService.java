package com.ailk.service;

import java.util.Iterator;

import com.ailk.common.Constants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.data.impl.Visit;
import com.ailk.service.invoker.MethodInterceptFactory;
import com.ailk.service.protocol.IBaseService;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.session.SessionManager;

public class BaseService implements IBaseService {
	
	private static final long serialVersionUID = 1L;
	private String resultCode = "0";
	private String resultInfo = "ok";
	private long resultCount = 0l;
	private String group = "";
	private Pagination pagination = null;
	private String name = "";
	private ServiceEntity entity = null;
	
	public BaseService() {
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void initialize(IData input) throws Exception {
	}
	
	/**
	 * getVisit
	 * @return
	 */
	public static IVisit getVisit(){
		return SessionManager.getInstance().getVisit();
	}
	
	/**
	 * set method intercept
	 * @param clazz
	 */
	public void setMethodIntercept(String clazz) throws Exception {
		getEntity().setMethodIntercept(MethodInterceptFactory.getMethodIntercept(clazz));
	}
	
	/**
	 * 获取服务属性值
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String getAttribute(String name) throws Exception {
		return getEntity().getAttributes().get(name);
	}
	
	/**
	 * get resultcode
	 */
	public String getResultCode() {
		return resultCode;
	}
	
	/**
	 * set resultcode
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	
	/**
	 * get resultinfo
	 */
	public String getResultInfo() {
		return resultInfo;
	}
	
	/**
	 * set resultinfo
	 */
	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}
	
	/**
	 * get resultcount
	 */
	public long getResultCount() {
		return resultCount;
	}
	
	/**
	 * set resultcount
	 */
	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}
	
	/**
	 * get pagination
	 */
	public Pagination getPagination() {
		return pagination;
	}
	
	/**
	 * set pagination
	 */
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
	
	
	/**
	 * create visit
	 * @param input
	 * @return
	 */
	public IVisit createVisit(IData head) throws Exception {
		IVisit visit = new Visit();
		
		if (head == null)
			return visit;
		
		Iterator<?> keys = head.keySet().iterator();
		while(keys.hasNext()) {
			String key = (String) keys.next();
			visit.set(key, head.getString(key));
		}
		return visit;
	}
	
	
	/**
	 * create pagination
	 * @param head
	 * @return
	 */
	public Pagination createPagination(IData head) {
		Pagination pagination = new Pagination();
		
		pagination.setCount(head.getLong(Constants.X_PAGINCOUNT));
		pagination.setCurrent(head.getInt(Constants.X_PAGINCURRENT));
		pagination.setPageSize(head.getInt(Constants.X_PAGINSIZE));
		pagination.setNeedCount(false);
		
		return pagination;
	}
	
	/**
	 * reate data input
	 * @param params
	 * @return
	 */
	public static IDataInput createDataInput() throws Exception {
		return createDataInput(new DataMap());
	}
	
	
	/**
	 * create data input
	 * @param params
	 * @param pagin
	 * @return
	 */
	public static IDataInput createDataInput(IData params) throws Exception {
		return createDataInput(params, null);
	}
	
	/**
	 * create data input
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataInput createDataInput(IData params, Pagination pagination) throws Exception {
		return DataHelper.createDataInput(getVisit(), params, pagination);
	}
	
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	protected String getGroup() {
		return this.group;
	}
	
	
	@Override
	public ServiceEntity getEntity() throws Exception {
		return this.entity;
	}
	
	/**
	 * set entity
	 * @param entity the entity to set
	 */
	public void setEntity(ServiceEntity entity) {
		this.entity = entity;
	}
	
	public boolean hasPriv(String privs) throws Exception{
		return true;
	}
	
	public void destroy(IDataInput input, IDataOutput output) throws Exception {
		this.entity = null;
	}
	
	@Override
	public void startTrace() {
		
	}
	
	@Override
	public void stopTrace(boolean success) {
		
	}
	
}
