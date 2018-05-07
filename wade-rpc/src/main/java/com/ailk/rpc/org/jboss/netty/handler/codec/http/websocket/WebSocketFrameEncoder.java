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
package com.ailk.rpc.org.jboss.netty.handler.codec.http.websocket;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandler.Sharable;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * @deprecated Use <tt>com.ailk.rpc.org.jboss.netty.handler.codec.http.websocketx</tt> instead.
 *
 * Encodes a {@link WebSocketFrame} into a {@link ChannelBuffer}.
 * <p>
 * For the detailed instruction on adding add Web Socket support to your HTTP
 * server, take a look into the <tt>WebSocketServer</tt> example located in the
 * {@code com.ailk.rpc.org.jboss.netty.example.http.websocket} package.
 * @apiviz.landmark
 * @apiviz.uses com.ailk.rpc.org.jboss.netty.handler.codec.http.websocket.WebSocketFrame
 */
@Deprecated
@Sharable
public class WebSocketFrameEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            int type = frame.getType();
            if (frame.isText()) {
                // Text frame
                ChannelBuffer data = frame.getBinaryData();
                ChannelBuffer encoded =
                    channel.getConfig().getBufferFactory().getBuffer(
                            data.order(), data.readableBytes() + 2);
                encoded.writeByte((byte) type);
                encoded.writeBytes(data, data.readerIndex(), data.readableBytes());
                encoded.writeByte((byte) 0xFF);
                return encoded;
            } else {
                // Binary frame
                ChannelBuffer data = frame.getBinaryData();
                int dataLen = data.readableBytes();
                ChannelBuffer encoded =
                    channel.getConfig().getBufferFactory().getBuffer(
                            data.order(), dataLen + 5);

                // Encode type.
                encoded.writeByte((byte) type);

                // Encode length.
                int b1 = dataLen >>> 28 & 0x7F;
                int b2 = dataLen >>> 14 & 0x7F;
                int b3 = dataLen >>> 7 & 0x7F;
                int b4 = dataLen & 0x7F;
                if (b1 == 0) {
                    if (b2 == 0) {
                        if (b3 == 0) {
                            encoded.writeByte(b4);
                        } else {
                            encoded.writeByte(b3 | 0x80);
                            encoded.writeByte(b4);
                        }
                    } else {
                        encoded.writeByte(b2 | 0x80);
                        encoded.writeByte(b3 | 0x80);
                        encoded.writeByte(b4);
                    }
                } else {
                    encoded.writeByte(b1 | 0x80);
                    encoded.writeByte(b2 | 0x80);
                    encoded.writeByte(b3 | 0x80);
                    encoded.writeByte(b4);
                }

                // Encode binary data.
                encoded.writeBytes(data, data.readerIndex(), dataLen);
                return encoded;
            }
        }
        return msg;
    }
}