package com.ailk.ant.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.ailk.ant.file.FileRecursion;
import com.ailk.ant.file.impl.AbstractFileOperation;

/**
 * @author huangbo
 * UTF-8处理类
 * 记事本保存UTF-8格式的java文件,去掉前3个字节。
 */
public class EncodeFileOperation extends AbstractFileOperation{
	
	@Override
	public void fileDo(File file) throws Exception {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
		FileOutputStream fos = null;
		String tempPath = null;
		try{
			fis = new FileInputStream(file);
			byte startBytes[] = new byte[3];
			fis.read(startBytes);
			
			boolean bo = (startBytes[0]==-17)&&(startBytes[1]==-69)&&(startBytes[2]==-65);
			
			if(bo){
				tempPath = file.getAbsoluteFile()+".bakup";
				System.out.println("处理文件:"+file);
				fos = new FileOutputStream(tempPath);
				byte bytes[] = new byte[16];
				int count;
				while((count = fis.read(bytes))!=-1){
					fos.write(bytes, 0, count);//替代fos.write(bytes)方法,去掉了最后的换行符
				}
			}
		}finally{
			if(fis!=null)
				fis.close();
			if(fos!=null)
				fos.close();
		}
		if(tempPath!=null){
			file.delete();//风险,需要备份起来
			new File(tempPath).renameTo(file);
		}
	}

	@Override
	public boolean fileFliter(File dir, String name) {
		// TODO Auto-generated method stub
		if(name.endsWith(".java")){
			return true;
		}else{
			return false;
		}
	}
	
	public static void main(String[] args) {
		if(args==null||args.length!=1){
			System.out.println("运行EncodeFileOperation缺失参数");
			return;
		}
		try {
			new FileRecursion(new EncodeFileOperation()).recursion(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("EncodeFileOperation运行完成!");
	}

}
