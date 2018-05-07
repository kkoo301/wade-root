package com.ailk.common.util.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.ailk.common.config.CodeCfg;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.AbstractImpExpManager;
import com.ailk.common.util.FileManHelper;
import com.ailk.common.util.IDealData;
import com.ailk.common.util.IFileAction;
import com.ailk.common.util.IResultData;
import com.ailk.common.util.SimpleParser;
import com.ailk.common.util.SimpleParser.FilePattern;
import com.ailk.common.util.Utility;
import com.ailk.common.util.impl.DefaultImpExpManager;

public class ImpExpUtil {
	
	private static transient final Logger log = Logger.getLogger(ImpExpUtil.class);
	private static int c = 100000;
	
	public static final String importExcel = "WADE_EXCEL_IMPORT";
	public static final String exportExcel = "WADE_EXCEL_EXPORT";
	
	public static final int excel_03 = ExcelConfig.excel_03 ;
	public static final int excel_07 = ExcelConfig.excel_07 ;

	private static AbstractImpExpManager impExpManager = null;
	
	/*public static void setImpExpManager(AbstractImpExpManager impExpManagerObj){
		impExpManager = impExpManagerObj;
	}*/
	
	public static AbstractImpExpManager getImpExpManager(){
		if(impExpManager == null){
			synchronized(ImpExpUtil.class){
				if(impExpManager == null){
					String managerClazz = GlobalCfg.getImpExpManager();
					try {
						if ("".equals(managerClazz) || managerClazz == null)
							throw new ClassNotFoundException("action is empty");

						Class<?> clazz = ImpExpUtil.class.getClassLoader().loadClass(managerClazz);
						impExpManager = (AbstractImpExpManager) clazz.newInstance();

					} catch (Exception e) {
						impExpManager = new DefaultImpExpManager();
					}
				}
			}
		}
		return impExpManager; 
	}
	
	/**
	 * 获取下载路径
	 * 
	 * @param fileSerializeId
	 * @return
	 */
	public static String getDownloadPath(String fileId,String fileName){
		try {
			fileName = URLEncoder.encode(fileName, GlobalCfg.getCharset());
		} catch (UnsupportedEncodingException e) {	
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("attach?action=download&fileId="+fileId);
		sb.append("&realName=" + fileName);
		return sb.toString();
	}
	
	public static String beginExport(String fileSerializeId, IData params, String fileName, IDataset[] datasets) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExport(fileSerializeId, fileName, params, datasets, null, null, 
				params.getString("ftpSite"), 
				params.getString("config"), null, 
				params.getString("model"),
				StringUtils.isNotBlank(poxStr)?Integer.parseInt(poxStr):0, 
				StringUtils.isNotBlank(poyStr)?Integer.parseInt(poyStr):0,null		
				);
	}
	
