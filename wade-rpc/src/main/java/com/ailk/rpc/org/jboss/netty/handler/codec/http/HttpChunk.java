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
package com.ailk.rpc.org.jboss.netty.handler.codec.http;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffers;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipeline;

/**
 * An HTTP chunk which is used for HTTP chunked transfer-encoding.
 * {@link HttpMessageDecoder} generates {@link HttpChunk} after
 * {@link HttpMessage} when the content is large or the encoding of the content
 * is 'chunked.  If you prefer not to receive {@link HttpChunk} in your handler,
 * please {@link HttpChunkAggregator} after {@link HttpMessageDecoder} in the
 * {@link ChannelPipeline}.
 * @apiviz.landmark
 */
public interface HttpChunk {

    /**
     * The 'end of content' marker in chunked encoding.
     */
    HttpChunkTrailer LAST_CHUNK = new HttpChunkTrailer() {
        public ChannelBuffer getContent() {
            return ChannelBuffers.EMPTY_BUFFER;
        }

        public void setContent(ChannelBuffer content) {
            throw new IllegalStateException("read-only");
        }

        public boolean isLast() {
            return true;
        }

        @Deprecated
        public void addHeader(String name, Object value) {
            throw new IllegalStateException("read-only");
        }

        @Deprecated
        public void clearHeaders() {
            // NOOP
        }

        @Deprecated
        public boolean containsHeader(String name) {
            return false;
        }

        @Deprecated
        public String getHeader(String name) {
            return null;
        }

        @Deprecated
        public Set<String> getHeaderNames() {
            return Collections.emptySet();
        }

        @Deprecated
        public List<String> getHeaders(String name) {
            return Collections.emptyList();
        }

        @Deprecated
        public List<Map.Entry<String, String>> getHeaders() {
            return Collections.emptyList();
        }

        @Deprecated
        public void removeHeader(String name) {
            // NOOP
        }

        @Deprecated
        public void setHeader(String name, Object value) {
            throw new IllegalStateException("read-only");
        }

        @Deprecated
        public void setHeader(String name, Iterable<?> values) {
            throw new IllegalStateException("read-only");
        }

        public HttpHeaders trailingHeaders() {
            return HttpHeaders.EMPTY_HEADERS;
        }
    };

    /**
     * Returns {@code true} if and only if this chunk is the 'end of content'
     * marker.
     */
    boolean isLast();

    /**
     * Returns the content of this chunk.  If this is the 'end of content'
     * marker, {@link ChannelBuffers#EMPTY_BUFFER} will be returned.
     */
    ChannelBuffer getContent();

    /**
     * Sets the content of this chunk.  If an empty buffer is specified,
     * this chunk becomes the 'end of content' marker.
     */
    void setContent(ChannelBuffer content);
}
