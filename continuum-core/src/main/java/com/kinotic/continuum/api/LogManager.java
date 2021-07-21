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

package com.kinotic.continuum.api;

import com.kinotic.continuum.api.annotations.Publish;
import com.kinotic.continuum.api.annotations.Scope;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;

import java.util.List;
import java.util.Map;

/**
 * Interface providing the ability to work with runtime logging configuration per node
 *
 * Created by Navid Mitchell on 7/9/20
 */
@Publish(version = "0.1.0")
public interface LogManager {

    @Scope
    String nodeId();

    /**
     * Gets all loggers configured within the system
     * @return A Json object containing the following
     *         {
     *             levels: [] // array of available levels
     *             loggers: // Map of log configuration name and value of LoggerLevels
     *             groups: // group name and GroupLoggerLevels
     *         }
     */
    Map<String, Object> loggers();

    LoggerLevels loggerLevels(String name);

    void configureLogLevel(String name, LogLevel configuredLevel);


    /**
     * Levels configured for a given logger exposed in a JSON friendly way.
     */
    class LoggerLevels {

        private String configuredLevel;

        public LoggerLevels(LogLevel configuredLevel) {
            this.configuredLevel = getName(configuredLevel);
        }

        protected final String getName(LogLevel level) {
            return (level != null) ? level.name() : null;
        }

        public String getConfiguredLevel() {
            return this.configuredLevel;
        }

    }

    class GroupLoggerLevels extends LoggerLevels {

        private List<String> members;

        public GroupLoggerLevels(LogLevel configuredLevel, List<String> members) {
            super(configuredLevel);
            this.members = members;
        }

        public List<String> getMembers() {
            return this.members;
        }

    }

    class SingleLoggerLevels extends LoggerLevels {

        private String effectiveLevel;

        public SingleLoggerLevels(LoggerConfiguration configuration) {
            super(configuration.getConfiguredLevel());
            this.effectiveLevel = getName(configuration.getEffectiveLevel());
        }

        public String getEffectiveLevel() {
            return this.effectiveLevel;
        }

    }

}
