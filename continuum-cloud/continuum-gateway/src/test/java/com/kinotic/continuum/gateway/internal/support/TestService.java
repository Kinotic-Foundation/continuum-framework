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

package com.kinotic.continuum.gateway.internal.support;

import com.kinotic.continuum.api.annotations.Publish;
import reactor.core.publisher.Mono;

/**
 *
 * Created by navid on 12/23/19
 */
@Publish(version = "0.1.0")
public interface TestService {

    String noArgs();

    String hello(String who);

    String test(String data, int value);

    String testSingleNumber(int value);

    Mono<String> testMono(String who);

    Mono<String> testMonoNoArg();

}
