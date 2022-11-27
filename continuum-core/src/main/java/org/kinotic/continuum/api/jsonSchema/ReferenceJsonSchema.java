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

/**
 * A simple object to allow referencing other components in the specification, internally and externally.
 *
 * NOTE: this is a continuum extension to the standard Json Specification
 *
 * This should work similar to the Json Reference spec but is kept consistent with other Json Schema types for ease of use.
 * https://swagger.io/specification/#referenceObject
 *
 *
 * Created by navid on 2019-06-12.
 */
public class ReferenceJsonSchema extends JsonSchema {

    private String urn = null;

    public ReferenceJsonSchema() {
    }

    public ReferenceJsonSchema(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    public ReferenceJsonSchema setUrn(String urn) {
        this.urn = urn;
        return this;
    }
}
