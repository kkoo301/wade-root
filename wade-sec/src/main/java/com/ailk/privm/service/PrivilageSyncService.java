package com.ailk.privm.service;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.privm.PConstants;
import com.ailk.privm.cache.KeyCreator;
import com.ailk.privm.cache.PrivilageDataDest;


public class PrivilageSyncService extends BizService {
	private static final long serialVersionUID = 1L;
	
private static transient Logger log = Logger.getLogger(PrivilageSyncService.class);
	
	public IDataset syncStaffsPriv(IData data) throws Exception {	
		IDataset returnSet = new DatasetList();
		IData returnData = new DataMap();
		String staffIdParams = data.getString(PConstants.SystemManagerParam.SYNC_STAFF_IDS);
		if(null == staffIdParams){
			returnData.put("X_RESULTINFO", "参数" + PConstants.SystemManagerParam.SYNC_STAFF_IDS + "为空");
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTSUCCESS", "0");
			returnSet.add(returnData);
			return returnSet;
		}
		
		String [] staffIds = staffIdParams.split(PConstants.SystemManagerParam.PARAMS_SEPARATOR);
		if(staffIds.length > PConstants.SystemManagerParam.MAX_SYNC_STAFFS) {
			returnData.put("X_RESULTINFO", "传过来的员工数超过最大限制数(" + PConstants.SystemManagerParam.MAX_SYNC_STAFFS + ")，不执行刷新操作");
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTSUCCESS", "0");
			returnData.put("X_RESULTFAIL", staffIds.length + "");
			returnSet.add(returnData);
			return returnSet;
		}
		int len = staffIds.length, fail = 0, success = 0;
		for(int i = 0; i < len; i++){
			String staffId = staffIds[i].trim();
			try {
				String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.DISCNT_TYPE);
				PrivilageDataDest.delete(key);
				key = KeyCreator.createKey(staffId, PConstants.PrivilageType.FILED_TYPE);
				PrivilageDataDest.delete(key);
				key = KeyCreator.createKey(staffId, PConstants.PrivilageType.FUNC_TYPE);
				PrivilageDataDest.delete(key);
				key = KeyCreator.createKey(staffId, PConstants.PrivilageType.MENU_TYPE);
				PrivilageDataDest.deleteSerial(key);
				key = KeyCreator.createKey(staffId, PConstants.PrivilageType.OP_ROLE_TYPE);
				PrivilageDataDest.delete(key);
				key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PACKAGE_TYPE);
				PrivilageDataDest.delete(key);
				key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PROD_TYPE);
				PrivilageDataDest.delete(key);
				key = KeyCreator.createKey(staffId, PConstants.PrivilageType.SERVICE_TYPE);
				PrivilageDataDest.delete(key);
				success = success + 1;
			} catch (Exception e){
				log.error(e.getMessage(), e);
				fail = fail + 1;
			}
		}
		if(0 == fail){
			returnData.put("X_RESULTINFO", "刷新成功" );
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "0");
			returnData.put("X_RESULTFAIL", "0");
			returnData.put("X_RESULTSUCCESS", staffIds.length + "");
			returnSet.add(returnData);
		} else {
			returnData.put("X_RESULTINFO", "刷新未成功，部分失败" );
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTFAIL", "" +  fail);
			returnData.put("X_RESULTSUCCESS", "" + success);
			returnSet.add(returnData);
		}
		return returnSet;
	}
	

	
	public IDataset syncStaffsPrivWithType(IData data) throws Exception {	
		IDataset returnSet = new DatasetList();
		IData returnData = new DataMap();
		String staffIdParams = data.getString(PConstants.SystemManagerParam.SYNC_STAFF_IDS);
		String privType = data.getString(PConstants.SystemManagerParam.SYNC_PRIV_TYPE);
		if(null == staffIdParams || null == privType){
			returnData.put("X_RESULTINFO", "参数" + PConstants.SystemManagerParam.SYNC_STAFF_IDS + "或者" + PConstants.SystemManagerParam.SYNC_PRIV_TYPE + "为空");
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTSUCCESS", "0");
			returnSet.add(returnData);
			return returnSet;
		}
		int flag = 0;
		for(int i = 0; i < PConstants.PrivilageType.PRIV_TYPE_ARRAY.length; i++){
			if(privType.equals(PConstants.PrivilageType.PRIV_TYPE_ARRAY[i])) {
				flag = 1;
			}
		}
		if(0 == flag){
			returnData.put("X_RESULTINFO", "传入的权限类型错误，不支持此权限类型: " + privType);
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTSUCCESS", "0");
			returnSet.add(returnData);
			return returnSet;
		}
		
		String [] staffIds = staffIdParams.split(PConstants.SystemManagerParam.PARAMS_SEPARATOR);
		if(staffIds.length > PConstants.SystemManagerParam.MAX_SYNC_STAFFS) {
			returnData.put("X_RESULTINFO", "传过来的员工数超过最大限制数(" + PConstants.SystemManagerParam.MAX_SYNC_STAFFS + ")，不执行刷新操作");
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTSUCCESS", "0");
			returnData.put("X_RESULTFAIL", staffIds.length + "");
			returnSet.add(returnData);
			return returnSet;
		}
		
		int len = staffIds.length, fail = 0, success = 0;
		for(int i = 0; i < len; i++){
			String staffId = staffIds[i].trim();
			try {
				String key = KeyCreator.createKey(staffId, privType);
				if(privType.equals(PConstants.PrivilageType.MENU_TYPE)) {
					PrivilageDataDest.deleteSerial(key);
				} else {
					PrivilageDataDest.delete(key);
				}
				success = success + 1;
			} catch (Exception e){
				log.error(e.getMessage(), e);
				fail = fail + 1;
			}
		}
		if(0 == fail){
			returnData.put("X_RESULTINFO", "刷新成功" );
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "0");
			returnData.put("X_RESULTFAIL", "0");
			returnData.put("X_RESULTSUCCESS", staffIds.length + "");
			returnSet.add(returnData);
		} else {
			returnData.put("X_RESULTINFO", "刷新未成功，部分失败" );
			returnData.put("X_RESULTSIZE", "0");
			returnData.put("X_RESULTCOUNT", "0");
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTFAIL", "" +  fail);
			returnData.put("X_RESULTSUCCESS", "" + success);
			returnSet.add(returnData);
		}
		return returnSet;
	}
	
}
