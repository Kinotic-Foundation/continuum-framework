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

package org.kinotic.continuum.internal.core.api.service.rpc.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.EventConstants;
import org.kinotic.continuum.internal.core.api.service.rpc.RpcRequest;
import org.kinotic.continuum.internal.core.api.service.rpc.RpcResponseConverter;
import org.kinotic.continuum.internal.core.api.service.rpc.RpcReturnValueHandler;
import org.kinotic.continuum.internal.utils.EventUtil;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;

/**
 * Return value handler that provides a {@link Future}
 *
 * Created by navid on 2019-04-25.
 */
public class VertxFutureRpcReturnValueHandler implements RpcReturnValueHandler {

    private static final Logger log = LoggerFactory.getLogger(VertxFutureRpcReturnValueHandler.class);

    private final MethodParameter methodParameter;
    private final RpcResponseConverter rpcResponseConverter;
    private final ObjectMapper objectMapper;
    private final Future<Object> returnValue;

    public VertxFutureRpcReturnValueHandler(MethodParameter methodParameter,
                                            RpcResponseConverter rpcResponseConverter,
                                            ObjectMapper objectMapper) {

        Assert.notNull(methodParameter, "methodParameter must not be null");
        Assert.notNull(rpcResponseConverter, "responseConverter must not be null");
        Assert.notNull(objectMapper, "objectMapper must not be null");

        this.methodParameter = methodParameter;
        this.rpcResponseConverter = rpcResponseConverter;
        this.returnValue = Future.future();
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean processResponse(Event<byte[]> incomingEvent) {
        try{
            // Error data is returned differently
            if(incomingEvent.metadata().contains(EventConstants.ERROR_HEADER)) {
                returnValue.fail(EventUtil.createThrowableForEventWithError(incomingEvent, objectMapper));
            }else{
                returnValue.complete(rpcResponseConverter.convert(incomingEvent, methodParameter));
            }
        }catch (Exception e){
            log.error("Error converting the incoming message to expected java type", e);
            returnValue.fail(e);
        }
        return true;
    }

    @Override
    public boolean isMultiValue() {
        return false;
    }

    @Override
    public Object getReturnValue(RpcRequest rpcRequest) {
        rpcRequest.send();
        return returnValue;
    }

    @Override
    public void processError(Throwable throwable) {
        returnValue.fail(throwable);
    }

    @Override
    public void cancel(String message) {
        returnValue.fail(message);
    }

}
