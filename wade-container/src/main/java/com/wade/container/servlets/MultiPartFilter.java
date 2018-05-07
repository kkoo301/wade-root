//
//  ========================================================================
//  Copyright (c) 1995-2014 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package com.wade.container.servlets;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.wade.container.http.MimeTypes;
import com.wade.container.io.ByteArrayBuffer;
import com.wade.container.util.B64Code;
import com.wade.container.util.LazyList;
import com.wade.container.util.MultiMap;
import com.wade.container.util.QuotedStringTokenizer;
import com.wade.container.util.ReadLineInputStream;
import com.wade.container.util.StringUtil;
import com.wade.container.util.TypeUtil;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

/* ------------------------------------------------------------ */
/**
 * Multipart Form Data Filter.
 * <p>
 * This class decodes the multipart/form-data stream sent by a HTML form that uses a file input
 * item.  Any files sent are stored to a temporary file and a File object added to the request 
 * as an attribute.  All other values are made available via the normal getParameter API and
 * the setCharacterEncoding mechanism is respected when converting bytes to Strings.
 * <p>
 * If the init parameter "delete" is set to "true", any files created will be deleted when the
 * current request returns.
 * <p>
 * The init parameter maxFormKeys sets the maximum number of keys that may be present in a 
 * form (default set by system property com.wade.container.server.Request.maxFormKeys or 1000) to protect 
 * against DOS attacks by bad hash keys. 
 * <p>
 * The init parameter deleteFiles controls if uploaded files are automatically deleted after the request
 * completes.
 * 
 */
