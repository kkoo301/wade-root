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

package com.wade.container.util.ajax;

import java.lang.reflect.Method;
import java.util.Map;

import com.wade.container.util.Loader;
import com.wade.container.util.ajax.JSON.Output;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

/* ------------------------------------------------------------ */
/**
 * Convert an {@link Enum} to JSON.
 * If fromJSON is true in the constructor, the JSON generated will
 * be of the form {class="com.acme.TrafficLight",value="Green"}
 * If fromJSON is false, then only the string value of the enum is generated.
 *
 *
 */
public class JSONEnumConvertor implements JSON.Convertor
{
    private static final Logger LOG = Log.getLogger(JSONEnumConvertor.class);
    private boolean _fromJSON;
    private Method _valueOf;
    {
        try
        {
            Class<?> e = Loader.loadClass(getClass(),"java.lang.Enum");
            _valueOf=e.getMethod("valueOf",Class.class,String.class);
        }
        catch(Exception e)
        {
            throw new RuntimeException("!Enums",e);
        }
    }

    public JSONEnumConvertor()
    {
        this(false);
    }

    public JSONEnumConvertor(boolean fromJSON)
    {
        _fromJSON=fromJSON;
    }

    public Object fromJSON(Map map)
    {
        if (!_fromJSON)
            throw new UnsupportedOperationException();
        try
        {
            Class c=Loader.loadClass(getClass(),(String)map.get("class"));
            return _valueOf.invoke(null,c,map.get("value"));
        }
        catch(Exception e)
        {
            LOG.warn(e);
        }
        return null;
    }

    public void toJSON(Object obj, Output out)
    {
        if (_fromJSON)
        {
            out.addClass(obj.getClass());
            out.add("value",((Enum)obj).name());
        }
        else
        {
            out.add(((Enum)obj).name());
        }
    }
}