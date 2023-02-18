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

package org.kinotic.continuum.gatewayserver


import org.kinotic.continuum.api.annotations.EnableContinuum
import org.kinotic.continuum.gateway.api.annotations.EnableContinuumGateway
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter
import org.springframework.boot.context.TypeExcludeFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType


@SpringBootConfiguration
@ComponentScan(excludeFilters = [ @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
			   @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) ])
@EnableConfigurationProperties
@EnableContinuum
@EnableContinuumGateway
@SuppressWarnings('SpringFacetCodeInspection')
class ContinuumGatewayServerApplication {

	static void main(String[] args) {
		SpringApplication.run(ContinuumGatewayServerApplication, args)
	}
}