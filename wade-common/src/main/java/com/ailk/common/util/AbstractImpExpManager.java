package com.ailk.common.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 后台需要缓存的各种状态信息
 * PROGRESS:当前进度(0,1,2,3,......100);
 * STATUS_STEP:当前的执行状态，执行到了那个步骤(值为error时表示报错,客户端将把ERROR_REASON对应的值作为提示信息);
 * Hint:记录更新状态时对应的提示信息;
 * DOWNLOAD_URL:返回客户端供下载的链接;
 * REMAIN_TIME:客户端执行还需要的剩余时间
 * 
 * @author lvchao
 *
 */
public abstract class AbstractImpExpManager {
	
	private static final String PROGRESS = "PROGRESS";
	private static final String STATUS_STEP = "STATUS";
	private static final String HINT = "HINT";
	private static final String DOWNLOAD_URL = "DOWNLOAD_URL";
	private static final String REMAIN_TIME = "REMAIN_TIME";
	
	// 设置导入导出的文件处理类，该类可通过在web.xml的servlet中配置
	//private static String actionClz = null ;
	private static IFileAction action = null;
	
	public static final String LAST_UPDATE_TIME="LAST_UPDATE_TIME";

	/**
	 * 获取全部Status对象
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public abstract Map<String,Map> getStatus();
	
	/**
	 * 获取保存导入导出状态的对象
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public abstract Map getStatus(String fileSerializeId);
	
	/**
	 * 更新状态参数
	 * @param fileSerializeId
	 * @param key
	 * @param value
	 */
	public abstract void updateStatus(String fileSerializeId, String key, String value);
	
	/**
	 * 增加新的status状态
	 * 
	 * @param fileSerializeId
	 * @param status
	 */
	@SuppressWarnings("rawtypes")
	public abstract void addStatus(String fileSerializeId, Map status);
	
	/**
	 * 移除状态对象
	 * 
	 * @param fileSerializeId
	 */
	public abstract void removeStatus(String fileSerializeId);
	
	/**
	 * 移除全部状态
	 */
	public abstract void removeAllStatus();
	
	public void setStatus(String fileSerializeId, String progress, String status, String remainTime, String hint, String downloadUrl){
		setProgress(fileSerializeId, progress);
		setStatusStep(fileSerializeId, status);
		setRemainTime(fileSerializeId, remainTime);
		setHint(fileSerializeId, hint);
		setDownLoadUrl(fileSerializeId, downloadUrl);
	}

	public void setSimpleStatus(String fileSerializeId, String progress, String status){
		setProgress(fileSerializeId, progress);
		setStatusStep(fileSerializeId, status);
	}
	
	public void setSimpleStatusWithUrl(String fileSerializeId, String progress, String status, String downloadUrl){
		setProgress(fileSerializeId, progress);
		setStatusStep(fileSerializeId, status);
		setDownLoadUrl(fileSerializeId, downloadUrl);
	}
	
	public void setSimpleStatusWithHint(String fileSerializeId, String progress, String status, String hint){
		setProgress(fileSerializeId, progress);
		setStatusStep(fileSerializeId, status);
		setHint(fileSerializeId, hint);
	}
	
	public void setSimpleStatusWithHintAndUrl(String fileSerializeId, String progress, String status, String downloadUrl, String hint){
		setProgress(fileSerializeId, progress);
		setStatusStep(fileSerializeId, status);
		setHint(fileSerializeId, hint);
		setDownLoadUrl(fileSerializeId, downloadUrl);
	}
	
