package com.ailk.database.object.impl;

import java.sql.Types;

import com.ailk.database.object.IColumnObject;

public class ColumnObject implements IColumnObject {
	
	private static final long serialVersionUID = 6881552828920011695L;
	
	private String columnName;
	private int columnType;
	private String columnDesc;
	private int columnSize;
	private int decimalDigits; 
	private boolean key;
	private boolean nullable;
	private String rowId;
	private boolean isNString;
	
	public String getRowId() {
		return rowId;
	}
	
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public int getColumnType() {
		return columnType;
	}
	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public String getColumnDesc() {
		return columnDesc;
	}
	public void setColumnDesc(String columnDesc) {
		this.columnDesc = columnDesc;
	}

	public int getColumnSize() {
		return columnSize;
	}
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}
	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public boolean isKey() {
		return key;
	}
	public void setKey(boolean key) {
		this.key = key;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
	public boolean isDatetimeColumn() {
		return columnType == Types.DATE || columnType == Types.TIME || columnType == Types.TIMESTAMP;
	}
	
	@Override
	public boolean isNumeric() {
		return columnType == Types.INTEGER || columnType == Types.NUMERIC;
	}
	
	@Override
	public boolean isNString() {
		return this.isNString;
	}
	
	@Override
	public void setNString(boolean isNString) {
		this.isNString = isNString;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getColumnName()).append(":").append("isKey=").append(isKey()).append(",");
		sb.append("Type=").append(getColumnType()).append(",");
		sb.append("Size=").append(getColumnSize()).append(",");
		sb.append("Digits=").append(getDecimalDigits()).append(",");
		sb.append("Nullable=").append(isNullable()).append(",");
		sb.append("NString=").append(isNString()).append(",");
		sb.append("Desc=").append(getColumnDesc());
		return sb.toString();
	}
}