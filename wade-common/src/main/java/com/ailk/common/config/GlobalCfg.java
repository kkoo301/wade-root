package com.ailk.common.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public final class GlobalCfg {

	private static transient final Logger log = Logger.getLogger(GlobalCfg.class);
	private static GlobalCfg global = null;
	private static Map<String, String> data = new HashMap<String, String>(50);
	public static final String SESSION_STATUS = "__SESSION_STATUS__";
	public static final String SESSION_ID = "__SESSION_ID__";
	public static final String LOGIN_CUST_ACCOUNT = "__LOGIN_CUST_ACCOUNT__";
	public static final String SESSION_VERIFY_CODE = "__SESSION_VERIFY__";
	public static final String SESSION_VERIFY_COUNT = "__SESSION_VERIFY_COUNT__";
	public static final String SESSION_VISIT="__SESSION_WEBFRAME_VISIT__";
	public static final String ACCESS_BEAN_DEFAULT="com.ailk.base.service.impl.BaseAccess";
	public static final String IMP_EXP_ACTION="com.ailk.common.util.impl.DefaultImpExpAction";
	public static final String IMP_EXP_MANAGER="com.ailk.common.util.impl.DefaultImpExpManager";
	public static final String DEFAULT_FILEACTION="com.ailk.common.util.impl.DefaultFileAction";
	public static final String DEFAULT_FLOWCONFIG="com.ailk.web.view.component.pageflow.DefaultFlowConfig";
	public static final String WELCOME_PAGE_DEFAULT="Base.Welcome";
	public static final String DEFAULT_PAGE_TIPS="com.ailk.web.view.component.tips.impl.DefaultPageTips";
	public static final String DEFAULT_MSGBOX_ACTION="com.ailk.web.view.component.msgbox.impl.DefaultMsgBoxAction";
	public static final String SYS_NAME_DEFAULT="base";
	public static final String DEFAULT_DOWNLOADWEB_TYPE = "jpg.gif.png.xls.xlsx.doc.docx.txt.zip.rar";
	
	public static final String CONTEXT_ROOT = "contextroot";
	public static final String CONTEXT_NAME = "contextname";
	public static final String LANGUAGE = "language";
	public static final String CHARSET = "charset";
	public static final String PRODUCTMODE = "productmode";
	public static final String RELEASE_NUMBER = "release.number";
	
	public static final String WEB_RESOURCE_PATH = "web.resource.path";
	
	public static final String VISIT_NAME="visit.name";
	public static final String ACCESS_BEAN_PATH="acccess.bean.path";
	public static final String WELCOME_PAGE="welcome.page";
	public static final String SYS_NAME="ecl.skin";
	public static final String SYSTEM_PROCESS_NAME = "wade.server.name";
	public static final String WEBSECURITY_LEVEL = "websecurity.filter.check.level";
	public static final String FLOW_OUTSCROLL = "flow.outscroll";
	public static final String OLDAJAXJSONDATA = "oldAjaxJsonData";	
	public static final String DOWNLOADWEB_FILETYPE = "downloadweb.fileType";
	public static final String PAGE_TIPS = "page.tips";
	public static final String MSGBOX_ACTION = "msgbox.action";
	public static final String VALIDATE_RESULTMODE="validate.resultMode";
	public static final String VALIDATE_DISPLAYMODE="validate.displayMode";
	public static final String VALIDATE_SHOWTIP="validate.showTip";
	public static final String VALIDATE_HIDDENTIP="validate.hiddenTip";
			
	

	private GlobalCfg() {	}

	public static GlobalCfg getInstance() {
		return global;
	}

	
	/**
	 * get property
	 * 
	 * @param name
	 * @return
	 */
	public static final String getProperty(String name) {
		return data.get(name);
	}

	/**
	 * get property from global.properties
	 * 
	 * @param name
	 * @param defval
	 * @return
	 */
	public static final String getProperty(String name, String defval) {
		String value = getProperty(name);
		if (value == null) {
			return defval;
		} else {
			return value.trim();
		}
	}
	
	/**
	 * clean up
	 */
	public static final void cleanup() {
		data = null;
	}

	/**
	 * is production
	 * 
	 * @return
	 */
	public static final boolean isProduction() {
		return Boolean.parseBoolean(getProperty(PRODUCTMODE, "true"));
	}

	public static final boolean isOldAjaxJsonData(){
		return Boolean.parseBoolean(getProperty(OLDAJAXJSONDATA, "true"));
	}
	
	/**
	 * get language
	 * @return
	 */
	public static final String getLanguage() {
		return getProperty(LANGUAGE, "zh_CN");
	}
	
	/**
	 * get charset
	 * @return
	 */
	public static final String getCharset() {
		return getProperty(CHARSET, "UTF-8");
	}
	
	/**
	 * 获取发布版本号
	 * @return
	 */
	public static final String getReleaseNumber() {
		//return getProperty(RELEASE_NUMBER, "1");
		return ReleaseCfg.getReleaseNumber();
	}
	
	/**
	 * 获取静态资源路径
	 * @return
	 */
	public static final String getWebResourcePath(){
		return getProperty(WEB_RESOURCE_PATH);
	}

	public static final String getVisitName(){
		return getProperty(VISIT_NAME, SESSION_VISIT);
	}
	
	public static final String getSubSys(String subsys) {
		return getProperty("subsys." + subsys + "addr", subsys);
	}
	
	public static final String getAccessBeanPath(){
		return getProperty(ACCESS_BEAN_PATH,ACCESS_BEAN_DEFAULT);
	}
	
	public static final String getWelcomePage(){
		return getProperty(WELCOME_PAGE,WELCOME_PAGE_DEFAULT);
	}
	
	public static final String getSysName(){
		return getProperty(SYS_NAME,SYS_NAME_DEFAULT);
	}
	
	public static final String getShowShortCut(){
		String showShortCut = getProperty("main.show.shortcutmenus", "true");
		if("true".equals(showShortCut)){
			String showSideBar = getProperty("main.show.sidebar", "true");
			if(!"true".equals(showSideBar)){
				return "false";
			}
		}
		return showShortCut;
	}
	
	public static final String getShowMainSlip(){
		String showSlip = getProperty("main.show.slip","true");
		if("true".equals(showSlip)){
			String showSideBar = getProperty("main.show.sidebar","true");
			if(!"true".equals(showSideBar)){
				return "false";
			}
		}
		return showSlip;
	}
	
	public static final String getImpExpAction(){
		return getProperty("impExp.action",IMP_EXP_ACTION);
	}
	
	public static final String getImpExpManager(){
		return  getProperty("impExp.manager",IMP_EXP_MANAGER);
	}
	
	public static final String getFileAction(){
		return getProperty("fileman.fileaction",DEFAULT_FILEACTION);
	}
	
	public static final String getFlowConfig(){
		return getProperty("pageflow.flowconfig",DEFAULT_FLOWCONFIG);
	}
	
	public static final int getFileMaxSize(){
		return Integer.parseInt(getProperty("file.maxsize","30"));
	}
	
	static {
		global = new GlobalCfg();
		try {
			PropertiesConfig cfg = null;
			InputStream in = GlobalCfg.class.getClassLoader().getResourceAsStream("global.properties");
			cfg = new PropertiesConfig(in);
			data = cfg.getProperties();
		} catch (Exception e){
			log.error(e.getMessage(), e);
		}
	}
}