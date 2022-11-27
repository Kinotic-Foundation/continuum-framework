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

package org.kinotic.continuum.internal.api;

import org.kinotic.continuum.api.Continuum;
import org.kinotic.continuum.api.LogManager;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.logging.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Default impl of {@link LogManager}
 *
 * Shamelessly copied from org.springframework.boot.actuate.logging.LoggersEndpoint
 *
 * @author Ben Hale
 * @author Phillip Webb
 * @author HaiTao Zhang
 */
@Component
public class DefaultLogManager implements LogManager {


    private final Continuum continuum;
    private final LoggingSystem loggingSystem;
    private final LoggerGroups loggerGroups;


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DefaultLogManager(Continuum continuum,
                             LoggingSystem loggingSystem,
                             LoggerGroups loggerGroups) {
        this.continuum = continuum;
        this.loggingSystem = loggingSystem;
        this.loggerGroups = loggerGroups;
    }

    @Override
    public String nodeId() {
        return continuum.nodeId();
    }

    public Map<String, Object> loggers() {
        Collection<LoggerConfiguration> configurations = this.loggingSystem.getLoggerConfigurations();
        if (configurations == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("levels", getLevels());
        result.put("loggers", getLoggers(configurations));
        result.put("groups", getGroups());
        return result;
    }

    private Map<String, LoggerLevels> getGroups() {
        Map<String, LoggerLevels> groups = new LinkedHashMap<>();
        this.loggerGroups.forEach((group) -> groups.put(group.getName(),
                                                        new GroupLoggerLevels(group.getConfiguredLevel(), group.getMembers())));
        return groups;
    }

    public LoggerLevels loggerLevels(String name) {
        Validate.notNull(name, "Name must not be null");
        LoggerGroup group = this.loggerGroups.get(name);
        if (group != null) {
            return new GroupLoggerLevels(group.getConfiguredLevel(), group.getMembers());
        }
        LoggerConfiguration configuration = this.loggingSystem.getLoggerConfiguration(name);
        return (configuration != null) ? new SingleLoggerLevels(configuration) : null;
    }

    public void configureLogLevel(String name, LogLevel configuredLevel) {
        Validate.notNull(name, "Name must not be empty");
        LoggerGroup group = this.loggerGroups.get(name);
        if (group != null && group.hasMembers()) {
            group.configureLogLevel(configuredLevel, this.loggingSystem::setLogLevel);
            return;
        }
        this.loggingSystem.setLogLevel(name, configuredLevel);
    }

    private NavigableSet<LogLevel> getLevels() {
        Set<LogLevel> levels = this.loggingSystem.getSupportedLogLevels();
        return new TreeSet<>(levels).descendingSet();
    }

    private Map<String, LoggerLevels> getLoggers(Collection<LoggerConfiguration> configurations) {
        Map<String, LoggerLevels> loggers = new LinkedHashMap<>(configurations.size());
        for (LoggerConfiguration configuration : configurations) {
            loggers.put(configuration.getName(), new SingleLoggerLevels(configuration));
        }
        return loggers;
    }

}
