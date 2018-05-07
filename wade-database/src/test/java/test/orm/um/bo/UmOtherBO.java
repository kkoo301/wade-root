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

@Table(name = "UM_OTHER", primary = "PARTITION_ID,SUBSCRIBER_INS_ID")
public class UmOtherBO extends BOContainer {

    private static final long serialVersionUID = 1L;


    public UmOtherBO() {
        super();
    }

    public UmOtherBO(Map<String, Object> properties) {
        super(properties);
    }

    @Column(name="OTHER_INS_ID", type=Types.NUMERIC, length=16, desc ="") 
    public static final String OTHER_INS_ID = "OTHER_INS_ID";

    @Column(name="PARTITION_ID", type=Types.NUMERIC, length=4, desc ="") 
    public static final String PARTITION_ID = "PARTITION_ID";

    @Column(name="SUBSCRIBER_INS_ID", type=Types.NUMERIC, length=16, desc ="") 
    public static final String SUBSCRIBER_INS_ID = "SUBSCRIBER_INS_ID";

    @Column(name="RSRV_VALUE_CODE", type=Types.VARCHAR, length=20, desc ="") 
    public static final String RSRV_VALUE_CODE = "RSRV_VALUE_CODE";

    @Column(name="RSRV_VALUE", type=Types.VARCHAR, length=50, desc ="") 
    public static final String RSRV_VALUE = "RSRV_VALUE";

    @Column(name="RSRV_NUM1", type=Types.NUMERIC, length=8, desc ="") 
    public static final String RSRV_NUM1 = "RSRV_NUM1";

    @Column(name="RSRV_NUM2", type=Types.NUMERIC, length=8, desc ="") 
    public static final String RSRV_NUM2 = "RSRV_NUM2";

    @Column(name="RSRV_NUM3", type=Types.NUMERIC, length=8, desc ="") 
    public static final String RSRV_NUM3 = "RSRV_NUM3";

