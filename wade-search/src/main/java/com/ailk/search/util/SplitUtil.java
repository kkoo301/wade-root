package com.ailk.search.util;

import org.apache.log4j.Logger;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SplitUtil
 * @description: 断词工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class SplitUtil {
	
	private static final transient Logger LOG = Logger.getLogger(SplitUtil.class);
	private static final String KEYWORD_SEPARATOR = " ";
	//private static final int MAX_LENGTH = Integer.parseInt(GlobalCfg.getProperty("search.index.maxlen", "100"));

	/**
	 * 索引关键词分词, 只做二，三分词
	 * 
	 * @param keyword
	 * @return
	 */
	public static final String indexKeywordSplit(String keyword) {
		
		if (StringUtils.isBlank(keyword)) {
			return "";
		}

		/*
		if (MAX_LENGTH < keyword.length()) {
			LOG.debug("关键词长度超出" + MAX_LENGTH + "限制! 关键字: " + keyword);
			keyword = keyword.substring(0, MAX_LENGTH);
		}

		StringBuilder sb = new StringBuilder(500);
		String[] words = StringUtils.split(keyword); // 先按空格分成一个个关键字
		for (String word : words) {
			int len = word.length();
			for (int step = 2, maxStep = Math.min(len, 3); step <= maxStep; step++) {
				for (int i = step; i <= len; i++) {
					sb.append(word.substring(i - step, i));
					sb.append(KEYWORD_SEPARATOR);
				}
			}
		}
		return sb.toString();
		*/

		return keywordSplit(keyword);
		
	}
	
	/**
	 * 搜索关键词分词
	 * 
	 * @param keyword
	 * @return
	 */
	public static final String searchKeywordSplit(final String keyword) {
		
		if (StringUtils.isBlank(keyword)) {
			throw new IllegalArgumentException("搜索关键字不能为空！");
		}

		/*
		StringBuilder sb = new StringBuilder(500);

		String[] words = StringUtils.split(keyword); // 先按空格分成一个个关键字
		for (String word : words) { 
			int len = word.length();
			int step = len > 2 ? 3 : 2; // 尽可能按三元分词，否则按二元分词
			for (int i = step; i <= 3; i++) {
				for (int j = i; j <= len; j++) {
					sb.append(word.substring(j - i, j));
					sb.append(KEYWORD_SEPARATOR);
				}
			}
		}
		return sb.toString();
		*/

		return keywordSplit(keyword);

	}

	private static final String keywordSplit(final String keyword) {

		StringBuilder sb = new StringBuilder(500);

//		// 一元分词
//		for (int i = 0; i < keyword.length(); i++) {
//			char c = keyword.charAt(i);
//
//			if (' ' != c && '\t' != c && '\n' != c) {
//				sb.append(c);
//				sb.append(KEYWORD_SEPARATOR);
//			}
//		}

		// 二元分词
		String[] words = StringUtils.split(keyword);
		for (String word : words) {
			for (int i = 0; i < word.length() - 1; i++) {
				sb.append(word.substring(i, i + 2));
				sb.append(KEYWORD_SEPARATOR);
			}
		}

		return sb.toString().trim();

	}

	public static void main(String[] args) {
		
		String s = "神州行长途套餐";
//		s += s;
//		s += s;
//		s += s;
//		s += s;
		s += " ";
		System.out.println(s);
		System.out.println(s.length());
		s += PYUtil.parseToPinYinHeadChar2(s);
		System.out.println(s);



		//System.out.println(indexKeywordSplit(s));
		//System.out.println(searchKeywordSplit(s));

		System.out.println(searchKeywordSplit(s));
		System.out.println(indexKeywordSplit(s));
		System.out.println(searchKeywordSplit("湖南省"));
	}
	
}
