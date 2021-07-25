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

/**
 * Export all the things
 */

export * from './core/api/crud/ICrudServiceProxy'
export * from './core/api/crud/CrudServiceProxy'
export * from './core/api/crud/ICrudServiceProxyFactory'
export * from './core/api/crud/IDataSource'
export * from './core/api/crud/Identifiable'
export * from './core/api/crud/Pageable'
export * from './core/api/crud/Page'
export * from './core/api/crud/Sort'

export * from './core/api/EventBus'
export * from './core/api/IEventBus'
export * from './core/api/IServiceRegistry'
export * from './core/api/ServiceRegistry'

export * from './core/api/StreamData'

// insure DI objects are added to the InversifyJs container that do not need to be exported
import './core/api/crud/CrudServiceProxyFactory'
