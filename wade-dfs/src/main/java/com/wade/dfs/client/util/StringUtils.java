package com.wade.dfs.client.util;

import java.util.ArrayList;
import java.util.List;

import com.wade.dfs.client.proto.DFSFile;

public final class StringUtils {

	public static final int INDEX_NOT_FOUND = -1;
	
    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
	
	/**
	 * 按指定字符分割字符串
	 * 
	 * @param str
	 * @param separatorChar
	 * @return
	 */
	public static final String[] split(String str, char separatorChar) {
		
		if (null == str) {
			return null;
		}

		int len = str.length();
		if (0 == len) {
			return new String[0];
		}

		List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		boolean match = false;

		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match) {
					list.add(str.substring(start, i));
					match = false;
				}
				start = ++i;
				continue;
			}

			match = true;
			i++;
		}

		if (match) {
			list.add(str.substring(start, i));
		}

		return list.toArray(new String[list.size()]);
	}
	
    /**
     * <p>Strips any of a set of characters from the start of a String.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.stripStart(null, *)          = null
     * StringUtils.stripStart("", *)            = ""
     * StringUtils.stripStart("abc", "")        = "abc"
     * StringUtils.stripStart("abc", null)      = "abc"
     * StringUtils.stripStart("  abc", null)    = "abc"
     * StringUtils.stripStart("abc  ", null)    = "abc  "
     * StringUtils.stripStart(" abc ", null)    = "abc "
     * StringUtils.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND) {
                start++;
            }
        }
        return str.substring(start);
    }
	
	public static final DFSFile parse(String dfsFileName) {
		
		if (isBlank(dfsFileName)) {
			throw new IllegalArgumentException("dfsFileName could not be blank! dfsFileName: " + dfsFileName);
		}
		
		dfsFileName = stripStart(dfsFileName, "/");
		
		int i = dfsFileName.indexOf('/');
		
		if (INDEX_NOT_FOUND == i) {
			throw new IllegalArgumentException("dfsFileName pattern is invalid! dfsFileName: " + dfsFileName);
		}
		
		String group = dfsFileName.substring(0, i);
		String localtion  = dfsFileName.substring(i + 1);
		
		DFSFile file = new DFSFile();
		file.setGroup(group);
		file.setLocaltion(localtion);
		
		return file;
	}
	
	
	public static void main(String[] args) {
		System.out.println(parse("////CRM01/M00/00/11/asdfasdfasdfadsf.java"));
	}
}
