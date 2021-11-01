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

package com.kinotic.continuum.kafkaconnect.internal.api;

import com.kinotic.continuum.api.config.ContinuumProperties;
import com.kinotic.continuum.kafkaconnect.api.KafkaConnectService;
import com.kinotic.continuum.kafkaconnect.api.config.KafkaConnectProperties;
import org.apache.kafka.common.config.provider.ConfigProvider;
import org.apache.kafka.common.utils.Exit;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.connector.policy.ConnectorClientConfigOverridePolicy;
import org.apache.kafka.connect.runtime.*;
import org.apache.kafka.connect.runtime.distributed.DistributedConfig;
import org.apache.kafka.connect.runtime.distributed.DistributedHerder;
import org.apache.kafka.connect.runtime.isolation.PluginDesc;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.runtime.rest.RestServer;
import org.apache.kafka.connect.runtime.rest.entities.*;
import org.apache.kafka.connect.storage.*;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.predicates.Predicate;
import org.apache.kafka.connect.util.Callback;
import org.apache.kafka.connect.util.ConnectUtils;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.*;

/**
 *
 * Created by Navid Mitchell 🤬 on 1/6/21
 */
@Component
public class DefaultKafkaConnectService implements KafkaConnectService {

    private static final Logger log = LoggerFactory.getLogger(DefaultKafkaConnectService.class);

    private final Time time = Time.SYSTEM;
    private final long initStart = time.hiResClockMs();

    private Herder delegate;
    private Connect connect;

    private final ContinuumProperties continuumProperties;
    private final KafkaConnectProperties kafkaConnectProperties;

    public DefaultKafkaConnectService(ContinuumProperties continuumProperties,
                                      KafkaConnectProperties kafkaConnectProperties) {
        this.continuumProperties = continuumProperties;
        this.kafkaConnectProperties = kafkaConnectProperties;
    }

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        WorkerInfo initInfo = new WorkerInfo();
        initInfo.logAll();

        Map<String, String> workerProps = new HashMap<>();
        workerProps.put("bootstrap.servers", continuumProperties.getKafkaBootstrapServers());
        workerProps.putAll(kafkaConnectProperties.getWorkerProperties());

