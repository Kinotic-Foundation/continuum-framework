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

package org.kinotic.continuum.idl.internal.api.jsonSchema;

import org.kinotic.continuum.idl.api.datestyles.DateStyle;

/**
 *
 * Created by nic on 2019-12-10.
 */
public class DateJsonSchema extends JsonSchema {

    private DateStyle format = null;

    public DateStyle getFormat() {
        return format;
    }

    public DateJsonSchema setFormat(DateStyle format) {
        this.format = format;
        return this;
    }
}
