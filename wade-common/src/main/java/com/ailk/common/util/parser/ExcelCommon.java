/**  
*
* Copyright: Copyright (c) 2014 Asiainfo-Linkage
*
*/
package com.ailk.common.util.parser;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Element;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.IDealData;
import com.ailk.common.util.Utility;

/**
 * @className:ExcelCommon.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2014-5-22 
 */
public class ExcelCommon {

	public static final String SHEET_DUMP_NAME_SPLIT = "__$";
	
	/**
	 * 验证数据范围，每个excel文档最多有256个表格，每个表格最多65536行、256列
	 * @param sheets
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 */
	public static boolean verifyData(List sheets, IDataset[] datasets, int pos_x, int pos_y, int version){
		int maxRows,maxColumn;
		if (version == ExcelConfig.excel_03) {
			maxRows = ExcelConfig.MAX_ROWS_SIZE;
			maxColumn = ExcelConfig.MAX_COLUMN_SIZE;
		} else {
			maxRows = ExcelConfig.MAX_ROWS_SIZE_07;
			maxColumn = ExcelConfig.MAX_COLUMN_SIZE_07;
		}
		
		boolean out = false;
		for (int i = 0; i < datasets.length; i++) {
			pos_y = ExcelCommon.checkAndReturnPosY((sheets != null && sheets.size() > i) ? (Element)sheets.get(i) : null, pos_y);
			if (datasets[i].size() > (maxRows - 1 - pos_y)) {
				out = true;
			}
		}
		
		if (sheets.size() > ExcelConfig.MAX_SHEET_SIZE && datasets.length > ExcelConfig.MAX_SHEET_SIZE ) {
			throw new BaseException("Excel-export-exception",
					new String[]{"sheet-size:"+sheets.size()+"|"+datasets.length},"export data too large");
		}
		
		for (int i = 0;i < sheets.size(); i++) {
			Element sheet = (Element)sheets.get(i);
			Element header = sheet.element("header");
			List cells = header.elements();
			pos_x = ExcelCommon.checkAndReturnPosX(sheet, pos_x);
			if (cells.size() > (maxColumn - pos_x)) {
				throw new BaseException("Excel-export-exception",
						new String[]{"sheet-"+i+"-column:"+cells.size()},"export data too large");
			}
		}
		return out;
		
	}
	
	/**
	 * 表格名去重, 并初始化sheet的创建
	 * @param sheets
	 * @return
	 */
	public static final List<String> initSheets(Workbook workbook, List sheets){
		int sheetCount = sheets.size();
		List<String> names = new ArrayList<String>(sheetCount);
		Element sheet = null;
		String sheet_desc = null;
		Map<String,List<Integer>> samesGroup = null;
		List<Integer> sames = null;
		List<String> uniqueNames = new LinkedList<String>();
		for (int i = 0; i < sheetCount ; i++) {
			sheet = (Element) sheets.get(i);
			sheet_desc = sheet.attributeValue("desc");
			names.add(sheet_desc);
			
			if (!uniqueNames.contains(sheet_desc)) {
				uniqueNames.add(sheet_desc);
			}
			
			for (int j = 0; j < i; j++) {
				if (sheet_desc.equals(names.get(j))) {
					if (samesGroup == null) {
						samesGroup = new HashMap<String,List<Integer>>(sheetCount/2);
					}
					sames = samesGroup.get(String.valueOf(j));
					if (sames == null) {
						sames = new ArrayList<Integer>(sheetCount);
						samesGroup.put(String.valueOf(j), sames);
					}
					sames.add(i);
					break;
				}
			}
		}
		
		int uniqueSheetNums = uniqueNames.size();
		for (int i = 0; i < uniqueSheetNums; i++) {
			String name = uniqueNames.get(i);
			if (workbook.getSheet(name) == null) {
				createSheet(workbook, i, name);
			}
		}
		
		if (samesGroup != null) {
			Iterator<String> keys = samesGroup.keySet().iterator();
			while (keys.hasNext()) {
				String srcIndexStr = keys.next();
				sames = samesGroup.get(srcIndexStr);
				for (int i = 0; i < sames.size();i++) {
					int nameIndex = sames.get(i);
					String newName = createDumplicateSheetName(names.get(nameIndex), i+1);
					names.set(nameIndex, newName);
					int srcIndex = Integer.valueOf(srcIndexStr);
					int descIndex = srcIndex + i + 1;
					orderSheet(workbook, workbook.cloneSheet(srcIndex).getSheetName(), newName, descIndex);
				}
			}
		}
		
		return names;
	}
	
