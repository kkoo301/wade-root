package com.ailk.jlcu.util;

import org.apache.log4j.Logger;

public class JlcuUtility {
	
	public static void error(Throwable throwable) throws JlcuException {
		if (throwable instanceof JlcuException) {
			throw (JlcuException) throwable;
		} else {
			throw new JlcuException(throwable);
		}
	}

	public static void error(String message, Throwable throwable)
			throws JlcuException {
		if (throwable instanceof JlcuException) {
			JlcuException je = (JlcuException) throwable;
			je.addMessage(message);
			throw je;
		} else {
			throw new JlcuException(message, throwable);
		}
	}

	public static void error(String message) throws JlcuException {
		throw new JlcuException(message);
	}

	public static void log(Logger log, String message) {
		if (log.isDebugEnabled()) {
			int len = message.length();
			if (len >= 20) {
				message = message.substring(20);
			}
			log.debug("[JLCU]" + message);
		}
	}

	public static void log(Logger log, Throwable e) {
		log.error(e);
	}

	public static void log(Logger log, String message, Throwable e) {
		log.error(message, e);
	}
}
