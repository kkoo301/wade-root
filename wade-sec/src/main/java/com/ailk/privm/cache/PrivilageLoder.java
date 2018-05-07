package com.ailk.privm.cache;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.privm.PConstants;



public class PrivilageLoder {


	
	public static Map<String, String> loadStaffRight(String staffId) throws Exception{
		
		//加载功能权限
		//loadStaffFuncRight(staffId);
		
		//加载数据权限
		loadStaffDataRight(staffId);
		
		//加载域权限
		loadStaffFieldRight(staffId);
		
		//加载OP角色权限
		loadStaffOpRoleRight(staffId);
		
		//加载菜单、功能权限
		return loadStaffMenuRight(staffId);
		
	}
	
	public static Map<String, String> loadStaffMenuRight(String staffId) throws Exception{
		Map<String, String> funcRights = loadStaffFuncRight(staffId);
		Map<String, String> staffMenus = MenuRightParser.getStaffMenus(funcRights);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.MENU_TYPE);
		PrivilageDataDest.loadSerial(key, (HashMap<String, String>)staffMenus);
		return staffMenus;
	}
	
	public static Map<String, String> loadStaffFuncRight(String staffId) throws Exception{
		IDataset datas = PrivilageDataSource.queryFuncRight(staffId);
		Map<String, String> privilage = transform2Map(datas, PConstants.TableColumnName.F_RIGHT_CODE);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.FUNC_TYPE);
		PrivilageDataDest.load(key, privilage);
		return privilage;
	}
	
	public static void loadStaffDataRight(String staffId) throws Exception{
		IDataset datas = PrivilageDataSource.queryAllDataRight(staffId);
		Map<String, String> prodData = new HashMap<String, String>(), DiscntData = new HashMap<String, String>();
		Map<String, String> serviceData = new HashMap<String, String>(), pkgData = new HashMap<String, String>();
		int len = datas.size();
		String dataType = null, dataCode = null;
		for(int i = 0; i < len; i ++){
			IData data = datas.getData(i);
			dataType = data.getString(PConstants.TableColumnName.D_DATA_TYPE);
			dataCode = data.getString(PConstants.TableColumnName.D_DATA_CODE);
			if(dataType.equals(PConstants.PrivilageType.PROD_TYPE)) {
				prodData.put(dataCode, "");
			} else if (dataType.equals(PConstants.PrivilageType.DISCNT_TYPE)) {
				DiscntData.put(dataCode, "");
			}else if (dataType.equals(PConstants.PrivilageType.SERVICE_TYPE)) {
				serviceData.put(dataCode, "");
			}else if (dataType.equals(PConstants.PrivilageType.PACKAGE_TYPE)) {
				pkgData.put(dataCode, "");
			}
		}
		
		String procKey = KeyCreator.createKey(staffId, PConstants.PrivilageType.PROD_TYPE);
		String discntKey = KeyCreator.createKey(staffId, PConstants.PrivilageType.DISCNT_TYPE);
		String serviceKey = KeyCreator.createKey(staffId, PConstants.PrivilageType.SERVICE_TYPE);
		String pkgKey = KeyCreator.createKey(staffId, PConstants.PrivilageType.PACKAGE_TYPE);
		PrivilageDataDest.load(procKey, prodData);
		PrivilageDataDest.load(discntKey, DiscntData);
		PrivilageDataDest.load(serviceKey, serviceData);
		PrivilageDataDest.load(pkgKey, pkgData);
	}
	
	public static Map<String, String> loadStaffProdRight(String staffId)throws Exception{
		IDataset datas = PrivilageDataSource.queryProcDataRight(staffId);
		Map<String, String> privilage = transform2Map(datas, PConstants.TableColumnName.D_DATA_CODE);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PROD_TYPE);
		PrivilageDataDest.load(key, privilage);
		return privilage;
	}
	
	public static Map<String, String> loadStaffDiscntRight(String staffId) throws Exception{
		IDataset datas = PrivilageDataSource.queryDiscntDataRight(staffId);
		Map<String, String> privilage = transform2Map(datas, PConstants.TableColumnName.D_DATA_CODE);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.DISCNT_TYPE);
		PrivilageDataDest.load(key, privilage);
		return privilage;
	}
	
	public static Map<String, String> loadStaffPackageRight(String staffId) throws Exception{
		IDataset datas = PrivilageDataSource.queryPkgDataRight(staffId);
		Map<String, String> privilage = transform2Map(datas, PConstants.TableColumnName.D_DATA_CODE);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PACKAGE_TYPE);
		PrivilageDataDest.load(key, privilage);
		return privilage;
	}
	
	public static Map<String, String> loadStaffServiceRight(String staffId) throws Exception{
		IDataset datas = PrivilageDataSource.queryServiceDataRight(staffId);
		Map<String, String> privilage = transform2Map(datas, PConstants.TableColumnName.D_DATA_CODE);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.SERVICE_TYPE);
		PrivilageDataDest.load(key, privilage);
		return privilage;
	}
	
	public static Map<String, String> loadStaffFieldRight(String staffId) throws Exception{
		IDataset datas = PrivilageDataSource.queryFieldDataRight(staffId);
		Map<String, String> privilage = transform2Map(datas, PConstants.TableColumnName.D_DATA_CODE, PConstants.TableColumnName.D_RIGHT_CLASS);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.FILED_TYPE);
		PrivilageDataDest.load(key, privilage);
		return privilage;
	}
	
	public static Map<String, String> loadStaffOpRoleRight(String staffId) throws Exception{
		IDataset datas = PrivilageDataSource.queryOpRoleRight(staffId);
		Map<String, String> privilage = transform2Map(datas, PConstants.TableColumnName.O_OP_ROLE_CODE);
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.OP_ROLE_TYPE);
		PrivilageDataDest.load(key, privilage);
		return privilage;
	}
	
	public static Map<String, String> transform2Map(IDataset set, String columnName)throws Exception{
		Map<String, String> privilage = new HashMap<String, String>();
		IData data = new DataMap();
		for(int i = 0; i < set.size(); i ++){
			data = set.getData(i);
			privilage.put(data.getString(columnName), "");
		}
		return privilage;
	}
	
	public static Map<String, String> transform2Map(IDataset set, String columnName, String columnValue) throws Exception{
		Map<String, String> privilage = new HashMap<String, String>();
		IData data = new DataMap();
		String key = null, value = null, oldValue = null;
		for(int i = 0; i < set.size(); i ++){
			data = set.getData(i);
			key = data.getString(columnName);
			value = data.getString(columnValue, "");
			if(null != privilage.get( key )) {
				oldValue = privilage.get( key );
				if(value.compareTo(oldValue) > 0){
					privilage.put(key, value);
				}
			} else {
				privilage.put(key, value);
			}
		}
		return privilage;
	}
	
}
