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

package com.wade.container.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.wade.container.io.Buffer;
import com.wade.container.io.NetworkTrafficListener;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

public class NetworkTrafficSelectChannelEndPoint extends SelectChannelEndPoint
{
    private static final Logger LOG = Log.getLogger(NetworkTrafficSelectChannelEndPoint.class);

    private final List<NetworkTrafficListener> listeners;

    public NetworkTrafficSelectChannelEndPoint(SocketChannel channel, SelectorManager.SelectSet selectSet, SelectionKey key, int maxIdleTime, List<NetworkTrafficListener> listeners) throws IOException
    {
        super(channel, selectSet, key, maxIdleTime);
        this.listeners = listeners;
    }

    @Override
    public int fill(Buffer buffer) throws IOException
    {
        int read = super.fill(buffer);
        notifyIncoming(buffer, read);
        return read;
    }

    @Override
    public int flush(Buffer buffer) throws IOException
    {
        int position = buffer.getIndex();
        int written = super.flush(buffer);
        notifyOutgoing(buffer, position, written);
        return written;
    }

    @Override
    protected int gatheringFlush(Buffer header, ByteBuffer bbuf0, Buffer buffer, ByteBuffer bbuf1) throws IOException
    {
        int headerPosition = header.getIndex();
        int headerLength = header.length();
        int bufferPosition = buffer.getIndex();
        int written = super.gatheringFlush(header, bbuf0, buffer,bbuf1);
        notifyOutgoing(header, headerPosition, written > headerLength ? headerLength : written);
        notifyOutgoing(buffer, bufferPosition, written > headerLength ? written - headerLength : 0);
        return written;
    }

    public void notifyOpened()
    {
        if (listeners != null && !listeners.isEmpty())
        {
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    listener.opened(_socket);
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }

    public void notifyIncoming(Buffer buffer, int read)
    {
        if (listeners != null && !listeners.isEmpty() && read > 0)
        {
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    Buffer view = buffer.asReadOnlyBuffer();
                    listener.incoming(_socket, view);
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }

    public void notifyOutgoing(Buffer buffer, int position, int written)
    {
        if (listeners != null && !listeners.isEmpty() && written > 0)
        {
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    Buffer view = buffer.asReadOnlyBuffer();
                    view.setGetIndex(position);
                    view.setPutIndex(position + written);
                    listener.outgoing(_socket, view);
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }

    public void notifyClosed()
    {
        if (listeners != null && !listeners.isEmpty())
        {
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    listener.closed(_socket);
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }
}
