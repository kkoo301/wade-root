/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月28日
 * 
 * Just Do IT.
 */
package com.ailk.database.rs;

import java.sql.SQLException;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.jdbc.IResultSetReader;

/**
 * @description
 * 读取ResultSet，并转换成IDataset对象
 */
public class DatasetListReader implements IResultSetReader<IDataset, IData> {
	
	private IDataset resultSet = null;
	
	public DatasetListReader() {
		this.resultSet = new DatasetList();
	}
	
	@Override
	public IDataset getResultSet() {
		return resultSet;
	}

	@Override
	public IData nextRow() {
		return new DataMap();
	}

	@Override
	public void addRow(IData row) {
		this.resultSet.add(row);
	}

	@Override
	public boolean isRowId(String columnName) {
		return "ROWID".equals(columnName);
	}

	@Override
	public void read(IData row, String columnName, Object value, int sqlType) throws SQLException {
		row.put(columnName, value);
	}
	
	@Override
	public int size() {
		return this.resultSet.size();
	}
}
