/**  
*
* Copyright: Copyright (c) 2014 Asiainfo-Linkage
*
*/
package com.ailk.common.util.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.dom4j.Element;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.IDealData;
import com.ailk.common.util.Utility;

/**
 * @className:AdvanceReadHandler.java
 *
 * @version V1.0
 * @author lvchao
 * @date 2014-5-24
 */
public class AdvanceReadHandler extends DefaultHandler {
    
	private SharedStringsTable sst;
	private int pos_x;
   	private int sstCount = 0;
   	private String lastContents;
   	private int dataFrom = 0; // 0:非单元格; 1: 来自引用文件; 2:来自文件自身
   	private int sheetIndex = -1;
   	private int curRow = -1;
   	private int curCol = 0;
   	private int startRow = 0;
   	private IDealData dealData;
   	private List<Element> cells;
   	private List<String> cellDatas = new LinkedList<String>();
   	private SimpleDateFormat sdf = new SimpleDateFormat();
   	private boolean cellIsNull = true; // 避免单元格为空不解析的情况
   	private String excelPath = null;

   	public void process(List sheets, File excelFile, IDealData dealData, int pos_x, int pos_y) throws Exception { 
   		this.excelPath = excelFile.getAbsolutePath();
   		OPCPackage pkg = OPCPackage.open(excelFile, PackageAccess.READ);
   		process(pkg, sheets, dealData, pos_x, pos_y);
   	}
   	/**
   	 * 读取所有工作簿的入口方法
   	 * @param path 
   	 * @throws Exception 
   	 */  
   	public void process(String excelCfg, String excelPath, IDealData dealData, int pos_x, int pos_y) throws Exception { 
	   	this.excelPath = excelPath;
	   	OPCPackage pkg = OPCPackage.open(excelPath, PackageAccess.READ);
       	List sheets = ExcelConfig.getSheets(excelCfg);
       	process(pkg, sheets, dealData, pos_x, pos_y);
   	}
   	
   	private void process(OPCPackage pkg, List sheets, IDealData dealData, int pos_x, int pos_y) throws IOException, OpenXML4JException, SAXException {
   		this.dealData = dealData;
   		int oldPos_x = pos_x;
	   	int oldPos_y = pos_y;
   		XSSFReader r = new XSSFReader(pkg);
       	SharedStringsTable sst = r.getSharedStringsTable();
       	sstCount = sst.getCount();
       	XMLReader parser = fetchSheetParser(sst);
       	Iterator<InputStream> workSheets = r.getSheetsData();
       	int i = 0;
   		while (workSheets.hasNext()) {
       		Element sheet = (Element) sheets.get(i);
    	   	Element header = sheet.element("header");
    	   	this.cells = header.elements();
    	   	this.pos_x = ExcelCommon.checkAndReturnPosX(sheet, oldPos_x);
   			pos_y = ExcelCommon.checkAndReturnPosY(sheet, oldPos_y);
			this.startRow = ExcelCommon.getStartRow(header, pos_y);
   			curRow = -1;
           	sheetIndex++;
           	IData sheetAttrs = ExcelCommon.getSheetAttrs(sheet);
			String sheetName = sheet.attributeValue("desc");
           	dealData.begin(sheetName, sheetAttrs);
           	InputStream workSheet = workSheets.next();
           	InputSource sheetSource = new InputSource(workSheet);
           	parser.parse(sheetSource);
           	workSheet.close();
           	dealData.end(sheetName);
           	i++;
       	}
       	dealData.over();
   	}
   	
