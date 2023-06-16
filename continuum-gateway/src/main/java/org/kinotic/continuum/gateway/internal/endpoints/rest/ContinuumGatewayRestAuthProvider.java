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

package org.kinotic.continuum.gateway.internal.endpoints.rest;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import org.kinotic.continuum.core.api.security.Participant;
import org.kinotic.continuum.core.api.security.SecurityService;
import org.kinotic.continuum.internal.utils.VertxUtils;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 *
 * Created by Navid Mitchell on 3/12/20
 */
@Component
public class ContinuumGatewayRestAuthProvider implements AuthProvider {

    private final SecurityService securityService;

    public ContinuumGatewayRestAuthProvider(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        securityService.authenticate(VertxUtils.jsonObjectToFlatMap(authInfo))
                .handle((BiFunction<Participant, Throwable, Void>) (participant, throwable) -> {
                    if(throwable == null) {
                        resultHandler.handle(Future.succeededFuture(new ParticipantToUserAdapter(participant)));
                    } else {
                        resultHandler.handle(Future.failedFuture(throwable));
                    }
                    return null;
                });
    }

}
