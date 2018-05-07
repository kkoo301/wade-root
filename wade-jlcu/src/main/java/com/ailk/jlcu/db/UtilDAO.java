package com.ailk.jlcu.db;

import com.ailk.common.data.IDataset;
import com.ailk.database.dao.impl.BaseDAO;

public class UtilDAO extends BaseDAO {
/*
	public IDataset getFlowChart(String xTransCode) throws Exception {
		StringBuffer sql = new StringBuffer(1000);
		sql.append(" select * ");
		sql.append("  from (select t.DEFINITION ");
		sql.append("		from TD_JLCU_DEFINITION t ");
		sql.append("		where t.X_TRANS_CODE = ? ");
		sql.append("		order by VERSION DESC) ");
		sql.append("  where rownum <= 1 ");

		return queryList(sql.toString(), new Object[] { xTransCode });
	}*/
}