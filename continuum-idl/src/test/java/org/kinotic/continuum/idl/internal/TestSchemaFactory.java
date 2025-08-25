package org.kinotic.continuum.idl.internal;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kinotic.continuum.idl.api.directory.SchemaFactory;
import org.kinotic.continuum.idl.api.schema.NamespaceDefinition;
import org.kinotic.continuum.idl.api.schema.ServiceDefinition;
import org.kinotic.continuum.idl.internal.support.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Navíd Mitchell 🤪 on 4/14/23.
 */
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

        Optional<ServiceDefinition> serviceOptional = namespaceDefinition.getServices().stream().findFirst();

        Assertions.assertTrue(serviceOptional.isPresent());

        Assertions.assertEquals(TestService.class.getName(), serviceOptional.get().getQualifiedName());

        ServiceDefinition serviceDefinition = serviceOptional.get();

        Assertions.assertEquals(3, serviceDefinition.getFunctions().size());

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(namespaceDefinition);
        log.info("Namespace Definition\n"+json);
    }

}
