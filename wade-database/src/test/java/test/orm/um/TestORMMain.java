/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月22日
 * 
 * Just Do IT.
 */
package test.orm.um;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.data.impl.Pagination;
import com.ailk.database.orm.bo.BOContainer;
import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.util.BOUtil;
import com.ailk.database.rule.TableRuleContext;
import com.ailk.database.transaction.LocalMutilTransaction;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.service.session.SessionManager;
import com.veris.crm.sv.AppSVFactory;

import test.orm.um.bo.UmSubscriberBO;
import test.orm.um.dao.UmSubscriberDAO;
import test.orm.um.sv.interfaces.IUmSubscriberSV;

/**
 * @description
 * 验证ORM的功能
 */
public class TestORMMain implements Callable<Object> {
	
	private static final Logger log = LoggerFactory.getLogger(TestORMMain.class);
	
	public static void main(String[] args) throws Exception {
		invoke(new TestORMMain());
		
		/*String strTime = "20171112030201";
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMddHHmmss");
		DateTime dt = format.parseDateTime(strTime);
		
		System.out.println(">>>>" + dt.getMillis());
		
		dt = new DateTime(0L);
		System.out.println(dt.toString(format));*/
		
		/*StringBuilder s = new StringBuilder();
		s.append("adfasdfs,");
		System.out.println(s.charAt(s.length() - 1));
		s.deleteCharAt(s.length() - 1);
		System.out.println(s.toString());*/
	}
	
	
	@Override
	public Object call() throws Exception {
		IUmSubscriberSV sv = AppSVFactory.getService(IUmSubscriberSV.class);
		
		int partitionId = 10;
		long subscriberInsId = 1101100800080011L;
		String subscriberStateCodeset = "6";
		
		//场景：默认值
		/*Connection conn = SessionManager.getInstance().getSessionConnection("FILE1");
		String sql2 = "SELECT COLUMN_NAME, DATA_DEFAULT FROM USER_TAB_COLS WHERE TABLE_NAME = ? ";
		String sql = "update UM_SUBSCRIBER set MPUTE_MONTH_FEE=? where PARTITION_ID=? and SUBSCRIBER_INS_ID=?";
		PreparedStatement stmt = conn.prepareStatement(sql2);
		stmt.setString(1, "UM_SUBSCRIBER");
		ResultSet result = stmt.executeQuery();
		while (result.next()) {
			System.out.println(">>>>>" + result.getString(1) + result.getString(2));
			
		}
		stmt.setString(1, "");
		stmt.setInt(2, 9);
		stmt.setLong(3, 1100073100000009l);
		stmt.executeUpdate();
		conn.commit();
		conn.close();*/
		
		//场景：手动创建BOContainer后set属性，再New成BO，检查更新字段列表
		/*BOContainer bc = new BOContainer();
		bc.set(UmSubscriberBO.SUBSCRIBER_INS_ID, "abc");
		UmSubscriberBO bo = BOUtil.createByEntity(UmSubscriberBO.class, bc);
		log.debug("获取修改的字段列表:" + bo.getChangedProperties());
		bo = BOUtil.create(UmSubscriberBO.class, bc.toMap());
		log.debug("获取修改的字段列表:" + bo.getChangedProperties());*/
		
		
		//场景：创建BO对象
		/*Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(UmSubscriberBO.PARTITION_ID, String.valueOf(partitionId));
		parameter.put(UmSubscriberBO.SUBSCRIBER_INS_ID, String.valueOf(subscriberInsId));
		parameter.put(UmSubscriberBO.OPEN_DATE, "2015-10-10 12:21:22");
		
		UmSubscriberBO entity = BOUtil.create(UmSubscriberBO.class, parameter);
		System.out.println(entity.getPartitionId());
		System.out.println(entity.getSubscriberInsId());
		System.out.println(new java.util.Date(entity.getOpenDate().getTime()));*/
		
		//场景：按主键查询所有字段(包括ROWID)
		UmSubscriberBO entity = sv.getUmSubscriber(partitionId, subscriberInsId);
		if (entity.isEmpty()) {
			log.error("单表主键查询结果为NULL");
			return null;
		}
		log.debug("单表主键查询结果1：" + entity.toMap().toString());
		log.debug("获取查询后修改的字段列表：" + entity.getChangedProperties());
		entity.setAcctTag("1");
		log.debug("获取SET后修改的字段列表：" + entity.getChangedProperties());
		
		/*Set<String> columns = entity.getColumnNames();
		log.debug("获取表字段列表：" + columns.toString());
		columns.remove("CUST_ID");
		log.debug("获取表字段列表：" + entity.getColumnNames());*/
		
		//场景：按主键查询指定字段
		/*UmSubscriberBO bo2 = sv.getUmSubscriber(partitionId, subscriberInsId, new String[] {UmSubscriberBO.ACCESS_NUM, UmSubscriberBO.OPEN_DATE});
		log.debug("单表主键查询结果2：" + bo2.toMap().toString());*/
		
		//场景：多条件查询
		/*UmSubscriberBO[] bos = sv.getUmSubscriber(partitionId, subscriberStateCodeset);
		log.debug("单表查询结果：" + bos.length);
		log.debug("单表查询结果：" + bos[0].toMap());*/
		
		//场景：多条件分页查询
		/*Map<String, Object> parameter = new HashMap<String, Object>(10);
		parameter.put(UmSubscriberBO.PARTITION_ID, partitionId);
		parameter.put(UmSubscriberBO.SUBSCRIBER_STATE_CODESET, subscriberStateCodeset);
		UmSubscriberBO[] bos = sv.getUmSubscriber(parameter, new Pagination(10));
		log.debug("单表主键查询结果：" + bos.length);
		log.debug("单表主键查询结果：" + bos[0].toMap());*/
		
		//场景：多条件统计查询 + 拼日期SQL
		/*Map<String, Object> parameter = new HashMap<String, Object>(10);
		parameter.put(UmSubscriberBO.PARTITION_ID, partitionId);
		parameter.put(UmSubscriberBO.CREATE_DATE, new Date(System.currentTimeMillis()));
		parameter.put("START_DATE", DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd"));
		parameter.put("END_DATE", DateFormatUtils.format(System.currentTimeMillis() + 3600000L * 48, "yyyyMMdd"));
		int count = sv.countUmSubscriber(parameter, new String[] {"START_DATE", "END_DATE"});
		log.debug("统计结果:" + count);*/
		
		//场景：查询后修改
		entity.setCustId(1000);
		entity.setSubscriberInsId(1101100800080012L);
		entity.setOpenDate(new Date(System.currentTimeMillis()));
		sv.saveUmSubscriber(entity);
		
		//场景：手动创建BO后修改
		/*UmSubscriberBO bo = new UmSubscriberBO(entity.toMap());
		bo.setCustId(1000);
		sv.saveUmSubscriber(bo);*/
		
		//场景：批量修改
		/*for (UmSubscriberBO bo : bos) {
			bo.setCustId(1000L);
			bo.setOpenDate(new Date(System.currentTimeMillis()));
		}
		sv.saveUmSubscriber(bos);*/
		
		//场景：批处理修改
		/*for (int i = 0, len = bos.length; i < len; i++) {
			UmSubscriberBO bo = bos[i];
			bo.setCustId(2000L);
			bo.setOpenDate(new Date(System.currentTimeMillis()));
			if (i % 2 == 0) {
				bo.setStsToNew();
				bo.setSubscriberInsId(bo.getSubscriberInsId() + 30);
			} else if (i % 3 == 0){
				bo.setStsToDelete();
			}
		}
		sv.saveUmSubscriberBatch(bos);*/
		
		//场景：指定字段保存
		/*entity.setCustId(1000);
		entity.setSubscriberInsId(9707010104950011L);
		entity.setOpenDate(new Date(System.currentTimeMillis()));
		sv.saveUmSubscriber(entity, new String[] {UmSubscriberBO.CUST_ID, UmSubscriberBO.PARTITION_ID, UmSubscriberBO.SUBSCRIBER_INS_ID});*/
		
		//场景：指定字段多条保存
		/*for (UmSubscriberBO bo : bos) {
			bo.setCustId(1000L);
			bo.setOpenDate(new Date(System.currentTimeMillis()));
		}
		sv.saveUmSubscriber(bos, new String[] {UmSubscriberBO.CUST_ID, UmSubscriberBO.PARTITION_ID, UmSubscriberBO.SUBSCRIBER_INS_ID});*/
		
		//场景：指定字段批量保存
		/*for (int i = 0, len = bos.length; i < len; i++) {
			UmSubscriberBO bo = bos[i];
			bo.setCustId(2000L);
			bo.setOpenDate(new Date(System.currentTimeMillis()));
			if (i % 2 == 0) {
				bo.setStsToNew();
				bo.setSubscriberInsId(bo.getSubscriberInsId() + 100);
			} else if (i % 3 == 0){
				bo.setStsToDelete();
			}
		}
		sv.saveUmSubscriberBatch(bos, new String[] {UmSubscriberBO.CUST_ID, UmSubscriberBO.PARTITION_ID, UmSubscriberBO.SUBSCRIBER_INS_ID});*/
		
		//场景：查询后删除再新增
		/*entity.setStsToDelete();
		sv.saveUmSubscriber(entity);
		entity.setStsToNew();
		sv.saveUmSubscriber(entity);*/
		
		
		//场景：弱类型返回
		/*Map<String, Object> parameter = new HashMap<String, Object>(10);
		parameter.put(UmSubscriberBO.PARTITION_ID, partitionId);
		parameter.put(UmSubscriberBO.SUBSCRIBER_STATE_CODESET, subscriberStateCodeset);
		Pagination pagin = new Pagination();
		BOContainer[] dcs = sv.getEntities(UmSubscriberBO.class, parameter, pagin);
		System.out.println("查询结果集大小：" + dcs.length);
		System.out.println("统计值：" + pagin.getCount());*/
		
		//场景：分表
		
		
		return null;
	}
	
	
	private static void invoke(Callable<Object> callable) throws Exception {
		try {
			callable.call();
			
			LocalMutilTransaction.commit();
		} catch (Exception e) {
			LocalMutilTransaction.rollback();
			throw e;
		} finally {
			LocalMutilTransaction.close();
		}
	}

}
