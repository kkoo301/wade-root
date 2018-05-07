package com.ailk.jlcu.util;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;

public class DatasBuffer extends DatasetList implements Serializable {

	private static final Logger logger = Logger.getLogger(DatasBuffer.class);

	public DatasBuffer() {
		super();
	}

	public DatasBuffer(String strBunch) {
		super(strBunch);
	}

	public String GetStringDefault(String strKey, String strDefaultValue, int idx) throws Exception {
		StringBuilder strError = new StringBuilder("");
		if (size() == 0 || idx >= size()) {
			if (strDefaultValue != null) {
				return strDefaultValue;
			} else {
				strError.append("List.size() == ").append(size())
						.append(" Not [columns:").append(idx).append("] [key:")
						.append(strKey).append("] return!");
			}
		} else {
			if (!IsFldExist(strKey, idx) && strDefaultValue == null) {
				strError.append("List.size() == ").append(size())
						.append(" Not [columns:").append(idx).append("] [key:")
						.append(strKey).append("] return!");
			} else {
				Object obj = getData(idx).get(strKey) == null ? strDefaultValue
						: getData(idx).get(strKey);
				return obj == null ? null : String.valueOf(obj);
			}
		}

		if (!strError.equals("")) {
			throw new Exception(strError.toString());
		}

		return strDefaultValue;
	}

	public String GetString(String strKey) throws Exception {
		String str = GetStringDefault(strKey, "", 0);

		if (str == null) {
			return "";
		}

		return str;
	}

	public String GetStringDefault(String strKey, String strDefaultValue) throws Exception {
		return GetStringDefault(strKey, strDefaultValue, 0);
	}

	public String GetString(String strKey, int idx) throws Exception {
		String str = GetStringDefault(strKey, "", idx);

		if (str == null) {
			return "";
		}

		return str;
	}

	public int GetInt(String strKey) throws Exception {
		String str = GetStringDefault(strKey, "-1", 0);

		if (str == null) {
			return 0;
		}

		return Integer.parseInt(str);
	}

	public int GetInt(String strKey, int idx) throws Exception {
		String str = GetStringDefault(strKey, "-1", idx);

		if (str == null) {
			return 0;
		}

		return Integer.parseInt(str);
	}

	public int GetIntDefault(String strKey, int iDefaultValue) throws Exception {
		return Integer.parseInt(GetStringDefault(strKey, String.valueOf(iDefaultValue), 0));
	}

	public Integer GetIntDefault(String strKey, int iValue, int idx) throws Exception {
		return Integer.parseInt(GetStringDefault(strKey, String.valueOf(iValue), idx));
	}

	public void SetString(String strKey, String strValue) throws Exception {
		SetString(strKey, strValue, 0);
	}

	public void SetString(String strKey, String strValue, int iPos)	throws Exception {
		IData data = null;

		if (iPos != 0 && iPos > size() - 1) {
			data = new DataMap();
			data.put(strKey, strValue);
			add(data);
		} else if (iPos == 0) {
			if (size() == 0) {
				data = new DataMap();
				data.put(strKey, strValue);
				add(data);
			} else {
				data = (getData(0));
				data.put(strKey, strValue);
				set(0, data);
			}
		} else {
			data = getData(iPos);
			data.put(strKey, strValue);
			set(iPos, data);
		}
	}

	public void converList(IDataset list2) throws Exception {
		String strName[] = list2.getNames();

		for (int idx = 0; idx < list2.size(); idx++) {
			for (int iName = 0; iName < strName.length; iName++) {
				SetString(strName[iName],
						(String) list2.get(idx, strName[iName]), idx);
			}
		}
	}

	public void SetInt(String strKey, int iValue) throws Exception {
		SetString(strKey, String.valueOf(iValue), 0);
	}

	public void SetInt(String strKey, int iValue, int iPos) throws Exception {
		SetString(strKey, String.valueOf(iValue), iPos);
	}

	public int GetCount() throws Exception {
		return size();
	}

	public int GetCount(String strKey) throws Exception {
		int iCount = 0;

		for (int idx = 0; idx < size(); idx++) {
			if (!getData(idx).containsKey(strKey)) {
				if (idx != 0) {
					return iCount;
				} else {
					return idx;
				}
			} else {
				iCount = (idx + 1);
			}
		}

		return iCount;
	}

	public boolean IsFldExist(String strKey) throws Exception {
		return IsFldExist(strKey, 0);
	}

