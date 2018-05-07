/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月22日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.bo;

import java.text.SimpleDateFormat;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.database.orm.err.BOError;
import com.ailk.database.orm.err.BOException;

/**
 * @description 兼容保留
 */
public class DataType {
	/**
	 * 数据类型
	 */
	public static final String DATATYPE_STRING = "String";
	public static final String DATATYPE_SHORT = "Short";
	public static final String DATATYPE_INTEGER = "Integer";
	public static final String DATATYPE_LONG = "Long";
	public static final String DATATYPE_DOUBLE = "Double";
	public static final String DATATYPE_FLOAT = "Float";
	public static final String DATATYPE_BYTE = "Byte";
	public static final String DATATYPE_CHAR = "Char";
	public static final String DATATYPE_BOOLEAN = "Boolean";
	public static final String DATATYPE_DATE = "Date";
	public static final String DATATYPE_TIME = "Time";
	public static final String DATATYPE_DATETIME = "DateTime";
	public static final String DATATYPE_OBJECT = "Object";

	public static final String DATATYPE_short = "short";
	public static final String DATATYPE_int = "int";
	public static final String DATATYPE_long = "long";
	public static final String DATATYPE_double = "double";
	public static final String DATATYPE_float = "float";
	public static final String DATATYPE_byte = "byte";
	public static final String DATATYPE_char = "char";
	public static final String DATATYPE_boolean = "boolean";

