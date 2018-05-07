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

package com.wade.container.server.jmx;

import com.wade.container.jmx.ObjectMBean;
import com.wade.container.server.Handler;
import com.wade.container.server.Server;
import com.wade.container.server.handler.ContextHandler;

/**
 *
 */
public class ServerMBean extends ObjectMBean
{
    private final long startupTime;
    private final Server server;

    public ServerMBean(Object managedObject)
    {
        super(managedObject);
        startupTime = System.currentTimeMillis();
        server = (Server)managedObject;
    }

    public Handler[] getContexts()
    {
        return server.getChildHandlersByClass(ContextHandler.class);
    }

    public long getStartupTime()
    {
        return startupTime;
    }
}