	public static String createDumplicateSheetName(String oldName, int index) {
		return oldName + SHEET_DUMP_NAME_SPLIT + index;
	}
	
	public static String createSheetNameByOldName(String oldName) {
		String newSheetName = null;
		if (StringUtils.isNotBlank(oldName)) {
			int splitIndex = oldName.lastIndexOf(SHEET_DUMP_NAME_SPLIT);
			if (splitIndex == -1) {
				newSheetName = createDumplicateSheetName(oldName, 1);
			} else {
				newSheetName = createDumplicateSheetName(oldName.substring(0, splitIndex), Integer.valueOf(oldName.substring(splitIndex + 3)) + 1);
			}
		} else {
			newSheetName = createDumplicateSheetName(oldName, 1);
		}
		return newSheetName;
	}
	
	public static CellStyle createDefaultHeadStyle(Workbook workbook, int excelVersion) {
		if (excelVersion == ExcelConfig.excel_03) {
			HSSFCellStyle styleH = ((HSSFWorkbook)workbook).createCellStyle();
			styleH.setWrapText(true);
			styleH.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styleH.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			HSSFFont fontH = ((HSSFWorkbook)workbook).createFont();
			fontH.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			fontH.setColor(HSSFColor.WHITE.index);
			styleH.setFont(fontH);
			styleH.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
			styleH.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			styleH.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleH.setBottomBorderColor(HSSFColor.WHITE.index);
			styleH.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			styleH.setLeftBorderColor(HSSFColor.WHITE.index);
			styleH.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleH.setRightBorderColor(HSSFColor.WHITE.index);
			styleH.setBorderTop(HSSFCellStyle.BORDER_THIN);
			styleH.setTopBorderColor(HSSFColor.WHITE.index);
			return styleH;
		} else if (excelVersion == ExcelConfig.excel_07) {
			XSSFCellStyle styleH = ((XSSFWorkbook)workbook).createCellStyle();
			styleH.setWrapText(true);
			styleH.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			styleH.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			XSSFFont fontH = ((XSSFWorkbook)workbook).createFont();
			fontH.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			fontH.setColor(new XSSFColor(new Color(0,0,0,255)));
			styleH.setFont(fontH);
			styleH.setFillForegroundColor(new XSSFColor(new Color(51, 102, 255,255)));
			styleH.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			styleH.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleH.setBottomBorderColor(new XSSFColor(new Color(255,255,255,255)));
			styleH.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleH.setLeftBorderColor(new XSSFColor(new Color(255,255,255,255)));
			styleH.setBorderRight(XSSFCellStyle.BORDER_THIN);
			styleH.setRightBorderColor(new XSSFColor(new Color(255,255,255,255)));
			styleH.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleH.setTopBorderColor(new XSSFColor(new Color(255,255,255,255)));
			return styleH;
		} else {
			CellStyle styleH = ((SXSSFWorkbook)workbook).createCellStyle();
			styleH.setWrapText(true);
			styleH.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			styleH.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			Font fontH = ((SXSSFWorkbook)workbook).createFont();
			fontH.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			fontH.setColor(HSSFColor.WHITE.index);
			styleH.setFont(fontH);
			styleH.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
			styleH.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			styleH.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleH.setBottomBorderColor(HSSFColor.WHITE.index);
			styleH.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			styleH.setLeftBorderColor(HSSFColor.WHITE.index);
			styleH.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleH.setRightBorderColor(HSSFColor.WHITE.index);
			styleH.setBorderTop(HSSFCellStyle.BORDER_THIN);
			styleH.setTopBorderColor(HSSFColor.WHITE.index);
			return styleH;
		}
	}
	
	public static void orderSheet(Workbook workbook, String sheetName, String newSheetName, int descIndex) {
		workbook.setSheetOrder(sheetName, descIndex);
		workbook.setSheetName(descIndex, newSheetName);
	}
	
	public static void createSheet(Workbook workbook, int srcIndex, String sheetName) {
		workbook.createSheet(sheetName);
		workbook.setSheetOrder(sheetName, srcIndex);
	}
	
	public static int checkAndReturnPosX(Element sheet, int pos_x) {
		if (pos_x <= 0 && sheet != null) {
			String posXStr = sheet.attributeValue("pos_x");
			if (StringUtils.isNotBlank(posXStr)) {
				pos_x = Integer.valueOf(posXStr);
			}
		}
		
		return pos_x;
	}
	
	public static int checkAndReturnPosY(Element sheet, int pos_y) {
		if (pos_y <= 0 && sheet != null) {
			String posYStr = sheet.attributeValue("pos_y");
			if (StringUtils.isNotBlank(posYStr)) {
				pos_y = Integer.valueOf(posYStr);
			}
		}
		
		return pos_y;
	}
	
