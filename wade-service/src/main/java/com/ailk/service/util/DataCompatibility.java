package com.ailk.service.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


public class DataCompatibility{
	
	private transient static final Logger log = Logger.getLogger(DataCompatibility.class);
	
	public static IData createDataMap(String value){
		List list=strToList(value);
		if (list != null && list.size() > 0){
			IData data=new DataMap();
			data.putAll((Map) list.get(0));
			return data;
		}
		return null;
	}
	
	public static IDataset createDatasetList(String value){
		List list=strToList(value);
		if (list != null){
			IDataset dataset=new DatasetList();
			dataset.addAll(list);
			return dataset;
		}
		return null;
	}
	
	/**
	 * to string
	 * @return String
	 */
    public static String toString(IData data) {
    	StringBuffer str = new StringBuffer();
    	str.append("{");
    	
    	Iterator it = data.entrySet().iterator();
    	while (it.hasNext()) {
    		Entry entity = (Entry) (it.next());
    		Object key = entity.getKey();
    		Object value = entity.getValue();
    		str.append("\"" + key + "\":");
    		
    		if (value == null) {
    			str.append("\"\"");
      		} else if (value instanceof Map || value instanceof List) {
        		str.append(value);
    		} else {
    			str.append("\"" + value + "\"");
    		}
    		
    		if (it.hasNext()) str.append(",");
    	}
    	
    	str.append("}");
    	return str.toString();
	}
    
	/**
	 * to wade string
	 * @return String
	 */
    public static String toWadeString(IData data) {
    	StringBuffer str = new StringBuffer();
    	str.append("{");
    	
    	Iterator it = data.entrySet().iterator();
    	while (it.hasNext()) {
    		Entry entity = (Entry) (it.next());
    		Object key = entity.getKey();
    		Object value = entity.getValue();
    		str.append(key + "=");
    		
    		if (value == null) {
    			str.append("[\"\"]");
    		} else if (value instanceof JSONObject) {
    			IData object = new DataMap();
    			object.putAll((Map) value);
    			str.append(toWadeString(data));
      		} else if (value instanceof JSONArray) {
    			IDataset object = new DatasetList();
    			object.addAll((List) value);
    			str.append(toWadeString(object));
    		} else if (value instanceof Map) {
        		str.append(toWadeString((IData) value));
    		} else if (value instanceof List) {
        		str.append(toWadeString((IDataset)value));
    		} else {
    			str.append("[\"" + value + "\"]");
    		}
    		
    		if (it.hasNext()) str.append(", ");
    	}
    	
    	str.append("}");
    	return str.toString();
	}
    
    /**
	 * to bude string
	 * @return String
	 */
    public static String toBudeString(IData data) {
    	StringBuffer str = new StringBuffer();
    	str.append("{");
    	
    	Iterator it = data.entrySet().iterator();
    	while (it.hasNext()) {
    		Entry entity = (Entry) (it.next());
    		Object key = entity.getKey();
    		Object value = entity.getValue();
    		str.append(key + "=");
    		
    		if (value == null) {
    			str.append("[\"\"]");
    		} else if (value instanceof JSONObject) {
    			IData object = new DataMap();
    			object.putAll((Map) value);
    			str.append(toBudeString(object));
      		} else if (value instanceof JSONArray) {
    			IDataset object = new DatasetList();
    			object.addAll((List) value);
    			str.append(toBudeString(object));
    		} else if (value instanceof Map) {
        		str.append(toBudeString((IData) value));
    		} else if (value instanceof List) {
        		str.append(toBudeString((IDataset) value));
    		} else {
    			str.append("[\"" + value.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"") + "\"]");
    		}
    		
    		if (it.hasNext()) str.append(", ");
    	}
    	
