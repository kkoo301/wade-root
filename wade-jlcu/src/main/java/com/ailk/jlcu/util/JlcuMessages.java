package com.ailk.jlcu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum JlcuMessages {
	//EngineFactory
	INSTANCE_EXIST_FIELD(NLS.INSTANCE_EXIST_FIELD),
	INSTANTIATION_EXCEP(NLS.INSTANTIATION_EXCEP),
	ILLEGAL_ACCESS_EXCEP(NLS.ILLEGAL_ACCESS_EXCEP),
	//Engine
	PARAM_NOT_NULL(NLS.PARAM_NOT_NULL),
	JLCU_NOT_NULL(NLS.JLCU_NOT_NULL),
	CASE_IS_BOOLEAN(NLS.CASE_IS_BOOLEAN),
	SWTICH_HAS_NO_DEFAULT(NLS.SWTICH_HAS_NO_DEFAULT),
	//JavaMethod
	BUFF_IS_NULL(NLS.BUFF_IS_NULL),
	
	NODE_EXCEP(NLS.NODE_EXCEP),
	JAVA_EXCEP(NLS.JAVA_EXCEP),
	EXPRESS_EXCEP(NLS.EXPRESS_EXCEP),
	SUBFLOW_EXCEP(NLS.SUBFLOW_EXCEP),
	HTTP_EXCEP(NLS.HTTP_EXCEP),
	WS_EXCEP(NLS.WS_EXCEP),
	;
	
	private String message;
	JlcuMessages(String message){
		this.message = message;
	}
	@Override
	public String toString() {
		return this.message;
	}
	
	private static Pattern pattern;
	static{
		pattern = Pattern.compile("%v",Pattern.CASE_INSENSITIVE+Pattern.DOTALL);
	}
	
	public final String bind(String ... binds){
		if(binds == null || binds.length <= 0){
			return this.message;
		}
		StringBuffer buff = new StringBuffer();
		Matcher m = pattern.matcher(this.message);
		int i = 0, len = binds.length;
		while(i < len && m.find()){
			m.appendReplacement(buff, binds[i]);
			i++;
		}
		m.appendTail(buff);
		return buff.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(JlcuMessages.INSTANTIATION_EXCEP);
		System.out.println(JlcuMessages.ILLEGAL_ACCESS_EXCEP.bind(JlcuMessages.class.getName()));
	}
}
