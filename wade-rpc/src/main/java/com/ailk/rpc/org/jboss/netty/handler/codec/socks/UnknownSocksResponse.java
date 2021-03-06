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
package com.ailk.rpc.org.jboss.netty.handler.codec.socks;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;

/**
 * An unknown socks response.
 *
 * @see {@link SocksInitResponseDecoder}
 * @see {@link SocksAuthResponseDecoder}
 * @see {@link SocksCmdResponseDecoder}
 */
public final class UnknownSocksResponse extends SocksResponse {

    public UnknownSocksResponse() {
        super(SocksResponseType.UNKNOWN);
    }

    @Override
    public void encodeAsByteBuf(ChannelBuffer buffer) {
        // NOOP
    }
}
