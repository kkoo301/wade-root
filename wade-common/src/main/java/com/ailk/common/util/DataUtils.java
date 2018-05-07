/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月17日
 * 
 * Just Do IT.
 */
package com.ailk.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.ArrayUtils;

/**
 * @description 数据格式的常用判断类API
 */
public final class DataUtils {

	/**
	 * 判断对象数组是否为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(Object[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断字符串数组是否为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(String[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断数组是否为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(long[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断数组是否为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(char[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断数组是否为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(int[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断数组是否为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(short[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断数组是否为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(boolean[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断String对象是否为NULL或空串
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		return null == string || string.trim().length() == 0;
	}

	/**
	 * 判断IDataset对象是否为NULL或为空
	 * 
	 * @param dataset
	 * @return
	 */
	public static boolean isEmpty(IDataset dataset) {
		return null == dataset || dataset.isEmpty();
	}

	/**
	 * 判断List对象是否为NULL或为空
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<?> list) {
		return null == list || list.isEmpty();
	}

	/**
	 * 判断Map对象是否为NULL或为空
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return null == map || map.isEmpty();
	}

	/**
	 * 判断Set对象是否为NULL或为空
	 * 
	 * @param set
	 * @return
	 */
	public static boolean isEmpty(Set<?> set) {
		return null == set || set.isEmpty();
	}

	/**
	 * 判断IData对象是否为NULL或为空
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isEmpty(IData data) {
		return null == data || data.isEmpty();
	}

	/**
	 * 判断对象数组是否不为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static <T> boolean isNotEmpty(T[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 判断字符串数组是否不为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isNotEmpty(String[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 判断数组是否不为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isNotEmpty(long[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 判断数组是否不为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isNotEmpty(char[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 判断数组是否不为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isNotEmpty(int[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 判断数组是否不为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isNotEmpty(short[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 判断数组是否不为NULL或为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isNotEmpty(boolean[] array) {
		return ArrayUtils.isNotEmpty(array);
	}

	/**
	 * 判断String对象是否不为NULL或空串
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNotEmpty(String string) {
		return null != string && string.trim().length() > 0;
	}

	/**
	 * 判断IDataset对象是否不为NULL或为空
	 * 
	 * @param dataset
	 * @return
	 */
	public static boolean isNotEmpty(IDataset dataset) {
		return null != dataset && dataset.size() > 0;
	}

	/**
	 * 判断List对象是否不为NULL或为空
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNotEmpty(List<?> list) {
		return null != list && list.size() > 0;
	}

	/**
	 * 判断Map对象是否不为NULL或为空
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		return null != map && map.size() > 0;
	}

	/**
	 * 判断Set对象是否不为NULL或为空
	 * 
	 * @param set
	 * @return
	 */
	public static boolean isNotEmpty(Set<?> set) {
		return null != set && set.size() > 0;
	}

	/**
	 * 判断IData对象是否不为NULL或为空
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isNotEmpty(IData data) {
		return null != data && data.size() > 0;
	}
	
	
	/**
	 * 验证List是否包括NULL对象
	 * @param list
	 * @return
	 */
	public static boolean containNull(List<Object> list) {
		for (Object obj: list) {
			if (null == obj) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 验证Map是否有NULL对象的值
	 * @param data
	 * @return
	 */
	public static boolean containNull(Map<Object, Object> data) {
		for (Map.Entry<Object, Object> item : data.entrySet()) {
			if (null == item.getValue())
				return false;
		}
		return true;
	}
	

	/**
	 * 遍历Collection对象<br>
	 * <code>
	 * 示例：遍历IDataset里的每个IData对象，执行put操作<br>
	 * IDataset list = each(ds, new DataEacher<IDataset, IData>(ds, ds) {<br>
	 * public void run(IDataset rtn, IData item) {<br>
	 *			item.put("NAME", "D");<br>
	 *		}<br>
	 *	});<br>
	 * </code>
	 * @param source
	 *            支持IDataset, List等
	 * @param eacher
	 *            需要自定义实现
	 * @return 返回遍历后的RETURN对象
	 */
	public static <RETURN, ITEM> RETURN each(Collection<?> source, DataEacher<RETURN, ITEM> eacher) {
		return eacher.each();
	}

}
