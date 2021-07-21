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

package com.kinotic.continuum.internal.core.api.support;

import com.kinotic.continuum.api.annotations.Proxy;
import io.vertx.core.Future;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 *
 * Created by navid on 10/30/19
 */
@Proxy(targetClass = RpcTestService.class, version = "0.1.0")
public interface RpcTestServiceProxy {

    Mono<String> getString();

    Mono<SimpleObject> getSimpleObject();

    Mono<String> getSimpleObjectToString(SimpleObject simpleObject);

    Mono<String> getUnknownFailure();

    Future<String> getAnotherString();

    Future<String> getVertxFutureNullString();

    Mono<ABunchOfArgumentsHolder> acceptABunchOfArguments(int intValue,
                                                          long longValue,
                                                          String stringValue,
                                                          boolean boolValue,
                                                          SimpleObject simpleObject,
                                                          List<String> listOfStrings);

    Mono<List<List<String>>> getAListOfLists(List<List<String>> inputList);

    Mono<List<String>> getListOfStrings();

    Mono<Integer> putListOfStrings(List<String> strings);

    Mono<List<String>> modifyListOfStrings(List<String> stringsToModify);

    Flux<Integer> getLimitedFlux();

    Flux<String> getInfiniteFlux();

    Mono<String> getMonoWithValue();

    Mono<Void> getMonoWithVoidFromEmpty();

    Mono<Void> getMonoWithVoidFromNull();

    Mono<String> getMonoStringNull();

    Mono<Integer> getMonoIntegerNull();

    Mono<String> getRemoteMonoFailure();

    Mono<String> getMonoEmptyString();

    Mono<String> getMonoStringLiterallyNull();

}
