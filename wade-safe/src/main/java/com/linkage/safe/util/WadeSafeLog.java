package com.linkage.safe.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * @author $Id: WadeSafeLog.java 688 2014-09-24 03:15:01Z xiedx $
 *
 */
public class WadeSafeLog {

	private  class WadeLogHander extends Formatter { 
        @Override 
        public String format(LogRecord record) { 
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	Date date = new Date(record.getMillis());
        	//record.getLevel() + ":"+ 
            return sdf.format(date) + ":" + record.getMessage()+"\n"; 
        } 
	};
	private Logger log = null;
	private static WadeSafeLog wsl =null;
	private FileHandler fh = null;
	private static String servName = "";
	//private static FileHandler fileHandler =null;
	public WadeSafeLog(){
		try{
			if(log==null){
				log =Logger.getLogger("wadesafe");
				log.setUseParentHandlers(false);//不在控制台显示
				log.setLevel(Level.INFO);
				if(fh==null)
					//修改日志路径构造 2014/09/23 xiedx
					fh = new FileHandler(FilenameUtils.concat(System.getProperty("user.dir"),
							              ".." + File.separator + "logs" + File.separator 
							              + "wadesafe"+ servName +".log"));
				fh.setLevel(Level.INFO); 
				fh.setFormatter(new WadeLogHander()); 
		        log.addHandler(fh);
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static WadeSafeLog getInstance(){
		if(wsl==null){
			wsl = new WadeSafeLog();
		}
		return wsl;
	}
	public  void Log(String msg){
	    log.info(msg); 
	}
	
	
	public  void LogBySession(HttpServletRequest request,String msg){
		Boolean bShowLog = (Boolean)request.getSession().getAttribute("ShowLog");
		if(bShowLog!=null && bShowLog){
			log.info(msg);
		}
	}
	
	public void Close(){
		log.removeHandler(fh);
		fh.flush();
		fh.close();
		wsl = null;
	}
	
	public static void setServName(String sName,String sPort){
		if(sName!=null){
			servName = servName + '_' + sName;
		}
		if(sPort!=null){
			servName = servName + '_' + sPort;
		}
	}
}
