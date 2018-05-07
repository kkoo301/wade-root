package com.wade.message.comet.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiXSSEncoder
{
	private static transient final String DOM_EVENT_ACTION = "onabort|onactivate|onafterprint|onafterupdate|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditfocus|onbeforepaste|onbeforeprint|onbeforeunload|onbeforeupdate|onblur|onbounce|oncellchange|onchange|onclick|oncontextmenu|oncontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend|ondragenter|ondragleave|ondragover|ondragstart|ondrop|onerror|onerrorupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout|onhelp|onkeydown|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove|onmouseout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onpaste|onpropertychange|onreadystatechange|onreset|onresize|onresizeend|onresizestart|onrowenter|onrowexit|onrowsdelete|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit|onunload";
	private static transient final String PLACE_HOLDER = "antixss";
	private static transient final Map<String, Pattern> patterns = new HashMap<String, Pattern>(); //new ConcurrentHashMap<String, Pattern>();

	public static String encode(String value) {
		if ( value == null || "".equals(value.trim()) ) {
			return value;
		}
		
		value = replaceAll( "<iframe([^>]*)>", value, PLACE_HOLDER);
		value = replaceAll( "<\\/iframe([^>]*)>", value, PLACE_HOLDER);
		value = replaceAll( "<frameset([^>]*)>", value, PLACE_HOLDER);
		value = replaceAll( "<\\/frameset([^>]*)>", value, PLACE_HOLDER);
		value = replaceAll( "<script([^>]*)>", value, PLACE_HOLDER);
		value = replaceAll( "<\\/script([^>]*)>", value, PLACE_HOLDER);
		
		value = replaceAll( "javascript:", value, PLACE_HOLDER); 
		//value = replaceAll( "script", value, PLACE_HOLDER);  //可能导致部分模板下载问题
		//value = replaceAll( "document\.", value, PLACE_HOLDER, 0);  //可能导致部分pageName带document字符的页面异常
		value = replaceAll( "cookie", value, PLACE_HOLDER); 
		value = replaceAll( "eval[^\\(a-zA-Z_]*\\(", value, PLACE_HOLDER, 0); 
		value = replaceAll( "alert[^\\(a-zA-Z_]*\\(", value, PLACE_HOLDER, 0); 
		
		value = replaceAll("(" + DOM_EVENT_ACTION + ")(\\s*)=", value, PLACE_HOLDER); 
		//value = replaceAll( "(on[a-zA-Z]+)(\\s*)=", value, "");
		
		value = value.replaceAll("<\\?", "!~a~!");
		value = value.replaceAll("\\?>", "!~b~!");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll( ">", "&gt;" );
		value = value.replaceAll("!~a~!", "<?");
		value = value.replaceAll("!~b~!", "?>");
		//value = value.replaceAll("<", "&lt;").replaceAll( ">", "&gt;" );
		
		//value = value.replaceAll("\\(", "&#40;").replaceAll( "\\)", "&#41;" );
		//value = value.replaceAll("'", "&#39;"); //不过滤单引号

		return value;
	}
	
	private static String replaceAll(String regExp, String input, String replaceStr){
		return replaceAll(regExp, input, replaceStr, Pattern.CASE_INSENSITIVE);
	}	

	private static String replaceAll(String regExp, String input, String replaceStr, int flags){
		String key = regExp + "_" + flags;
		Pattern p = patterns.get(key);
		if(p == null){
			p = Pattern.compile(regExp, flags > 0 ? flags : 0);
			patterns.put(key, p);
		}
		Matcher m = p.matcher(input);
		StringBuffer buf = new StringBuffer();
		while(m.find()){
			m.appendReplacement(buf, replaceStr);
		}
		m.appendTail(buf);
		return buf.toString();
	}	
	
	public static void main(String[] args){
		System.out.println( encode("valuecard.VPMNGiveValueCard") );
	}
}