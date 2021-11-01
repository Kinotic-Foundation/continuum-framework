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

package com.kinotic.continuum.internal.core.api.service.invoker;

import com.kinotic.continuum.core.api.event.Event;

/**
 * Converts the return value to a {@link Event} that can bes sent on the {@link com.kinotic.continuum.core.api.event.EventBusService}
 * NOTE: All implementations should be threadsafe.
 *
 *
 * Created by Navid Mitchell on 2019-03-29.
 */
public interface ReturnValueConverter {

    boolean supports(Event<byte[]> incomingEvent, Class<?> returnType);

    Event<byte[]> convert(Event<byte[]> incomingEvent, Class<?> returnType, Object returnValue);

}
