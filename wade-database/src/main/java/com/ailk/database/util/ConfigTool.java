package com.ailk.database.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ConfigTool
 * @description: 3DES加解密配置工具
 * 
 * @version: v1.0.0
 * @author: zhoulin2@asiainfo-linkage.com
 * @date: 2013-9-21
 */
public final class ConfigTool {

	public static void main(String[] args) throws Exception {
		if (0 == args.length) {
			printUsage();
			System.exit(1);
		}

		if (args[0].equals("-f")) {
			if (args.length != 2) {
				printUsage();
				System.exit(1);
			}

			String filename = args[1];
			
			InputStream ins = new FileInputStream(filename);
			Properties props = new Properties();
			props.load(ins);

			Set<String> keys = props.stringPropertyNames();
			for (String key : keys) {
				String clearText = props.getProperty(key);
				String encrypt = TripleDES.encrypt(clearText);
				System.out.printf("%-20s -> {3DES}%s\n", key, encrypt);
			}

		} else {
			for (String clearText : args) {
				String encrypt = TripleDES.encrypt(clearText);
				System.out.printf("%-20s -> {3DES}%s\n", clearText, encrypt);
			}
		}
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("  java com.ailk.database.util.ConfigTool MINGWEN");
		System.out.println("  java com.ailk.database.util.ConfigTool -f 批量加密.properties");
		System.out.println();
		System.out.println("批量加密.properties 格式:");
		System.out.println("USERNAME1=USERNAME1_PASSWORD");
		System.out.println("USERNAME2=USERNAME2_PASSWORD");
		System.out.println("USERNAME3=USERNAME3_PASSWORD");
	}

}
