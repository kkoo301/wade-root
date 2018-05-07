package com.ailk.ant.file;

import java.io.File;

/**
 * @author huangbo
 * 文件操作类接口
 */
public interface IFileOperation {
	/**
	 * 文件操作方法
	 */
	public void fileDo(File file) throws Exception;
	
	/**
	 * 文件夹操作方法
	 */
	public void dirDo(File file) throws Exception;
	
	/**
	 * 文件过滤方法
	 */
	public boolean fileFliter(File dir, String name);
	/**
	 * 文件夹过滤方法
	 */
	public boolean dirFliter(File dir, String name);
}
