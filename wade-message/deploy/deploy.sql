#insert static param
insert into td_s_static (TYPE_ID,DATA_ID,DATA_NAME,SUBSYS_CODE)
( select 'COMET_SERVER_ADDR',area_code ,'http://10.154.63.121:8000/','NCM' from td_m_area where parent_area_code ='HNAN');

--Comet Task
delete from td_m_asynctask where TASK_ID = '1201';

insert into td_m_asynctask (TASK_ID, TASK_NAME, DESTINATION, CLASS_NAME, TIMEOUT_SECOND, STATE, SUBSYS_CODE, REMARKS, AUTHOR)
values (1201, 'Comet消息推送服务', 'cluster-comet-all', 'com.ailk.biz.message.comet.server.impl.SendTask', null, 'U', 'NGB', null, null);
