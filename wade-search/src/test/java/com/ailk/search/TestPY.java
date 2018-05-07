package com.ailk.search;


import java.util.LinkedList;
import java.util.List;

import com.ailk.search.util.PYUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class TestPY extends Thread {

	public static String[] getAllSpell(String chinese) {
		List list = new LinkedList();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > '?')
				try {
					String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
					if ((pinyinArr == null) || (pinyinArr.length <= 0)) {
						System.out.println(arr[i]);
					} else {
						System.out.println(pinyinArr[0]);
					}
					// dealWithMultiSpells(list, pinyinArr);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			else {
				
				// dealWithNewSpell(list, String.valueOf(arr[i]), null);
			}
		}
		int listLength = list.size();
		String[] pinyinStrs = new String[listLength];
		for (int i = 0; i < listLength; i++) {
			pinyinStrs[i] = ((StringBuilder) list.get(i)).toString();
		}
		return pinyinStrs;
	}

	public static void main(String[] args) throws Exception {
		String s = PYUtil.parseToPinYinHeadChar("热搜★百万炫铃");
		//System.out.println(s);

		String[] ss = getAllSpell("热搜★百万炫铃");
		for (String str : ss) {
			System.out.println(str);
		}
	}
}
