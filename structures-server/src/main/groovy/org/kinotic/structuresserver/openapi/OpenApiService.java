package org.kinotic.structuresserver.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.kinotic.structures.api.domain.Structure;
import org.kinotic.structures.api.domain.Trait;

/**
 * Created by Navíd Mitchell 🤪 on 3/18/23.
 */
public interface OpenApiService {

    /**
     * Gets the OpenAPI spec for all the structures
     * TODO: eventually this will be namespaced
     * @return the OpenAPI spec
     */
    OpenAPI getOpenApiSpec();

    /**
     * This gets the Schema for a structure item.
     * This is not the schema for the structure itself but rather the schema for the items that are defined by the structure.
     *
     * @param structure to get the schema for
     * @return the schema for the structure item
     */
    Schema<?> getSchemaForStructureItem(Structure structure);

    /**
     * Gets the {@link Schema} that represents the given {@link Trait}
     *
     * @param trait to get the schema for
     * @return the schema for the trait
     * @throws Exception if there is an error getting the schema
     */
    Schema<?> getSchemaForTrait(Trait trait) throws Exception;
}
