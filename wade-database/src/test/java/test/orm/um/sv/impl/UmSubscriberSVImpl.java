/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package test.orm.um.sv.impl;

import java.sql.SQLException;
import java.util.Map;

import com.ailk.common.data.impl.Pagination;
import com.ailk.database.orm.bo.BOContainer;
import com.ailk.database.orm.sql.ISQLAppender;
import com.ailk.database.orm.sql.SQLAppenderFactory;
import com.veris.crm.rs.BOContainerReader;
import com.veris.crm.sv.AbstractSV;

import test.orm.um.bo.UmSubscriberBO;
import test.orm.um.dao.UmSubscriberDAO;
import test.orm.um.sv.interfaces.IUmSubscriberSV;

/**
 * @description
 * 增删改查的服务示例
 */
public class UmSubscriberSVImpl extends AbstractSV implements IUmSubscriberSV {

	private static final long serialVersionUID = 1141028894343625053L;
	
	@Override
	public String getRouteId() {
		return "FILE1";
	}


	/**
	 * 单表查询
	 * @param partitionId
	 * @return
	 * @throws SQLException
	 */
	@Override
	public UmSubscriberBO getUmSubscriber(int partitionId, long subscriberInsId) throws Exception {
		return new UmSubscriberDAO(getRouteId()).getEntity(partitionId, subscriberInsId);
	}
	
	/**
	 * 单表查询
	 */
	@Override
	public UmSubscriberBO getUmSubscriber(int partitionId, long subscriberInsId, String[] cols) throws Exception {
		return new UmSubscriberDAO(getRouteId()).getEntity(partitionId, subscriberInsId, cols);
	}
	
	
	/**
	 * 查询单表结果集
	 * @param partitionId
	 * @param stauts
	 * @return
	 * @throws SQLException
	 */
	@Override
	public UmSubscriberBO[] getUmSubscriber(int partitionId, String subscriberStateCodeset) throws Exception {
		//需要查询的字段列表
		String[] cols = new String[] {UmSubscriberBO.ACCESS_NUM, UmSubscriberBO.OPEN_DATE};
		
		ISQLAppender<UmSubscriberBO> appender = SQLAppenderFactory.create(UmSubscriberBO.class);
		appender.addParameter(UmSubscriberBO.PARTITION_ID, partitionId);
		appender.addParameter(UmSubscriberBO.SUBSCRIBER_STATE_CODESET, subscriberStateCodeset);

		//根据业务逻辑拼SQL条件
		appender.where(UmSubscriberBO.PARTITION_ID).equal().bind(UmSubscriberBO.PARTITION_ID);
		appender.and(UmSubscriberBO.SUBSCRIBER_STATE_CODESET).equal().bind(UmSubscriberBO.SUBSCRIBER_STATE_CODESET);
		
		return new UmSubscriberDAO(getRouteId()).getEntities(cols, appender, -1, -1);
	}
	
	@Override
	public UmSubscriberBO[] getUmSubscriber(Map<String, Object> parameter, Pagination pagin) throws Exception {
		//需要查询的字段列表
		//String[] cols = new String[] {UmSubscriberBO.ACCESS_NUM, UmSubscriberBO.OPEN_DATE};
		String[] cols = null;
		
		//批量设置参数
		ISQLAppender<UmSubscriberBO> appender = SQLAppenderFactory.create(UmSubscriberBO.class);
		appender.setParameter(parameter);
		
		//根据业务逻辑拼SQL条件
		appender.append(" WHERE " + UmSubscriberBO.PARTITION_ID + " = :" + UmSubscriberBO.PARTITION_ID);
		appender.append(" AND " + UmSubscriberBO.SUBSCRIBER_STATE_CODESET + " = :" + UmSubscriberBO.SUBSCRIBER_STATE_CODESET);
		
		// 处理分页
		if (null != pagin)
			return new UmSubscriberDAO(getRouteId()).getEntities(cols, appender, pagin.getStart(), pagin.getEnd());
		else
			return new UmSubscriberDAO(getRouteId()).getEntities(cols, appender, -1, -1);
	}
	
	@Override
	public void saveUmSubscriber(UmSubscriberBO entity) throws Exception {
		new UmSubscriberDAO(getRouteId()).saveEntity(entity);
	}
	
	@Override
	public void saveUmSubscriber(UmSubscriberBO[] entities) throws Exception {
		new UmSubscriberDAO(getRouteId()).saveEntity(entities);
	}
	
	@Override
	public void saveUmSubscriberBatch(UmSubscriberBO[] entities) throws Exception {
		new UmSubscriberDAO(getRouteId()).saveBatch(entities);
	}
	
	
	@Override
	public void saveUmSubscriber(UmSubscriberBO entity, String[] columns) throws Exception {
		new UmSubscriberDAO(getRouteId()).saveEntity(entity, columns);
	}
	
	@Override
	public void saveUmSubscriber(UmSubscriberBO[] entities, String[] columns) throws Exception {
		new UmSubscriberDAO(getRouteId()).saveEntity(entities, columns);
	}
	
	@Override
	public void saveUmSubscriberBatch(UmSubscriberBO[] entities, String[] columns) throws Exception {
		new UmSubscriberDAO(getRouteId()).saveBatch(entities, columns);
	}
	
	@Override
	public int countUmSubscriber(Map<String, Object> parameter, String[] columns) throws Exception {
		ISQLAppender<UmSubscriberBO> appender = SQLAppenderFactory.create(UmSubscriberBO.class);
		appender.setParameter(parameter);
		
		// 演示多种拼SQL的方法
		appender.where(UmSubscriberBO.PARTITION_ID).equal().bind(UmSubscriberBO.PARTITION_ID);
		appender.append(" AND ").bind(UmSubscriberBO.CREATE_DATE);//日期类型
		appender.betweenToDate("START_DATE", "END_DATE", "yyyyMMdd");//字符串转日期类型TO_DATE
		
		return new UmSubscriberDAO(getRouteId()).countEntity(appender);
	}
	
	
	@Override
	public BOContainer[] getEntities(Class<? extends BOContainer> clazz, Map<String, Object> parameter, Pagination pagin) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append(" AND PARTITION_ID=:PARTITION_ID");
		sql.append(" AND SUBSCRIBER_STATE_CODESET=:SUBSCRIBER_STATE_CODESET");
		return new UmSubscriberDAO(getRouteId()).getEntities(clazz, new BOContainerReader(), sql, parameter, pagin);
	}
}
