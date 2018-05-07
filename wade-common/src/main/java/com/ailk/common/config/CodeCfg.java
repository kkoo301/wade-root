package com.ailk.common.config;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class CodeCfg {
	
	private static transient final Logger log = Logger.getLogger(CodeCfg.class);
	private static transient final Pattern PARAM_PATTERN = Pattern.compile("\\{([\\d]+)\\}");
	
	private static transient final Locale DEFAULT_LOCALE = Locale.forLanguageTag(GlobalCfg.getLanguage().replace('_', '-'));
	private static transient final String CODE_FILE_NAME_PREFIX = "code.";
	private static transient final String CODE_APP_FILE_NAME_PREFIX = "i18n.";
	
	private static Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
	private static CodeCfg code = new CodeCfg();
	//public static final String CODE_DEFAULT_NAME = "default";
	
	static {
		getInstance();
		
		code.init();
		
	}
	
	private CodeCfg(){
		
	}

	private void init(){
		
		URL classesLoc = CodeCfg.class.getResource("/");
		if(classesLoc != null){
			
			//如果启动路径里有空格，则需要替换空格
			String locFilePath = classesLoc.getPath().replaceAll("%20", " ");
			File classesDir = new File(locFilePath);
			if(classesDir != null && classesDir.isDirectory()){
				File[] i18nFiles = classesDir.listFiles(new FileFilter(){

					@Override
					public boolean accept(File file) {
						if( file.isFile() && ( 
										file.getName().startsWith(CODE_FILE_NAME_PREFIX) || file.getName().startsWith(CODE_APP_FILE_NAME_PREFIX) 
									)
								){
							return true;
						}
						return false;
					}
					
				});
				
				if(i18nFiles != null && i18nFiles.length > 0){
					for(File file : i18nFiles){
						String resName = file.getName();
						String locale = resName.substring(resName.lastIndexOf('.') + 1, resName.length());
						Map<String, String> configMap = null;
						
						try {
							configMap = TextConfig.getProperties( resName );
						} catch (Exception e) {
							//if(log.isDebugEnabled()){
								log.error("CodeCfg Resource[" + resName + "] load error:", e);
							//}
						}
						
						if (configMap != null ) {
							Map<String, String> map = data.get(locale);
							if(null == map){
								map = new HashMap<String, String>();
								data.put(locale, map);
							}
							map.putAll(configMap);
						}
					}
				}
			}
			
		}

	}
	
	public static final CodeCfg getInstance() {
		return code;
	}
	

	/**
	 * get property
	 * @param code
	 * @return
	 */
	public static final String getProperty(String code) {
		return getProperty(code, code);
	}

	/**
	 * get property
	 * @param code
	 * @param defval
	 * @return
	 */
	public static final String getProperty(String code, String defval) {
		String value = getProperty(DEFAULT_LOCALE, code, null, null);
		if (value == null) {
			value = defval;
		} else {
			value = value.trim();
		}
		return parse(value, null, value);
	}
	
	
	/**
	 * getProperty
	 * @param code
	 * @param params
	 * @return
	 */
	public static String getProperty(String code, String[] params) {
		return getProperty(DEFAULT_LOCALE, code, params, code);
	}

	/**
	 * 
	 * @param code
	 * @param params
	 * @param defval
	 * @return
	 */
	public static String getProperty(String code, String[] params, String defval) {
		return getProperty(DEFAULT_LOCALE, code, params, defval);
	}
	
	/**
	 * get code.[language] & i18ncode.[language] defined value by code, the value will use
	 * params to parsed it
	 * 
	 * @param locale
	 * @param code
	 * @param data
	 * @param defval
	 * @return
	 */
	public static String getProperty(Locale locale, String code, String[] params, String defval) {
		if (defval == null)
			defval = code;

		if( null == locale ) locale = DEFAULT_LOCALE;
		
		String message = null;
		String localeStr = locale.toString();
		if( localeStr != null && !"".equals(localeStr) ){
			Map<String, String> map = data.get(localeStr);
			if( map != null )
				message = map.get(code);
		}

		if (null == message || "".equals(message.trim())) {
			return defval;
		}

		return parse(message, params, defval);
	}

	/**
	 * 
	 * @param message
	 * @param param
	 * @param defval
	 * @return
	 *  
	 *  xiedx //2015/10 修改字符串处理逻辑
	 */
	public static String parse(String message, String[] param, String defval) {
		if (null == message || "".equals(message.trim())) {
			return defval;
		}
		
		Matcher m = PARAM_PATTERN.matcher(message); 
		
		StringBuffer sb = new StringBuffer(); 
		int idx = -1;
		String paramVal;
		
		while(m.find()){
			idx = Integer.parseInt(m.group(1));
			paramVal = param != null && idx > -1 && idx < param.length ? param[idx] : "";
			
			m.appendReplacement(sb, paramVal);
		}
		
		m.appendTail(sb);

		return sb.toString();
	}
	
}
