package com.wade.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Display {
	
	private static final Map<String, List<Map>> CT = new HashMap<String, List<Map>>();
	private static long i = 1;
	
	private static final File[] listBomcFiles() {
		
		File dir = new File("C:/Users/Administrator/Downloads");
		
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("bomc") && name.endsWith("dat")) {
					return true;
				} else {
					return false;
				}
			}
		});
		
		return files;
	}
	
	private static final void resolve(File[] files) throws Exception {
		
		for (File name : files) {

			FileInputStream fis = new FileInputStream(name);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			try {
				
				while (true) {
					Map<String, Object> info = (Map<String, Object>) ois.readObject();
					Map<String, String> m = (Map<String, String>)info.get("ext");
					System.out.println(info);
					
					//System.out.println(i++ + ": " + m.get("SERIAL_NUMBER"));
					//System.out.println(i++ + ": " + m);
					
					
					//System.out.println(); // 15120844215
					
					/*
					String traceid = info.get("traceid");
					
					List<Map> list = CT.get(traceid);
					if (null == list) {
						list = new ArrayList<Map>();
						CT.put(traceid, list);
					}
					
					list.add(info);
					*/
				}
			} catch (Exception e) {
				ois.close();
			}	
		}
		
		/*
		for (String traceid : CT.keySet()) {
			List<Map> list = CT.get(traceid);
			for (Map m : list) {
				String probetype = (String)m.get("probetype");
				String parentid = (String)m.get("parentid");
				String id = (String)m.get("id");
				Boolean mainservice = (Boolean)m.get("mainservice");
				
				if (probetype.equals("menuclick")) {
					System.out.println(m);	
				}
				
				System.out.println();
				//System.out.printf("traceid=%s id=%s parentid=%s probetype=%s mainservice=%b\n", traceid, id, parentid, probetype, mainservice);
			}
			System.out.println("");
		}
		*/
		
	}
	
	public static void main(String[] args) throws Exception {
		
		File[] files = listBomcFiles();
		resolve(files);
		
	}
	
	
}
