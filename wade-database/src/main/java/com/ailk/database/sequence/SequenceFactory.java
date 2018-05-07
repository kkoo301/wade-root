package com.ailk.database.sequence;

import java.util.HashMap;
import java.util.Map;

import com.ailk.database.sequence.impl.RawSequence;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SequenceFactory
 * @description: 序列工厂
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-4-18
 */
public class SequenceFactory {

	/**
	 * 以Class为Key的序列缓存
	 */
	private static Map<Class<?>, ISequence> clazzSeqCache = new HashMap<Class<?>, ISequence>();
	
	/**
	 * 以seqName为Key的序列缓存
	 */
	private static Map<String, ISequence> nameSeqCache = new HashMap<String, ISequence>();
	
	/**
	 * 取序列的下一个值
	 * 
	 * @param connName 连接名
	 * @param clazz 序列类
	 * @return
	 * @throws Exception
	 */
	public static String nextval(String connName, Class<?> clazz) throws Exception {
		ISequence seq = getSequenceByClazz(clazz);
		return seq.getNextval(connName);
	}
	
	/**
	 * 取序列的下一个值
	 * 
	 * @param connName 连接名
	 * @param clazz 序列类
	 * @param eparchyCode 地州编码
	 * @return
	 * @throws Exception
	 */
	public static String nextval(String connName, Class<?> clazz, String eparchyCode) throws Exception {
		ISequence seq = getSequenceByClazz(clazz);
		return seq.getNextval(connName, eparchyCode);
	}

	/**
	 * 取序列的下一个值
	 * 
	 * @param connName 连接名
	 * @param seqName 序列名
	 * @return
	 * @throws Exception
	 */
	public static String nextval(String connName, String seqName) throws Exception {
		ISequence seq = getSequenceByName(seqName);
		return seq.getNextval(connName);
	}

	/**
	 * 取序列的下一个值
	 * 
	 * @param connName 连接名
	 * @param seqName 序列名
	 * @param eparchyCode 地州编码
	 * @return
	 * @throws Exception
	 */
	public static String nextval(String connName, String seqName, String eparchyCode) throws Exception {
		ISequence seq = getSequenceByName(seqName);
		return seq.getNextval(connName, eparchyCode);
	}

	/**
	 * 根据class取对应的序列缓存对象
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private static ISequence getSequenceByClazz(Class<?> clazz) throws Exception {

		ISequence seq = clazzSeqCache.get(clazz);

		while (null == seq) {
			synchronized (SequenceFactory.class) {
				seq = clazzSeqCache.get(clazz);
				if (null != seq) break;
				
				seq = (ISequence) clazz.newInstance();
				clazzSeqCache.put(clazz, seq);
			}
		}
		
		return seq;
	}
	
	/**
	 * 根据序列名取对应的序列缓存对象
	 * 
	 * @param seqName
	 * @return
	 * @throws Exception
	 */
	private static ISequence getSequenceByName(String seqName) throws Exception {
		
		ISequence seq = nameSeqCache.get(seqName);

		while (null == seq) {
			synchronized (SequenceFactory.class) {
				seq = nameSeqCache.get(seqName);
				if (null != seq) break; 
				
				seq = new RawSequence(seqName);
				nameSeqCache.put(seqName, seq);
			}
		}
		
		return seq;
	}
}