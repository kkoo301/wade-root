package com.ailk.privm.service;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.privm.PConstants;
import com.ailk.privm.bean.PrivilageDataBean;
import com.ailk.service.BaseService;
import com.ailk.service.bean.BeanManager;
import com.ailk.common.util.Utility;

public class PrivilageDataService extends BaseService{
	
	private static final long serialVersionUID = 1L;
	
	public IDataset queryAllDataRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryAllDataRight(staffId);
	}
	
	public IDataset queryProcDataRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryProcDataRight(staffId);
	} 
	
	public IDataset queryDiscntDataRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryDiscntDataRight(staffId);
	}
	
	public IDataset queryFieldDataRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryFieldDataRight(staffId);
	}
	
	public IDataset queryPkgDataRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryPkgDataRight(staffId);
	}
	
	public IDataset queryServiceDataRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryServiceDataRight(staffId);
	}
	
	public IDataset queryAllMenu(IData param) throws Exception{
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryAllMenu();
	}
	
	public IDataset queryMenuPath(IData param) throws Exception{
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryMenuPath();
	}
	
	public IDataset queryNodeMenuWithRightcode(IData param) throws Exception{
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryNodeMenuWithRightcode();
	}
	
	public IDataset queryFuncRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryFuncRight(staffId);
	}
	
	
	public IDataset queryOpRoleRight(IData param) throws Exception{
		String staffId = param.getString(PConstants.DbServiceParam.P_STAFF_ID);
		if(null == staffId){
			Utility.error(PConstants.DbServiceParam.P_STAFF_ID + "参数不能为空");
		}
		PrivilageDataBean bean = (PrivilageDataBean)BeanManager.createBean(PrivilageDataBean.class);
		return bean.queryOpRoleRight(staffId);
	}
	
}