	public static void fillRowData(Workbook workbook, Sheet worksheet, IData data, List cells, List styles, DataFormat format, 
			int row, int pos_x, boolean isFirstRow, Font defaultFont, boolean isRightData, int version) throws Exception {
		Row workrow = worksheet.createRow(row);
		int fillCellSize = cells.size() + pos_x;
		for (int h = 0; h < fillCellSize; h++) {
			if (h < pos_x) {
				Cell cellH = workrow.createCell(h);
				cellH.setCellValue("");
			} else {
				Element cell = (Element) cells.get(h - pos_x);
				String cell_name = cell.attributeValue("name");
				String cell_type = cell.attributeValue("type");
				String cell_align = cell.attributeValue("align");
				String cell_scale = cell.attributeValue("scale");
				String cell_format = cell.attributeValue("format");
				
				String cell_value = data.getString(cell_name,"");
				Cell workcell = workrow.createCell(h);
				
				CellStyle style = null;
				if (isFirstRow) {
					style = workbook.createCellStyle();
					style.setFont(defaultFont);
					style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
					if (cell_align != null) style.setAlignment(Short.parseShort(cell_align));	
					styles.add(style);
				} else {
					style = (CellStyle) styles.get(h - pos_x);
				}
				
				if (cell_value != null) {	
					if (isRightData) {
						if (StringUtils.isNotBlank(cell_format)) style.setDataFormat(format.getFormat(cell_format));
						if (ExcelConfig.CELL_TYPE_STRING.equals(cell_type) || ExcelConfig.CELL_TYPE_PSPT.equals(cell_type)) {	
							setCellValue(workcell, cell_value, version);
						}
						if (ExcelConfig.CELL_TYPE_NUMERIC.equals(cell_type)) {
							workcell.setCellValue(cell_scale == null ? 
									Double.parseDouble(cell_value) : 
									Double.parseDouble(cell_value) / Double.parseDouble(cell_scale));
						}
						if (ExcelConfig.CELL_TYPE_DATETIME.equals(cell_type)) {
							if (StringUtils.isBlank(cell_format)) {
								cell_format = ExcelConfig.DEFAULT_DATE_FORMAT;
								style.setDataFormat(format.getFormat(cell_format));
							}
							workcell.setCellValue(Utility.decodeTimestamp(cell_format, cell_value));
						}
					} else {
						workcell.setCellValue(cell_value);
					}
					workcell.setCellStyle(style);
				}
			}
		}
	}
	
	public static void setCellValue(Cell cell, String cellValue, int version) {
		if (ExcelConfig.excel_03 == version) {
			cell.setCellValue(new HSSFRichTextString(cellValue));
		} else if (ExcelConfig.excel_07 == version) {
			cell.setCellValue(new XSSFRichTextString(cellValue));
		} else if (ExcelConfig.excel_07_advance == version) {
			cell.setCellValue(new XSSFRichTextString(cellValue));
		}
	}
	
	public static Font createDefaultFont(Workbook workbook, int version) {
		if (version == ExcelConfig.excel_03) {
			Font font = workbook.createFont();
			font.setColor(HSSFColor.BLACK.index);
			return font;
		} else if (version == ExcelConfig.excel_07) {
			XSSFFont font = ((XSSFWorkbook)workbook).createFont();
			font.setColor(new XSSFColor(new Color(255,255,255,255)));
			return font;
		} else {
			Font font = ((SXSSFWorkbook)workbook).createFont();
			font.setColor(HSSFColor.BLACK.index);
			return font;
		}
	}
	
	public static int dealHeader(Element header, List cells, Workbook workbook, Sheet worksheet, int lastRowNum, int pos_x, int pos_y, int version) {
		boolean isshow = Boolean.valueOf(header.attributeValue("isshow")).booleanValue();
  		int rows = lastRowNum == 0 ? 0 : ++lastRowNum;
  		int fillCellSize = cells.size() + pos_x;
  		//空行
	  	while (rows < pos_y) {
	  		Row rowH = worksheet.createRow(rows++);
	  		for (int h = 0; h < fillCellSize; h++) {					
				Cell cellH = rowH.createCell(h);
				cellH.setCellValue("");
			}
		}
		if (isshow) {
			CellStyle styleH = ExcelCommon.createDefaultHeadStyle(workbook, version);
			Row rowH = worksheet.createRow(rows++);
			String heightStr = header.attributeValue("height");
			if (StringUtils.isNotBlank(heightStr)) {
				rowH.setHeight(Short.parseShort(heightStr));
			}
			
			for (int h = 0; h < fillCellSize; h++) {
				if (h < pos_x) {
					Cell cellH = rowH.createCell(h);
					cellH.setCellValue("");
				} else {
					Element cell = (Element)cells.get(h - pos_x);
					String cell_desc = cell.attributeValue("desc");
					String cell_width = cell.attributeValue("width");

					Cell cellH = rowH.createCell(h);
					setCellValue(cellH, cell_desc, version);
					
					worksheet.setColumnWidth(h, Integer.parseInt(cell_width));
					cellH.setCellStyle(styleH);
				}
			}
		} else {
			rows = getStartRow(header, rows);
		}
		return rows;
	}
	
