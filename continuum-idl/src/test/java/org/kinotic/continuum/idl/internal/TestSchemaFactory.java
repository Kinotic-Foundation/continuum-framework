package org.kinotic.continuum.idl.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kinotic.continuum.idl.api.NamespaceSchema;
import org.kinotic.continuum.idl.api.SchemaFactory;
import org.kinotic.continuum.idl.api.ServiceSchema;
import org.kinotic.continuum.idl.internal.support.TestService;
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

    @Autowired
    private SchemaFactory schemaFactory;

    @Test
    public void testSchemaFactory(){
        NamespaceSchema namespaceSchema = schemaFactory.createForService(TestService.class);

        Optional<Map.Entry<String, ServiceSchema>> serviceSchemasOptional = namespaceSchema.getServiceSchemas().entrySet().stream().findFirst();

        Assertions.assertTrue(serviceSchemasOptional.isPresent());

        Assertions.assertEquals(TestService.class.getName(), serviceSchemasOptional.get().getKey());

        ServiceSchema serviceSchema = serviceSchemasOptional.get().getValue();

        Assertions.assertEquals(3, serviceSchema.getFunctions().size());

    }

}
