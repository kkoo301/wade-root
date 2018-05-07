/**
 * $
 */
package com.ailk.common.exce;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.config.CodeCfg;
import com.ailk.common.config.SystemCfg;
import com.ailk.common.exce.IExceptionWrapper.ExceptionLevel;
import com.ailk.common.util.Utility;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: WadeExceptionParser.java
 * @description: 针对WADE定制的异常包装类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-11
 */
public class WadeExceptionParser implements IExceptionParser {
	
	private static final Logger log = Logger.getLogger(WadeExceptionParser.class);
	/**
	 * X_RESULTCODE 格式校验正则
	 */
	private static final Pattern PATTERN_RESULTCODE = Pattern.compile("^(-)?[@#\\s\\.:;$\\{\\}\\[\\]a-zA-Z0-9_-]+$");

	@Override
	public IExceptionWrapper parse(Throwable e) {
		return parse(e, null, false, true);
	}

	@Override
	public IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater) {
		return parse(e, codeTranslater, false, true);
	}
	
	@Override
	public IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater, boolean needStack) {
		return parse(e, codeTranslater, false, true);
	}
	
	@Override
	public IExceptionWrapper parse(Throwable e, IExceptionCodeTranslater codeTranslater, boolean needMask, boolean needStack) {
		WadeExceptionWrapper wrapper = new WadeExceptionWrapper();
		wrapper.setCode("-1"); //默认异常编码
		
		Throwable bottom = Utility.getBottomException(e);
		wrapper.setThrowable(bottom);
		
		if(needStack){
			StringWriter sw = new StringWriter();
			bottom.printStackTrace(new PrintWriter(sw));
			String stack = sw.toString();
			wrapper.setStack(stack);
		}
		
		String message = bottom.getMessage();
		
		//Caused by: xxxxException: msg
		if ( null == message || "".equals(message.trim()) ) {
			log.error("No Message Exception", e);
			
			wrapper.setCode("-88");
			wrapper.setInfo( CodeCfg.getProperty("com.ailk.common.util.Utility.syserror") );
			return wrapper;
		}

		if (message.startsWith("baseexception:")) {
			message = message.substring(14);
		} else if (message.startsWith("ClassNotFoundException:")) {
			int index = message.lastIndexOf("ClassNotFoundException:");
			message = "ClassNotFound:" + message.substring(index + "ClassNotFoundException:".length()).trim();
		} else {
			int index = message.lastIndexOf("Exception:");
			if (index != -1) 
				message = message.substring(index + "Exception:".length()).trim();
			
			index = message.lastIndexOf("$Enhance_");
			if (index != -1) {
				//message = message.substring(index).trim();
				wrapper.setInfo( CodeCfg.getProperty("com.ailk.common.util.Utility.syntax") + message);
			}
			
			index = message.lastIndexOf("->");
			if (index != -1)
				message = message.substring(index + 2).trim();
			
			if ( null == message || "".equals(message.trim()) ) {
				log.error("No Message Exception", e);
				
				wrapper.setCode("-88");
				wrapper.setInfo( CodeCfg.getProperty("com.ailk.common.util.Utility.syserror") );
				return wrapper;
			}
			
			/*index = message.indexOf(":");
			if (index != -1)
				message = StringUtils.replaceOnce(message, ":", BaseException.SPLITE_CHART);*/
		}
		
		message = message.replaceAll(",", " ");
		
		int index = message.indexOf(":svc:");
		if (index != -1) {
			message = message.substring(0, index);
			message = message.replaceFirst("@svc@", BaseException.INFO_SPLITE_CHAR);
		}
		
		String[] msgs = message.split(BaseException.SPLITE_CHART);
		int msglength = msgs.length;
		
		String code = "-1", info = message;
		if ( msglength >= 2 ) {
			code = msgs[0] != null && !"".equals(msgs[0].trim()) ? msgs[0] : "-1";
			info = msgs[1];
		}
		
	
		//解析info里的业务异常编码  xiedx 2017/10/30
		int infoSplitIndex = info != null ? info.indexOf(BaseException.INFO_SPLITE_CHAR) : -1;
		if(info != null &&  infoSplitIndex > -1){
			String infoCode = info.substring(0, infoSplitIndex);
			
			//增加的infoCode格式校验  xiedx/2017/12/2
			if( infoCode != null && !"".equals(infoCode.trim()) && PATTERN_RESULTCODE.matcher(infoCode).find()){
				code = infoCode;
				info = info.substring(infoSplitIndex + 1);
				
				wrapper.setLevel(ExceptionLevel.WARN); //设置异常级别为warn
			}else{
				info = infoCode;
			}
			
		}
		
		if( !"-1".equals(code) ){
			String codeInfo = null;
			boolean translateHit = false;

			if( code != null && !"".equals(code.trim()) ){
				
				if(null != codeTranslater){
					codeInfo = codeTranslater.translate(code, msglength > 2 ? msgs[2].split(BaseException.SPLITE_PARAM_CHART) : null, "");
					if( codeInfo != null && !"".equals(codeInfo.trim()) && !code.equals(codeInfo) ){
						translateHit = true;
						info = codeInfo;
					}
				}else{
					codeInfo = CodeCfg.getProperty(code, msglength > 2 ? msgs[2].split(BaseException.SPLITE_PARAM_CHART) : null, "");
					if( codeInfo != null && !"".equals(codeInfo.trim()) && !code.equals(codeInfo) ){
						translateHit = true;
						info = codeInfo;
					}
				}
				
				wrapper.setCode(code);
				wrapper.setCodeTranslateHit(translateHit);
			}
			
			wrapper.setInfo(info);	
		}else{
			wrapper.setCode(code);
			wrapper.setInfo(info);
		}
		
		//隐藏详细错误信息
		if(needMask && !"DEBUG".equals(SystemCfg.errorStackLevel) ){
			wrapper.setInfo( CodeCfg.getProperty("com.ailk.common.util.Utility.syserror") ); 
    	}
		
		//设置target
		String wadeServerName = System.getProperty("wade.server.name");
		if(wadeServerName != null && !"".equals(wadeServerName.trim())){
			wrapper.setTarget(wadeServerName);
			
			if(needStack){
				String stack = wrapper.getStack();
				if(stack != null && !"".equals(stack)){
					wrapper.setStack(wadeServerName + "\n" + stack);
				}
			}
		}
		
		return wrapper;
	}
}
