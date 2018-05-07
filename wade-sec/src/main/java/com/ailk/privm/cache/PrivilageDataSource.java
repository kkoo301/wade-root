package com.ailk.privm.cache;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.privm.PConstants;
import com.ailk.service.client.ServiceFactory;

public class PrivilageDataSource {
	
	public static IDataset queryAllDataRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_AllDataRight, input);
		return output.getData();
	}
	
	public static IDataset queryProcDataRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_PorcDataRight, input);
		return output.getData();
	}
	
	public static IDataset queryDiscntDataRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_DiscntDataRight, input);
		return output.getData();
	}
	
	public static IDataset queryFieldDataRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_FieldDataRight, input);
		return output.getData();
	}
	
	public static IDataset queryPkgDataRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_PkgDataRight, input);
		return output.getData();
	}
	
	public static IDataset queryServiceDataRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_ServiceDataRight, input);
		return output.getData();
	}
	
	public static IDataset queryFuncRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_FuncRight, input);
		return output.getData();
	}
	
	public static IDataset queryOpRoleRight(String staffId) throws Exception{
		IDataInput input = new DataInput();
		input.getData().put(PConstants.DbServiceParam.P_STAFF_ID, staffId);
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_OpRoleRight, input);
		return output.getData();
	}
	
	public static IDataset queryAllMenu() throws Exception {
		IDataInput input = new DataInput();
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_AllMenu, input);
		return output.getData();
	}
	
	public static IDataset queryMenuPath() throws Exception {
		IDataInput input = new DataInput();
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_AllMenuPath, input);
		return output.getData();
	}
	
	public static IDataset queryNodeMenuWithRightcode() throws Exception {
		IDataInput input = new DataInput();
		IDataOutput output = ServiceFactory.call(PConstants.DbServiceParam.GET_NodeMenuWithRight, input);
		return output.getData();
	}
	
	
	
}
