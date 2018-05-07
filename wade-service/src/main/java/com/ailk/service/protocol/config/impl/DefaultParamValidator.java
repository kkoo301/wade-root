package com.ailk.service.protocol.config.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.ailk.service.protocol.config.IParamObject;
import com.ailk.service.protocol.config.IValidator;

public class DefaultParamValidator implements IValidator {

	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private Map<String, String> attrs = new HashMap<String, String>();

	public static final String REGEX_STRING ="^.*$";
	public static final String REGEX_NUMBER ="^[+/-]?((\\d*)[.]?([0-9]\\d*))$";
	public static final String REGEX_MAIL ="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
	public static final String REGEX_MBPHONE ="^\\d{8,17}$";
	public static final String REGEX_IPV4 ="^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
	public static final String REGEX_MAC ="^([0-9A-Fa-f]{2})(-[0-9A-Fa-f]{2}){5}$";
	public static final String REGEX_DATE ="^([1-2][0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) ([0-1][0-9]|2[0-3]|):([0-5][0-9]):([0-5][0-9])$";
	
	private static Map<String, String> regexMap = new HashMap<String,String>();
	
	static{
		// 加入type 与其对应的默认 正则表达式
		regexMap.put("string",REGEX_STRING);
		regexMap.put("number",REGEX_NUMBER);
		regexMap.put("mail",REGEX_MAIL);
		regexMap.put("mbphone",REGEX_MBPHONE);
		regexMap.put("ip",REGEX_IPV4);
		regexMap.put("mac",REGEX_MAC);
		regexMap.put("date",REGEX_DATE);
	}
	
	public DefaultParamValidator() {
		this.name = getClass().getName();
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean validate(IParamObject po, String value) {
		return validate(po.getType(), mergeAttrs(getAttrs()), value);
	}
	
	private boolean validate(String type, Map<String, String> map, String value){
		if(!regexMap.containsKey(type)){
			return false;
		}
		
		String regex = regexMap.get(type);
		
		if(map.get("defval")!=null &&!"".equals(map.get("defval"))&&(value ==null || "".equals(value.trim()))){
			value = map.get("defval");
		}

		if(value==null&&"false".equals(map.get("nullable"))||
				("true".equals(map.get("required")) && (value ==null || "".equals(value.trim())))){
			return false;
		}
		
		if(map.get("format")!=null&&!"".equals(map.get("format"))){
			if("number".equals(type)){
				regex = "^"+map.get("format").replaceAll("[^.+-]", "[0-9]")+"$";
			}
			if("date".equals(type)){
				SimpleDateFormat sdf = new SimpleDateFormat(map.get("format"));
				try {
					sdf.parse(value);
					regex = "";
				} catch (ParseException e) {
					return false ;
				}
			}
		}
		
		if(map.get("regex")!=null&&!"".equals(map.get("regex"))){
			regex = map.get("regex");
		}
		
		if(value !=null && !"".equals(value) && (regex ==null || "".equals(regex.trim()))){
			return false ;
		}
		
		if(map.get("length")!=null&&!"".equals(map.get("length").trim())&&!"0".equals(map.get("length").trim())){
			if(value==null|| "".equals(value.trim())){
				return false ;
			}else{
				value = value.replaceAll("[^x00-xff]", "**");
				String [] rangs = map.get("length").split(",");
				if(rangs != null && rangs.length==2){
					long min = Long.valueOf(rangs[0]);
					long max = Long.valueOf(rangs[1]);
					long vLenth = value.length();
					if(!(vLenth >= min && vLenth <=max)){
						return false ;
					}
				}
			}
		}
		
		if(!"".equals(map.get("rang"))){
			String [] rangs = map.get("rang").split(",");
			if(rangs != null && rangs.length==2){
				if(value==null||"".equals(value.trim())){
					return false ;
				}
				double min = Double.valueOf(rangs[0]);
				double max = Double.valueOf(rangs[1]);
				double dValue = Double.valueOf(value);
				if(!(dValue >= min && dValue <=max)){
					return false ;
				}
			}
		}
		return regex!=null&&!"".equals(regex) && !(value ==null || "".equals(value.trim()))?value.matches(regex):true;
	}

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public String getAttr(String name) {
		return this.attrs.get(name);
	}

	public void setAttr(Map<String, String> attrs) {
		this.attrs = attrs;
	}
	
	private Map<String, String> mergeAttrs(Map<String,String> attrs){
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("nullable","yes");
		map.put("format","");
		map.put("length","");
		map.put("rang","");
		map.put("required","false");
		map.put("defval","");
		
		if(attrs!=null && !attrs.isEmpty()){
			map.putAll(attrs);
		}
		return map;
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{name=" + getName() + ",");
		sb.append("type=" + getType() + ",");
		sb.append("attrs=" + getAttrs() + "}");
		return sb.toString();
	}

}
