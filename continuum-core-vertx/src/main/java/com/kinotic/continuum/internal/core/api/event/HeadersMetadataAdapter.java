/*
 *
 * Copyright 2008-2021 Kinotic and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kinotic.continuum.internal.core.api.event;

import com.kinotic.continuum.core.api.event.Metadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * Created by Navid Mitchell on 8/12/20
 */
public class HeadersMetadataAdapter implements Metadata {

    private final Headers headers;

    public HeadersMetadataAdapter(Headers headers) {
        this.headers = headers;
    }

    @Override
    public String get(String key) {
        String ret = null;
        Header header = headers.lastHeader(key);
        if(header != null){
            ret = new String(header.value());
        }
        return ret;
    }

    @Override
    public void put(String key, String value) {
        headers.add(key, value.getBytes());
    }

    @Override
    public void remove(String key) {
        headers.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return headers.lastHeader(key) != null;
    }

    @Override
    public void clear() {
        for(Header header: headers){
            headers.remove(header.key());
        }
    }

    @Override
    public boolean isEmpty() {
        return headers.toArray().length == 0;
    }

    @Override
    public int size() {
        return headers.toArray().length;
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return new HeaderIteratorAdapter(headers.iterator());
    }

    private static class HeaderIteratorAdapter implements Iterator<Map.Entry<String, String>> {

        private final Iterator<Header> delegate;

        public HeaderIteratorAdapter(Iterator<Header> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public java.util.Map.Entry<String, String> next() {
            return new MapEntryHeaderAdapter(delegate.next());
        }

    }

    private static class MapEntryHeaderAdapter implements Map.Entry<String, String> {
        private final Header header;

        public MapEntryHeaderAdapter(Header header) {
            this.header = header;
        }

        @Override
        public String getKey() {
            return header.key();
        }

        @Override
        public String getValue() {
            return new String(header.value());
        }

        @Override
        public String setValue(String value) {
            throw new UnsupportedOperationException("setValue is not supported");
        }
    }


}
