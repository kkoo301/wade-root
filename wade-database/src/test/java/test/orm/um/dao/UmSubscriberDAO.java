/**
* Copyright: Copyright (c) 2017 Asiainfo
*
* @version: v1.0.0
* @date: 2017-05-17 00:11:43
*
* Just Do IT.
*/
package test.orm.um.dao;



import java.sql.SQLException;

import com.ailk.biz.bean.BizEntityDAO;

import com.ailk.database.orm.sql.ISQLAppender;

import test.orm.um.bo.UmSubscriberBO;


/**
 * @description 业务DAO实现<br>
 *              处理数据源、连接、事务，以及单表的增删改查，分页，统计功能<br>
 *              建议由工具统一生成
 */
public class UmSubscriberDAO extends BizEntityDAO<UmSubscriberBO> {


   /**
    * 采用默认数据源连接，根据当前服务归属子系统获取默认连接名，默认配置在dbroute.properties，如：<br>
    * route.group.upc=upc<br>
    * route.upc.def=upc<br>
    */
   public UmSubscriberDAO() {
       super();
   }


   /**
    * 采用指定的数据源
    */
   public UmSubscriberDAO(String dataSourcenName) {
       super(dataSourcenName);
   }

   /**
    * 根据主键查询并返回BO对象
    * 
    * @param clazz
    * @return
    * @throws SQLException
    */
    public UmSubscriberBO getEntity(int partitionId, long subscriberInsId) throws SQLException {
        Object[] values = new Object[] {partitionId, subscriberInsId};
        return query(createEntityByPrimary(UmSubscriberBO.class, values), values);
    }


   /**
    * 根据主键查询并返回BO对象
    * @param cols
    * @return
    * @throws SQLException
    */
    public UmSubscriberBO getEntity(int partitionId, long subscriberInsId, String[] cols) throws SQLException {
        Object[] values = new Object[] {partitionId, subscriberInsId};
        return query(createEntityByPrimary(UmSubscriberBO.class, values), cols, values);
    }


   /**
    * 单表查询
    * 
    * @param cols
    *            需要查询的字段，传NULL时为所有字段
    * @param appender
    * @param parameter
    * @param start 分页起始值，-1则不分页
    * @param end   分页结束值，-1则不分页
    * @return
    * @throws SQLException
    */
   public UmSubscriberBO[] getEntities(String[] cols, ISQLAppender<UmSubscriberBO> appender, int start, int end) throws SQLException {
       return query(cols, appender, start, end).toArray(new UmSubscriberBO[]{});
   }


   /**
    * 修改对象，根据状态来判断执行增、删、改操作。<br>
    * 1.isNew() 则Insert，可通过entity.setStsToNew()修改为该状态;<br>
    * 2.isDelete() 则Delete，可通过entity.setStsToDelete()修改为该状态;<br>
    * @param entity
    * @throws SQLException
    */
   public void saveEntity(UmSubscriberBO entity) throws SQLException {
       save(entity);
   }


   /**
    * 修改对象，根据状态来判断执行增、删、改操作；可通过colums来指定条件字段，适用于不按主键操作的场景;<br>
    * 1.isNew() 则Insert，可通过entity.setStsToNew()修改为该状态;<br>
    * 2.isDelete() 则Delete，可通过entity.setStsToDelete()修改为该状态;<br>
    * @param entity
    * @param columns
    * @throws SQLException
    */
   public void saveEntity(UmSubscriberBO entity, String[] columns) throws SQLException {
       save(entity, columns);
   }


   /**
    * 批量修改，遍历执行save(entity)操作
    * @param entities
    * @throws SQLException
    */
   public void saveEntity(UmSubscriberBO[] entities) throws SQLException {
       save(entities);
   }


   /**
    * 批量修改，遍历执行save(entity)操作;可通过colums来指定条件字段，适用于不按主键操作的场景;
    * @param entities
    * @param columns
    * @throws SQLException
    */
   public void saveEntity(UmSubscriberBO[] entities, String[] columns) throws SQLException {
       save(entities, columns);
   }


   /**
    * 批量修改，先根据状态生成批量语句，然后一次性执行
    * @param entities
    * @throws SQLException
    */
   public void saveBatch(UmSubscriberBO[] entities) throws SQLException {
       super.saveBatch(entities);
   }


   /**
    * 批量修改，先根据状态生成批量语句，然后一次性执行;可通过colums来指定条件字段，适用于不按主键操作的场景;
    * @param entities
    * @param columns
    * @throws SQLException
    */
   public void saveBatch(UmSubscriberBO[] entities, String[] columns) throws SQLException {
       super.saveBatch(entities, columns);
   }


   /**
    * 统计
    * @param cols
    * @param appender
    * @return
    * @throws SQLException
    */
   public int countEntity(ISQLAppender<UmSubscriberBO> appender) throws SQLException {
       return count(appender);
   }


}
