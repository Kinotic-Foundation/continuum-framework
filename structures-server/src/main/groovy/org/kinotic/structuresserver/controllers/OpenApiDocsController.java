package org.kinotic.structuresserver.controllers;

import io.swagger.v3.oas.models.OpenAPI;
import org.kinotic.structuresserver.openapi.OpenApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Created by Navíd Mitchell 🤪 on 3/18/23.
 */
@RestController
public class OpenApiDocsController {

    private final OpenApiService openApiService;

    public OpenApiDocsController(OpenApiService openApiService) {
        this.openApiService = openApiService;
    }

    // TODO: eventually this will be namespaced
    @GetMapping("/api-docs/openapi.json")
    public Mono<OpenAPI> getOpenApiDocs() {
        return Mono.defer(() -> Mono.just(openApiService.getOpenApiSpec()))
                   .subscribeOn(Schedulers.boundedElastic());
    }


}
