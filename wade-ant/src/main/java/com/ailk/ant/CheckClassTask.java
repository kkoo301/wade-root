package com.ailk.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class CheckClassTask extends Task {

	private Map<String, String> classContainer = new HashMap<String, String>();
	private static final Map<Integer, String> JDK_MAPPING = new HashMap<Integer, String>();
	
	static {		 
		 JDK_MAPPING.put(45, "JDK1.1");
		 JDK_MAPPING.put(46, "JDK1.2");
		 JDK_MAPPING.put(47, "JDK1.3");
		 JDK_MAPPING.put(48, "JDK1.4");
		 JDK_MAPPING.put(49, "JDK1.5");
		 JDK_MAPPING.put(50, "JDK1.6");
		 JDK_MAPPING.put(51, "JDK1.7");
		 JDK_MAPPING.put(52, "JDK1.8");
	}
		
	/**
	 * 运行时class版本，默认JDK6
	 */
	private int version = 50;
	
	/**
	 * 校验目录
	 */
	private String directory = null;

	public void setVersion(String strVersion) {
		this.version = Integer.parseInt(strVersion);
		System.out.print("\n\n");
		System.out.println("------------------------------------------------------------");
		System.out.println("运行时CLASS版本: " + this.version + " [" + JDK_MAPPING.get(this.version) + "]");
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
		classContainer.clear();
		System.out.println("校验目录: " + directory);
		System.out.println("------------------------------------------------------------");
	}
	
	@Override
	public void execute() throws BuildException {
		File libDir = new File(directory);
		for (File name : libDir.listFiles()) {
			String jarName = name.getAbsolutePath();
			if (!jarName.endsWith(".jar")) {
				continue;
			}

			System.out.println("jar包扫描, 检查 类重复/类版本: " + jarName);
			if (!check(jarName)) {
				throw new BuildException("请处理jar包冲突!");
			}
		}
	}

	/**
	 * 同名Class类检测
	 * 
	 * @param jarName
	 * @param jarFile
	 * @throws Exception
	 */
	private final boolean check(String jarName) throws BuildException {
		
		boolean rt = true;
		JarFile jarFile = null;
		
		try {
			jarFile = new JarFile(jarName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Enumeration<JarEntry> e = jarFile.entries();
		while (e.hasMoreElements()) {
			JarEntry entry = e.nextElement();
			String className = entry.getName();
			if (className.endsWith(".class")) {
				
				if (classContainer.containsKey(className)) {
					rt = false;
					System.out.println("ERROR: " + classContainer.get(className) + " 已包含 "	+ className);
				} else {
					classContainer.put(className, jarName);
				}
				
				versionCheck(jarFile, entry);
		
			}
		}
		
		return rt;
		
	}
	
	/**
	 * class版本校验，确保class文件版本<=运行时环境
	 * 	45: JDK1.1
	 * 	46: JDK1.2
	 * 	47: JDK1.3
	 * 	48: JDK1.4
	 * 	49: JDK1.5
	 * 	50: JDK1.6
	 * 	51: JDK1.7
	 * 	52: JDK1.8
	 * 
	 * @param jarFile
	 * @param entry
	 * @return
	 * @throws Exception
	 */
	private final boolean versionCheck(JarFile jarFile, JarEntry entry) {

		byte[] data = new byte[8];
		InputStream in = null;
		
		try {
			in = jarFile.getInputStream(entry);
			in.read(data, 0, 8);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		int minorVersion = (((int) data[4]) << 8) + data[5];
		int majorVersion = (((int) data[6]) << 8) + data[7];
		
		if (majorVersion > this.version) {
			System.out.println("ERROR: 不兼容的版本:" + majorVersion + " :" + entry.getName());
			return false;
		}
		
		return true;
	}

}
