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

package com.wade.container.client.webdav;

import java.io.IOException;

import com.wade.container.client.HttpExchange;
import com.wade.container.http.HttpStatus;
import com.wade.container.io.Buffer;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;


public class PropfindExchange extends HttpExchange
{
    private static final Logger LOG = Log.getLogger(PropfindExchange.class);

    boolean _propertyExists = false;

    /* ------------------------------------------------------------ */
    @Override
    protected void onResponseStatus(Buffer version, int status, Buffer reason) throws IOException
    {
        if ( status == HttpStatus.OK_200 )
        {
            LOG.debug( "PropfindExchange:Status: Exists" );
            _propertyExists = true;
        }
        else
        {
            LOG.debug( "PropfindExchange:Status: Not Exists" );
        }

        super.onResponseStatus(version, status, reason);
    }

    public boolean exists()
    {
        return _propertyExists;
    }
}
