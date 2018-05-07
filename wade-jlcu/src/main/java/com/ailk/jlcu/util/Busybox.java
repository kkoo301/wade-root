package com.ailk.jlcu.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

public class Busybox {
	
	private static final Logger log = Logger.getLogger(Busybox.class);
	/**
	 * 深度克隆
	 */
	public static Object deepClone(Object srcObj) {
		Object cloneObj = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(srcObj);
			oos.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			cloneObj = ois.readObject();
			ois.close();
		} catch (Exception e) {
			JlcuUtility.log(log, e);
		}

		return cloneObj;
	}
}
