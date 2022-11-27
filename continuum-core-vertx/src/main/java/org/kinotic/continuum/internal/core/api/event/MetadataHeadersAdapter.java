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

package org.kinotic.continuum.internal.core.api.event;

import org.kinotic.continuum.core.api.event.Metadata;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 🤓 on 5/29/21.
 */
public class MetadataHeadersAdapter implements Iterable<Header> {

    private final Metadata delegate;

    public MetadataHeadersAdapter(Metadata delegate) {
        this.delegate = delegate;
    }

    @Override
    public Iterator<Header> iterator() {
        return new MetadataIteratorAdapter(delegate.iterator());
    }

    private static class MetadataIteratorAdapter implements Iterator<Header> {

        private final Iterator<Map.Entry<String, String>> delegate;

        public MetadataIteratorAdapter(Iterator<Map.Entry<String, String>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public Header next() {
            return new HeaderMapEntryAdapter(delegate.next());
        }

    }

    private static class HeaderMapEntryAdapter implements Header {
        private final Map.Entry<String, String> delegate;

        public HeaderMapEntryAdapter(Map.Entry<String, String> delegate) {
            this.delegate = delegate;
        }

        @Override
        public String key() {
            return delegate.getKey();
        }

        @Override
        public byte[] value() {
            return delegate.getValue().getBytes(StandardCharsets.UTF_8);
        }
    }

}
