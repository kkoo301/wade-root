package com.ailk.common.data.impl;

import java.io.Serializable;


public class Pagination implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int MAX_PAGE_SIZE = 500;
	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int MAX_RECODE_SIZE = Integer.MAX_VALUE;
	private static final int MAX_FETCH_SIZE = 2000;
	
	public static final String X_PAGINCOUNT = "X_PAGINCOUNT";
	public static final String X_PAGINCURRENT = "X_PAGINCURRENT";
	public static final String X_PAGINSELCOUNT = "X_PAGINSELCOUNT";
	public static final String X_PAGINSIZE = "X_PAGINSIZE";
	public static final String X_RESULTCOUNT = "X_RESULTCOUNT";

	/**
	 * 是否需要分页
	 */
	private boolean needCount = true;
	/**
	 * 是否只需要Count
	 */
	private boolean onlyCount = false;
	/**
	 * Count数
	 */
	private long count;
	/**
	 * 当前页码
	 */
	private int current = 1;
	/**
	 * 每页显示的行数
	 */
	private int pagesize;
	/**
	 * 原生设置的每页显示行数
	 */
	private int originPageSize;
	/**
	 * ResultSet的FetchSize
	 */
	private int fetchSize;
	/**
	 * 当前页的数据量
	 */
	private int currentSize = 0;

	/**
	 * construct function
	 * 
	 * @throws Exception
	 */
	public Pagination() {

	}

	public Pagination(boolean isbatch, int pagesize) {
		if (isbatch) {
			this.pagesize = pagesize;
		}
	}

	/**
	 * construct function
	 * 
	 * @param batch
	 * @param size
	 * @throws Exception
	 */
	public Pagination(int pagesize) {
		this.pagesize = pagesize;
	}

	/**
	 * construct function
	 * 
	 * @param pagesize
	 * @param current
	 */
	public Pagination(int pagesize, int current) {
		this.pagesize = pagesize;
		this.current = current;
	}

	
	/**
	 * next pagination
	 * @param pagin
	 * @return
	 */
	public boolean next() {
		if (this.current >= getPageCount()) {
			return false;
		} else {
			this.current++;
			return true;
		}
	}

	/**
	 * get fetch size
	 * 
	 * @return
	 */
	public int getFetchSize() {
		if (fetchSize == 0 && this.pagesize > 0)
			this.fetchSize = this.pagesize;
		else
			this.fetchSize = DEFAULT_PAGE_SIZE;
		return fetchSize;
	}

	/**
	 * set fetch size
	 * 
	 * @param fetchSize
	 */
	public void setFetchSize(int fetchSize) {
		if (fetchSize <= MAX_FETCH_SIZE &&  fetchSize >= 0) {
			this.fetchSize = fetchSize;
		} else {
			this.fetchSize = getDefaultPageSize();
		}
	}

	/**
	 * get max page size
	 * 
	 * @return int
	 * @throws Exception
	 */
	public static int getMaxPageSize() {
		return MAX_PAGE_SIZE;
	}

	/**
	 * get default pagesize
	 * 
	 * @return
	 */
	public static int getDefaultPageSize() {
		return DEFAULT_PAGE_SIZE;
	}

	/**
	 * is need count
	 * 
	 * @return boolean
	 */
	public boolean isNeedCount() {
		return needCount;
	}

	/**
	 * set need count
	 * 
	 * @param needCount
	 */
	public void setNeedCount(boolean needCount) {
		this.needCount = needCount;
	}

	/**
	 * is count
	 * 
	 * @return long
	 */
	public long getCount() {
		return count;
	}

	/**
	 * set count
	 * 
	 * @param count
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * get pagesize
	 * 
	 * @return int
	 */
	public int getPageSize() {
		return pagesize;
	}

	/**
	 * set pagesize
	 * 
	 * @param pagesize
	 */
	public void setPageSize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getOriginPageSize() {
		return originPageSize;
	}
	
	public void setOriginPageSize(int originPageSize) {
		this.originPageSize = originPageSize;
	}
	
	/**
	 * get page count
	 * @return
	 */
	public long getPageCount() {
		long pageCount = (long) (getCount() / getPageSize());
		if (pageCount == 0 || getCount() % getPageSize() != 0) {
			pageCount++;
		}
		return pageCount;
	}

	/**
	 * @return onlyCount
	 */
	public boolean isOnlyCount() {
		return onlyCount;
	}

	/**
	 * set only count
	 * 
	 * @param onlyCount
	 */
	public void setOnlyCount(boolean onlyCount) {
		this.onlyCount = onlyCount;
	}

	/**
	 * get current
	 * 
	 * @return
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * set current
	 * 
	 * @param current
	 */
	public void setCurrent(int current) {
		this.current = current;
	}

	/**
	 * get start
	 * 
	 * @return
	 */
	public int getStart() {
		if (this.current <= 1)
			return 0;
		return (this.current - 1) * this.pagesize;
	}

	/**
	 * get end
	 * 
	 * @return
	 */
	public int getEnd() {
		if (this.current <= 1) {
			return this.pagesize;
		}
		return this.current * this.pagesize;
	}
	
	/**
	 * @return the currentSize
	 */
	public int getCurrentSize() {
		return currentSize;
	}
	
	/**
	 * @param currentSize the currentSize to set
	 */
	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}
}