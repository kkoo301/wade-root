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
package com.ailk.rpc.org.jboss.netty.channel.local;

import static com.ailk.rpc.org.jboss.netty.channel.Channels.*;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ailk.rpc.org.jboss.netty.channel.AbstractServerChannel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelConfig;
import com.ailk.rpc.org.jboss.netty.channel.ChannelFactory;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipeline;
import com.ailk.rpc.org.jboss.netty.channel.ChannelSink;
import com.ailk.rpc.org.jboss.netty.channel.DefaultServerChannelConfig;

/**
 */
final class DefaultLocalServerChannel extends AbstractServerChannel
                                      implements LocalServerChannel {

    final ChannelConfig channelConfig;
    final AtomicBoolean bound = new AtomicBoolean();
    volatile LocalAddress localAddress;

    DefaultLocalServerChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(factory, pipeline, sink);
        channelConfig = new DefaultServerChannelConfig();
        fireChannelOpen(this);
    }

    public ChannelConfig getConfig() {
        return channelConfig;
    }

    public boolean isBound() {
        return isOpen() && bound.get();
    }

    public LocalAddress getLocalAddress() {
        return isBound()? localAddress : null;
    }

    public LocalAddress getRemoteAddress() {
        return null;
    }

    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
}
