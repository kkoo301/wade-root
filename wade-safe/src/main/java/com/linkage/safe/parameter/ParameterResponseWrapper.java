package com.linkage.safe.parameter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

//import java.util.zip.GZIPOutputStream;


public class ParameterResponseWrapper extends HttpServletResponseWrapper {
	private HttpServletResponse response;
    private ServletOutputStream out;
    private ResponseStream RespnseOut;
    private PrintWriter writer;
    private int contentLength;

    /**
     * create compress to HTTP
     * @param response the HTTP response to wrap.
     * @throws IOException if an I/O error occurs.
     */
    public ParameterResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.response = response;
        String sKey = String.valueOf(System.currentTimeMillis());
        response.addHeader("WADE-Server-Date", sKey);
        RespnseOut  = new ResponseStream(response.getOutputStream(),sKey);
    }
    
    /**
     * Ignore attempts to set the content length since the actual content
     * length will be determined by the GZIP compression.
     * @param len the content length
     */
    public void setContentLength(int len) {
        contentLength = len;
    }

    /** @see HttpServletResponse **/
    public ServletOutputStream getOutputStream() throws IOException {
        if (out == null) {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }
            out = RespnseOut;
        }
        return out;
    }

    /** @see HttpServletResponse **/
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            if (out != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }
            writer = new PrintWriter(RespnseOut);
        }
        return writer;
    }
    
    /** @see HttpServletResponse **/
    public void flushBuffer() {
        try
        {
            if (writer != null) {
                writer.flush();
            } else if (out != null) {
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** @see HttpServletResponse **/
    public void reset()
    {
        super.reset();
        try {
        	RespnseOut.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** @see HttpServletResponse **/
    public void resetBuffer() {
        super.resetBuffer();
        try {
        	RespnseOut.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finishes writing the compressed data to the output stream.
     * Note: this closes the underlying output stream.
     * @throws IOException if an I/O error occurs.
     */
    public void finish() throws IOException {
    	RespnseOut.close();
    }
	
    
    public class ResponseStream extends ServletOutputStream {
    	private ServletOutputStream out;
    	private long contentLength;
    	private long lKey;
        //private GZIPOutputStream gzip;
        /**
         * construct function
         * @param out
         * @throws IOException
         */
        public ResponseStream(ServletOutputStream out,String sKey) throws IOException {
        	this.out = out;
        	this.lKey = Long.valueOf(sKey.substring(sKey.length()-3));
        	reset();
    	}
        
        /** @see ServletOutputStream **/
        public void write(byte[] b) throws IOException {
        	write(b, 0, b.length);
        }
        /** @see ServletOutputStream **/
        public void write(byte[] b, int off, int len) throws IOException {
        	for(int i=0;i<len;i++){
        		b[i] =  (byte)(b[i] ^ lKey);
        	}
        	out.write(b, off, len);
        	contentLength  = + len;
        }
        /*
        /** @see ServletOutputStream **/
        public void write(int b) throws IOException {
           out.write(b);
           contentLength ++;
        }
        /**
         * Resets the stream.
         *
         * @throws IOException if an I/O error occurs.
         */
        public void reset() throws IOException {
        	contentLength = 0;
        }
    }

    
}



