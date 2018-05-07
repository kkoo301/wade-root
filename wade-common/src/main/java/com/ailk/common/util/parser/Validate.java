package com.ailk.common.util.parser;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

import com.ailk.common.util.Utility;
import com.ailk.common.config.CodeCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public final class Validate {
	
	/**
	 * check length
	 * @param value
	 * @param length
	 * @param desc
	 * @return String
	 */
	public static final String checkLength(String value, int length, String desc) {
		if (!"".equals(value) && getLength(value) != length) {
			return desc + CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkLength") + length + ";";
		}
		return "";
	}
	
	/**
	 * check min length
	 * @param value
	 * @param length
	 * @param desc
	 * @return String
	 */
	public static final String checkMinLength(String value, int length, String desc) {
		if (!"".equals(value) && getLength(value) < length) {
			return desc + CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkMinLength") + length + ";";
		}
		return "";
	}
	
	/**
	 * check max length
	 * @param value
	 * @param length
	 * @param desc
	 * @return String
	 */
	public static final String checkMaxLength(String value, int length, String desc) {
		if (!"".equals(value) && getLength(value) > length) {
			return desc + CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkMaxLength") + length + ";";
		}
		return "";
	}
	
	/**
	 * check text
	 * @param value
	 * @param desc
	 * @return String
	 */
	public static final String checkText(String value, String desc) {
		if (StringUtils.isBlank(value)) {
			return desc + CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkText");
		}
		return "";
	}
	
	/**
	 * check numeric
	 * @param value
	 * @param format
	 * @param desc
	 * @return String
	 */
	public static String checkNumeric(String value, String format, String desc) {
		String expression = "[+-]?\\d+";
		String checkdesc = CodeCfg.getProperty("com.ailk.common.util.parser.Validate.mustint");
		if (format != null && format.indexOf(".") != -1) {
			expression = "[+-]?\\d+(\\.\\d{1," + (format.length() - format.indexOf(".") - 1) + "})?";
			checkdesc = CodeCfg.getProperty("com.ailk.common.util.parser.Validate.mustnum")+"(" + format + ")";
		}
		if (!"".equals(value) && !Utility.isMatches(value, expression)) {
			return desc + checkdesc + ";";
		}
		return "";
	}
	
	/**
	 * check date
	 * @param value
	 * @param format
	 * @param desc
	 * @return String
	 */
	public static String checkDate(String value, String format, String desc) {
		String expression = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})";
		
		if ("yyyy-MM-dd".equals(format)) {
			expression = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})";
		} else if ("yyyy-MM-dd HH:mm".equals(format)) {
			expression = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2}) (\\d{1,2}):(\\d{1,2})";
		} else if ("yyyy-MM-dd HH:mm:ss".equals(format)) {
			expression = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2}) (\\d{1,2}):(\\d{1,2}):(\\d{1,2})";
		} else if ("HH:mm:ss".equals(format)) {
			expression = "(\\d{1,2})(:)?(\\d{1,2})\\2(\\d{1,2})";
		} else if ("yyyy".equals(format)) {
			expression = "(\\d{1,4})";
		} else if ("yyyy-MM".equals(format)) {
			expression = "(\\d{1,4})(-|\\/)(\\d{1,2})";
		} else if ("HH".equals(format)) {
			expression = "(\\d{1,2})";
		} else if ("HH:mm".equals(format)) {
			expression = "(\\d{1,2})(:)?(\\d{1,2})";
		} else if ("yyyy-MM-dd HH".equals(format)) {
			expression = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2}) (\\d{1,2})";
		}
		
		if (!"".equals(value) && !Utility.isMatches(value, expression)) {
			return desc + CodeCfg.getProperty("com.ailk.common.util.parser.Validate.musttime")+"(" + format + ");";
		}
		
		return "";
	}
	
	private static IData cellConfigToMap(Element cell) {
		IData config = new DataMap();
		if (null == cell)
			return config;
		
		@SuppressWarnings("unchecked")
		Iterator<Attribute> elems = cell.attributeIterator();
		while (elems.hasNext()) {
			Attribute attr = elems.next();
			String name = attr.getName();
			config.put(name, attr.getValue());
		}
		
		return config;
	}

	
	/**
	 * verify cell
	 * @param bd
	 * @param cell
	 * @param value
	 * @return String
	 * @throws Exception
	 */
	public static String verifyCell( Element cell, String value) throws Exception {
		StringBuilder error = new StringBuilder();
		
		String type = cell.attributeValue("type");
		String desc = cell.attributeValue("desc");
		String nullable = cell.attributeValue("nullable");
		String equsize = cell.attributeValue("equsize");
		String minsize = cell.attributeValue("minsize");
		String maxsize = cell.attributeValue("maxsize");
		String format = cell.attributeValue("format");
//		String datasrc = cell.attributeValue("datasrc");
		String filter = cell.attributeValue("filter");
		
		if (nullable != null && "no".equals(nullable)) {
			error.append(checkText(value, desc));
		}
		if (equsize != null) {
			error.append(checkLength(value, Integer.parseInt(equsize), desc));
		}
		if (minsize != null) {
			error.append(checkMinLength(value, Integer.parseInt(minsize), desc));
		}
		if (maxsize != null) {
			error.append(checkMaxLength(value, Integer.parseInt(maxsize), desc));
		}
		if (ExcelConfig.CELL_TYPE_PSPT.equals(type)) {
			error.append(checkPspt(value, desc));
		}
		if (ExcelConfig.CELL_TYPE_DATETIME.equals(type)) {
			if (format != null) {
				error.append(checkDate(value, format, desc));
			}
		}
		if (ExcelConfig.CELL_TYPE_NUMERIC.equals(type)) {
			error.append(checkNumeric(value, format, desc));
		}
		
		// 添加自定义值过滤器逻辑
		if (null != filter) {
			IValueFilter vf = ValueFilterLoader.getInstance().getFilter(filter);
			if (null != vf) {
				IData config = cellConfigToMap(cell);
				error.append(vf.filter(config, value));
			} else {
				error.append("未实例化的 值过滤器[" + filter + "];");
			}
		}
		return error.toString();
	}
	
	/**
	 * 验证行中空单元格
	 * @param list
	 * @param rowMap
	 * @return
	 */
	public static String verifyRow(List<Element> list, IData rowMap){
		StringBuilder error = new StringBuilder();
		for(int i=0; i < list.size(); i++){
			Element cell = list.get(i);
			String name = cell.attributeValue("name");
			String desc = cell.attributeValue("desc");
			String nullable = cell.attributeValue("nullable");
			if (nullable != null && "no".equals(nullable)) {
				if(!rowMap.containsKey(name)){
					error.append(checkText("", desc));
				}
			}
		}
		return error.toString();
	}



    /**
     * get length
     * @param value
     * @return int
     */
    public static int getLength(String value) {
		int length = 0;
		
		char[] chars = value.toCharArray();
    	for (int i = 0; i < chars.length; i++) {
			if (((int) chars[i]) > 0x80) {
				length += 2;
			} else {
				length += 1;
			}
		}
		
		return length;
    }
	/**
	 * check pspt
	 * @param value
	 * @param desc
	 * @return 成功返回空字符串，失败则返回错误信息
	 */
	public static String checkPspt(String value, String desc) {
		String[] errors = {
				CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkPspt.error1"),
				CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkPspt.error2"),
				CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkPspt.error3"),
				CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkPspt.error4"),
				CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkPspt.error5"),
				CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkPspt.error6"),
				CodeCfg.getProperty("com.ailk.common.util.parser.Validate.checkPspt.error7")};
		if (value == null || "".equals(value))
			return desc + "(" + value + ")" + errors[4] + ";";
		if(value.length()<15){
			return desc + "(" + value + ")" + errors[1] + ";";
		}
		IData area = new DataMap();
		area.put("11", "\u5317\u4EAC");
		area.put("12", "\u5929\u6D25");
		area.put("13", "\u6CB3\u5317");
		area.put("14", "\u5C71\u897F");
		area.put("15", "\u5185\u8499\u53E4");
		area.put("21", "\u8FBD\u5B81");
		area.put("22", "\u5409\u6797");
		area.put("23", "\u9ED1\u9F99\u6C5F");
		area.put("31", "\u4E0A\u6D77");
		area.put("32", "\u6C5F\u82CF");
		area.put("33", "\u6D59\u6C5F");
		area.put("34", "\u5B89\u5FBD");
		area.put("35", "\u798F\u5EFA");
		area.put("36", "\u6C5F\u897F");
		area.put("37", "\u5C71\u4E1C");
		area.put("41", "\u6CB3\u5357");
		area.put("42", "\u6E56\u5317");
		area.put("43", "\u6E56\u5357");
		area.put("44", "\u5E7F\u4E1C");
		area.put("45", "\u5E7F\u897F");
		area.put("46", "\u6D77\u5357");
		area.put("50", "\u91CD\u5E86");
		area.put("51", "\u56DB\u5DDD");
		area.put("52", "\u8D35\u5DDE");
		area.put("53", "\u4E91\u5357");
		area.put("54", "\u897F\u85CF");
		area.put("61", "\u9655\u897F");
		area.put("62", "\u7518\u8083");
		area.put("63", "\u9752\u6D77");
		area.put("64", "\u5B81\u590F");
		area.put("65", "\u65B0\u7586");
		area.put("71", "\u53F0\u6E7E");
		area.put("81", "\u9999\u6E2F");
		area.put("82", "\u6FB3\u95E8");
		area.put("91", "\u56FD\u5916");
		
		String idcard = value, Y, JYM;
		String S, M, ereg;
		Calendar c = Calendar.getInstance();
		if (idcard.charAt(idcard.length() - 1) == '*')
			idcard = idcard.substring(0, idcard.length() - 1) + 'X';
		
		if (!area.containsKey(idcard.substring(0, 2))) {
			return desc + "(" + value + ")" + errors[4] + ";";
		}
		switch (idcard.length()) {
		case 15:
			if ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0
					|| ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 100 == 0 && (Integer
							.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0)) {
				ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";
			} else {
				ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}$";
			}
			boolean bTemp = Pattern.compile(ereg).matcher(idcard).find();
			if (bTemp) {
				Matcher matches = Pattern.compile(ereg).matcher(idcard);
				c.setTime(new java.util.Date());
				int nowY = c.get(Calendar.YEAR);
				if (matches.groupCount() > 0) {
					if (Integer.parseInt(("19" + idcard.substring(6, 8))) + 100 < nowY) {
						return desc + "(" + value + ")" + errors[5] + ";";
					}
				}
				return "";
			} else {
				return desc + "(" + value + ")" + errors[2] + ";";
			}
		case 18:
			if (Integer.parseInt(idcard.substring(6, 10)) % 4 == 0
					|| (Integer.parseInt(idcard.substring(6, 10)) % 100 == 0 && Integer
							.parseInt(idcard.substring(6, 10)) % 4 == 0)) {
				ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";
			} else {
				ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}[0-9Xx]$";
			}
			boolean bTemp18 = Pattern.compile(ereg).matcher(idcard).find();
			if (bTemp18) {
				Pattern pattern = Pattern.compile(ereg);
				Matcher matches = pattern.matcher(idcard);
				c.setTime(new java.util.Date());
				int nowY = c.get(Calendar.YEAR);
				if (matches.groupCount() > 0) {
					int iYear = Integer.parseInt(idcard.substring(6, 10));
					if ((iYear + 15) > nowY || (iYear + 100) < nowY) {
						return desc + "(" + value + ")" + errors[5] + ";";
					}
				}
				return "";
			} else {
				return desc + "(" + value + ")" + errors[2] + ";";
			}
		default:
			return desc + "(" + value + ")" + errors[2] + ";";
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		List list = ExcelConfig.getSheets("export/custmgr.xml");
		Element e = (Element) list.get(0);
		Element header = e.element("header");
		List cells = header.elements();
		
		for (int i = 0; i < cells.size(); i++) {
			Element cell = (Element) cells.get(i);
			System.out.println(verifyCell(cell, "13787135440"));
		}
	}
}