package com.ailk.common.util;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.config.CodeCfg;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;

public final class Utility {
	
	private static final Logger log = Logger.getLogger(Utility.class);

	public static final int MESSAGE_CONFIRM = 1;
	public static final int MESSAGE_WARNING = 2;
	public static final int MESSAGE_ERROR = 3;
	private static String[] chineseDigits = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
	
	private static Utility utility;

	static{
		if (utility == null) {
			utility = new Utility();
		}
	}
	
	private Utility() {}
	
	public static final void error(Throwable throwable){
		throw new BaseException(throwable);
	}
	
	public static final void error(String message, Throwable throwable){
		throw new BaseException(message, throwable);
	}
	
	public static final void error(String message, Throwable throwable, IData data){
		throw new BaseException(message, throwable, data);
	}
	
	public static final void error(String message){
		throw new BaseException(message);
	}
	
	public static final void error(String code, String[] params, String message){
		throw new BaseException(code,params,message);
	}
	
	public static final void error(String code, String[] params, String message, IData data){
		throw new BaseException(code, params, message, data);
	}
	
	public static final void error(String code, String[] params){
		throw new BaseException(code,params,null);
	}
	
	public static final void error(String code, String[] params, IData data){
		throw new BaseException(code, params, null, data);
	}
	
	public static final void abort(String message){
		throw new BaseException(message);
	}

	public static final void abort(String code,String[] params){
		abort(code,params,null);
	}
	
	public static final void abort(String code,String[] params, String message){
		throw new BaseException(code,params,message);
	}
	
	/**
	 * get class resource stream
	 * @param file
	 * @return InputStream
	 * @throws Exception
	 */
	public static final InputStream getClassResourceStream(String file){
		InputStream in = utility.getClass().getClassLoader().getResourceAsStream(file);
		if (in == null) error("file " + file + " not exist!");
		return in;
	}
	
	/**
	 * get class resource
	 * @param file
	 * @return URL
	 * @throws Exception
	 */
	public static final URL getClassResource(String file){
		URL url = utility.getClass().getClassLoader().getResource(file);
		if (url == null) error("file " + file + " not exist!");
		return url;
	}
	
	
	/**
	 * get uniqe name
	 * @return String
	 * @throws Exception
	 */
	public static final String getUniqeName() throws Exception {
		return String.valueOf(System.currentTimeMillis()) + Math.abs(new Random().nextInt());
	}
	
	/***
	 * 获取应用目录
	 * @param name
	 * @return
	 */
	public static final String getDomainPath(String name) {
		String path = utility.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (StringUtils.isBlank(name))
			return path;
		
		String[] dirs = StringUtils.split(name, '/');
		
		String pathname = "";
		boolean find = true;
		for (int i = 0; i < dirs.length; i++) {
			int index = path.indexOf(dirs[i]);
			
			if (index != -1 && find) {
				find = true;
				pathname = path.substring(0, index + dirs[i].length() + 1);
			} else {
				find = false;
				if (i == 0) return "";
				File file = new File(pathname + dirs[i]);
				if (file.exists())
					pathname = pathname + dirs[i] + "/";
				else
					return "";
			}
		}
		return pathname;
	}
	
	public static final String getClassRoot(String name) {
		return utility.getClass().getClassLoader().getResource(name).getPath();
	}
	
	
	/**
	 * get timestamp format
	 * @param value
	 * @return String
	 */
	public static final String getTimestampFormat(String value) {
		switch (value.length()) {
			case 4:
				return "yyyy";
			case 6:
				return "yyyyMM";
			case 7:
				return "yyyy-MM";
			case 8:
				return "yyyyMMdd";
			case 10:
				return value.indexOf("/") == 4 ? "yyyy/MM/dd" : "yyyy-MM-dd";
			case 13:
				return "yyyy-MM-dd HH";
			case 14:
				return "yyyyMMddHHmmss";
			case 16:
				return "yyyy-MM-dd HH:mm";
			case 19:
				return "yyyy-MM-dd HH:mm:ss";
			case 21:
				return "yyyy-MM-dd HH:mm:ss.S";
		}
		return null;
	}
	
