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

package com.kinotic.continuum.iam.internal.init;

import com.kinotic.continuum.core.api.event.EventConstants;
import com.kinotic.continuum.iam.api.UserService;
import com.kinotic.continuum.iam.api.domain.AccessPattern;
import com.kinotic.continuum.iam.api.domain.AccessPolicy;
import com.kinotic.continuum.iam.api.domain.Role;
import com.kinotic.continuum.iam.internal.api.DomainConstants;
import com.kinotic.continuum.iam.internal.repositories.AccessPolicyRepository;
import com.kinotic.continuum.iam.internal.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * Created by Navid Mitchell on 2/12/20
 */
@Component
public class DefaultDataInitialization {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataInitialization.class);

    @Autowired
    private AccessPolicyRepository accessPolicyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;


    @PostConstruct
    public void init() {
        Optional<Role> iamAdminRole  = roleRepository.findById("iam-admin-role");
        if(iamAdminRole.isEmpty()){
            AccessPolicy accessPolicy = new AccessPolicy("iam-admin-policy");
            accessPolicy.setAllowedSendPatterns(Collections.singletonList(new AccessPattern(EventConstants.SERVICE_DESTINATION_SCHEME
                                                                                            + "://com.kinotic.continuum.iam.api.**")));
            accessPolicy = accessPolicyRepository.save(accessPolicy);

            Role role = new Role("iam-admin-role");
            role.addAccessPolicy(accessPolicy);
            role = roleRepository.save(role);

            userService.createNewUser("iam-admin","w3mak3th1sr0ck", List.of(role)).block();
            log.info("Added iam-admin user, roles, and policies");
        }else{
            log.info("iam-admin role exists");
        }

        Optional<Role> superRole  = roleRepository.findById("super-role");
        if(superRole.isEmpty()){
            AccessPolicy accessPolicy = new AccessPolicy("super-policy");
            accessPolicy.setAllowedSendPatterns(List.of(new AccessPattern(EventConstants.SERVICE_DESTINATION_SCHEME + "://*.**"),
                                                        new AccessPattern(EventConstants.STREAM_DESTINATION_SCHEME + "://*.**")));

            accessPolicy.setAllowedSubscriptionPatterns(List.of(new AccessPattern(EventConstants.SERVICE_DESTINATION_SCHEME + "://*.**"),
                                                                new AccessPattern(EventConstants.STREAM_DESTINATION_SCHEME + "://*.**")));

            accessPolicy = accessPolicyRepository.save(accessPolicy);

            Role role = new Role("super-role");
            role.addAccessPolicy(accessPolicy);
            role = roleRepository.save(role);

            userService.createNewUser("super","w3mak3th1sr0ck1nr0ll", List.of(role)).block();
            log.info("Added super user, roles, and policies");
        }else{
            log.info("super-role role exists");
        }

        Optional<Role> deviceRegistrationRole  = roleRepository.findById(DomainConstants.DEVICE_REGISTRATION_ROLE_ID);
        if(deviceRegistrationRole.isEmpty()){
            AccessPolicy accessPolicy = new AccessPolicy("device-registration-policy");
            accessPolicy.setAllowedSendPatterns(List.of(new AccessPattern(EventConstants.SERVICE_DESTINATION_SCHEME
                                                                          + "://com.kinotic.continuum.iam.api.DeviceService/registerDevice*")));

            // for registration response since registration service delegates subscriptions..
            accessPolicy.setAllowedSubscriptionPatterns(List.of(new AccessPattern(EventConstants.SERVICE_DESTINATION_SCHEME
                                                                                  + "://*@continuum.cpp.RegistrationRequest/replyHandler")));
            accessPolicy = accessPolicyRepository.save(accessPolicy);

            Role role = new Role(DomainConstants.DEVICE_REGISTRATION_ROLE_ID);
            role.addAccessPolicy(accessPolicy);
            role = roleRepository.save(role);

            log.info("Added device-registration-policy");

        }else{
            log.info("device-registration-role role exists");
        }

        Optional<Role> defaultDeviceRole  = roleRepository.findById(DomainConstants.DEFAULT_DEVICE_ROLE_ID);
        if(defaultDeviceRole.isEmpty()){
            AccessPolicy accessPolicy = new AccessPolicy("default-device-policy");
            accessPolicyRepository.save(accessPolicy);

            Role role = new Role(DomainConstants.DEFAULT_DEVICE_ROLE_ID);
            role.addAccessPolicy(accessPolicy);

            roleRepository.save(role);

            log.info("Added default-device role and policy");
        }else{
            log.info("default-device-role role exists");
        }

    }




}
