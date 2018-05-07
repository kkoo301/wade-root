/**
* Copyright: Copyright (c) 2017 Asiainfo
*
* @version: v1.0.0
* @date: 2017-05-17 00:11:43
*
* Just Do IT.
*/
package test.orm.um.bo;

import java.sql.Date;
import java.sql.Types;

import java.util.Map;

import com.ailk.database.orm.annotation.meta.Column;
import com.ailk.database.orm.annotation.meta.Table;
import com.ailk.database.orm.bo.BOContainer;
import com.ailk.database.orm.bo.DataType;
import com.ailk.database.orm.err.BOException;

@Table(name = "UM_SUBSCRIBER", primary = "PARTITION_ID,SUBSCRIBER_INS_ID")
public class UmSubscriberBO extends BOContainer {

    private static final long serialVersionUID = 1L;


    public UmSubscriberBO() {
        super();
    }

    public UmSubscriberBO(Map<String, Object> properties) {
        super(properties);
    }

    @Column(name="PARTITION_ID", type=Types.NUMERIC, length=4, desc ="") 
    public static final String PARTITION_ID = "PARTITION_ID";

    @Column(name="SUBSCRIBER_INS_ID", type=Types.NUMERIC, length=16, desc ="") 
    public static final String SUBSCRIBER_INS_ID = "SUBSCRIBER_INS_ID";

    @Column(name="CUST_ID", type=Types.NUMERIC, length=16, desc ="") 
    public static final String CUST_ID = "CUST_ID";

    @Column(name="PROD_LINE_ID", type=Types.NUMERIC, length=16, desc ="") 
    public static final String PROD_LINE_ID = "PROD_LINE_ID";

    @Column(name="PROD_LINE_NAME", type=Types.VARCHAR, length=128, desc ="") 
    public static final String PROD_LINE_NAME = "PROD_LINE_NAME";

    @Column(name="SUBSCRIBER_TYPE", type=Types.VARCHAR, length=6, desc ="") 
    public static final String SUBSCRIBER_TYPE = "SUBSCRIBER_TYPE";

    @Column(name="ACCESS_NUM", type=Types.VARCHAR, length=32, desc ="") 
    public static final String ACCESS_NUM = "ACCESS_NUM";

    @Column(name="PASSWORD_TYPE", type=Types.NUMERIC, length=2, desc ="") 
    public static final String PASSWORD_TYPE = "PASSWORD_TYPE";

    @Column(name="PASSWORD", type=Types.VARCHAR, length=32, desc ="") 
    public static final String PASSWORD = "PASSWORD";

    @Column(name="SUB_BILL_ID", type=Types.VARCHAR, length=50, desc ="") 
    public static final String SUB_BILL_ID = "SUB_BILL_ID";

    @Column(name="ACCT_TAG", type=Types.VARCHAR, length=8, desc ="") 
    public static final String ACCT_TAG = "ACCT_TAG";

    @Column(name="MPUTE_TAG", type=Types.VARCHAR, length=8, desc ="") 
    public static final String MPUTE_TAG = "MPUTE_TAG";

    @Column(name="OPEN_MODE", type=Types.VARCHAR, length=8, desc ="") 
    public static final String OPEN_MODE = "OPEN_MODE";

