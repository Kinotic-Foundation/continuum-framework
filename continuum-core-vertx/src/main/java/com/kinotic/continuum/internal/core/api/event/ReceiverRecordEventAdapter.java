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

import com.kinotic.continuum.core.api.CRI;
import com.kinotic.continuum.core.api.event.Event;
import com.kinotic.continuum.core.api.event.Metadata;
import reactor.kafka.receiver.ReceiverRecord;

/**
 *
 * Created by Navid Mitchell on 8/12/20
 */
public class ReceiverRecordEventAdapter implements Event<byte[]> {

    private final CRI cri;
    private final ReceiverRecord<String, byte[]> receiverRecord;
    private final HeadersMetadataAdapter metadataAdapter;

    public ReceiverRecordEventAdapter(CRI cri,
                                      ReceiverRecord<String, byte[]> receiverRecord) {
        this.cri = cri;
        this.receiverRecord = receiverRecord;
        this.metadataAdapter = new HeadersMetadataAdapter(receiverRecord.headers());
    }

    @Override
    public CRI cri() {
        return cri;
    }

    @Override
    public Metadata metadata() {
        return metadataAdapter;
    }

    @Override
    public byte[] data() {
        return receiverRecord.value();
    }
}
