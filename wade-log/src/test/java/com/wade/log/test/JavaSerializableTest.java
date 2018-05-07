package com.wade.log.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.wade.log.impl.LogData;

public class JavaSerializableTest {

	 public static void main(String[] args) throws IOException, ClassNotFoundException {  
	        long start =  System.currentTimeMillis();  
	        setSerializableObject();  
	        System.out.println("java Serializable writeObject time:" + (System.currentTimeMillis() - start) + " ms" );  
	        start =  System.currentTimeMillis();  
	        getSerializableObject();  
	        System.out.println("java Serializable readObject time:" + (System.currentTimeMillis() - start) + " ms");  
	    }  
	  
	    public static void setSerializableObject() throws IOException{  
	  
	        FileOutputStream fo = new FileOutputStream("data.ser");  
	  
	        ObjectOutputStream so = new ObjectOutputStream(fo);  
	  
	        for (int i = 0; i < 100000; i++) {  
	        	LogData logData = new LogData("1983");
				IData map = new DataMap();
				map.put("const", Math.random());
				map.put("string", "" + Math.random());
				logData.setContent(map);
				
	            so.writeObject(logData);  
	        }  
	        so.flush();  
	        so.close();  
	    }  
	  
	    public static void getSerializableObject(){  
	        FileInputStream fi;
	        int count = 0;
	        try {  
	            fi = new FileInputStream("data.ser");  
	            ObjectInputStream si = new ObjectInputStream(fi);  
	  
	            LogData logData = null;  
	            while((logData = (LogData)si.readObject()) != null){  
	            	//IData map = (IData)logData.getContent();
					//System.out.println(map.get("const"));
	            	count ++;
	            }  
	            fi.close();  
	            si.close();  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            //e.printStackTrace();  
	        } catch (ClassNotFoundException e) {  
	            e.printStackTrace();  
	        }finally{
	        	System.out.println("total>>" + count);
	        }
	  
	  
	    }  

}


