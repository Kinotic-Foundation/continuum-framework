package org.kinotic.continuum.cloud

import org.kinotic.continuum.api.annotations.EnableContinuum
import org.kinotic.continuum.grind.api.annotations.EnableContinuumGrind
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableContinuumGrind
@EnableContinuum
class ContinuumCloudApplication {

	static void main(String[] args) {
		SpringApplication.run(ContinuumCloudApplication, args)
	}

}
