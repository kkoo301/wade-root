package com.ailk.privm.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.privm.PConstants;



public class MenuRightParser {
	
	public static IData menuPathLocalCache = null;
	public static IData nodeMenuWithRightLocalCache = null;
	public static IDataset allMenuLocalCache = null;
	
	public static long       cacheTimeP = 0L;
	public static int        useTimesP  = 0;
	public static long       cacheTimeN = 0L;
	public static int        useTimesN  = 0;
	public static long       cacheTimeA = 0L;
	public static int        useTimesA  = 0;
	
	public static final int  useTimesLimit = 10000;
	public static final long timeLimit     = 3600 * 1000 * 2;

	/*此类需要提供缓存刷新页面，先提供一个简单的自我刷新规则*/
	
	public static Map<String, String> getStaffMenus(Map<String, String> funcRight) throws Exception {
		
		IDataset allMenu = getAllMenuC();
		IData menuPath = getMenuPathC();
		IData nodeMenusWithRight = getNodeMenusWithRightC();
		Map<String, String> result = new HashMap<String, String>();
		
		String menuId = null;
		String rightCode = null;
		String nodeMenuId = null;
		String nodeMenuRightCode = null;
		Set<String> tempSet = null;
		int len = allMenu.size();
		int flag = 0;
		
		for(int i = 0; i< len; i++){
			IData menu = allMenu.getData(i);
			flag = 0;
			menuId = menu.getString(PConstants.TableColumnName.M_MENU_ID);
			rightCode = menu.getString(PConstants.TableColumnName.F_RIGHT_CODE);
			if(null == rightCode || rightCode.trim().equals(PConstants.EMPTY_STRING)){
				result.put(menuId, "");
				flag = 1;
			} else if(null != funcRight.get(rightCode)){
				result.put(menuId, "");
				flag = 1;
			}
			if(1 == flag){
				tempSet = (Set<String>)menuPath.get(menuId);
				for(Iterator<String> it = tempSet.iterator(); it.hasNext();){
					nodeMenuId = it.next();
					nodeMenuRightCode = nodeMenusWithRight.getString(nodeMenuId);
					if(null == nodeMenuRightCode || nodeMenuRightCode.trim().equals(PConstants.EMPTY_STRING)){
						result.put(nodeMenuId, "");
					} else if(null != funcRight.get(nodeMenuRightCode)){
						result.put(nodeMenuId, "");
					}
				}
			}
		}
		return result;
	}
	
	
	public static IData getMenuPathC() throws Exception {
		IData result = null;
		if(null == menuPathLocalCache || 0 == menuPathLocalCache.size()){
			result = getMenuPath();
			useTimesP = 0;
			cacheTimeP = System.currentTimeMillis();
			menuPathLocalCache = result;
		} else {
			long currentTime = System.currentTimeMillis();
			long interval = currentTime - cacheTimeP;
			if(useTimesP > useTimesLimit || interval > timeLimit){
				result = getMenuPath();
				useTimesP = 0;
				cacheTimeP = System.currentTimeMillis();
				menuPathLocalCache = result;
			} else {
				//因没有做同步，这边最好复制一下，防止在使用的时候其它线程修改
				result = new DataMap(menuPathLocalCache);
				useTimesP = useTimesP + 1;
			}
		}
		return result;
	}
	
	public static IDataset getAllMenuC() throws Exception {
		IDataset result = null;
		if(null == allMenuLocalCache || 0 == allMenuLocalCache.size()){
			result = PrivilageDataSource.queryAllMenu();
			useTimesA = 0;
			cacheTimeA = System.currentTimeMillis();
			allMenuLocalCache = result;
		} else {
			long currentTime = System.currentTimeMillis();
			long interval = currentTime - cacheTimeA;
			if(useTimesA > useTimesLimit || interval > timeLimit){
				result = PrivilageDataSource.queryAllMenu();
				useTimesA = 0;
				cacheTimeA = System.currentTimeMillis();
				allMenuLocalCache = result;
			} else {
				//因没有做同步，这边最好复制一下，防止在使用的时候其它线程修改
				result = new DatasetList(allMenuLocalCache);
				useTimesA = useTimesA + 1;
			}
		}
		return result;
	}
	
	public static IData getNodeMenusWithRightC() throws Exception {
		IData result = null;
		if(null == nodeMenuWithRightLocalCache || 0 == nodeMenuWithRightLocalCache.size()){
			result = getNodeMenusWithRight();
			useTimesN = 0;
			cacheTimeN = System.currentTimeMillis();
			nodeMenuWithRightLocalCache = result;
		} else {
			long currentTime = System.currentTimeMillis();
			long interval = currentTime - cacheTimeN;
			if(useTimesN > useTimesLimit || interval > timeLimit){
				result = getNodeMenusWithRight();
				useTimesN = 0;
				cacheTimeN = System.currentTimeMillis();
				nodeMenuWithRightLocalCache = result;
			} else {
				//因没有做同步，这边最好复制一下，防止在使用的时候其它线程修改
				result = new DataMap(nodeMenuWithRightLocalCache);
				useTimesN = useTimesN + 1;
			}
		}
		return result;
	}
	
	public static IData getMenuPath()  throws Exception{
		
		IDataset menus = PrivilageDataSource.queryMenuPath();
		IData nodeMenus = new DataMap();
		IData menu = null;
		
		for(int i = 0; i < menus.size(); i++){
			menu = menus.getData(i);
			String menuIds = menu.getString(PConstants.TableColumnName.M_MENU_ID);
			String [] menuArray = menuIds.split(PConstants.MenuParserParam.MENU_PATH_SEPARATOR);
			if(menuArray.length < PConstants.MenuParserParam.MENU_PARENT_BEGIN){
				continue;
			}
			Set<String> nodeSet = new HashSet<String>();
			for(int j = PConstants.MenuParserParam.MENU_PARENT_BEGIN; j < menuArray.length; j++){
				nodeSet.add(menuArray[j]);
			}
			nodeMenus.put(menuArray[PConstants.MenuParserParam.MENU_PARENT_BEGIN - 1], nodeSet);
		}
		return nodeMenus;
	}
	
	public static IData getNodeMenusWithRight() throws Exception{
		IData nodeMenus = new DataMap();
		IDataset menus = PrivilageDataSource.queryNodeMenuWithRightcode();
		if(null == menus){
			return nodeMenus;
		}
		IData menu = null;
		for(int i = 0; i < menus.size(); i++){
			menu = menus.getData(i);
			nodeMenus.put(menu.getString(PConstants.TableColumnName.M_MENU_ID), menu.getString(PConstants.TableColumnName.F_RIGHT_CODE, ""));
		}
		return nodeMenus;
	}
}


