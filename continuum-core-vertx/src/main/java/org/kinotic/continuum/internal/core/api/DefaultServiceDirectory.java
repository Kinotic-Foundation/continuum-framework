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

package org.kinotic.continuum.internal.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.*;
import org.kinotic.continuum.core.api.ServiceDirectory;
import org.kinotic.continuum.core.api.service.ServiceDescriptor;
import org.kinotic.continuum.core.api.service.ServiceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This implementation relies on vertx {@link io.vertx.core.shareddata.SharedData} structures to store the directory
 * and manage multiple applications using it concurrently.
 * The following describes the data structures used and there purpose.
 *
 *
 *
 *
 *
 *
 * Created by navid on 2019-06-11.
 */
//@Component
public class DefaultServiceDirectory implements ServiceDirectory {

    private static final Logger log = LoggerFactory.getLogger(DefaultServiceDirectory.class);

    private static final String CONTINUUM_SERVICE_DIRECTORY_KEY = "ContinuumServiceDirectory";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Vertx vertx;


    private ConcurrentLinkedQueue<DirectoryChangeRequest> directoryChangeRequests = new ConcurrentLinkedQueue<>();
    private String deploymentId = null;

    @Override
    public void register(ServiceDescriptor serviceDescriptor) {

    }

    @Override
    public void unregister(ServiceIdentifier serviceIdentifier) {

    }

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent applicationReadyEvent) {
        // we wait till the application is completely started to begin publishing directory entries to storage
        vertx.deployVerticle(new DirectoryWorkerVerticle(), deployEvent -> {
            if(deployEvent.succeeded()){
                deploymentId = deployEvent.result();
            }else{
                log.error("Could not start DirectoryWorkerVerticle", deployEvent.cause());
            }
        });
    }

    @EventListener
    public void onContextClosedEvent(ContextClosedEvent contextClosedEvent){
        if(deploymentId != null){
            vertx.undeploy(deploymentId, undeployEvent -> {
                if(undeployEvent.failed()){
                    log.error("Could not stop DirectoryWorkerVerticle", undeployEvent.cause());
                }
            });
        }
    }

    private class DirectoryWorkerVerticle extends AbstractVerticle implements Handler<Void> {
        private final AtomicBoolean stopped = new AtomicBoolean(false);
        private final Future<Void> finished = Future.future();
        private Context creatingContext;

        @Override
        public void start(Future<Void> startFuture) throws Exception {
            // Setup control structures used by this directory
            this.creatingContext = getVertx().getOrCreateContext();

            doLoop();
            startFuture.complete();
        }

        @Override
        public void stop(Future<Void> stopFuture) throws Exception {
            stopped.compareAndSet(false, true);
            finished.setHandler(stopFuture);
        }

        @Override
        public void handle(Void event) {

        }

        private void doLoop() {
            creatingContext.runOnContext(this);
        }
    }

    private class DirectoryChangeRequest {
        private boolean publish = true;
        private String serviceIdentifier;
        private Class<?> serviceInterface;

        public DirectoryChangeRequest(String serviceIdentifier, Class<?> serviceInterface) {
            this.serviceIdentifier = serviceIdentifier;
            this.serviceInterface = serviceInterface;
        }

        public DirectoryChangeRequest(String serviceIdentifier) {
            this.serviceIdentifier = serviceIdentifier;
            this.publish = false;
        }

        public String getServiceIdentifier() {
            return serviceIdentifier;
        }

        public Class<?> getServiceInterface() {
            return serviceInterface;
        }

        public boolean isPublish() {
            return publish;
        }
    }

}
