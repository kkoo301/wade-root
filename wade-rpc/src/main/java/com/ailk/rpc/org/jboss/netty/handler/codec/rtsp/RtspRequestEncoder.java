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
package com.ailk.rpc.org.jboss.netty.handler.codec.rtsp;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.handler.codec.http.HttpMessage;
import com.ailk.rpc.org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * Encodes an RTSP request represented in {@link HttpRequest} into
 * a {@link ChannelBuffer}.

 */
public class RtspRequestEncoder extends RtspMessageEncoder {

    @Override
    protected void encodeInitialLine(ChannelBuffer buf, HttpMessage message)
            throws Exception {
        HttpRequest request = (HttpRequest) message;
        buf.writeBytes(request.getMethod().toString().getBytes("ASCII"));
        buf.writeByte((byte) ' ');
        buf.writeBytes(request.getUri().getBytes("UTF-8"));
        buf.writeByte((byte) ' ');
        buf.writeBytes(request.getProtocolVersion().toString().getBytes("ASCII"));
        buf.writeByte((byte) '\r');
        buf.writeByte((byte) '\n');
    }
}