	public boolean IsFldExist(String strKey, int iPos) throws Exception {
		if (size() == 0 || iPos > (size() - 1)) {
			return false;
		}

		return getData(iPos).containsKey(strKey);
	}

	public void ClearFmlValue() throws Exception {
		clear();
	}

	public void Clear() throws Exception {
		clear();
	}

	public void Print(String strListName) throws Exception {
		if (size() == 0) {
			logger.debug("=================================" + strListName);
			logger.debug("{null}");
		} else {
			logger.debug("=================================" + strListName);
			String[] strKeys = this.getNames();
			StringBuilder str = new StringBuilder(String.valueOf(size()));

			for (int i = 0; i < strKeys.length; i++) {
				str.append("`").append(strKeys[i]);
			}

			logger.debug(str);

			for (int i = 0; i < size(); i++) {
				str = new StringBuilder(String.valueOf(i));
				for (int idx = 0; idx < strKeys.length; idx++) {
					str.append("`").append(getData(i).getString(strKeys[idx]));
				}
				logger.debug(str);
			}
		}
	}

	public void Print() throws Exception {
		Print("XXXXX NAME");
	}

	public void Append(DatasBuffer list) throws Exception {
		append(list);
	}

	public void append(DatasBuffer list) throws Exception {
		String[] strNames;
		IData dataList;

		for (int iList = 0; iList < list.size(); iList++) {
			dataList = list.getData(iList);
			strNames = dataList.getNames();

			for (int iName = 0; iName < strNames.length; iName++) {
				for (int iSelf = 0; iSelf < this.size(); iSelf++) {
					if (!this.getData(iSelf).containsKey(strNames[iName])) {
						this.getData(iSelf).put(strNames[iName],
								dataList.get(strNames[iName]));
						dataList.remove(strNames[iName]);
					}
				}
			}

			if (dataList != null && !dataList.isEmpty()) {
				this.add(dataList);
			}
		}
	}

	public void plusSign(DatasBuffer list) throws Exception {
		converList(list);
	}

	public DatasBuffer putAll(IDataset list) throws Exception {
		// TODO Auto-generated method stub
		if (list == null || list.size() == 0) {
			return this;
		}
		int len = this.size() < list.size() ? this.size() : list.size();
		int i;
		for (i = 0; i < len; i++) {
			this.getData(i).putAll(list.getData(i));
		}
		if (i < list.size()) {
			len = list.size();
			IData data;
			while (i < len) {
				data = new DataMap();// 创建新的引用
				data.putAll(list.getData(i));
				this.add(data);
				i++;
			}
		}
		return this;
	}

	/******************* 小写方法 ********************/
	public String getString(String strKey) throws Exception {
		return GetString(strKey);
	}

	public String getString(String strKey, int idx) throws Exception {
		return GetString(strKey, idx);
	}

	public String getStringDefault(String strKey, String strDefaultValue,
			int idx) throws Exception {
		return GetStringDefault(strKey, strDefaultValue, idx);
	}

	public String getStringDefault(String strKey, String strDefaultValue)
			throws Exception {
		return GetStringDefault(strKey, strDefaultValue);
	}

	public int getInt(String strKey) throws Exception {
		return GetInt(strKey);
	}

	public int getInt(String strKey, int idx) throws Exception {
		return GetInt(strKey, idx);
	}

	public Integer getIntDefault(String strKey, int iValue, int idx)
			throws Exception {
		return GetIntDefault(strKey, iValue, idx);
	}

	public Integer getIntDefault(String strKey, int iValue) throws Exception {
		return GetIntDefault(strKey, iValue);
	}

	public void setString(String strKey, String strValue) throws Exception {
		SetString(strKey, strValue);
	}

	public void setString(String strKey, String strValue, int iPos)
			throws Exception {
		SetString(strKey, strValue, iPos);
	}

	public void setInt(String strKey, int iValue) throws Exception {
		SetInt(strKey, iValue);
	}

	public void setInt(String strKey, int iValue, int iPos) throws Exception {
		SetInt(strKey, iValue, iPos);
	}

	public int getCount(String strKey) throws Exception {
		return GetCount(strKey);
	}

	public int getCount() throws Exception {
		return GetCount();
	}

	public boolean isFldExist(String strKey) throws Exception {
		return IsFldExist(strKey, 0);
	}

	public boolean isFldExist(String strKey, int iPos) throws Exception {
		return IsFldExist(strKey, iPos);
	}
}
