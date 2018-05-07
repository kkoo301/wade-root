package com.ailk.search.client;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.impl.DatasetList;
import com.ailk.rpc.client.RPCClient;
import com.ailk.rpc.codec.Transporter;
import com.ailk.search.SearchXML;
import com.ailk.search.StaticFactory;
import com.ailk.search.SearchResponse;
import com.ailk.search.util.SplitUtil;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SearchClient
 * @description: 搜索客户端
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class SearchClient {
	
	private static final Logger log = Logger.getLogger(SearchClient.class);
	
	/**
	 * 搜索编码与集群的对应关系
	 */
	private static Map<String, RPCClient> clusters = new HashMap<String, RPCClient>();
		
	private SearchClient() {
		// 无需实例化
	}
	
	/**
	 * 搜索特殊字符处理   xiedx 2018/1/25
	 * @param s
	 * @return
	 */
	public static String escapeQueryChars(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '('
				|| c == ')'  || c == ':' || c == '^' || c == '[' || c == ']'
				|| c == '\"' || c == '{' || c == '}' || c == '~' || c == '*'  
				|| c == '?'  || c == '|' || c == '&' || c == ';' || c == '/') {
				sb.append('\\');
			}
			sb.append(c);
		}
		return sb.toString();
	}
		
	/**
	 * 搜索方法
	 * 
	 * @param searchCode 搜索编码
	 * @param keyword 搜索关键字，注：必须>=2个字符
	 * @param start 开始位置，注: 从0开始
	 * @param end 结束位置
	 * @return
	 * @throws Exception
	 */
	public static SearchResponse search(String searchCode, String keyword, int start, int end) {
		return search(searchCode, keyword, null, start, end);
	}
	
	/**
	 * 搜索方法
	 * 
	 * @param searchCode 搜索编码
	 * @param keyword 搜索关键字，注：必须>=2个字符
	 * @param fullMatchMap 关键字，全词匹配
	 * @param start 开始位置，注: 从0开始
	 * @param end 结束位置
	 * @return
	 * @throws Exception
	 */
	public static SearchResponse search(String searchCode, String keyword, Map<String, String> fullMatchMap, int start, int end) {
		
		String rawKeyword = keyword;
		SearchResponse rtn = null;
		
		if (StringUtils.isBlank(searchCode)) {
			throw new IllegalArgumentException("搜索编码不可为空！");
		}
				
		if (start < 0 || end < 0) {
			throw new IllegalArgumentException("start, end参数必须大于零！start=" + start + ", end=" + end);
		}
		
		if (start > end) {
			throw new IllegalArgumentException("start必须小于等于end！start=" + start + ", end=" + end);
		}
		
		// 将全词匹配的关键词用空格分隔开拼成一个串
		StringBuilder fullMatchBuff = new StringBuilder(80);
		if (null != fullMatchMap) {
			for (String key : fullMatchMap.keySet()) {
				fullMatchBuff.append(key);
				fullMatchBuff.append(fullMatchMap.get(key));
				fullMatchBuff.append(StaticFactory.KEYWORD_SEPARATOR);
			}
		}
		String fullMatchWord = fullMatchBuff.toString().trim();
		
		if (StringUtils.isBlank(keyword)) {
			keyword = fullMatchWord;
		} else {
			keyword = SplitUtil.searchKeywordSplit(keyword) + StaticFactory.KEYWORD_SEPARATOR + fullMatchWord;
			keyword = keyword.trim();
		}
		
		if (StringUtils.isBlank(keyword)) {
			throw new IllegalArgumentException("搜索关键字不可为空！");
		}
		
		/*
		if (StringUtils.containsAny(keyword, "+-&|!(){}[]^\"~*?:")) { // 过滤Lucene特殊字符
			throw new IllegalArgumentException("搜索参数中不能包涵特殊符号! keyword=[" + rawKeyword + "]");
		}
		*/
		
		/**
		 * 当搜索关键字少于2个char时直接返回
		 */
		if (keyword.length() <= 1) {
			rtn = new SearchResponse();
			rtn.setNumTotalHits(0);
			rtn.setDatas(new DatasetList());
			return rtn;
		}
		
		Transporter transporter = new Transporter();
		transporter.setClazzName("com.ailk.search.server.SearchServer");
		transporter.setMethodName("search");
		transporter.setParams(new Object[]{searchCode, keyword, Integer.valueOf(start), Integer.valueOf(end)});
		transporter.setParamTypes(new Class[]{ String.class, String.class, Integer.class, Integer.class });
		
		RPCClient client = clusters.get(searchCode);
		rtn = (SearchResponse)client.rpcCall(transporter);
		if (null != rtn && !"".equals(rtn.getErrInfo())) {
			throw new RuntimeException(rtn.getErrInfo());
		}
		
		return rtn;
		
	}
	
	static {

		// 加载搜索配置
		SearchXML xml = new SearchXML();
		xml.load();
		
		Map<String, String> codes = xml.getSearchCodes();
		Map<String, SortedSet<String>> clusterMap = xml.getClusters();
		int hbsec = xml.getHbsec();
		int poolSize = xml.getPoolSize();
		
		// 建立集群对应的连接
		Map<String, RPCClient> clients = new HashMap<String, RPCClient>();
		for (String clusterName : clusterMap.keySet()) {
			SortedSet<String> address = clusterMap.get(clusterName);
			RPCClient client = new RPCClient(StringUtils.join(address, ','));
			client.setMaintSleepSec(hbsec);
			log.info("搜索客户端心跳周期:" + hbsec + "秒");
			
			client.setConnNumEachServer(poolSize);
			log.info("搜索连接池个数:" + poolSize);
			
			clients.put(clusterName, client);
		}
		
		// 建立搜索编码与集群的对应关系
		for (String code : codes.keySet()) {
			String connect = codes.get(code);
			clusters.put(code, clients.get(connect));
			log.info("---------------------------------");
			log.info("搜索编码:" + code);
			log.info("对应集群:" + connect);
			log.info("集群地址:" + StringUtils.join(clusterMap.get(connect), ',') + "\n");
		}
		
	}
		
}
