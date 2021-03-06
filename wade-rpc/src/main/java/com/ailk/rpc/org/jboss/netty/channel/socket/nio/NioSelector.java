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
package com.ailk.rpc.org.jboss.netty.channel.socket.nio;

import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelFuture;

import java.nio.channels.Selector;


public interface NioSelector extends Runnable {

    void register(Channel channel, ChannelFuture future);

    /**
     * Replaces the current {@link Selector} with a new {@link Selector} to work around the infamous epoll 100% CPU
     * bug.
     */
    void rebuildSelector();

    void shutdown();
}
