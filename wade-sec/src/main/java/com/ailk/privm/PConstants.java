package com.ailk.privm;

public class PConstants {
	public static final String DB_NAME = "sys";
	public static final String EMPTY_STRING = "";
	public static class MenuParserParam {
		public static final String MENU_PATH_SEPARATOR = ",";
		public static final int MENU_PARENT_BEGIN      = 2;
	}
	
	public static class SystemManagerParam{
		public static final String SYNC_STAFF_IDS = "STAFF_IDS";
		public static final String SYNC_PRIV_TYPE = "PRIV_TYPE";
		public static final String PARAMS_SEPARATOR = ",";
		public static final int    MAX_SYNC_STAFFS  = 500;
	}
	
	public static class DbServiceParam{
		public static final String P_STAFF_ID            = "STAFF_ID";
		public static final String GET_AllDataRight      = "C_PRIV_QueryAllDataRight";
		public static final String GET_PorcDataRight     = "C_PRIV_QueryProcDataRight";
		public static final String GET_DiscntDataRight   = "C_PRIV_QueryDiscntDataRight";
		public static final String GET_FieldDataRight    = "C_PRIV_QueryFieldDataRight";
		public static final String GET_PkgDataRight      = "C_PRIV_QueryPkgDataRight";
		public static final String GET_ServiceDataRight  = "C_PRIV_QueryServiceDataRight";
		public static final String GET_FuncRight         = "C_PRIV_QueryFuncRight";
		public static final String GET_OpRoleRight       = "C_PRIV_QueryOpRoleRight";
		public static final String GET_AllMenu           = "C_PRIV_QueryAllMenu";
		public static final String GET_AllMenuPath       = "C_PRIV_QueryMenuPath";
		public static final String GET_NodeMenuWithRight = "C_PRIV_QueryNodeMenuWithRightcode";
	}
	
	public static class CacheParam{
		public static final String REDIS_GROUP_NAME      = "sec";
		public static final String REDIS_EMPTY_KEY       = "REDIS_EMPTY_KEY";
		public static final String REDIS_PRIV_KEY_PREFIX = "PV_";
	}
	
	public static class PrivilageType {
		//Ȩ����sc权限验证类型
		public static final String FUNC_TYPE    = "F";    //功能权限
		public static final String PROD_TYPE    = "P";    //产品权限
		public static final String DISCNT_TYPE  = "D";    //资费权限
		public static final String SERVICE_TYPE = "S";    //服务权限
		public static final String PACKAGE_TYPE = "K";    //包权限
		public static final String FILED_TYPE   = "1";    //域权限
		public static final String OP_ROLE_TYPE = "O";    //OP角色权限
		public static final String MENU_TYPE    = "M";    //菜单权限
		public static final String [] PRIV_TYPE_ARRAY = new String[]{FUNC_TYPE, PROD_TYPE, DISCNT_TYPE, SERVICE_TYPE, PACKAGE_TYPE, 
			FILED_TYPE, OP_ROLE_TYPE, MENU_TYPE};
	}
	
	public static class TableColumnName {
		public static final String D_DATA_CODE    = "DATA_CODE";
		public static final String D_DATA_TYPE    = "DATA_TYPE";
		public static final String D_RIGHT_CLASS  = "RIGHT_CLASS";
		public static final String F_RIGHT_CODE   = "RIGHT_CODE";
		public static final String M_MENU_ID      = "MENU_ID";
		public static final String O_OP_ROLE_CODE = "OP_ROLE_CODE";
	}
	
}
