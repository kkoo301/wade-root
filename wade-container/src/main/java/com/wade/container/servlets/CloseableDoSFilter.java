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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wade.container.server.AbstractHttpConnection;
import com.wade.container.server.Request;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

/* ------------------------------------------------------------ */
/** Closeable DoS Filter.
 * This is an extension to the {@link DoSFilter} that uses Jetty APIs to allow
 * connections to be closed cleanly. 
 */

public class CloseableDoSFilter extends DoSFilter
{
    private static final Logger LOG = Log.getLogger(CloseableDoSFilter.class);

    protected void closeConnection(HttpServletRequest request, HttpServletResponse response, Thread thread)
    {
        try
        {
            Request base_request=(request instanceof Request)?(Request)request:AbstractHttpConnection.getCurrentConnection().getRequest();
            base_request.getConnection().getEndPoint().close();
        }
        catch(IOException e)
        {
            LOG.warn(e);
        }
    }
}
