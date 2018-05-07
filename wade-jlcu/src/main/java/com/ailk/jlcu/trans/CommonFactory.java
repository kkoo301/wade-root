package com.ailk.jlcu.trans;

import java.lang.reflect.Constructor;
import java.util.Map;

import com.ailk.jlcu.EngineFactory;
import com.ailk.jlcu.util.JlcuConfig;
import com.ailk.jlcu.util.JlcuMessages;
import com.ailk.jlcu.util.JlcuUtility;

/**
 * 输入输出的数据转换使用单实例来处理
 * 因此不能存在可修改的全局变量
 */
public class CommonFactory {
	
	private static ICommonDo commonDo;
	private static ICommonDo commonSubDo;
	
	public static ICommonDo getCommonDo(Map databus) throws Exception {
		if (null != commonDo) {
			return commonDo;
		} else {
			String commonClass = JlcuConfig.getCommonClass();
			if (null == commonClass) {
				return null;
			}
			Class clazz = Class.forName(commonClass);
			if (EngineFactory.checkExistField(clazz)) {
				JlcuUtility.error(clazz.getName()+JlcuMessages.INSTANCE_EXIST_FIELD);
			}
			
			Constructor cons = clazz.getDeclaredConstructor(Map.class);
			cons.setAccessible(true);
			commonDo = (ICommonDo)cons.newInstance(databus);
			return commonDo;
		}
	}
	
	public static ICommonDo getCommonSubDo(Map databus) throws Exception {
		if (null != commonSubDo) {
			return commonSubDo;
		} else {
			String commonSubClass = JlcuConfig.getCommonSubClass();
			if (null == commonSubClass) {
				return null;
			}
			Class clazz = Class.forName(commonSubClass);
			if (EngineFactory.checkExistField(clazz)) {
				JlcuUtility.error(clazz.getName() + JlcuMessages.INSTANCE_EXIST_FIELD);
			}
			Constructor cons = clazz.getDeclaredConstructor(Map.class);
			cons.setAccessible(true);
			commonSubDo = (ICommonDo)cons.newInstance(databus);
			return commonSubDo;
		}
	}
}