    	str.append("}");
    	return str.toString();
	}
  
	/**
	 * to string
	 * @return String
	 */
    public static String toString(IDataset dataset) {
    	StringBuffer str = new StringBuffer();
    	str.append("[");
    	
    	Iterator it = dataset.iterator();
    	while (it.hasNext()) {
    		Object value = it.next();
    		if (value == null) {
    			str.append("\"\"");
      		} else if (value instanceof Map || value instanceof List) {
    			str.append(value);
    		} else {
    			str.append("\"" + value + "\"");
    		}
    		if (it.hasNext()) str.append(",");
    	}
    	
    	str.append("]");
    	return str.toString();
	}
    
    
    /**
	 * to wade string
	 * @return String
	 */
    public static String toWadeString(IDataset dataset) {
    	StringBuffer str = new StringBuffer();
    	str.append("[");
    	
    	Iterator it = dataset.iterator();
    	while (it.hasNext()) {
    		Object value = it.next();
    		if (value == null) {
    			str.append("\"\"");
    		} else if (value instanceof JSONObject) {
    			IData object = new DataMap();
    			object.putAll((Map) value);
    			str.append(toWadeString(object));
      		} else if (value instanceof JSONArray) {
    			IDataset object = new DatasetList();
    			object.addAll((List) value);
    			str.append(toWadeString(object));
    		} else if (value instanceof Map) {
    			str.append(toWadeString((IData) value));
    		} else if (value instanceof List) {
    			str.append(toWadeString((IDataset) value));
    		} else {
    			str.append("\"" + value + "\"");
    		}
    		if (it.hasNext()) str.append(", ");
    	}
    	
    	str.append("]");
    	return str.toString();
	}
    
    /**
	 * to bude string
	 * @return String
	 */
    public static String toBudeString(IDataset dataset) {
    	StringBuffer str = new StringBuffer();
    	str.append("[");
    	
    	Iterator it = dataset.iterator();
    	while (it.hasNext()) {
    		Object value = it.next();
    		if (value == null) {
    			str.append("\"\"");
    		} else if (value instanceof JSONObject) {
    			IData object = new DataMap();
    			object.putAll((Map) value);
    			str.append(toBudeString(object));
      		} else if (value instanceof JSONArray) {
    			IDataset object = new DatasetList();
    			object.addAll((List) value);
    			str.append(toBudeString(object));
    		} else if (value instanceof Map) {
    			str.append(toBudeString((IData) value));
    		} else if (value instanceof List) {
    			str.append(toBudeString((IDataset) value));
    		} else {
    			str.append("\"" + value.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"") + "\"");
    		}
    		if (it.hasNext()) str.append(", ");
    	}
    	
    	str.append("]");
    	return str.toString();
	}
    
	/**
	 * to data
	 * @return IData
	 */
    /*
	public static IData toData(IDataset dataset) throws Exception {
		IData data = new DataMap();
		
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			IData element = (IData) it.next();
			Iterator iterator = element.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				if (data.containsKey(key)) {
					IDataset list = (IDataset) data.get(key);
					list.add(element.get(key));
				} else {
					IDataset list = new DatasetList();
					list.add(element.get(key));
					data.put(key, list);
				}
			}			
		}
		
		if ("".equals(data.getString("X_RECORDNUM", ""))) data.put("X_RECORDNUM", String.valueOf(size()));
		
		return data;
	}
	*/

	/**
	 * str to list
	 * @param value
	 * @return List
	 */
	public static List strToList(String value) {
		if (value == null) return null;
		
		String orgstr = value;
		
		Pattern pattern = Pattern.compile("([\\[|\\{]+\")");
		Matcher matcher = pattern.matcher(value);
		boolean iswadestr = !(matcher.find() && value.startsWith(matcher.group()));
		
		StringBuffer str = new StringBuffer();
		String regstr = iswadestr ? "(\\r)|(\\n)|(\\$)|(\\\\)|(\", \")|([\\[(, )]?\"[\\](, )]?)" : "(\\r)|(\\n)|(\\$)|(\\\\)|([\\{,]\"\\d*\\w*[:]?\\w*\":)";
		pattern = Pattern.compile(regstr);
		matcher = pattern.matcher(value);
		while (matcher.find()) {
			String group = matcher.group();
			if ("\r".equals(group)) {
				matcher.appendReplacement(str, "!~5~!");
			} else if ("\n".equals(group)) {
				matcher.appendReplacement(str, "!~6~!");
			} else if ("$".equals(group)) {
				matcher.appendReplacement(str, "!~7~!");
			} else if ("\"".equals(group)) {
				matcher.appendReplacement(str, "!~8~!");
			} else if ("\\".equals(group)) {
				matcher.appendReplacement(str, "!~9~!");
			} else if (!iswadestr) {
				if ((group.startsWith("{\"") || group.startsWith(",\"")) && group.endsWith("\":")) {
					matcher.appendReplacement(str, group.replaceAll("\"", "!~a~!"));
				}
			}
		}
		matcher.appendTail(str);
		
		value = str.toString();
		str = new StringBuffer();
		pattern = Pattern.compile(iswadestr ? "(\".*?\"[\\],])" : "(\".*?\"[\\]\\},])");
		matcher = pattern.matcher(value);
		while (matcher.find()) {
			String group = matcher.group();
			String prefix = group.substring(0, 1);
			String suffix = group.substring(group.length() - 2);
			group = group.substring(1, group.length() - 2);
			
			StringBuffer substr = new StringBuffer();
			Pattern subpattern = Pattern.compile("(\\{)|(\\[)|(\\])|(,)|(\")");
			Matcher submatcher = subpattern.matcher(group);
			while (submatcher.find()) {
				String subgroup = submatcher.group();
				if ("{".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~1~!");
				} else if ("[".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~2~!");
				} else if ("]".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~3~!");
				} else if (",".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~4~!");
				} else if ("\"".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~8~!");
				}
			}
			submatcher.appendTail(substr);
			matcher.appendReplacement(str, prefix + substr + suffix);
		}
		matcher.appendTail(str);
		
		if (iswadestr) {
			value = str.toString();
			str = new StringBuffer();
			pattern = Pattern.compile("(=?[\\{\\[][\\{\\}\\[\\]]*(, [\"]?)?[\\{\\}\\[]*)|([\\}\\]]*(, [\"]?)[\\{\\[]*)|(\", \")");
			matcher = pattern.matcher(value);
			while (matcher.find()) {
				String group = matcher.group();
				if (group.startsWith("=")) {
					group = "\":" + group.substring(1);
				}
				if (group.endsWith("{")) {
					group += "\"";
				} else if (group.endsWith(" ")) {
					group += "\"";
				}
				group = group.replaceFirst(" ", "");
				matcher.appendReplacement(str, group);
			}
			matcher.appendTail(str);
			
			value = str.toString();
			str = new StringBuffer();
			pattern = Pattern.compile("(:\\[\".*?\"\\])");
			matcher = pattern.matcher(value);
			while (matcher.find()) {
				String group = matcher.group();
				if (!Pattern.compile("(\",\")|(\",\\[)").matcher(group).find()) {
					matcher.appendReplacement(str, group.substring(0, 1) + group.substring(2, group.length() - 1));	
				}
			}
			matcher.appendTail(str);
		}
		
		value = str.toString();
		str = new StringBuffer();
		pattern = Pattern.compile("(!~1~!)|(!~2~!)|(!~3~!)|(!~4~!)|(!~5~!)|(!~6~!)|(!~7~!)|(!~8~!)|(!~9~!)|(!~a~!)");
		matcher = pattern.matcher(value);
		while (matcher.find()) {
			String group = matcher.group();
			if ("!~1~!".equals(group)) {
				matcher.appendReplacement(str, "{");
			} else if ("!~2~!".equals(group)) {
				matcher.appendReplacement(str, "[");
			} else if ("!~3~!".equals(group)) {
				matcher.appendReplacement(str, "]");
			} else if ("!~4~!".equals(group)) {
				matcher.appendReplacement(str, ",");
			} else if ("!~5~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\r");
			} else if ("!~6~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\n");
			} else if ("!~7~!".equals(group)) {
				matcher.appendReplacement(str, "\\$");
			} else if ("!~8~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\\"");
			} else if ("!~9~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\\\\\");
			} else if (!iswadestr) {
				if ("!~a~!".equals(group)) {
					matcher.appendReplacement(str, "\"");
				}
			}
		}
		matcher.appendTail(str);
		value = str.toString();
		
		if (!(value.startsWith("[") && value.endsWith("]"))) {
			value = "[" + value + "]";
		}
		
		try {
			return (List) JSONSerializer.toJSON(value);
		} catch (JSONException e) {
			throw new RuntimeException("syntax error" + orgstr);
		}
	}
}