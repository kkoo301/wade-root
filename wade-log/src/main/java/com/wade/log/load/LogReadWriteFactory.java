package com.wade.log.load;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.wade.log.ILogData;
import com.wade.log.ILogHandler;
import com.wade.log.ILogServerListener;

public class LogReadWriteFactory
{
	private static final transient Logger log = Logger.getLogger(LogReadWriteFactory.class);

	private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100);
	private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	private static Scheduler scheduler = null;
	
	/**
	 * 写文件句柄对象Map
	 */
	private static Map<String, LogWriteHandler> writerMap = new ConcurrentHashMap<String, LogWriteHandler>();
	
	static Pattern numberPattern = Pattern.compile("[0-9]*");
	
	/**
	 * 判断文件名是否是数字
	 * @param fileName
	 * @return
	 */
	static boolean isNumeric(String fileName) {
		Matcher isNum = numberPattern.matcher(fileName);
		if (!isNum.matches()){
			return false;
		}
		return true;
	}
	
	/**
	 * 拼Key
	 * @param listener
	 * @param handler
	 * @return
	 */
	static String getKey(ILogServerListener listener, ILogHandler handler){
		return listener.getPort() + "_" + handler.getType();
	}
	
	public static LogWriteHandler getLogWriteHandler(ILogServerListener listener, ILogHandler handler){
		String key = getKey(listener, handler);
		return writerMap.get(key);
	}

	public static LogWriteHandler createLogWriteHandler(ILogServerListener listener, ILogHandler handler){
		String key = getKey(listener, handler);
		String cron = handler.getCron();
		
		if(cron == null || "".equals(cron))
			return null;
		
		if(writerMap.containsKey(key))
			return writerMap.get(key);
		
		
		final LogWriteHandler writer = new LogWriteHandler(listener, handler);
		int spf = handler.getSPF();
		if(spf > 0){
			//定时生成文件
			executor.scheduleAtFixedRate(new Runnable(){
				@Override
				public void run() {
					writer.redirect();
				}
			}, spf, spf, TimeUnit.SECONDS);
		}
		writerMap.put(key, writer);

		return writer;
	}
	
	/**
	 * 初始化调度器
	 */
	public static void initSecheduler(){
		if(scheduler == null){
			try {
				scheduler = schedulerFactory.getScheduler();
			} catch (SchedulerException e) {
				log.error("日志入库调度器初始化失败! " + e);
			}
		}
	}
	
	/**
	 * 启动调度器
	 */
	public static void startSecheduler(){
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("日志入库调度器启动失败! " + e);
		}
	}
	
	public static void createAutoLoadJob(ILogServerListener listener, ILogHandler handler){
		String key = getKey(listener, handler);
		String cron = handler.getCron();
		
		if(cron == null || "".equals(cron))
			return;

		// 创建日志自动入库任务
		JobDetail jobDetail = new JobDetail("load_" + handler.getType() + "_job", "" + listener.getPort(), LogAutoLoadJob.class);
		jobDetail.getJobDataMap().put("listener", listener);
		jobDetail.getJobDataMap().put("handler", handler);
		
		CronTrigger trigger = new CronTrigger("load_" + key + "_trigger", "d");
		try {
			trigger.setCronExpression(cron);
			//trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (ParseException e) {
			log.error(e);
		} catch (SchedulerException e) {
			log.error(e);
		}
	}
	
	/**
	 * 切换日志文件写入句柄，读取文件日志并调用LogHandler处理数据
	 * @param listener
	 * @param handler
	 */
	public static void loadFileLogs(ILogServerListener listener, ILogHandler handler){
		LogWriteHandler writer = getLogWriteHandler(listener, handler);
		if(writer == null){
			return;
		}
		
		/**
		 * 获取目录信息
		 */
		String folderPath = writer.getFolderPath();
		File folder = new File(folderPath);
		if(!folder.exists() || !folder.isDirectory()){
			return;
		}
		
		/**
		 * 切换日志文件写入句柄
		 */
		final long timeStamp;
		int spf = handler.getSPF();
		
		//xiedx 2017/3/29
		timeStamp = writer.getTimeStamp();
		if(spf <= 0){
			writer.redirect();
		}
		
		/*
		if(spf > 0){
			timeStamp = writer.getTimeStamp();
		}else{
			timeStamp = System.currentTimeMillis();
			writer.redirect();
		}
		*/
		
		/**
		 * 文件过滤器
		 */
		FileFilter filter = new FileFilter(){
			@Override
			public boolean accept(File file) {
				boolean ret = false;
				String name =  file.getName();
				if(name != null && !"".equals(name)
						&& isNumeric(name)){ //判断文件名为数字
					
					//最后修改时间小于写文件句柄切换时间
					long modifyTime = file.lastModified();
					ret = modifyTime < timeStamp;
					
					//DEBUG
					if(log.isDebugEnabled()){
						log.debug("过滤文件[" + file.getAbsolutePath() + "]，文件修改时间戳[" + modifyTime  + "]，对比时间戳[" + timeStamp + "]，是否排除[" + ret + "]");
					}
				}
				return ret;
			}
		};
		
		int bufferSize = writer.getBufferSize();
		File[] files = folder.listFiles(filter);
		
		if(log.isDebugEnabled()){
			log.debug("加载并处理[" + folderPath + "]路径下的日志文件，按文件修改时间过滤后待处理文件数量为 " + files != null ? files.length : 0 + "个");
		}
		
		if(files != null && files.length > 0){
			FileInputStream fis = null;
			BufferedInputStream bis  = null;
			ObjectInputStream ois = null;
			
			for(File file : files){
								
				if(file == null || !file.exists()){
					log.error("日志文件[" + file.getAbsolutePath() + "]不存在");
					continue;
				}
				
				int count = 0;
 				try{
 					if(log.isDebugEnabled()){
 						log.debug("解析和处理日志文件[" + file.getAbsolutePath() + "]");
 					}
 					
 					fis = new FileInputStream(file);
 					bis = new BufferedInputStream(fis, bufferSize);
 					ois = new ObjectInputStream(bis);

 					while(true){
 						Object obj = ois.readObject();
 						if(obj != null){
 							ILogData logData = (ILogData)obj;
 							try{
 								handler.execute(logData);
 							}catch(Exception ex){
 								log.error("执行日志处理Handler发生错误", ex);
 							}
 						}
 						count ++;
 					}	
 					
				}catch(EOFException ex){
					if(log.isDebugEnabled()){
						log.debug("已经达到文件末尾，完成[" + file.getAbsolutePath() + "]日志文件处理，共处理" + count + "条数据");
					}
				}catch(Exception ex){
					log.error("解析日志文件[" + file.getAbsolutePath() + "]发生错误", ex);
				}finally{
					try{
						if(ois != null){
							ois.close();
						}
						if(bis != null){
							bis.close();
						}
						if(fis != null){
							fis.close();
						}
					}catch(Exception ex){
						log.error("关闭日志文件[" + file.getAbsolutePath() + "]发生错误", ex);
					}
					try{
						file.delete();
					}catch(Exception ex){
						log.error("删除日志文件[" + file.getAbsolutePath() + "]发生错误", ex);
					}
				}
			}
		}
	}
}