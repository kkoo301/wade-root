package com.ailk.ant.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author huangbo
 * 文件夹遍历类
 */
public class FileRecursion {
	IFileOperation fileOper;
	
	/**
	 * 构造方法
	 * 参数:遍历时对文件的操作类
	 */
	public FileRecursion(IFileOperation fileOper){
		this.fileOper = fileOper;
	}
	
	public void recursion(String path) throws Exception{
		FileInputStream fis = null;
		try{
			/**递归操作所有文件*/
			recursion(new File(path));
		}finally{
			try {
				if(fis!=null){
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 递归遍历
	 */
	void recursion(File file) throws Exception{
		// TODO Auto-generated method stub
		if(file.isDirectory()){
			File childs[] = file.listFiles(fileFilter);
			for(File child : childs){
				recursion(child);
			}
			fileOper.dirDo(file);//对文件目录的操作在最后执行
		}else if(file.isFile()){
			fileOper.fileDo(file);
		}
	}
	
	
	FilenameFilter fileFilter = new FilenameFilter(){
		@Override
		public boolean accept(File dir, String name) {
			// TODO Auto-generated method stub
			File file = new File(dir, name);
			if(file.isFile()){
				return fileOper.fileFliter(dir, name);
			}else{
				return fileOper.dirFliter(dir, name);
			}
		}
	};
}
