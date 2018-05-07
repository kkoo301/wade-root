package com.ailk.common.util.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ailk.common.util.Utility;

public class ExcelConfig {
	
	public static final int MAX_ROWS_SIZE = 65536;
	public static final int MAX_COLUMN_SIZE = 256;
	public static final int MAX_SHEET_SIZE = 256;
	
	public static final int MAX_ROWS_SIZE_07 = 1048576;
	public static final int MAX_COLUMN_SIZE_07 = 16384;
	
	public static final int excel_03 = 3 ;
	public static final int excel_07 = 7 ;
	public static final int excel_07_advance = 8;
	
	public final static String CELL_TYPE_STRING 	 = "1";
	public final static String CELL_TYPE_NUMERIC 	 = "2";
	public final static String CELL_TYPE_DATETIME 	 = "3";
	public final static String CELL_TYPE_PSPT	 	 = "4";
	public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	
	// 配置文件池
	public static final Map<String,List> excel_config_cache = Collections.synchronizedMap(new HashMap<String,List>());
	
	private ExcelConfig() {}
	
	/**
	 * 读取excel配置文件
	 * @param excelCfg excel配置文件相对路径(如：export/examples/basic/VipcustList.xml)
	 * @return
	 * @throws Exception
	 */
	public static List getSheets(String excelCfg) throws Exception {
		List sheets = excel_config_cache.get(excelCfg);
		if(sheets == null){
			InputStream in = Utility.getClassResourceStream(excelCfg);
			SAXReader reader = new SAXReader();
		    Document document = reader.read(in);
			in.close();
			Element book = document.getRootElement();
			sheets = book.elements();
			excel_config_cache.put(excelCfg, sheets);
		}
		return sheets;
	}
}
