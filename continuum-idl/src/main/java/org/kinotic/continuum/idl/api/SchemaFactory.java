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

package org.kinotic.continuum.idl.api;

/**
 * Provides the ability to create {@link TypeSchema}'s
 * Created by navid on 2019-06-13.
 */
public interface SchemaFactory {

    /**
     * Creates a {@link TypeSchema} for the given {@link Class}
     * This method treats the class as a standard POJO or basic type.
     * If you need to convert a class that is a "service" use {@link SchemaFactory#createForService(Class)}
     *
     * @param clazz the class to create the schema for
     * @return the newly created {@link TypeSchema}
     */
    TypeSchema createForClass(Class<?> clazz);

    /**
     * Creates a {@link NamespaceSchema} for the given {@link Class}
     * This method treats the class as a java "service"
     *
     * @param clazz the class to create the schema for
     * @return the newly created {@link NamespaceSchema}
     */
    NamespaceSchema createForService(Class<?> clazz);

}
