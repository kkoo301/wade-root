/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月22日
 * 
 * Just Do IT.
 */
package test.orm.um.sv.interfaces;

import java.util.Map;

import com.ailk.common.data.impl.Pagination;
import com.ailk.database.orm.bo.BOContainer;
import com.ailk.database.orm.bo.BOEntity;

import test.orm.um.bo.UmSubscriberBO;

/**
 * @description
 * 服务实现类
 */
public interface IUmSubscriberSV {
	
	/**
	 * 按主键查询
	 * @param partitionId
	 * @return
	 * @throws Exception
	 */
	public UmSubscriberBO getUmSubscriber(int partitionId, long subscriberInsId) throws Exception;
	
	/**
	 * 按主键查询，指定查询字段
	 * @param partitionId
	 * @param subscriberInsId
	 * @param cols
	 * @return
	 * @throws Exception
	 */
	public UmSubscriberBO getUmSubscriber(int partitionId, long subscriberInsId, String[] cols) throws Exception;
	
	
	/**
	 * 单表查询
	 * @param partitionId
	 * @param stauts
	 * @return
	 * @throws Exception
	 */
	public UmSubscriberBO[] getUmSubscriber(int partitionId, String subscriberStateCodeset) throws Exception;
	
	/**
	 * 单表分页查询
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	public UmSubscriberBO[] getUmSubscriber(Map<String, Object> parameter, Pagination pagin) throws Exception;

	
	/**
	 * 单条更新
	 * @param entities
	 * @throws Exception
	 */
	public void saveUmSubscriber(UmSubscriberBO entity) throws Exception;
	
	/**
	 * 批量更新
	 * @param entity
	 * @throws Exception
	 */
	public void saveUmSubscriber(UmSubscriberBO[] entity) throws Exception;
	
	/**
	 * 批量更新
	 * @param entities
	 * @throws Exception
	 */
	public void saveUmSubscriberBatch(UmSubscriberBO[] entities) throws Exception;
	
	
	/**
	 * 单条更新
	 * @param entities
	 * @param columns
	 * @throws Exception
	 */
	public void saveUmSubscriber(UmSubscriberBO entity, String[] columns) throws Exception;
	
	/**
	 * 批量更新
	 * @param entity
	 * @param columns
	 * @throws Exception
	 */
	public void saveUmSubscriber(UmSubscriberBO[] entity, String[] columns) throws Exception;
	
	/**
	 * 批量更新
	 * @param entities
	 * @param columns
	 * @throws Exception
	 */
	public void saveUmSubscriberBatch(UmSubscriberBO[] entities, String[] columns) throws Exception;
	
	/**
	 * 统计
	 * @param parameter
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public int countUmSubscriber(Map<String, Object> parameter, String[] columns) throws Exception;
	
	/**
	 * 弱类型单表查询
	 * @param whereSQL
	 * @param parameter
	 * @param pagin
	 * @return
	 * @throws Exception
	 */
	public BOContainer[] getEntities(Class<? extends BOContainer> clazz, Map<String, Object> parameter, Pagination pagin) throws Exception;
}
