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

package com.wade.container.client.security;


import java.io.IOException;

import com.wade.container.client.HttpExchange;
import com.wade.container.http.HttpHeaders;
import com.wade.container.io.Buffer;
import com.wade.container.io.ByteArrayBuffer;
import com.wade.container.util.B64Code;
import com.wade.container.util.StringUtil;

/**
 * Sets proxy authentication headers for BASIC authentication challenges
 * 
 * 
 */
public class ProxyAuthorization implements Authentication
{
    private Buffer _authorization;
    
    public ProxyAuthorization(String username,String password) throws IOException
    {
        String authenticationString = "Basic " + B64Code.encode( username + ":" + password, StringUtil.__ISO_8859_1);
        _authorization= new ByteArrayBuffer(authenticationString);
    }
    
    /**
     * BASIC proxy authentication is of the form
     * 
     * encoded credentials are of the form: username:password
     * 
     * 
     */
    public void setCredentials( HttpExchange exchange ) throws IOException
    {
        exchange.setRequestHeader( HttpHeaders.PROXY_AUTHORIZATION_BUFFER, _authorization);
    }
}
