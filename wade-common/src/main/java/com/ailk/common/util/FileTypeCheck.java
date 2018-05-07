package com.ailk.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ailk.common.config.GlobalCfg;


/**
 * 根据文件头的前两个字节来判断文件类型，用在文件上传时对文件类型的限制；
 * 非法的文件类型需要在global.properties里定义，格式如下：ftp.filetype.illegal＝bat:40656368|dll,exe:4D5A900003000；
 * 文本文件只能验证文件的内容，而不能通过文件头来判断；
 * 常用类型如下
	prop.put("bat", "40656368");
	prop.put("dll,exe", "4D5A900003000");
	prop.put("jpg", "FFD8FF");
	prop.put("png", "89504E47");
	prop.put("gif", "47494638");
	prop.put("tif", "49492A00");
	prop.put("bmp", "424D");
	prop.put("dwg", "41433130");
	prop.put("html", "68746D6C3E");
	prop.put("rtf", "7B5C727466");
	prop.put("xml", "3C3F786D6C");
	prop.put("zip", "504B0304");
	prop.put("rar", "52617221");
	prop.put("psd", "38425053");
	prop.put("eml", "44656C69766572792D646174653A");
	prop.put("dbx", "CFAD12FEC5FD746F");
	prop.put("pst", "2142444E");
	prop.put("xls", "D0CF11E0");
	prop.put("doc", "D0CF11E0");
	prop.put("mdb", "5374616E64617264204A");
	prop.put("wpd", "FF575043");
	prop.put("eps", "252150532D41646F6265");
	prop.put("ps", "252150532D41646F6265");
	prop.put("pdf", "255044462D312E");
	prop.put("qdf", "AC9EBD8F");
	prop.put("pwl", "E3828596");
	prop.put("wav", "57415645");
	prop.put("avi", "41564920");
	prop.put("ram", "2E7261FD");
	prop.put("rm", "2E524D46");
	prop.put("mpg", "000001BA");
	prop.put("mov", "6D6F6F76");
	prop.put("asf", "3026B2758E66CF11");
	prop.put("mid", "4D546864");
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FileTypeCheck.java
 * @author: liaosheng
 * @date: 2015-4-25
 */
public class FileTypeCheck {
	
	private static String fileTypes = GlobalCfg.getProperty("ftp.filetype.illegal", "");
	
	
	/**
	 * 验证文件的前两个字节所指示的文件类型判断是否非法文件
	 * 返回值为-1则标识为合法文件，否则返回fileTypes的索引位置，通过getFileType(index)可获取对应的文件类型定义
	 * @param b
	 * @return
	 * @throws IOException
	 */
	public static int isLegal(final byte[] b) {
		if (null == fileTypes || fileTypes.length() == 0)
			return -1;
		String code = bytesToHexString(b);
		return fileTypes.indexOf(":" + code);
	}
	
	
	/**
	 * 根据isLegal()的返回值在fileTypes里获取对应的文件类型定义
	 * @param index
	 * @return
	 */
	public static String getFileType(int fromIndex) {
		int splitIndex = fileTypes.lastIndexOf("|", fromIndex);
		return fileTypes.substring(splitIndex + 1, fromIndex);
	}
	
	/**
	 * 字节转16进制
	 * @param src
	 * @return
	 */
	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString().toUpperCase();
	}
	
	
	/**
	 * 用来测试文件头的类型，后台将输出文件头的前两个字节编码
	 * @param filePath
	 * @throws IOException
	 */
	public static void testFileType (String filePath) throws IOException {
		File file = new File(filePath);
		FileInputStream fis = null;
		byte[] b = new byte[4];

		try {
			fis = new FileInputStream(file);
			int i = fis.read(b, 0, b.length);
			
			if (i != -1) {
				System.out.println(bytesToHexString(b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				fis.close();
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		File file = new File("d:\\Hello.exe");
		InputStream fis = null;
		byte[] b = new byte[4];

		try {
			fis = new FileInputStream(file);
			int i = fis.read(b, 0, b.length);
			
			if (i != -1) {
				int index = isLegal(b);
				if (index != -1) {
					System.out.println(getFileType(index));
				}
			}
			
			i = fis.read(b, 0, b.length);
			
			if (i != -1) {
				int index = isLegal(b);
				if (index != -1) {
					System.out.println(getFileType(index));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				fis.close();
			}
		}
		
	}
	
}
