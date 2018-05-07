package com.wade.log.load;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import com.wade.log.ILogData;
import com.wade.log.ILogHandler;
import com.wade.log.ILogServerListener;
import com.wade.log.config.LogServerXml;

/**
 * 写文件执行句柄类
 * @author Shieh
 *
 */
public class LogWriteHandler{
	
	private static final transient Logger log = Logger.getLogger(LogWriteHandler.class);
	
	/**
	 * 日志缓冲区
	 */
	private static int bufferSize = 8192;
	
	/**
	 * 日志目录
	 */
	private static final String logDirectory = LogServerXml.getInstance().getLogDirectory();
	
	private ILogServerListener listener;
	private ILogHandler handler;
	private FileOutputStream fos;
	private BufferedOutputStream bos;
	private ObjectOutputStream oos;
	private String folderPath;
	private long timeStamp;
	
	public LogWriteHandler(ILogServerListener l, ILogHandler h)
	{
		try
		{
			listener = l;
			handler = h;
			timeStamp = System.currentTimeMillis();
			
			if(!checkFolder()){
				return;
			}

			fos = new FileOutputStream(getFileName());
			bos = new BufferedOutputStream(fos, bufferSize);
			oos = new ObjectOutputStream(bos);
		}catch(Exception ex){
			log.error("创建日志文件写句柄出错：", ex);
		}
	}
	
	/**
	 * 执行目录检查，创建目录
	 * @return
	 */
	private boolean checkFolder(){
		if(logDirectory == null){
			log.error("获取日志写入目录配置为null");
			return false;
		}
		
		folderPath = logDirectory;
		if(!folderPath.endsWith( File.separator )){
			folderPath += File.separator;
		}
		folderPath += listener.getPort();

		try{
			File folder = new File(folderPath);
			if(!folder.exists()){
				synchronized(folder){
					folder.mkdir();
				}
			}
			
			folderPath += File.separator + handler.getType();
			folder = new File(folderPath);
			if(!folder.exists()){
				synchronized(folder){
					folder.mkdir();
				}
			}
		}catch(Exception ex){
			log.error("执行目录[" + folderPath + "]检查出错：", ex);
			return false;
		}
		
		folderPath += File.separator;
		
		return true;
	}
	
	/**
	 * 以当前时间值生成文件名
	 * @return
	 */
	private String getFileName(){
		return folderPath + timeStamp;
	}
	
	public long getTimeStamp(){
		return timeStamp;
	}
	
	public String getFolderPath(){
		return folderPath;
	}
	
	public int getBufferSize(){
		return bufferSize;
	}
	
	public void redirect(){
		try {
			if(oos != null){
				synchronized(oos){  
					//输出上一个文件
					oos.flush();
					oos.close();
					
					//更新timeStamp
					timeStamp = System.currentTimeMillis();
					fos = new FileOutputStream(getFileName());
					bos = new BufferedOutputStream(fos, bufferSize);
					oos = new ObjectOutputStream(bos);
				}
			}
		}catch(Exception ex){
			log.error("切换日志文件写句柄出错：", ex);
		}
	}
	
	public void write(ILogData data){
		try {
			if(oos != null){
				synchronized(oos){
					oos.writeObject(data);
				}
			}
		}catch(Exception ex){
			log.error("写入日志文件出错：", ex);
		}
	}
	
	public void flushAndClose(){
		try {
			if(oos != null){
				synchronized(oos){
					oos.flush();
					oos.close();
				}
			}
		}catch(Exception ex){
			log.error("关闭日志文件写句柄出错：", ex);
		}
	}
}