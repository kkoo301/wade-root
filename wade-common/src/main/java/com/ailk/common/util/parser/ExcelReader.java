package com.ailk.common.util.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.IDealData;

public class ExcelReader {

	/**
	 * 解析Excel(03,07)文件返回数据(验证成功和失败)
	 * IDataset[] right = data.get("right")
	 * IDataset[] error = data.get("error")
	 * int rightCount = data.getInt("rightCount");
	 * int errorCount = data.getInt("errorCount");
 	 * @param sheets 配置文件信息
 	 * @param input 导入文件输入流
	 * @param pos_x excel空列
	 * @param pos_y excel空行
 	 * @return IData(String,IDataset[])
  	 * @throws Exception
	 */
	public static IData readExcelToData(List sheets, InputStream input, IDealData dealData, int pos_x, int pos_y) throws Exception{
		Workbook workbook = WorkbookFactory.create(input);
		IData[] sheetInfos = new IData[sheets.size()]; //add by xiedx 2014/3/17 加入sheet属性数据
		IDataset[] rightDatasets = new IDataset[sheets.size()];
		IDataset[] errorDatasets = new IDataset[sheets.size()];
		
		int rightCount = 0;
		int errorCount = 0;
		SimpleDateFormat sdf = new SimpleDateFormat();
		
		int maxRows = isExcel03(input) ? ExcelConfig.MAX_ROWS_SIZE : ExcelConfig.MAX_ROWS_SIZE_07;
		int oldPos_x = pos_x;
		int oldPos_y = pos_y;
		
		for (int i = 0, size = sheets.size(); i < size; i++) {
			Sheet worksheet = workbook.getSheetAt(i);
			rightDatasets[i] = new DatasetList();
			errorDatasets[i] = new DatasetList();
			
			Element sheet = (Element) sheets.get(i);
			sheetInfos[i] = ExcelCommon.getSheetAttrs(sheet);//add by xiedx 2014/3/17 加入sheet属性数据
			
			String sheetName = sheet.attributeValue("desc");
			if (dealData != null) {
				dealData.begin(sheetName, sheetInfos[i]);
			}
			
			pos_x = ExcelCommon.checkAndReturnPosX(sheet, oldPos_x);
   			pos_y = ExcelCommon.checkAndReturnPosY(sheet, oldPos_y);
			
			Element header = ((Element) sheets.get(i)).element("header");
			boolean isshow = Boolean.valueOf(header.attributeValue("isshow")).booleanValue();
			
			List cells = header.elements();
			int rowSize = worksheet.getPhysicalNumberOfRows() + pos_y;
			int startRow = ExcelCommon.getStartRow(header, pos_y);
			for (int j = startRow; j < rowSize; j++) {
				if (j == startRow && isshow) continue;
				
				if (j > maxRows) {
					break ;
				}
				Row workrow = worksheet.getRow(j);
				if (workrow == null) {
					rowSize++;
					continue;
				}
				
				IData data = new DataMap();
				StringBuilder error = new StringBuilder();
				int cellSize = cells.size() + pos_x;
				for (short k = (short)pos_x; k < cellSize; k++) {
					Element cell = (Element) cells.get(k-pos_x);
					Cell workcell = workrow.getCell(k);
					if (!ExcelCommon.getCellData(cell, data, error, sdf, workcell, null)) {
						continue;
					}
				}
				if (dealData != null && !dealData.execute(data, StringUtils.isBlank(error.toString()), error.toString())) {
					return null;
				} else if (data.size() != 0) {
					data.put("IMPORT_RESULT", String.valueOf(error.length() == 0));
					data.put("IMPORT_ERROR", error.toString());
					int rowNum = j + 1;
					data.put("ROW_NUM", "" + rowNum);  //需要使用字符串值 xiedx 2015/11/25
					if(error.length() == 0){
						rightDatasets[i].add(data);
						rightCount++;
					}else{
						if (errorCount == 0) {
							data.put("WADE_TRANSFORM_ERROR_DATA", true);
						}
						errorDatasets[i].add(data);
						errorCount++;
					}
				}
			}
			if (dealData != null) {
				dealData.end(sheetName);
			}
		}
		
		if (dealData != null) {
			dealData.over();
		}
		
		
		IData data = new DataMap();
		data.put("sheet", sheetInfos); //add by xiedx 2014/3/17
		data.put("right", rightDatasets);
		data.put("error", errorDatasets);
		data.put("rightCount", rightCount);
		data.put("errorCount", errorCount);
		return data;
	}
	
	/**
	 * 是否03版及以下
	 * @param input
	 * @return
	 */
	public static boolean isExcel03(InputStream input){
		boolean re = false;
		try {
			if(POIFSFileSystem.hasPOIFSHeader(input)) {
				re = true ;
			}
		} catch (IOException e) {
			re = false;
		}
		return re;
	}
	
	/**
	 * 是否07版及以上
	 * @param input
	 * @return
	 */
	public static boolean isExcel07(InputStream input){
		boolean re = false;
		try {
			if(POIXMLDocument.hasOOXMLHeader(input)) {
				re = true ;
			}
		} catch (IOException e) {
			re = false;
		}
		return re;
	}

	public static IData readExcelToData(List sheets, InputStream input, int pos_x, int pos_y) throws Exception{
		return readExcelToData(sheets, input, null, pos_x, pos_y);
	}
	
	public static void main(String[] args)throws Exception {
		//test  read
		InputStream is = new FileInputStream("C:/Users/lvchao/Desktop/TestWrite07_4.xlsx");
		
		FileInputStream in = new FileInputStream(new File("C:/Users/lvchao/Desktop/testWriteExcel.xml"));
		SAXReader reader = new SAXReader();
	    Document document = reader.read(in);
		in.close();
		Element book = document.getRootElement();
		List sheets = book.elements();
		
  		IData data = readExcelToData(sheets, is, null, 1, 3);
  		IDataset[] rightDatasets = (IDataset[])data.get("right");
		IDataset[] errorDatasets = (IDataset[])data.get("error");
		System.out.println(data);
		
	}
}
