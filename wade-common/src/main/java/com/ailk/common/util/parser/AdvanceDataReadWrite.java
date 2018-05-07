/**  
*
* Copyright: Copyright (c) 2014 Asiainfo-Linkage
*
*/
package com.ailk.common.util.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.IDealData;
import com.ailk.common.util.IResultData;
import com.ailk.common.util.Utility;


/**
 * 用于大数据量的Excel文件处理,避免内存占用过高的情况
 * 仅支持07版及其以上的excel
 * 
 * @className:AdvanceDataReadWrite.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2014-5-14 
 */
public class AdvanceDataReadWrite {
	
	/**
	* 创建excel
	* @param excelConfig
	* @param modelPath
	* @param sources 可以为ResultSet 或  IResultData的实现类
	* @param pos_x
	* @param pos_y
	* @param cacheSize
	* 
	* return 返回值标记 是否创建创建文件成功
	*/
	public static boolean createExcel(String excelConfig, String modelPath, IResultData[] sources, IDealData dealAction, OutputStream out, int pos_x, int pos_y, int cacheSize) {
		if (cacheSize <= 0) {
			cacheSize = 100;
		}
		boolean createSafe = true;
		InputStream excelModelStream = null;
		XSSFWorkbook xssfWb = null;
		SXSSFWorkbook workbook = null;
		try {
			List sheets = ExcelConfig.getSheets(excelConfig);
			//导出模板
			if(modelPath != null && !"".equals(modelPath)){
				excelModelStream = Utility.getClassResourceStream(modelPath);
			}
			boolean hasTemplate = false;
			if (excelModelStream != null) {
				xssfWb = new XSSFWorkbook(excelModelStream);
				hasTemplate = true;
			} else {
				xssfWb = new XSSFWorkbook();
			}
			workbook = new SXSSFWorkbook(xssfWb, cacheSize);
			int sheetCount = sheets.size() > sources.length ? sources.length : sheets.size();
			int oldPos_x = pos_x;
			int oldPos_y = pos_y;
			DataFormat format = xssfWb.createDataFormat();
			Font font = ExcelCommon.createDefaultFont(xssfWb, ExcelConfig.excel_07);
			
			for (int i = 0; i < sheetCount; i++) {
				Element sheet = (Element) sheets.get(i);
				String sheetName = sheet.attributeValue("desc");
				if (dealAction != null) {
					dealAction.begin(sheetName, ExcelCommon.getSheetAttrs(sheet));
				}
				IResultData source = sources[i];
				if (source == null) continue;
				int lastRowNum = 0;
				if (hasTemplate) {
					lastRowNum = xssfWb.getSheetAt(i).getLastRowNum();
				}
				boolean notCancel = buildSheetData(workbook, sheetName, sheet, source, 
								dealAction, format, font, cacheSize, lastRowNum, oldPos_x, oldPos_y);
				if (!notCancel) {
					createSafe = false;
					source.close();
					break;
				}
				source.close();
				if (dealAction != null) {
					dealAction.end(sheetName);
				}
			}
			
			if (dealAction != null) {
				dealAction.over();
			}
			if (createSafe) {
				workbook.write(out);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utility.error(e);
			createSafe = false;
		} finally {
			if (excelModelStream != null) {
				try {
					excelModelStream.close();
				} catch (IOException e) {
					Utility.error(e);
				}
			}
			if (xssfWb != null) {
				xssfWb.unLockRevision();
				xssfWb.unLockStructure();
				xssfWb.unLockWindows();
			}
			if (workbook != null) {
				((SXSSFWorkbook)workbook).dispose();
			}
		}
		return createSafe;
	}
	
	public static void readExcel(String excelCfg, String excelPath, IDealData dealData, int pos_x, int pos_y) throws Exception {
		new AdvanceReadHandler().process(excelCfg, excelPath, dealData, pos_x, pos_y);
	}
	
	public static void readExcel(List sheets, File excelFile, IDealData dealData, int pos_x, int pos_y) throws Exception {
		new AdvanceReadHandler().process(sheets, excelFile, dealData, pos_x, pos_y);
	}
	
	private static boolean buildSheetData(Workbook workbook, String worksheetName, Element sheet, IResultData source, 
			IDealData dealAction, DataFormat format, Font font, int cacheSize, int lastRowNum, int oldPos_x, int oldPos_y) throws Exception {
		int pos_x = ExcelCommon.checkAndReturnPosX(sheet, oldPos_x);
		int pos_y = ExcelCommon.checkAndReturnPosY(sheet, oldPos_y);
		Sheet worksheet = workbook.getSheet(worksheetName);
		if (worksheet == null) {
			worksheet = workbook.createSheet(worksheetName);
		}
		Element header = sheet.element("header");
		List<Element> cells = header.elements();
		int curRows = ExcelCommon.dealHeader(header, cells, workbook, worksheet, lastRowNum, pos_x, pos_y, ExcelConfig.excel_07_advance);
		List styles = new ArrayList();
		IData data = new DataMap();
		Boolean isFirstRow = null;
		while (source.hasNext()) {
			isFirstRow = (isFirstRow == null) ? true : false;
			for (Element cell : cells) {
				String cellName = cell.attributeValue("name");
				data.put(cellName, source.get(cellName));
			}
			
			// 导出数据回调方法
			if (dealAction != null && !dealAction.execute(data, true, null)) {
				return false;
			}
			
			ExcelCommon.fillRowData(workbook, worksheet, data, cells, styles, format, curRows++, 
					pos_x, isFirstRow, font, false, ExcelConfig.excel_07_advance);
			
			if (curRows >= ExcelConfig.MAX_ROWS_SIZE_07) {
				XSSFWorkbook oriWorkbook = ((SXSSFWorkbook)workbook).getXSSFWorkbook();
				int curSheetIndex = workbook.getSheetIndex(worksheet);
				String oldSheetName = worksheet.getSheetName();
				String newSheetName = ExcelCommon.createSheetNameByOldName(oldSheetName);
				
				worksheet = workbook.cloneSheet(curSheetIndex);
				int newSheetIndex = ++curSheetIndex;
				String newCloneSheetName = worksheet.getSheetName();
				
				ExcelCommon.orderSheet(workbook, newCloneSheetName, newSheetName, newSheetIndex);
				if (dealAction != null) {
					dealAction.end(oldSheetName);
					dealAction.begin(newSheetName, ExcelCommon.getSheetAttrs(sheet));
				}
				return buildSheetData(workbook, newSheetName, sheet, source, dealAction, format, font, cacheSize, lastRowNum, oldPos_x, oldPos_y);
			}
		}
		return true;
	}
	

	public static void main(String[] args) throws Exception {
		String excelConfig = "C:/Users/lvchao/Desktop/testWriteExcel.xml";
		String modelPath = "C:/Users/lvchao/Desktop/excelModel.xlsx";
		//FileOutputStream output = new FileOutputStream(new File("C:/Users/lvchao/Desktop/TestWrite07_4.xlsx"));
		FileInputStream input = new FileInputStream(new File("C:/Users/lvchao/Desktop/TestWrite07_4.xlsx"));
		AdvanceDataReadWrite write = new AdvanceDataReadWrite();
		
		IResultData data1 = new IResultData() {
			
			int i = 0;
			@Override
			public boolean hasNext() {
				if (i < 100 * 4) {
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public Object get(String columnName) {
				return i++;
			}
			
			@Override
			public void close() {
				i = 0;
			}
			
			public long getCount() {
				return 100;
			}
		};
		
		IDealData dealData = new IDealData() {
			int i = 0;
			String sheetName = null;
			@Override
			public void over() {
				
			}
			
			@Override
			public boolean execute(IData data, boolean right, String info) {
				if ("开发任务导出_3".equals(sheetName)) {
					if (i < 10) {
						System.out.println(" data :" + data.size() + "==" + data.toString());
					}
					if (i == 10) {
						return false;					
					}
				}
				i++;
				return true;
			}
			
			@Override
			public void end(String sheetName) {
				System.out.println(sheetName + ":" + i);
				i = 0;
			}
			
			@Override
			public void begin(String sheetName, IData sheetAttrs) {
				this.sheetName = sheetName;
				System.out.println("sheetName :" + sheetName);
			}

			@Override
			public boolean execute(Object[] data, boolean right, String info) {
				return false;
			}
			
			public long getCount() {
				return 100;
			}
		};
		IData params = new DataMap();
		ImpExpUtil.beginExportAdvanceExcel("fileSerializeId", params, "fileName",new IResultData[] {data1}, dealData);
		/*write.createExcel(excelConfig, modelPath, new IResultData[]{data1, data1, data1}, null, output, 0, 0, 0);
		
		if (true) {
			return;
		}*/
		
		InputStream excelModelStream =  new FileInputStream(new File(modelPath));
		FileInputStream in = new FileInputStream(new File(excelConfig));
		SAXReader reader = new SAXReader();
	    Document document = reader.read(in);
		in.close();
		Element book = document.getRootElement();
		List sheets = book.elements();
		AdvanceReadHandler handler = new AdvanceReadHandler();
		
		File file = new File("C:/Users/lvchao/Desktop/TestWrite07_4.xlsx");
		handler.process(sheets, file, dealData ,0 , 4);
		//write.readExcel(sheets, input, dealData, 0, 0, ExcelConfig.excel_07);
	}
}
