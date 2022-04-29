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

package com.kinotic.structures.testenv;

import com.kinotic.structures.api.domain.AlreadyExistsException;
import com.kinotic.structures.api.domain.Structure;
import com.kinotic.structures.api.domain.Trait;
import com.kinotic.structures.api.services.ItemService;
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
public class GenerateData {

    @Autowired
    private ItemService itemService;
    @Autowired
    private TraitService traitService;
    @Autowired
    private StructureService structureService;
    
    @Test
    public void createData() {

//		Structure nucStructure = getNucStructure()
//		Structure piStructure = getPiStructure()
//		Structure kitStructure = getKitStructure()
//
//		TypeCheckMap nuc = new TypeCheckMap()
//		nuc.put("ip","192.0.2.101")
//		nuc.put("mac","111111111111")
//
//		TypeCheckMap pi1 = new TypeCheckMap()
//		pi1.put("ip","192.0.2.211")
//		pi1.put("mac","111111111111")
//		pi1.put("generation","B+v1.2")
//		pi1.put("type","SensorPi")
//
//		TypeCheckMap pi2 = new TypeCheckMap()
//		pi2.put("ip","192.0.2.212")
//		pi2.put("mac","111111111112")
//		pi2.put("generation","Bv1.1")
//		pi1.put("type","CountsPi")
//
//
//		TypeCheckMap savedNuc = itemService.createItem(nucStructure.id, nuc)
//		TypeCheckMap savedPi1 = itemService.createItem(piStructure.id, pi1)
//		TypeCheckMap savedPi2 = itemService.createItem(piStructure.id, pi2)
//
//		TypeCheckMap kit = new TypeCheckMap()
//		kit.put("placement","End1")
//		kit.put("partNumber","123456789")
//		kit.put("nuc",savedNuc)
//		kit.put("pi1",savedPi1)
//		kit.put("pi2",savedPi2)
//
//		TypeCheckMap savedKit = itemService.createItem(kitStructure.id, kit)

    }

    public Structure getNucStructure() throws AlreadyExistsException, IOException {
        Structure structure = new Structure();
        structure.setId("NUC-" + System.currentTimeMillis());
        structure.setDescription("Defines an NUC");

        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        structure.getTraits().put("mac", macOptional.get());

        structureService.save(structure);
        structureService.publish(structure.getId());
        return structure;
    }

    public Structure getPiStructure() throws AlreadyExistsException, IOException {
        Structure structure = new Structure();
        structure.setId("PI-" + System.currentTimeMillis());
        structure.setDescription("Defines an PI");

        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");
        Optional<Trait> textOptional = traitService.getTraitByName("KeywordString");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        structure.getTraits().put("mac", macOptional.get());
        structure.getTraits().put("generation", textOptional.get());
        structure.getTraits().put("type", textOptional.get());

        structureService.save(structure);
        structureService.publish(structure.getId());
        return structure;
    }

    public Structure getKitStructure() throws AlreadyExistsException, IOException {
        Structure structure = new Structure();
        structure.setId("KIT-" + System.currentTimeMillis());
        structure.setDescription("Defines a KIT");

        Optional<Trait> textOptional = traitService.getTraitByName("KeywordString");
        Optional<Trait> objRefOptional = traitService.getTraitByName("ObjectReference");

        structure.getTraits().put("placement", textOptional.get());
        structure.getTraits().put("partNumber", textOptional.get());
        structure.getTraits().put("nuc", objRefOptional.get());
        structure.getTraits().put("pi1", objRefOptional.get());
        structure.getTraits().put("pi2", objRefOptional.get());

        structureService.save(structure);
        structureService.publish(structure.getId());
        return structure;
    }
}