public class MultiPartFilter implements Filter
{
    private static final Logger LOG = Log.getLogger(MultiPartFilter.class);
    public final static String CONTENT_TYPE_SUFFIX=".com.wade.container.servlet.contentType";
    private final static String FILES ="com.wade.container.servlet.MultiPartFilter.files";
    private File tempdir;
    private boolean _deleteFiles;
    private ServletContext _context;
    private int _fileOutputBuffer = 0;
    private int _maxFormKeys = Integer.getInteger("com.wade.container.server.Request.maxFormKeys",1000).intValue();

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        tempdir=(File)filterConfig.getServletContext().getAttribute("javax.servlet.context.tempdir");
        _deleteFiles="true".equals(filterConfig.getInitParameter("deleteFiles"));
        String fileOutputBuffer = filterConfig.getInitParameter("fileOutputBuffer");
        if(fileOutputBuffer!=null)
            _fileOutputBuffer = Integer.parseInt(fileOutputBuffer);
        _context=filterConfig.getServletContext();
        String mfks = filterConfig.getInitParameter("maxFormKeys");
        if (mfks!=null)
            _maxFormKeys=Integer.parseInt(mfks);
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) 
        throws IOException, ServletException
    {
        HttpServletRequest srequest=(HttpServletRequest)request;
        if(srequest.getContentType()==null||!srequest.getContentType().startsWith("multipart/form-data"))
        {
            chain.doFilter(request,response);
            return;
        }

        InputStream in = new ReadLineInputStream(request.getInputStream());
        String content_type=srequest.getContentType();

        // TODO - handle encodings
        String contentTypeBoundary = "";
        int bstart = content_type.indexOf("boundary=");
        if (bstart >= 0)
        {
            int bend = content_type.indexOf(";", bstart);
            bend = (bend < 0? content_type.length(): bend);
            contentTypeBoundary = QuotedStringTokenizer.unquote(value(content_type.substring(bstart,bend)).trim());
        }

        String boundary="--"+contentTypeBoundary;
        
        byte[] byteBoundary=(boundary+"--").getBytes(StringUtil.__ISO_8859_1);
        
        MultiMap params = new MultiMap();
        for (Iterator i = request.getParameterMap().entrySet().iterator();i.hasNext();)
        {
            Map.Entry entry=(Map.Entry)i.next();
            Object value=entry.getValue();
            if (value instanceof String[])
                params.addValues(entry.getKey(),(String[])value);
            else
                params.add(entry.getKey(),value);
        }
        
        boolean badFormatLogged = false;
        try
        {
            // Get first boundary
            String line=((ReadLineInputStream)in).readLine();

            if (line == null)
                throw new IOException("Missing content for multipart request");

            line = line.trim();
        
            while (line != null && !line.equals(boundary))
            {
                if (!badFormatLogged)
                {
                    LOG.warn("Badly formatted multipart request");
                    badFormatLogged = true;
                }
                line=((ReadLineInputStream)in).readLine();
                line=(line==null?line:line.trim());
            }
            
            if (line == null)
                throw new IOException("Missing initial multi part boundary");
            
            // Read each part
            boolean lastPart=false;
      
            outer:while(!lastPart && params.size()<_maxFormKeys)
            {
                String type_content=null;
                String content_disposition=null;
                String content_transfer_encoding=null;
                
                while(true)
                {
                    // read a line
                    line=((ReadLineInputStream)in).readLine();
                    
                    //No more input
                    if (line==null)
                        break outer;
                    
                    // If blank line, end of part headers
                    if("".equals(line))
                        break;
                    
                    // place part header key and value in map
                    int c=line.indexOf(':',0);
                    if(c>0)
                    {
                        String key=line.substring(0,c).trim().toLowerCase(Locale.ENGLISH);
                        String value=line.substring(c+1,line.length()).trim();
                        if(key.equals("content-disposition"))
                            content_disposition=value;
                        else if(key.equals("content-transfer-encoding"))
                            content_transfer_encoding=value;
                        else if (key.equals("content-type"))
                             type_content = value;  
                    }
                }
                // Extract content-disposition
                boolean form_data=false;
                if(content_disposition==null)
                {
                    throw new IOException("Missing content-disposition");
                }
                
                LOG.debug("Content-Disposition: {}", content_disposition);
                QuotedStringTokenizer tok=new QuotedStringTokenizer(content_disposition,";",false,true);
                String name=null;
                String filename=null;
                while(tok.hasMoreTokens())
                {
                    String t=tok.nextToken().trim();
                    String tl=t.toLowerCase();
                    if(t.startsWith("form-data"))
                        form_data=true;
                    else if(tl.startsWith("name="))
                        name=value(t);
                    else if(tl.startsWith("filename="))
                        filename=filenameValue(t);
                }
                
                // Check disposition
                if(!form_data)
                {
                    continue;
                }
                //It is valid for reset and submit buttons to have an empty name.
                //If no name is supplied, the browser skips sending the info for that field.
                //However, if you supply the empty string as the name, the browser sends the
                //field, with name as the empty string. So, only continue this loop if we
                //have not yet seen a name field.
                if(name==null)
                {
                    continue;
                }
                
                OutputStream out=null;
                File file=null;
                try
                {
                    if (filename!=null && filename.length()>0)
                    {
                        LOG.debug("filename = \"{}\"", filename);
                        file = File.createTempFile("MultiPart", "", tempdir);
                        out = new FileOutputStream(file);
                        if(_fileOutputBuffer>0)
                            out = new BufferedOutputStream(out, _fileOutputBuffer);
                        request.setAttribute(name,file);
                        params.add(name, filename);
                        if (type_content != null)
                            params.add(name+CONTENT_TYPE_SUFFIX, type_content);
                        
                        if (_deleteFiles)
                        {
                            file.deleteOnExit();
                            ArrayList files = (ArrayList)request.getAttribute(FILES);
                            if (files==null)
                            {
                                files=new ArrayList();
                                request.setAttribute(FILES,files);
                            }
                            files.add(file);
                        }   
                    }
                    else
                    {
                        out=new ByteArrayOutputStream();
                    }
                    
                    
                    if ("base64".equalsIgnoreCase(content_transfer_encoding))
                    {
                        in = new Base64InputStream((ReadLineInputStream)in);   
                    }
                    else if ("quoted-printable".equalsIgnoreCase(content_transfer_encoding))
                    {
                        in = new FilterInputStream(in)
                        {
                            @Override
                            public int read() throws IOException
                            {
                                int c = in.read();
                                if (c >= 0 && c == '=')
                                {
                                    int hi = in.read();
                                    int lo = in.read();
                                    if (hi < 0 || lo < 0)
                                    {
                                        throw new IOException("Unexpected end to quoted-printable byte");
                                    }
                                    char[] chars = new char[] { (char)hi, (char)lo };
                                    c = Integer.parseInt(new String(chars),16);
                                }
                                return c;
                            }
                        };
                    }

                    int state=-2;
                    int c;
                    boolean cr=false;
                    boolean lf=false;
                    
                    // loop for all lines`
                    while(true)
                    {
                        int b=0;
                        while((c=(state!=-2)?state:in.read())!=-1)
                        {
                            state=-2;
                            // look for CR and/or LF
                            if(c==13||c==10)
                            {
                                if(c==13)
                                {
                                    in.mark(1);
                                    int tmp=in.read();
                                    if (tmp!=10)
                                        in.reset();
                                    else
                                        state=tmp;
                                }
                                break;
                            }
                            // look for boundary
                            if(b>=0&&b<byteBoundary.length&&c==byteBoundary[b])
                                b++;
                            else
                            {
                                // this is not a boundary
                                if(cr)
                                    out.write(13);
                                if(lf)
                                    out.write(10);
                                cr=lf=false;
                                if(b>0)
                                    out.write(byteBoundary,0,b);
                                b=-1;
                                out.write(c);
                            }
                        }
                        // check partial boundary
                        if((b>0&&b<byteBoundary.length-2)||(b==byteBoundary.length-1))
                        {
                            if(cr)
                                out.write(13);
                            if(lf)
                                out.write(10);
                            cr=lf=false;
                            out.write(byteBoundary,0,b);
                            b=-1;
                        }
                        // boundary match
                        if(b>0||c==-1)
                        {
                            if(b==byteBoundary.length)
                                lastPart=true;
                            if(state==10)
                                state=-2;
                            break;
                        }
                        // handle CR LF
                        if(cr)
                            out.write(13);
                        if(lf)
                            out.write(10);
                        cr=(c==13);
                        lf=(c==10||state==10);
                        if(state==10)
                            state=-2;
                    }
                }
                finally
                {
                    out.close();
                }
                
                if (file==null)
                {
                    byte[] bytes = ((ByteArrayOutputStream)out).toByteArray();
                    params.add(name,bytes);
                    if (type_content != null)
                        params.add(name+CONTENT_TYPE_SUFFIX, type_content);
                }
            }
        
            // handle request
            chain.doFilter(new Wrapper(srequest,params),response);
        }
        catch (IOException e)
        {
            if (!badFormatLogged)
                LOG.warn("Badly formatted multipart request");
            throw e;             
        }
        finally
        {
            deleteFiles(request);
        }
    }

    private void deleteFiles(ServletRequest request)
    {
        ArrayList files = (ArrayList)request.getAttribute(FILES);
        if (files!=null)
        {
            Iterator iter = files.iterator();
            while (iter.hasNext())
            {
                File file=(File)iter.next();
                try
                {
                    file.delete();
                }
                catch(Exception e)
                {
                    _context.log("failed to delete "+file,e);
                }
            }
        }
    }
    
    /* ------------------------------------------------------------ */
    private String value(String nameEqualsValue)
    {
        int idx = nameEqualsValue.indexOf('=');
        String value = nameEqualsValue.substring(idx+1).trim();
        return QuotedStringTokenizer.unquoteOnly(value);
    }
    
    
    /* ------------------------------------------------------------ */
    private String filenameValue(String nameEqualsValue)
    {
        int idx = nameEqualsValue.indexOf('=');
        String value = nameEqualsValue.substring(idx+1).trim();   

        if (value.matches(".??[a-z,A-Z]\\:\\\\[^\\\\].*"))
        {
            //incorrectly escaped IE filenames that have the whole path
            //we just strip any leading & trailing quotes and leave it as is
            char first=value.charAt(0);
            if (first=='"' || first=='\'')
                value=value.substring(1);
            char last=value.charAt(value.length()-1);
            if (last=='"' || last=='\'')
                value = value.substring(0,value.length()-1);

            return value;
        }
        else
            //unquote the string, but allow any backslashes that don't
            //form a valid escape sequence to remain as many browsers
            //even on *nix systems will not escape a filename containing
            //backslashes
            return QuotedStringTokenizer.unquoteOnly(value, true);
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
    }

    /* ------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------- */
    private static class Wrapper extends HttpServletRequestWrapper
    {
        String _encoding=StringUtil.__UTF8;
        MultiMap _params;
        
        /* ------------------------------------------------------------------------------- */
        /** Constructor.
         * @param request
         */
        public Wrapper(HttpServletRequest request, MultiMap map)
        {
            super(request);
            this._params=map;
        }
        
        /* ------------------------------------------------------------------------------- */
        /**
         * @see javax.servlet.ServletRequest#getContentLength()
         */
        @Override
        public int getContentLength()
        {
            return 0;
        }
        
        /* ------------------------------------------------------------------------------- */
        /**
         * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
         */
        @Override
        public String getParameter(String name)
        {
            Object o=_params.get(name);
            if (!(o instanceof byte[]) && LazyList.size(o)>0)
                o=LazyList.get(o,0);
            
            if (o instanceof byte[])
            {
                try
                {
                   return getParameterBytesAsString(name, (byte[])o);
                }
                catch(Exception e)
                {
                    LOG.warn(e);
                }
            }
            else if (o!=null)
                return String.valueOf(o);
            return null;
        }
        
        /* ------------------------------------------------------------------------------- */
        /**
         * @see javax.servlet.ServletRequest#getParameterMap()
         */
        @Override
        public Map getParameterMap()
        {
            Map<String, String[]> cmap = new HashMap<String,String[]>();
            
            for ( Object key : _params.keySet() )
            {
                cmap.put((String)key,getParameterValues((String)key));
            }
            
            return Collections.unmodifiableMap(cmap);
        }
        
        /* ------------------------------------------------------------------------------- */
        /**
         * @see javax.servlet.ServletRequest#getParameterNames()
         */
        @Override
        public Enumeration getParameterNames()
        {
            return Collections.enumeration(_params.keySet());
        }
        
        /* ------------------------------------------------------------------------------- */
        /**
         * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
         */
        @Override
        public String[] getParameterValues(String name)
        {
            List l=_params.getValues(name);
            if (l==null || l.size()==0)
                return new String[0];
            String[] v = new String[l.size()];
            for (int i=0;i<l.size();i++)
            {
                Object o=l.get(i);
                if (o instanceof byte[])
                {
                    try
                    {
                        v[i]=getParameterBytesAsString(name, (byte[])o);
                    }
                    catch(Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else if (o instanceof String)
                    v[i]=(String)o;
            }
            return v;
        }
        
        /* ------------------------------------------------------------------------------- */
        /**
         * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
         */
        @Override
        public void setCharacterEncoding(String enc) 
            throws UnsupportedEncodingException
        {
            _encoding=enc;
        }
        
        
        /* ------------------------------------------------------------------------------- */
        private String getParameterBytesAsString (String name, byte[] bytes) 
        throws UnsupportedEncodingException
        {
            //check if there is a specific encoding for the parameter
            Object ct = _params.get(name+CONTENT_TYPE_SUFFIX);
            //use default if not
            String contentType = _encoding;
            if (ct != null)
            {
                String tmp = MimeTypes.getCharsetFromContentType(new ByteArrayBuffer((String)ct));
                contentType = (tmp == null?_encoding:tmp);
            }
            
            return new String(bytes,contentType);
        }
    }
    
    private static class Base64InputStream extends InputStream
    {
        ReadLineInputStream _in;
        String _line;
        byte[] _buffer;
        int _pos;
        
        public Base64InputStream (ReadLineInputStream in)
        {
            _in = in;
        }

        @Override
        public int read() throws IOException
        {
            if (_buffer==null || _pos>= _buffer.length)
            {
                _line = _in.readLine();
                System.err.println("LINE: "+_line);
                if (_line==null)
                    return -1;
                if (_line.startsWith("--"))
                    _buffer=(_line+"\r\n").getBytes();
                else if (_line.length()==0)
                    _buffer="\r\n".getBytes();
                else
                {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream(4*_line.length()/3);  
                    B64Code.decode(_line, bout);    
                    bout.write(13);
                    bout.write(10);
                    _buffer = bout.toByteArray();
                }
                
                _pos=0;
            }
            return _buffer[_pos++];
        } 
    }
}
