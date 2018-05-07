-- Create table
create table UCR_CEN1.SYS_ADM_ACCT
(
  ACCT_ID     NUMBER(12) not null,
  ACCT_NAME   VARCHAR2(45),
  ACCT_SN     VARCHAR2(45),
  STATUS      CHAR(1) not null,
  CREATE_TIME DATE
);
-- Add comments to the table 
comment on table UCR_CEN1.SYS_ADM_ACCT
  is '系统账号';
-- Add comments to the columns 
comment on column UCR_CEN1.SYS_ADM_ACCT.ACCT_ID
  is '账户标识';
comment on column UCR_CEN1.SYS_ADM_ACCT.ACCT_NAME
  is '账户名称';
comment on column UCR_CEN1.SYS_ADM_ACCT.ACCT_SN
  is '联系人';
comment on column UCR_CEN1.SYS_ADM_ACCT.STATUS
  is '状态：U在用，N不可用';
-- Create/Recreate primary, unique and foreign key constraints 
alter table UCR_CEN1.SYS_ADM_ACCT
  add constraint PK_SYS_ADM_ACCT primary key (ACCT_ID)
  using index ;
grant select, insert, update, delete on UCR_CEN1.SYS_ADM_ACCT to UOP_CEN1;

-- Create sequence 
create sequence SEQ_ACCT_ID
minvalue 0
maxvalue 999999999999
start with 981
increment by 1
cache 20
cycle;


insert into SYS_ADM_ACCT (ACCT_ID, ACCT_NAME, ACCT_SN, STATUS, CREATE_TIME)
values ('198', '198', '137', 'U', to_date('06-04-2013 12:11:11', 'dd-mm-yyyy hh24:mi:ss'));

insert into SYS_ADM_ACCT (ACCT_ID, ACCT_NAME, ACCT_SN, STATUS, CREATE_TIME)
values ('199', '98', '137', 'U', to_date('06-04-2013 12:11:11', 'dd-mm-yyyy hh24:mi:ss'));



