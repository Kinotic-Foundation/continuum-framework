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

package com.kinotic.continuum.iam

import com.kinotic.continuum.core.api.security.SecurityService
import com.kinotic.continuum.iam.api.AccessPolicyService
import com.kinotic.continuum.iam.api.DeviceService
import com.kinotic.continuum.iam.api.RoleService
import com.kinotic.continuum.iam.api.UserService
import com.kinotic.continuum.iam.api.domain.*
import com.kinotic.continuum.internal.util.SecurityUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.StepVerifier

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(["test"])
class CrudTests {

    private static String TEST_USER_IDENTITY = "navid@kinotic.com"
    private static String TEST_USER_PASSWORD = "a1843621"
    private static List<AccessPattern> ALLOWED_SEND_ACCESS_PATHS =
            Arrays.asList(new AccessPattern("srv://*"),
                          new AccessPattern("srv://com.kinotic.test.TestService/testMethod"))

    @Autowired
    private UserService userService

    @Autowired
    private DeviceService deviceService

    @Autowired
    private RoleService roleService

    @Autowired
    private AccessPolicyService accessPolicyService

    @Autowired
    private SecurityService securityService

    @Test
    void userCreationAndAuthentication() {

        /**
         * Create {@link AccessPolicy}, {@link Role}, and {@link IamParticipant}
         */
        Role role = null
        for (int i =0; i < 5; i++) {
            String accessPolicyId = UUID.randomUUID().toString()
            AccessPolicy accessPolicy = new AccessPolicy(accessPolicyId)
                                            .setDescription("Another Cool Access Policy")
                                            .setAllowedSendPatterns(ALLOWED_SEND_ACCESS_PATHS)

            AccessPolicy savedAccessPolicy = accessPolicyService.create(accessPolicy).block()


            String roleId = UUID.randomUUID().toString()
            role = new Role(roleId)
                            .setDescription("Another Cool Role")
                            .addAccessPolicy(savedAccessPolicy)

            StepVerifier.create(roleService.create(role))
                        .expectNextCount(1)
                        .expectComplete()
                        .verify()
        }

        StepVerifier.create(userService.createNewUser(TEST_USER_IDENTITY, TEST_USER_PASSWORD, Collections.singletonList(role)))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify()

        /**
         * make sure the user can authenticate
         */
        StepVerifier.create(securityService.authenticate(TEST_USER_IDENTITY, TEST_USER_PASSWORD))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify()

    }

    @Test
    void deviceCreationAndAuthentication(){
        for (int i = 10; i < 15; i++) {
            String accessPolicyId = UUID.randomUUID().toString()
            AccessPolicy accessPolicy = new AccessPolicy(accessPolicyId)
                    .setDescription("Another Cool Access Policy")
                    .setAllowedSendPatterns(ALLOWED_SEND_ACCESS_PATHS)

            AccessPolicy savedAccessPolicy = accessPolicyService.create(accessPolicy).block()

            String roleId = UUID.randomUUID().toString()
            Role role = new Role(roleId)
                    .setDescription("Another Cool Role")
                    .addAccessPolicy(savedAccessPolicy)

            StepVerifier.create(roleService.create(role))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify()

            String mac = "0103040000" + i
            IamParticipant device = new IamParticipant(mac)
            device.setRoles(Collections.singletonList(role))

            LegacySharedSecretAuthenticator authenticator = new LegacySharedSecretAuthenticator()
            authenticator.setAccessKey(UUID.randomUUID().toString())
            authenticator.setSharedSecret(SecurityUtil.generateSecretKey(256))
            device.addAuthenticator(authenticator)

            StepVerifier.create(deviceService.create(device))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify()
        }
    }

    @Test
    void testRegisterDefaultDevice(){
        for (int i = 10; i < 15; i++) {
            String mac = "0103050000" + i
            StepVerifier.create(deviceService.registerDeviceWithSharedSecretAuth(mac))
                    .expectNextCount(1)
                    .expectComplete()
                    .verify()
        }
    }

}
