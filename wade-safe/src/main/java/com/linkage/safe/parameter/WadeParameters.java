package com.linkage.safe.parameter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.linkage.safe.SafeBrowserFilter;

/**
 * 
 * @author $Id: WadeParameters.java 1 2014-02-20 08:34:02Z huangbo $
 *
 */
public class WadeParameters {
	private HashMap parameterMap=null;
	private boolean isLockes = false;
	private boolean parametersParsed = false;
	private ByteChunk tmpName=new ByteChunk();
	private ByteChunk tmpValue=new ByteChunk();
	private UDecoder urlDec;
	private String enc = SafeBrowserFilter.defaultEnc;
	public Map getPostMap(byte bytes[], int start, int len)
	{
	  if (this.isLockes)
	    return this.parameterMap;
	  
	  if(parameterMap==null)
		  parameterMap = new HashMap();

	  processParameters(bytes,start,len);

	  this.isLockes = true;
	  return this.parameterMap;
	}

	  
      private void addParam( String key, String value ) {
          if( key==null ) return;
          String values[];
          if (parameterMap.containsKey(key)) {
              String oldValues[] = (String[])parameterMap.
                  get(key);
              values = new String[oldValues.length + 1];
              for (int i = 0; i < oldValues.length; i++) {
                  values[i] = oldValues[i];
              }
              values[oldValues.length] = value;
          } else {
              values = new String[1];
              values[0] = value;
          }
          parameterMap.put(key, values);
      }

      


	    public void processParameters( byte bytes[], int start, int len) {
	        int end=start+len;
	        int pos=start;
	        

	        do {
	            boolean noEq=false;
	            int valStart=-1;
	            int valEnd=-1;
	            
	            int nameStart=pos;
	            int nameEnd=ByteChunk.indexOf(bytes, nameStart, end, '=' );
	            // Workaround for a&b&c encoding
	            int nameEnd2=ByteChunk.indexOf(bytes, nameStart, end, '&' );
	            if( (nameEnd2!=-1 ) &&
	                ( nameEnd==-1 || nameEnd > nameEnd2) ) {
	                nameEnd=nameEnd2;
	                noEq=true;
	                valStart=nameEnd;
	                valEnd=nameEnd;
	                
	            }
	            if( nameEnd== -1 ) 
	                nameEnd=end;

	            if( ! noEq ) {
	                valStart= (nameEnd < end) ? nameEnd+1 : end;
	                valEnd=ByteChunk.indexOf(bytes, valStart, end, '&');
	                if( valEnd== -1 ) valEnd = (valStart < end) ? end : valStart;
	            }
	            
	            pos=valEnd+1;
	            
	            if( nameEnd<=nameStart ) {
	                continue;
	                // invalid chunk - it's better to ignore
	            }
	           
	            tmpName.setBytes( bytes, nameStart, nameEnd-nameStart );
	            tmpValue.setBytes( bytes, valStart, valEnd-valStart );

	            try {
	                addParam( urlDecode(tmpName, enc), urlDecode(tmpValue, enc) );
	            } catch (IOException e) {
	                // Exception during character decoding: skip parameter
	            }

	            tmpName.recycle();
	            tmpValue.recycle();

	        } while( pos<end );
	    }
	    
	 private String urlDecode(ByteChunk bc, String enc)
        throws IOException {
        if( urlDec==null ) {
            urlDec=new UDecoder();   
        }
        urlDec.convert(bc);
        String result = null;
        if (enc != null) {
            bc.setEncoding(enc);
            result = bc.toString();
        }
        /*else {
            CharChunk cc = tmpNameC;
            int length = bc.getLength();
            cc.allocate(length, -1);
            // Default encoding: fast conversion
            byte[] bbuf = bc.getBuffer();
            char[] cbuf = cc.getBuffer();
            int start = bc.getStart();
            for (int i = 0; i < length; i++) {
                cbuf[i] = (char) (bbuf[i + start] & 0xff);
            }
            cc.setChars(cbuf, 0, length);
            result = cc.toString();
            cc.recycle();
        }
        */
        return result;
    }
	 
	 
	public void setEncoding(String contextType){
		String reqEnc = getCharsetFromContentType(contextType);
		if(reqEnc!=null) this.enc=reqEnc;
	}
	 
     public static String getCharsetFromContentType(String contentType) {

         if (contentType == null)
             return (null);
         int start = contentType.indexOf("charset=");
         if (start < 0)
             return (null);
         String encoding = contentType.substring(start + 8);
         int end = encoding.indexOf(';');
         if (end >= 0)
             encoding = encoding.substring(0, end);
         encoding = encoding.trim();
         if ((encoding.length() > 2) && (encoding.startsWith("\""))
             && (encoding.endsWith("\"")))
             encoding = encoding.substring(1, encoding.length() - 1);
         return (encoding.trim());

     }
}
