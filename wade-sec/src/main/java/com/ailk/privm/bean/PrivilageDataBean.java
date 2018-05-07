package com.ailk.privm.bean;

import com.ailk.common.data.IDataset;
import com.ailk.database.dao.DAOManager;
import com.ailk.privm.PConstants;
import com.ailk.service.bean.BaseBean;

public class PrivilageDataBean extends BaseBean{
	public IDataset queryAllDataRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryAllDataRight(staffId);
	}
	
	public IDataset queryProcDataRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryProcDataRight(staffId);
	} 
	
	public IDataset queryDiscntDataRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryDiscntDataRight(staffId);
	}
	
	public IDataset queryFieldDataRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryFieldDataRight(staffId);
	}
	
	public IDataset queryPkgDataRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryPkgDataRight(staffId);
	}
	
	public IDataset queryServiceDataRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryServiceDataRight(staffId);
	}
	
	public IDataset queryAllMenu() throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryAllMenu();
	}
	
	public IDataset queryMenuPath() throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryMenuPath();
	}
	
	public IDataset queryNodeMenuWithRightcode() throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryNodeMenuWithRightcode();
	}

	public IDataset queryFuncRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryFuncRight(staffId);
	}
	
	
	public IDataset queryOpRoleRight(String staffId) throws Exception{
		PrivilageDataDAO dao = (PrivilageDataDAO)DAOManager.createDAO(PrivilageDataDAO.class, PConstants.DB_NAME);
		return dao.queryOpRoleRight(staffId);
	}
	
	
}