	public static int getStartRow(Element header, int rows) {
		boolean isshow = Boolean.valueOf(header.attributeValue("isshow")).booleanValue();
		if (!isshow) {
			String rowNums = header.attributeValue("rowNums");
			if (StringUtils.isNotBlank(rowNums)) {
				int headerRowNums = Integer.parseInt(rowNums);
				rows += headerRowNums;
			}
		}
		return rows;
	}
	
	public static IData getSheetAttrs(Element sheet) {
		IData attrsData = new DataMap();
		List<Attribute> attrs = sheet.attributes();
		//add by xiedx add sheet attrs
		if(attrs != null){
			for(Attribute attr : attrs){
				attrsData.put(attr.getName(), attr.getValue());
			}
		}
		return attrsData;
	}
	
	public static Workbook createWorkbook(Workbook workbook, List sheets, IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y, int version) throws Exception {
		DataFormat format = workbook.createDataFormat();
		//验证数据范围
		if (ExcelCommon.verifyData(sheets, datasets, pos_x, pos_y, version)) {
			Object[] data = splitSheet(sheets, datasets, pos_y, version);
			sheets = (List)data[0];
			datasets = (IDataset[])data[1];
		}
		int sheetCount = sheets.size() > datasets.length ? datasets.length : sheets.size();
		ExcelCommon.initSheets(workbook, sheets);
		int oldPos_x = pos_x;
		int oldPos_y = pos_y;
		
		// 要写入的内容是否为导入异常内容, 导入异常内容写文件时 不再做校验, 同时也不进行格式转换
		IDataset firstDataset = (datasets == null || datasets.length < 1) ? null : datasets[0];
		boolean isRightData = true;
		if (firstDataset != null && !firstDataset.isEmpty() && ((IData)firstDataset.get(0)).getBoolean("WADE_TRANSFORM_ERROR_DATA", false)) {
			isRightData = false;
		}
					
		for (int i=0; i<sheetCount; i++) {
			Element sheet = (Element) sheets.get(i);
			IData sheetAttrs = ExcelCommon.getSheetAttrs(sheet);
			if (dealAction != null) {
				dealAction.begin(sheet.attributeValue("desc"), sheetAttrs);
			}
			pos_x = ExcelCommon.checkAndReturnPosX(sheet, oldPos_x);
			pos_y = ExcelCommon.checkAndReturnPosY(sheet, oldPos_y);
			Sheet worksheet = workbook.getSheetAt(i);
			Element header = sheet.element("header");
			List cells = header.elements();
			int rows = ExcelCommon.dealHeader(header, cells, workbook, worksheet, 0, pos_x, pos_y, version);
			Font font = createDefaultFont(workbook, version);
			List styles = new ArrayList();
			IDataset dataset = datasets[i];
			for (int j = 0; j < dataset.size(); j++) {
				IData data = (IData) dataset.get(j);
				if (dealAction != null && !dealAction.execute(data, true, null)) {
					// 中断操作
					return null;
				}
				ExcelCommon.fillRowData(workbook, worksheet, data, cells, styles, format,
							rows++, pos_x, (j == 0), font, isRightData, version);
			}
			if (dealAction != null) {
				dealAction.end(sheet.attributeValue("desc"));
			}
		}
		if (dealAction != null) {
			dealAction.over();
		}
		return workbook;
	}
	