	public static final IData getURLParams(String queryString){
		if (StringUtils.isBlank(queryString)) 
			return null;
		
		int idx = queryString.lastIndexOf("?");
		if (idx > -1) {
			queryString = queryString.substring(idx);
		}

		String[] queryStringSplit = StringUtils.split(queryString, '&');
        IData params = new DataMap();
        
        for (String qs : queryStringSplit) {
        	if (StringUtils.isNotBlank(qs)) {
        		idx = qs.indexOf("=");
        		if (idx > -1) {
        			String name = qs.substring(0, idx);
        			String value = qs.substring(idx + 1, qs.length());
        			params.put(name, value);
        		}
        	}
        }
        return params;
	}

	/**
	 * get match str
	 * @param str
	 * @param regex
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	public static final String getMatchStr(String str, String regex) {
		List result = getMatchArray(str, regex);
		return result.size() == 0 ? null : (String) result.get(0);
	}
	
	/**
	 * get match array
	 * @param str
	 * @param regex
	 * @return List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final List getMatchArray(String str, String regex) {
		List result = new ArrayList();
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			result.add(matcher.group());
		}
		
		return result;
	}
	
	/**
	 * is matches
	 * @param str
	 * @param regex
	 * @return boolean
	 */
	public static final boolean isMatches(String str, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * trim prefix
	 * @param str
	 * @param suffix
	 * @return String
	 */
	public static final String trimPrefix(String str, String prefix) {
		return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
	}
	
	/**
	 * trim suffix
	 * @param str
	 * @param suffix
	 * @return String
	 */
	public static final String trimSuffix(String str, String suffix) {
		return str.endsWith(suffix) ? str.substring(0, str.length() - 1) : str;
	}

	/**
	 * 
	 * @param array
	 * @return String
	 */
	public static final String getStrByArray(Object[] array) {
		return getStrByArray(array, ",");
	}
	
	/**
	 * 
	 * @param array
	 * @param separator
	 * @return String
	 */
	public static final String getStrByArray(Object[] array, String separator) {
		return StringUtils.join(array, separator);
	}
	
	/**
	 * encode timestamp
	 * @param timeStr
	 * @return Timestamp
	 * @throws Exception
	 */
	public static final Timestamp encodeTimestamp(String timeStr) throws Exception {
		String format = getTimestampFormat(timeStr);
		return encodeTimestamp(format, timeStr);
	}
	
	/**
	 * encode timestamp
	 * @param format
	 * @param timeStr
	 * @return Timestamp
	 * @throws Exception
	 * modified by caom on 08.7.28, check timeStr is null
	 */
	public static final Timestamp encodeTimestamp(String format, String timeStr) throws Exception {
		if (StringUtils.isBlank(timeStr)) return null;
		if (format.length() != timeStr.length()) format = getTimestampFormat(timeStr);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return new Timestamp(sdf.parse(timeStr).getTime());
	}
	
	/**
	 * decode timestamp
	 * @param format
	 * @param timeStr
	 * @return String
	 * @throws Exception
	 */
	public static final String decodeTimestamp(String format, String timeStr) throws Exception {
		Timestamp time = encodeTimestamp(format, timeStr);
		return decodeTimestamp(format, time);
	}
	
	/**
	 * decode timestamp
	 * @param format
	 * @param time
	 * @return String
	 * @throws Exception
	 */
	public static final String decodeTimestamp(String format, Timestamp time) throws Exception {
		if (time == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}

	/**
	 * get current time
	 * @return Timestamp
	 * @throws Exception
	 */
	public static final Timestamp getCurrentTime() throws Exception {
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * get sys time
	 * @return String
	 * @throws Exception
	 */
	public static final String getSysTime() throws Exception {
		return decodeTimestamp("yyyy-MM-dd HH:mm:ss", new Timestamp(System.currentTimeMillis()));
	}
	
	/**
	 * get sys date
	 * @return String
	 * @throws Exception
	 */
	public static final String getSysDate() throws Exception {
		return decodeTimestamp("yyyy-MM-dd", new Timestamp(System.currentTimeMillis()));
	}
	
	/**
	 * get last day
	 * @return String
	 * @throws Exception
	 */
	public static final String getLastDay() throws Exception {
		return getLastDay(getSysDate());
	}
	
	/**
	 * get last day
	 * @return String
	 * @throws Exception
	 */
	public static final String getLastDay(String timestr) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(encodeTimestamp(timestr));
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		return dateformat.format(cal.getTime());
	}

	/**
	 * get prev day by curr date
	 * @return String
	 * @throws Exception
	 */
	public static final String getPrevDayByCurrDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		return dateformat.format(cal.getTime());
	}
	
	/**
	 * format decimal
	 * @param format <"#.##(mentisia lack ignore)、0.00(appoint mentisia，lack add 0>"
	 * @param decimal
	 * @return String
	 * @throws Exception
	 */
	public static final String formatDecimal(String format, double decimal) throws Exception {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(decimal);
	}

	 /**
     * get char length
     * @param value
     * @return String
     */
    public static final int getCharLength(String value) {
    	char[] chars = value.toCharArray();
    	
    	int charlen = 0;
    	for (int i = 0; i < chars.length; i++) {
    		if ((int) chars[i] > 0x80) {
    			charlen += 2;
    		} else {
    			charlen += 1;
    		}
    	}
    	
    	return charlen;
    }

	
    /**
     * get char length
     * @param value
     * @param length
     * @return String
     */
    public static final int getCharLength(String value, int length) {
    	char[] chars = value.toCharArray();
    	//liaos 20090916 getArrayByCodingStr; note if
    	//if (chars.length < length) length = chars.length;
    	
    	int charidx = 0, charlen = 0;
    	
    	//liaos 20090916 getArrayByCodingStr; update while
    	//while (charlen < length)
    	while (charlen < length && charidx < chars.length) {
    		if ((int) chars[charidx] > 0x80) {
    			charlen += 2;
    		} else {
    			charlen += 1;
    		}
    		charidx ++;
    	}
    	
    	return charidx;
    }
    
    /**
	 * to chinese money
	 * @param money
	 * @return
	 * @throws Exception
	 */
	public static final String toChineseMoney(String money) throws Exception {
		if (money == null) return null;
		int index = money.indexOf(".");
		if (index == -1) {
			money = money + ".0";
			return amountToChinese(Double.parseDouble(money));
		} else {
			String decimal = money.substring(index + 1);
			if (decimal.length() >= 2) money = money.substring(0, index + 3);
			return amountToChinese(Double.parseDouble(money));
		}
	}
	
	
	/**
     * 把金额转换为汉字表示的数量，小数点后四舍五入保留两位
     * @param amount
     * @return
     */
    public static final String amountToChinese(double amount) throws Exception {

        if(amount > 9999999999999.999 || amount < -9999999999999.999)
        	error("参数值超出允许范围 (-9999999999999.999 ～ 9999999999999.999)！");

        boolean negative = false;
        if(amount < 0) {
            negative = true;
            amount = amount * (-1);
        }

        long temp = Math.round(amount * 100);
        int numFen = (int)(temp % 10); // 分
        temp = temp / 10;
        int numJiao = (int)(temp % 10); //角
        temp = temp / 10;
        //temp 目前是金额的整数部分

        int[] parts = new int[20]; // 其中的元素是把原来金额整数部分分割为值在 0~9999 之间的数的各个部分
        int numParts = 0; // 记录把原来金额整数部分分割为了几个部分（每部分都在 0~9999 之间）
        for(int i=0; ; i++) {
            if(temp ==0)
                break;
            int part = (int)(temp % 10000);
            parts[i] = part;
            numParts ++;
            temp = temp / 10000;
        }

        boolean beforeWanIsZero = true; // 标志“万”下面一级是不是 0

        String chineseStr = "";
        for(int i=0; i<numParts; i++) {

            String partChinese = partTranslate(parts[i]);
            if(i % 2 == 0) {
                if("".equals(partChinese))
                    beforeWanIsZero = true;
                else
                    beforeWanIsZero = false;
            }

            if(i != 0) {
                if(i % 2 == 0)
                    chineseStr = "亿" + chineseStr;
                else {
                    if("".equals(partChinese) && !beforeWanIsZero)   // 如果“万”对应的 part 为 0，而“万”下面一级不为 0，则不加“万”，而加“零”
                        chineseStr = "零" + chineseStr;
                    else {
                        if(parts[i-1] < 1000 && parts[i-1] > 0) // 如果"万"的部分不为 0, 而"万"前面的部分小于 1000 大于 0， 则万后面应该跟“零”
                            chineseStr = "零" + chineseStr;
                        chineseStr = "万" + chineseStr;
                    }
                }
            }
            chineseStr = partChinese + chineseStr;
        }

        if("".equals(chineseStr))  // 整数部分为 0, 则表达为"零元"
        {
        	if (negative)
        		chineseStr = "负" + chineseDigits[0];
        	else
        		chineseStr = chineseDigits[0];
        } else if(negative) // 整数部分不为 0, 并且原金额为负数
            chineseStr = "负" + chineseStr;

        chineseStr = chineseStr + "元";

        if(numFen == 0 && numJiao == 0) {
            chineseStr = chineseStr + "整";
        }
        else if(numFen == 0) { // 0 分，角数不为 0
            chineseStr = chineseStr + chineseDigits[numJiao] + "角";
        }
        else { // “分”数不为 0
            if(numJiao == 0)
                chineseStr = chineseStr + "零" + chineseDigits[numFen] + "分";
            else
                chineseStr = chineseStr + chineseDigits[numJiao] + "角" + chineseDigits[numFen] + "分";
        }

        return chineseStr.replaceAll("亿万", "亿");
    }


    /**
     * 把一个 0~9999 之间的整数转换为汉字的字符串，如果是 0 则返回 ""
     * @param amountPart
     * @return
     */
    private static final String partTranslate(int amountPart) {

        if(amountPart < 0 || amountPart > 10000) {
            throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！");
        }


        String[] units = new String[] {"", "拾", "佰", "仟"};

        int temp = amountPart;

        String amountStr = new Integer(amountPart).toString();
        int amountStrLength = amountStr.length();
        boolean lastIsZero = true; //在从低位往高位循环时，记录上一位数字是不是 0
        String chineseStr = "";

        for(int i=0; i<amountStrLength; i++) {
            if(temp == 0)  // 高位已无数据
                break;
            int digit = temp % 10;
            if(digit == 0) { // 取到的数字为 0
                if(!lastIsZero)  //前一个数字不是 0，则在当前汉字串前加“零”字;
                    chineseStr = "零" + chineseStr;
                lastIsZero = true;
            }
            else { // 取到的数字不是 0
                chineseStr = chineseDigits[digit] + units[i] + chineseStr;
                lastIsZero = false;
            }
            temp = temp / 10;
        }
        return chineseStr;
    }
	
	/** 
	 * get array by coding str
	 * @param namestr
	 * @param encodestr
	 * @return IDataset
	 * @throws Exception
	 */
	public static final IDataset getArrayByCodingStr(String namestr, String encodestr) throws Exception {
		String[] encodename = namestr.split(",");
		
		int tablen = 4, rowlen = 4, collen = 3;
		int rows = Integer.parseInt(encodestr.substring(tablen, tablen + rowlen));
		String content = encodestr.substring(tablen + rowlen + collen);
		
		IDataset dataset = new DatasetList();
		for (int i=0; i<rows; i++) {
			IData data = new DataMap();
			for (int j=0; j<encodename.length; j++) {
				int namelen = Integer.parseInt(content.substring(0, 4));
				content = content.substring(4);
				int vallen = getCharLength(content, namelen);
				String value = content.substring(0, vallen);
				content = content.substring(vallen);
				data.put(encodename[j], value);
			}
			dataset.add(data);
		}
		
		return dataset;
	}
	

	/**
	 * total dataset by column
	 * @param dataset
	 * @param column
	 * @param format
	 * @return String
	 * @throws Exception
	 */
	public static final String totalDatasetByColumn(IDataset dataset, String column, String format) throws Exception {
		return totalDatasetByColumn(dataset, column, format, 1);
	}
	
	/**
	 * total dataset by column
	 * @param dataset
	 * @param column
	 * @param format
	 * @param scale
	 * @return String
	 * @throws Exception
	 */
	public static final String totalDatasetByColumn(IDataset dataset, String column, String format, int scale) throws Exception {
		double total = 0;
		
		if (dataset != null) {
			for (int i = 0, size = dataset.size(); i < size; i++) {
				IData data = (IData) dataset.get(i);
				
				String value = (String) data.get(column);
				if (value == null || "".equals(value)) continue;
				
				total += Double.parseDouble(value);
			}
		}
		
		return formatDecimal(format, total / scale);
	}
	

	/**
	 * get values
	 * @param value
	 * @return String[]
	 * @throws Exception
	 */
	public static final String[] getValues(Object value) throws Exception {
		if (value == null) return new String[] {};
		if (value instanceof String[]) {
			return (String[]) value;
		} else {
			return new String[] { (String) value } ;
		}
	}
	
	
	/**
	 * get host address
	 * @return String
	 * @throws Exception
	 */
	public static final String getHostAddress() throws Exception {
		return InetAddress.getLocalHost().getHostAddress();		
	}
	
	/**
	 * equalsNVL
	 * @param obj1
	 * @param obj2
	 * @return boolean
	 */
	public static final boolean equalsNVL(Object obj1, Object obj2) {
		if ((obj1 == null) && (obj2 == null)) {
			return true;
		}
		if ((obj1 != null) && (obj2 != null) && obj1.equals(obj2)) {
			return true;
		}
		return false;
	}

	/**
	 * hashCodeNVL
	 * @param o
	 * @return int
	 */
	public static final int hashCodeNVL(Object o) {
		if (o == null) {
			return 0;
		}
		return o.hashCode();
	}
	
	
	/**
	 * get statck trace
	 * @param e
	 * @return
	 */
	public static final String getStackTrace(Throwable e) {
		return getStackTrace(e, 0);
	}
	
	/**
	 * get statck trace
	 * @param e
	 * @param maxLength
	 * @return
	 */
	public static final String getStackTrace(Throwable e, int maxLength) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String str = sw.toString();
		if (maxLength == 0) return str;
		
		int charLength = getCharLength(str, maxLength);
		return str.substring(0, charLength);
	}
	
