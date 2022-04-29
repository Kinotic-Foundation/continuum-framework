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

package com.kinotic.continuum.internal.core.api.service.invoker.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinotic.continuum.core.api.event.Event;
import com.kinotic.continuum.internal.core.api.service.invoker.ArgumentResolver;
import com.kinotic.continuum.internal.core.api.service.invoker.HandlerMethod;
import com.kinotic.continuum.internal.core.api.service.json.AbstractJackson2Support;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.stereotype.Component;

/**
 * Resolves arguments from json data using jackson
 *
 *
 * Created by Navid Mitchell on 2019-04-08.
 */
@Component
public class Jackson2ArgumentResolver extends AbstractJackson2Support implements ArgumentResolver {

    public Jackson2ArgumentResolver(ObjectMapper objectMapper,
                                    ReactiveAdapterRegistry reactiveAdapterRegistry) {
        super(objectMapper, reactiveAdapterRegistry);
    }

    @Override
    public boolean supports(Event<byte[]> incomingEvent) {
        return containsJsonContent(incomingEvent.metadata());
    }

    @Override
    public Object[] resolveArguments(Event<byte[]> incomingEvent, HandlerMethod handlerMethod) {
        return createJavaObjectsFromJsonEvent(incomingEvent, handlerMethod.getMethodParameters(), true);
    }

}
