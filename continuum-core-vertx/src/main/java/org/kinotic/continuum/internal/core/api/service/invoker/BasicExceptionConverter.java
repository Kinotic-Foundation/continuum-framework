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

package org.kinotic.continuum.internal.core.api.service.invoker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kinotic.continuum.api.config.ContinuumProperties;
import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.EventConstants;
import org.kinotic.continuum.core.api.event.Metadata;
import org.kinotic.continuum.core.api.service.ServiceExceptionWrapper;
import org.kinotic.continuum.internal.utils.EventUtil;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.EncodingException;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Navid Mitchell on 2019-04-08.
 */
@Component
@Order
public class BasicExceptionConverter implements ExceptionConverter {

    private final ContinuumProperties properties;
    private final ObjectMapper objectMapper;

    public BasicExceptionConverter(ContinuumProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(Metadata incomingMetadata) {
        return true;
    }

    @Override
    public Event<byte[]> convert(Metadata incomingMetadata, Throwable throwable) {
        Map<String, String> headers = new HashMap<>(2);

        headers.put(EventConstants.ERROR_HEADER, throwable.getMessage());
        headers.put(EventConstants.CONTENT_TYPE_HEADER, MimeTypeUtils.APPLICATION_JSON_VALUE);

        return EventUtil.createReplyEvent(incomingMetadata, headers, () -> {
            Class<? extends Throwable> clazz = throwable.getClass();
            ServiceExceptionWrapper wrapper = new ServiceExceptionWrapper(clazz.getSimpleName(),
                                                                          clazz.getName(),
                                                                          throwable.getMessage());

            if(properties.isDebug()) {
                wrapper.setStackTrace(throwable.getStackTrace());
            }
            try {
                return objectMapper.writeValueAsBytes(wrapper);
            } catch (JsonProcessingException e) {
                throw new EncodingException("JSON encoding error: " + e.getOriginalMessage(), e);
            }
        });
    }

}
