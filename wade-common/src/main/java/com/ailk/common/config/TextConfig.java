package com.ailk.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.BaseException;
import com.ailk.common.config.TextConfig;

public class TextConfig {
	
	public static Map<String, String> getProperties(String textFile) throws Exception {
		InputStream is = TextConfig.class.getClassLoader().getResourceAsStream(textFile);
		if (is == null)
			throw new BaseException("file [" + textFile + "] not exist.");
		return getProperties(is);
	}

	public static Map<String, String> getProperties(InputStream instream) throws Exception {
		Map<String, String> config = new HashMap<String, String>(500);
				BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(instream, GlobalCfg.getCharset()));
			
			String temp = null;

			do{
				temp = br.readLine();
				if (temp == null) break;
				if (temp.startsWith("#")) continue;
				int index = temp.indexOf("=");
				if (index == -1) continue;
				String key = temp.substring(0,index).trim();
				String value = temp.substring(index+1).trim();
				config.put(key, value);
			}
			while(temp != null);
			br.close();
			return config;
		} catch (Exception e) {
			throw new BaseException(e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					throw new BaseException(e);
				}
		}
	}
}