   	/** 
   	 * 该方法自动被调用，每读一行调用一次，在方法中写自己的业务逻辑即可 
   	 * @param sheetIndex 工作簿序号 
   	 * @param curRow 处理到第几行 
   	 * @param rowList 当前数据行的数据集合 
   	 */  
   	public void optRow(int sheetIndex, int curRow, List<String> cellDatas) {
   		StringBuilder error = new StringBuilder();
   		IData rowData = new DataMap();
   		int cellSize = cells.size() + pos_x;
   		for (short k = (short)pos_x; k < cellSize; k++) {
   			int index = k - pos_x;
   			Element cell = (Element)cells.get(index);
   			try {
   				if (!ExcelCommon.getCellData(cell, rowData, error, sdf, null, cellDatas.get(index))) {
   					continue;
   				}
   			} catch (Exception e) {
   				e.printStackTrace();
   				Utility.error(e);
   			}
   		}
   		
   		String errorStr = error.toString();
   		boolean isRight = StringUtils.isBlank(errorStr);
   		if (!dealData.execute(rowData, isRight, errorStr)) {
   			Utility.error("解析Excel文件被终止, 此异常出现在需停止解析excel文档时! ExcelPath: " + 
   					excelPath + "; sheetIndex: " + sheetIndex + 
   					(!isRight ? "; errorInfo: " + errorStr : "") + 
   					"; rowData: " + rowData.toString());
   		}
   	}

   	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
   		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
   		this.sst = sst;
   		parser.setContentHandler(this);
   		return parser;
   	}

   	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
   		if (name.equals("row")) {
   			int rowIndex = Integer.valueOf(attributes.getValue("r"));
   			if (curRow == -1) {
   	   			curRow = rowIndex;
   	   		}
   			if (curRow < rowIndex) {
   	   			// 避免空行不计算的情况
   	   			curRow = rowIndex;
   	   		}
   		}
   		
   		if (curRow <= startRow) return;
   		// c => 单元格  
   		if (name.equals("c")) {
   			// 如果下一个元素是 SST 的索引，则将nextIsString标记为true
   			String cellType = attributes.getValue("t");
   			if (cellType != null && cellType.equals("s")) {
   				dataFrom = 1;
   			} else if (cellType != null && cellType.equals("inlineStr")) { 
   				dataFrom = 2;
   			} else {
   				dataFrom = 0;
   			}
   		}
   		// 置空  
   		lastContents = "";
   	}

   	public void endElement(String uri, String localName, String name) throws SAXException {
   		if (curRow > startRow) {
	   		// 根据SST的索引值的到单元格的真正要存储的字符串
	   		// 这时characters()方法可能会被调用多次
	   		if (dataFrom == 1) {
	   			//System.out.println(name);
	   			if (!"".equals(lastContents)) {
	   				int idx = Integer.parseInt(lastContents);
		   			if (idx < sstCount) {
		   				CTRst ctRst = sst.getEntryAt(idx);
		   				if (ctRst != null) {
		   					XSSFRichTextString richStr = new XSSFRichTextString(ctRst);
		   					if (richStr != null) {
		   						lastContents = richStr.toString();
		   					}
		   				}
		   			}
	   			}
	   			if (name.equals("v")) {
	   				cellIsNull = false;
	   				String value = lastContents.trim();
	   				value = value.equals("") ? " " : value;
	   				cellDatas.add(curCol, value);
	   				curCol++;
	   			}
	   		} else if (dataFrom == 2) {
	   			if (name.equals("t")) {
	   				cellIsNull = false;
	   				String value = lastContents.trim();
	   				value = value.equals("") ? " " : value;
	   				cellDatas.add(curCol, value);
	   				curCol++;
	   			}
	   		}
   		}
   		if (name.equals("c")) {
   			if (cellIsNull) {
   				cellDatas.add(curCol, " ");
   				curCol++;
   			} else {
   				cellIsNull = true;
   			}
   		}
   		dataFrom = 0;
   		// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
   		if (name.equals("row")) {
   			if (curRow > startRow) {
   				optRow(sheetIndex, curRow, cellDatas);
   			}
   			cellDatas.clear();
   			curRow++;
   			curCol = 0;
   		}
   	}
   	
   	public void characters(char[] ch, int start, int length) throws SAXException {
   		// 得到单元格内容的值
   		lastContents += new String(ch, start, length);
   	}
}