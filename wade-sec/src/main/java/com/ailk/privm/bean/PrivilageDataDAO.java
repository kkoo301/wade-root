package com.ailk.privm.bean;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IDataset;
import com.ailk.database.dao.impl.BaseDAO;

public class PrivilageDataDAO extends BaseDAO{
	private static final boolean date_check = "true".equals(GlobalCfg.getProperty("privload.date.check", "false"));
	
	public IDataset queryAllDataRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT s.data_code, s.data_type ");
		sql.append(" FROM tf_m_staffdataright s ");
		sql.append(" WHERE s.right_attr = '0' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_type in ('K', 'D', 'S', 'P') ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT r.data_code, r.data_type ");
		sql.append(" FROM tf_m_staffdataright s, tf_m_roledataright r ");
		sql.append(" WHERE s.right_attr = '1' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_code = r.role_code ");
		sql.append(" AND r.data_type in ('K', 'D', 'S', 'P') ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND (r.rsvalue1 IS NULL OR r.rsvalue1 != '1') ");
		if (date_check) {
			sql.append(" AND sysdate between decode(r.VALID_DATE, null, sysdate-1, r.valid_date) and decode(r.EXPIRE_DATE, null, to_date('20501231235959', 'yyyyMMddHH24miss'), r.expire_date)");
		}
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT s.data_code, s.data_type ");
		sql.append(" FROM tf_m_stafftempdataright s ");
		sql.append(" WHERE s.data_type in ('K', 'D', 'S', 'P') ");
		sql.append(" AND ((s.use_tag = '1' and sysdate >= s.start_date and sysdate <= s.end_date)");
		sql.append(" or (s.use_tag = '0' and s.used_times < s.times))");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		
		return queryList(sql.toString(), new Object []{staffId, staffId, staffId});
	}
	
	public IDataset queryProcDataRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_staffdataright s ");
		sql.append(" WHERE s.right_attr = '0' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_type = 'P' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT r.data_code ");
		sql.append(" FROM tf_m_staffdataright s, tf_m_roledataright r ");
		sql.append(" WHERE s.right_attr = '1' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_code = r.role_code ");
		sql.append(" AND r.data_type = 'P' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND (r.rsvalue1 IS NULL OR r.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		if (date_check) {
			sql.append(" AND sysdate between decode(r.VALID_DATE, null, sysdate-1, r.valid_date) and decode(r.EXPIRE_DATE, null, to_date('20501231235959', 'yyyyMMddHH24miss'), r.expire_date)");
		}
		sql.append(" UNION ");
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_stafftempdataright s ");
		sql.append(" WHERE s.data_type = 'P' ");
		sql.append(" AND ((s.use_tag = '1' and sysdate >= s.start_date and sysdate <= s.end_date) ");
		sql.append(" or (s.use_tag = '0' and s.used_times < s.times)) ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		return queryList(sql.toString(), new Object []{staffId, staffId, staffId});
	} 
	
	public IDataset queryDiscntDataRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_staffdataright s ");
		sql.append(" WHERE s.right_attr = '0' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_type = 'D' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT r.data_code ");
		sql.append(" FROM tf_m_staffdataright s, tf_m_roledataright r ");
		sql.append(" WHERE s.right_attr = '1' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_code = r.role_code ");
		sql.append(" AND r.data_type = 'D' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND (r.rsvalue1 IS NULL OR r.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		if (date_check) {
			sql.append(" AND sysdate between decode(r.VALID_DATE, null, sysdate-1, r.valid_date) and decode(r.EXPIRE_DATE, null, to_date('20501231235959', 'yyyyMMddHH24miss'), r.expire_date)");
		}
		sql.append(" UNION ");
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_stafftempdataright s ");
		sql.append(" WHERE s.data_type = 'D' ");
		sql.append(" AND ((s.use_tag = '1' and sysdate >= s.start_date and sysdate <= s.end_date) ");
		sql.append(" or (s.use_tag = '0' and s.used_times < s.times)) ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		return queryList(sql.toString(), new Object []{staffId, staffId, staffId});
	}
	
	public IDataset queryFieldDataRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT s.data_code, s.right_class ");
		sql.append(" FROM tf_m_staffdataright s ");
		sql.append(" WHERE s.right_attr = '0' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_type = '1' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT r.data_code, r.right_class ");
		sql.append(" FROM tf_m_staffdataright s, tf_m_roledataright r ");
		sql.append(" WHERE s.right_attr = '1' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_code = r.role_code ");
		sql.append(" AND r.data_type = '1' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND (r.rsvalue1 IS NULL OR r.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		if (date_check) {
			sql.append(" AND sysdate between decode(r.VALID_DATE, null, sysdate-1, r.valid_date) and decode(r.EXPIRE_DATE, null, to_date('20501231235959', 'yyyyMMddHH24miss'), r.expire_date)");
		}
		sql.append(" UNION ");
		sql.append(" SELECT s.data_code, s.right_class ");
		sql.append(" FROM tf_m_stafftempdataright s ");
		sql.append(" WHERE s.data_type = '1' ");
		sql.append(" AND ((s.use_tag = '1' and sysdate >= s.start_date and sysdate <= s.end_date) ");
		sql.append(" or (s.use_tag = '0' and s.used_times < s.times)) ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		return queryList(sql.toString(), new Object []{staffId, staffId, staffId});
	}
	
	public IDataset queryPkgDataRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_staffdataright s ");
		sql.append(" WHERE s.right_attr = '0' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_type = 'K' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT r.data_code ");
		sql.append(" FROM tf_m_staffdataright s, tf_m_roledataright r ");
		sql.append(" WHERE s.right_attr = '1' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_code = r.role_code ");
		sql.append(" AND r.data_type = 'K' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND (r.rsvalue1 IS NULL OR r.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		if (date_check) {
			sql.append(" AND sysdate between decode(r.VALID_DATE, null, sysdate-1, r.valid_date) and decode(r.EXPIRE_DATE, null, to_date('20501231235959', 'yyyyMMddHH24miss'), r.expire_date)");
		}
		sql.append(" UNION ");
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_stafftempdataright s ");
		sql.append(" WHERE s.data_type = 'K' ");
		sql.append(" AND ((s.use_tag = '1' and sysdate >= s.start_date and sysdate <= s.end_date) ");
		sql.append(" or (s.use_tag = '0' and s.used_times < s.times)) ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		return queryList(sql.toString(), new Object []{staffId, staffId, staffId});
	}
	
	public IDataset queryServiceDataRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_staffdataright s ");
		sql.append(" WHERE s.right_attr = '0' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_type = 'S' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT r.data_code ");
		sql.append(" FROM tf_m_staffdataright s, tf_m_roledataright r ");
		sql.append(" WHERE s.right_attr = '1' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.data_code = r.role_code ");
		sql.append(" AND r.data_type = 'S' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND (r.rsvalue1 IS NULL OR r.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		if (date_check) {
			sql.append(" AND sysdate between decode(r.VALID_DATE, null, sysdate-1, r.valid_date) and decode(r.EXPIRE_DATE, null, to_date('20501231235959', 'yyyyMMddHH24miss'), r.expire_date)");
		}
		sql.append(" UNION ");
		sql.append(" SELECT s.data_code ");
		sql.append(" FROM tf_m_stafftempdataright s ");
		sql.append(" WHERE s.data_type = 'S' ");
		sql.append(" AND ((s.use_tag = '1' and sysdate >= s.start_date and sysdate <= s.end_date) ");
		sql.append(" or (s.use_tag = '0' and s.used_times < s.times)) ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		return queryList(sql.toString(), new Object []{staffId, staffId, staffId});
	}
	
	public IDataset queryFuncRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT s.right_code ");
		sql.append(" FROM tf_m_stafffuncright s ");
		sql.append(" WHERE s.right_attr = '0' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		sql.append(" UNION ");
		sql.append(" SELECT r.right_code ");
		sql.append(" FROM tf_m_stafffuncright s, tf_m_rolefuncright  r ");
		sql.append(" WHERE s.right_attr = '1' ");
		sql.append(" AND s.right_tag = '1' ");
		sql.append(" AND s.right_code = r.role_code");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND (r.rsvalue1 IS NULL OR r.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		if (date_check) {
			sql.append(" AND sysdate between decode(r.VALID_DATE, null, sysdate-1, r.valid_date) and decode(r.EXPIRE_DATE, null, to_date('20501231235959', 'yyyyMMddHH24miss'), r.expire_date)");
		}
		sql.append(" UNION ");
		sql.append(" SELECT s.right_code ");
		sql.append(" FROM tf_m_stafftempfuncright s ");
		sql.append(" WHERE ((s.use_tag = '1' and sysdate >= s.start_date and sysdate <= s.end_date) ");
		sql.append(" or (s.use_tag = '0' and s.used_times < s.times)) ");
		sql.append(" AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1') ");
		sql.append(" AND s.staff_id = ? ");
		return queryList(sql.toString(), new Object []{staffId, staffId, staffId});
	}
	
	public IDataset queryOpRoleRight(String staffId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT distinct b.op_role_code ");
		sql.append(" FROM tf_m_staffoprole a, td_m_op_role b ");
		sql.append(" WHERE a.op_role_code = b.op_role_code ");
		sql.append(" AND a.right_tag = '1' ");
		sql.append(" AND b.validflag = '0' ");
		sql.append(" AND a.staff_id = ? ");
		return queryList(sql.toString(), new Object []{staffId});
	}
	
	public IDataset queryAllMenu() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT MENU_ID, RIGHT_CODE FROM TD_B_SYSTEMGUIMENU ");
		return queryList(sql.toString(), new Object []{});
	}
	
	public IDataset queryMenuPath() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT a.menu_id ");
		sql.append(" FROM (SELECT connect_by_isleaf isleaf, sys_connect_by_path(t.menu_id, ',') menu_id");
		sql.append(" FROM TD_B_SYSTEMGUIMENU t ");
		sql.append(" start with menu_id is not null ");
		sql.append(" connect by prior parent_menu_id = menu_id) a ");
		sql.append(" where a.isleaf = 1 ");
		return queryList(sql.toString(), new Object []{});
	}
	
	public IDataset queryNodeMenuWithRightcode() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT menu_id, RIGHT_CODE ");
		sql.append(" FROM TD_B_SYSTEMGUIMENU ");
		sql.append(" where menu_id in (SELECT parent_menu_id FROM TD_B_SYSTEMGUIMENU) ");
		sql.append(" AND RIGHT_CODE IS NOT NULL ");
		return queryList(sql.toString(), new Object []{});
	}
}
