package org.kinotic.structuresserver.controllers;

import org.elasticsearch.search.SearchHits;
import org.kinotic.structures.api.domain.TypeCheckMap;
import org.kinotic.structures.api.services.ItemService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Created by Navíd Mitchell 🤪 on 3/18/23.
 */
@RestController
@RequestMapping("/api")
public class StructureItemController {

    private final ItemService itemService;

    public StructureItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{structureId}/{id}")
    public Mono<LinkedHashMap<String, Object>> getItemById(@PathVariable String structureId, @PathVariable String id) {
        return Mono.defer(() -> {
            try {
                Optional<TypeCheckMap> item = itemService.getItemById(structureId, id);
                return item.map(typeCheckMap -> Mono.just((LinkedHashMap<String, Object>) typeCheckMap))
                           .orElseGet(Mono::empty);
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/{structureId}/create")
    public Mono<LinkedHashMap<String, Object>> createItem(@PathVariable String structureId, LinkedHashMap<String, Object> item) {
        return Mono.defer(() -> {
            try {
                return Mono.just((LinkedHashMap<String, Object>)itemService.createItem(structureId, new TypeCheckMap(item)));
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/{structureId}/update")
    public Mono<LinkedHashMap<String, Object>> updateItem(@PathVariable String structureId, LinkedHashMap<String, Object> item) {
        return Mono.defer(() -> {
            try {
                return Mono.just((LinkedHashMap<String, Object>)itemService.updateItem(structureId, new TypeCheckMap(item)));
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{structureId}")
    public Mono<SearchHits> listItems(@PathVariable String structureId, @RequestParam int numberPerPage, @RequestParam int from) {
        return Mono.defer(() -> {
            try {
                return Mono.just(itemService.getAll(structureId, numberPerPage, from));
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping("/{structureId}/{id}")
    public Mono<Void> deleteItem(@PathVariable String structureId, @PathVariable String id) {
        return Mono.defer(() -> {
            try {
                itemService.delete(structureId, id);
                return Mono.empty().then();
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
