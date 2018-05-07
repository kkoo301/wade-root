package com.ailk.search.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ailk.database.dbconn.DBConnection;
import com.ailk.service.session.SessionManager;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: FeatureRecognise
 * @description: 号码特征识别
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class FeatureRecognise {
	
	/**
	 * 转成趋势byte[]，即后一位减前一位的差值
	 * 
	 * @param sn
	 * @return
	 */
	private static byte[] toTrendBytes(byte[] bytes) {
		byte[] rtn = new byte[bytes.length - 1];
		for (int i = 0, len = bytes.length - 1; i < len; i++) {
			rtn[i] = (byte)(bytes[i + 1] - bytes[i]);
		}
		return rtn;
	}
	
	/**
	 * 判断是否3A,4A,5A...号
	 * 连续00表示3A号,连续000表示4A号...
	 * 
	 * @param trendByte
	 * @return
	 */
	private static final int isXA(final byte[] trendByte) {
		int count = 0;
		int max = 0;
		for (int i = 0, len = trendByte.length; i < len; i++) {
			if (0 == trendByte[i]) {
				count++;
			} else {
				if (count > max)  max = count;
				count = 0;
			}
		}
		
		if (count > max) {
			max = count;
		}

		return max < 2 ? -1 : (max + 1);
	}
	
	/**
	 * 判断是否为顺号
	 * 连续11表示3顺号,连续111表示4顺号...
	 * 
	 * @param trendByte
	 * @return
	 */
	private static final int isAscend(final byte[] trendByte) {
		int count = 0;
		int max = 0;
		for (int i = 0, len = trendByte.length; i < len; i++) {
			if (1 == trendByte[i]) {
				count++;
			} else {
				if (count > max)  max = count;
				count = 0;
			}
		}
		if (count > max) {
			max = count;
		}
		
		return max < 2 ? -1 : (max + 1);
	}
	
	/**
	 * 判断是否为降顺号
	 * 连续-1-1表示3逆序号,连续-1-1-1表示4逆顺号...
	 * 
	 * @param trendByte
	 * @return
	 */
	private static final int isDescend(final byte[] trendByte) {
		int count = 0;
		int max = 0;
		for (int i = 0, len = trendByte.length; i < len; i++) {
			if (-1 == trendByte[i]) {
				count++;
			} else {
				if (count > max)  max = count;
				count = 0;
			}
		}
		if (count > max) {
			max = count;
		}
		
		return max < 2 ? -1 : (max + 1);		
	}
	
	/**
	 * 是否为AABB号码
	 * 
	 * @param trendByte
	 * @return
	 */
	private static final boolean isAABB(final byte[] trendByte) {
		for (int i = 0, len = trendByte.length - 2; i < len; i++) {
			if (0 == trendByte[i] && 0 != trendByte[i + 1] && 0 == trendByte[i + 2]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ABAB 
	 * 
	 * 特征码: 1X1  X != 0
	 * 
	 * @param bytes
	 * @return
	 */
	private static final boolean isABAB(final byte[] bytes) {
		for (int i = 0, len = bytes.length - 2; i < len; i++) {
			if (bytes[i] == bytes[i + 2] && bytes[i] == bytes[i + 1] * -1 && 0 != bytes[i + 1]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * AABBCC
	 * 
	 * 特征码: 0X0Y0 
	 * X != Y != 0
	 * 
	 * @param bytes
	 * @return
	 */
	private static final boolean isAABBCC(final byte[] bytes) {
		for (int i = 0, len = bytes.length - 4; i < len; i++) {
			if (0 == bytes[i] && 0 == bytes[i + 2] && 0 == bytes[i + 4]) {
				if (0 != bytes[i + 1] && 0 != bytes[i + 3]) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * AAABBB
	 * 
	 * 特征码: 00X00  X!=0
	 * 
	 * @param bytes
	 * @return
	 */
	private static final boolean isAAABBB(final byte[] bytes) {
		for (int i = 0, len = bytes.length - 4; i < len; i++) {
			if (bytes[i] == 0 && bytes[i + 1] == 0 && bytes[i + 3] == 0 && bytes[i + 4] == 0) {
				if (bytes[i + 2] != 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * ABABAB
	 * 
	 * 特征码: 连续5位，一正一负，一正一负
	 * 131313
	 *  2 -2 2 -2 2
	 *  
	 * @param bytes
	 * @return
	 */
	private static final boolean isABABAB(final byte[] bytes) {
		
		for (int i = 0, len = bytes.length - 4; i < len; i++) {
			if (bytes[i] == bytes[i + 2] && bytes[i + 2] == bytes[i + 4]) {
				if (bytes[i + 1] == bytes[i + 3] && bytes[i + 1] == (bytes[i] * -1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 特征码: ABCABC
	 * 11X11
	 * 
	 * @param bytes
	 * @return
	 */
	private static final boolean isABCABC(final byte[] trendByte, final byte[] bytes) {
		
		for (int i = 0, len = trendByte.length - 4; i < len; i++) {
			if (1 == trendByte[i] && 1 == trendByte[i + 1] && -2 == trendByte[i + 2] && 1 == trendByte[i + 3] && 1 == trendByte[i + 4])  {
				if (bytes[i] == bytes[i + 3]) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * ABCCBA
	 * 
	 * 特征码: 110-1-1
	 * 
	 * @param bytes
	 * @return
	 */
	private static final boolean isABCCBA(final byte[] trendByte) {
		
		for (int i = 0, len = trendByte.length - 4; i < len; i++) {
			if (1 == trendByte[i] && 1 == trendByte[i + 1] && 0 == trendByte[i + 2] && -1 == trendByte[i + 3] && -1 == trendByte[i + 4]) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * 判断哪个数字较多
	 * 
	 * @return
	 */
	private static final int isNMuch(final byte[] bytes) {
		byte[] cnt = new byte[10];
		for (byte b : bytes) {
			cnt[b - '0']++;
		}
		int n = 0;
		int max = 0;
		for (int i = 0, len = cnt.length; i < len; i++) {
			if (cnt[i] > max) {
				max = cnt[i];
				n = i;
			}
		}
		return max > 3 ? n : -1;
	}
	
	private static void working() throws Exception {
		DBConnection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement uptStmt = null;
		ResultSet rs = null;

		try {

			conn = SessionManager.getInstance().getSessionConnection("cen1");
			stmt = conn.prepareStatement("select T.ROWID, T.SERIAL_NUMBER from TF_R_MPHONECODE_IDLE T");
			uptStmt = conn.prepareStatement("update TF_R_MPHONECODE_IDLE set FEATURE_CODE = ? where rowid = ?");
			rs = stmt.executeQuery();
		    rs.setFetchSize(10000);
		    
		    long updateCount = 0;
		    long count = 0;
			long start = System.currentTimeMillis();
			while (rs.next()) {
				String rowid = rs.getString("ROWID");
				String sn = rs.getString("SERIAL_NUMBER");
				
				String featureCode = process(sn.getBytes()).trim();
				if (!featureCode.equals("")) {
					uptStmt.setString(1, featureCode);
					uptStmt.setString(2, rowid);
					uptStmt.addBatch();
					uptStmt.clearParameters();
					updateCount++;
					if (updateCount % 1000 == 0) {
						uptStmt.executeBatch();
						conn.commit();
						System.out.println("提交1000条...");
					}
				}
				count++;
				if (count % 10000 == 0) {
					System.out.println("已处理" + count + "条");
				}
			}
			
			long elipse = System.currentTimeMillis() - start;
			System.out.println("耗时:" + elipse + "毫秒, TPS:" + count / elipse * 1000);
			
		} finally {
			conn.commit();
			
			if (null != rs) {
				rs.close();
			}
			
			if (null != stmt) {
				stmt.close();
			}
		}
	}
	
	private static String process(byte[] bytes) {
		byte[] trendBytes = toTrendBytes(bytes);
		
		StringBuilder sb = new StringBuilder();
		
		if (isAAABBB(trendBytes)) {
			sb.append("AAABBB ");
		}
		
		if (isAABB(trendBytes)) {
			sb.append("AABB ");
		}
		
		if (isAABBCC(trendBytes)) {
			sb.append("AABBCC ");
		}
		
		if (isABAB(trendBytes)) {
			sb.append("ABAB ");
		}
		
		if (isABABAB(bytes)) {
			sb.append("ABABAB ");
		}
		
		if (isABCABC(trendBytes, bytes)) {
			sb.append("ABCABC ");
		}
		
		if (isABCCBA(trendBytes)) {
			sb.append("ABCCBA ");
		}
		
		int ns = isDescend(trendBytes);
		if (-1 != ns) {
			sb.append(ns + "NS ");
		}
		
		int s = isAscend(trendBytes);
		if (-1 != s) {
			sb.append(s + "S ");
		}
		
		int xa = isXA(trendBytes);
		if (-1 != xa) {
			sb.append(xa + "A ");
		}
		
		int nm = isNMuch(bytes);
		if (-1 != nm) {
			sb.append(nm + "JD");
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("启动..");
		working();
		//System.out.println(isNMuch("13787228127".getBytes()));
	}
}
