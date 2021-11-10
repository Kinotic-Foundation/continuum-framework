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

package com.kinotic.continuum.kafkaconnect.api;

import com.kinotic.continuum.api.annotations.Publish;
import org.apache.kafka.common.config.provider.ConfigProvider;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.runtime.TaskStatus;
import org.apache.kafka.connect.runtime.isolation.PluginDesc;
import org.apache.kafka.connect.runtime.rest.entities.*;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.predicates.Predicate;
import org.apache.kafka.connect.util.ConnectorTaskId;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by Navid Mitchell 🤬 on 1/8/21
 */
@Publish(version = "0.1.0")
public interface KafkaConnectService {

    /**
     * Get a list of connectors currently running in this cluster. This is a full list of connectors in the cluster gathered
     * from the current configuration.
     *
     * @throws org.apache.kafka.connect.runtime.distributed.RequestTargetException if this node can not resolve the request
     *                                                                             (e.g., because it has not joined the cluster or does not have configs in sync with the group) and it is
     *                                                                             not the leader or the task owner (e.g., task restart must be handled by the worker which owns the task)
     * @throws org.apache.kafka.connect.errors.ConnectException                    if this node is the leader, but still cannot resolve the
     *                                                                             request (e.g., it is not in sync with other worker's config state)
     * @returns A list of connector names
     */
    Mono<Collection<String>> connectors();

    /**
     * Get the definition and status of a connector.
     */
    Mono<ConnectorInfo> connectorInfo(String connName);

    /**
     * Get the configuration for a connector.
     *
     * @param connName name of the connector
     */
    Mono<Map<String, String>> connectorConfig(String connName);

    /**
     * Set the configuration for a connector. This supports creation and updating.
     *
     * @param connName     name of the connector
     * @param config       the connectors configuration, or null if deleting the connector
     * @param allowReplace if true, allow overwriting previous configs; if false, throw AlreadyExistsException if a connector
     *                     with the same name already exists
     */
    Mono<Void> putConnectorConfig(String connName, Map<String, String> config, boolean allowReplace);

    /**
     * Delete a connector and its configuration.
     *
     * @param connName name of the connector
     */
    Mono<Void> deleteConnectorConfig(String connName);

    /**
     * Lookup the current status of a connector.
     *
     * @param connName name of the connector
     */
    ConnectorStateInfo connectorStatus(String connName);

    /**
     * Lookup the set of topics currently used by a connector.
     *
     * @param connName name of the connector
     * @return the set of active topics
     */
    ActiveTopicsInfo connectorActiveTopics(String connName);

    /**
     * Request to asynchronously reset the active topics for the named connector.
     *
     * @param connName name of the connector
     */
    void resetConnectorActiveTopics(String connName);

    /**
     * Get the configurations for the current set of tasks of a connector.
     *
     * @param connName connector to update
     */
    Mono<List<TaskInfo>> tasks(String connName);

    /**
     * Lookup the status of the a task.
     *
     * @param id id of the task
     */
    ConnectorStateInfo.TaskState taskStatus(ConnectorTaskId id);

    /**
     * Get the states of all tasks for the given connector.
     * @param connector the connector name
     * @return all {@link TaskStatus}'s for the connector
     */
    Collection<TaskStatus> taskStatuses(String connector);

    /**
     * Validate the provided connector config values against the configuration definition.
     *
     * @param connectorConfig the provided connector config values
     * @return Mono containing the {@link ConfigInfos} for the config
     */
    Mono<ConfigInfos> validateConnectorConfig(Map<String, String> connectorConfig);

    /**
     * Restart the task with the given id.
     *
     * @param id id of the task
     */
    Mono<Void> restartTask(ConnectorTaskId id);

    /**
     * Restart the connector.
     *
     * @param connName name of the connector
     */
    Mono<Void> restartConnector(String connName);

    /**
     * Restart the connector.
     *
     * @param connName name of the connector
     * @param delayMs  delay before restart
     */
    Mono<Void> restartConnectorLater(String connName, long delayMs);

    /**
     * Pause the connector. This call will asynchronously suspend processing by the connector and all
     * of its tasks.
     *
     * @param connector name of the connector
     */
    void pauseConnector(String connector);

    /**
     * Resume the connector. This call will asynchronously start the connector and its tasks (if
     * not started already).
     *
     * @param connector name of the connector
     */
    void resumeConnector(String connector);

    Set<PluginDesc<Connector>> connectorPlugins();

    Set<PluginDesc<Converter>> converterPlugins();

    @SuppressWarnings("rawtypes")
    Set<PluginDesc<Transformation>> transformationPlugins();

    @SuppressWarnings("rawtypes")
    Set<PluginDesc<Predicate>> predicatePlugins();

    Set<PluginDesc<ConfigProvider>> configProviderPlugins();

    /**
     * Get the cluster ID of the Kafka cluster backing this Connect cluster.
     *
     * @return the cluster ID of the Kafka cluster backing this connect cluster
     */
    String kafkaClusterId();
}
