package com.ailk.search;

import com.ailk.org.apache.commons.lang3.StringUtils;

public class Tester2 {
	
	private static final String KEYWORD_SEPARATOR = " ";
	
	public static void main(String[] args) {
		System.out.println(addStopToken("中华人民共和国               湖南长沙市天心区       长沙化工机械厂"));
	}
	
	private static String addStopToken(final String keywords) {
		
		if (StringUtils.isBlank(keywords)) {
			throw new IllegalArgumentException("搜索关键字不能为空！");
		}

		StringBuilder sb = new StringBuilder(500);
		String[] words = StringUtils.split(keywords);
		for (String word : words) {
			int len = word.length();
			int step = len > 2 ? 3 : 2;
			for (int i = step; i <= 3; i++) {
				for (int j = i; j <= len; j++) {
					sb.append(word.substring(j - i, j));
					sb.append(KEYWORD_SEPARATOR);
				}
			}			
		}

		return sb.toString();
	}
}
