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

package com.kinotic.continuum.orbiter

import com.kinotic.continuum.api.annotations.EnableContinuum
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication(exclude = [DataSourceAutoConfiguration.class,
								  DataSourceTransactionManagerAutoConfiguration.class,
								  HibernateJpaAutoConfiguration.class,
								  JpaRepositoriesAutoConfiguration.class,
								  WebMvcAutoConfiguration.class,
		                          HttpMessageConvertersAutoConfiguration.class,
								  JmxAutoConfiguration.class])
@EnableConfigurationProperties
@EnableContinuum
class ContinuumOrbiterApplication {

	static void main(String[] args) {
		SpringApplication.run(ContinuumOrbiterApplication, args)
	}

}
