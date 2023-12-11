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

package org.kinotic.continuum.gateway.internal.support;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * Created by navid on 12/23/19
 */
@Component
public class DefaultTestService implements TestService{

    @Override
    public String noArgs() {
        return "wat";
    }

    @Override
    public String hello(String who) {
        return "Hello "+who;
    }

    @Override
    public String test(String data, int value) {
        return data+value;
    }

    @Override
    public String testSingleNumber(int value) {
        return Integer.toString(value);
    }

    @Override
    public Mono<String> testMono(String who) {
        return Mono.just("Hello "+who);
    }

    @Override
    public Mono<String> testMonoNoArg() {
        return Mono.just("hello");
    }
}
