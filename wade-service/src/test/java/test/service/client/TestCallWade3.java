package test.service.client;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.Constants;
import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.serializer.Wade3ToIOData;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;

public class TestCallWade3 {
	
	private static final Logger log = Logger.getLogger(TestCallWade3.class);
	
	public static void main(String[] args) throws IOException {
		
		TestCallWade3 call = new TestCallWade3();
		
		//UIP的调用串
		String requestString = "{\"ORIGDOMAIN\":\"UMMP\",\"HOMEDOMAIN\":\"BOSS\",\"BIPCODE\":\"BIP3A221\",\"ACTIVITYCODE\":\"T3000227\",\"TESTFLAG\":\"0\",\"X_TRANS_CODE\":\"QAM_BBOSS_qryPrestoreReturn\",\"KIND_ID\":\"BIP3A221_T3000227_1_0\",\"BUSI_SIGN\":\"BIP3A221_T3000227_1_0\",\"UIPBUSIID\":\"315101517081401113359\",\"BIPVER\":\"\",\"ACTIONCODE\":\"0\",\"ROUTETYPE\":\"01\",\"ROUTEVALUE\":\"13920165629\",\"PROCID\":\"070214448982861181444898286119\",\"TRANSIDO\":\"070214448982861181444898286119\",\"TRANSIDH\":\"\",\"PROCESSTIME\":\"20151015164124\",\"TRANSIDC\":\"9980770120151015164006123007303\",\"CUTOFFDAY\":\"20151015\",\"OSNDUNS\":\"9980\",\"HSNDUNS\":\"2200\",\"CONVID\":\"570e2bb4-5b58-44d7-9692-395196c2abfd\",\"MSGSENDER\":\"0216\",\"MSGRECEIVER\":\"2201\",\"SVCCONTVER\":\"\",\"ID_TYPE\":\"01\",\"ID_ITEM_RANGE\":\"13920165629\",\"BIZ_TYPE\":\"07\",\"OPR_NUMB\":\"UMMPBIP3A22120151015164124721857\",\"IDENT_CODE\":\"ua110502030211425220140516100143\",\"TRADE_CITY_CODE\":\"0022\",\"TRADE_DEPART_ID\":\"00309\",\"TRADE_STAFF_ID\":\"IBOSS000\",\"TRADE_EPARCHY_CODE\":\"0022\",\"IN_MODE_CODE\":\"6\",\"X_IBOSSMODE\":\"1\",\"X_TRACE_ID\":\"\",\"X_PARENT_ID\":\"\"}";
		
		//UIP发起的HTTP的contentType, 默认是GBK
		String charset = "UTF-8";
		
		//模拟账务返回的IDataset对象
		IDataset dataset = new DatasetList("[{\"RSPDESC\":\"TradeOK\",\"X_RSPTYPE\":\"0\",\"X_RECORDNUM\":4,\"EXPIRE_DATE\":\"20151231\",\"X_RESULTCODE\":\"0\",\"PRESTORE_SUM_FEE\":\"0.00\",\"VALID_DATE\":\"20141201\",\"X_RESULTINFO\":\"TradeOK\",\"PRESTORE_PROD_NAME\":\"2012139邮箱年末营销活动5元\",\"X_RSPCODE\":\"0000\",\"RETURN_FEE_INFO\":[],\"OPR_TIME\":\"20151016193116\",\"RSPCODE\":\"0000\"},{\"VALID_DATE\":\"20141101\",\"PRESTORE_SUM_FEE\":\"100.00\",\"PRESTORE_PROD_NAME\":\"2010网龄优惠计划全球通在网4年及以上送100\",\"RETURN_FEE_INFO\":[],\"EXPIRE_DATE\":\"20151130\"},{\"VALID_DATE\":\"20141101\",\"PRESTORE_SUM_FEE\":\"20.00\",\"PRESTORE_PROD_NAME\":\"2011年网龄优惠促销活动赠送20元话费\",\"RETURN_FEE_INFO\":[],\"EXPIRE_DATE\":\"20151130\"},{\"VALID_DATE\":\"20140901\",\"PRESTORE_SUM_FEE\":\"5.00\",\"PRESTORE_PROD_NAME\":\"60元小额话费包活动\",\"RETURN_FEE_INFO\":[{\"STATUS\":\"1\",\"RETURN_FEE_NAME\":\"60元小额话费包活动\",\"RETURN_TIME\":\"20150903\",\"RETURN_FEE_NUM\":\"5.00\"},{\"STATUS\":\"1\",\"RETURN_FEE_NAME\":\"60元小额话费包活动\",\"RETURN_TIME\":\"20151003\",\"RETURN_FEE_NUM\":\"5.00\"},{\"STATUS\":\"1\",\"RETURN_FEE_NAME\":\"60元小额话费包活动\",\"RETURN_TIME\":\"20150903\",\"RETURN_FEE_NUM\":\"5.00\"}],\"EXPIRE_DATE\":\"20151131\"}]");
		IData d1 = new DataMap();
		
		System.out.println(dataset.get(0));
		//返回给UIP的串
		String out = call.uipCallCrm(new ByteArrayInputStream(requestString.getBytes()), charset, dataset);
		System.out.println(out);
		
		//out="{\"RETURN_FEE_INFO\"=[[\"[]\", [], [], []]]}";
		List list = Wade3DataTran.strToList(out);
		IDataset ds = Wade3DataTran.wade3To4Dataset(list);
		System.out.println(ds);
		
		Map map = Wade3DataTran.strToMap(out);
		IData data = Wade3DataTran.wade3To4DataMap(map);
		System.out.println(data);
	}
	
	
	/**
	 * 模拟外接接口调云化CRM的接口
	 * @param requestString	外围接口发起的调用串
	 * @param charset	字符集
	 * @param dataset	CRM接口返回的IDataset对象
	 * @return
	 * @throws IOException
	 */
	public String uipCallCrm(InputStream requestString, String charset, IDataset dataset) throws IOException {
		Wade3ToIOData wade3io = new Wade3ToIOData();
		
		if (log.isDebugEnabled()) {
			log.debug(">>>WADE3请求字符集:" + charset);
		}
		if (null != charset && charset.length() > 0)
			wade3io.setCharset(charset);
		
		IDataOutput output = new DataOutput();
		
		String serviceName = null;
		
		try {
			IDataInput input = wade3io.read(requestString);
			IData head = input.getHead();

			serviceName = head.getString(Constants.X_TRANS_CODE);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			String ibossMode = head.getString("X_IBOSSMODE", "0");
			String xTransMode = head.getString("X_TRANSMODE", "0");
			
			if (log.isDebugEnabled()) {
				log.debug(">>>IBOSS多行请求模式:" + "1".equals(ibossMode));
			}
			
			
			output = new DataOutput(input.getHead(), dataset);
			
			IData outHead = output.getHead();
			outHead.put("X_IBOSSMODE", ibossMode);
			outHead.put("X_TRANSMODE", xTransMode);
			
			String code = outHead.getString(Constants.X_RESULTCODE);
			if ("0".equals(code)) {
				if ("SHXI".equals(SystemCfg.provinceCode)) {
					if ("6".equals(inModeCode)) {
						outHead.put(Constants.X_RESULTCODE, "00");
					} else {
						outHead.put(Constants.X_RESULTCODE, "0");
					}
				} else {
					if ("6".equals(inModeCode) || "N".equals(inModeCode)) {
						outHead.put(Constants.X_RESULTCODE, "00");
					} else {
						outHead.put(Constants.X_RESULTCODE, "0");
					}
				}
				outHead.put(Constants.X_RSPCODE, "0000");
			}
			if ("1".equals(ibossMode)) {
				return new String(wade3io.write(output, true));
			} else {
				return new String(wade3io.write(output));
			}
		} catch (EOFException e) {
			StringBuilder err = new StringBuilder();
			err.append(BaseException.CODE_SVC_TESTATTACK);
			err.append("[");
			err.append(new Date(System.currentTimeMillis()).toString());
			err.append("]");
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_TESTATTACK);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, err.toString());
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			return new String(wade3io.write(output));
		} catch (BaseException e) {
			StringBuilder err = new StringBuilder();
			err.append(BaseException.INFO_SVC_INVOKEERROR);
			err.append(":");
			err.append(e.getMessage());
			err.append("[");
			err.append(System.getProperty("wade.server.name"));
			err.append(",");
			err.append(serviceName);
			err.append(",");
			err.append(System.currentTimeMillis());
			err.append("]");
			
			log.error(err.toString(), e);
			
			IDataset data = new DatasetList();
			data.add(e.getData());
			
			output = new DataOutput(e.getData(), data);
			return new String(wade3io.write(output));
		} catch (Exception e) {
			StringBuilder err = new StringBuilder();
			err.append(BaseException.INFO_SVC_INVOKEERROR);
			err.append(":");
			err.append(e.getMessage());
			err.append("[");
			err.append(System.getProperty("wade.server.name"));
			err.append(",");
			err.append(serviceName);
			err.append(",");
			err.append(System.currentTimeMillis());
			err.append("]");
			
			log.error(err.toString(), e);
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_INVOKEERROR);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, err.toString());
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());

			return new String(wade3io.write(output));
		}
	}

}
