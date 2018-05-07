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

package com.wade.container.server.handler; 

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wade.container.continuation.Continuation;
import com.wade.container.continuation.ContinuationListener;
import com.wade.container.server.AsyncContinuation;
import com.wade.container.server.DispatcherType;
import com.wade.container.server.Request;
import com.wade.container.server.RequestLog;
import com.wade.container.server.Response;
import com.wade.container.server.Server;
import com.wade.container.util.component.AbstractLifeCycle;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;


/** 
 * RequestLogHandler.
 * This handler can be used to wrap an individual context for context logging.
 * 
 * @org.apache.xbean.XBean
 */
public class RequestLogHandler extends HandlerWrapper
{
    private static final Logger LOG = Log.getLogger(RequestLogHandler.class);

    private RequestLog _requestLog;
    
    /* ------------------------------------------------------------ */
    /* 
     * @see com.wade.container.server.server.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
     */
    @Override
    public void handle(String target, final Request baseRequest, HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException
    {
        AsyncContinuation continuation = baseRequest.getAsyncContinuation();
        if (!continuation.isInitial())
        {
            baseRequest.setDispatchTime(System.currentTimeMillis());
        }
        
        try
        {
            super.handle(target, baseRequest, request, response);
        }
        finally
        {
            if (_requestLog != null && baseRequest.getDispatcherType().equals(DispatcherType.REQUEST))
            {
                if (continuation.isAsync())
                {
                    if (continuation.isInitial())
                        continuation.addContinuationListener(new ContinuationListener()
                        {

                            public void onTimeout(Continuation continuation)
                            {

                            }

                            public void onComplete(Continuation continuation)
                            {
                                _requestLog.log(baseRequest, (Response)response);
                            }
                        });
                }
                else
                    _requestLog.log(baseRequest, (Response)response);
            }
        }
    }

    /* ------------------------------------------------------------ */
    public void setRequestLog(RequestLog requestLog)
    {
        //are we changing the request log impl?
        try
        {
            if (_requestLog != null)
                _requestLog.stop();
        }
        catch (Exception e)
        {
            LOG.warn (e);
        }
        
        if (getServer()!=null)
            getServer().getContainer().update(this, _requestLog, requestLog, "logimpl",true);
        
        _requestLog = requestLog;
        
        //if we're already started, then start our request log
        try
        {
            if (isStarted() && (_requestLog != null))
                _requestLog.start();
        }
        catch (Exception e)
        {
            throw new RuntimeException (e);
        }
    }

    /* ------------------------------------------------------------ */
    /* 
     * @see com.wade.container.server.server.handler.HandlerWrapper#setServer(server.server.Server)
     */
    @Override
    public void setServer(Server server)
    {
        if (_requestLog!=null)
        {
            if (getServer()!=null && getServer()!=server)
                getServer().getContainer().update(this, _requestLog, null, "logimpl",true);
            super.setServer(server);
            if (server!=null && server!=getServer())
                server.getContainer().update(this, null,_requestLog, "logimpl",true);
        }
        else
            super.setServer(server);
    }

    /* ------------------------------------------------------------ */
    public RequestLog getRequestLog() 
    {
        return _requestLog;
    }

    /* ------------------------------------------------------------ */
    /* 
     * @see com.wade.container.server.server.handler.HandlerWrapper#doStart()
     */
    @Override
    protected void doStart() throws Exception
    {
        if (_requestLog==null)
        {
            LOG.warn("!RequestLog");
            _requestLog=new NullRequestLog();
        }
        super.doStart();
        _requestLog.start();
    }

    /* ------------------------------------------------------------ */
    /* 
     * @see com.wade.container.server.server.handler.HandlerWrapper#doStop()
     */
    @Override
    protected void doStop() throws Exception
    {
        super.doStop();
        _requestLog.stop();
        if (_requestLog instanceof NullRequestLog)
            _requestLog=null;
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    private static class NullRequestLog extends AbstractLifeCycle implements RequestLog
    {
        public void log(Request request, Response response)
        {            
        }
    }
    
}