	@SuppressWarnings("rawtypes")
	public static Object transfer(Object value, Class type) throws BOException {
		if (value == null)
			return null;
		if ((value instanceof String) && (value.toString().trim().equals(""))) {
			if (String.class.equals(type)) // add for obd
				return value;
			else
				return null;
		}

		if (type.equals(Short.class) || type.equals(short.class)) {
			if (value instanceof Short)
				return value;
			else
				return new Short(new java.math.BigDecimal(value.toString()).shortValue());
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			if (value instanceof Integer)
				return value;
			else
				return new Integer(new java.math.BigDecimal(value.toString()).intValue());
		} else if (type.equals(Character.class) || type.equals(char.class)) {
			if (value instanceof Character)
				return value;
			else
				return new Character(value.toString().charAt(0));
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			if (value instanceof Long)
				return value;
			else
				return new Long(new java.math.BigDecimal(value.toString()).longValue());
		} else if (type.equals(String.class)) {
			if (value instanceof String)
				return value;
			else
				return value.toString();
		} else if (type.equals(java.sql.Date.class)) {
			if (value instanceof java.sql.Date)
				return value;
			else if (value instanceof java.util.Date)
				return new java.sql.Date(((java.util.Date) value).getTime());
			else {
				try {
					SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd");
					return new java.sql.Date(a.parse(value.toString()).getTime());
				} catch (Exception e) {
					throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type.getName()), e);
				}
			}
		} else if (type.equals(java.sql.Time.class)) {
			if (value instanceof java.sql.Time)
				return value;
			else if (value instanceof java.util.Date)
				return new java.sql.Time(((java.util.Date) value).getTime());
			else {
				try {
					SimpleDateFormat a = new SimpleDateFormat("HH:mm:ss");
					return new java.sql.Time(a.parse(value.toString()).getTime());
				} catch (Exception e) {
					throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type.getName()), e);
				}
			}
		} else if (type.equals(java.sql.Timestamp.class)) {
			if (value instanceof java.sql.Timestamp)
				return value;
			else if (value instanceof java.util.Date)
				return new java.sql.Timestamp(((java.util.Date) value).getTime());
			else {
				try {
					SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String tmpstr = value.toString();
					if (tmpstr.trim().length() <= 10)
						tmpstr = tmpstr + " 00:00:00";
					return new java.sql.Timestamp(a.parse(tmpstr).getTime());
				} catch (Exception e) {
					throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type.getName()), e);
				}
			}
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			if (value instanceof Double)
				return value;
			else
				return new Double(new java.math.BigDecimal(value.toString()).doubleValue());
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			if (value instanceof Float)
				return value;
			else
				return new Float(new java.math.BigDecimal(value.toString()).floatValue());
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			if (value instanceof Byte)
				return value;
			else
				return new Byte(new java.math.BigDecimal(value.toString()).byteValue());
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			if (value instanceof Boolean)
				return value;
			else if (value instanceof java.lang.Number) {
				if (((Number) value).doubleValue() > 0)
					return new Boolean(true);
				else
					return new Boolean(false);
			} else if (value instanceof String) {
				if (((String) value).equalsIgnoreCase("true") || ((String) value).equalsIgnoreCase("y"))
					return new Boolean(true);
				else
					return new Boolean(false);
			} else {
				throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type.getName()));
			}
		} else {
			return value;
		}
	}

	public static Object transfer(Object value, String type) throws BOException {
		if (value == null)
			return null;
		if ((value instanceof String) && (value.toString().trim().equals(""))) {
			if (DATATYPE_STRING.equalsIgnoreCase(type)) // add for obd
				return value;
			else
				return null;
		}

		if (type.equalsIgnoreCase(DATATYPE_SHORT) || type.equalsIgnoreCase(DATATYPE_short)) {
			if (value instanceof Short)
				return value;
			else
				return new Short(new java.math.BigDecimal(value.toString()).shortValue());
		} else if (type.equalsIgnoreCase(DATATYPE_INTEGER) || type.equalsIgnoreCase(DATATYPE_int)) {
			if (value instanceof Integer)
				return value;
			else
				return new Integer(new java.math.BigDecimal(value.toString()).intValue());
		} else if (type.equalsIgnoreCase(DATATYPE_CHAR) || type.equalsIgnoreCase(DATATYPE_char)) {
			if (value instanceof Character)
				return value;
			else
				return new Character(value.toString().charAt(0));
		} else if (type.equalsIgnoreCase(DATATYPE_LONG) || type.equalsIgnoreCase(DATATYPE_long)) {
			if (value instanceof Long)
				return value;
			else
				return new Long(new java.math.BigDecimal(value.toString()).longValue());
		} else if (type.equalsIgnoreCase(DATATYPE_STRING)) {
			if (value instanceof String)
				return value;
			else
				return value.toString();
		} else if (type.equalsIgnoreCase(DATATYPE_DATE)) {
			if (value instanceof java.sql.Date)
				return value;
			else if (value instanceof java.sql.Timestamp)
				return new java.sql.Date(((java.sql.Timestamp) value).getTime());
			else {
				try {
					String tmpstr = value.toString().replace('/', '-');
					SimpleDateFormat DATA_FORMAT_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
					return new java.sql.Date(DATA_FORMAT_yyyyMMdd.parse(tmpstr).getTime());
				} catch (Exception e) {
					throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type), e);
				}
			}
		} else if (type.equalsIgnoreCase(DATATYPE_TIME)) {
			if (value instanceof java.sql.Time)
				return value;
			else if (value instanceof java.sql.Timestamp)
				return new java.sql.Time(((java.sql.Timestamp) value).getTime());
			else {
				try {
					SimpleDateFormat DATA_FORMAT_HHmmss = new SimpleDateFormat("HH:mm:ss");
					return new java.sql.Time(DATA_FORMAT_HHmmss.parse(value.toString()).getTime());
				} catch (Exception e) {
					throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type), e);
				}
			}
		} else if (type.equalsIgnoreCase(DATATYPE_DATETIME)) {
			if (value instanceof java.sql.Timestamp)
				return value;
			else if (value instanceof java.util.Date)
				return new java.sql.Timestamp(((java.util.Date) value).getTime());
			else {
				try {
					SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String tmpstr = value.toString();
					if (tmpstr.trim().length() <= 10)
						tmpstr = tmpstr + " 00:00:00";
					return new java.sql.Timestamp(a.parse(tmpstr).getTime());
				} catch (Exception e) {
					throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type), e);
				}
			}
		} else if (type.equalsIgnoreCase(DATATYPE_DOUBLE) || type.equalsIgnoreCase(DATATYPE_double)) {
			if (value instanceof Double)
				return value;
			else
				return new Double(new java.math.BigDecimal(value.toString()).doubleValue());
		} else if (type.equalsIgnoreCase(DATATYPE_FLOAT) || type.equalsIgnoreCase(DATATYPE_float)) {
			if (value instanceof Float)
				return value;
			else
				return new Float(new java.math.BigDecimal(value.toString()).floatValue());
		} else if (type.equalsIgnoreCase(DATATYPE_BYTE) || type.equalsIgnoreCase(DATATYPE_byte)) {
			if (value instanceof Byte)
				return value;
			else
				return new Byte(new java.math.BigDecimal(value.toString()).byteValue());
		} else if (type.equalsIgnoreCase(DATATYPE_BOOLEAN) || type.equalsIgnoreCase(DATATYPE_boolean)) {
			if (value instanceof Boolean)
				return value;
			else if (value instanceof java.lang.Number) {
				if (((Number) value).doubleValue() > 0)
					return new Boolean(true);
				else
					return new Boolean(false);
			} else if (value instanceof String) {
				if (((String) value).equalsIgnoreCase("true") || ((String) value).equalsIgnoreCase("y"))
					return new Boolean(true);
				else
					return new Boolean(false);
			} else {
				throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(type));
			}
		} else // 可能存在潜在的问题
			return value;

		// throw new AIException("没有找到数据类型：" + type.toString());
	}

	public static String getAsString(Object obj) {
		if (obj == null)
			return null;
		else
			return obj.toString();
	}

	public static short getAsShort(Object obj) throws BOException {
		if (obj == null) // 对象为空返回0
			return 0;
		if (obj instanceof Number)
			return ((Number) obj).shortValue();
		else
			return ((Short) transfer(obj, Short.class)).shortValue();
	}

	public static int getAsInt(Object obj) throws BOException {
		if (obj == null) // 对象为空返回0
			return 0;
		if (obj instanceof Number)
			return ((Number) obj).intValue();
		else
			return ((Integer) transfer(obj, Integer.class)).intValue();
	}

	public static long getAsLong(Object obj) throws BOException {
		if (obj == null) // 对象为空返回0
			return 0;
		if (obj instanceof Number)
			return ((Number) obj).longValue();
		else {
			return ((Long) transfer(obj, Long.class)).longValue();
		}
	}

	public static double getAsDouble(Object obj) throws BOException {
		if (obj == null) // 对象为空返回0
			return 0;
		if (obj instanceof Number)
			return ((Number) obj).doubleValue();
		else
			return ((Double) transfer(obj, Double.class)).doubleValue();
	}

	public static float getAsFloat(Object obj) throws BOException {
		if (obj == null) // 对象为空返回0
			return 0;
		if (obj instanceof Number)
			return ((Number) obj).floatValue();
		else
			return ((Float) transfer(obj, Float.class)).floatValue();
	}

	public static byte getAsByte(Object obj) throws BOException {
		if (obj == null) // 对象为空返回0
			return 0;
		if (obj instanceof Number)
			return ((Number) obj).byteValue();
		else
			return ((Byte) transfer(obj, Byte.class)).byteValue();
	}

	public static boolean getAsBoolean(Object obj) throws BOException {
		if (obj == null) // 对象为空返回false
			return false;
		if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();
		else
			return ((Boolean) transfer(obj, Boolean.class)).booleanValue();
	}

	public static char getAsChar(Object obj) throws BOException {
		if (obj == null) // 对象为空返回false
			return 0;
		if (obj instanceof Character)
			return ((Character) obj).charValue();
		else if ((obj instanceof String) && (((String) obj).length() == 1)) {
			return ((String) obj).charAt(0);
		} else
			return ((Character) transfer(obj, Character.class)).charValue();
	}

	/**
	 * 将三种日期格式的字符串转换成java.sql.Date：<br>
	 * 1.yyyy-MM-dd<br>
	 * 2.yyyy-MM-dd HH:mm:ss<br>
	 * 3.yyyy-MM-dd HH:mm:ss.S<br>
	 * @param datestr
	 * @return
	 * @throws BOException
	 */
	public static java.sql.Date strToSqlDate(String datestr) throws BOException {
		int len = datestr.length();
		try {
			if (len == 10) {
				return new java.sql.Date(DateUtils.parseDate(datestr, new String[] { "yyyy-MM-dd" }).getTime());
			}
			if (len == 19) {
				return new java.sql.Date(DateUtils.parseDate(datestr, new String[] { "yyyy-MM-dd HH:mm:ss" }).getTime());
			}
			if (len == 21) {
				return new java.sql.Date(DateUtils.parseDate(datestr, new String[] { "yyyy-MM-dd HH:mm:ss.S" }).getTime());
			}
			throw new Exception("日期格式长度不规范，请使用yyyy-MM-dd或yyyy-MM-dd HH:mm:ss或yyyy-MM-dd HH:mm:ss.S，当前输入的值：" + datestr);
		} catch (Exception e) {
			BOException boe = new BOException(BOError.bo10012.getCode(),
					BOError.bo10012.getInfo(new Object[] { datestr }), e);
			throw boe;
		}
	}

	public static java.sql.Date getAsDate(Object obj) throws BOException {
		if (obj == null)
			return null;
		if (obj instanceof String) {
			return strToSqlDate((String)obj);
		} else if (obj instanceof java.sql.Date)
			return (java.sql.Date) obj;
		else if (obj instanceof java.sql.Timestamp) {
			return new java.sql.Date(((java.sql.Timestamp) obj).getTime());
		} else {
			throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(obj.toString()));
		}
	}

	public static java.sql.Time getAsTime(Object obj) throws BOException {
		if (obj == null)
			return null;
		if (obj instanceof String) {
			return new java.sql.Time(strToSqlDate((String)obj).getTime());
		} else if (obj instanceof java.sql.Time)
			return (java.sql.Time) obj;
		else if (obj instanceof java.sql.Timestamp) {
			return new java.sql.Time(((java.sql.Timestamp) obj).getTime());
		} else {
			throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(obj.toString()));
		}
	}

	public static java.sql.Timestamp getAsDateTime(Object obj) throws BOException {
		if (obj == null)
			return null;
		if (obj instanceof String) {
			return new java.sql.Timestamp(strToSqlDate((String)obj).getTime());
		} else if (obj instanceof java.sql.Timestamp)
			return (java.sql.Timestamp) obj;
		else if (obj instanceof java.sql.Date) {
			return new java.sql.Timestamp(((java.sql.Date) obj).getTime());
		} else {
			throw new BOException(BOError.bo10007.getCode(), BOError.bo10007.getInfo(obj.toString()));
		}
	}

}
