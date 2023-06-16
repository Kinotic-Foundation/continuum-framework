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

package org.kinotic.continuum.gateway.internal.endpoints.mqtt;

import io.vertx.core.AbstractVerticle;

/**
 *
 * Created by Navid Mitchell on 11/2/20
 */
//@Component
public class MqttServerVerticle extends AbstractVerticle {

//    private static final Logger log = LoggerFactory.getLogger(MqttServerVerticle.class);
//
//    @Autowired
//    private ContinuumGatewayProperties gatewayProperties;
//
//    @Autowired
//    private Services services;
//
//    private MqttServer mqttServer;
//
//    @Override
//    public void start(Future<Void> startFuture) {
//        mqttServer = MqttServer.create(vertx, gatewayProperties.getMqtt());
//        mqttServer.endpointHandler(endpoint -> {
//
//            MqttClientConnection clientConnection = new MqttClientConnection(endpoint);
//            EndpointConnectionHandler endpointConnectionHandler = new EndpointConnectionHandler(services);
//            MqttHandler mqttHandler = new MqttHandler(endpointConnectionHandler, clientConnection);
//
//            endpoint.subscriptionAutoAck(false);
//            endpoint.publishAutoAck(false);
//            endpoint.autoKeepAlive(true);
//
//            String identity = endpoint.auth().getUsername();
//            String secret = endpoint.auth().getPassword();
//
//            mqttHandler.authenticate(identity, secret)
//                       .future()
//                       .setHandler(event -> {
//                           if(event.succeeded()){
//
//                               endpoint.accept();
//
//                               endpoint.publishHandler(mqttHandler::publish);
//
//                               endpoint.publishAcknowledgeHandler(mqttHandler::publishAcknowledge);
//
//                               endpoint.publishReceivedHandler(mqttHandler::publishReceived);
//
//                               endpoint.publishCompletionHandler(mqttHandler::publishCompletion);
//
//                               endpoint.subscribeHandler(mqttHandler::subscribe);
//
//                               endpoint.unsubscribeHandler(mqttHandler::unsubscribe);
//
//                               endpoint.exceptionHandler(mqttHandler::exception);
//
//                               endpoint.closeHandler(v -> mqttHandler.close());
//
//                           }else{
//                               endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
//                           }
//                       });
//
//        }).exceptionHandler(event -> log.error("MQTT server Exception before completing Client Connection", event))
//          .listen( ar -> {
//              if (ar.succeeded()) {
//                  log.info("MQTT Server Listening on port "+ ar.result().actualPort());
//              } else {
//                  log.error("Error starting MQTT Server", ar.cause());
//                  System.out.println("Error on starting the server");
//              }
//          });
//    }
//
//    @Override
//    public void stop(Future<Void> stopFuture) {
//        if(mqttServer != null){
//            mqttServer.close();
//        }
//    }
}
