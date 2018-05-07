/**
 * Copyright: Copyright (c) 2012 Asiainfo-Linkage. All rights are reserved.
 */
package com.ailk.common.data.impl;

import java.io.Serializable;

import com.ailk.common.data.impl.DataMap;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataContext;

/**
 * @author Jack Tang
 * @date 2012-5-17
 */
public class DataContext implements IDataContext, Serializable {

	private static final long serialVersionUID = -4879976226336160907L;

	private IData attr = new DataMap();
	private boolean validate = false;

	public DataContext() {
		setAttr("productMode", GlobalCfg.getProperty("productmode", "true"));
		setAttr("provinceId", GlobalCfg.getProperty("province", ""));
		setAttr("subSysCode", GlobalCfg.getProperty("module", ""));
		setAttr("version", GlobalCfg.getProperty("version", "0"));
		setAttr("contextRoot", GlobalCfg.getProperty("contextRoot", ""));
		setAttr("contextName", GlobalCfg.getProperty("contextName", ""));
		setAttr(IDataContext.X_RESULTCODE, "0");
		setAttr(IDataContext.X_RESULTINFO, "ok");
	}
	
	/**
	 * @see com.ailk.common.data.IDataContext#isValidate()
	 */
	
	public boolean isValidate() {
		return validate;
	}

	/**
	 * @see com.ailk.common.data.IDataContext#setValidate(boolean)
	 */
	
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	/**
	 * @see com.ailk.common.data.IDataContext#getAttrs()
	 */
	
	public IData getAttrs() {
		return this.attr;
	}

	/**
	 * @see com.ailk.common.data.IDataContext#getAttr(java.lang.String, java.lang.String)
	 */
	
	public String getAttr(String name, String defval) {
		return attr.getString(name, defval);
	}
	
	/**
	 * @see com.ailk.common.data.IDataContext#setAttr(java.lang.String, java.lang.String)
	 */
	public void setAttr(String name, String value) {
		attr.put(name, value);
	}

	
	
	public long getDataCount() {
		return this.attr.getLong(DATA_COUNT, 0l);
	}
	
	public void setDataCount(long count) {
		setAttr(DATA_COUNT, String.valueOf(count));
	}
	
	public boolean isNeedCount() {
		return this.attr.getBoolean(NEED_COUNT, false);
	}
	
	public void setNeedCount(boolean needCount) {
		setAttr(NEED_COUNT, String.valueOf(needCount));
	}
	
	public boolean isDataPagin() {
		return this.attr.getBoolean(DATA_PAGIN, false);
	}

	public void setDataPagin(boolean pagin) {
		setAttr(DATA_PAGIN, String.valueOf(pagin));
	}
	
	public long getPaginStart() {
		return this.attr.getLong(PAGIN_START, 0L);
	}
	
	public void setPaginStart(long paginStart) {
		setAttr(PAGIN_START, String.valueOf(paginStart));
	}
	
	public long getPaginEnd() {
		return this.attr.getLong(PAGIN_END, 0L);
	}
	
	public void setPaginEnd(long paginEnd) {
		setAttr(PAGIN_END, String.valueOf(paginEnd));
	}
	
	public String getResultCode() {
		return this.attr.getString(IDataContext.X_RESULTCODE);
	}
	
	public void setResultCode(String resultCode) {
		this.attr.put(IDataContext.X_RESULTCODE, resultCode);
	}
	
	public String getResultInfo() {
		return this.attr.getString(IDataContext.X_RESULTINFO);
	}
	
	public void setResultInfo(String resultInfo) {
		this.attr.put(IDataContext.X_RESULTINFO, resultInfo);
	}
	
	public String getErrorId(){
		return this.attr.getString(IDataContext.X_ERRORID);
	}
	
	public void setErrorId(String errorId){
		this.attr.put(IDataContext.X_ERRORID, errorId);
	}
	
	public String getErrorInfo() {
		return this.attr.getString(IDataContext.X_ERRORINFO);
	}
	
	public void setErrorInfo(String error) {
		this.attr.put(IDataContext.X_ERRORINFO, error);
	}
	
	public String getErrorLevel() {
		return this.attr.getString(IDataContext.X_ERRORLEVEL);
	}
	
	public void setErrorLevel(String level){
		this.attr.put(IDataContext.X_ERRORLEVEL, level);
	}
	
	public String getExceptionLevel(){
		return this.attr.getString(IDataContext.X_EXCELEVEL);
	}
	
	public void setExceptionLevel(String level){
		this.attr.put(IDataContext.X_EXCELEVEL, level);
	}
	
	public boolean isOK() {
		return "0".equals(getResultCode());
	}
	
	public String toString() {
		return this.attr.toString();
	}
}