	/**
	 * get bottom exception
	 * @param exception
	 * @return Throwable
	 */
	public static final Throwable getBottomException(Throwable exception) {
		if (exception == null) return null;
		
		if (exception.getCause() != null) {
			return getBottomException(exception.getCause());
		}
		
		return exception;
	}
	
	
	public static final String parseExceptionMessage(Throwable exception) {
		String version = GlobalCfg.getProperty("exception.version", "wade");
		if ("wade".equals(version)) {
			return parseExceptionMessageWade(exception);
		} else if ("aif".equals(version)) {
			return parseExceptionMessageAif(exception);
		}
		return null;
	}
	
	
	/**
	 * csf exception message
	 * @param exception
	 * @return
	 */
	private static final String parseExceptionMessageAif(Throwable exception) {
		
		if (log.isDebugEnabled()) {
			log.debug("异常信息MSG:" + exception.getMessage(), exception);
			log.debug("异常信息MSG结束");
		}
		
		Throwable bottom = getBottomException(exception);
		if (bottom instanceof BaseException) {
			
			//BaseException be = (BaseException) bottom;
			//return be.getCode() + BaseException.INFO_SPLITE_CHAR + be.getInfo();
			
			//xiedx 2016/9/3 BaseException使用Wade方式解析
			return parseExceptionMessageWade(exception);
		}
		
		if (bottom instanceof NullPointerException) {
			StringWriter sw = new StringWriter();
			bottom.printStackTrace(new ErrorPrintWriter(sw));
			String stack = sw.toString();
			
			int index = stack.indexOf("java.lang.NullPointerException");
			stack = stack.substring(index);
			index = stack.indexOf(")");
			
			return "" + BaseException.INFO_SPLITE_CHAR + "空指针异常:" + stack.substring("java.lang.NullPointerException".length(), index + 1);
		}
		
		String message = bottom.getMessage();
		//CsfException
		if (null == message || message.length() <= 0) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new ErrorPrintWriter(sw));
			message = sw.toString();
		}
		//exceptionCode=CRM_ORDER_940, exceptionMessage=当前用户没, exceptionStack=CsfException
		int index = message.indexOf("exceptionCode");
		if (index != -1) {
			int msgIdx = message.indexOf("exceptionMessage");
			int stackIdx = message.indexOf("exceptionStack");
			String code = message.substring(index + "exceptionCode=".length(), msgIdx - 2);
			String msg = message.substring(msgIdx + "exceptionMessage=".length(), stackIdx - 2);
			return code + BaseException.INFO_SPLITE_CHAR + msg;
		}
		
		return bottom.getMessage();
	}
	
	
	/**
	 * parse exception message to code:message
	 * @param exception
	 * @return
	 */
	private static final String parseExceptionMessageWade(Throwable exception) {
		String message = getBottomException(exception).getMessage();
		
		//Caused by: xxxxException: msg
		if (StringUtils.isBlank(message)) {
			log.error("No Message Exception", exception);
			return "-88:空指针异常";
		}
		
		if (message.startsWith("baseexception:")) {
			message = message.substring(14);
		} else if (message.startsWith("ClassNotFoundException:")) {
			int index = message.lastIndexOf("ClassNotFoundException:");
			message = "找不到类文件:" + message.substring(index + "ClassNotFoundException:".length()).trim();
		} else {
			int index = message.lastIndexOf("Exception:");
			if (index != -1) 
				message = message.substring(index + "Exception:".length()).trim();
			
			index = message.lastIndexOf("$Enhance_");
			if (index != -1) {
				//message = message.substring(index).trim();
				return CodeCfg.getProperty("com.ailk.common.util.Utility.syntax") + message;
			}
			
			index = message.lastIndexOf("->");
			if (index != -1)
				message = message.substring(index + 2).trim();
			
			if (StringUtils.isBlank(message)) 
				return "no message";
			
			/*index = message.indexOf(":");
			if (index != -1)
				message = StringUtils.replaceOnce(message, ":", BaseException.SPLITE_CHART);*/
		}
		
		message = message.replaceAll(",", " ");
		
		String[] msgs = message.split(BaseException.SPLITE_CHART);

		int msglength = msgs.length;
		if (msglength >= 2) {
			String code = msgs[0];
			String msg = msgs[1];
			
			//xiedx CodeCfg中找不到匹配值时保留原始参数信息  2016/9/3
			String codeMsg = CodeCfg.getProperty(code, msglength > 2 ? msgs[2].split(BaseException.SPLITE_PARAM_CHART) : null, "");
			if(codeMsg != null && !"".equals(codeMsg.trim())){
				return code + BaseException.INFO_SPLITE_CHAR + codeMsg;
			}else{
				//在第三段保留code解析的入参 xiedx
				return code + BaseException.INFO_SPLITE_CHAR + msg + ( msglength > 2 ? BaseException.INFO_SPLITE_CHAR + msgs[2] : "" );
			}
			//return code + BaseException.INFO_SPLITE_CHAR + (msglength > 2 ? CodeCfg.getProperty(code,msgs[2].split(BaseException.SPLITE_PARAM_CHART), msg) : msg);
		} else {
			return message;
		}
	}
	
	
	/**
	 * get data by str
	 * @param str
	 * @return
	 */
	public static final IData getDataByStr(String str) {
		IData data = new DataMap();
		
		StringTokenizer st = new StringTokenizer(str, "&");
		while (st.hasMoreElements()) {
			String[] source = StringUtils.split((String) st.nextElement(), '=');
			data.put(source[0], source[1]);
		}
		
		return data;
	}
	
	public static final String encodeCharset(String charSet){
		try {
			return new String(charSet.getBytes(GlobalCfg.getCharset()), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new BaseException("Utility-10001", e);
		}
	}
	
	public static final String buildFilePath(String prefix ,String filePath){
		if (StringUtils.isNotBlank(filePath)) {
			if (!filePath.startsWith(prefix)) {
				if (filePath.indexOf("./") >= 0) {
					Utility.error("Param filePath includes illegal characters[" + filePath + "]!");
				}
				if (filePath.charAt(0) == '/') {
					filePath = prefix + filePath;
				} else {
					filePath = prefix + "/" + filePath;
				}
			}
		} else {
			filePath = prefix;
		}
		return filePath;
	}
	
	/**
	 * 打印异常信息
	 * @param ex 异常对象
	 */
	public static final void print(Exception ex){
		print(null, ex);
	}
	
	/**
	 * 打印异常信息
	 * @param prefix 特定前缀
	 * @param ex 异常对象
	 */
	public static final void print(String prefix, Exception ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new ErrorPrintWriter(sw));
		String stack = sw.toString();
		
		log.error( (prefix != null ? prefix + " " : "") + System.getProperty("wade.server.name", "") + " " + System.currentTimeMillis() + " " + stack);
	}
	
	/**
     * 将标准ip转换为整形 
     * @param ip
     * @return
     */
	public static int ipToInt(String ip) {
		if(ip == null || ip.indexOf(":") > -1)   //for ipv6
			return -1;
		
		String[] ips = ip.split("\\.");
		int ipLength = ips.length;
		if (ipLength < 4) {
			com.ailk.common.util.Utility.error("Ip is error, ip is " + ip
					+ " ; ip.length = " + ips.length);
		}
		return (Integer.parseInt(ips[0]) << 24)
				| (Integer.parseInt(ips[1]) << 16)
				| (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
	}
	
	static class ErrorPrintWriter extends PrintWriter {
		/**
		 * @param out
		 */
		public ErrorPrintWriter(Writer out) {
			super(out);
		}
		
		@Override
		public void println(String x) {
			super.println(x);
		}
		
	}

}
