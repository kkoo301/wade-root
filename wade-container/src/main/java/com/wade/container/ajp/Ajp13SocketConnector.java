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

package com.wade.container.ajp;

import java.io.IOException;

import com.wade.container.http.HttpSchemes;
import com.wade.container.io.Connection;
import com.wade.container.io.EndPoint;
import com.wade.container.server.Request;
import com.wade.container.server.bio.SocketConnector;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

/**
 *
 *
 *
 */
public class Ajp13SocketConnector extends SocketConnector
{
    private static final Logger LOG = Log.getLogger(Ajp13SocketConnector.class);

    static String __secretWord = null;
    static boolean __allowShutdown = false;
    public Ajp13SocketConnector()
    {
        super.setRequestHeaderSize(Ajp13Packet.MAX_PACKET_SIZE);
        super.setResponseHeaderSize(Ajp13Packet.MAX_PACKET_SIZE);
        super.setRequestBufferSize(Ajp13Packet.MAX_PACKET_SIZE);
        super.setResponseBufferSize(Ajp13Packet.MAX_PACKET_SIZE);
        // IN AJP protocol the socket stay open, so
        // by default the time out is set to 0 seconds
        super.setMaxIdleTime(0);
    }

    @Override
    protected void doStart() throws Exception
    {
        super.doStart();
        LOG.info("AJP13 is not a secure protocol. Please protect port {}",Integer.toString(getLocalPort()));
    }



    /* ------------------------------------------------------------ */
    /* (non-Javadoc)
     * @see com.wade.container.server.bio.SocketConnector#customize(org.eclipse.io.EndPoint, com.wade.container.server.Request)
     */
    @Override
    public void customize(EndPoint endpoint, Request request) throws IOException
    {
        super.customize(endpoint,request);
        if (request.isSecure())
            request.setScheme(HttpSchemes.HTTPS);
    }

    /* ------------------------------------------------------------ */
    @Override
    protected Connection newConnection(EndPoint endpoint)
    {
        return new Ajp13Connection(this,endpoint,getServer());
    }

    /* ------------------------------------------------------------ */
    // Secured on a packet by packet bases not by connection
    @Override
    public boolean isConfidential(Request request)
    {
        return ((Ajp13Request) request).isSslSecure();
    }

    /* ------------------------------------------------------------ */
    // Secured on a packet by packet bases not by connection
    @Override
    public boolean isIntegral(Request request)
    {
        return ((Ajp13Request) request).isSslSecure();
    }

    /* ------------------------------------------------------------ */
    @Deprecated
    public void setHeaderBufferSize(int headerBufferSize)
    {
        LOG.debug(Log.IGNORED);
    }

    /* ------------------------------------------------------------ */
    @Override
    public void setRequestBufferSize(int requestBufferSize)
    {
        LOG.debug(Log.IGNORED);
    }

    /* ------------------------------------------------------------ */
    @Override
    public void setResponseBufferSize(int responseBufferSize)
    {
        LOG.debug(Log.IGNORED);
    }

    /* ------------------------------------------------------------ */
    public void setAllowShutdown(boolean allowShutdown)
    {
        LOG.warn("AJP13: Shutdown Request is: " + allowShutdown);
        __allowShutdown = allowShutdown;
    }

    /* ------------------------------------------------------------ */
    public void setSecretWord(String secretWord)
    {
        LOG.warn("AJP13: Shutdown Request secret word is : " + secretWord);
        __secretWord = secretWord;
    }

}
