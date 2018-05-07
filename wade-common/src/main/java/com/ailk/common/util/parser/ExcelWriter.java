package com.ailk.common.util.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.FileManHelper;
import com.ailk.common.util.IDealData;

public class ExcelWriter {

	protected final static Logger log = Logger.getLogger(ExcelWriter.class);

	/**
	 * writeExcel03FromData  无模板写Excel03
	 * @param sheets 配置的xml模板element数组
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @throws Exception
	 */
	public static void writeExcel03FromData(List sheets, OutputStream output,
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception{
		
		writeExcel03FromData( sheets, output, null, datasets, dealAction, pos_x, pos_y);
	}
	
	/**
	 * writeExcel07FromData  无模板写Excel07
	 * @param sheets 配置的xml模板element数组
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @throws Exception
	 */
	public static void writeExcel07FromData(List sheets, OutputStream output,
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception{
		
		writeExcel07FromData( sheets, output, null, datasets, dealAction, pos_x, pos_y);
	}
	
	/**
	 * writeExcel03FromData 有模板写Excel03
	 * @param sheets 配置的xml模板element数组
	 * @param excelModel Excel模板（如水印），必须为03版
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @throws Exception
	 */
	public static void writeExcel03FromData(List sheets, OutputStream output, InputStream excelModel,
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception{
		
		HSSFWorkbook workbook = getWorkbook03( sheets, excelModel, datasets, dealAction, pos_x, pos_y);
		if (workbook != null) {
			workbook.write(output);
		}
	}
	
	/**
	 * writeExcel07FromData 有模板写Excel07
	 * @param sheets 配置的xml模板element数组
	 * @param excelModel Excel模板（如水印），必须为07版
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @throws Exception
	 */
	public static void writeExcel07FromData(List sheets, OutputStream output, InputStream excelModel,
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception{
		
		XSSFWorkbook workbook = getWorkbook07( sheets, excelModel, datasets, dealAction, pos_x, pos_y);
		if (workbook != null) {
			workbook.write(output);
		}
	}
	
	/**
	 * 
	 * @param sheets
	 * @param output
	 * @param excelModel
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @throws Exception
	 */
	public static void appendExcel03FromData(List sheets, ZipOutputStream output, String entryName , InputStream excelModel,
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception {
		appendExcelFromData(sheets, output, entryName , excelModel, datasets, dealAction, pos_x, pos_y, ImpExpUtil.excel_03);
	}
	
	/**
	 * 
	 * @param sheets
	 * @param output
	 * @param excelModel
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @throws Exception
	 */
	public static void appendExcel07FromData(List sheets, ZipOutputStream output ,String entryName , InputStream excelModel,
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception {
		appendExcelFromData(sheets, output, entryName , excelModel, datasets, dealAction, pos_x, pos_y, ImpExpUtil.excel_07);
	}
	
	private static void appendExcelFromData(List sheets, ZipOutputStream output, String entryName , InputStream excelModel,
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y ,int version) throws Exception {
		
		File file = new File(entryName);
		OutputStream fileoutput = new FileOutputStream(file);
		String type = null;
		if(version == ImpExpUtil.excel_07){
			writeExcel07FromData(sheets, fileoutput, excelModel, datasets, dealAction, pos_x, pos_y);
			type = ".xlsx";
		}else{
			writeExcel03FromData(sheets, fileoutput, excelModel, datasets, dealAction, pos_x, pos_y);
			type = ".xls";
		}
		if(fileoutput != null){
			fileoutput.flush();
			fileoutput.close();
		}
		InputStream fileinput = new FileInputStream(file);
		output.putNextEntry(new ZipEntry(file.getName()+type));
		FileManHelper.writeInputToOutput(fileinput, output, false);
		if(file != null && file.exists()){
			file.delete();
		}
	}
	

	/**
	 * get excel03 workbook
	 * @param sheets
	 * @param excelModel
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @return HSSFWorkbook
	 * @throws Exception
	 */
	private static HSSFWorkbook getWorkbook03(List sheets,InputStream excelModel, 
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception {
		boolean hasTemplate = excelModel != null;
		HSSFWorkbook workbook = null;
		if (hasTemplate) {
			POIFSFileSystem fs = new POIFSFileSystem(excelModel);
			workbook = new HSSFWorkbook(fs);
		} else {
			workbook = new HSSFWorkbook();
		}
		workbook = (HSSFWorkbook)ExcelCommon.createWorkbook(workbook, sheets, datasets, dealAction, pos_x, pos_y, ExcelConfig.excel_03);
		return workbook;
	}
	
	
	/**
	 * get excel07 workbook
	 * @param sheets
	 * @param excelModel
	 * @param datasets
	 * @param pos_x
	 * @param pos_y
	 * @return XSSFWorkbook
	 * @throws Exception
	 */
	private static XSSFWorkbook getWorkbook07(List sheets,InputStream excelModel, 
			IDataset[] datasets, IDealData dealAction, int pos_x, int pos_y) throws Exception {
		boolean hasTemplate = excelModel != null;
		XSSFWorkbook workbook = null;
		if (hasTemplate) {
			workbook = (XSSFWorkbook) WorkbookFactory.create(excelModel);
		} else {
			workbook = new XSSFWorkbook();
		}
		workbook = (XSSFWorkbook)ExcelCommon.createWorkbook(workbook, sheets, datasets, dealAction, pos_x, pos_y, ExcelConfig.excel_07);
		return workbook;
	}
	
	public static void writeExcel03FromData(List sheets, OutputStream output,
			IDataset[] datasets, int pos_x, int pos_y) throws Exception{
		writeExcel03FromData(sheets, output, datasets, null, pos_x, pos_y);
	}
	
	public static void writeExcel07FromData(List sheets, OutputStream output,
			IDataset[] datasets, int pos_x, int pos_y) throws Exception{
		writeExcel07FromData(sheets, output, datasets, null, pos_x, pos_y);
	}

	public static void writeExcel03FromData(List sheets, OutputStream output, InputStream excelModel,
			IDataset[] datasets, int pos_x, int pos_y) throws Exception{
		writeExcel03FromData(sheets, output, excelModel, datasets, null, pos_x, pos_y);
	}
	
	public static void writeExcel07FromData(List sheets, OutputStream output, InputStream excelModel,
			IDataset[] datasets, int pos_x, int pos_y) throws Exception{
		writeExcel07FromData(sheets, output, excelModel, datasets, null, pos_x, pos_y);
	}
	
	public static void appendExcel03FromData(List sheets, ZipOutputStream output, String entryName , InputStream excelModel,
			IDataset[] datasets, int pos_x, int pos_y) throws Exception {
		appendExcel03FromData(sheets, output, entryName, excelModel, datasets, null, pos_x, pos_y);
	}
	
	public static void appendExcel07FromData(List sheets, ZipOutputStream output ,String entryName , InputStream excelModel,
			IDataset[] datasets, int pos_x, int pos_y) throws Exception {
		appendExcel07FromData(sheets, output, entryName, excelModel, datasets, null, pos_x, pos_y);
	}
	
	public static void main(String[] args) throws Exception {
		IData data11=new DataMap();
		data11.put("DEPART_ID", "部门1111111");
		data11.put("VALID_FLAG", "Y");
		data11.put("START_DATE", "2012-09-08");
		data11.put("END_DATE", "2013-01-01");
		
		IData data12=new DataMap();
		data12.put("DEPART_ID", "部门1222222");
		data12.put("VALID_FLAG", "Y");
		data12.put("START_DATE", "2012-09-08");
		data12.put("END_DATE", "2013-01-01");
		
		IData data13=new DataMap();
		data13.put("DEPART_ID", "部门13333333");
		data13.put("VALID_FLAG", "Y");
		data13.put("START_DATE", "2012-09-08");
		data13.put("END_DATE", "2013-01-01");
		
		DatasetList dataset1=new DatasetList();
		dataset1.add(data11);dataset1.add(data12);dataset1.add(data13);
		
		IData data21=new DataMap();
		data21.put("DEPART_ID", "11部门11");
		data21.put("VALID_FLAG", "Y");
		data21.put("START_DATE", "2012-09-08");
		data21.put("END_DATE", "2013-01-01");
		
		IData data22=new DataMap();
		data22.put("DEPART_ID", "22部门12");
		data22.put("VALID_FLAG", "Y");
		data22.put("START_DATE", "2012-09-08");
		data22.put("END_DATE", "2013-01-01");
		
		IData data23=new DataMap();
		data23.put("DEPART_ID", "33部门13");
		data23.put("VALID_FLAG", "Y");
		data23.put("START_DATE", "2012-09-08");
		data23.put("END_DATE", "2013-01-01");
		DatasetList dataset2=new DatasetList();
		dataset2.add(data21);dataset2.add(data22);
		
		IData data31=new DataMap();
		data31.put("DEPART_ID", "部门11");
		data31.put("STATIC_COUNT", "Y");
		data31.put("STATIC_DATE", "2012-09-08");
		data31.put("STATIC_TIME", "2013-01-01");
		data31.put("VALID_FLAG", "2013-01-01");
		IData data32=new DataMap();
		data32.put("DEPART_ID", "部门12");
		data32.put("VALID_FLAG", "Y");
		data32.put("START_DATE", "2012-09-08");
		data32.put("END_DATE", "2013-01-01");
		
		IData data33=new DataMap();
		data33.put("DEPART_ID", "部门13");
		data33.put("VALID_FLAG", "Y");
		data33.put("START_DATE", "2012-09-08");
		data33.put("END_DATE", "2013-01-01");
		DatasetList dataset3=new DatasetList();
		dataset3.add(data31);
		IDataset[] datasets=new DatasetList[]{dataset1,dataset2,dataset3};
		
		FileOutputStream output = new FileOutputStream(new File("C:/Users/lvchao/Desktop/TestWrite07_111111.xlsx"));
		FileInputStream excelModel =  new FileInputStream(new File("C:/Users/lvchao/Desktop/excelModel.xlsx"));
		FileInputStream in = new FileInputStream(new File("C:/Users/lvchao/Desktop/testWriteExcel.xml"));
		SAXReader reader = new SAXReader();
	    Document document = reader.read(in);
		in.close();
		Element book = document.getRootElement();
		List sheets = book.elements();
		writeExcel07FromData(sheets, output, excelModel, datasets, null, 0, 0);
		
		if (true) {
			return;
		}
		
		FileOutputStream fos = new FileOutputStream(new File("test006.zip"),true);
		ZipOutputStream zipout = new ZipOutputStream(fos);
		zipout.close();
		fos.close();
	}
}
