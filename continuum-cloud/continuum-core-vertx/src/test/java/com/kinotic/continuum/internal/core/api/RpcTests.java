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

package com.kinotic.continuum.internal.core.api;

import com.google.common.collect.Lists;
import com.kinotic.continuum.internal.core.api.service.rpc.RpcInvocationException;
import com.kinotic.continuum.internal.core.api.support.ABunchOfArgumentsHolder;
import com.kinotic.continuum.internal.core.api.support.RpcTestService;
import com.kinotic.continuum.internal.core.api.support.RpcTestServiceProxy;
import com.kinotic.continuum.internal.core.api.support.SimpleObject;
import io.vertx.core.Future;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.security.SecureRandom;
import java.security.Security;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 *
 * Created by navid on 10/30/19
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class RpcTests {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // these are not detected because continuum wires them..
    @Autowired
    private RpcTestServiceProxy rpcTestServiceProxy;

    @Test
    public void testRpcMonoString(){
        Mono<String> mono = rpcTestServiceProxy.getString();

        StepVerifier.create(mono).expectNext(RpcTestService.STRING_VALUE).expectComplete().verify();
    }

    @Test
    public void testReceiveCollection(){
        Mono<List<String>> mono = rpcTestServiceProxy.getListOfStrings();
        StepVerifier.create(mono)
                    .expectNext(RpcTestService.LIST_OF_STRINGS)
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testSendCollection(){
        Mono<Integer> mono = rpcTestServiceProxy.putListOfStrings(RpcTestService.LIST_OF_STRINGS);
        StepVerifier.create(mono)
                    .expectNext(RpcTestService.LIST_OF_STRINGS.size())
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testSendAndReceiveCollection(){
        Mono<List<String>> mono = rpcTestServiceProxy.modifyListOfStrings(RpcTestService.LIST_OF_STRINGS);
        StepVerifier.create(mono)
                    .expectNext(RpcTestService.LIST_OF_STRINGS.stream().map(s -> "Hello "+ s).collect(Collectors.toList()))
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testNestedArrays(){
        List<List<String>> input = Lists.newArrayList(RpcTestService.LIST_OF_STRINGS, RpcTestService.LIST_OF_STRINGS, RpcTestService.LIST_OF_STRINGS);
        Mono<List<List<String>>> mono = rpcTestServiceProxy.getAListOfLists(input);
        StepVerifier.create(mono)
                    .expectNext(input.stream().map(strings -> strings.stream().map(s -> "Hello "+ s).collect(Collectors.toList())).collect(Collectors.toList()))
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testVertxFuture(){
        Future<String> future  = rpcTestServiceProxy.getAnotherString();

        Awaitility.await().until(future::isComplete);

        if(future.failed()){
            throw new IllegalStateException("TestServiceProxy method invocation failed", future.cause());
        }else if(!future.result().equals(RpcTestService.STRING_VALUE)){
            throw new IllegalStateException("Service data returned does not match what was expected: "+RpcTestService.STRING_VALUE+" got: "+future.result());
        }
    }

    @Test
    public void testVertxFutureNullString(){
        Future<String> future  = rpcTestServiceProxy.getVertxFutureNullString();

        Awaitility.await().until(future::isComplete);

        if(future.failed()){
            throw new IllegalStateException("TestServiceProxy method invocation failed", future.cause());
        }else if(!(future.result() == null)){
            throw new IllegalStateException("Service data returned does not match what was expected: null got: "+future.result());
        }
    }

    @Test
    public void testSimpleObject(){
        Mono<Tuple2<SimpleObject, String>> mono = rpcTestServiceProxy.getSimpleObject()
                                               .zipWhen(simpleObject -> rpcTestServiceProxy.getSimpleObjectToString(simpleObject));

        StepVerifier.create(mono)
                    .expectNextMatches(tuple -> {
                        return tuple.getT1().toString().equals(tuple.getT2());
                    })
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testABunchOfArguments(){
        ABunchOfArgumentsHolder argumentsHolder = RpcTestService.BUNCH_OF_ARGUMENTS;
        Mono<ABunchOfArgumentsHolder> mono = rpcTestServiceProxy.acceptABunchOfArguments(argumentsHolder.getIntValue(),
                                                                          argumentsHolder.getLongValue(),
                                                                          argumentsHolder.getStringValue(),
                                                                          argumentsHolder.isBoolValue(),
                                                                          argumentsHolder.getSimpleObject(),
                                                                          RpcTestService.LIST_OF_STRINGS);

        StepVerifier.create(mono)
                    .expectNext(argumentsHolder)
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testUnknownFailure(){
        Mono<String> mono = rpcTestServiceProxy.getUnknownFailure();

        StepVerifier.create(mono).expectErrorMatches(throwable -> {
            boolean ret = false;
            // When the original exception class cannot be instantiated you wll get a RpcInvocationException
            if(throwable instanceof RpcInvocationException){
                ret = Objects.equals(((RpcInvocationException) throwable).getOriginalClassName(),
                                     "com.kinotic.continuum.internal.core.api.support.DefaultRpcTestService$UnknownThrowable");
            }
            return ret;
        }).verify();
    }

    @Test
    public void testLimitedFlux(){
        Flux<Integer> flux = rpcTestServiceProxy.getLimitedFlux();

        StepVerifier.create(flux)
                    .expectNext(1, 2, 3, 4, 5)
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testInfiniteFlux(){
        Flux<String> flux = rpcTestServiceProxy.getInfiniteFlux();

        StepVerifier.create(flux)
                    .expectNextMatches(s -> {
                        return s.startsWith("Hello Sucka");
                    })
                    .expectNextMatches(s -> {
                        return s.startsWith("Hello Sucka");
                    })
                    .expectNextMatches(s -> {
                        return s.startsWith("Hello Sucka");
                    })
                    .expectNextMatches(s -> {
                        return s.startsWith("Hello Sucka");
                    })
                    .thenCancel()
                    .verify();
    }

    @Test
    public void testMultipleRequests(){
        Mono<String> mono = rpcTestServiceProxy.getString();

        StepVerifier.create(mono).expectNext(RpcTestService.STRING_VALUE).expectComplete().verify();

        Mono<String> mono2 = rpcTestServiceProxy.getString();

        StepVerifier.create(mono2).expectNext(RpcTestService.STRING_VALUE).expectComplete().verify();

        Mono<String> mono3 = rpcTestServiceProxy.getString();

        StepVerifier.create(mono3).expectNext(RpcTestService.STRING_VALUE).expectComplete().verify();
    }


    @Test
    public void testMonoWithValue() {
        Mono<String> mono = rpcTestServiceProxy.getMonoWithValue();

        StepVerifier.create(mono)
                    .expectNext("Hello Bob")
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testMonoWithVoidFromEmpty() {
        Mono<Void> mono = rpcTestServiceProxy.getMonoWithVoidFromEmpty();
        StepVerifier.create(mono)
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testMonoWithVoidFromNull() {
        Mono<Void> mono = rpcTestServiceProxy.getMonoWithVoidFromNull();
        StepVerifier.create(mono)
                    .expectComplete()
                    .verify();
    }

    @Test
    public void testMonoStringNull() {
        AtomicBoolean hasNull = new AtomicBoolean();
        Mono<String> mono = rpcTestServiceProxy
                .getMonoStringNull()
                .doOnSuccess(v -> {
                    if (v == null) hasNull.set(true);
                });

        StepVerifier.create(mono)
                    .expectComplete()
                    .verify();

        Assertions.assertTrue(hasNull.get());
    }

    @Test
    public void testMonoIntegerNull() {
        AtomicBoolean hasNull = new AtomicBoolean();
        Mono<Integer> mono = rpcTestServiceProxy
                .getMonoIntegerNull()
                .doOnSuccess(v -> {
                    if (v == null) hasNull.set(true);
                });

        StepVerifier.create(mono)
                    .expectComplete()
                    .verify();

        Assertions.assertTrue(hasNull.get());
    }

    @Test
    public void testMissingRemoteMethodFailure() {
        Mono<String> mono = rpcTestServiceProxy.getMissingRemoteMethodFailure();

        StepVerifier.create(mono)
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    public void testMonoEmptyString() {
        Mono<String> mono = rpcTestServiceProxy.getMonoEmptyString();

        StepVerifier.create(mono)
                    .expectNext("")
                    .expectComplete()
                    .verify();
    }
    @Test
    public void testMonoStringLiterallyNull() {
        Mono<String> mono = rpcTestServiceProxy.getMonoStringLiterallyNull();

        StepVerifier.create(mono)
                    .expectNext("null")
                    .expectComplete()
                    .verify();
    }
}
