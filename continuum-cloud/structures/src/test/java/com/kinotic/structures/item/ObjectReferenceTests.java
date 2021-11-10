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

package com.kinotic.structures.item;

import com.kinotic.structures.api.domain.*;
import com.kinotic.structures.api.services.ItemService;
import com.kinotic.structures.api.services.StructureService;
import com.kinotic.structures.api.services.TraitService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ObjectReferenceTests {

    @Autowired
    private ItemService itemService;
    @Autowired
    private TraitService traitService;
    @Autowired
    private StructureService structureService;

    @Test
    public void createItemThatAlsoHasObjectReference_GetAllShouldNotLazyLoad_ThenDeleteAll() throws Exception {

        Structure nucStructure = getNucStructure();
        Structure piStructure = getPiStructure();
        Structure kitStructure = getKitStructure(piStructure,nucStructure);

        TypeCheckMap nuc = new TypeCheckMap();
        nuc.put("ip", "192.0.2.101");
        nuc.put("mac", "111111111111");

        TypeCheckMap pi1 = new TypeCheckMap();
        pi1.put("ip", "192.0.2.211");
        pi1.put("mac", "111111111111");
        pi1.put("generation", "B+v1.2");
        pi1.put("type", "SensorPi");

        TypeCheckMap pi2 = new TypeCheckMap();
        pi2.put("ip", "192.0.2.212");
        pi2.put("mac", "111111111112");
        pi2.put("generation", "Bv1.1");
        pi1.put("type", "CountsPi");


        TypeCheckMap savedNuc = itemService.createItem(nucStructure.getId(), nuc);
        TypeCheckMap savedPi1 = itemService.createItem(piStructure.getId(), pi1);
        TypeCheckMap savedPi2 = itemService.createItem(piStructure.getId(), pi2);

        TypeCheckMap kit = new TypeCheckMap();
        kit.put("placement", "End1");
        kit.put("partNumber", "123456789");
        kit.put("nuc", savedNuc);
        kit.put("pi1", savedPi1);
        kit.put("pi2", savedPi2);

        TypeCheckMap savedKit = itemService.createItem(kitStructure.getId(), kit);

        try {

            Thread.sleep(1000);// give time for ES to flush the new item

            SearchHits hits = itemService.getAll(kitStructure.getId(), 100, 0);

            SearchHit hit = hits.getHits()[0];
            TypeCheckMap obj = new TypeCheckMap(hit.getSourceAsMap());
            final TypeCheckMap nucRef = obj.getTypeCheckMap("nuc");
            if (nucRef.length() != 2) {
                throw new IllegalStateException("We saved a Kit which has ObjectReferences, getAll() should never resolve deps, expected 2 fields - structureId, and id - got " + nucRef.length());
            }


        } finally {
            itemService.delete(kitStructure.getId(), savedKit.getString("id"));
            itemService.delete(nucStructure.getId(), savedNuc.getString("id"));
            itemService.delete(piStructure.getId(), savedPi1.getString("id"));
            itemService.delete(piStructure.getId(), savedPi2.getString("id"));
            Thread.sleep(1000);
            structureService.delete(nucStructure.getId());
            structureService.delete(piStructure.getId());
            structureService.delete(kitStructure.getId());
        }

    }

    @Test
    public void createItemThatAlsoHasObjectReference_GetOwnerByIdShouldLazyLoad_ThenDeleteAll() throws Exception {

        Structure nucStructure = getNucStructure();
        Structure piStructure = getPiStructure();
        Structure kitStructure = getKitStructure(piStructure,nucStructure);

        TypeCheckMap nuc = new TypeCheckMap();
        nuc.put("ip", "192.0.2.101");
        nuc.put("mac", "111111111111");

        TypeCheckMap pi1 = new TypeCheckMap();
        pi1.put("ip", "192.0.2.211");
        pi1.put("mac", "111111111111");
        pi1.put("generation", "B+v1.2");
        pi1.put("type", "SensorPi");

        TypeCheckMap pi2 = new TypeCheckMap();
        pi2.put("ip", "192.0.2.212");
        pi2.put("mac", "111111111112");
        pi2.put("generation", "Bv1.1");
        pi2.put("type", "CountsPi");


        TypeCheckMap savedNuc = itemService.createItem(nucStructure.getId(), nuc);
        TypeCheckMap savedPi1 = itemService.createItem(piStructure.getId(), pi1);
        TypeCheckMap savedPi2 = itemService.createItem(piStructure.getId(), pi2);

        TypeCheckMap kit = new TypeCheckMap();
        kit.put("placement", "End1");
        kit.put("partNumber", "123456789");
        kit.put("nuc", savedNuc);
        kit.put("pi1", savedPi1);
        kit.put("pi2", savedPi2);

        TypeCheckMap savedKit = itemService.createItem(kitStructure.getId(), kit);

        try {

            Thread.sleep(1000);// give time for ES to flush the new item

            Optional<TypeCheckMap> resolvedKit = itemService.getById(kitStructure, kit.getString("id"));

            final TypeCheckMap nucRef = resolvedKit.get().getTypeCheckMap("nuc");
            if (nucRef.length() != 9 || !nucRef.getString("ip").equals("192.0.2.101")) {
                throw new IllegalStateException("We saved a Kit which has ObjectReferences, called getById() and it should resolve all deps, NUC (nuc) expected 9 fields - got " + nucRef.length());
            }

            final TypeCheckMap pi1Ref = resolvedKit.get().getTypeCheckMap("pi1");
            if (pi1Ref.length() != 11 || !pi1Ref.getString("type").equals("SensorPi")) {
                throw new IllegalStateException("We saved a Kit which has ObjectReferences, called getById() and it should resolve all deps, PI (pi1) expected 11 fields - got " + pi1Ref.length());
            }

            final TypeCheckMap pi2Ref = resolvedKit.get().getTypeCheckMap("pi2");
            if (pi2Ref.length() != 11 || !pi2Ref.getString("type").equals("CountsPi")) {
                throw new IllegalStateException("We saved a Kit which has ObjectReferences, called getById() and it should resolve all deps, PI (pi2) expected 11 fields - got " + pi2Ref.length());
            }


        } finally {
            itemService.delete(kitStructure.getId(), savedKit.getString("id"));
            itemService.delete(nucStructure.getId(), savedNuc.getString("id"));
            itemService.delete(piStructure.getId(), savedPi1.getString("id"));
            itemService.delete(piStructure.getId(), savedPi2.getString("id"));
            Thread.sleep(1000);
            structureService.delete(nucStructure.getId());
            structureService.delete(piStructure.getId());
            structureService.delete(kitStructure.getId());
        }

    }

    @Test
    public void createItemThatAlsoHasObjectReferences_ThenAttemptToDeleteReference_CleanUp() throws Exception {
        Assertions.assertThrows(IsReferencedException.class, () -> {
            Structure nucStructure = getNucStructure();
            Structure piStructure = getPiStructure();
            Structure kitStructure = getKitStructure(piStructure, nucStructure);

            TypeCheckMap nuc = new TypeCheckMap();
            nuc.put("ip", "192.0.2.101");
            nuc.put("mac", "111111111111");

            TypeCheckMap pi1 = new TypeCheckMap();
            pi1.put("ip", "192.0.2.211");
            pi1.put("mac", "111111111111");
            pi1.put("generation", "B+v1.2");
            pi1.put("type", "SensorPi");

            TypeCheckMap pi2 = new TypeCheckMap();
            pi2.put("ip", "192.0.2.212");
            pi2.put("mac", "111111111112");
            pi2.put("generation", "Bv1.1");
            pi1.put("type", "CountsPi");


            TypeCheckMap savedNuc = itemService.createItem(nucStructure.getId(), nuc);
            TypeCheckMap savedPi1 = itemService.createItem(piStructure.getId(), pi1);
            TypeCheckMap savedPi2 = itemService.createItem(piStructure.getId(), pi2);

            TypeCheckMap kit = new TypeCheckMap();
            kit.put("placement", "End1");
            kit.put("partNumber", "123456789");
            kit.put("nuc", savedNuc);
            kit.put("pi1", savedPi1);
            kit.put("pi2", savedPi2);

            TypeCheckMap savedKit = itemService.createItem(kitStructure.getId(), kit);

            try {

                Thread.sleep(1000);// give time for ES to flush the new item

                // will throw since it has a reference for it somewhere.
                itemService.delete(nucStructure.getId(), savedNuc.getString("id"));

            } catch (Exception e) {
                throw e;
            } finally {
                itemService.delete(kitStructure.getId(), savedKit.getString("id"));
                itemService.delete(nucStructure.getId(), savedNuc.getString("id"));
                itemService.delete(piStructure.getId(), savedPi1.getString("id"));
                itemService.delete(piStructure.getId(), savedPi2.getString("id"));
                Thread.sleep(1000);
                structureService.delete(nucStructure.getId());
                structureService.delete(piStructure.getId());
                structureService.delete(kitStructure.getId());
            }
        });
    }

    @Test
    public void createItemThatAlsoHasObjectReferences_TryUseSameTypeInReference_CleanUp() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Structure nucStructure = getNucStructure();
            Structure piStructure = getPiStructure();
            Structure kitStructure = getKitStructure(piStructure, nucStructure);

            TypeCheckMap nuc = new TypeCheckMap();
            nuc.put("ip", "192.0.2.101");
            nuc.put("mac", "111111111111");

            TypeCheckMap pi1 = new TypeCheckMap();
            pi1.put("ip", "192.0.2.211");
            pi1.put("mac", "111111111111");
            pi1.put("generation", "B+v1.2");
            pi1.put("type", "SensorPi");

            TypeCheckMap pi2 = new TypeCheckMap();
            pi2.put("ip", "192.0.2.212");
            pi2.put("mac", "111111111112");
            pi2.put("generation", "Bv1.1");
            pi1.put("type", "CountsPi");

            TypeCheckMap savedNuc = itemService.createItem(nucStructure.getId(), nuc);
            TypeCheckMap savedPi1 = itemService.createItem(piStructure.getId(), pi1);
            TypeCheckMap savedPi2 = itemService.createItem(piStructure.getId(), pi2);

            TypeCheckMap kit = new TypeCheckMap();
            kit.put("placement", "End1");
            kit.put("partNumber", "123456789");
            kit.put("nuc", savedNuc); // put a PI in a NUC position
            kit.put("pi1", savedPi1); // put a NUC in a PI position
            kit.put("pi2", savedPi2);

            // should throw up b/c of the type mismatch
            TypeCheckMap savedKit = itemService.createItem(kitStructure.getId(), kit);

            try {

                Thread.sleep(1000);// give time for ES to flush the new item

                TypeCheckMap kit2 = new TypeCheckMap();
                kit2.put("placement", "End1");
                kit2.put("partNumber", "123456789");
                kit2.put("nuc", savedKit); // put a PI in a NUC position
                kit2.put("pi1", savedKit); // put a NUC in a PI position
                kit2.put("pi2", savedKit);

                // should throw up b/c of the type mismatch
                TypeCheckMap throwUp = itemService.createItem(kitStructure.getId(), kit2);

            } catch (Exception e) {
                throw e;
            } finally {
                itemService.delete(kitStructure.getId(), savedKit.getString("id"));
                Thread.sleep(1000);
                itemService.delete(nucStructure.getId(), savedNuc.getString("id"));
                itemService.delete(piStructure.getId(), savedPi1.getString("id"));
                itemService.delete(piStructure.getId(), savedPi2.getString("id"));
                Thread.sleep(1000);
                structureService.delete(nucStructure.getId());
                structureService.delete(piStructure.getId());
                structureService.delete(kitStructure.getId());
            }
        });

    }

    @Test
    public void createItemThatAlsoHasObjectReferences_UseInvalidReferenceTypesForField_CleanUp() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Structure nucStructure = getNucStructure();
            Structure piStructure = getPiStructure();
            Structure kitStructure = getKitStructure(piStructure, nucStructure);

            TypeCheckMap nuc = new TypeCheckMap();
            nuc.put("ip", "192.0.2.101");
            nuc.put("mac", "111111111111");

            TypeCheckMap pi1 = new TypeCheckMap();
            pi1.put("ip", "192.0.2.211");
            pi1.put("mac", "111111111111");
            pi1.put("generation", "B+v1.2");
            pi1.put("type", "SensorPi");

            TypeCheckMap pi2 = new TypeCheckMap();
            pi2.put("ip", "192.0.2.212");
            pi2.put("mac", "111111111112");
            pi2.put("generation", "Bv1.1");
            pi1.put("type", "CountsPi");


            TypeCheckMap savedNuc = itemService.createItem(nucStructure.getId(), nuc);
            TypeCheckMap savedPi1 = itemService.createItem(piStructure.getId(), pi1);
            TypeCheckMap savedPi2 = itemService.createItem(piStructure.getId(), pi2);

            TypeCheckMap kit = new TypeCheckMap();
            kit.put("placement", "End1");
            kit.put("partNumber", "123456789");
            kit.put("nuc", savedPi1); // put a PI in a NUC position
            kit.put("pi1", savedNuc); // put a NUC in a PI position
            kit.put("pi2", savedPi2);


            try {

                Thread.sleep(1000);// give time for ES to flush the new item

                // should throw up b/c of the type mismatch
                TypeCheckMap savedKit = itemService.createItem(kitStructure.getId(), kit);

            } catch (Exception e) {
                throw e;
            } finally {
                itemService.delete(nucStructure.getId(), savedNuc.getString("id"));
                itemService.delete(piStructure.getId(), savedPi1.getString("id"));
                itemService.delete(piStructure.getId(), savedPi2.getString("id"));
                Thread.sleep(1000);
                structureService.delete(nucStructure.getId());
                structureService.delete(piStructure.getId());
                structureService.delete(kitStructure.getId());
            }
        });
    }

    public Structure getNucStructure() throws Exception {
        Structure structure = new Structure();
        structure.setId("NUC-" + String.valueOf(System.currentTimeMillis()));
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

    public Structure getPiStructure() throws Exception {
        Structure structure = new Structure();
        structure.setId("PI-" + String.valueOf(System.currentTimeMillis()));
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

    public Structure getKitStructure(Structure piStructure, Structure nucStructure) throws Exception {
        Structure structure = new Structure();
        structure.setId("KIT-" + String.valueOf(System.currentTimeMillis()));
        structure.setDescription("Defines a KIT");

        Optional<Trait> textOptional = traitService.getTraitByName("KeywordString");
        Optional<Trait> objNucRefOptional = traitService.getTraitByName("Reference "+nucStructure.getId().trim());
        Optional<Trait> objPiRefOptional = traitService.getTraitByName("Reference "+piStructure.getId().trim());

        // What if we just used some auto generated ObjectReferences when creating structure
        // we create the object reference for the object and we can just use the Name or Description
        // fields to populate or use a pattern in the name so that we can get at the particular structure
        // that we configured.

        structure.getTraits().put("placement", textOptional.get());
        structure.getTraits().put("partNumber", textOptional.get());
        structure.getTraits().put("nuc", objNucRefOptional.get());
        structure.getTraits().put("pi1", objPiRefOptional.get());
        structure.getTraits().put("pi2", objPiRefOptional.get());

        structureService.save(structure);
        structureService.publish(structure.getId());
        return structure;
    }

}
