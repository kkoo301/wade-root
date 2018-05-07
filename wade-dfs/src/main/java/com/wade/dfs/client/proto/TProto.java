package com.wade.dfs.client.proto;

public final class TProto {
	
	/**
	 * 没有groupName的情况下，查询Storage地址
	 */
	public static final byte GET_STORESITE_WITHOUT_GNAME = 101;
	
	/**
	 * 有groupName的情况下，查询Storage地址
	 */
	public static final byte GET_STORESITE_WITH_GNAME = 104;
	
	/**
	 * 
	 */
	public static final int QUERY_STORAGE_STORE_BODY_LEN = Proto.GNAME_MAX_LEN + Proto.IPADDR_SIZE + Proto.PKG_LEN_SIZE;
	
	/**
	 * tracker返回编码
	 */
	public static final byte RESP = 100;
}
