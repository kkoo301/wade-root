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
package com.ailk.rpc.org.jboss.netty.channel.socket;

import java.net.InetSocketAddress;

import com.ailk.rpc.org.jboss.netty.channel.ServerChannel;

/**
 * A TCP/IP {@link ServerChannel} which accepts incoming TCP/IP connections.
 *
 * @apiviz.landmark
 * @apiviz.composedOf com.ailk.rpc.org.jboss.netty.channel.socket.ServerSocketChannelConfig
 */
public interface ServerSocketChannel extends ServerChannel {
    ServerSocketChannelConfig getConfig();
    InetSocketAddress getLocalAddress();
    InetSocketAddress getRemoteAddress();
}
