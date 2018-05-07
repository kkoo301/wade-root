package com.ailk.ant.file.impl;

import java.io.File;

import com.ailk.ant.file.IFileOperation;

/**
 * @author huangbo
 * 文件操作抽象类
 * 提供关于目录的默认方法
 */
public abstract class AbstractFileOperation implements IFileOperation{
	
	@Override
	public void dirDo(File file) throws Exception {}
	
	@Override
	public boolean dirFliter(File dir, String name) {
		// TODO Auto-generated method stub
		return true;
	}
}