        connect = startConnect(workerProps);
    }

    @PreDestroy
    public void shutdown(){
        if(connect != null){
            connect.stop();
        }
    }

    public Connect startConnect(Map<String, String> workerProps) {
        log.info("Scanning for plugin classes. This might take a moment ...");
        Plugins plugins = new Plugins(workerProps);
        plugins.compareAndSwapWithDelegatingLoader();
        DistributedConfig config = new DistributedConfig(workerProps);

        String kafkaClusterId = ConnectUtils.lookupKafkaClusterId(config);
        log.debug("Kafka cluster ID: {}", kafkaClusterId);

        RestServer rest = new RestServer(config);
        rest.initializeServer();

        URI advertisedUrl = rest.advertisedUrl();
        String workerId = advertisedUrl.getHost() + ":" + advertisedUrl.getPort();

        KafkaOffsetBackingStore offsetBackingStore = new KafkaOffsetBackingStore();
        offsetBackingStore.configure(config);

        ConnectorClientConfigOverridePolicy connectorClientConfigOverridePolicy = plugins.newPlugin(
                config.getString(WorkerConfig.CONNECTOR_CLIENT_POLICY_CLASS_CONFIG),
                config, ConnectorClientConfigOverridePolicy.class);

        Worker worker = new Worker(workerId, time, plugins, config, offsetBackingStore, connectorClientConfigOverridePolicy);
        WorkerConfigTransformer configTransformer = worker.configTransformer();

        Converter internalValueConverter = worker.getInternalValueConverter();
        StatusBackingStore statusBackingStore = new KafkaStatusBackingStore(time, internalValueConverter);
        statusBackingStore.configure(config);

        ConfigBackingStore configBackingStore = new KafkaConfigBackingStore(
                internalValueConverter,
                config,
                configTransformer);

        delegate = new DistributedHerder(config, time, worker,
                                                         kafkaClusterId, statusBackingStore, configBackingStore,
                                                         advertisedUrl.toString(), connectorClientConfigOverridePolicy);

        final Connect connect = new Connect(delegate, rest);
        log.info("Kafka Connect distributed worker initialization took {}ms", time.hiResClockMs() - initStart);
        try {
            connect.start();
        } catch (Exception e) {
            log.error("Failed to start Connect", e);
            connect.stop();
            Exit.exit(3);
        }

        return connect;
    }


    @Override
    public Mono<Collection<String>> connectors() {
        return Mono.create(sink -> delegate.connectors(callbackForSink(sink)));

    }

    @Override
    public Mono<ConnectorInfo> connectorInfo(String connName) {
        return Mono.create(sink -> delegate.connectorInfo(connName, callbackForSink(sink)));
    }

    @Override
    public Mono<Map<String, String>> connectorConfig(String connName) {
        return Mono.create(sink -> delegate.connectorConfig(connName, callbackForSink(sink)));
    }

    @Override
    public Mono<Void> putConnectorConfig(String connName, Map<String, String> config, boolean allowReplace) {
        return Mono.create(sink -> delegate.putConnectorConfig(connName, config, allowReplace, (error, result) -> {
            if(error == null){
                sink.success();
            }else{
                sink.error(error);
            }
        }));
    }

    @Override
    public Mono<Void> deleteConnectorConfig(String connName) {
        return Mono.create(sink -> delegate.deleteConnectorConfig(connName, (error, result) -> {
            if(error == null){
                sink.success();
            }else{
                sink.error(error);
            }
        }));
    }

    @Override
    public Mono<List<TaskInfo>> tasks(String connName) {
        return Mono.create(sink -> delegate.taskConfigs(connName, callbackForSink(sink)));

    }

    @Override
    public ConnectorStateInfo connectorStatus(String connName) {
        return delegate.connectorStatus(connName);
    }

    @Override
    public ActiveTopicsInfo connectorActiveTopics(String connName) {
        return delegate.connectorActiveTopics(connName);
    }

    @Override
    public void resetConnectorActiveTopics(String connName) {
        delegate.resetConnectorActiveTopics(connName);
    }

    @Override
    public ConnectorStateInfo.TaskState taskStatus(ConnectorTaskId id) {
        return delegate.taskStatus(id);
    }

    @Override
    public Collection<TaskStatus> taskStatuses(String connector) {
        return delegate.statusBackingStore().getAll(connector);
    }

    @Override
    public Mono<ConfigInfos> validateConnectorConfig(Map<String, String> connectorConfig) {
        return Mono.create(sink -> delegate.validateConnectorConfig(connectorConfig, callbackForSink(sink)));
    }

    @Override
    public Mono<Void> restartTask(ConnectorTaskId id) {
        return Mono.create(sink -> delegate.restartTask(id, callbackForSink(sink)));
    }

    @Override
    public Mono<Void> restartConnector(String connName) {
        return Mono.create(sink -> delegate.restartConnector(connName, callbackForSink(sink)));
    }

    @Override
    public Mono<Void> restartConnectorLater(String connName, long delayMs) {
        return Mono.create(sink -> {
            HerderRequest request = delegate.restartConnector(delayMs, connName, callbackForSink(sink));
            sink.onDispose(request::cancel);
        });
    }

    @Override
    public void pauseConnector(String connector) {
        delegate.pauseConnector(connector);
    }

    @Override
    public void resumeConnector(String connector) {
        delegate.resumeConnector(connector);
    }

    @Override
    public Set<PluginDesc<Connector>> connectorPlugins() {
        return delegate.plugins().connectors();
    }

    @Override
    public Set<PluginDesc<Converter>> converterPlugins() {
        return delegate.plugins().converters();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Set<PluginDesc<Transformation>> transformationPlugins() {
        return delegate.plugins().transformations();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Set<PluginDesc<Predicate>> predicatePlugins() {
        return delegate.plugins().predicates();
    }

    @Override
    public Set<PluginDesc<ConfigProvider>> configProviderPlugins() {
        return delegate.plugins().configProviders();
    }

    @Override
    public String kafkaClusterId() {
        return delegate.kafkaClusterId();
    }

    private <T> Callback<T> callbackForSink(MonoSink<T> sink){
        return (error, result) -> {
            if(error == null){
                sink.success(result);
            }else{
                sink.error(error);
            }
        };
    }

}
