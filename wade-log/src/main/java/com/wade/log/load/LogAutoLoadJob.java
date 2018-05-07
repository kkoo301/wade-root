package com.wade.log.load;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.wade.log.ILogHandler;
import com.wade.log.ILogServerListener;
import com.wade.log.db.DAOSession;

public class LogAutoLoadJob implements StatefulJob{

	private static final transient Logger log = Logger.getLogger(LogAutoLoadJob.class);
	
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {

		try{
			
			JobDataMap map = ctx.getJobDetail().getJobDataMap();
			
			ILogServerListener listener = (ILogServerListener) map.get("listener");
			ILogHandler handler = (ILogHandler)map.get("handler");
			
			LogReadWriteFactory.loadFileLogs(listener, handler);
			
			//提交事务
			DAOSession.commit();
		}catch(Exception ex){
			try {
				DAOSession.rollback();
			} catch (SQLException ex1) {
				log.error("数据库事务回滚失败", ex1);
			}
			log.error("日志自动入库执行失败：", ex);
		}finally{
			try {
				DAOSession.close();
			} catch (Exception ex2) {
				log.error("数据库连接销毁失败", ex2);
			}
		}
		
	}
	
}