    @Column(name="RSRV_NUM4", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM4 = "RSRV_NUM4";

    @Column(name="RSRV_NUM5", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM5 = "RSRV_NUM5";

    @Column(name="RSRV_NUM6", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM6 = "RSRV_NUM6";

    @Column(name="RSRV_NUM7", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM7 = "RSRV_NUM7";

    @Column(name="RSRV_NUM8", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM8 = "RSRV_NUM8";

    @Column(name="RSRV_NUM9", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM9 = "RSRV_NUM9";

    @Column(name="RSRV_NUM10", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM10 = "RSRV_NUM10";

    @Column(name="RSRV_NUM11", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM11 = "RSRV_NUM11";

    @Column(name="RSRV_NUM12", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM12 = "RSRV_NUM12";

    @Column(name="RSRV_NUM13", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM13 = "RSRV_NUM13";

    @Column(name="RSRV_NUM14", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM14 = "RSRV_NUM14";

    @Column(name="RSRV_NUM15", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM15 = "RSRV_NUM15";

    @Column(name="RSRV_NUM16", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM16 = "RSRV_NUM16";

    @Column(name="RSRV_NUM17", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM17 = "RSRV_NUM17";

    @Column(name="RSRV_NUM18", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM18 = "RSRV_NUM18";

    @Column(name="RSRV_NUM19", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM19 = "RSRV_NUM19";

    @Column(name="RSRV_NUM20", type=Types.NUMERIC, length=12, desc ="") 
    public static final String RSRV_NUM20 = "RSRV_NUM20";

    @Column(name="RSRV_STR1", type=Types.VARCHAR, length=100, desc ="") 
    public static final String RSRV_STR1 = "RSRV_STR1";

    @Column(name="RSRV_STR2", type=Types.VARCHAR, length=100, desc ="") 
    public static final String RSRV_STR2 = "RSRV_STR2";

    @Column(name="RSRV_STR3", type=Types.VARCHAR, length=100, desc ="") 
    public static final String RSRV_STR3 = "RSRV_STR3";

    @Column(name="RSRV_STR4", type=Types.VARCHAR, length=100, desc ="") 
    public static final String RSRV_STR4 = "RSRV_STR4";

    @Column(name="RSRV_STR5", type=Types.VARCHAR, length=100, desc ="") 
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

    @Column(name="RSRV_STR11", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR11 = "RSRV_STR11";

    @Column(name="RSRV_STR12", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR12 = "RSRV_STR12";

    @Column(name="RSRV_STR13", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR13 = "RSRV_STR13";

    @Column(name="RSRV_STR14", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR14 = "RSRV_STR14";

    @Column(name="RSRV_STR15", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR15 = "RSRV_STR15";

    @Column(name="RSRV_STR16", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR16 = "RSRV_STR16";

    @Column(name="RSRV_STR17", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR17 = "RSRV_STR17";

    @Column(name="RSRV_STR18", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR18 = "RSRV_STR18";

    @Column(name="RSRV_STR19", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR19 = "RSRV_STR19";

    @Column(name="RSRV_STR20", type=Types.VARCHAR, length=300, desc ="") 
    public static final String RSRV_STR20 = "RSRV_STR20";

    @Column(name="RSRV_STR21", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR21 = "RSRV_STR21";

    @Column(name="RSRV_STR22", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR22 = "RSRV_STR22";

    @Column(name="RSRV_STR23", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR23 = "RSRV_STR23";

    @Column(name="RSRV_STR24", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR24 = "RSRV_STR24";

    @Column(name="RSRV_STR25", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR25 = "RSRV_STR25";

    @Column(name="RSRV_STR26", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR26 = "RSRV_STR26";

    @Column(name="RSRV_STR27", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR27 = "RSRV_STR27";

    @Column(name="RSRV_STR28", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR28 = "RSRV_STR28";

    @Column(name="RSRV_STR29", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR29 = "RSRV_STR29";

    @Column(name="RSRV_STR30", type=Types.VARCHAR, length=500, desc ="") 
    public static final String RSRV_STR30 = "RSRV_STR30";

    @Column(name="RSRV_DATE1", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE1 = "RSRV_DATE1";

    @Column(name="RSRV_DATE2", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE2 = "RSRV_DATE2";

    @Column(name="RSRV_DATE3", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE3 = "RSRV_DATE3";

    @Column(name="RSRV_DATE4", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE4 = "RSRV_DATE4";

    @Column(name="RSRV_DATE5", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE5 = "RSRV_DATE5";

    @Column(name="RSRV_DATE6", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE6 = "RSRV_DATE6";

    @Column(name="RSRV_DATE7", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE7 = "RSRV_DATE7";

    @Column(name="RSRV_DATE8", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE8 = "RSRV_DATE8";

    @Column(name="RSRV_DATE9", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE9 = "RSRV_DATE9";

    @Column(name="RSRV_DATE10", type=Types.DATE, length=0, desc ="") 
    public static final String RSRV_DATE10 = "RSRV_DATE10";

    @Column(name="RSRV_TAG1", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG1 = "RSRV_TAG1";

    @Column(name="RSRV_TAG2", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG2 = "RSRV_TAG2";

    @Column(name="RSRV_TAG3", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG3 = "RSRV_TAG3";

    @Column(name="RSRV_TAG4", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG4 = "RSRV_TAG4";

    @Column(name="RSRV_TAG5", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG5 = "RSRV_TAG5";

    @Column(name="RSRV_TAG6", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG6 = "RSRV_TAG6";

    @Column(name="RSRV_TAG7", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG7 = "RSRV_TAG7";

    @Column(name="RSRV_TAG8", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG8 = "RSRV_TAG8";

    @Column(name="RSRV_TAG9", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG9 = "RSRV_TAG9";

    @Column(name="RSRV_TAG10", type=Types.VARCHAR, length=1, desc ="") 
    public static final String RSRV_TAG10 = "RSRV_TAG10";

    @Column(name="PROCESS_TAG", type=Types.VARCHAR, length=1, desc ="") 
    public static final String PROCESS_TAG = "PROCESS_TAG";

    @Column(name="VALID_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String VALID_DATE = "VALID_DATE";

    @Column(name="EXPIRE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String EXPIRE_DATE = "EXPIRE_DATE";

    @Column(name="DONE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String DONE_DATE = "DONE_DATE";

    @Column(name="OP_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String OP_ID = "OP_ID";

    @Column(name="ORG_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String ORG_ID = "ORG_ID";

    @Column(name="REMARKS", type=Types.VARCHAR, length=1000, desc ="") 
    public static final String REMARKS = "REMARKS";

    @Column(name="OPEN_CODE", type=Types.VARCHAR, length=2, desc ="") 
    public static final String OPEN_CODE = "OPEN_CODE";

    @Column(name="IS_NEED_PF", type=Types.VARCHAR, length=1, desc ="") 
    public static final String IS_NEED_PF = "IS_NEED_PF";

    @Column(name="CREATE_DATE", type=Types.DATE, length=0, desc ="") 
    public static final String CREATE_DATE = "CREATE_DATE";

    @Column(name="CREATE_OP_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String CREATE_OP_ID = "CREATE_OP_ID";

    @Column(name="CREATE_ORG_ID", type=Types.VARCHAR, length=12, desc ="") 
    public static final String CREATE_ORG_ID = "CREATE_ORG_ID";

    @Column(name="DONE_CODE", type=Types.NUMERIC, length=12, desc ="") 
    public static final String DONE_CODE = "DONE_CODE";

    @Column(name="REGION_ID", type=Types.VARCHAR, length=6, desc ="") 
    public static final String REGION_ID = "REGION_ID";


     /**
     * 获取字段值:UM_OTHER.OTHER_INS_ID<br>
     * 字段描述:
     * @return the otherInsId
     * @throws BOException
     */
     public long getOtherInsId() throws BOException {
         return DataType.getAsLong(get(OTHER_INS_ID));
     }

     /**
     * 设置字段值:UM_OTHER.OTHER_INS_ID<br>
     * 字段描述:
     */
     public void setOtherInsId(long otherInsId) {
         set(OTHER_INS_ID, otherInsId);
     }

     /**
     * 获取字段值:UM_OTHER.PARTITION_ID<br>
     * 字段描述:
     * @return the partitionId
     * @throws BOException
     */
     public int getPartitionId() throws BOException {
         return DataType.getAsInt(get(PARTITION_ID));
     }

     /**
     * 设置字段值:UM_OTHER.PARTITION_ID<br>
     * 字段描述:
     */
     public void setPartitionId(int partitionId) {
         set(PARTITION_ID, partitionId);
     }

     /**
     * 获取字段值:UM_OTHER.SUBSCRIBER_INS_ID<br>
     * 字段描述:
     * @return the subscriberInsId
     * @throws BOException
     */
     public long getSubscriberInsId() throws BOException {
         return DataType.getAsLong(get(SUBSCRIBER_INS_ID));
     }

     /**
     * 设置字段值:UM_OTHER.SUBSCRIBER_INS_ID<br>
     * 字段描述:
     */
     public void setSubscriberInsId(long subscriberInsId) {
         set(SUBSCRIBER_INS_ID, subscriberInsId);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_VALUE_CODE<br>
     * 字段描述:
     * @return the rsrvValueCode
     * @throws BOException
     */
     public String getRsrvValueCode() throws BOException {
         return DataType.getAsString(get(RSRV_VALUE_CODE));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_VALUE_CODE<br>
     * 字段描述:
     */
     public void setRsrvValueCode(String rsrvValueCode) {
         set(RSRV_VALUE_CODE, rsrvValueCode);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_VALUE<br>
     * 字段描述:
     * @return the rsrvValue
     * @throws BOException
     */
     public String getRsrvValue() throws BOException {
         return DataType.getAsString(get(RSRV_VALUE));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_VALUE<br>
     * 字段描述:
     */
     public void setRsrvValue(String rsrvValue) {
         set(RSRV_VALUE, rsrvValue);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM1<br>
     * 字段描述:
     * @return the rsrvNum1
     * @throws BOException
     */
     public int getRsrvNum1() throws BOException {
         return DataType.getAsInt(get(RSRV_NUM1));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM1<br>
     * 字段描述:
     */
     public void setRsrvNum1(int rsrvNum1) {
         set(RSRV_NUM1, rsrvNum1);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM2<br>
     * 字段描述:
     * @return the rsrvNum2
     * @throws BOException
     */
     public int getRsrvNum2() throws BOException {
         return DataType.getAsInt(get(RSRV_NUM2));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM2<br>
     * 字段描述:
     */
     public void setRsrvNum2(int rsrvNum2) {
         set(RSRV_NUM2, rsrvNum2);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM3<br>
     * 字段描述:
     * @return the rsrvNum3
     * @throws BOException
     */
     public int getRsrvNum3() throws BOException {
         return DataType.getAsInt(get(RSRV_NUM3));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM3<br>
     * 字段描述:
     */
     public void setRsrvNum3(int rsrvNum3) {
         set(RSRV_NUM3, rsrvNum3);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM4<br>
     * 字段描述:
     * @return the rsrvNum4
     * @throws BOException
     */
     public long getRsrvNum4() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM4));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM4<br>
     * 字段描述:
     */
     public void setRsrvNum4(long rsrvNum4) {
         set(RSRV_NUM4, rsrvNum4);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM5<br>
     * 字段描述:
     * @return the rsrvNum5
     * @throws BOException
     */
     public long getRsrvNum5() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM5));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM5<br>
     * 字段描述:
     */
     public void setRsrvNum5(long rsrvNum5) {
         set(RSRV_NUM5, rsrvNum5);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM6<br>
     * 字段描述:
     * @return the rsrvNum6
     * @throws BOException
     */
     public long getRsrvNum6() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM6));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM6<br>
     * 字段描述:
     */
     public void setRsrvNum6(long rsrvNum6) {
         set(RSRV_NUM6, rsrvNum6);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM7<br>
     * 字段描述:
     * @return the rsrvNum7
     * @throws BOException
     */
     public long getRsrvNum7() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM7));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM7<br>
     * 字段描述:
     */
     public void setRsrvNum7(long rsrvNum7) {
         set(RSRV_NUM7, rsrvNum7);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM8<br>
     * 字段描述:
     * @return the rsrvNum8
     * @throws BOException
     */
     public long getRsrvNum8() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM8));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM8<br>
     * 字段描述:
     */
     public void setRsrvNum8(long rsrvNum8) {
         set(RSRV_NUM8, rsrvNum8);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM9<br>
     * 字段描述:
     * @return the rsrvNum9
     * @throws BOException
     */
     public long getRsrvNum9() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM9));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM9<br>
     * 字段描述:
     */
     public void setRsrvNum9(long rsrvNum9) {
         set(RSRV_NUM9, rsrvNum9);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM10<br>
     * 字段描述:
     * @return the rsrvNum10
     * @throws BOException
     */
     public long getRsrvNum10() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM10));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM10<br>
     * 字段描述:
     */
     public void setRsrvNum10(long rsrvNum10) {
         set(RSRV_NUM10, rsrvNum10);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM11<br>
     * 字段描述:
     * @return the rsrvNum11
     * @throws BOException
     */
     public long getRsrvNum11() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM11));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM11<br>
     * 字段描述:
     */
     public void setRsrvNum11(long rsrvNum11) {
         set(RSRV_NUM11, rsrvNum11);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM12<br>
     * 字段描述:
     * @return the rsrvNum12
     * @throws BOException
     */
     public long getRsrvNum12() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM12));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM12<br>
     * 字段描述:
     */
     public void setRsrvNum12(long rsrvNum12) {
         set(RSRV_NUM12, rsrvNum12);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM13<br>
     * 字段描述:
     * @return the rsrvNum13
     * @throws BOException
     */
     public long getRsrvNum13() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM13));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM13<br>
     * 字段描述:
     */
     public void setRsrvNum13(long rsrvNum13) {
         set(RSRV_NUM13, rsrvNum13);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM14<br>
     * 字段描述:
     * @return the rsrvNum14
     * @throws BOException
     */
     public long getRsrvNum14() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM14));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM14<br>
     * 字段描述:
     */
     public void setRsrvNum14(long rsrvNum14) {
         set(RSRV_NUM14, rsrvNum14);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM15<br>
     * 字段描述:
     * @return the rsrvNum15
     * @throws BOException
     */
     public long getRsrvNum15() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM15));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM15<br>
     * 字段描述:
     */
     public void setRsrvNum15(long rsrvNum15) {
         set(RSRV_NUM15, rsrvNum15);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM16<br>
     * 字段描述:
     * @return the rsrvNum16
     * @throws BOException
     */
     public long getRsrvNum16() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM16));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM16<br>
     * 字段描述:
     */
     public void setRsrvNum16(long rsrvNum16) {
         set(RSRV_NUM16, rsrvNum16);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM17<br>
     * 字段描述:
     * @return the rsrvNum17
     * @throws BOException
     */
     public long getRsrvNum17() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM17));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM17<br>
     * 字段描述:
     */
     public void setRsrvNum17(long rsrvNum17) {
         set(RSRV_NUM17, rsrvNum17);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM18<br>
     * 字段描述:
     * @return the rsrvNum18
     * @throws BOException
     */
     public long getRsrvNum18() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM18));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM18<br>
     * 字段描述:
     */
     public void setRsrvNum18(long rsrvNum18) {
         set(RSRV_NUM18, rsrvNum18);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM19<br>
     * 字段描述:
     * @return the rsrvNum19
     * @throws BOException
     */
     public long getRsrvNum19() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM19));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM19<br>
     * 字段描述:
     */
     public void setRsrvNum19(long rsrvNum19) {
         set(RSRV_NUM19, rsrvNum19);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_NUM20<br>
     * 字段描述:
     * @return the rsrvNum20
     * @throws BOException
     */
     public long getRsrvNum20() throws BOException {
         return DataType.getAsLong(get(RSRV_NUM20));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_NUM20<br>
     * 字段描述:
     */
     public void setRsrvNum20(long rsrvNum20) {
         set(RSRV_NUM20, rsrvNum20);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR1<br>
     * 字段描述:
     * @return the rsrvStr1
     * @throws BOException
     */
     public String getRsrvStr1() throws BOException {
         return DataType.getAsString(get(RSRV_STR1));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR1<br>
     * 字段描述:
     */
     public void setRsrvStr1(String rsrvStr1) {
         set(RSRV_STR1, rsrvStr1);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR2<br>
     * 字段描述:
     * @return the rsrvStr2
     * @throws BOException
     */
     public String getRsrvStr2() throws BOException {
         return DataType.getAsString(get(RSRV_STR2));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR2<br>
     * 字段描述:
     */
     public void setRsrvStr2(String rsrvStr2) {
         set(RSRV_STR2, rsrvStr2);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR3<br>
     * 字段描述:
     * @return the rsrvStr3
     * @throws BOException
     */
     public String getRsrvStr3() throws BOException {
         return DataType.getAsString(get(RSRV_STR3));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR3<br>
     * 字段描述:
     */
     public void setRsrvStr3(String rsrvStr3) {
         set(RSRV_STR3, rsrvStr3);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR4<br>
     * 字段描述:
     * @return the rsrvStr4
     * @throws BOException
     */
     public String getRsrvStr4() throws BOException {
         return DataType.getAsString(get(RSRV_STR4));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR4<br>
     * 字段描述:
     */
     public void setRsrvStr4(String rsrvStr4) {
         set(RSRV_STR4, rsrvStr4);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR5<br>
     * 字段描述:
     * @return the rsrvStr5
     * @throws BOException
     */
     public String getRsrvStr5() throws BOException {
         return DataType.getAsString(get(RSRV_STR5));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR5<br>
     * 字段描述:
     */
     public void setRsrvStr5(String rsrvStr5) {
         set(RSRV_STR5, rsrvStr5);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR6<br>
     * 字段描述:
     * @return the rsrvStr6
     * @throws BOException
     */
     public String getRsrvStr6() throws BOException {
         return DataType.getAsString(get(RSRV_STR6));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR6<br>
     * 字段描述:
     */
     public void setRsrvStr6(String rsrvStr6) {
         set(RSRV_STR6, rsrvStr6);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR7<br>
     * 字段描述:
     * @return the rsrvStr7
     * @throws BOException
     */
     public String getRsrvStr7() throws BOException {
         return DataType.getAsString(get(RSRV_STR7));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR7<br>
     * 字段描述:
     */
     public void setRsrvStr7(String rsrvStr7) {
         set(RSRV_STR7, rsrvStr7);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR8<br>
     * 字段描述:
     * @return the rsrvStr8
     * @throws BOException
     */
     public String getRsrvStr8() throws BOException {
         return DataType.getAsString(get(RSRV_STR8));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR8<br>
     * 字段描述:
     */
     public void setRsrvStr8(String rsrvStr8) {
         set(RSRV_STR8, rsrvStr8);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR9<br>
     * 字段描述:
     * @return the rsrvStr9
     * @throws BOException
     */
     public String getRsrvStr9() throws BOException {
         return DataType.getAsString(get(RSRV_STR9));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR9<br>
     * 字段描述:
     */
     public void setRsrvStr9(String rsrvStr9) {
         set(RSRV_STR9, rsrvStr9);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR10<br>
     * 字段描述:
     * @return the rsrvStr10
     * @throws BOException
     */
     public String getRsrvStr10() throws BOException {
         return DataType.getAsString(get(RSRV_STR10));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR10<br>
     * 字段描述:
     */
     public void setRsrvStr10(String rsrvStr10) {
         set(RSRV_STR10, rsrvStr10);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR11<br>
     * 字段描述:
     * @return the rsrvStr11
     * @throws BOException
     */
     public String getRsrvStr11() throws BOException {
         return DataType.getAsString(get(RSRV_STR11));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR11<br>
     * 字段描述:
     */
     public void setRsrvStr11(String rsrvStr11) {
         set(RSRV_STR11, rsrvStr11);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR12<br>
     * 字段描述:
     * @return the rsrvStr12
     * @throws BOException
     */
     public String getRsrvStr12() throws BOException {
         return DataType.getAsString(get(RSRV_STR12));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR12<br>
     * 字段描述:
     */
     public void setRsrvStr12(String rsrvStr12) {
         set(RSRV_STR12, rsrvStr12);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR13<br>
     * 字段描述:
     * @return the rsrvStr13
     * @throws BOException
     */
     public String getRsrvStr13() throws BOException {
         return DataType.getAsString(get(RSRV_STR13));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR13<br>
     * 字段描述:
     */
     public void setRsrvStr13(String rsrvStr13) {
         set(RSRV_STR13, rsrvStr13);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR14<br>
     * 字段描述:
     * @return the rsrvStr14
     * @throws BOException
     */
     public String getRsrvStr14() throws BOException {
         return DataType.getAsString(get(RSRV_STR14));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR14<br>
     * 字段描述:
     */
     public void setRsrvStr14(String rsrvStr14) {
         set(RSRV_STR14, rsrvStr14);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR15<br>
     * 字段描述:
     * @return the rsrvStr15
     * @throws BOException
     */
     public String getRsrvStr15() throws BOException {
         return DataType.getAsString(get(RSRV_STR15));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR15<br>
     * 字段描述:
     */
     public void setRsrvStr15(String rsrvStr15) {
         set(RSRV_STR15, rsrvStr15);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR16<br>
     * 字段描述:
     * @return the rsrvStr16
     * @throws BOException
     */
     public String getRsrvStr16() throws BOException {
         return DataType.getAsString(get(RSRV_STR16));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR16<br>
     * 字段描述:
     */
     public void setRsrvStr16(String rsrvStr16) {
         set(RSRV_STR16, rsrvStr16);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR17<br>
     * 字段描述:
     * @return the rsrvStr17
     * @throws BOException
     */
     public String getRsrvStr17() throws BOException {
         return DataType.getAsString(get(RSRV_STR17));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR17<br>
     * 字段描述:
     */
     public void setRsrvStr17(String rsrvStr17) {
         set(RSRV_STR17, rsrvStr17);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR18<br>
     * 字段描述:
     * @return the rsrvStr18
     * @throws BOException
     */
     public String getRsrvStr18() throws BOException {
         return DataType.getAsString(get(RSRV_STR18));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR18<br>
     * 字段描述:
     */
     public void setRsrvStr18(String rsrvStr18) {
         set(RSRV_STR18, rsrvStr18);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR19<br>
     * 字段描述:
     * @return the rsrvStr19
     * @throws BOException
     */
     public String getRsrvStr19() throws BOException {
         return DataType.getAsString(get(RSRV_STR19));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR19<br>
     * 字段描述:
     */
     public void setRsrvStr19(String rsrvStr19) {
         set(RSRV_STR19, rsrvStr19);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR20<br>
     * 字段描述:
     * @return the rsrvStr20
     * @throws BOException
     */
     public String getRsrvStr20() throws BOException {
         return DataType.getAsString(get(RSRV_STR20));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR20<br>
     * 字段描述:
     */
     public void setRsrvStr20(String rsrvStr20) {
         set(RSRV_STR20, rsrvStr20);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR21<br>
     * 字段描述:
     * @return the rsrvStr21
     * @throws BOException
     */
     public String getRsrvStr21() throws BOException {
         return DataType.getAsString(get(RSRV_STR21));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR21<br>
     * 字段描述:
     */
     public void setRsrvStr21(String rsrvStr21) {
         set(RSRV_STR21, rsrvStr21);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR22<br>
     * 字段描述:
     * @return the rsrvStr22
     * @throws BOException
     */
     public String getRsrvStr22() throws BOException {
         return DataType.getAsString(get(RSRV_STR22));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR22<br>
     * 字段描述:
     */
     public void setRsrvStr22(String rsrvStr22) {
         set(RSRV_STR22, rsrvStr22);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR23<br>
     * 字段描述:
     * @return the rsrvStr23
     * @throws BOException
     */
     public String getRsrvStr23() throws BOException {
         return DataType.getAsString(get(RSRV_STR23));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR23<br>
     * 字段描述:
     */
     public void setRsrvStr23(String rsrvStr23) {
         set(RSRV_STR23, rsrvStr23);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR24<br>
     * 字段描述:
     * @return the rsrvStr24
     * @throws BOException
     */
     public String getRsrvStr24() throws BOException {
         return DataType.getAsString(get(RSRV_STR24));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR24<br>
     * 字段描述:
     */
     public void setRsrvStr24(String rsrvStr24) {
         set(RSRV_STR24, rsrvStr24);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR25<br>
     * 字段描述:
     * @return the rsrvStr25
     * @throws BOException
     */
     public String getRsrvStr25() throws BOException {
         return DataType.getAsString(get(RSRV_STR25));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR25<br>
     * 字段描述:
     */
     public void setRsrvStr25(String rsrvStr25) {
         set(RSRV_STR25, rsrvStr25);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR26<br>
     * 字段描述:
     * @return the rsrvStr26
     * @throws BOException
     */
     public String getRsrvStr26() throws BOException {
         return DataType.getAsString(get(RSRV_STR26));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR26<br>
     * 字段描述:
     */
     public void setRsrvStr26(String rsrvStr26) {
         set(RSRV_STR26, rsrvStr26);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR27<br>
     * 字段描述:
     * @return the rsrvStr27
     * @throws BOException
     */
     public String getRsrvStr27() throws BOException {
         return DataType.getAsString(get(RSRV_STR27));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR27<br>
     * 字段描述:
     */
     public void setRsrvStr27(String rsrvStr27) {
         set(RSRV_STR27, rsrvStr27);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR28<br>
     * 字段描述:
     * @return the rsrvStr28
     * @throws BOException
     */
     public String getRsrvStr28() throws BOException {
         return DataType.getAsString(get(RSRV_STR28));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR28<br>
     * 字段描述:
     */
     public void setRsrvStr28(String rsrvStr28) {
         set(RSRV_STR28, rsrvStr28);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR29<br>
     * 字段描述:
     * @return the rsrvStr29
     * @throws BOException
     */
     public String getRsrvStr29() throws BOException {
         return DataType.getAsString(get(RSRV_STR29));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR29<br>
     * 字段描述:
     */
     public void setRsrvStr29(String rsrvStr29) {
         set(RSRV_STR29, rsrvStr29);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_STR30<br>
     * 字段描述:
     * @return the rsrvStr30
     * @throws BOException
     */
     public String getRsrvStr30() throws BOException {
         return DataType.getAsString(get(RSRV_STR30));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_STR30<br>
     * 字段描述:
     */
     public void setRsrvStr30(String rsrvStr30) {
         set(RSRV_STR30, rsrvStr30);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE1<br>
     * 字段描述:
     * @return the rsrvDate1
     * @throws BOException
     */
     public Date getRsrvDate1() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE1));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE1<br>
     * 字段描述:
     */
     public void setRsrvDate1(Date rsrvDate1) {
         set(RSRV_DATE1, rsrvDate1);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE2<br>
     * 字段描述:
     * @return the rsrvDate2
     * @throws BOException
     */
     public Date getRsrvDate2() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE2));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE2<br>
     * 字段描述:
     */
     public void setRsrvDate2(Date rsrvDate2) {
         set(RSRV_DATE2, rsrvDate2);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE3<br>
     * 字段描述:
     * @return the rsrvDate3
     * @throws BOException
     */
     public Date getRsrvDate3() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE3));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE3<br>
     * 字段描述:
     */
     public void setRsrvDate3(Date rsrvDate3) {
         set(RSRV_DATE3, rsrvDate3);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE4<br>
     * 字段描述:
     * @return the rsrvDate4
     * @throws BOException
     */
     public Date getRsrvDate4() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE4));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE4<br>
     * 字段描述:
     */
     public void setRsrvDate4(Date rsrvDate4) {
         set(RSRV_DATE4, rsrvDate4);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE5<br>
     * 字段描述:
     * @return the rsrvDate5
     * @throws BOException
     */
     public Date getRsrvDate5() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE5));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE5<br>
     * 字段描述:
     */
     public void setRsrvDate5(Date rsrvDate5) {
         set(RSRV_DATE5, rsrvDate5);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE6<br>
     * 字段描述:
     * @return the rsrvDate6
     * @throws BOException
     */
     public Date getRsrvDate6() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE6));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE6<br>
     * 字段描述:
     */
     public void setRsrvDate6(Date rsrvDate6) {
         set(RSRV_DATE6, rsrvDate6);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE7<br>
     * 字段描述:
     * @return the rsrvDate7
     * @throws BOException
     */
     public Date getRsrvDate7() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE7));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE7<br>
     * 字段描述:
     */
     public void setRsrvDate7(Date rsrvDate7) {
         set(RSRV_DATE7, rsrvDate7);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE8<br>
     * 字段描述:
     * @return the rsrvDate8
     * @throws BOException
     */
     public Date getRsrvDate8() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE8));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE8<br>
     * 字段描述:
     */
     public void setRsrvDate8(Date rsrvDate8) {
         set(RSRV_DATE8, rsrvDate8);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE9<br>
     * 字段描述:
     * @return the rsrvDate9
     * @throws BOException
     */
     public Date getRsrvDate9() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE9));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE9<br>
     * 字段描述:
     */
     public void setRsrvDate9(Date rsrvDate9) {
         set(RSRV_DATE9, rsrvDate9);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_DATE10<br>
     * 字段描述:
     * @return the rsrvDate10
     * @throws BOException
     */
     public Date getRsrvDate10() throws BOException {
         return DataType.getAsDate(get(RSRV_DATE10));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_DATE10<br>
     * 字段描述:
     */
     public void setRsrvDate10(Date rsrvDate10) {
         set(RSRV_DATE10, rsrvDate10);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG1<br>
     * 字段描述:
     * @return the rsrvTag1
     * @throws BOException
     */
     public String getRsrvTag1() throws BOException {
         return DataType.getAsString(get(RSRV_TAG1));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG1<br>
     * 字段描述:
     */
     public void setRsrvTag1(String rsrvTag1) {
         set(RSRV_TAG1, rsrvTag1);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG2<br>
     * 字段描述:
     * @return the rsrvTag2
     * @throws BOException
     */
     public String getRsrvTag2() throws BOException {
         return DataType.getAsString(get(RSRV_TAG2));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG2<br>
     * 字段描述:
     */
     public void setRsrvTag2(String rsrvTag2) {
         set(RSRV_TAG2, rsrvTag2);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG3<br>
     * 字段描述:
     * @return the rsrvTag3
     * @throws BOException
     */
     public String getRsrvTag3() throws BOException {
         return DataType.getAsString(get(RSRV_TAG3));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG3<br>
     * 字段描述:
     */
     public void setRsrvTag3(String rsrvTag3) {
         set(RSRV_TAG3, rsrvTag3);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG4<br>
     * 字段描述:
     * @return the rsrvTag4
     * @throws BOException
     */
     public String getRsrvTag4() throws BOException {
         return DataType.getAsString(get(RSRV_TAG4));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG4<br>
     * 字段描述:
     */
     public void setRsrvTag4(String rsrvTag4) {
         set(RSRV_TAG4, rsrvTag4);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG5<br>
     * 字段描述:
     * @return the rsrvTag5
     * @throws BOException
     */
     public String getRsrvTag5() throws BOException {
         return DataType.getAsString(get(RSRV_TAG5));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG5<br>
     * 字段描述:
     */
     public void setRsrvTag5(String rsrvTag5) {
         set(RSRV_TAG5, rsrvTag5);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG6<br>
     * 字段描述:
     * @return the rsrvTag6
     * @throws BOException
     */
     public String getRsrvTag6() throws BOException {
         return DataType.getAsString(get(RSRV_TAG6));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG6<br>
     * 字段描述:
     */
     public void setRsrvTag6(String rsrvTag6) {
         set(RSRV_TAG6, rsrvTag6);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG7<br>
     * 字段描述:
     * @return the rsrvTag7
     * @throws BOException
     */
     public String getRsrvTag7() throws BOException {
         return DataType.getAsString(get(RSRV_TAG7));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG7<br>
     * 字段描述:
     */
     public void setRsrvTag7(String rsrvTag7) {
         set(RSRV_TAG7, rsrvTag7);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG8<br>
     * 字段描述:
     * @return the rsrvTag8
     * @throws BOException
     */
     public String getRsrvTag8() throws BOException {
         return DataType.getAsString(get(RSRV_TAG8));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG8<br>
     * 字段描述:
     */
     public void setRsrvTag8(String rsrvTag8) {
         set(RSRV_TAG8, rsrvTag8);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG9<br>
     * 字段描述:
     * @return the rsrvTag9
     * @throws BOException
     */
     public String getRsrvTag9() throws BOException {
         return DataType.getAsString(get(RSRV_TAG9));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG9<br>
     * 字段描述:
     */
     public void setRsrvTag9(String rsrvTag9) {
         set(RSRV_TAG9, rsrvTag9);
     }

     /**
     * 获取字段值:UM_OTHER.RSRV_TAG10<br>
     * 字段描述:
     * @return the rsrvTag10
     * @throws BOException
     */
     public String getRsrvTag10() throws BOException {
         return DataType.getAsString(get(RSRV_TAG10));
     }

     /**
     * 设置字段值:UM_OTHER.RSRV_TAG10<br>
     * 字段描述:
     */
     public void setRsrvTag10(String rsrvTag10) {
         set(RSRV_TAG10, rsrvTag10);
     }

     /**
     * 获取字段值:UM_OTHER.PROCESS_TAG<br>
     * 字段描述:
     * @return the processTag
     * @throws BOException
     */
     public String getProcessTag() throws BOException {
         return DataType.getAsString(get(PROCESS_TAG));
     }

     /**
     * 设置字段值:UM_OTHER.PROCESS_TAG<br>
     * 字段描述:
     */
     public void setProcessTag(String processTag) {
         set(PROCESS_TAG, processTag);
     }

     /**
     * 获取字段值:UM_OTHER.VALID_DATE<br>
     * 字段描述:
     * @return the validDate
     * @throws BOException
     */
     public Date getValidDate() throws BOException {
         return DataType.getAsDate(get(VALID_DATE));
     }

     /**
     * 设置字段值:UM_OTHER.VALID_DATE<br>
     * 字段描述:
     */
     public void setValidDate(Date validDate) {
         set(VALID_DATE, validDate);
     }

     /**
     * 获取字段值:UM_OTHER.EXPIRE_DATE<br>
     * 字段描述:
     * @return the expireDate
     * @throws BOException
     */
     public Date getExpireDate() throws BOException {
         return DataType.getAsDate(get(EXPIRE_DATE));
     }

     /**
     * 设置字段值:UM_OTHER.EXPIRE_DATE<br>
     * 字段描述:
     */
     public void setExpireDate(Date expireDate) {
         set(EXPIRE_DATE, expireDate);
     }

     /**
     * 获取字段值:UM_OTHER.DONE_DATE<br>
     * 字段描述:
     * @return the doneDate
     * @throws BOException
     */
     public Date getDoneDate() throws BOException {
         return DataType.getAsDate(get(DONE_DATE));
     }

     /**
     * 设置字段值:UM_OTHER.DONE_DATE<br>
     * 字段描述:
     */
     public void setDoneDate(Date doneDate) {
         set(DONE_DATE, doneDate);
     }

     /**
     * 获取字段值:UM_OTHER.OP_ID<br>
     * 字段描述:
     * @return the opId
     * @throws BOException
     */
     public String getOpId() throws BOException {
         return DataType.getAsString(get(OP_ID));
     }

     /**
     * 设置字段值:UM_OTHER.OP_ID<br>
     * 字段描述:
     */
     public void setOpId(String opId) {
         set(OP_ID, opId);
     }

     /**
     * 获取字段值:UM_OTHER.ORG_ID<br>
     * 字段描述:
     * @return the orgId
     * @throws BOException
     */
     public String getOrgId() throws BOException {
         return DataType.getAsString(get(ORG_ID));
     }

     /**
     * 设置字段值:UM_OTHER.ORG_ID<br>
     * 字段描述:
     */
     public void setOrgId(String orgId) {
         set(ORG_ID, orgId);
     }

     /**
     * 获取字段值:UM_OTHER.REMARKS<br>
     * 字段描述:
     * @return the remarks
     * @throws BOException
     */
     public String getRemarks() throws BOException {
         return DataType.getAsString(get(REMARKS));
     }

     /**
     * 设置字段值:UM_OTHER.REMARKS<br>
     * 字段描述:
     */
     public void setRemarks(String remarks) {
         set(REMARKS, remarks);
     }

     /**
     * 获取字段值:UM_OTHER.OPEN_CODE<br>
     * 字段描述:
     * @return the openCode
     * @throws BOException
     */
     public String getOpenCode() throws BOException {
         return DataType.getAsString(get(OPEN_CODE));
     }

     /**
     * 设置字段值:UM_OTHER.OPEN_CODE<br>
     * 字段描述:
     */
     public void setOpenCode(String openCode) {
         set(OPEN_CODE, openCode);
     }

     /**
     * 获取字段值:UM_OTHER.IS_NEED_PF<br>
     * 字段描述:
     * @return the isNeedPf
     * @throws BOException
     */
     public String getIsNeedPf() throws BOException {
         return DataType.getAsString(get(IS_NEED_PF));
     }

     /**
     * 设置字段值:UM_OTHER.IS_NEED_PF<br>
     * 字段描述:
     */
     public void setIsNeedPf(String isNeedPf) {
         set(IS_NEED_PF, isNeedPf);
     }

     /**
     * 获取字段值:UM_OTHER.CREATE_DATE<br>
     * 字段描述:
     * @return the createDate
     * @throws BOException
     */
     public Date getCreateDate() throws BOException {
         return DataType.getAsDate(get(CREATE_DATE));
     }

     /**
     * 设置字段值:UM_OTHER.CREATE_DATE<br>
     * 字段描述:
     */
     public void setCreateDate(Date createDate) {
         set(CREATE_DATE, createDate);
     }

     /**
     * 获取字段值:UM_OTHER.CREATE_OP_ID<br>
     * 字段描述:
     * @return the createOpId
     * @throws BOException
     */
     public String getCreateOpId() throws BOException {
         return DataType.getAsString(get(CREATE_OP_ID));
     }

     /**
     * 设置字段值:UM_OTHER.CREATE_OP_ID<br>
     * 字段描述:
     */
     public void setCreateOpId(String createOpId) {
         set(CREATE_OP_ID, createOpId);
     }

     /**
     * 获取字段值:UM_OTHER.CREATE_ORG_ID<br>
     * 字段描述:
     * @return the createOrgId
     * @throws BOException
     */
     public String getCreateOrgId() throws BOException {
         return DataType.getAsString(get(CREATE_ORG_ID));
     }

     /**
     * 设置字段值:UM_OTHER.CREATE_ORG_ID<br>
     * 字段描述:
     */
     public void setCreateOrgId(String createOrgId) {
         set(CREATE_ORG_ID, createOrgId);
     }

     /**
     * 获取字段值:UM_OTHER.DONE_CODE<br>
     * 字段描述:
     * @return the doneCode
     * @throws BOException
     */
     public long getDoneCode() throws BOException {
         return DataType.getAsLong(get(DONE_CODE));
     }

     /**
     * 设置字段值:UM_OTHER.DONE_CODE<br>
     * 字段描述:
     */
     public void setDoneCode(long doneCode) {
         set(DONE_CODE, doneCode);
     }

     /**
     * 获取字段值:UM_OTHER.REGION_ID<br>
     * 字段描述:
     * @return the regionId
     * @throws BOException
     */
     public String getRegionId() throws BOException {
         return DataType.getAsString(get(REGION_ID));
     }

     /**
     * 设置字段值:UM_OTHER.REGION_ID<br>
     * 字段描述:
     */
     public void setRegionId(String regionId) {
         set(REGION_ID, regionId);
     }

}