	/**
	 * 分隔过量数据
	 * @param sheets
	 * @param datasets
	 * @param pos_y
	 * @param version
	 * @return
	 */
	public static Object[] splitSheet(List sheets,IDataset[] datasets, int pos_y,int version){
		int maxRows;
		if (version == ExcelConfig.excel_03) {
			maxRows = ExcelConfig.MAX_ROWS_SIZE;
		} else {
			maxRows = ExcelConfig.MAX_ROWS_SIZE_07;
		}
		
		Object[] returnData = new Object[2];
		List copy = new ArrayList(sheets);
		//
		IDataset[] newdataset = null;
		IDataset[] olddataset = datasets;
		IDataset dataset = null;
		for (int i = 0; i < copy.size() && i < olddataset.length; i++) {
			pos_y = ExcelCommon.checkAndReturnPosY((Element)copy.get(i), pos_y);
			int max = maxRows-1-pos_y;
			if (olddataset[i].size() > max) {
				int count = (int) Math.ceil((double) olddataset[i].size() / (double) max);
				for (int j = 1;j < count; j++) {
					copy.add(i+j, copy.get(i));
				}
				newdataset = new IDataset[olddataset.length + count - 1];
				System.arraycopy(olddataset, 0, newdataset, 0, i);
				System.arraycopy(olddataset, i+1, newdataset, i+count, olddataset.length-i-1);
				dataset = olddataset[i];
				for (int k = 0; k < count ; k++) {
					newdataset[i+k] = new DatasetList();
					if (k == (count-1) && olddataset[i].size()%max > 0) {
						newdataset[i+k].addAll(dataset.subList(k*max, olddataset[i].size() % max + k*max ));
					} else {
						newdataset[i+k].addAll(dataset.subList(k*max, (k+1)*max));
					}
				}
				olddataset = newdataset ;
				i += count-1;
			}
		}
		if (copy.size() > ExcelConfig.MAX_SHEET_SIZE && olddataset.length > ExcelConfig.MAX_SHEET_SIZE ) {
			throw new BaseException("Excel-export-exception",
					new String[]{"sheet-size:"+copy.size()+"|"+olddataset.length},"export data too large");
		}
		
		returnData[0] = copy;
		returnData[1] = olddataset;
		return returnData;
	}
	
	public static boolean getCellData(Element cell, IData data, StringBuilder error, SimpleDateFormat sdf, Cell workcell, String cellValue) throws Exception {
		String cell_name = cell.attributeValue("name");
		if (workcell == null && cellValue == null) {
			error.append(Validate.verifyCell(cell, data.getString(cell_name, "")));
			return false;
		}
		String cell_value = "";
		String xml_cell_type = cell.attributeValue("type");
		String cell_format = cell.attributeValue("format");
		if (workcell != null) {
			int cell_type = workcell.getCellType();
			switch (cell_type) {
				case Cell.CELL_TYPE_STRING :
					cell_value = workcell.getStringCellValue().trim();
				 	break;
				case Cell.CELL_TYPE_NUMERIC :
					if (ExcelConfig.CELL_TYPE_DATETIME.equals(xml_cell_type) && DateUtil.isCellDateFormatted(workcell)) {
						if (StringUtils.isBlank(cell_format)) {
							cell_format = ExcelConfig.DEFAULT_DATE_FORMAT;
						}
						Date date_value = workcell.getDateCellValue();
						sdf.applyPattern(cell_format);
						cell_value = sdf.format(date_value);
					} else {
						if (StringUtils.isBlank(cell_format)) {
							cell_format = "#.##";
						}
						cell_value = String.valueOf(Utility.formatDecimal(cell_format, workcell.getNumericCellValue()));
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN :
					cell_value = String.valueOf(workcell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_BLANK :
					break;
				case Cell.CELL_TYPE_FORMULA :
					cell_value = String.valueOf(workcell.getCellFormula());
					break;
				case Cell.CELL_TYPE_ERROR :
					cell_value = String.valueOf(workcell.getErrorCellValue());
					break;
			}
		} else if (cellValue != null) {
			if (ExcelConfig.CELL_TYPE_NUMERIC.equals(xml_cell_type)) {
				if (StringUtils.isBlank(cell_format)) {
					cell_format = "#.##";
				}
				try {
					double cellValueNumber = Double.parseDouble(cellValue);
					cell_value = String.valueOf(Utility.formatDecimal(cell_format, cellValueNumber));
				} catch (Exception e) {
					cell_value = cellValue;
				}
			} else if (ExcelConfig.CELL_TYPE_DATETIME.equals(xml_cell_type)) {
				if (StringUtils.isBlank(cell_format)) {
					cell_format = ExcelConfig.DEFAULT_DATE_FORMAT;
				}
				try {
					sdf.applyPattern(cell_format);
					cell_value = sdf.format(cellValue);
				} catch (Exception e) {
					cell_value = cellValue;
				}
			} else {
				cell_value = cellValue;
			}
		}
		if (!"".equals(cell_value)) data.put(cell_name, cell_value);
		error.append(Validate.verifyCell(cell, data.getString(cell_name, "")));
		
		return true;
	}
	
}