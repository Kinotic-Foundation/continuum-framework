package org.kinotic.structuresserver.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.kinotic.continuum.api.jsonSchema.datestyles.DateStyle;
import org.kinotic.continuum.api.jsonSchema.datestyles.MillsDateStyle;
import org.kinotic.continuum.api.jsonSchema.datestyles.StringDateStyle;
import org.kinotic.continuum.api.jsonSchema.datestyles.UnixDateStyle;
import org.kinotic.structures.api.domain.Structure;
import org.kinotic.structures.api.domain.Trait;
import org.kinotic.structuresserver.domain.StructureHolder;
import org.kinotic.structuresserver.serializer.Structures;
import org.kinotic.structuresserver.structures.IStructureManager;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Navíd Mitchell 🤪 on 3/17/23.
 */
@Component
public class DefaultOpenApiService implements OpenApiService {

    private final ObjectMapper objectMapper;
    private final IStructureManager structureManager;

    public DefaultOpenApiService(ObjectMapper objectMapper, IStructureManager structureManager) {
        this.objectMapper = objectMapper;
        this.structureManager = structureManager;
    }

    @Override
    public OpenAPI getOpenApiSpec() {
        OpenAPI openAPI = new OpenAPI();

        Info info = new Info()
                .title("All Structures API")
                .version("1.0")
                .description("Provides access to Structures Items");
        openAPI.setInfo(info);

        Structures structures = structureManager.getAllPublished(100, 0, "id", false);

        for(StructureHolder structureHolder : structures.getContent()){
            Structure structure = structureHolder.getStructure();
            Paths paths = getDefaultPathItemsForStructure(structure);
            openAPI.setPaths(paths);
        }

        return openAPI;
    }

    public Paths getDefaultPathItemsForStructure(Structure structure){
        Paths paths = new Paths();

        // Create a path item for the get all operation
        paths.put("/api/"+structure.getId(), new PathItem().get(createBaseOperation("Get all "+structure.getId(),
                                                                                    "getAll"+structure.getId(),
                                                                                        structure.getId(),
                                                                                        false)));

        // Create a path item for the get by id operation
        Operation getByIdOperation = createBaseOperation("Get "+structure.getId()+" by id",
                                                        "get"+structure.getId()+"ById",
                                                        structure.getId(),
                                                         true);

        getByIdOperation.addParametersItem(new Parameter().name("id")
                                                          .in("path")
                                                          .required(true)
                                                          .schema(new StringSchema()));
        paths.put("/api/"+structure.getId()+"/{id}", new PathItem().get(getByIdOperation));


        // Request body for create or update operations
        Schema<?> refSchema = new Schema<>().$ref(structure.getId());
        RequestBody structureRequestBody = new RequestBody()
                .content(new Content().addMediaType("application/json",
                                                    new MediaType().schema(refSchema)));

        // Create a path item for the create operation
        Operation createOperation = createBaseOperation("Create "+structure.getId(),
                                                       "create"+structure.getId(),
                                                       structure.getId(),
                                                        true);
        createOperation.addParametersItem(new Parameter().name("id")
                                                         .in("path")
                                                         .required(true)
                                                         .schema(new StringSchema()));
        createOperation.requestBody(structureRequestBody);
        paths.put("/api/"+structure.getId(), new PathItem().post(createOperation));


        // Create a path item for the update operation
        Operation updateOperation = createBaseOperation("Update "+structure.getId(),
                                                       "update"+structure.getId(),
                                                       structure.getId(),
                                                        true);
        updateOperation.addParametersItem(new Parameter().name("id")
                                                         .in("path")
                                                         .required(true)
                                                         .schema(new StringSchema()));
        updateOperation.requestBody(structureRequestBody);
        paths.put("/api/"+structure.getId()+"/{id}", new PathItem().put(updateOperation));

        // Create a path item for the delete operation
        Operation deleteOperation = createBaseOperation("Delete "+structure.getId(),
                                                       "delete"+structure.getId(),
                                                       structure.getId(),
                                                        true);
        deleteOperation.addParametersItem(new Parameter().name("id")
                                                         .in("path")
                                                         .required(true)
                                                         .schema(new StringSchema()));
        paths.put("/api/"+structure.getId()+"/{id}", new PathItem().delete(deleteOperation));


        return paths;
    }

    private static Operation createBaseOperation(String operationSummary, String operationId, String structureId, boolean singleItem) {
        Operation operation = new Operation().summary(operationSummary)
                                             .operationId(operationId);

        // Add the default responses and the response for the structure item being returned
        ApiResponses defaultResponses = getDefaultResponses();

        // create a response for the structure item
        ApiResponse response = new ApiResponse().description(operationSummary + "response");
        Content content = new Content();
        MediaType mediaType = new MediaType();
        if(singleItem){
            mediaType.setSchema(new Schema<>().$ref(structureId));
        }else{
            // TODO: fix this to be correct for searchHits response
            mediaType.setSchema(new ArraySchema().items(new Schema<>().$ref(structureId)));
        }
        content.addMediaType("application/json", mediaType);
        response.setContent(content);
        defaultResponses.put("200", response);

        operation.setResponses(defaultResponses);

        return operation;
    }

    private static ApiResponses getDefaultResponses(){
        ApiResponses responses = new ApiResponses();
        responses.put("400", new ApiResponse().description("Bad Request"));
        responses.put("401", new ApiResponse().description("Unauthorized"));
        responses.put("403", new ApiResponse().description("Forbidden"));
        responses.put("404", new ApiResponse().description("Not Found"));
        responses.put("500", new ApiResponse().description("Internal Server Error"));
        return responses;
    }


    @Override
    public Schema<?> getSchemaForStructureItem(Structure structure){
        ObjectSchema objectSchema = new ObjectSchema();
        for (Map.Entry<String, Trait> traitEntry : structure.getTraits().entrySet()) {
            try {
                objectSchema.addProperty(traitEntry.getKey(), getSchemaForTrait(traitEntry.getValue()));
                if(traitEntry.getValue().isRequired() && traitEntry.getValue().isModifiable()){
                    objectSchema.addRequiredItem(traitEntry.getKey());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get schema for trait "+traitEntry.getKey(), e);
            }
        }
        return objectSchema;
    }

    @Override
    public Schema<?> getSchemaForTrait(Trait trait) throws Exception{
        Schema<?> schema = objectMapper.readValue(trait.getSchema(), Schema.class);
        // We have a custom schema for dates as explained here continuum-core/src/main/java/org/kinotic/continuum/api/jsonSchema/JsonSchema.java
        // We need to adapt to a compatible schema for OpenApi
        if(schema.getType().equals("date")){
            DateStyle dateStyle = objectMapper.readValue(schema.getFormat(), DateStyle.class);
            if(dateStyle instanceof UnixDateStyle){
                schema = new IntegerSchema().format("int64");
            }else if (dateStyle instanceof MillsDateStyle){
                schema = new IntegerSchema().format("int64");
            } else if (dateStyle instanceof StringDateStyle) {
                // FIXME: I think the intent here is unclear. The OpenApi spec expects a reg ex. Im not certain this is clear in the continuum json spec
                StringDateStyle stringDateStyle = (StringDateStyle) dateStyle;
                schema = new StringSchema().pattern(stringDateStyle.getPattern());
            }
        }
        return schema;
    }



}
