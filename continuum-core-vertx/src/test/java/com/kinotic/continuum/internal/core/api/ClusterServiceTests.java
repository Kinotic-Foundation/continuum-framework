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

import com.kinotic.continuum.core.api.ClusterService;
import com.kinotic.continuum.internal.core.api.support.ClusterTestServiceProxy;
import com.kinotic.continuum.internal.core.api.support.DefaultClusterTestService;
import io.vertx.core.Future;
import org.apache.commons.lang3.Validate;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.loader.tools.RunProcess;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 *
 * Created by navid on 10/17/19
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClusterServiceTests {

    @Autowired
    private ClusterService clusterService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // these are not detected because continuum wires them..
    @Autowired
    private ClusterTestServiceProxy clusterTestServiceProxy;

    private RunProcess process;

    private static final String SERVICE_NAME = "TestService";
    private static final String SERVICE_DATA = "Hello Sucka!";

    public void init() throws Exception{
//        StringBuilder classPathStringBuilder = new StringBuilder();
//        List<URL> urls = ClassLoaderUtils.classPathFromClassLoader(Thread.currentThread().getContextClassLoader());
//        for(URL url : urls){
//            String path = url.toString();
//            // all paths begin with file:
//            if(classPathStringBuilder.length() > 0){
//                classPathStringBuilder.append(":");
//            }
//            classPathStringBuilder.append(path.substring(5));
//        }
//
//        String javaPath = System.getProperty("java.home")+"/bin/java";
//        String cliJarPath= Path.of("lib/spring-boot-cli-2.2.0.RELEASE.jar").toAbsolutePath().toString();
//        String appPath = Path.of("src/test/java/"+ ClassPathUtils.toFullyQualifiedPath(TestApplication.class, "TestApplication.groovy"))
//                             .toAbsolutePath()
//                             .toString();
//
//        process = new RunProcess(javaPath,
//                                 "-Dspring.profiles.active=development",
//                                 "-cp",
//                                 cliJarPath,
//                                 "org.springframework.boot.loader.JarLauncher",
//                                 "run",
//                                 "-cp",
//                                 classPathStringBuilder.toString(),
//                                 appPath);
//        process.run(false);
    }

    /**
     * Full integration test of publish, proxy test, un-deploy
     */
    @Test
    public void fullOnClusterServiceTest() throws Exception{
        Validate.notNull(clusterService, "Cluster service is null");

        /**
         * Deploy service
         */
        Mono<Void> deployMono = clusterService.deployClusterSingleton(SERVICE_NAME,
                                                                DefaultClusterTestService.class,
                                                                SERVICE_DATA);

        StepVerifier.create(deployMono).expectComplete().verify();

        /**
         * Ensure deployed
         */
        Mono<Boolean> isDeployedMono = clusterService.isServiceDeployed(SERVICE_NAME);

        StepVerifier.create(isDeployedMono).expectNext(true).expectComplete().verify();

        Thread.sleep(2000); // make sure ignite is done deploying service. It seems this takes a while

        /**
         * Service proxy getData() test
         */
        Validate.notNull(clusterTestServiceProxy, "TestServiceProxy is null and therefor was not autowired");

        Future<String> future = clusterTestServiceProxy.getData(); // data is set in test 1

        Awaitility.await().until(future::isComplete);

        if(future.failed()){
            throw new IllegalStateException("TestServiceProxy method invocation failed", future.cause());
        }else if(!future.result().equals(SERVICE_DATA)){
            throw new IllegalStateException("Service data returned does not match what was expected: "+SERVICE_DATA+" got: "+future.result());
        }

        /**
         * Service proxy getFreeMemory() test
         */
        Mono<Long> freeMemoryLong = clusterTestServiceProxy.getFreeMemory();

        StepVerifier.create(freeMemoryLong).expectNext(428L).expectComplete().verify();

        /**
         * Un-deploy service
         */
        Mono<Void> unDeployService = clusterService.unDeployService(SERVICE_NAME);

        StepVerifier.create(unDeployService).expectComplete().verify();
    }

    // TODO: after upgrade to ignite 2.9.1 this is still failing maybe something did not make it into this release
    // Discussions https://cwiki.apache.org/confluence/display/IGNITE/IEP-17%3A+Oil+Change+in+Service+Grid
    // Ignite Bugs not sure which one needs to be fixed
    // https://issues.apache.org/jira/browse/IGNITE-13299?jql=project%20%3D%20IGNITE%20AND%20component%20%3D%20%22managed%20services%22
    @Test
    public void testFailOnDuplicateServiceDeployment() throws Exception{
        /**
         * Deploy service
         */
        Mono<Void> deployMono = clusterService.deployClusterSingleton(SERVICE_NAME,
                                                                      DefaultClusterTestService.class,
                                                                      SERVICE_DATA);

        StepVerifier.create(deployMono)
                    .expectComplete()
                    .verify();

        /**
         * Ensure deployed
         */
        Mono<Boolean> isDeployedMono = clusterService.isServiceDeployed(SERVICE_NAME);

        StepVerifier.create(isDeployedMono)
                    .expectNext(true)
                    .as("Service is deployed")
                    .expectComplete()
                    .verify();

        Thread.sleep(2000); // make sure ignite is done deploying service. It seems this takes a while

        /**
         * Try to deploy same service again and expect failure
         */
        Mono<Void> secondaryDeployMono = clusterService.deployClusterSingleton(SERVICE_NAME,
                                                                      DefaultClusterTestService.class,
                                                                      SERVICE_DATA);

        StepVerifier.create(secondaryDeployMono)
                    .expectError(IllegalStateException.class)
                    .verify();
    }

}
