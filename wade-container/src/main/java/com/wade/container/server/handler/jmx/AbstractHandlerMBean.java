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

package com.wade.container.server.handler.jmx;

import java.io.IOException;

import com.wade.container.jmx.ObjectMBean;
import com.wade.container.server.Server;
import com.wade.container.server.handler.AbstractHandler;
import com.wade.container.server.handler.AbstractHandlerContainer;
import com.wade.container.server.handler.ContextHandler;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

public class AbstractHandlerMBean extends ObjectMBean
{
    private static final Logger LOG = Log.getLogger(AbstractHandlerMBean.class);

    /* ------------------------------------------------------------ */
    public AbstractHandlerMBean(Object managedObject)
    {
        super(managedObject);
    }

    /* ------------------------------------------------------------ */
    @Override
    public String getObjectContextBasis()
    {
        if (_managed != null )
        {
            String basis = null;
            if (_managed instanceof ContextHandler)
            {
                return null;
            }
            else if (_managed instanceof AbstractHandler)
            {
                AbstractHandler handler = (AbstractHandler)_managed;
                Server server = handler.getServer();
                if (server != null)
                {
                    ContextHandler context = 
                        AbstractHandlerContainer.findContainerOf(server,
                                ContextHandler.class, handler);
                    
                    if (context != null)
                        basis = getContextName(context);
                }
            }
            if (basis != null)
                return basis;
        }
        return super.getObjectContextBasis();
    }
    
    /* ------------------------------------------------------------ */
    @Override
    public String getObjectNameBasis()
    {
        if (_managed != null )
        {
            String name = null;
            if (_managed instanceof ContextHandler)
            {
                ContextHandler context = (ContextHandler)_managed;
                name = getContextName(context);
            }
            
            if (name != null)
                return name;
        }
        
        return super.getObjectNameBasis();
    }

    /* ------------------------------------------------------------ */
    protected String getContextName(ContextHandler context)
    {
        String name = null;
        
        if (context.getContextPath()!=null && context.getContextPath().length()>0)
        {
            int idx = context.getContextPath().lastIndexOf('/');
            name = idx < 0 ? context.getContextPath() : context.getContextPath().substring(++idx);
            if (name==null || name.length()==0)
                name= "ROOT";
        }
        
        if (name==null && context.getBaseResource()!=null)
        {
            try
            {
                if (context.getBaseResource().getFile()!=null)
                    name = context.getBaseResource().getFile().getName();
            }
            catch(IOException e)
            {
                LOG.ignore(e);
                name=context.getBaseResource().getName();
            }
        }
        
        return name;
    }
}
