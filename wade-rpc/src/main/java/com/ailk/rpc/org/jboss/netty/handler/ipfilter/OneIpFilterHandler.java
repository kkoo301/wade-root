/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ailk.rpc.org.jboss.netty.handler.ipfilter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ailk.rpc.org.jboss.netty.channel.ChannelEvent;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandler.Sharable;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.channel.ChannelState;
import com.ailk.rpc.org.jboss.netty.channel.ChannelStateEvent;

/**
 * Handler that block any new connection if there are already a currently active
 * channel connected with the same InetAddress (IP).<br>
 * <br>
 * <p/>
 * Take care to not change isBlocked method except if you know what you are doing
 * since it is used to test if the current closed connection is to be removed
 * or not from the map of currently connected channel.
 */
@Sharable
public class OneIpFilterHandler extends IpFilteringHandlerImpl {
    /** HashMap of current remote connected InetAddress */
    private final ConcurrentMap<InetAddress, Boolean> connectedSet = new ConcurrentHashMap<InetAddress, Boolean>();

    @Override
    protected boolean accept(ChannelHandlerContext ctx, ChannelEvent e, InetSocketAddress inetSocketAddress)
            throws Exception {
        InetAddress inetAddress = inetSocketAddress.getAddress();
        if (connectedSet.containsKey(inetAddress)) {
            return false;
        }
        connectedSet.put(inetAddress, Boolean.TRUE);
        return true;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        super.handleUpstream(ctx, e);
        // Try to remove entry from Map if already exists
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent evt = (ChannelStateEvent) e;
            if (evt.getState() == ChannelState.CONNECTED) {
                if (evt.getValue() == null) {
                    // DISCONNECTED but was this channel blocked or not
                    if (isBlocked(ctx)) {
                        // remove inetsocketaddress from set since this channel was not blocked before
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) e.getChannel().getRemoteAddress();
                        connectedSet.remove(inetSocketAddress.getAddress());
                    }
                }
            }
        }
    }

}
