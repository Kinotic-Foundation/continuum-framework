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

package org.kinotic.continuum.api.jsonSchema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

/**
 * Java implementation of Json schema spec with extension to support continuum functionality. Any changes to the original will be denoted here.
 *
 * https://json-schema.org/understanding-json-schema/reference/index.html
 *
 * Changes From the Spec:
 *
 *  - Object type:
 *      Additional properties flag will always be false.
 *      Property Dependencies are NOT supported ( https://json-schema.org/understanding-json-schema/reference/object.html#dependenices )
 *      Pattern Properties are NOT supported ( https://json-schema.org/understanding-json-schema/reference/object.html#patternproperties )
 *
 *  - Array Type:
 *      Additional items flag will always be false.
 *
 *  - Generic Keywords:
 *      Currently none of the generic keywords are supported https://json-schema.org/understanding-json-schema/reference/generic.html
 *
 *  Extensions to the Spec:
 *
 *  - Reference type:
 *      This is a type that references another defined schema.
 *      {@link ReferenceJsonSchema}
 *
 *  - Void type:
 *      This is a type to represent a void type.
 *      {@link VoidJsonSchema}
 *
 *  - Function Type:
 *      This is a type to represent a function definition.
 *      {@link FunctionJsonSchema}
 *
 *  - Service Type:
 *      This is a type to represent an service definition.
 *      {@link ServiceJsonSchema}
 *
 * - Namespace Type:
 *      This is a type to represent a namespace definition.
 *      {@link NamespaceJsonSchema}
 *
 * - Date Type:
 *      This is a type to represent a date definition.
 *      {@link DateJsonSchema}
 *
 *
 * Created by navid on 2019-06-11.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringJsonSchema.class, name = "string"),
        @JsonSubTypes.Type(value = NumberJsonSchema.class, name = "number"),
        @JsonSubTypes.Type(value = ObjectJsonSchema.class, name = "object"),
        @JsonSubTypes.Type(value = ArrayJsonSchema.class, name = "array"),
        @JsonSubTypes.Type(value = MapJsonSchema.class, name = "map"),
        @JsonSubTypes.Type(value = BooleanJsonSchema.class, name = "boolean"),
        @JsonSubTypes.Type(value = NullJsonSchema.class, name = "null"),
        @JsonSubTypes.Type(value = ReferenceJsonSchema.class, name = "ref"),
        @JsonSubTypes.Type(value = VoidJsonSchema.class, name = "void"),
        @JsonSubTypes.Type(value = FunctionJsonSchema.class, name = "func"),
        @JsonSubTypes.Type(value = ServiceJsonSchema.class, name = "service"),
        @JsonSubTypes.Type(value = NamespaceJsonSchema.class, name = "namespace"),
        @JsonSubTypes.Type(value = DateJsonSchema.class, name = "date")
})
@JsonInclude(JsonInclude.Include.NON_EMPTY) // do not include any empty or null fields
public abstract class JsonSchema {

    /**
     * The title and description keywords must be strings.
     * A “title” will preferably be short, whereas a “description” will provide a more lengthy explanation about the purpose of the data described by the schema.
     */
    private String title = null;
    private String description = null;


    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public JsonSchema setTitle(String title) {
        this.title = title;
        return this;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public JsonSchema setDescription(String description) {
        this.description = description;
        return this;
    }
}
