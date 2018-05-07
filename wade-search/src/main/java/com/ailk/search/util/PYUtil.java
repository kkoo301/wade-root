package com.ailk.search.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.StaticFactory;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: PYUtil
 * @description: 拼音工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class PYUtil {
	
	private static final transient Logger LOG = Logger.getLogger(PYUtil.class);
	
	/**
	 * 将中文转英文首字母（小写）
	 * 
	 * @param chinese
	 * @return
	 * @deprecated
	 */
	public static String parseToPinYinHeadChar(String chinese) {
		
		if (StringUtils.isBlank(chinese)) {
			return "";
		}
		
		StringBuilder headchars = new StringBuilder();
		for (int i = 0, len = chinese.length(); i < len; i++) {
			
			char word = chinese.charAt(i);

			String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(word);
			if (null != pinyin) {
				headchars.append(pinyin[0].charAt(0));
			} else {
				headchars.append(word);
			}
		}
		
		return headchars.toString().toLowerCase();
	}
	
	/**
	 * 将中文转英文首字母，支持多音字（小写）
	 * 
	 * @param chinese
	 * @return
	 */
	public static String parseToPinYinHeadChar2(String chinese) {
		
		if (StringUtils.isBlank(chinese)) {
			return "";
		}

		if (50 < chinese.length()) {
			LOG.debug("字段长度超过50位! 字段: " + chinese);
			chinese = chinese.substring(0, 50);
		}
		
		LinkedList<String> groups = new LinkedList<String>();
		
		for (int i = 0, len = chinese.length(); i < len; i++) {
			
			char word = chinese.charAt(i);

			Set<Character> headchars = toPinyinHeadchars(word);
			if (0 == headchars.size()) {
				continue;
			}
			
			if (0 == groups.size()) {
				for (char headchar : headchars) {
					groups.addLast(headchar + "");
				}
			} else {
				for (int k = 0, size = groups.size(); k < size; k++) {
					
					String pinyin = groups.removeFirst();
					for (char headchar : headchars) {
						groups.addLast(pinyin + headchar);
					}
				}
			}
		}
		
		StringBuilder rtn = new StringBuilder();
		for (String group : groups) {
			rtn.append(group);
			rtn.append(StaticFactory.KEYWORD_SEPARATOR);
		}
		
		return rtn.toString().toLowerCase();
	}
	
	/**
	 * 将单个汉字转换成拼音首字母，并排重
	 * 
	 * @param c
	 * @return
	 */
	private static Set<Character> toPinyinHeadchars(char c) {
		Set<Character> rtn = new HashSet<Character>();
		String[] headchars = PinyinHelper.toHanyuPinyinStringArray(c);
		if (null != headchars) {
			for (String headchar : headchars) {
				rtn.add(headchar.charAt(0));
			}	
		} else {
			rtn.add(c);
		}
		
		return rtn;
	}
	
	public static void main(String[] args) {
		
		System.out.println("--- start ---");
		//System.out.println(parseToPinYinHeadChar2("乐单好"));
		//System.out.println(parseToPinYinHeadChar2("兴民小区光交覆盖：9栋1单元、9栋2单元、10栋1单元、10栋2单元、11栋1单元、11栋2单元、11栋3单元、12栋1单元、12栋2单元、12栋3单元、13栋、14栋1单元、14栋2单元、15栋1单元、15栋2单元、16栋、17栋、18栋1单元、18栋2单元"));
		System.out.println(parseToPinYinHeadChar2("中国北京北京平谷区-建设西街-林荫家园10号楼"));
		System.out.println("--- end ---");
		
	}
}
