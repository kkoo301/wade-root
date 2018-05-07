package com.ailk.common.data;

import com.ailk.common.data.IData;

public interface IDataContext {
	
	public static final String SUBSYS_CODE = "subSysCode";
	public static final String CONTEXT_NAME = "contextName";
	public static final String PROVINCE_ID = "provinceId";
	public static final String PRODUCT_MODE = "productMode";
	public static final String VERSION = "version";
	public static final String UUID = "uuid";
	public static final String PAGE_NAME = "com.ailk.common.pageName";
	public static final String SERVICE_NAME = "com.ailk.common.serviceName";
	public static final String DATA_COUNT = "dataCount";
	public static final String NEED_COUNT = "needCount";
	public static final String DATA_PAGIN = "dataPagin";
	public static final String PAGIN_START = "paginStart";
	public static final String PAGIN_END = "paginEnd";
	
	//数据总线操作结果
	public static final String X_RESULTCODE = "x_resultcode";
	public static final String X_RESULTINFO = "x_resultinfo";
	public static final String X_ERRORID = "x_errorid";
	public static final String X_ERRORINFO = "x_errorinfo";
    public static final String X_ERRORLEVEL = "x_errorlevel";
    public static final String X_EXCELEVEL = "x_excelevel";
    
    public static final String ERROR_LEVEL_OFF = "OFF";
    public static final String ERROR_LEVEL_LOG = "LOG";
    public static final String ERROR_LEVEL_DEBUG = "DEBUG";
    
    public static final String EXCEPTION_LEVEL_WARN = "warn";
    public static final String EXCEPTION_LEVEL_ERROR = "error";
    
	public boolean isValidate();

	public void setValidate(boolean validate);

	public IData getAttrs();

	public String getAttr(String name, String defval);

	public void setAttr(String name, String value);

	public boolean isNeedCount();
	
	public void setNeedCount(boolean needCount);
	
	public long getDataCount();
	
	public void setDataCount(long count);
	
	public boolean isDataPagin();
	
	public void setDataPagin(boolean pagin);
	
	public long getPaginStart();
	
	public void setPaginStart(long paginStart);
	
	public long getPaginEnd();
	
	public void setPaginEnd(long paginEnd);
	
	public boolean isOK();
	
	public String getResultCode();
	
	public String getResultInfo();
	
	public void setResultCode(String resultCode);
	
	public void setResultInfo(String resultInfo);
	
	public String getErrorId();
	
	public void setErrorId(String errorId);
	
	public String getErrorInfo();
	
	public void setErrorInfo(String error);
	
	public String getErrorLevel();
	
	public void setErrorLevel(String errorLevel);
	
	public String getExceptionLevel();
	
	public void setExceptionLevel(String exceptionLevel);
	
}
