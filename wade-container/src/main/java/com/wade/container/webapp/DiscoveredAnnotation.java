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

package com.wade.container.webapp;

import com.wade.container.util.Loader;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

/**
 * DiscoveredAnnotation
 *
 * Represents an annotation that has been discovered
 * by scanning source code of WEB-INF/classes and WEB-INF/lib jars.
 * 
 */
public abstract class DiscoveredAnnotation
{
    private static final Logger LOG = Log.getLogger(DiscoveredAnnotation.class);

    protected WebAppContext _context;
    protected String _className;
    protected Class<?> _clazz;
    
    public abstract void apply();
    
    public DiscoveredAnnotation (WebAppContext context, String className)
    {
        _context = context;
        _className = className;
    } 
    
    
    public Class<?> getTargetClass()
    {
        if (_clazz != null)
            return _clazz;
        
        loadClass();
        
        return _clazz;
    }
    
    private void loadClass ()
    {
        if (_clazz != null)
            return;
        
        if (_className == null)
            return;
        
        try
        {
            _clazz = Loader.loadClass(null, _className);
        }
        catch (Exception e)
        {
            LOG.warn(e);
        }
    }  
}
