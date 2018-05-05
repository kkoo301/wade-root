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

import java.util.concurrent.ConcurrentMap;

import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.util.internal.ConcurrentHashMap;

/**
 */
final class LocalChannelRegistry {

    private static final ConcurrentMap<LocalAddress, Channel> map =
        new ConcurrentHashMap<LocalAddress, Channel>();

    static boolean isRegistered(LocalAddress address) {
        return map.containsKey(address);
    }

    static Channel getChannel(LocalAddress address) {
        return map.get(address);
    }

    static boolean register(LocalAddress address, Channel channel) {
        return map.putIfAbsent(address, channel) == null;
    }

    static boolean unregister(LocalAddress address) {
        return map.remove(address) != null;
    }

    private LocalChannelRegistry() {
        // Unused
    }
}
