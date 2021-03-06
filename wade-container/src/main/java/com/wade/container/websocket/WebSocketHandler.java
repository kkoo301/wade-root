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

package com.wade.container.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wade.container.server.Request;
import com.wade.container.server.handler.HandlerWrapper;

public abstract class WebSocketHandler extends HandlerWrapper implements WebSocketFactory.Acceptor
{
    private final WebSocketFactory _webSocketFactory=new WebSocketFactory(this,32*1024);
    
    public WebSocketFactory getWebSocketFactory()
    {
        return _webSocketFactory;
    }

    /* ------------------------------------------------------------ */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        if (_webSocketFactory.acceptWebSocket(request,response) || response.isCommitted())
        {
            baseRequest.setHandled(true);
            return;
        }
        super.handle(target,baseRequest,request,response);
    }
    
    /* ------------------------------------------------------------ */
    public boolean checkOrigin(HttpServletRequest request, String origin)
    {
        return true;
    }
    
}
