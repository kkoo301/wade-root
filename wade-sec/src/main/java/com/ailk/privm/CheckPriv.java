package com.ailk.privm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


import com.ailk.cache.redis.RedisFactory;
import com.ailk.cache.redis.client.RedisClient;
import com.ailk.org.apache.commons.lang3.SerializationUtils;
import com.ailk.privm.cache.KeyCreator;
import com.ailk.privm.cache.PrivilageLoder;
import com.ailk.privm.exception.NOPrivTypeException;


public class CheckPriv {
	public static RedisClient redis = RedisFactory.getRedisClient(PConstants.CacheParam.REDIS_GROUP_NAME);
	
	public static Set<String> hasPrivList(String staffId, LinkedList<String> checkList, String privType)throws Exception{
		
		switch (privType.charAt(0)){
			case 'F': return hasFuncPrivList(staffId, checkList);
			case 'P': return hasProdPrivList(staffId, checkList);
			case 'D': return hasDistPrivList(staffId, checkList);
			case 'S': return hasSvcPrivList(staffId, checkList);
			case 'K': return hasPkgPrivList(staffId, checkList);
			case 'O': return hasOpRolePrivList(staffId, checkList);
			case '1': {
					  Map<String, String> field_map = hasFieldPrivList(staffId, checkList);
					  return field_map.keySet();
				}
			default:throw new NOPrivTypeException(privType);
		}		
	}
	
