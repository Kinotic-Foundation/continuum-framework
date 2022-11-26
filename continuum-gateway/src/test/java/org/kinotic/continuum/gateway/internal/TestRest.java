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

package org.kinotic.continuum.gateway.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kinotic.continuum.gateway.api.config.ContinuumGatewayProperties;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * Created by navid on 12/23/19
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TestRest {

    @Autowired
    private ContinuumGatewayProperties properties;

    @Autowired
    private WebClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testServiceRequestTwoParams(){
        Promise<HttpResponse<Buffer>> response = Promise.promise();

        client.post(properties.getRest().getPort(), "localhost", "/api/srv/com.kinotic.continuum.gateway.internal.support.TestService/test")
              .basicAuthentication("dummy", "dummyPass")
              .putHeader("content-type", "application/json")
              .sendBuffer(Buffer.buffer("[\"Hello\",42]"), response);

        verifyResult(response, String.class, s -> Objects.equals(s, "Hello42"), "TestService.test method invocation failed");
    }

    @Test
    public void testServiceRequestSingleParams(){
        Promise<HttpResponse<Buffer>> response = Promise.promise();

        client.post(properties.getRest().getPort(), "localhost", "/api/srv/com.kinotic.continuum.gateway.internal.support.TestService/hello")
              .basicAuthentication("dummy", "dummyPass")
              .putHeader("content-type", "application/json")
              .sendBuffer(Buffer.buffer("[\"Bob\"]"), response);

        verifyResult(response, String.class, s -> Objects.equals(s, "Hello Bob"), "TestService.hello method invocation failed");
    }

    @Test
    public void testServiceRequestNoParams(){
        Promise<HttpResponse<Buffer>> response = Promise.promise();

        client.post(properties.getRest().getPort(), "localhost", "/api/srv/com.kinotic.continuum.gateway.internal.support.TestService/noArgs")
              .basicAuthentication("dummy", "dummyPass")
              .putHeader("content-type", "application/json")
              .send(response);

        verifyResult(response, String.class, s -> Objects.equals(s, "wat"), "TestService.noArgs method invocation failed");
    }

    @Test
    public void testMissingMethod(){
        Promise<HttpResponse<Buffer>> response = Promise.promise();

        client.post(properties.getRest().getPort(), "localhost", "/api/srv/com.kinotic.continuum.gateway.internal.support.TestService/wat")
              .basicAuthentication("dummy", "dummyPass")
              .putHeader("content-type", "application/json")
              .send(response);

        Awaitility.await().until(response.future()::isComplete);

        HttpResponse<Buffer> httpResponse = response.future().result();

        if(httpResponse.statusCode() != 500){
            throw new IllegalStateException("Exception was expected but got "+"\n HTTP Status: "+httpResponse.statusCode() + "\n HTTP Message: "+httpResponse.statusMessage());
        }
    }

    @Test
    public void testServiceRequestMonoSingleParams(){
        Promise<HttpResponse<Buffer>> response = Promise.promise();

        client.post(properties.getRest().getPort(), "localhost", "/api/srv/com.kinotic.continuum.gateway.internal.support.TestService/testMono")
              .basicAuthentication("dummy", "dummyPass")
              .putHeader("content-type", "application/json")
              .sendBuffer(Buffer.buffer("[\"Bob\"]"), response);

        verifyResult(response, String.class, s -> Objects.equals(s, "Hello Bob"), "TestService.testMono method invocation failed");
    }

    @Test
    public void testServiceRequestMonoNoParams(){
        Promise<HttpResponse<Buffer>> response = Promise.promise();

        client.post(properties.getRest().getPort(), "localhost", "/api/srv/com.kinotic.continuum.gateway.internal.support.TestService/testMonoNoArg")
              .basicAuthentication("dummy", "dummyPass")
              .putHeader("content-type", "application/json")
              .send(response);

        verifyResult(response, String.class, s -> Objects.equals(s, "hello"), "TestService.testMonoNoArg method invocation failed");
    }


    private <T> void verifyResult(Promise<HttpResponse<Buffer>> responsePromise, Class<T> clazz, Predicate<T> predicate, String message){
        Awaitility.await().until(responsePromise.future()::isComplete);
        if(responsePromise.future().failed()){
            throw new IllegalStateException(message, responsePromise.future().cause());
        }else{
            HttpResponse<Buffer> response = responsePromise.future().result();
            if(response.statusCode() == 200){
                try {
                    T value = objectMapper.readValue(response.body().getBytes(), clazz);
                    if(!predicate.test(value)){
                        throw new IllegalStateException(message+"\nResult: "+value+" did not match expected value");
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(message, e);
                }
            }else{
                throw new IllegalStateException(message +"\n HTTP Status: "+response.statusCode() + "\n HTTP Message: "+response.statusMessage());
            }
        }
    }

}
