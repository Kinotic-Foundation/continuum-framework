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

import org.kinotic.continuum.core.api.security.SecurityService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * Created by Navid Mitchell on 3/12/20
 */
@Component
public class ContinuumGatewayRestAuthProvider implements AuthProvider {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // not detected because created by BeanFactory
    @Autowired
    private SecurityService securityService;

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String accessKey = authInfo.getString("username");
        String secretKey = authInfo.getString("password");

        securityService.authenticate(accessKey, secretKey)
                       .subscribe(participant -> {
                           resultHandler.handle(Future.succeededFuture(new ParticipantToUserAdapter(participant)));
                       }, throwable -> {
                           resultHandler.handle(Future.failedFuture(throwable));
                       });
    }

}
