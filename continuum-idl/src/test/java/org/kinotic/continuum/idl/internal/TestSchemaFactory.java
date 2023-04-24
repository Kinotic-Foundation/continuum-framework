package org.kinotic.continuum.idl.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kinotic.continuum.idl.api.NamespaceDefinition;
import org.kinotic.continuum.idl.api.SchemaFactory;
import org.kinotic.continuum.idl.api.ServiceDefinition;
import org.kinotic.continuum.idl.internal.support.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Navíd Mitchell 🤪 on 4/14/23.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TestSchemaFactory {

    private static final Logger log = LoggerFactory.getLogger(TestSchemaFactory.class);

    @Autowired
    private SchemaFactory schemaFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSchemaFactory() throws Exception {
        NamespaceDefinition namespaceDefinition = schemaFactory.createForService(TestService.class);

        Optional<Map.Entry<String, ServiceDefinition>> serviceSchemasOptional = namespaceDefinition.getServiceSchemas().entrySet().stream().findFirst();

        Assertions.assertTrue(serviceSchemasOptional.isPresent());

        Assertions.assertEquals(TestService.class.getName(), serviceSchemasOptional.get().getKey());

        ServiceDefinition serviceDefinition = serviceSchemasOptional.get().getValue();

        Assertions.assertEquals(3, serviceDefinition.getFunctions().size());

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(namespaceDefinition);
        log.info("Namespace Definition\n"+json);
    }

}
