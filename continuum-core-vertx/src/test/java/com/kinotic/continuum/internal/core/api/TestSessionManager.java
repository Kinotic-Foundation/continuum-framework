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

import com.kinotic.continuum.core.api.event.CRI;
import com.kinotic.continuum.core.api.event.EventConstants;
import com.kinotic.continuum.core.api.security.Participant;
import com.kinotic.continuum.core.api.security.Permissions;
import com.kinotic.continuum.core.api.security.Session;
import com.kinotic.continuum.core.api.security.SessionManager;
import com.kinotic.continuum.internal.core.api.security.DefaultParticipant;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

/**
 *
 * Created by navid on 1/23/20
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class TestSessionManager {

    private static final Logger log = LoggerFactory.getLogger(TestSessionManager.class);

    private static final String IDENTITY = "testuser@kinotic.com";

    @Autowired
    private SessionManager sessionManager;

    @Test
    public void testCreateFindRemove(){
        Permissions permissions = new Permissions();
        permissions.addAllowedSendPattern(EventConstants.SERVICE_DESTINATION_SCHEME+"://*.**");
        Participant participant = new DefaultParticipant(IDENTITY, permissions);

        String sessionId = UUID.randomUUID().toString();
        Session session = sessionManager.create(sessionId, participant).block();

        StepVerifier.create(sessionManager.findSession(sessionId))
                    .expectNext(session)
                    .verifyComplete();

        StepVerifier.create(sessionManager.removeSession(sessionId))
                    .expectNext(true)
                    .verifyComplete();

        StepVerifier.create(sessionManager.findSession(sessionId))
                    .verifyError();

    }

    @Test
    public void testAllowAny(){
        Permissions permissions = new Permissions();
        permissions.addAllowedSendPattern(EventConstants.SERVICE_DESTINATION_SCHEME+"://*.**");
        Participant participant = new DefaultParticipant(IDENTITY, permissions);
        /**
         * Create Session
         */
        Session session = sessionManager.create(UUID.randomUUID().toString(), participant).block();

        /**
         * Now test patterns
         */
        CRI cri = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewhere.tests.TestService");
        CRI cri2 = CRI.create(cri + "/testMethod");

        Validate.isTrue(session.sendAllowed(cri), cri + " Not allowed");
        Validate.isTrue(session.sendAllowed(cri2), cri2 + " Not allowed");
    }

    @Test
    public void testWildcardPathPatterns(){
        /**
         * Setup Permissions
         */
        Permissions permissions = new Permissions();
        permissions.addAllowedSendPattern(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewhere.**");
        Participant participant = new DefaultParticipant(IDENTITY, permissions);
        /**
         * Create Session
         */
        Session session = sessionManager.create(UUID.randomUUID().toString(), participant).block();

        /**
         * Now test patterns
         */
        CRI cri = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewhere.tests.TestService");
        CRI cri2 = CRI.create(cri + "/testMethod");

        Validate.isTrue(session.sendAllowed(cri), cri + " Not allowed");
        Validate.isTrue(session.sendAllowed(cri2), cri2 + " Not allowed");

        CRI invalidCRI = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewher.tests.TestService");
        CRI invalidCRI2 = CRI.create(invalidCRI + "/testMethod");

        Validate.isTrue(!session.sendAllowed(invalidCRI), invalidCRI + " Allowed");
        Validate.isTrue(!session.sendAllowed(invalidCRI2), invalidCRI2 + " Allowed");
    }

    @Test
    public void testManyWildcardPathPatterns(){
        /**
         * Setup Permissions
         */
        Permissions permissions = new Permissions();
        // try adding a bunch of unrelated paths that should not match first to test performance
        permissions.addAllowedSendPattern(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewhere.tests.**");
        String baseString = EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewhere.test.TestService";
        for(int i = 0; i < 1000; i++){
            permissions.addAllowedSendPattern(baseString + i);
        }


        Participant participant = new DefaultParticipant(IDENTITY, permissions);
        /**
         * Create Session
         */
        Session session = sessionManager.create(UUID.randomUUID().toString(), participant).block();


        /**
         * Now test patterns
         */
        CRI cri = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewhere.tests.TestService");
        CRI cri2 = CRI.create(cri + "/testMethod");

        CRI invalidCRI = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.somewher.tests.TestService");
        CRI invalidCRI2 = CRI.create(invalidCRI + "/testMethod");


        StopWatch stopWatch = StopWatch.createStarted();

        Validate.isTrue(session.sendAllowed(cri), cri + " Not allowed");
        Validate.isTrue(session.sendAllowed(cri2), cri2 + " Not allowed");

        Validate.isTrue(!session.sendAllowed(invalidCRI), invalidCRI + " Allowed");
        Validate.isTrue(!session.sendAllowed(invalidCRI2), invalidCRI2 + " Allowed");

        stopWatch.stop();
        log.info("** Results - Took "+stopWatch.getTime() + " millis");

    }


    @Test
    public void testManySpecificPathPatterns(){
        /**
         * Setup Permissions
         */
        Permissions permissions = new Permissions();

        String baseString = EventConstants.SERVICE_DESTINATION_SCHEME+"://com.kinotic.tests.TestService";
        for(int i = 0; i < 1000; i++){
            permissions.addAllowedSendPattern(baseString + i + "/*");
        }

        permissions.addAllowedSendPattern(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.kinotic.tests.TestService/*");
        Participant participant = new DefaultParticipant(IDENTITY, permissions);
        /**
         * Create Session
         */
        Session session = sessionManager.create(UUID.randomUUID().toString(), participant).block();

        /**
         * Now test patterns
         */
        CRI cri = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.kinotic.tests.TestService");
        CRI cri2 = CRI.create(cri + "/testMethod");
        CRI cri3 = CRI.create(cri + "/getFreeMemory");

        CRI invalidCRI = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.kinotic.tests");
        CRI invalidCRI2 = CRI.create(invalidCRI + ".**");
        CRI invalidCRI3 = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.kinotic.tests.SomeService/testMethod");

        StopWatch stopWatch = StopWatch.createStarted();

        CRI validFromBigList = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME+"://com.kinotic.tests.TestService10/testMethod");

        Validate.isTrue(session.sendAllowed(validFromBigList), validFromBigList + " Not allowed");

        Validate.isTrue(!session.sendAllowed(cri), cri + " Allowed");
        Validate.isTrue(session.sendAllowed(cri2), cri2 + " Not allowed");
        Validate.isTrue(session.sendAllowed(cri3), cri3 + " Not allowed");

        Validate.isTrue(!session.sendAllowed(invalidCRI), invalidCRI + " Allowed");
        Validate.isTrue(!session.sendAllowed(invalidCRI2), invalidCRI2 + " Allowed");
        Validate.isTrue(!session.sendAllowed(invalidCRI3), invalidCRI3 + " Allowed");
        stopWatch.stop();
        log.info("** Results - Took "+stopWatch.getTime() + " millis");
    }

    @Test
    public void testSubscriptions(){
        /**
         * Setup Permissions
         */
        Permissions permissions = new Permissions();
        permissions.addAllowedSubscriptionPattern(EventConstants.SERVICE_DESTINATION_SCHEME + "://" + IDENTITY + "*@*.**");
        permissions.addAllowedSubscriptionPattern(EventConstants.SERVICE_DESTINATION_SCHEME + "://" + IDENTITY + "*@*.**");

        Participant participant = new DefaultParticipant(IDENTITY, permissions);
        /**
         * Create Session
         */
        Session session = sessionManager.create(UUID.randomUUID().toString(), participant).block();

        /**
         * Now test Allowed Sub patterns
         */
        CRI cri = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME + "://" + IDENTITY + "@com.kinotic.tests.ReplyHandler");

        Validate.isTrue(session.subscribeAllowed(cri), cri + " Not Allowed");

        CRI criWithParticipantUUID = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME + "://"
                + IDENTITY + ":"
                + UUID.randomUUID()
                + "@com.kinotic.tests.ReplyHandler");

        Validate.isTrue(session.subscribeAllowed(criWithParticipantUUID), criWithParticipantUUID + " Not Allowed");


        /**
         * Now test Not Allowed Sub patterns
         */
        CRI invalidCRI = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME + "://" + "somegal." + IDENTITY + "@com.kinotic.tests.ReplyHandler");

        Validate.isTrue(!session.subscribeAllowed(invalidCRI), invalidCRI + " Allowed");

        CRI invalidCRIWithParticipantUUID = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME + "://"
                + "someguy_" + IDENTITY + ":"
                + UUID.randomUUID().toString()
                + "@com.kinotic.tests.ReplyHandler");

        Validate.isTrue(!session.subscribeAllowed(invalidCRIWithParticipantUUID), invalidCRIWithParticipantUUID + " Allowed");

        CRI anotherInvalid = CRI.create(EventConstants.SERVICE_DESTINATION_SCHEME + "://" + "somebody@kinotic.com" + "@com.kinotic.tests.ReplyHandler");

        Validate.isTrue(!session.subscribeAllowed(anotherInvalid), anotherInvalid + " Allowed");
    }

    @Test
    public void testPartialMethodMatch(){
        String baseServiceURI = EventConstants.SERVICE_DESTINATION_SCHEME+"://com.kinotic.tests.SomeService/register";
        Permissions permissions = new Permissions();
        permissions.addAllowedSendPattern(baseServiceURI+"*");
        Participant participant = new DefaultParticipant(IDENTITY, permissions);
        Session session = sessionManager.create(UUID.randomUUID().toString(), participant).block();

        Validate.isTrue(session.sendAllowed(CRI.create(baseServiceURI+"Cats")), "Valid pattern not allowed");
        Validate.isTrue(session.sendAllowed(CRI.create(baseServiceURI+"Dogs")), "Valid pattern not allowed");
        Validate.isTrue(session.sendAllowed(CRI.create(baseServiceURI+"Anything")), "Valid pattern not allowed");
        Validate.isTrue(!session.sendAllowed(CRI.create(baseServiceURI.substring(0, baseServiceURI.length() - 1)+"Anything")), "In-Valid pattern allowed");
    }


}
