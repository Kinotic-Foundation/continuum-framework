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

package org.kinotic.continuum.internal.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.kinotic.continuum.core.api.crud.CursorPage;
import org.kinotic.continuum.core.api.crud.Page;

import java.io.IOException;

/**
 *
 * Created by navid on 2/4/20
 */
@SuppressWarnings("rawtypes")
public class PageSerializer extends JsonSerializer<Page> {

    @Override
    public void serialize(Page page, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeStartObject();

        if(page.getTotalElements() != null){
            jsonGenerator.writeNumberField("totalElements", page.getTotalElements());
        }else{
            jsonGenerator.writeNullField("totalElements");
        }

        jsonGenerator.writeArrayFieldStart("content");
        for (Object value: page.getContent()) {
            jsonGenerator.writeObject(value);
        }
        jsonGenerator.writeEndArray();

        if(page instanceof CursorPage) {
            jsonGenerator.writeStringField("cursor", ((CursorPage) page).getCursor());
        }

        jsonGenerator.writeEndObject();
    }

}
