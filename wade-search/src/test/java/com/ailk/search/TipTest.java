package com.ailk.search;



import com.ailk.org.apache.commons.lang3.StringUtils;

import com.ailk.search.StaticFactory;

public class TipTest {
	public static void main(String[] args) {
		String[] fullMatchWord = null;//new String[] {"111", "222", "333", "444"};
		String s = StringUtils.join(fullMatchWord, StaticFactory.KEYWORD_SEPARATOR);
		System.out.println(s);
	}
}
