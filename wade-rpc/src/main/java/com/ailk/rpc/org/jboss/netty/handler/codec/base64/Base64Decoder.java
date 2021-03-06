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
package com.ailk.rpc.org.jboss.netty.handler.codec.base64;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffers;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandler.Sharable;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipeline;
import com.ailk.rpc.org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import com.ailk.rpc.org.jboss.netty.handler.codec.frame.Delimiters;
import com.ailk.rpc.org.jboss.netty.handler.codec.frame.FrameDecoder;
import com.ailk.rpc.org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import com.ailk.rpc.org.jboss.netty.util.CharsetUtil;

/**
 * Decodes a Base64-encoded {@link ChannelBuffer} or US-ASCII {@link String}
 * into a {@link ChannelBuffer}.  Please note that this decoder must be used
 * with a proper {@link FrameDecoder} such as {@link DelimiterBasedFrameDecoder}
 * if you are using a stream-based transport such as TCP/IP.  A typical decoder
 * setup for TCP/IP would be:
 * <pre>
 * {@link ChannelPipeline} pipeline = ...;
 *
 * // Decoders
 * pipeline.addLast("frameDecoder", new {@link DelimiterBasedFrameDecoder}(80, {@link Delimiters#nulDelimiter()}));
 * pipeline.addLast("base64Decoder", new {@link Base64Decoder}());
 *
 * // Encoder
 * pipeline.addLast("base64Encoder", new {@link Base64Encoder}());
 * </pre>
 * @apiviz.landmark
 * @apiviz.uses com.ailk.rpc.org.jboss.netty.handler.codec.base64.Base64
 */
@Sharable
public class Base64Decoder extends OneToOneDecoder {

    private final Base64Dialect dialect;

    public Base64Decoder() {
        this(Base64Dialect.STANDARD);
    }

    public Base64Decoder(Base64Dialect dialect) {
        if (dialect == null) {
            throw new NullPointerException("dialect");
        }
        this.dialect = dialect;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg)
            throws Exception {
        if (msg instanceof String) {
            msg = ChannelBuffers.copiedBuffer((String) msg, CharsetUtil.US_ASCII);
        } else if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }

        ChannelBuffer src = (ChannelBuffer) msg;
        return Base64.decode(
                src, src.readerIndex(), src.readableBytes(), dialect);
    }

}