	public static Set<String> hasDistPrivList(String staffId, LinkedList<String> checkList)throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.DISCNT_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffDiscntRight(staffId);
			Set<String> resultSet = new HashSet<String>();
			String rightCode = null;
			for(Iterator<String> i = checkList.iterator(); i.hasNext();){
				rightCode = i.next();
				if(null != rights.get(rightCode)){
					resultSet.add(rightCode);
				}
			}
			return resultSet;
		}
		Map<String, String> result = redis.hmget(key, checkList.toArray(new String[0]));
		return result.keySet();
	}
	
	public static Set<String> hasFuncPrivList(String staffId, LinkedList<String> checkList)throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.FUNC_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffFuncRight(staffId);
			Set<String> resultSet = new HashSet<String>();
			String rightCode = null;
			for(Iterator<String> i = checkList.iterator(); i.hasNext();){
				rightCode = i.next();
				if(null != rights.get(rightCode)){
					resultSet.add(rightCode);
				}
			}
			return resultSet;
		}
		Map<String, String> result = redis.hmget(key, checkList.toArray(new String[0]));
		return result.keySet();
	}
	
	public static Set<String> hasOpRolePrivList(String staffId, LinkedList<String> checkList)throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.OP_ROLE_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffOpRoleRight(staffId);
			Set<String> resultSet = new HashSet<String>();
			String rightCode = null;
			for(Iterator<String> i = checkList.iterator(); i.hasNext();){
				rightCode = i.next();
				if(null != rights.get(rightCode)){
					resultSet.add(rightCode);
				}
			}
			return resultSet;
		}
		Map<String, String> result = redis.hmget(key, checkList.toArray(new String[0]));
		return result.keySet();
	}
	
	public static Set<String> hasProdPrivList(String staffId, LinkedList<String> checkList)throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PROD_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffProdRight(staffId);
			Set<String> resultSet = new HashSet<String>();
			String rightCode = null;
			for(Iterator<String> i = checkList.iterator(); i.hasNext();){
				rightCode = i.next();
				if(null != rights.get(rightCode)){
					resultSet.add(rightCode);
				}
			}
			return resultSet;
		}
		Map<String, String> result = redis.hmget(key, checkList.toArray(new String[0]));
		return result.keySet();
	}
	
	public static Set<String> hasSvcPrivList(String staffId, LinkedList<String> checkList)throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.SERVICE_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffServiceRight(staffId);
			Set<String> resultSet = new HashSet<String>();
			String rightCode = null;
			for(Iterator<String> i = checkList.iterator(); i.hasNext();){
				rightCode = i.next();
				if(null != rights.get(rightCode)){
					resultSet.add(rightCode);
				}
			}
			return resultSet;
		}
		Map<String, String> result = redis.hmget(key, checkList.toArray(new String[0]));
		return result.keySet();
	}
	
	public static Set<String> hasPkgPrivList(String staffId, LinkedList<String> checkList)throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PACKAGE_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffPackageRight(staffId);
			Set<String> resultSet = new HashSet<String>();
			String rightCode = null;
			for(Iterator<String> i = checkList.iterator(); i.hasNext();){
				rightCode = i.next();
				if(null != rights.get(rightCode)){
					resultSet.add(rightCode);
				}
			}
			return resultSet;
		}
		Map<String, String> result = redis.hmget(key, checkList.toArray(new String[0]));
		return result.keySet();
	}
	
	public static Map<String, String> hasFieldPrivList(String staffId, LinkedList<String> checkList)throws Exception{

		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.FILED_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffFieldRight(staffId);
			Map<String, String> resultSet = new HashMap<String, String>();
			String rightCode = null, rightClass = null;
			for(Iterator<String> i = checkList.iterator(); i.hasNext();){
				rightCode = i.next();
				rightClass = rights.get(rightCode);
				if(null != rightClass){
					resultSet.put(rightCode, rightClass);
				}
			}
			return resultSet;
		}
		Map<String, String> result = redis.hmget(key, checkList.toArray(new String[0]));
		return result;
	}
	
	
	//权限验证
	public static boolean checkPermission(String staffId, String privId, String privType) throws Exception{
				
		switch (privType.charAt(0)){
			case 'F': return checkFuncPermission(staffId, privId);
			case 'P': return checkProdPermission(staffId, privId);
			case 'D': return checkDistincPermission(staffId, privId);
			case 'S': return checkServicePermission(staffId, privId);
			case 'K': return checkPackagePermission(staffId, privId);
			case 'O': return checkOpRolePermission(staffId, privId);
			case 'M': return checkMenuPermission(staffId, privId);
			case '1': {
				if( null != checkFieldPermission(staffId, privId, PConstants.PrivilageType.FILED_TYPE)){
					return true;
				} else {
					return false;
				}
			}
			default:throw new NOPrivTypeException(privType);
		}
	}
	
	//权限验证
	public static boolean checkPermission(String staffId, String privId) throws Exception{
			
		if(checkFuncPermission(staffId, privId)){
			return true;
		}
		if( null != checkFieldPermission(staffId, privId, PConstants.PrivilageType.FILED_TYPE)){
			return true;
		} 
		return false;
	}
	
	//功能权限验证
	public static boolean checkFuncPermission(String staffId, String privId) throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.FUNC_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffFuncRight(staffId);
			if(null != rights.get(privId)){
				return true;
			}
			return false;
		}
		
		if(redis.hexists(key, privId)){
			return true;
		}
		return false;
	}
	
	//OP角色权限验证
	public static boolean checkOpRolePermission(String staffId, String opRole) throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.OP_ROLE_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffOpRoleRight(staffId);
			if(null != rights.get(opRole)){
				return true;
			}
			return false;
		}
		if(redis.hexists(key, opRole)){
			return true;
		}
		return false;
	}
	
	//菜单权限验证
	public static boolean checkMenuPermission(String staffId, String menuId) throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.MENU_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffMenuRight(staffId);
			if(null != rights.get(menuId)){
				return true;
			}
			return false;
		}
		HashMap<String, String> menus = (HashMap<String, String>)SerializationUtils.deserialize(redis.get(key.getBytes()));
		if(null != menus.get(menuId)){
			return true;
		}
		return false;
	}
	
	public static Set<String> getAllStaffMenu(String staffId) throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.MENU_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffRight(staffId);
			return rights.keySet();
		}
		HashMap<String, String> menus = (HashMap<String, String>)SerializationUtils.deserialize(redis.get(key.getBytes()));
		return menus.keySet();
	}
	
	
	public static boolean checkProdPermission(String staffId, String  prodPrivId) throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PROD_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffProdRight(staffId);
			if(null != rights.get(prodPrivId)){
				return true;
			}
			return false;
		}
		if(redis.hexists(key, prodPrivId)){
			return true;
		}
		return false;
	}
	
	public static boolean checkServicePermission(String staffId, String  prodPrivId) throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.SERVICE_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffServiceRight(staffId);
			if(null != rights.get(prodPrivId)){
				return true;
			}
			return false;
		}
		if(redis.hexists(key, prodPrivId)){
			return true;
		}
		return false;
	}
	
	public static boolean checkDistincPermission(String staffId, String  prodPrivId) throws Exception{
		
		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.DISCNT_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffDiscntRight(staffId);
			if(null != rights.get(prodPrivId)){
				return true;
			}
			return false;
		}
		if(redis.hexists(key, prodPrivId)){
			return true;
		}
		return false;
	}
	
	public static boolean checkPackagePermission(String staffId, String  prodPrivId) throws Exception{

		String key = KeyCreator.createKey(staffId, PConstants.PrivilageType.PACKAGE_TYPE);
		if( !redis.exists(key) ){
			Map<String, String> rights = PrivilageLoder.loadStaffPackageRight(staffId);
			if(null != rights.get(prodPrivId)){
				return true;
			}
			return false;
		}
		if(redis.hexists(key, prodPrivId)){
			return true;
		}
		return false;
	}
		
	public static String checkFieldPermission(String staffId, String privId, String privType) throws Exception{
		
		if(!PConstants.PrivilageType.FILED_TYPE.equals(privType)){
			throw new NOPrivTypeException(privType);
		}
		String key = KeyCreator.createKey(staffId, privType);
		if( !redis.exists(key) ) {
			Map<String, String> rights = PrivilageLoder.loadStaffFieldRight(staffId);
			return rights.get(privId);
		}
		String [] rights = new String[1];
		rights[0] = privId;
		Map<String, String> result = redis.hmget(key, rights);
		return result.get(privId);
	}
}
