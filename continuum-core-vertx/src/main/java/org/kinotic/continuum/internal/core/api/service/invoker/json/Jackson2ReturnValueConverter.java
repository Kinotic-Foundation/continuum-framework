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

package org.kinotic.continuum.internal.core.api.service.invoker.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kinotic.continuum.api.config.ContinuumProperties;
import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.EventConstants;
import org.kinotic.continuum.core.api.event.Metadata;
import org.kinotic.continuum.internal.core.api.service.invoker.ReturnValueConverter;
import org.kinotic.continuum.internal.core.api.service.json.AbstractJackson2Support;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;

/**
 * Resolves return values to json data using jackson
 * Created by Navid Mitchell on 2019-04-08.
 */
@Component
public class Jackson2ReturnValueConverter extends AbstractJackson2Support implements ReturnValueConverter {

    public Jackson2ReturnValueConverter(ObjectMapper objectMapper,
                                        ReactiveAdapterRegistry reactiveAdapterRegistry,
                                        ContinuumProperties continuumProperties) {
        super(objectMapper, reactiveAdapterRegistry, continuumProperties);
    }

    @Override
    public Event<byte[]> convert(Metadata incomingMetadata, Class<?> returnType, Object returnValue) {
        // insure void return types are not mistakenly seen as null
        if(Void.TYPE.isAssignableFrom(returnType)){
            returnValue = Void.TYPE;
        }
        HashMap<String,String> headers = new HashMap<>(1);
        headers.put(EventConstants.CONTENT_TYPE_HEADER, MimeTypeUtils.APPLICATION_JSON_VALUE);

        return createOutgoingEvent(incomingMetadata, headers, returnValue);
    }

    @Override
    public boolean supports(Metadata incomingMetadata, Class<?> returnType) {
        return containsJsonContent(incomingMetadata);
    }

}
