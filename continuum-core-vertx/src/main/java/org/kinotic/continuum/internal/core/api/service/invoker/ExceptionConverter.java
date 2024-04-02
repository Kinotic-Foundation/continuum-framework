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

import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.Metadata;

/**
 * Converts an exception to a message that can bes sent on the event bus
 *
 * NOTE: All implementations should be thread safe.
 *
 *
 * Created by Navid Mitchell on 2019-03-30.
 */
public interface ExceptionConverter {

    Event<byte[]> convert(Metadata incomingMetadata, Throwable throwable);

    boolean supports(Metadata incomingMetadata);

}

