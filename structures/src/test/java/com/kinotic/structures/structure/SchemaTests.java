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

package com.kinotic.structures.structure;

import com.kinotic.structures.api.domain.AlreadyExistsException;
import com.kinotic.structures.api.domain.PermenentTraitException;
import com.kinotic.structures.api.domain.Structure;
import com.kinotic.structures.api.domain.Trait;
import com.kinotic.structures.api.services.StructureService;
import com.kinotic.structures.api.services.TraitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SchemaTests {

    @Autowired
    private TraitService traitService;
    @Autowired
    private StructureService structureService;

    @Test
    public void validateJsonSchemaGeneration() throws AlreadyExistsException, IOException, PermenentTraitException {
        Structure structure = new Structure();
        structure.setId("NUC11-" + System.currentTimeMillis());
        structure.setDescription("Defines the NUC Device properties");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        structure.getTraits().put("mac", macOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        final Structure saved = structureService.save(structure);

        String jsonSchema = structureService.getJsonSchema(saved);
        System.out.println(jsonSchema);

        structureService.delete(structure.getId());

        if (saved.getTraits().size() != 9) {
            throw new IllegalStateException("We should have 9 traits, 6 given by default. We have " + saved.getTraits().size());
        }

    }

    @Test
    public void validateElasticSearchMappingGeneration() throws AlreadyExistsException, IOException, PermenentTraitException {
        Structure structure = new Structure();
        structure.setId("NUC12-" + System.currentTimeMillis());
        structure.setDescription("Defines the NUC Device properties");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        structure.getTraits().put("mac", macOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        final Structure saved = structureService.save(structure);

        String esSchema = structureService.getElasticSearchBaseMapping(saved);
        System.out.println(esSchema);

        structureService.delete(structure.getId());

        if (saved.getTraits().size() != 9) {
            throw new IllegalStateException("We should have 9 traits, 6 given by default. We have " + saved.getTraits().size());
        }


    }
}
