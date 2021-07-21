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

package com.kinotic.continuum.internal.utils;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.MonoSink;

/**
 * Project reactor utils to simplify implementation details
 *
 *
 * Created by navid on 2/3/20
 */
public class ReactorUtils {

    public static <T> BaseSubscriber<T> monoSinkToSubscriber(MonoSink<T> sink){
        return new MonoSinkDelegatingSubscriber<>(sink);
    }

    private static class MonoSinkDelegatingSubscriber<T> extends BaseSubscriber<T> {

        private final MonoSink<T> delegate;

        public MonoSinkDelegatingSubscriber(MonoSink<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void hookOnNext(T value) {
            delegate.success(value);
        }

        @Override
        protected void hookOnComplete() {
            delegate.success();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            delegate.error(throwable);
        }
    }

}
