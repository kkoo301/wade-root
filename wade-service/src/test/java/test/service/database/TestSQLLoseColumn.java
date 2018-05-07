package test.service.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.service.session.app.AppInvoker;

public class TestSQLLoseColumn {
	
	static {
		//ConnectionManagerFactory.getConnectionManager();
	}
	
	public void test() throws Exception {
		String sql = "SELECT A.RULE_ID,A.RULE_NAME,A.SCORE,A.SCORE_NUM,A.SCORE_TYPE_CODE,A.START_DATE,A.STATUS,A.UNIT,A.UPDATE_TIME,A.AMONTHS,A.BRAND_CODE,A.CLASS_LIMIT,A.FENABLED_TAG,A.COND_FACTOR1,A.COND_FACTOR2,A.COND_FACTOR3,A.DEPOSIT_CODE,A.END_DATE,A.EPARCHY_CODE,A.EXCHANGE_LIMIT,A.EXCHANGE_MODE,A.EXCHANGE_TYPE_CODE,A.FMONTHS,A.GIFT_TYPE_CODE,A.MONEY_RATE,A.REMARK,A.REWARD_LIMIT,A.RIGHT_CODE,A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5,A.RSRV_STR6,A.RSRV_STR7,A.RSRV_STR8,A.RSRV_STR9,A.RSRV_STR10,B.EXCHANGE_TYPE FROM TD_B_EXCHANGE_RULE A,TD_B_SCORE_EXCHANGE_TYPE B WHERE A.EXCHANGE_TYPE_CODE(+) = B.EXCHANGE_TYPE_CODE AND   A.STATUS = '0' AND   A.END_DATE+0 >= SYSDATE ORDER BY rule_id";
		
		IData source = new DataMap();
		try {
			BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "cen1");
			IDataset ds = dao.queryList(sql, source);
			
			IData data = ds.getData(1);
			Iterator<String> iter = data.keySet().iterator();
			int index = 0;
			while (iter.hasNext()) {
				index ++;
				String key = iter.next();
				System.out.println("key " + index + " : " + key + " = " + data.getString(key));
			}
			
			
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public void testData() {
		IData data = new DataMap();
		data.put("A", null);
		System.out.println(data);
	}
	
	public static void main(String[] args) throws Exception {
		//AppInvoker.invoke(null, new TestSQLLoseColumn(), "test", null);
		
		new TestSQLLoseColumn().testData();
	}
}