    @Column(name="OPEN_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String OPEN_DATE = "OPEN_DATE";

    @Column(name="FIRST_ACTIVE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String FIRST_ACTIVE_DATE = "FIRST_ACTIVE_DATE";

    @Column(name="REMOVE_TAG", type=Types.VARCHAR, length=8, desc ="") 
    public static final String REMOVE_TAG = "REMOVE_TAG";

    @Column(name="REMOVE_REASON", type=Types.VARCHAR, length=200, desc ="") 
    public static final String REMOVE_REASON = "REMOVE_REASON";

    @Column(name="PRE_DESTORY_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String PRE_DESTORY_DATE = "PRE_DESTORY_DATE";

    @Column(name="DESTORY_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String DESTORY_DATE = "DESTORY_DATE";

    @Column(name="AREA_CODE", type=Types.VARCHAR, length=40, desc ="") 
    public static final String AREA_CODE = "AREA_CODE";

    @Column(name="REMARKS", type=Types.VARCHAR, length=4000, desc ="") 
    public static final String REMARKS = "REMARKS";

    @Column(name="USECUST_ID", type=Types.NUMERIC, length=16, desc ="") 
    public static final String USECUST_ID = "USECUST_ID";

    @Column(name="COUNTY_A", type=Types.VARCHAR, length=4, desc ="") 
    public static final String COUNTY_A = "COUNTY_A";

    @Column(name="SUBSCRIBER_DIFF_CODE", type=Types.VARCHAR, length=2, desc ="") 
    public static final String SUBSCRIBER_DIFF_CODE = "SUBSCRIBER_DIFF_CODE";

    @Column(name="SUBSCRIBER_TAG_SET", type=Types.VARCHAR, length=50, desc ="") 
    public static final String SUBSCRIBER_TAG_SET = "SUBSCRIBER_TAG_SET";

    @Column(name="SUBSCRIBER_STATE_CODESET", type=Types.VARCHAR, length=10, desc ="") 
    public static final String SUBSCRIBER_STATE_CODESET = "SUBSCRIBER_STATE_CODESET";

    @Column(name="NET_TYPE_CODE", type=Types.VARCHAR, length=2, desc ="") 
    public static final String NET_TYPE_CODE = "NET_TYPE_CODE";

    @Column(name="CONTRACT_ID", type=Types.VARCHAR, length=50, desc ="") 
    public static final String CONTRACT_ID = "CONTRACT_ID";

    @Column(name="PREPAY_TAG", type=Types.VARCHAR, length=1, desc ="") 
    public static final String PREPAY_TAG = "PREPAY_TAG";

    @Column(name="MPUTE_MONTH_FEE", type=Types.VARCHAR, length=1, desc ="") 
    public static final String MPUTE_MONTH_FEE = "MPUTE_MONTH_FEE";

    @Column(name="MPUTE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String MPUTE_DATE = "MPUTE_DATE";

    @Column(name="LAST_STOP_TIME", type=Types.DATE, length=0, desc ="") 
    public static final String LAST_STOP_TIME = "LAST_STOP_TIME";

    @Column(name="CHANGEUSER_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String CHANGEUSER_DATE = "CHANGEUSER_DATE";

    @Column(name="IN_NET_MODE", type=Types.VARCHAR, length=3, desc ="") 
    public static final String IN_NET_MODE = "IN_NET_MODE";

    @Column(name="OPEN_OP_ID", type=Types.VARCHAR, length=8, desc ="") 
    public static final String OPEN_OP_ID = "OPEN_OP_ID";

    @Column(name="OPEN_ORG_ID", type=Types.VARCHAR, length=5, desc ="") 
    public static final String OPEN_ORG_ID = "OPEN_ORG_ID";

    @Column(name="DEVELOP_OP_ID", type=Types.VARCHAR, length=100, desc ="") 
    public static final String DEVELOP_OP_ID = "DEVELOP_OP_ID";

    @Column(name="DEVELOP_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String DEVELOP_DATE = "DEVELOP_DATE";

    @Column(name="DEVELOP_ORG_ID", type=Types.VARCHAR, length=5, desc ="") 
    public static final String DEVELOP_ORG_ID = "DEVELOP_ORG_ID";

    @Column(name="DEVELOP_COUNTY", type=Types.VARCHAR, length=4, desc ="") 
    public static final String DEVELOP_COUNTY = "DEVELOP_COUNTY";

    @Column(name="DEVELOP_DISTRICT", type=Types.VARCHAR, length=4, desc ="") 
    public static final String DEVELOP_DISTRICT = "DEVELOP_DISTRICT";

    @Column(name="DEVELOP_NO", type=Types.VARCHAR, length=30, desc ="") 
    public static final String DEVELOP_NO = "DEVELOP_NO";

    @Column(name="ASSURE_CUST_ID", type=Types.NUMERIC, length=16, desc ="") 
    public static final String ASSURE_CUST_ID = "ASSURE_CUST_ID";

    @Column(name="ASSURE_TYPE_CODE", type=Types.VARCHAR, length=1, desc ="") 
    public static final String ASSURE_TYPE_CODE = "ASSURE_TYPE_CODE";

    @Column(name="ASSURE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String ASSURE_DATE = "ASSURE_DATE";

    @Column(name="REMOVE_DISTRICT", type=Types.VARCHAR, length=4, desc ="") 
    public static final String REMOVE_DISTRICT = "REMOVE_DISTRICT";

    @Column(name="REMOVE_COUNTY", type=Types.VARCHAR, length=4, desc ="") 
    public static final String REMOVE_COUNTY = "REMOVE_COUNTY";

    @Column(name="REMOVE_ORG_ID", type=Types.VARCHAR, length=5, desc ="") 
    public static final String REMOVE_ORG_ID = "REMOVE_ORG_ID";

    @Column(name="CREATE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String CREATE_DATE = "CREATE_DATE";

    @Column(name="CREATE_OP_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String CREATE_OP_ID = "CREATE_OP_ID";

    @Column(name="CREATE_ORG_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String CREATE_ORG_ID = "CREATE_ORG_ID";

    @Column(name="DONE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String DONE_DATE = "DONE_DATE";

    @Column(name="OP_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String OP_ID = "OP_ID";

    @Column(name="ORG_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String ORG_ID = "ORG_ID";

    @Column(name="MGMT_DISTRICT", type=Types.VARCHAR, length=6, desc ="") 
    public static final String MGMT_DISTRICT = "MGMT_DISTRICT";

    @Column(name="MGMT_COUNTY", type=Types.VARCHAR, length=6, desc ="") 
    public static final String MGMT_COUNTY = "MGMT_COUNTY";

    @Column(name="REGION_ID", type=Types.VARCHAR, length=6, desc ="") 
    public static final String REGION_ID = "REGION_ID";

    @Column(name="IS_USIM", type=Types.VARCHAR, length=1, desc ="") 
    public static final String IS_USIM = "IS_USIM";

    @Column(name="RSRV_STR1", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR1 = "RSRV_STR1";

    @Column(name="RSRV_STR2", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR2 = "RSRV_STR2";

    @Column(name="RSRV_STR3", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR3 = "RSRV_STR3";

    @Column(name="RSRV_STR4", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR4 = "RSRV_STR4";

    @Column(name="RSRV_STR5", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR5 = "RSRV_STR5";

    @Column(name="RSRV_STR6", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR6 = "RSRV_STR6";

    @Column(name="RSRV_STR7", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR7 = "RSRV_STR7";

    @Column(name="RSRV_STR8", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR8 = "RSRV_STR8";

    @Column(name="RSRV_STR9", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR9 = "RSRV_STR9";

    @Column(name="RSRV_STR10", type=Types.VARCHAR, length=200, desc ="") 
    public static final String RSRV_STR10 = "RSRV_STR10";

    @Column(name="RSRV_NUM1", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM1 = "RSRV_NUM1";

    @Column(name="RSRV_NUM2", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM2 = "RSRV_NUM2";

    @Column(name="RSRV_NUM3", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM3 = "RSRV_NUM3";

    @Column(name="RSRV_NUM4", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM4 = "RSRV_NUM4";

    @Column(name="RSRV_NUM5", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM5 = "RSRV_NUM5";

    @Column(name="RSRV_DATE1", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE1 = "RSRV_DATE1";

    @Column(name="RSRV_DATE2", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE2 = "RSRV_DATE2";

    @Column(name="RSRV_DATE3", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE3 = "RSRV_DATE3";

    @Column(name="RSRV_TAG1", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG1 = "RSRV_TAG1";

    @Column(name="RSRV_TAG2", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG2 = "RSRV_TAG2";

    @Column(name="RSRV_TAG3", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG3 = "RSRV_TAG3";


     /**
     * 获取字段值:UM_SUBSCRIBER.PARTITION_ID<br>
     * 字段描述:
     * @return the partitionId
     * @throws BOException
     */
     public int getPartitionId() throws BOException {
         return DataType.getAsInt(get(PARTITION_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.PARTITION_ID<br>
     * 字段描述:
     */
     public void setPartitionId(int partitionId) {
         set(PARTITION_ID, partitionId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.SUBSCRIBER_INS_ID<br>
     * 字段描述:
     * @return the subscriberInsId
     * @throws BOException
     */
     public long getSubscriberInsId() throws BOException {
         return DataType.getAsLong(get(SUBSCRIBER_INS_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.SUBSCRIBER_INS_ID<br>
     * 字段描述:
     */
     public void setSubscriberInsId(long subscriberInsId) {
         set(SUBSCRIBER_INS_ID, subscriberInsId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.CUST_ID<br>
     * 字段描述:
     * @return the custId
     * @throws BOException
     */
     public long getCustId() throws BOException {
         return DataType.getAsLong(get(CUST_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.CUST_ID<br>
     * 字段描述:
     */
     public void setCustId(long custId) {
         set(CUST_ID, custId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.PROD_LINE_ID<br>
     * 字段描述:
     * @return the prodLineId
     * @throws BOException
     */
     public long getProdLineId() throws BOException {
         return DataType.getAsLong(get(PROD_LINE_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.PROD_LINE_ID<br>
     * 字段描述:
     */
     public void setProdLineId(long prodLineId) {
         set(PROD_LINE_ID, prodLineId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.PROD_LINE_NAME<br>
     * 字段描述:
     * @return the prodLineName
     * @throws BOException
     */
     public String getProdLineName() throws BOException {
         return DataType.getAsString(get(PROD_LINE_NAME));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.PROD_LINE_NAME<br>
     * 字段描述:
     */
     public void setProdLineName(String prodLineName) {
         set(PROD_LINE_NAME, prodLineName);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.SUBSCRIBER_TYPE<br>
     * 字段描述:
     * @return the subscriberType
     * @throws BOException
     */
     public String getSubscriberType() throws BOException {
         return DataType.getAsString(get(SUBSCRIBER_TYPE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.SUBSCRIBER_TYPE<br>
     * 字段描述:
     */
     public void setSubscriberType(String subscriberType) {
         set(SUBSCRIBER_TYPE, subscriberType);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.ACCESS_NUM<br>
     * 字段描述:
     * @return the accessNum
     * @throws BOException
     */
     public String getAccessNum() throws BOException {
         return DataType.getAsString(get(ACCESS_NUM));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.ACCESS_NUM<br>
     * 字段描述:
     */
     public void setAccessNum(String accessNum) {
         set(ACCESS_NUM, accessNum);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.PASSWORD_TYPE<br>
     * 字段描述:
     * @return the passwordType
     * @throws BOException
     */
     public int getPasswordType() throws BOException {
         return DataType.getAsInt(get(PASSWORD_TYPE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.PASSWORD_TYPE<br>
     * 字段描述:
     */
     public void setPasswordType(int passwordType) {
         set(PASSWORD_TYPE, passwordType);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.PASSWORD<br>
     * 字段描述:
     * @return the password
     * @throws BOException
     */
     public String getPassword() throws BOException {
         return DataType.getAsString(get(PASSWORD));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.PASSWORD<br>
     * 字段描述:
     */
     public void setPassword(String password) {
         set(PASSWORD, password);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.SUB_BILL_ID<br>
     * 字段描述:
     * @return the subBillId
     * @throws BOException
     */
     public String getSubBillId() throws BOException {
         return DataType.getAsString(get(SUB_BILL_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.SUB_BILL_ID<br>
     * 字段描述:
     */
     public void setSubBillId(String subBillId) {
         set(SUB_BILL_ID, subBillId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.ACCT_TAG<br>
     * 字段描述:
     * @return the acctTag
     * @throws BOException
     */
     public String getAcctTag() throws BOException {
         return DataType.getAsString(get(ACCT_TAG));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.ACCT_TAG<br>
     * 字段描述:
     */
     public void setAcctTag(String acctTag) {
         set(ACCT_TAG, acctTag);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.MPUTE_TAG<br>
     * 字段描述:
     * @return the mputeTag
     * @throws BOException
     */
     public String getMputeTag() throws BOException {
         return DataType.getAsString(get(MPUTE_TAG));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.MPUTE_TAG<br>
     * 字段描述:
     */
     public void setMputeTag(String mputeTag) {
         set(MPUTE_TAG, mputeTag);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.OPEN_MODE<br>
     * 字段描述:
     * @return the openMode
     * @throws BOException
     */
     public String getOpenMode() throws BOException {
         return DataType.getAsString(get(OPEN_MODE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.OPEN_MODE<br>
     * 字段描述:
     */
     public void setOpenMode(String openMode) {
         set(OPEN_MODE, openMode);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.OPEN_DATE<br>
     * 字段描述:
     * @return the openDate
     * @throws BOException
     */
     public Date getOpenDate() throws BOException {
         return DataType.getAsDate(get(OPEN_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.OPEN_DATE<br>
     * 字段描述:
     */
     public void setOpenDate(Date openDate) {
         set(OPEN_DATE, openDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.FIRST_ACTIVE_DATE<br>
     * 字段描述:
     * @return the firstActiveDate
     * @throws BOException
     */
     public Date getFirstActiveDate() throws BOException {
         return DataType.getAsDate(get(FIRST_ACTIVE_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.FIRST_ACTIVE_DATE<br>
     * 字段描述:
     */
     public void setFirstActiveDate(Date firstActiveDate) {
         set(FIRST_ACTIVE_DATE, firstActiveDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.REMOVE_TAG<br>
     * 字段描述:
     * @return the removeTag
     * @throws BOException
     */
     public String getRemoveTag() throws BOException {
         return DataType.getAsString(get(REMOVE_TAG));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.REMOVE_TAG<br>
     * 字段描述:
     */
     public void setRemoveTag(String removeTag) {
         set(REMOVE_TAG, removeTag);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.REMOVE_REASON<br>
     * 字段描述:
     * @return the removeReason
     * @throws BOException
     */
     public String getRemoveReason() throws BOException {
         return DataType.getAsString(get(REMOVE_REASON));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.REMOVE_REASON<br>
     * 字段描述:
     */
     public void setRemoveReason(String removeReason) {
         set(REMOVE_REASON, removeReason);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.PRE_DESTORY_DATE<br>
     * 字段描述:
     * @return the preDestoryDate
     * @throws BOException
     */
     public Date getPreDestoryDate() throws BOException {
         return DataType.getAsDate(get(PRE_DESTORY_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.PRE_DESTORY_DATE<br>
     * 字段描述:
     */
     public void setPreDestoryDate(Date preDestoryDate) {
         set(PRE_DESTORY_DATE, preDestoryDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DESTORY_DATE<br>
     * 字段描述:
     * @return the destoryDate
     * @throws BOException
     */
     public Date getDestoryDate() throws BOException {
         return DataType.getAsDate(get(DESTORY_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DESTORY_DATE<br>
     * 字段描述:
     */
     public void setDestoryDate(Date destoryDate) {
         set(DESTORY_DATE, destoryDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.AREA_CODE<br>
     * 字段描述:
     * @return the areaCode
     * @throws BOException
     */
     public String getAreaCode() throws BOException {
         return DataType.getAsString(get(AREA_CODE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.AREA_CODE<br>
     * 字段描述:
     */
     public void setAreaCode(String areaCode) {
         set(AREA_CODE, areaCode);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.REMARKS<br>
     * 字段描述:
     * @return the remarks
     * @throws BOException
     */
     public String getRemarks() throws BOException {
         return DataType.getAsString(get(REMARKS));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.REMARKS<br>
     * 字段描述:
     */
     public void setRemarks(String remarks) {
         set(REMARKS, remarks);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.USECUST_ID<br>
     * 字段描述:
     * @return the usecustId
     * @throws BOException
     */
     public long getUsecustId() throws BOException {
         return DataType.getAsLong(get(USECUST_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.USECUST_ID<br>
     * 字段描述:
     */
     public void setUsecustId(long usecustId) {
         set(USECUST_ID, usecustId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.COUNTY_A<br>
     * 字段描述:
     * @return the countyA
     * @throws BOException
     */
     public String getCountyA() throws BOException {
         return DataType.getAsString(get(COUNTY_A));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.COUNTY_A<br>
     * 字段描述:
     */
     public void setCountyA(String countyA) {
         set(COUNTY_A, countyA);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.SUBSCRIBER_DIFF_CODE<br>
     * 字段描述:
     * @return the subscriberDiffCode
     * @throws BOException
     */
     public String getSubscriberDiffCode() throws BOException {
         return DataType.getAsString(get(SUBSCRIBER_DIFF_CODE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.SUBSCRIBER_DIFF_CODE<br>
     * 字段描述:
     */
     public void setSubscriberDiffCode(String subscriberDiffCode) {
         set(SUBSCRIBER_DIFF_CODE, subscriberDiffCode);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.SUBSCRIBER_TAG_SET<br>
     * 字段描述:
     * @return the subscriberTagSet
     * @throws BOException
     */
     public String getSubscriberTagSet() throws BOException {
         return DataType.getAsString(get(SUBSCRIBER_TAG_SET));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.SUBSCRIBER_TAG_SET<br>
     * 字段描述:
     */
     public void setSubscriberTagSet(String subscriberTagSet) {
         set(SUBSCRIBER_TAG_SET, subscriberTagSet);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.SUBSCRIBER_STATE_CODESET<br>
     * 字段描述:
     * @return the subscriberStateCodeset
     * @throws BOException
     */
     public String getSubscriberStateCodeset() throws BOException {
         return DataType.getAsString(get(SUBSCRIBER_STATE_CODESET));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.SUBSCRIBER_STATE_CODESET<br>
     * 字段描述:
     */
     public void setSubscriberStateCodeset(String subscriberStateCodeset) {
         set(SUBSCRIBER_STATE_CODESET, subscriberStateCodeset);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.NET_TYPE_CODE<br>
     * 字段描述:
     * @return the netTypeCode
     * @throws BOException
     */
     public String getNetTypeCode() throws BOException {
         return DataType.getAsString(get(NET_TYPE_CODE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.NET_TYPE_CODE<br>
     * 字段描述:
     */
     public void setNetTypeCode(String netTypeCode) {
         set(NET_TYPE_CODE, netTypeCode);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.CONTRACT_ID<br>
     * 字段描述:
     * @return the contractId
     * @throws BOException
     */
     public String getContractId() throws BOException {
         return DataType.getAsString(get(CONTRACT_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.CONTRACT_ID<br>
     * 字段描述:
     */
     public void setContractId(String contractId) {
         set(CONTRACT_ID, contractId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.PREPAY_TAG<br>
     * 字段描述:
     * @return the prepayTag
     * @throws BOException
     */
     public String getPrepayTag() throws BOException {
         return DataType.getAsString(get(PREPAY_TAG));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.PREPAY_TAG<br>
     * 字段描述:
     */
     public void setPrepayTag(String prepayTag) {
         set(PREPAY_TAG, prepayTag);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.MPUTE_MONTH_FEE<br>
     * 字段描述:
     * @return the mputeMonthFee
     * @throws BOException
     */
     public String getMputeMonthFee() throws BOException {
         return DataType.getAsString(get(MPUTE_MONTH_FEE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.MPUTE_MONTH_FEE<br>
     * 字段描述:
     */
     public void setMputeMonthFee(String mputeMonthFee) {
         set(MPUTE_MONTH_FEE, mputeMonthFee);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.MPUTE_DATE<br>
     * 字段描述:
     * @return the mputeDate
     * @throws BOException
     */
     public Date getMputeDate() throws BOException {
         return DataType.getAsDate(get(MPUTE_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.MPUTE_DATE<br>
     * 字段描述:
     */
     public void setMputeDate(Date mputeDate) {
         set(MPUTE_DATE, mputeDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.LAST_STOP_TIME<br>
     * 字段描述:
     * @return the lastStopTime
     * @throws BOException
     */
     public Date getLastStopTime() throws BOException {
         return DataType.getAsDate(get(LAST_STOP_TIME));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.LAST_STOP_TIME<br>
     * 字段描述:
     */
     public void setLastStopTime(Date lastStopTime) {
         set(LAST_STOP_TIME, lastStopTime);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.CHANGEUSER_DATE<br>
     * 字段描述:
     * @return the changeuserDate
     * @throws BOException
     */
     public Date getChangeuserDate() throws BOException {
         return DataType.getAsDate(get(CHANGEUSER_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.CHANGEUSER_DATE<br>
     * 字段描述:
     */
     public void setChangeuserDate(Date changeuserDate) {
         set(CHANGEUSER_DATE, changeuserDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.IN_NET_MODE<br>
     * 字段描述:
     * @return the inNetMode
     * @throws BOException
     */
     public String getInNetMode() throws BOException {
         return DataType.getAsString(get(IN_NET_MODE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.IN_NET_MODE<br>
     * 字段描述:
     */
     public void setInNetMode(String inNetMode) {
         set(IN_NET_MODE, inNetMode);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.OPEN_OP_ID<br>
     * 字段描述:
     * @return the openOpId
     * @throws BOException
     */
     public String getOpenOpId() throws BOException {
         return DataType.getAsString(get(OPEN_OP_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.OPEN_OP_ID<br>
     * 字段描述:
     */
     public void setOpenOpId(String openOpId) {
         set(OPEN_OP_ID, openOpId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.OPEN_ORG_ID<br>
     * 字段描述:
     * @return the openOrgId
     * @throws BOException
     */
     public String getOpenOrgId() throws BOException {
         return DataType.getAsString(get(OPEN_ORG_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.OPEN_ORG_ID<br>
     * 字段描述:
     */
     public void setOpenOrgId(String openOrgId) {
         set(OPEN_ORG_ID, openOrgId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DEVELOP_OP_ID<br>
     * 字段描述:
     * @return the developOpId
     * @throws BOException
     */
     public String getDevelopOpId() throws BOException {
         return DataType.getAsString(get(DEVELOP_OP_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DEVELOP_OP_ID<br>
     * 字段描述:
     */
     public void setDevelopOpId(String developOpId) {
         set(DEVELOP_OP_ID, developOpId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DEVELOP_DATE<br>
     * 字段描述:
     * @return the developDate
     * @throws BOException
     */
     public Date getDevelopDate() throws BOException {
         return DataType.getAsDate(get(DEVELOP_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DEVELOP_DATE<br>
     * 字段描述:
     */
     public void setDevelopDate(Date developDate) {
         set(DEVELOP_DATE, developDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DEVELOP_ORG_ID<br>
     * 字段描述:
     * @return the developOrgId
     * @throws BOException
     */
     public String getDevelopOrgId() throws BOException {
         return DataType.getAsString(get(DEVELOP_ORG_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DEVELOP_ORG_ID<br>
     * 字段描述:
     */
     public void setDevelopOrgId(String developOrgId) {
         set(DEVELOP_ORG_ID, developOrgId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DEVELOP_COUNTY<br>
     * 字段描述:
     * @return the developCounty
     * @throws BOException
     */
     public String getDevelopCounty() throws BOException {
         return DataType.getAsString(get(DEVELOP_COUNTY));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DEVELOP_COUNTY<br>
     * 字段描述:
     */
     public void setDevelopCounty(String developCounty) {
         set(DEVELOP_COUNTY, developCounty);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DEVELOP_DISTRICT<br>
     * 字段描述:
     * @return the developDistrict
     * @throws BOException
     */
     public String getDevelopDistrict() throws BOException {
         return DataType.getAsString(get(DEVELOP_DISTRICT));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DEVELOP_DISTRICT<br>
     * 字段描述:
     */
     public void setDevelopDistrict(String developDistrict) {
         set(DEVELOP_DISTRICT, developDistrict);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DEVELOP_NO<br>
     * 字段描述:
     * @return the developNo
     * @throws BOException
     */
     public String getDevelopNo() throws BOException {
         return DataType.getAsString(get(DEVELOP_NO));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DEVELOP_NO<br>
     * 字段描述:
     */
     public void setDevelopNo(String developNo) {
         set(DEVELOP_NO, developNo);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.ASSURE_CUST_ID<br>
     * 字段描述:
     * @return the assureCustId
     * @throws BOException
     */
     public long getAssureCustId() throws BOException {
         return DataType.getAsLong(get(ASSURE_CUST_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.ASSURE_CUST_ID<br>
     * 字段描述:
     */
     public void setAssureCustId(long assureCustId) {
         set(ASSURE_CUST_ID, assureCustId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.ASSURE_TYPE_CODE<br>
     * 字段描述:
     * @return the assureTypeCode
     * @throws BOException
     */
     public String getAssureTypeCode() throws BOException {
         return DataType.getAsString(get(ASSURE_TYPE_CODE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.ASSURE_TYPE_CODE<br>
     * 字段描述:
     */
     public void setAssureTypeCode(String assureTypeCode) {
         set(ASSURE_TYPE_CODE, assureTypeCode);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.ASSURE_DATE<br>
     * 字段描述:
     * @return the assureDate
     * @throws BOException
     */
     public Date getAssureDate() throws BOException {
         return DataType.getAsDate(get(ASSURE_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.ASSURE_DATE<br>
     * 字段描述:
     */
     public void setAssureDate(Date assureDate) {
         set(ASSURE_DATE, assureDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.REMOVE_DISTRICT<br>
     * 字段描述:
     * @return the removeDistrict
     * @throws BOException
     */
     public String getRemoveDistrict() throws BOException {
         return DataType.getAsString(get(REMOVE_DISTRICT));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.REMOVE_DISTRICT<br>
     * 字段描述:
     */
     public void setRemoveDistrict(String removeDistrict) {
         set(REMOVE_DISTRICT, removeDistrict);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.REMOVE_COUNTY<br>
     * 字段描述:
     * @return the removeCounty
     * @throws BOException
     */
     public String getRemoveCounty() throws BOException {
         return DataType.getAsString(get(REMOVE_COUNTY));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.REMOVE_COUNTY<br>
     * 字段描述:
     */
     public void setRemoveCounty(String removeCounty) {
         set(REMOVE_COUNTY, removeCounty);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.REMOVE_ORG_ID<br>
     * 字段描述:
     * @return the removeOrgId
     * @throws BOException
     */
     public String getRemoveOrgId() throws BOException {
         return DataType.getAsString(get(REMOVE_ORG_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.REMOVE_ORG_ID<br>
     * 字段描述:
     */
     public void setRemoveOrgId(String removeOrgId) {
         set(REMOVE_ORG_ID, removeOrgId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.CREATE_DATE<br>
     * 字段描述:
     * @return the createDate
     * @throws BOException
     */
     public Date getCreateDate() throws BOException {
         return DataType.getAsDate(get(CREATE_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.CREATE_DATE<br>
     * 字段描述:
     */
     public void setCreateDate(Date createDate) {
         set(CREATE_DATE, createDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.CREATE_OP_ID<br>
     * 字段描述:
     * @return the createOpId
     * @throws BOException
     */
     public String getCreateOpId() throws BOException {
         return DataType.getAsString(get(CREATE_OP_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.CREATE_OP_ID<br>
     * 字段描述:
     */
     public void setCreateOpId(String createOpId) {
         set(CREATE_OP_ID, createOpId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.CREATE_ORG_ID<br>
     * 字段描述:
     * @return the createOrgId
     * @throws BOException
     */
     public String getCreateOrgId() throws BOException {
         return DataType.getAsString(get(CREATE_ORG_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.CREATE_ORG_ID<br>
     * 字段描述:
     */
     public void setCreateOrgId(String createOrgId) {
         set(CREATE_ORG_ID, createOrgId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.DONE_DATE<br>
     * 字段描述:
     * @return the doneDate
     * @throws BOException
     */
     public Date getDoneDate() throws BOException {
         return DataType.getAsDate(get(DONE_DATE));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.DONE_DATE<br>
     * 字段描述:
     */
     public void setDoneDate(Date doneDate) {
         set(DONE_DATE, doneDate);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.OP_ID<br>
     * 字段描述:
     * @return the opId
     * @throws BOException
     */
     public String getOpId() throws BOException {
         return DataType.getAsString(get(OP_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.OP_ID<br>
     * 字段描述:
     */
     public void setOpId(String opId) {
         set(OP_ID, opId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.ORG_ID<br>
     * 字段描述:
     * @return the orgId
     * @throws BOException
     */
     public String getOrgId() throws BOException {
         return DataType.getAsString(get(ORG_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.ORG_ID<br>
     * 字段描述:
     */
     public void setOrgId(String orgId) {
         set(ORG_ID, orgId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.MGMT_DISTRICT<br>
     * 字段描述:
     * @return the mgmtDistrict
     * @throws BOException
     */
     public String getMgmtDistrict() throws BOException {
         return DataType.getAsString(get(MGMT_DISTRICT));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.MGMT_DISTRICT<br>
     * 字段描述:
     */
     public void setMgmtDistrict(String mgmtDistrict) {
         set(MGMT_DISTRICT, mgmtDistrict);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.MGMT_COUNTY<br>
     * 字段描述:
     * @return the mgmtCounty
     * @throws BOException
     */
     public String getMgmtCounty() throws BOException {
         return DataType.getAsString(get(MGMT_COUNTY));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.MGMT_COUNTY<br>
     * 字段描述:
     */
     public void setMgmtCounty(String mgmtCounty) {
         set(MGMT_COUNTY, mgmtCounty);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.REGION_ID<br>
     * 字段描述:
     * @return the regionId
     * @throws BOException
     */
     public String getRegionId() throws BOException {
         return DataType.getAsString(get(REGION_ID));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.REGION_ID<br>
     * 字段描述:
     */
     public void setRegionId(String regionId) {
         set(REGION_ID, regionId);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.IS_USIM<br>
     * 字段描述:
     * @return the isUsim
     * @throws BOException
     */
     public String getIsUsim() throws BOException {
         return DataType.getAsString(get(IS_USIM));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.IS_USIM<br>
     * 字段描述:
     */
     public void setIsUsim(String isUsim) {
         set(IS_USIM, isUsim);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR1<br>
     * 字段描述:
     * @return the rsrvStr1
     * @throws BOException
     */
     public String getRsrvStr1() throws BOException {
         return DataType.getAsString(get(RSRV_STR1));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR1<br>
     * 字段描述:
     */
     public void setRsrvStr1(String rsrvStr1) {
         set(RSRV_STR1, rsrvStr1);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR2<br>
     * 字段描述:
     * @return the rsrvStr2
     * @throws BOException
     */
     public String getRsrvStr2() throws BOException {
         return DataType.getAsString(get(RSRV_STR2));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR2<br>
     * 字段描述:
     */
     public void setRsrvStr2(String rsrvStr2) {
         set(RSRV_STR2, rsrvStr2);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR3<br>
     * 字段描述:
     * @return the rsrvStr3
     * @throws BOException
     */
     public String getRsrvStr3() throws BOException {
         return DataType.getAsString(get(RSRV_STR3));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR3<br>
     * 字段描述:
     */
     public void setRsrvStr3(String rsrvStr3) {
         set(RSRV_STR3, rsrvStr3);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR4<br>
     * 字段描述:
     * @return the rsrvStr4
     * @throws BOException
     */
     public String getRsrvStr4() throws BOException {
         return DataType.getAsString(get(RSRV_STR4));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR4<br>
     * 字段描述:
     */
     public void setRsrvStr4(String rsrvStr4) {
         set(RSRV_STR4, rsrvStr4);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR5<br>
     * 字段描述:
     * @return the rsrvStr5
     * @throws BOException
     */
     public String getRsrvStr5() throws BOException {
         return DataType.getAsString(get(RSRV_STR5));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR5<br>
     * 字段描述:
     */
     public void setRsrvStr5(String rsrvStr5) {
         set(RSRV_STR5, rsrvStr5);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR6<br>
     * 字段描述:
     * @return the rsrvStr6
     * @throws BOException
     */
     public String getRsrvStr6() throws BOException {
         return DataType.getAsString(get(RSRV_STR6));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR6<br>
     * 字段描述:
     */
     public void setRsrvStr6(String rsrvStr6) {
         set(RSRV_STR6, rsrvStr6);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR7<br>
     * 字段描述:
     * @return the rsrvStr7
     * @throws BOException
     */
     public String getRsrvStr7() throws BOException {
         return DataType.getAsString(get(RSRV_STR7));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR7<br>
     * 字段描述:
     */
     public void setRsrvStr7(String rsrvStr7) {
         set(RSRV_STR7, rsrvStr7);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR8<br>
     * 字段描述:
     * @return the rsrvStr8
     * @throws BOException
     */
     public String getRsrvStr8() throws BOException {
         return DataType.getAsString(get(RSRV_STR8));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR8<br>
     * 字段描述:
     */
     public void setRsrvStr8(String rsrvStr8) {
         set(RSRV_STR8, rsrvStr8);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR9<br>
     * 字段描述:
     * @return the rsrvStr9
     * @throws BOException
     */
     public String getRsrvStr9() throws BOException {
         return DataType.getAsString(get(RSRV_STR9));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR9<br>
     * 字段描述:
     */
     public void setRsrvStr9(String rsrvStr9) {
         set(RSRV_STR9, rsrvStr9);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_STR10<br>
     * 字段描述:
     * @return the rsrvStr10
     * @throws BOException
     */
     public String getRsrvStr10() throws BOException {
         return DataType.getAsString(get(RSRV_STR10));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_STR10<br>
     * 字段描述:
     */
     public void setRsrvStr10(String rsrvStr10) {
         set(RSRV_STR10, rsrvStr10);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_NUM1<br>
     * 字段描述:
     * @return the rsrvNum1
     * @throws BOException
     */
     public long getRsrvNum1() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM1));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_NUM1<br>
     * 字段描述:
     */
     public void setRsrvNum1(long rsrvNum1) {
         set(RSRV_NUM1, rsrvNum1);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_NUM2<br>
     * 字段描述:
     * @return the rsrvNum2
     * @throws BOException
     */
     public long getRsrvNum2() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM2));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_NUM2<br>
     * 字段描述:
     */
     public void setRsrvNum2(long rsrvNum2) {
         set(RSRV_NUM2, rsrvNum2);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_NUM3<br>
     * 字段描述:
     * @return the rsrvNum3
     * @throws BOException
     */
     public long getRsrvNum3() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM3));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_NUM3<br>
     * 字段描述:
     */
     public void setRsrvNum3(long rsrvNum3) {
         set(RSRV_NUM3, rsrvNum3);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_NUM4<br>
     * 字段描述:
     * @return the rsrvNum4
     * @throws BOException
     */
     public long getRsrvNum4() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM4));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_NUM4<br>
     * 字段描述:
     */
     public void setRsrvNum4(long rsrvNum4) {
         set(RSRV_NUM4, rsrvNum4);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_NUM5<br>
     * 字段描述:
     * @return the rsrvNum5
     * @throws BOException
     */
     public long getRsrvNum5() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM5));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_NUM5<br>
     * 字段描述:
     */
     public void setRsrvNum5(long rsrvNum5) {
         set(RSRV_NUM5, rsrvNum5);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_DATE1<br>
     * 字段描述:
     * @return the rsrvDate1
     * @throws BOException
     */
     public Date getRsrvDate1() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE1));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_DATE1<br>
     * 字段描述:
     */
     public void setRsrvDate1(Date rsrvDate1) {
         set(RSRV_DATE1, rsrvDate1);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_DATE2<br>
     * 字段描述:
     * @return the rsrvDate2
     * @throws BOException
     */
     public Date getRsrvDate2() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE2));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_DATE2<br>
     * 字段描述:
     */
     public void setRsrvDate2(Date rsrvDate2) {
         set(RSRV_DATE2, rsrvDate2);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_DATE3<br>
     * 字段描述:
     * @return the rsrvDate3
     * @throws BOException
     */
     public Date getRsrvDate3() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE3));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_DATE3<br>
     * 字段描述:
     */
     public void setRsrvDate3(Date rsrvDate3) {
         set(RSRV_DATE3, rsrvDate3);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_TAG1<br>
     * 字段描述:
     * @return the rsrvTag1
     * @throws BOException
     */
     public String getRsrvTag1() throws BOException {
         return DataType.getAsString(get(RSRV_TAG1));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_TAG1<br>
     * 字段描述:
     */
     public void setRsrvTag1(String rsrvTag1) {
         set(RSRV_TAG1, rsrvTag1);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_TAG2<br>
     * 字段描述:
     * @return the rsrvTag2
     * @throws BOException
     */
     public String getRsrvTag2() throws BOException {
         return DataType.getAsString(get(RSRV_TAG2));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_TAG2<br>
     * 字段描述:
     */
     public void setRsrvTag2(String rsrvTag2) {
         set(RSRV_TAG2, rsrvTag2);
     }

     /**
     * 获取字段值:UM_SUBSCRIBER.RSRV_TAG3<br>
     * 字段描述:
     * @return the rsrvTag3
     * @throws BOException
     */
     public String getRsrvTag3() throws BOException {
         return DataType.getAsString(get(RSRV_TAG3));
     }

     /**
     * 设置字段值:UM_SUBSCRIBER.RSRV_TAG3<br>
     * 字段描述:
     */
     public void setRsrvTag3(String rsrvTag3) {
         set(RSRV_TAG3, rsrvTag3);
     }

}