	/**
	 * 支持生成大数据量的excel(07及以上版本)文件 
	 * @param fileSerializeId
	 * @param params
	 * @param fileName
	 * @param sources
	 * @param dealAction
	 * @param cacheSize
	 * @return
	 */
	public static String beginExportAdvanceExcel(String fileSerializeId,IData params,String fileName, IResultData[] sources, 
			IDealData dealAction, int cacheSize) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportAdvanceExcel(fileSerializeId, fileName, params, 
				params.getString("ftpSite"), 
				params.getString("config"), 
				params.getString("model"), 
				sources, dealAction, cacheSize, 
				StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0, 
				StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0);
	}
	
	public static String beginExportAdvanceExcel(String fileSerializeId,IData params,String fileName, IResultData[] sources, IDealData dealAction) throws Exception {
		return beginExportAdvanceExcel(fileSerializeId,params,fileName, sources, dealAction, 0);
	}
	
	public static String beginExport(String fileSerializeId,IData params,String fileName,IDataset[] datasets,String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExport(fileSerializeId, fileName, params, datasets, null, null, 
				params.getString("ftpSite"), 
				params.getString("config"), null, 
				params.getString("model"),
				StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0, 
				StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0,token		
				);
	}

	public static String beginExport(String fileSerializeId, IData params, String fileName, IDataset[] datasets, List cfgData) throws Exception {
		return beginExport(fileSerializeId, params, fileName, datasets, cfgData, null);
	}
	
	public static String beginExport(String fileSerializeId, IData params, String fileName, IDataset[] datasets, List cfgData, String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExport(fileSerializeId, fileName, params, datasets, null, null, 
				params.getString("ftpSite"), null, cfgData, 
				params.getString("model"),
				StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0, 
				StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	
	public static String beginExport(String fileSerializeId, String fileName, IData params, IDataset[] datasets,
				Object[] iDataKeys,String[] headNames) throws Exception {
		return beginExport(fileSerializeId, fileName, params, datasets, iDataKeys, headNames, null);
	}
	
	public static String beginExport(String fileSerializeId, String fileName, IData params, IDataset[] datasets,
				Object[] iDataKeys, String[] headNames, String token) throws Exception {
		
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExport(fileSerializeId, fileName, params, datasets, iDataKeys, headNames, 
				params.getString("ftpSite"), 
				params.getString("config"), null, 
				params.getString("model"),
				StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0, 
				StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0,token);
	}
	
	public static String beginExport(String fileSerializeId, String fileName, IData params, IDataset[] datasets, Object[] iDataKeys,
			String[] headNames, String excelCfg, String excelModel, String token) throws Exception {
		
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExport(fileSerializeId, fileName, params, datasets, iDataKeys, headNames, 
				params.getString("ftpSite"), 
				excelCfg, null, excelModel,
				StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0, 
				StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	
	public static String beginExport(String fileSerializeId, String fileName, IData params, IDataset[] datasets, Object[] iDataKeys,
			String[] headNames, List cfgData, String excelModel, String token) throws Exception {
		
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExport(fileSerializeId, fileName, params, datasets, iDataKeys, headNames, 
				params.getString("ftpSite"), 
				null, cfgData, excelModel,
				StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0, 
				StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	
	public static String beginExport(String fileSerializeId, String fileName, IData params, IDataset[] datasets, Object[] iDataKeys,
			String[] headNames, String excelCfg, List cfgData, String excelModel, String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExport(fileSerializeId, fileName, params, datasets, iDataKeys, headNames, 
				params.getString("ftpSite"), 
				excelCfg, cfgData, excelModel,
				StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0, 
				StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	public static String beginExportByStep(String fileType, IData params, IDataset[] datasets, String fileId, String model,
			List excelCfgData) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportByStep(fileType, datasets, fileId, model, excelCfgData,
					null,null,null,
					StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0,
					StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, null);
	}
	public static String beginExportByStep(String fileType, IData params, IDataset[] datasets, String fileId, String model,
			String excelCfg) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportByStep(fileType, datasets, fileId, model, null,
					excelCfg, null, null,
					StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0,
					StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, null);
	}
	public static String beginExportByStep(String fileType, IData params, IDataset[] datasets, String fileId, String model,
			List excelCfgData,String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportByStep(fileType,datasets,fileId ,model,excelCfgData,
					null,null,null,
					StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0,
					StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	public static String beginExportByStep(String fileType, IData params, IDataset[] datasets, String fileId, String model,
			String excelCfg, String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportByStep(fileType, datasets, fileId, model, null,
					excelCfg, null, null,
					StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0,
					StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	public static String beginExportByStep(String fileType, IData params, IDataset[] datasets, String fileId, String model,
			List excelCfgData, Object[] iDataKeys, String[] headNames, String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportByStep(fileType, datasets, fileId, model, excelCfgData,
					null, iDataKeys, headNames,
					StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0,
					StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	public static String beginExportByStep(String fileType, IData params, IDataset[] datasets, String fileId, String model,
			String excelCfg, Object[] iDataKeys, String[] headNames, String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportByStep(fileType,datasets,fileId ,model,null,
					excelCfg,iDataKeys,headNames,
					StringUtils.isNotBlank(poxStr) ? Integer.parseInt(poxStr) : 0,
					StringUtils.isNotBlank(poyStr) ? Integer.parseInt(poyStr) : 0, token);
	}
	public static String beginExportByStep(String fileType,IData params,IDataset[] datasets,String fileId ,String model,
			List excelCfgData,String excelCfg,Object[] iDataKeys,String[] headNames,String token) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportByStep(fileType,datasets,fileId ,model,excelCfgData,
					excelCfg,iDataKeys,headNames,
					StringUtils.isNotBlank(poxStr)?Integer.parseInt(poxStr):0,
					StringUtils.isNotBlank(poyStr)?Integer.parseInt(poyStr):0,token);
	}
	public static String beginExportByStep(String fileType,IDataset[] datasets,String fileId ,String model,
			List excelCfgData,String excelCfg,Object[] iDataKeys,String[] headNames, int pos_x,int pos_y,String token) throws Exception {
		try {
			if(StringUtils.isBlank(fileId)){
				fileId = getImpExpManager().getFileAction().createFileId();
			}
			File file = appendDataToFile(fileType,datasets,fileId ,model,
					excelCfgData,excelCfg,iDataKeys,headNames, pos_x,pos_y, token);
		} catch (Exception e) {
			throw e;
		}
		
		return fileId;
	}
	public String beginExportCSVByStep(IDataset datasets, String fileId, String filePath, String excelCfg, List configData, 
			String[] headNames, String[] iDataKeys) throws Exception {
		File file = null; 
		try {
			if(StringUtils.isBlank(fileId)){
				fileId = getImpExpManager().getFileAction().createFileId();
			}
			List sheets = getSheet(configData,"csv",excelCfg,iDataKeys,headNames);
			Element sheet = null;
			if(iDataKeys==null||headNames==null){
				// 文本文件默认利用config中第一个sheet的配置
				if(sheets!=null){
					sheet = (Element) sheets.get(0);
					if(sheet!=null){
						Map<String,String[]> initData = initHeadKeys(sheet);
						iDataKeys = initData.get("keys");
						headNames = initData.get("names");
					}
				}
			}
			file = new SimpleParser().appendCSVFromData(datasets, iDataKeys, headNames, fileId, sheet);
		} catch (Exception e) {
			throw e;
		}
		return fileId;
	}
	
	public String beginExportTxtByStep(IDataset datasets, String fileId, String filePath,  String excelCfg, List configData, 
			String[] headNames, String[] iDataKeys,String token) throws Exception {
		File file = null; 
		try {
			if(StringUtils.isBlank(fileId)){
				fileId = getImpExpManager().getFileAction().createFileId();
			}
			List sheets = getSheet(configData,"txt",excelCfg,iDataKeys,headNames);
			Element sheet = null;
			if(iDataKeys==null||headNames==null){
				// 文本文件默认利用config中第一个sheet的配置
				if(sheets!=null){
					sheet = (Element) sheets.get(0);
					if(sheet!=null){
						Map<String,String[]> initData = initHeadKeys(sheet);
						iDataKeys = initData.get("keys");
						headNames = initData.get("names");
					}
				}
			}
			file = new SimpleParser().appendTxtFromData(datasets, iDataKeys, headNames, fileId, sheet, token);
		} catch (Exception e) {
			throw e;
		}
		return fileId;
	}
	public static File appendDataToFile(String fileType,IDataset[] datasets ,String fileId ,String model,
			List excelCfgData,String excelCfg,Object[] iDataKeys,String[] headNames, int pos_x,int pos_y,String token) throws Exception{
		File file = null ;
		List sheets = getSheet(excelCfgData,fileType,excelCfg,iDataKeys,headNames);
		if("xls".equals(fileType)){
			appendExcelData(datasets, fileId, model, sheets, excel_03, pos_x, pos_y);
		}else if("xlsx".equals(fileType)){
			appendExcelData(datasets, fileId, model, sheets, excel_07, pos_x, pos_y);
		}else{
			Element sheet = null;
			if(iDataKeys==null||headNames==null){
				// 文本文件默认利用config中第一个sheet的配置
				if(sheets!=null){
					sheet = (Element) sheets.get(0);
					if(sheet!=null){
						Map<String,String[]> initData = initHeadKeys(sheet);
						iDataKeys = initData.get("keys");
						headNames = initData.get("names");
					}
				}
			}
			if("txt".equals(fileType)){
				file = new SimpleParser().appendTxtFromData(datasets[0], iDataKeys, headNames, fileId, sheet, token);
			}else if("csv".equals(fileType)){
				file = new SimpleParser().appendCSVFromData(datasets[0], iDataKeys, headNames, fileId, sheet);
			}
		}
		return file;
	}
	
	/**
	 * 更新导出状态,调用该方法后会直接将生成文件上传ftpSite
	 * 
	 * @param fileSerializeId 该次导出的唯一标记
	 * @param fileId 导出文件名
	 * @param datasets 本次导出的数据 excel可导出多个sheet，txt和csv则应保持datasets.size()==1
	 * @param iDataKeys 若导出的文件列 要按制定顺序显示的话，则在此列定义key值 
	 * @param headNames  表头显示内容
	 * @param ftpSite  ftpSite上传对应的配置
	 * @param excelCfg excel配置模版路径
	 * @param excelModel excel模版路径
	 * @param pos_x excel的X轴空位
	 * @param pos_y excel的y轴空位
	 */
	public static String beginExport(String fileSerializeId,String fileName,IData params,IDataset[] datasets,Object[] iDataKeys,
			String[] headNames,String ftpSite,String excelCfg,List excelCfgData,String model,int pos_x,int pos_y,String token) throws Exception {
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		File file = null; 
		InputStream in = null;
		if (StringUtils.isNotBlank(fileSerializeId)) {
			getImpExpManager().setStatus(fileSerializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.gotdata"), "");
		}
		String fileId = null;
		try {
			fileId = getImpExpManager().getFileAction().createFileId();
			file = writeDataToFile(fileType,datasets,ftpSite,fileId ,model,
					excelCfgData,excelCfg,iDataKeys,headNames, pos_x,pos_y, token);
			if(StringUtils.isNotBlank(fileSerializeId)){
				getImpExpManager().setStatus(fileSerializeId, "80", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.generated"), "");
			}
			
			String filePath = params.getString("filePath");
			if (filePath == null || filePath.length() <= 0) {
				filePath = "export";
			}
			filePath = Utility.buildFilePath(IFileAction.UPLOAD_EXPORT,filePath);
			in = new FileInputStream(file);
			getImpExpManager().getFileAction().upload(in, fileId, ftpSite, filePath, fileName, false);
			if(StringUtils.isNotBlank(fileSerializeId)){
				getImpExpManager().setStatus(fileSerializeId, "100", "ok", "0", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.completed"), getDownloadPath(fileId,fileName));
			}
		} catch (Exception e) {
			if(StringUtils.isNotBlank(fileSerializeId)){
				getImpExpManager().setStatus(fileSerializeId, "0", "error", "0", e.getMessage(), "");
			}
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if (file != null) {
				file.delete();
			}
		}
		return fileId;
	}
	
	public static String beginExportAdvanceExcel(String fileSerializeId, String fileName, IData params, String ftpSite, String excelCfg, String model,
			IResultData[] sources, IDealData dealAction, int cacheSize,	int pos_x,int pos_y) throws Exception {
		File file = null; 
		InputStream in = null;
		if (StringUtils.isNotBlank(fileSerializeId)) {
			getImpExpManager().setStatus(fileSerializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.gotdata"), "");
		}
		String fileId = null;
		FileOutputStream out = null;
		try {
			fileId = getImpExpManager().getFileAction().createFileId();
			file = createWriteExcelFile(fileId);
			out = new FileOutputStream(file);
			AdvanceDataReadWrite.createExcel(excelCfg, model, sources, dealAction, out, pos_x, pos_y, cacheSize);
			if(StringUtils.isNotBlank(fileSerializeId)){
				getImpExpManager().setStatus(fileSerializeId, "80", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.generated"), "");
			}
			String filePath = params.getString("filePath");
			if (filePath == null || filePath.length() <= 0) {
				filePath = "export";
			}
			filePath = Utility.buildFilePath(IFileAction.UPLOAD_EXPORT,filePath);
			in = new FileInputStream(file);
			getImpExpManager().getFileAction().upload(in, fileId, ftpSite, filePath, fileName, false);
			if(StringUtils.isNotBlank(fileSerializeId)){
				getImpExpManager().setStatus(fileSerializeId, "100", "ok", "0", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.completed"), getDownloadPath(fileId,fileName));
			}
		} catch (Exception e) {
			if(StringUtils.isNotBlank(fileSerializeId)){
				getImpExpManager().setStatus(fileSerializeId, "0", "error", "0", e.getMessage(), "");
			}
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if (file != null) {
				file.delete();
			}
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return fileId;
	}
	
	public static File writeDataToFile(String fileType,IDataset[] datasets,String ftpSite ,String fileId ,String model,
			String excelCfg) throws Exception{
		return writeDataToFile(fileType, datasets, ftpSite, fileId, model,
				null, excelCfg, null, null, 0, 0, null);
	}
	
	public static File writeDataToFile(String fileType, IDataset[] datasets, String ftpSite, String fileId, String model,
			String excelCfg, int pos_x, int pos_y, String token) throws Exception{
		return writeDataToFile(fileType, datasets, ftpSite, fileId, model,
				null, excelCfg, null, null, pos_x,pos_y,token);
	}
	
	public static File writeDataToFile(String fileType, IDataset[] datasets, String ftpSite, String fileId, String model,
			List excelCfgData) throws Exception{
		return writeDataToFile(fileType, datasets, ftpSite, fileId, model, excelCfgData, null, null, null, 0, 0, null);
	}
	
	public static File writeDataToFile(String fileType, IDataset[] datasets, String ftpSite, String fileId, String model,
			List excelCfgData, int pos_x, int pos_y, String token) throws Exception{
		return writeDataToFile(fileType, datasets, ftpSite, fileId, model, excelCfgData, null, null, null, pos_x, pos_y, token);
	}
	/**
	 * 根据文件类型及配置文件将数据生成到文件中，支持xls,txt,csv
	 * @param fileType 文件类型
	 * @param datasets	数据集
	 * @param ftpSite ftp服务器
	 * @param fileId 文件标识
	 * @param model excel模版
	 * @param excelCfgData xml配置数据
	 * @param excelCfg xml配置路径
	 * @param iDataKeys  datasets中对应的IData的key
	 * @param headNames 文件头信息
	 * @param pos_x x轴偏移
	 * @param pos_y y轴偏移
	 * @param token txt连接符
	 * @return
	 * @throws Exception
	 */
	public static File writeDataToFile(String fileType,IDataset[] datasets,String ftpSite ,String fileId ,String model,
			List excelCfgData,String excelCfg,Object[] iDataKeys,String[] headNames, int pos_x,int pos_y,String token) throws Exception{
		File file = null ;
		
		List sheets = getSheet(excelCfgData,fileType,excelCfg,iDataKeys,headNames);
		if("xls".equals(fileType)){
			file = writeExcelData(datasets, fileId, model, sheets, excel_03, pos_x, pos_y);
		}else if("xlsx".equals(fileType)){
			file = writeExcelData(datasets, fileId, model, sheets, excel_07, pos_x, pos_y);
		}else{
			Element sheet = null;
			if(iDataKeys==null||headNames==null){
				// 文本文件默认利用config中第一个sheet的配置
				if(sheets!=null){
					sheet = (Element) sheets.get(0);
					if(sheet!=null){
						Map<String,String[]> initData = initHeadKeys(sheet);
						iDataKeys = initData.get("keys");
						headNames = initData.get("names");
					}
				}
			}
			if("txt".equals(fileType)){
				file = new SimpleParser().writeTxtFromData(datasets[0], iDataKeys, headNames, fileId, sheet, token);
			}else if("csv".equals(fileType)){
				file = new SimpleParser().writeCSVFromData(datasets[0], iDataKeys, headNames, fileId, sheet);
			}
		}
		return file;
	}
	private static List getSheet(List excelCfgData,String fileType,String excelCfg,Object[] iDataKeys,String[] headNames) throws Exception{
		List sheets = null;
		if(excelCfgData!=null&&excelCfgData.size()>0){
			sheets = excelCfgData;
		}else{
			if("xls".equals(fileType)||"xlsx".equals(fileType)||iDataKeys==null||headNames==null){
				if(StringUtils.isBlank(excelCfg)){
					Utility.error(CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.config"));
				}
				sheets = ExcelConfig.getSheets(excelCfg);
			}
		}
		return sheets;
	}
	
	private static Map<String,String[]> initHeadKeys(Element sheet){
		String[] iDataKeys = null;
		String[] headNames = null;
		Element header = sheet.element("header");
		List<Element> cells = header.elements();
		
		List iDataKeysList = new LinkedList();
		List headNamesList = new LinkedList();
		for(Element cell : cells){
			iDataKeysList.add(cell.attributeValue("name"));
			headNamesList.add(cell.attributeValue("desc"));
		}
		iDataKeys = new String[iDataKeysList.size()];
		headNames = new String[headNamesList.size()];
		iDataKeysList.toArray(iDataKeys);
		headNamesList.toArray(headNames);//.toArray();
		Map<String,String[]> map = new HashMap<String,String[]>();
		map.put("keys", iDataKeys);
		map.put("names", headNames);
		return map;
	}
	
	public static String beginExportExcel(String serializeId, String fileName,IData params, IDataset[] datasets) throws Exception {
		String poxStr = params.getString("posX");
		String poyStr = params.getString("posY");
		return beginExportExcel(serializeId, fileName, params.getString("filePath"), datasets, params.getString("config"), null, 
				null,null, params.getString("ftpSite"), params.getString("model"), StringUtils.isNotBlank(poxStr)?Integer.parseInt(poxStr):0,
						StringUtils.isNotBlank(poyStr)?Integer.parseInt(poyStr):0);
	}
	
	public static String beginExportExcel(String serializeId, String fileName, String filePath, IDataset[] datasets, String excelCfg, 
			String ftpSite,	String model, int pos_x, int pos_y) throws Exception {
		return beginExportExcel(serializeId, fileName, filePath, datasets, excelCfg, null, 
				null,null, ftpSite, model, pos_x, pos_y);
	}
	
	public static String beginExportExcel(String serializeId, String fileName, String filePath, IDataset[] datasets, List configData,
			String ftpSite,	String model, int pos_x, int pos_y) throws Exception {
		return beginExportExcel(serializeId, fileName, filePath, datasets, null, configData, 
				null,null, ftpSite, model, pos_x, pos_y);
	}
	
	public static String beginExportExcel(String serializeId, String fileName, String filePath, IDataset[] datasets, String excelCfg, List configData, 
			String[] headNames,String[] iDataKeys, String ftpSite,	String model, int pos_x, int pos_y) throws Exception {
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		File file = null; 
		InputStream in = null;
		if(StringUtils.isNotBlank(serializeId)){
			getImpExpManager().setStatus(serializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.gotdata"), "");
		}
		String fileId = "";
		try {
			fileId = getImpExpManager().getFileAction().createFileId();
			List sheets = getSheet(configData,fileType,excelCfg,iDataKeys,headNames);
			if("xls".equals(fileType)){
				file = writeExcelData(datasets, fileId, model, sheets, excel_03, pos_x, pos_y);
			}else if("xlsx".equals(fileType)){
				file = writeExcelData(datasets, fileId, model, sheets, excel_07, pos_x, pos_y);
			}
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "80", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.generated"), "");
			}
			
			if (filePath == null || filePath.length() <= 0) {
				filePath = "export";
			}
			filePath = Utility.buildFilePath(IFileAction.UPLOAD_EXPORT,filePath);
			in = new FileInputStream(file);
			getImpExpManager().getFileAction().upload(in, fileId, ftpSite, filePath, fileName, false);
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "100", "ok", "0", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.completed"), getDownloadPath(fileId,fileName));
			}
		} catch (Exception e) {
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0", e.getMessage(), "");
			}
			throw e;
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if(file!=null){
				file.delete();
			}
		}
		return fileId;
	}
	
	public String beginExportTxt(String serializeId, String fileName, String filePath, String ftpSite, IDataset datasets, String excelCfg, List configData, 
			String token) throws Exception {
		return this.beginExportTxt(serializeId, fileName, filePath, datasets, excelCfg, configData, null, null, ftpSite, token);
	}
	
	public String beginExportTxt(String serializeId, String fileName, String filePath, IDataset datasets, String excelCfg, List configData, 
				String[] headNames, String[] iDataKeys, String ftpSite,String token) throws Exception {
		File file = null; 
		InputStream in = null;
		if(StringUtils.isNotBlank(serializeId)){
			getImpExpManager().setStatus(serializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.gotdata"), "");
		}
		String fileId = "";
		try {
			fileId = getImpExpManager().getFileAction().createFileId();
			List sheets = getSheet(configData,"txt",excelCfg,iDataKeys,headNames);
			Element sheet = null;
			if(iDataKeys==null||headNames==null){
				// 文本文件默认利用config中第一个sheet的配置
				if(sheets!=null){
					sheet = (Element) sheets.get(0);
					if(sheet!=null){
						Map<String,String[]> initData = initHeadKeys(sheet);
						iDataKeys = initData.get("keys");
						headNames = initData.get("names");
					}
				}
			}
			file = new SimpleParser().writeTxtFromData(datasets, iDataKeys, headNames, fileId, sheet, token);
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "80", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.generated"), "");
			}
			if (filePath == null || filePath.length() <= 0) {
				filePath = "export";
			}
			filePath = Utility.buildFilePath(IFileAction.UPLOAD_EXPORT,filePath);
			in = new FileInputStream(file);
			getImpExpManager().getFileAction().upload(in, fileId, ftpSite, filePath, fileName, false);
			if(StringUtils.isNotBlank(serializeId)) {
				getImpExpManager().setStatus(serializeId, "100", "ok", "0", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.completed"), getDownloadPath(fileId,fileName));
			}
		} catch (Exception e) {
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0", e.getMessage(), "");
			}
			throw e;
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if(file!=null){
				file.delete();
			}
		}
		return fileId;
	}
	
	public String beginExportCSV(String serializeId, String fileName, String filePath, IDataset datasets, String excelCfg, List configData, 
				String[] headNames, String[] iDataKeys, String ftpSite) throws Exception {
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		File file = null; 
		InputStream in = null;
		if(StringUtils.isNotBlank(serializeId)){
			getImpExpManager().setStatus(serializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.gotdata"), "");
		}
		String fileId = "";
		try {
			fileId = getImpExpManager().getFileAction().createFileId();
			List sheets = getSheet(configData,fileType,excelCfg,iDataKeys,headNames);
			Element sheet = null;
			if(iDataKeys==null||headNames==null){
				// 文本文件默认利用config中第一个sheet的配置
				if(sheets!=null){
					sheet = (Element) sheets.get(0);
					if(sheet!=null){
						Map<String,String[]> initData = initHeadKeys(sheet);
						iDataKeys = initData.get("keys");
						headNames = initData.get("names");
					}
				}
			}
			file = new SimpleParser().writeCSVFromData(datasets, iDataKeys, headNames, fileId, sheet);
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "80", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.generated"), "");
			}
			if (filePath == null || filePath.length() <= 0) {
				filePath = "export";
			}
			filePath = Utility.buildFilePath(IFileAction.UPLOAD_EXPORT,filePath);
			in = new FileInputStream(file);
			getImpExpManager().getFileAction().upload(in, fileId, ftpSite, filePath, fileName, false);
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "100", "ok", "0", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.export.completed"), getDownloadPath(fileId,fileName));
			}
		} catch (Exception e) {
			if(StringUtils.isNotBlank(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0", e.getMessage(), "");
			}
			throw e;
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if(file!=null){
				file.delete();
			}
		}
		return fileId;
	}
	/**
	 * 解析文件获得数据，支持文件Excel、Txt、CSV
	 * @param file 文件名要带类型: .xls .xlsx .txt .csv
	 * @param configData 配置文件
	 * @param pos_x Excel空余列数
	 * @param pos_y Excel空余行数
	 * @param txtSplit TXT文件分隔符
	 * @return IData
	 * @throws Exception
	 */
	public static IData beginImport(File file, List configData, int pos_x, int pos_y,String txtSplit) throws Exception{
		String fileName = file.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		InputStream input = new FileInputStream(file);
		if(fileType.equalsIgnoreCase("TXT")){
			return new SimpleParser().readTxtToData(input, (Element)configData.get(0),txtSplit);
		}else if(fileType.equalsIgnoreCase("CSV")){
			return new SimpleParser().readCSVToData(input, (Element)configData.get(0));
		}else if(fileType.equalsIgnoreCase("XLS") || fileType.equalsIgnoreCase("XLSX")){
			return ExcelReader.readExcelToData(configData, input, pos_x, pos_y);
		}
		return null;
	}
	
	/**
	 * 解析文件获得数据，支持文件Excel、Txt、CSV
	 * @param serializeId
	 * @param fileId
	 * @param configData
	 * @return
	 * @throws Exception
	 */
	public static IData beginImport(String serializeId, String fileId, List configData) throws Exception{
		return beginImport( serializeId, fileId, configData, 0, 0, null);
	}
	
	/**
	 * 支持大数据量的excel文件导入(07及以上excel版本)
	 * @param serializeId
	 * @param params
	 * @param dealAction
	 * @throws Exception
	 */
	public static void beginImportAdvanceExcel(String serializeId, IData params, IDealData dealAction) throws Exception{
		String fileId = params.getString("fileId");
		String config = params.getString("config");
		String posX = params.getString("posX");
		String posY = params.getString("posY");
		int pos_x = Integer.parseInt(posX);
		int pos_y = Integer.parseInt(posY);
		beginImportAdvanceExcel(serializeId, fileId, ExcelConfig.getSheets(config), dealAction, pos_x, pos_y);
	}
	
	/**
	 * 
	 * @param serializeId
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IData beginImport(String serializeId, IData params) throws Exception{
		String fileId = params.getString("fileId");
		String config = params.getString("config");
		String fileType = params.getString("fileType");
		String posX = params.getString("posX");
		String posY = params.getString("posY");
		int pos_x = Integer.parseInt(posX);
		int pos_y = Integer.parseInt(posY);
		IData data = null;
		if(!StringUtils.isEmpty(fileType)){
			if( "excel".equalsIgnoreCase(fileType) || "xls".equalsIgnoreCase(fileType) || "xlsx".equalsIgnoreCase(fileType) ){
				data = ImpExpUtil.beginImportExcel(serializeId, fileId, ExcelConfig.getSheets(config), pos_x, pos_y);
			}else if("txt".equalsIgnoreCase(fileType)){
				data = ImpExpUtil.beginImportTxt(serializeId, fileId, ExcelConfig.getSheets(config), null);
			}else if("csv".equalsIgnoreCase(fileType)){
				data = ImpExpUtil.beginImportCSV(serializeId, fileId, ExcelConfig.getSheets(config));
			}
		}
		return data;
	}

	/**
	 * 解析文件获得数据，支持文件Excel、Txt、CSV
	 * @param serializeId
	 * @param fileId
	 * @param configData
	 * @param pos_x
	 * @param pos_y
	 * @param txtSplit
	 * @return
	 * @throws Exception
	 */
	public static IData beginImport(String serializeId, String fileId, List configData, int pos_x, int pos_y,String txtSplit) throws Exception{
		Map<String, Object> fileInfo = getImpExpManager().getFileAction().query(fileId);
		if(fileInfo == null){
			Utility.error(CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.fileinfo"));
		}
		String fileName = (String)fileInfo.get("fileName");
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		if(fileType.equalsIgnoreCase("TXT")){
			return beginImportTxt(serializeId, fileId, configData, txtSplit);
		}else if(fileType.equalsIgnoreCase("CSV")){
			return beginImportCSV(serializeId, fileId, configData);
		}else if(fileType.equalsIgnoreCase("XLS") || fileType.equalsIgnoreCase("XLSX")){
			return beginImportExcel(serializeId, fileId, configData, pos_x, pos_y);
		}
		return null;
	}
	
	/**
	 * 解析文件获得数据，支持文件Excel、Txt、CSV
	 * @param serializeId
	 * @param fileId
	 * @param fileType 文件类型 Excel、Txt、CSV
	 * @param configData
	 * @return
	 * @throws Exception
	 */
	public static IData beginImport(String serializeId, String fileId,String fileType, List configData) throws Exception{
		return beginImport( serializeId, fileId, fileType, configData, 0, 0, null);
	}
	
	/**
	 * 解析文件获得数据，支持文件Excel、Txt、CSV
	 * @param serializeId
	 * @param fileId
	 * @param fileType 文件类型 Excel、Txt、CSV
	 * @param configData
	 * @param pos_x
	 * @param pos_y
	 * @param txtSplit
	 * @return
	 * @throws Exception
	 */
	public static IData beginImport(String serializeId, String fileId, String fileType, List configData, int pos_x, int pos_y,String txtSplit) throws Exception{

		if(fileType.equalsIgnoreCase("TXT")){
			return beginImportTxt(serializeId, fileId, configData, txtSplit);
		}else if(fileType.equalsIgnoreCase("CSV")){
			return beginImportCSV(serializeId, fileId, configData);
		}else if(fileType.equalsIgnoreCase("excel") || fileType.equalsIgnoreCase("XLS") || fileType.equalsIgnoreCase("XLSX")){
			return beginImportExcel(serializeId, fileId, configData, pos_x, pos_y);
		}
		return null;
	}

	/**
	 * 解析Excel返回数据(验证成功和失败)，并刷新进度信息 
	 * IDataset[] right = data.get("right")
	 * IDataset[] error = data.get("error")
	 * @param serializeId
	 * @param fileId
	 * @param configData
	 * @param pos_x
	 * @param pos_y
	 * @return IData(String,IDataset[])
	 */
	public static IData beginImportExcel(String serializeId, String fileId, List configData, int pos_x, int pos_y) throws Exception {
		
		IData data = null;
		File file = null;
		InputStream input = null;
		try {
			file = getImpExpManager().getFileAction().download(fileId);
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "20", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.gotfile"), "");
			}
			input = new FileInputStream(file);
			data = ExcelReader.readExcelToData(configData, input, pos_x, pos_y);
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parse"), "");
			}
		} catch (FileNotFoundException e){
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.nofile")+e.getMessage(), "");
			}
			throw e;
		} catch (Exception e) {
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parseexcp")+e.getMessage(), "");
			}
			throw e;
		} finally {
			if(GlobalCfg.getProperty("fileman.mode", "local").equals("ftp") && file!=null && file.exists()){
				file.delete();//删除本地临时导入文件
			}
		}
		return data;
	}
	
	/**
	 * 支持大文件的读取, 仅适用于excel07及以上版本
	 * @param serializeId
	 * @param fileId
	 * @param configData
	 * @param dealAction
	 * @param pos_x
	 * @param pos_y
	 * @return
	 */
	public static void beginImportAdvanceExcel(String serializeId, String fileId, List configData, IDealData dealAction, int pos_x, int pos_y) throws Exception {
		File file = null;
		try {
			file = getImpExpManager().getFileAction().download(fileId);
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "20", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.gotfile"), "");
			}
			AdvanceDataReadWrite.readExcel(configData, file, dealAction, pos_x, pos_y);
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "100", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parse"), "");
			}
		} catch (FileNotFoundException e){
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.nofile")+e.getMessage(), "");
			}
			throw e;
		} catch (Exception e) {
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parseexcp")+e.getMessage(), "");
			}
			throw e;
		} finally {
			if(GlobalCfg.getProperty("fileman.mode", "local").equals("ftp") && file!=null && file.exists()){
				file.delete();//删除本地临时导入文件
			}
		}
	}
	
	/**
	 * 解析CSV返回数据(验证成功和失败)
	 * IDataset right = data.getDataset("right")
	 * IDataset error = data.getDataset("error")
	 * @param serializeId
	 * @param fileId
	 * @param configData
	 * @return IData(String,IDataset)
	 */
	public static IData beginImportTxt(String serializeId, String fileId, List configData, String split) throws Exception {
		
		IData data = null;
		File file = null;
		InputStream input = null;
		try {
			file = getImpExpManager().getFileAction().download(fileId);
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "20", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.gotfile"), "");
			}
			input = new FileInputStream(file);
			
			data = new SimpleParser().readTxtToData(input, (Element)configData.get(0),split);
			
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parse"), "");
			}
		} catch (FileNotFoundException e){
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.nofile")+e.getMessage(), "");
			}
			throw e;
		} catch (Exception e) {
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parseexcp")+e.getMessage(), "");
			}
			throw e;
		} finally {
			if(GlobalCfg.getProperty("fileman.mode", "local").equals("ftp") && file!=null && file.exists()){
				file.delete();//删除本地临时导入文件
			}
		}
		return data;
	}

	/**
	 * 解析CSV返回数据(验证成功和失败)
	 * IDataset right = data.getDataset("right")
	 * IDataset error = data.getDataset("error")
	 * @param serializeId
	 * @param fileId
	 * @param configData
	 * @return IData(String,IDataset)
	 */
	public static IData beginImportCSV(String serializeId, String fileId, List configData) throws Exception {

		IData data = null;
		File file = null;
		InputStream input = null;
		try {
			file = getImpExpManager().getFileAction().download(fileId);
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "20", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.gotfile"), "");
			}
			input = new FileInputStream(file);
			
			data = new SimpleParser().readCSVToData(input, (Element)configData.get(0));

			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "60", "ok", "", CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parse"), "");
			}
		} catch (FileNotFoundException e){
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.nofile")+e.getMessage(), "");
			}
			throw e;
		} catch (Exception e) {
			if(!StringUtils.isEmpty(serializeId)){
				getImpExpManager().setStatus(serializeId, "0", "error", "0",CodeCfg.getProperty("com.ailk.common.util.parser.ImpExpUtil.import.parseexcp")+e.getMessage(), "");
			}
			throw e;
		} finally {
			if(GlobalCfg.getProperty("fileman.mode", "local").equals("ftp") && file!=null && file.exists()){
				file.delete();//删除本地临时导入文件
			}
		}
		return data;
	}
	
	/**
	 * 解析Excel返回数据(验证成功和失败)
	 * IDataset[] right = data.get("right")
	 * IDataset[] error = data.get("error")
	 * @param fileId
	 * @param configData
	 * @param pos_x
	 * @param pos_y
	 * @return IData(String,IDataset[])
	 */
	public static IData beginImportExcel(String fileId, List configData, int pos_x, int pos_y) throws Exception {
		return beginImportExcel(null,  fileId,  configData, pos_x,  pos_y);
	}
	
	/**
	 * 解析TXT返回数据(验证成功和失败)
	 * IDataset right = data.getDataset("right")
	 * IDataset error = data.getDataset("error")
	 * @param fileId
	 * @param configData
	 * @param split
	 * @return IData(String,IDataset)
	 */
	public static IData beginImportTxt(String fileId, List configData, String split) throws Exception {
		return beginImportTxt(null, fileId, configData, split);
	}
	
	/**
	 * 解析CSV返回数据(验证成功和失败)
	 * IDataset right = data.getDataset("right")
	 * IDataset error = data.getDataset("error")
	 * @param fileId
	 * @param configData 配置数据 
	 * @return IData(String,IDataset)
	 */
	public static IData beginImportCSV(String fileId, List configData) throws Exception {
		return beginImportCSV(null, fileId, configData);
	}


	/**
	 * 生成Excel临时文件名（导入时从FTP下载到本地，导出时在本地生成上传到FTP）
	 * @return
	 */
	public synchronized static String getFileName(String tag){
		if(c > 999000)
			 c = 100000;
		c++;
		return tag+"_"+String.valueOf(System.currentTimeMillis())+String.valueOf(c);
	}
	
	public static File createWriteExcelFile(String filePath) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		tmpdir = tmpdir + "/" + getFileName(exportExcel);
		if (log.isDebugEnabled()) {
			log.debug(">>>创建临时文件[" + tmpdir + "]");
		}
		
		File f = new File(tmpdir);
		return f;
	}

	/**
	 * 生成excel文件
	 * @param datasets
	 * @param ftpSite
	 * @param filePath
	 * @param excelCfg
	 * @param excelModel
	 * @param excelVersion
	 * @param pos_x
	 * @param pos_y
	 * @throws Exception
	 */
	public static File writeExcelData(IDataset[] datasets,String filePath,
			String excelModel,List excelCfgData,int excelVersion, int pos_x, int pos_y) throws Exception{
		
		File f = createWriteExcelFile(filePath);
		FileOutputStream fos = new FileOutputStream(f);
		try{
			//导出模板
			InputStream excelModelStream = null;
			if( excelModel != null && !"".equals(excelModel) ){
				excelModelStream = Utility.getClassResourceStream(excelModel);
			}
			
			if( excelVersion == excel_07 ){
				ExcelWriter.writeExcel07FromData(excelCfgData, fos, excelModelStream, datasets, pos_x, pos_y);
			}else{
				ExcelWriter.writeExcel03FromData(excelCfgData, fos, excelModelStream, datasets, pos_x, pos_y);
			}
		}catch(IOException e){
			throw e;
		} finally {
			if(fos!=null){
				fos.flush();
				fos.close();
			}
		}
		return f;
	}
	public static void appendExcelData(IDataset[] datasets,String fileId,
			String excelModel,List excelCfgData, int excelVersion,int pos_x, int pos_y) throws Exception{
		fileId = fileId.intern();
		FileOutputStream fos = null;
		ZipOutputStream zipout = null;
		//导出模板
		InputStream excelModelStream = null;
		int entrySize = 0;
		ZipInputStream zis = null;
		FileInputStream inputStream = null;
		File file = null;
		File tempFile = null;
		try{
			synchronized(fileId){
				file = new File(fileId);
				if(file.exists()){
					tempFile = new File(fileId+"_temp");
					boolean rename = file.renameTo(tempFile);
					int i = 0;
					while(!rename){
						rename = file.renameTo(tempFile);
						if(i>10){
							Utility.error("failed to rename file");
						}
						i++;
					}
					inputStream = new FileInputStream(tempFile);
					zis = new ZipInputStream(inputStream);
					
					fos = new FileOutputStream(file,true);
					zipout = new ZipOutputStream(fos);
					ZipEntry ze ;
					while((ze = zis.getNextEntry())!=null){
						entrySize++;
						zipout.putNextEntry(ze);
						FileManHelper.writeInputToOutput(zis, zipout, true);
					}
					if(zis!=null){
						zis.close();
					}
					if(tempFile.exists()){
						tempFile.delete();
						if(tempFile.exists()){
							tempFile.delete();
						}
					}
				}else{
					fos = new FileOutputStream(new File(fileId),true);
					zipout = new ZipOutputStream(fos);
				}
				if( excelModel != null && !"".equals(excelModel) ){
					excelModelStream = Utility.getClassResourceStream(excelModel);
				}
				String entryName = StringUtils.replaceOnce(fileId,".zip","")+"_"+entrySize;
				if( excelVersion == excel_07 ){
					ExcelWriter.appendExcel07FromData(excelCfgData, zipout, entryName, excelModelStream, datasets, pos_x, pos_y);
				}else{
					ExcelWriter.appendExcel03FromData(excelCfgData, zipout, entryName, excelModelStream, datasets, pos_x, pos_y);
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(inputStream!=null){
				inputStream.close();
			}
			if(excelModelStream!=null){
				excelModelStream.close();
			}
			if(zipout!=null){
				zipout.flush();
				zipout.close();
			}
			if(fos!=null){
				fos.flush();
				fos.close();
			}
		}
	}
	
	public static File writeTxtFromData(IDataset dataSet,String fileName,Element sheet) throws Exception {
		return writeFromData(dataSet,FilePattern.TXT,fileName,sheet);
	}
	
	public static File writeTxtFromData(IDataset dataSet,Element sheet) throws Exception {
		return writeFromData(dataSet,FilePattern.TXT,null,sheet);
	}

	public static File writeCSVFromData(IDataset dataSet,String fileName,Element sheet) throws Exception {
		return writeFromData(dataSet,FilePattern.CSV,fileName,sheet);
	}
	
	/**
	 * 把传入数据写入到CSV文件中
	 * 
	 * @param dataSet  传入数据集
	 * @param iDataKeys 所传入的数据需要按照一定顺序显示的话，则在此处传入对应的key值
	 * @param headNames 文件标题信息
	 * @param fileName 文件名
	 * @return
	 */
	public static File writeCSVFromData(IDataset dataSet,Object[] iDataKeys,String[] headNames,String fileName,Element sheet) throws Exception {
		return writeFromData(dataSet,iDataKeys,headNames,FilePattern.CSV,fileName,sheet);
	}
	
	public static File writeCSVFromData(IDataset dataSet,Element sheet) throws Exception {
		return writeFromData(dataSet,FilePattern.CSV,null,sheet);
	}
	
	public static File writeCSVFromData(IDataset dataSet,Object[] iDataKeys,String[] headNames,Element sheet) throws Exception {
		return writeFromData(dataSet,iDataKeys,headNames,FilePattern.CSV,null,sheet);
	}
	
	/**
	 * 不区分文件类型进行写文件，但只针对简单的文件类型如CSV,TXT等，用户也可根据需要扩展FilePattern
	 * 
	 * @param dataSet 传入的数据集
	 * @param filePattern 需要生成的文件类型 如FilePattern.TXT  FilePattern.CSV
	 * @param fileName 文件名
	 * @return
	 */
	public static File writeFromData(IDataset dataSet,FilePattern filePattern,String fileName,Element sheet) throws Exception {
		try {
			return new SimpleParser().writeFileFromData(dataSet,null,null,filePattern,fileName,sheet);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static File writeFromData(IDataset dataSet,Object[] iDataKeys,String[] headNames,FilePattern filePattern,String fileName,Element sheet) throws Exception {
		try {
			return new SimpleParser().writeFileFromData(dataSet,iDataKeys,headNames,filePattern,fileName,sheet);
		} catch (Exception e) {
			throw e;
		}
	}
	public static IData readCSVToData(InputStream in,Element sheet) throws Exception {
		return new SimpleParser().readToData(in,null,null,FilePattern.CSV,sheet);
	}
	
	public static IData readCSVToData(InputStream in,Object[] iDataKeys,String[] headNames,Element sheet) throws Exception {
		return new SimpleParser().readToData(in,iDataKeys,headNames,FilePattern.CSV,sheet);
	}
	
	/**
	 * 读CSV文件
	 * @param in 需要被读的文件流
	 * @param iDataKeys 生成数据每一列对应的key
	 * @return
	 */
	public static IData readCSVToData(InputStream in,IDataset iDataKeys,String[] headNames,Element sheet) throws Exception {
		return new SimpleParser().readToData(in,iDataKeys.toArray(),headNames,FilePattern.CSV,sheet);
	}
	
	public static IData readCSVToData(String fileName,Element sheet) throws Exception {
		try {
			InputStream in = new FileInputStream(fileName);
			return new SimpleParser().readToData(in,null,null,FilePattern.CSV,sheet);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	public static IData readCSVToData(String fileName,String[] iDataKeys,String[] headNames,Element sheet) throws Exception {
		try {
			InputStream in = new FileInputStream(fileName);
			return new SimpleParser().readToData(in,iDataKeys,headNames,FilePattern.CSV,sheet);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	public static IData readCSVToData(String fileName,IDataset iDataKeys,String[] headNames,Element sheet) throws Exception {
		try {
			InputStream in = new FileInputStream(fileName);
			return new SimpleParser().readToData(in,iDataKeys.toArray(),headNames,FilePattern.CSV,sheet);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}

	public static IData readTxtToData(String fileName,Element sheet) throws Exception {
		try {
			InputStream in = new FileInputStream(fileName);
			return new SimpleParser().readToData(in,null,null,FilePattern.TXT,sheet);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	public static IData readTxtToData(String fileName,String[] iDataKeys,String[] headNames,Element sheet) throws Exception {
		try {
			InputStream in = new FileInputStream(fileName);
			return new SimpleParser().readToData(in,iDataKeys,headNames,FilePattern.TXT,sheet);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	public static IData readTxtToData(String fileName,IDataset iDataKeys,String[] headNames,Element sheet) throws Exception {
		try {
			InputStream in = new FileInputStream(fileName);
			return new SimpleParser().readToData(in,iDataKeys.toArray(),headNames,FilePattern.TXT,sheet);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	public static IData readTxtToData(InputStream in,Element sheet) throws Exception {
		return new SimpleParser().readToData(in,null,null,FilePattern.TXT,sheet);
	}
	
	public static IData readTxtToData(InputStream in,Object[] iDataKeys,String[] headNames,Element sheet) throws Exception {
		return new SimpleParser().readToData(in,iDataKeys,headNames,FilePattern.TXT,sheet);
	}
	
	public static IData readTxtToData(InputStream in,IDataset iDataKeys,String[] headNames,Element sheet) throws Exception {
		return new SimpleParser().readToData(in,iDataKeys.toArray(),headNames,FilePattern.TXT,sheet);
	}
	
	
	public static void main(String[] args) throws Exception {
		String fileSerializeId = "1";
		String fileName = "abc";
		Map<String, Object> params = new HashMap<String, Object>();
		
		IDataset[] datasets = new DatasetList[2];
		String excelCfg = "export/test.xml";
		String excelModel = "export/test.xls";
		String filePath = "rs.xls";
		
		IDataset ds = new DatasetList();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("name1", String.valueOf(i));
			d.put("name2", String.valueOf(i));
			d.put("name3", String.valueOf(i));
			
			ds.add(d);
		}
		datasets[0] = ds;
		datasets[1] = ds;
		
		String tmpdir = System.getProperty("java.io.tmpdir");
		System.out.println(tmpdir);
		File file = ImpExpUtil.writeExcelData(datasets, filePath, excelModel, ExcelConfig.getSheets(excelCfg), ImpExpUtil.excel_03, 0, 0);
		System.out.println(file.getAbsolutePath());
	}
}