	public void setRemainTime(String fileSerializeId, String remainTime){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			fileMap = new ConcurrentHashMap<String, String>();
			addStatus(fileSerializeId, fileMap);
		}
		updateStatus(fileSerializeId,AbstractImpExpManager.REMAIN_TIME, remainTime);
		updateStatus(fileSerializeId,AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis()+"");
	}
	
	public String getRemainTime(String fileSerializeId){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			return null;
		}
		updateStatus(fileSerializeId,AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis()+"");
		return (String)getStatus(fileSerializeId).get(AbstractImpExpManager.REMAIN_TIME);
	}
	
	public void setDownLoadUrl(String fileSerializeId,String downloadUrl){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			fileMap = new ConcurrentHashMap<String, String>();
			addStatus(fileSerializeId, fileMap);
		}
		updateStatus(fileSerializeId, AbstractImpExpManager.DOWNLOAD_URL, downloadUrl);
		updateStatus(fileSerializeId, AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis() + "");
	}
	
	public String getDownLoadUrl(String fileSerializeId){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			return null;
		}
		updateStatus(fileSerializeId, AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis() + "");
		return (String)getStatus(fileSerializeId).get(AbstractImpExpManager.DOWNLOAD_URL);
	}
	
	public void setProgress(String fileSerializeId,String progress){
		Map fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			fileMap = new ConcurrentHashMap<String, String>();
			addStatus(fileSerializeId, fileMap);
		}
		updateStatus(fileSerializeId, AbstractImpExpManager.PROGRESS, progress);
		updateStatus(fileSerializeId, AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis() + "");
	}
	
	public String getProgress(String fileSerializeId){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			return null;
		}
		updateStatus(fileSerializeId, AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis() + "");
		return (String)getStatus(fileSerializeId).get(AbstractImpExpManager.PROGRESS);
	}
	
	public void setStatusStep(String fileSerializeId, String status){
		Map fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			fileMap = new ConcurrentHashMap<String, String>();
			addStatus(fileSerializeId, fileMap);
		}
		updateStatus(fileSerializeId, AbstractImpExpManager.STATUS_STEP, status);
		updateStatus(fileSerializeId, AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis() + "");
	}
	
	public String getStatusStep(String fileSerializeId){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			return null;
		}
		updateStatus(fileSerializeId, AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis()+"");
		return (String)getStatus(fileSerializeId).get(AbstractImpExpManager.STATUS_STEP);
	}
	
	public void setHint(String fileSerializeId,String hint){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			fileMap = new ConcurrentHashMap<String, String>();
			addStatus(fileSerializeId, fileMap);
		}
		updateStatus(fileSerializeId,AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis()+"");
		updateStatus(fileSerializeId,AbstractImpExpManager.HINT, hint);
	}
	
	public String getHint(String fileSerializeId){
		Map<String, String> fileMap = getStatus(fileSerializeId);
		if(fileMap == null){
			return null;
		}
		updateStatus(fileSerializeId,AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis()+"");
		String hint = (String)getStatus(fileSerializeId).get(AbstractImpExpManager.HINT);
		if(hint == null||"".equals(hint)){
			return null;
		}
		return hint.replaceAll("\\\\(?![\"])", "\\\\\\\\");
	}
	
	public void clearFileSerial(String fileSerializeId){
		removeStatus(fileSerializeId);
	}
	
	public void clearAll(){
		removeAllStatus();
	}
	
	//public void setFileActionClazz(String actionClazz){
	//	actionClz = actionClazz;
	//}
	
	public void setFileAction(IFileAction fileAction){
		action = fileAction;
	}
	
	public IFileAction getFileAction() {
		action = FileHelper.getFileAction();
		return action;
	}
	
	/**
	 * 清理过期的状态信息
	 * 
	 * @author lvchao
	 *
	 */
	public class ClearStatus implements Runnable{
		private long keepAliveTime=0;
		
		public ClearStatus(){
		}
		public ClearStatus(long keepAliveTime){
			this.keepAliveTime = keepAliveTime*1000;
		}
		public void run() {
			while(true){
				try {
					Thread.sleep(keepAliveTime);
					if(getStatus() == null) continue;
					Set<String> keys = getStatus().keySet();
					if(keys == null || keys.size() == 0) continue;
					for(String key : keys){
						Map<String,String> status = getStatus(key);
						if(status == null)break;
						String lastUpdateTimeStr = status.get(AbstractImpExpManager.LAST_UPDATE_TIME);
						long lastUpdateTime = 0;
						if(lastUpdateTimeStr != null && !"".equals(lastUpdateTimeStr)){
							lastUpdateTime = Long.valueOf(lastUpdateTimeStr);
						}
						long currTime = System.currentTimeMillis();
						if((currTime - lastUpdateTime) > (keepAliveTime)){
							removeStatus(key);
						}
					}
				} catch (InterruptedException e) {
					Utility.getBottomException(e).printStackTrace();
				}
			}
		}
		
	}
	
}
