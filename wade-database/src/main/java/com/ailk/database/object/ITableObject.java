package com.ailk.database.object;

import java.io.Serializable;

public interface ITableObject extends Serializable {

	public String getRemarks();

	public void setRemarks(String remarks);

	public String getTableCat();

	public void setTableCat(String tableCat);

	public String getTableName();

	public void setTableName(String tableName);

	public String getTableSchem();

	public void setTableSchem(String tableSchem);

	public String getTableType();

	public void setTableType(String tableType);

}
