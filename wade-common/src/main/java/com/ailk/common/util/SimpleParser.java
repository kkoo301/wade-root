package com.ailk.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.ailk.org.apache.commons.io.FilenameUtils;

import org.dom4j.Element;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelCommon;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.Validate;

/**
 * 
 * 用于将data与txt文件或csv文件之间的转换
 * 
 * @author lvchao
 * 
 */
public class SimpleParser {

	/**
	 * return : filePath
	 */
	public File writeTxtFromDataAdvance(IDataset dataSet, String fileName, Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return writeFromDataAdvance(dataSet, txt, fileName, sheet, dealAction);
	}

	public File writeTxtFromDataAdvance(IDataset dataSet, String fileName, Element sheet, IDealData dealAction) {
		return writeTxtFromDataAdvance(dataSet, fileName, sheet, null, dealAction);
	}

	public File writeTxtFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, 
			Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return writeFromDataAdvance(dataSet, iDataKeys, headNames, txt, fileName, sheet, dealAction);
	}

	public File writeTxtFromDataAdvance(IDataset dataSet, Element sheet, IDealData dealAction) {
		return writeTxtFromDataAdvance(dataSet, sheet, null, dealAction);
	}

	public File writeTxtFromDataAdvance(IDataset dataSet, Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return writeFromDataAdvance(dataSet, txt, null, sheet, dealAction);
	}

	public File appendTxtFromDataAdvance(IDataset dataSet, String fileName, Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return appendFromDataAdvance(dataSet, txt, fileName, sheet, dealAction);
	}

	public File appendTxtFromDataAdvance(IDataset dataSet, String fileName, Element sheet, IDealData dealAction) {
		return appendTxtFromDataAdvance(dataSet, fileName, sheet, null, dealAction);
	}

	public File appendTxtFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, 
			Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return appendFromDataAdvance(dataSet, iDataKeys, headNames, txt, fileName, sheet, dealAction);
	}

	public File appendTxtFromDataAdvance(IDataset dataSet, Element sheet, IDealData dealAction) {
		return appendTxtFromData(dataSet, sheet, null);
	}

	public File appendTxtFromDataAdvance(IDataset dataSet, Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return appendFromDataAdvance(dataSet, txt, null, sheet, dealAction);
	}

	/**
	 * return : filePath
	 */
	public File writeCSVFromDataAdvance(IDataset dataSet, String fileName, Element sheet, IDealData dealAction) {
		return writeFromDataAdvance(dataSet, FilePattern.CSV, fileName, sheet, dealAction);
	}

	public File writeCSVFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, Element sheet, IDealData dealAction) {
		return writeFromDataAdvance(dataSet, iDataKeys, headNames, FilePattern.CSV, fileName, sheet, dealAction);
	}

	public File appendCSVFromDataAdvance(IDataset dataSet, Element sheet, IDealData dealAction) {
		return appendFromDataAdvance(dataSet, FilePattern.CSV, null, sheet, dealAction);
	}

	public File appendCSVFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		return appendFromDataAdvance(dataSet, iDataKeys, headNames, FilePattern.CSV, null, sheet, dealAction);
	}

	public File appendCSVFromDataAdvance(IDataset dataSet, String fileName, Element sheet, IDealData dealAction) {
		return appendFromDataAdvance(dataSet, FilePattern.CSV, fileName, sheet, dealAction);
	}

	public File appendCSVFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, Element sheet, IDealData dealAction) {
		return appendFromDataAdvance(dataSet, iDataKeys, headNames, FilePattern.CSV, fileName, sheet, dealAction);
	}

	public File writeCSVFromDataAdvance(IDataset dataSet, Element sheet, IDealData dealAction) {
		return writeFromDataAdvance(dataSet, FilePattern.CSV, null, sheet, dealAction);
	}

	public File writeCSVFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		return writeFromDataAdvance(dataSet, iDataKeys, headNames, FilePattern.CSV, null, sheet, dealAction);
	}

	public File writeFromDataAdvance(IDataset dataSet, FilePattern filePattern, String fileName, Element sheet, IDealData dealAction) {
		File file = null;
		try {
			file = writeFileFromDataAdvance(dataSet, null, null, filePattern, fileName, sheet, dealAction);
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e);
		}
		return file;
	}

	public File writeFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet, IDealData dealAction) {
		File file = null;
		try {
			file = writeFileFromDataAdvance(dataSet, iDataKeys, headNames, filePattern, fileName, sheet, dealAction);
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e);
		}
		return file;
	}

	public File appendFromDataAdvance(IDataset dataSet, FilePattern filePattern, String fileName, Element sheet, IDealData dealAction) {
		try {
			return appendFileFromDataAdvance(dataSet, null, null, filePattern, fileName, sheet, dealAction);
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e);
		}
		return null;
	}

	public File appendFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet, IDealData dealAction) {
		try {
			return appendFileFromDataAdvance(dataSet, iDataKeys, headNames, filePattern, fileName, sheet, dealAction);
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e);
		}
		return null;
	}

	public File appendFileFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet, IDealData dealAction) throws Exception {	
		String filePath = FilenameUtils.concat(System.getProperty("java.io.tmpdir",""), fileName);
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file, true);
		if (!writeFileDataAdvance(fw, dataSet, iDataKeys, headNames, filePattern, sheet, dealAction)) {
			if (file != null) {
				file.deleteOnExit();
			}
			return null;
		}
		return file;
	}

	public File writeFileFromDataAdvance(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet, IDealData dealAction) throws Exception {
		if (StringUtils.isBlank(fileName)) {
			fileName = Utility.getUniqeName();
		}	
		String filePath = FilenameUtils.concat(System.getProperty("java.io.tmpdir",""), fileName);
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file);
		
		//写入BOM信息，UTF-8 文件 中文乱码问题解决  xiedx 2017/2/14
		fw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));
		
		if (!writeFileDataAdvance(fw, dataSet, iDataKeys, headNames, filePattern, sheet, dealAction)) {
			if (file != null) {
				file.deleteOnExit();
			}
			return null;
		}
		return file;
	}

	public boolean writeFileDataAdvance(FileWriter fw, IDataset dataSet, Object[] iDataKeys, String[] headNames, 
			FilePattern filePattern, Element sheet, IDealData dealAction) throws Exception {
		try {
			int dataSize = dataSet == null ? 0 : dataSet.size();
			String sheetName = (sheet == null) ? "" : sheet.attributeValue("desc");
			if (dealAction != null) {
				dealAction.begin(sheetName, ExcelCommon.getSheetAttrs(sheet));
			}
			if (headNames != null) {
				writeArrayDataToFile(fw, headNames, filePattern, sheet, true);
			}
			for (int i = 0; i < dataSize; i++) {
				if (!writeDataToFile(fw, dataSet.get(i), iDataKeys, headNames, filePattern, sheet, dealAction)){
					return false;
				}
			}
			if (dealAction != null) {
				dealAction.end(sheetName);
				dealAction.over();
			}
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e.getMessage());
		} finally {
			if (fw != null) {
				fw.flush();
				fw.close();
			}
		}
		return true;
	}

	public IData readCSVToDataAdvance(InputStream in, Element sheet, IDealData dealAction) {
		return readToDataAdvance(in, null, null, FilePattern.CSV, sheet, dealAction);
	}

	public IData readCSVToDataAdvance(InputStream in, Object[] iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		return readToDataAdvance(in, iDataKeys, headNames, FilePattern.CSV, sheet, dealAction);
	}

	public IData readCSVToDataAdvance(InputStream in, IDataset iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		return readToDataAdvance(in, iDataKeys.toArray(), headNames, FilePattern.CSV, sheet, dealAction);
	}

	public IData readCSVToDataAdvance(String fileName, Element sheet, IDealData dealAction) {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			return readToDataAdvance(in, null, null, FilePattern.CSV, sheet, dealAction);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Utility.getBottomException(e).printStackTrace();
					Utility.error(e.getMessage());
				}
			}
		}
		return null;
	}

	public IData readCSVToDataAdvance(String fileName, Object[] iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			return readToDataAdvance(in, iDataKeys, headNames, FilePattern.CSV, sheet, dealAction);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Utility.getBottomException(e).printStackTrace();
					Utility.error(e.getMessage());
				}
			}
		}
		return null;
	}

	public IData readCSVToDataAdvance(String fileName, IDataset iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			return readToDataAdvance(in, iDataKeys.toArray(), headNames, FilePattern.CSV, sheet, dealAction);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Utility.getBottomException(e).printStackTrace();
					Utility.error(e.getMessage());
				}
			}
		}
		return null;
	}

	public IData readTxtToDataAdvance(String fileName, Element sheet, IDealData dealAction) {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			return readToDataAdvance(in, null, null, FilePattern.TXT, sheet, dealAction);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Utility.getBottomException(e).printStackTrace();
					Utility.error(e.getMessage());
				}
			}
		}
		return null;
	}

	public IData readTxtToDataAdvance(String fileName, Object[] iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			return readToDataAdvance(in, iDataKeys, headNames, FilePattern.TXT, sheet, dealAction);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Utility.getBottomException(e).printStackTrace();
					Utility.error(e.getMessage());
				}
			}
		}
		return null;
	}

	public IData readTxtToDataAdvance(String fileName, IDataset iDataKeys, String[] headNames, Element sheet, IDealData dealAction) {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
			return readToDataAdvance(in, iDataKeys.toArray(), headNames, FilePattern.TXT, sheet, dealAction);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Utility.getBottomException(e).printStackTrace();
					Utility.error(e.getMessage());
				}
			}
		}
		return null;
	}

	public IData readTxtToDataAdvance(InputStream in, Element sheet, IDealData dealAction) {
		return readToDataAdvance(in, null, null, FilePattern.TXT, sheet, dealAction);
	}

	public IData readTxtToDataAdvance(InputStream in, Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return readToDataAdvance(in, null, null, txt, sheet, dealAction);
	}

	public IData readTxtToDataAdvance(InputStream in, Object[] iDataKeys, String[] headNames, Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return readToDataAdvance(in, iDataKeys, headNames, txt, sheet, dealAction);
	}

	public IData readTxtToDataAdvance(InputStream in, IDataset iDataKeys, String[] headNames, Element sheet, String token, IDealData dealAction) {
		FilePattern txt = FilePattern.TXT;
		if (StringUtils.isNotEmpty(token)) {
			txt.setToken(token);
		}
		return readToDataAdvance(in, iDataKeys.toArray(), headNames, txt, sheet, dealAction);
	}

	public IData readToDataAdvance(InputStream in, Object[] iDataKeys, String[] headNames, FilePattern filePattern, Element sheet, IDealData dealAction) {
		IData ds = new DataMap();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String spiltTag = (filePattern.columnPrefix == null ? "" : filePattern.columnPrefix) + filePattern.token + (filePattern.columnSuffix == null ? "" : filePattern.columnSuffix);

		if ((iDataKeys == null || headNames == null) && sheet != null) {
			Element header = sheet.element("header");
			List<Element> cells = header.elements();

			List iDataKeysList = new LinkedList();
			List headNamesList = new LinkedList();
			for (Element cell : cells) {
				iDataKeysList.add(cell.attributeValue("name"));
				headNamesList.add(cell.attributeValue("desc"));
			}
			iDataKeys = iDataKeysList.toArray();
			headNames = (String[]) headNamesList.toArray(new String[cells.size()]);// .toArray();
		}

		try {
			if (iDataKeys == null || iDataKeys.length == 0) {
				ds = readFileToArrayData(ds, br, spiltTag, headNames, filePattern, dealAction);
			} else {
				ds = readFileToMapData(ds, br, spiltTag, iDataKeys, headNames, filePattern, sheet, dealAction);
			}
			if (br != null) {
				br.close();
			}
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			Utility.error(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Utility.getBottomException(e).printStackTrace();
					Utility.error(e.getMessage());
				}
			}
		}

		return ds;
	}

	private IData readFileToArrayData(IData ds, BufferedReader br, String spiltTag, String[] headNames, 
			FilePattern filePattern, IDealData dealAction) throws IOException {
		String str = null;
		boolean dealHead = false;
		if (headNames == null && headNames.length <= 0) {
			dealHead = true;
		}
		IDataset[] rightData = { new DatasetList() };
		
		if (dealAction != null) {
			dealAction.begin(null, null);
		}
		int idx = 0;
		while ((str = br.readLine()) != null) {
			if (StringUtils.isNotBlank(str)) {
				//处理BOM，前4个byte值为-17,-69,-65,49
				if (idx == 0) {
					byte[] b = str.getBytes();
					if (b[0] == -17 && b[1] == -69 && b[2] == -65 && b[3] == 49) {
						str = new String(Arrays.copyOfRange(b, 3, b.length));
					}
				}
				
				idx ++;
				
				String[] strs = str.split(spiltTag);
				int strsLength = strs.length;
				String[] changedData = new String[strsLength];
				if (!dealHead) {
					dealHead = true;
					// 默认对文本中的第一行非空数据进行判断，辨别是否为文件表头信息，若为表头则继续判断下一行，不处理改行的数据
					boolean isHead = true;
					if (strsLength == headNames.length) {
						for (int i = 0; i < strsLength; i++) {
							String s = strs[i];
							if ((s == null || "".equals(s)) && (headNames[i] == null || "".equals(headNames[i])))
								continue;
							if (filePattern == FilePattern.CSV) {
								if (!(s.trim().equals(headNames[i].trim()) || s.trim().equals(headNames[i].trim() + "\"") || s.trim().equals("\"" + headNames[i].trim()))) {
									isHead = false;
									break;
								}
							} else {
								if (!s.trim().equals(headNames[i].trim())) {
									isHead = false;
									break;
								}
							}
						}
					}else{
						isHead = false;
					}
					if (isHead)
						continue;
				}
				if (filePattern.isNeedEncode()) {
					for (int i = 0; i < strsLength; i++) {
						changedData[i] = decodeColumnStr(strs[i], filePattern);
					}
				} else {
					changedData = strs;
				}
				
				if (dealAction != null && !dealAction.execute(changedData, true, null)) {
					return null;
				} else {
					rightData[0].add(changedData);
				}
			}
		}
		
		if (dealAction != null) {
			dealAction.end(null);
			dealAction.over();
		}
		ds.put("right", rightData);
		ds.put("rightCount", rightData[0].size());
		return ds;
	}

	private IData readFileToMapData(IData ds, BufferedReader br, String spiltTag, Object[] iDataKeys, String[] headNames, 
			FilePattern filePattern, Element sheet, IDealData dealAction) throws Exception {
		String str = null;
		// int keysLength = iDataKeys==null ? 0 : iDataKeys.length;
		IData data = null;
		boolean dealHead = false;
		if (headNames == null || headNames.length <= 0) {
			dealHead = true;
		}
		String sheetName = null;
		List<Element> cells = null;
		if (sheet != null) {
			Element header = sheet.element("header");
			sheetName = sheet.attributeValue("desc");
			cells = header.elements();
		}
		IDataset[] rightData = { new DatasetList() };
		IDataset[] errorData = { new DatasetList() };
		
		if (dealAction != null) {
			dealAction.begin(sheetName, ExcelCommon.getSheetAttrs(sheet));
		}
		int idx = 0;
		while ((str = br.readLine()) != null) {
			if (StringUtils.isNotBlank(str)) {
				//处理BOM，前4个byte值为-17,-69,-65,49
				if (idx == 0) {
					byte[] b = str.getBytes();
					if (b[0] == -17 && b[1] == -69 && b[2] == -65 && b[3] == 49) {
						str = new String(Arrays.copyOfRange(b, 3, b.length));
					}
				}
				
				idx ++;
				
				StringBuilder error = new StringBuilder();
				String[] strs = str.split(spiltTag);
				data = new DataMap();
				int strLength = strs.length;
				if (!dealHead) {
					// 默认对文本中的第一行非空数据进行判断，辨别是否为文件表头信息，若为表头则继续判断下一行，不处理改行的数据
					boolean isHead = true;
					dealHead = true;
					if (strLength == headNames.length) {
						for (int i = 0; i < strLength; i++) {
							String s = strs[i];
							if ((s == null || "".equals(s)) && (headNames[i] == null || "".equals(headNames[i])))
								continue;
							if (filePattern == FilePattern.CSV) {
								if (!(s.trim().equals(headNames[i].trim()) || s.trim().equals(headNames[i].trim() + "\"") || s.trim().equals("\"" + headNames[i].trim()))) {
									isHead = false;
									break;
								}
							} else {
								if (!s.trim().equals(headNames[i].trim())) {
									isHead = false;
									break;
								}
							}
						}
					}else{
						isHead = false;
					}
					if (isHead)
						continue;
				}
				
				int headLength = headNames == null ? 0 : headNames.length;
				for (int i = 0; i < headLength; i++) {
					if(i>=strLength){
						data.put((String) iDataKeys[i], "");
					}else{
						data.put((String) iDataKeys[i], decodeColumnStr(strs[i], filePattern));
					}
					if (cells != null && cells.size() > i) {
						error.append(Validate.verifyCell(cells.get(i), data.getString((String) iDataKeys[i], "")));
					}
				}
				String errorStr = error.toString();
				boolean isRight = StringUtils.isBlank(errorStr);
				if (dealAction != null && !dealAction.execute(data, isRight, errorStr)) {
					return null;
				} else {
					if (data.size() != 0) {
						data.put("IMPORT_RESULT", String.valueOf(isRight));
						if (isRight) {
							rightData[0].add(data);
						} else {
							data.put("IMPORT_ERROR", errorStr);
							errorData[0].add(data);
						}
					}
				}
				// ds.add(data);
			}
		}
		
		if (dealAction != null) {
			dealAction.end(sheetName);
			dealAction.over();
		}
		ds.put("right", rightData);
		ds.put("error", errorData);
		ds.put("rightCount", rightData[0].size());
		ds.put("errorCount", errorData[0].size());
		return ds;
	}

	private String decodeColumnStr(String str, FilePattern filePattern) {
		if (str != null && filePattern.isNeedEncode()) {
			String[] afterEncode = filePattern.afterEncode;
			if (afterEncode == null || afterEncode.length <= 0) {
				return str;
			}
			int encodeLength = afterEncode.length;
			for (int i = 0; i < encodeLength; i++) {
				str = str.replaceAll(afterEncode[i], filePattern.beforeEncode[i]);
			}
			str = Utility.trimPrefix(str, filePattern.columnPrefix);
			str = Utility.trimSuffix(str, filePattern.columnSuffix);
		}
		return str;
	}

	/*
	 * data:需写入的信息，支持IData,Array,List,其他类型的对象以该对象的toString内容输出; keys
	 * :若data是IData类型，且输出结果需要按照顺序排列时可通过设置keys的顺序实现; headNames
	 * :设置后可用于显示列的标题信息，需与keys对应;
	 */
	private boolean writeDataToFile(FileWriter fw, Object data, Object[] keys, String[] headNames, FilePattern filePattern, Element sheet, IDealData dealAction) throws Exception {
		if (data == null) {
			return true;
		}
		if (data instanceof IData) {
			Object[] dataStrs = null;
			// 如果存在keys ,则以keys中的顺序获取数据，否则将按hash顺序显示
			if (keys == null && sheet != null) {
				Element header = sheet.element("header");
				if (header != null && header.elements() != null && header.elements().size() > 0) {
					List<Element> cells = header.elements();
					List iDataKeysList = new LinkedList();
					for (Element cell : cells) {
						iDataKeysList.add(cell.attributeValue("name"));
					}
					keys = iDataKeysList.toArray();
				}
			}
			IData dataMap = (IData)data;
			if (keys == null) {
				keys = dataMap.keySet().toArray();
			}
			int keysLength = keys.length;
			dataStrs = new Object[keysLength];
			for (int i = 0; i < keysLength; i++) {
				dataStrs[i] = dataMap.get(keys[i]);
			}
			if (dealAction != null && !dealAction.execute(dataMap, true, null)) {
				return false;
			}
			writeArrayDataToFile(fw, dataStrs, filePattern, sheet, false);
		} else if (data instanceof List) {
			Object[] dataArr = ((List) data).toArray();
			if (dealAction != null && !dealAction.execute(dataArr, true, null)) {
				return false;
			}
			writeArrayDataToFile(fw, dataArr, filePattern, sheet, false);
		} else if (data.getClass().isArray()) {
			Object[] dataArr = (Object[])data;
			if (dealAction != null && !dealAction.execute(dataArr, true, null)) {
				return false;
			}
			writeArrayDataToFile(fw, dataArr, filePattern, sheet, false);
		} else {
			if (dealAction != null && !dealAction.execute(new Object[]{data}, true, null)) {
				return false;
			}
			fw.write(buildColumnStr(data.toString(), filePattern));
		}
		return true;
	}

	private void writeArrayDataToFile(FileWriter fw, Object[] data, FilePattern filePattern, Element sheet, boolean isHeader) throws Exception {
		StringBuilder rowData = new StringBuilder();
		List<Element> cells = null;
		if (!isHeader && sheet != null) {
			Element header = sheet.element("header");
			cells = header.elements();
		}

		int length = data != null ? data.length : 0;
		Object o;
		Element cell;
		for (int i = 0; i < length; i++) {
			// 增加根据配置文件验证数据代码段
			o = data[i];
			if (!isHeader && cells != null && cells.size() > i) {
				cell = cells.get(i);
				o = verifyData(cell, o == null ? "" : o.toString());
			}
			rowData.append(buildColumnStr(o == null ? "" : o.toString(), filePattern));
		}
		if (rowData.length() > 0) {
			fw.write(Utility.trimSuffix(rowData.toString(), filePattern.token) + "\r\n");
		}
	}

	private Object verifyData(Element cell, String str) throws Exception {
		String cell_type = cell.attributeValue("type");
		String cell_scale = cell.attributeValue("scale");
		String cell_format = cell.attributeValue("format");
		Object returnValue = str;
		if (str != null) {
			if (ExcelConfig.CELL_TYPE_NUMERIC.equals(cell_type)) {
				returnValue = cell_scale == null ? Double.parseDouble(str) : Double.parseDouble(str) / Double.parseDouble(cell_scale);
			}
			if (ExcelConfig.CELL_TYPE_DATETIME.equals(cell_type)) {
				returnValue = cell_format == null ? str : Utility.decodeTimestamp(cell_format, str);
			}
		}

		return returnValue;
	}

	private String buildColumnStr(String str, FilePattern filePattern) {
		str = str == null ? "" : str;
		if (filePattern.isNeedEncode) {
			String[] beforeEncode = filePattern.beforeEncode;
			for (int i = 0; i < beforeEncode.length; i++) {
				str = str.replaceAll(beforeEncode[i], filePattern.afterEncode[i]);
			}
			str = filePattern.columnPrefix + str + filePattern.columnSuffix;
		}
		str += filePattern.token;
		return str;
	}
	
	public File writeTxtFromData(IDataset dataSet, String fileName, Element sheet, String token) {
		return this.writeTxtFromDataAdvance(dataSet, fileName, sheet, token, null);
	}
	
	public File writeTxtFromData(IDataset dataSet, String fileName, Element sheet) {
		return this.writeTxtFromData(dataSet, fileName, sheet, null);
	}
	
	public File writeTxtFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, 
				Element sheet, String token) {
		return this.writeTxtFromDataAdvance(dataSet, iDataKeys, headNames, fileName, sheet, token, null);
	}

	public File writeTxtFromData(IDataset dataSet, Element sheet) {
		return this.writeTxtFromDataAdvance(dataSet, sheet, null);
	}
	
	public File writeTxtFromData(IDataset dataSet, Element sheet, String token) {
		return this.writeTxtFromDataAdvance(dataSet, sheet, token, null);
	}
	
	public File appendTxtFromData(IDataset dataSet, String fileName, Element sheet, String token) {
		return this.appendTxtFromDataAdvance(dataSet, fileName, sheet, token, null);
	}
	
	public File appendTxtFromData(IDataset dataSet, String fileName, Element sheet) {
		return this.appendTxtFromDataAdvance(dataSet, fileName, sheet, null);
	}
	
	public File appendTxtFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, 
			Element sheet, String token) {
		return this.appendTxtFromDataAdvance(dataSet, iDataKeys, headNames, fileName, sheet, token, null);
	}
	
	public File appendTxtFromData(IDataset dataSet, Element sheet) {
		return this.appendTxtFromDataAdvance(dataSet, sheet, null);
	}
	
	public File appendTxtFromData(IDataset dataSet, Element sheet, String token) {
		return this.appendTxtFromDataAdvance(dataSet, sheet, token, null);
	}
	
	public File writeCSVFromData(IDataset dataSet, String fileName, Element sheet) {
		return this.writeCSVFromDataAdvance(dataSet, fileName, sheet, null);
	}
	
	public File writeCSVFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, Element sheet) {
		return this.writeCSVFromDataAdvance(dataSet, iDataKeys, headNames, fileName, sheet, null);
	}
	
	public File appendCSVFromData(IDataset dataSet, Element sheet) {
		return this.appendCSVFromDataAdvance(dataSet, sheet, null);
	}
	
	public File appendCSVFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, Element sheet) {
		return this.appendCSVFromDataAdvance(dataSet, iDataKeys, headNames, sheet, null);
	}
	
	public File appendCSVFromData(IDataset dataSet, String fileName, Element sheet) {
		return this.appendCSVFromDataAdvance(dataSet, fileName, sheet, null);
	}
	
	public File appendCSVFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, String fileName, Element sheet) {
		return this.appendCSVFromDataAdvance(dataSet, iDataKeys, headNames, fileName, sheet, null);
	}
	
	public File writeCSVFromData(IDataset dataSet, Element sheet) {
		return this.writeCSVFromDataAdvance(dataSet, sheet, null);
	}
	
	public File writeCSVFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, Element sheet) {
		return this.writeCSVFromDataAdvance(dataSet, iDataKeys, headNames, sheet, null);
	}
	
	public File writeFromData(IDataset dataSet, FilePattern filePattern, String fileName, Element sheet) {
		return this.writeFromDataAdvance(dataSet, filePattern, fileName, sheet, null);
	}
	
	public File writeFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet) {
		return this.writeFromDataAdvance(dataSet, iDataKeys, headNames, filePattern, fileName, sheet, null);
	}
	
	public File appendFromData(IDataset dataSet, FilePattern filePattern, String fileName, Element sheet) {
		return this.appendFromDataAdvance(dataSet, filePattern, fileName, sheet, null);
	}
	
	public File appendFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet) {
		return this.appendFromDataAdvance(dataSet, iDataKeys, headNames, filePattern, fileName, sheet, null);
	}
	
	public File appendFileFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet) throws Exception {
		return this.appendFileFromDataAdvance(dataSet, iDataKeys, headNames, filePattern, fileName, sheet, null);
	}
	
	public File writeFileFromData(IDataset dataSet, Object[] iDataKeys, String[] headNames, FilePattern filePattern, 
			String fileName, Element sheet) throws Exception {
		return this.writeFileFromDataAdvance(dataSet, iDataKeys, headNames, filePattern, fileName, sheet, null);
	}
	
	public boolean writeFileData(FileWriter fw, IDataset dataSet, Object[] iDataKeys, String[] headNames, 
			FilePattern filePattern, Element sheet) throws Exception {
		return this.writeFileDataAdvance(fw, dataSet, iDataKeys, headNames, filePattern, sheet, null);
	}
	
	public IData readCSVToData(InputStream in, Element sheet) {
		return this.readCSVToDataAdvance(in, sheet, null);
	}
	
	public IData readCSVToData(InputStream in, Object[] iDataKeys, String[] headNames, Element sheet) {
		return this.readCSVToDataAdvance(in, iDataKeys, headNames, sheet, null);
	}
	
	public IData readCSVToData(InputStream in, IDataset iDataKeys, String[] headNames, Element sheet) {
		return this.readCSVToDataAdvance(in, iDataKeys, headNames, sheet, null);
	}
	
	public IData readCSVToData(String fileName, Element sheet) {
		return this.readCSVToDataAdvance(fileName, sheet, null);
	}
	
	public IData readCSVToData(String fileName, Object[] iDataKeys, String[] headNames, Element sheet) {
		return this.readCSVToDataAdvance(fileName, iDataKeys, headNames, sheet, null);
	}
	
	public IData readCSVToData(String fileName, IDataset iDataKeys, String[] headNames, Element sheet) {
		return this.readCSVToDataAdvance(fileName, iDataKeys, headNames, sheet, null);
	}
	
	public IData readTxtToData(String fileName, Element sheet) {
		return this.readTxtToDataAdvance(fileName, sheet, null);
	}
	
	public IData readTxtToData(String fileName, Object[] iDataKeys, String[] headNames, Element sheet) {
		return this.readTxtToDataAdvance(fileName, iDataKeys, headNames, sheet, null);
	}
	
	public IData readTxtToData(String fileName, IDataset iDataKeys, String[] headNames, Element sheet) {
		return this.readTxtToDataAdvance(fileName, iDataKeys, headNames, sheet, null);
	}
	
	public IData readTxtToData(InputStream in, Element sheet) {
		return this.readTxtToDataAdvance(in, sheet, null);
	}
	
	public IData readTxtToData(InputStream in, Element sheet, String token) {
		return this.readTxtToDataAdvance(in, sheet, token, null);
	}
	
	public IData readTxtToData(InputStream in, Object[] iDataKeys, String[] headNames, Element sheet, String token) {
		return this.readTxtToDataAdvance(in, iDataKeys, headNames, sheet, token, null);
	}
	
	public IData readTxtToData(InputStream in, IDataset iDataKeys, String[] headNames, Element sheet, String token) {
		return this.readTxtToDataAdvance(in, iDataKeys, headNames, sheet, token, null);
	}
	
	public IData readToData(InputStream in, Object[] iDataKeys, String[] headNames, FilePattern filePattern, Element sheet) {
		return this.readToDataAdvance(in, iDataKeys, headNames, filePattern, sheet, null);
	}
	
	public static void main(String[] args) throws Exception {
		// FileWriter fw = new FileWriter(new File("D:\\abc.csv"));
		List data = new LinkedList();
		data.add("唐朝,");
		data.add("宋朝,");
		data.add("元朝,");
		data.add("明朝\"");
		data.add("清朝");
		String[] strs = new String[] { "唐朝,", "宋朝,", "元朝,", "明朝\"", "清朝" };
		String a = "adfasdfasd,\"123213123\r\t\n123123123213\\123213/?!22~!@#$%^&*()_+-=[]{}|:;：；《》<>,./，。、？";
		IData iData = new DataMap();
		iData.put("key1", "亚信联创,132132\";123");
		iData.put("key2", "亚信联创123,&*)(_");
		IDataset ds = new DatasetList();
		ds.add(iData);
		// new SimpleParser().writeDataToFile(fw, a,null,null, FilePattern.CSV);
		Object[] keys = new Object[] { "key1", "key2", "key3", "key4", "key5", "key6", "key7", "key8" };
		String[] headNames = new String[] { "名字1", "名字2", "名字3", "名字4", "名字5", "名字6", "名字7", "名字8" };
		File file = new SimpleParser().writeCSVFromData(ds, keys, null, "a.csv", null);
		// System.out.println(file.getPath());

		InputStream is = new FileInputStream(file);
		IData ida = new SimpleParser().readCSVToData(is, new String[] { "key1", "key2", "key3", "key4", "key5", "key6", "key7", "key8" }, null, null);
		IDataset ids = ida.getDataset("right");
		int idsSize = (ids == null) ? 0 : ids.size();
		IData da = null;
		for (int i = 0; i < idsSize; i++) {
			da = (IData) ids.get(i);

		}
		System.out.println(ids);
		// System.out.println(fp.getToken());
	}

	public static enum FilePattern {
		// 文件后缀，列连接符，是否需要队列转码，转码时需替换内容,被替换后的内容（与需替换内容的顺序一致），列前缀，列后缀
		TXT(".txt", "\t", false, null, null, null, null), CSV(".csv", ",", true, new String[] { "\"", "," }, new String[] { "\"\"", "," }, "\"", "\"");

		private String suffix;
		private String token;
		private boolean isNeedEncode;
		private String[] beforeEncode;
		private String[] afterEncode;
		private String columnPrefix;
		private String columnSuffix;

		FilePattern(String suffix, String token, boolean isNeedEncode, String[] beforeEncode, String[] afterEncode, String columnPrefix, String columnSuffix) {
			this.suffix = suffix;
			this.token = token;
			this.isNeedEncode = isNeedEncode;
			this.beforeEncode = beforeEncode;
			this.afterEncode = afterEncode;
			this.columnPrefix = columnPrefix;
			this.columnSuffix = columnSuffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public boolean isNeedEncode() {
			return this.isNeedEncode;
		}

		public void setNeedEncode(boolean isNeedEncode) {
			this.isNeedEncode = isNeedEncode;
		}

		public void setBeforeEncode(String[] beforeEncode) {
			this.beforeEncode = beforeEncode;
		}

		public void setAfterEncode(String[] afterEncode) {
			this.afterEncode = afterEncode;
		}

		public void setColumnPrefix() {
			this.columnPrefix = columnPrefix;
		}

		public void setColumnSuffix() {
			this.columnSuffix = columnSuffix;
		}

	}

}
