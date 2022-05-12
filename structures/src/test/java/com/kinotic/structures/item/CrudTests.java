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

import com.kinotic.structures.api.domain.AlreadyExistsException;
import com.kinotic.structures.api.domain.Structure;
import com.kinotic.structures.api.domain.Trait;
import com.kinotic.structures.api.domain.TypeCheckMap;
import com.kinotic.structures.api.services.ItemService;
import com.kinotic.structures.api.services.StructureService;
import com.kinotic.structures.api.services.TraitService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CrudTests  {

    @Autowired
    private ItemService itemService;
    @Autowired
    private TraitService traitService;
    @Autowired
    private StructureService structureService;

    @Test
    public void createAndDeleteItem() throws Exception {

        Structure structure = new Structure();
        structure.setId("Item1-" + String.valueOf(System.currentTimeMillis()));
        structure.setDescription("Defines an Item1");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        structure.getTraits().put("mac", macOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        // now we can create an item with the above fields
        TypeCheckMap obj = new TypeCheckMap();
        obj.put("ip", "192.0.2.11");
        obj.put("mac", "000000000001");

        structureService.save(structure);
        structureService.publish(structure.getId());
        TypeCheckMap saved = itemService.createItem(structure.getId(), obj);

        Thread.sleep(1000);// give time for ES to flush the new item

        itemService.delete(structure.getId(), saved.getString("id"));

        Thread.sleep(1000);

        structureService.delete(structure.getId());

    }

    @Test
    public void createAndTryDuplicateCreate() {
        Assertions.assertThrows(AlreadyExistsException.class, () -> {
            Structure structure = new Structure();
            structure.setId("Item2-" + System.currentTimeMillis());
            structure.setDescription("Defines an Item1");


            Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
            Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
            Optional<Trait> macOptional = traitService.getTraitByName("Mac");

            structure.getTraits().put("vpnIp", vpnIpOptional.get());
            structure.getTraits().put("ip", ipOptional.get());
            structure.getTraits().put("mac", macOptional.get());
            // should also get createdTime, updateTime, and deleted by default

            // now we can create an item with the above fields
            TypeCheckMap obj = new TypeCheckMap();
            obj.put("ip", "192.0.2.11");
            obj.put("mac", "000000000001");

            structureService.save(structure);
            structureService.publish(structure.getId());
            TypeCheckMap saved = itemService.createItem(structure.getId(), obj);

            Thread.sleep(1000);// give time for ES to flush the new item

            try {
                saved = itemService.createItem(structure.getId(), saved);
            } catch (Exception e) {
                throw e;
            } finally {
                itemService.delete(structure.getId(), saved.getString("id"));

                Thread.sleep(1000);

                structureService.delete(structure.getId());
            }
        });
    }

    @Test
    public void createAndUpdateItem() throws Exception {

        Structure structure = new Structure();
        structure.setId("Item3-" + String.valueOf(System.currentTimeMillis()));
        structure.setDescription("Defines an Item1");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        structure.getTraits().put("mac", macOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        // now we can create an item with the above fields
        TypeCheckMap obj = new TypeCheckMap();
        obj.put("ip", "192.0.2.11");
        obj.put("mac", "000000000001");

        structureService.save(structure);
        structureService.publish(structure.getId());
        TypeCheckMap saved = itemService.createItem(structure.getId(), obj);

        try {
            Thread.sleep(1000);// give time for ES to flush the new item

            saved = itemService.getItemById(structure.getId(), saved.getString("id")).get();

            if (!saved.getString("mac").equals("000000000001")) {
                throw new IllegalStateException("Data provided to Item apon saving and getting");
            }


            saved.put("mac", "aaaaddddrrrr");

            itemService.updateItem(structure.getId(), saved);

            saved = itemService.getItemById(structure.getId(), saved.getString("id")).get();

            if (!saved.getString("mac").equals("aaaaddddrrrr")) {
                throw new IllegalStateException("Data provided to Item apon saving and getting");
            }


        } catch (AlreadyExistsException e) {
            throw e;
        } finally {
            itemService.delete(structure.getId(), saved.getString("id"));

            Thread.sleep(1000);

            structureService.delete(structure.getId());
        }


    }

    @Test
    public void createItemThenAddFieldAndUpdateItem() throws Exception {
        Structure structure = new Structure();
        structure.setId("Item4-" + String.valueOf(System.currentTimeMillis()));
        structure.setDescription("Defines an Item1");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        // now we can create an item with the above fields
        TypeCheckMap obj = new TypeCheckMap();
        obj.put("ip", "192.0.2.101");

        structureService.save(structure);
        structureService.publish(structure.getId());
        TypeCheckMap saved = itemService.createItem(structure.getId(), obj);

        try {
            Thread.sleep(1000);// give time for ES to flush the new item

            saved = itemService.getItemById(structure.getId(), saved.getString("id")).get();

            if (!saved.getString("ip").equals("192.0.2.101")) {
                throw new IllegalStateException("ip provided to Item apon saving and getting are not what was expected.");
            }


            structureService.addTraitToStructure(structure.getId(), "mac", macOptional.get());

            saved.put("mac", "aaaaddddrrrr");

            itemService.updateItem(structure.getId(), saved);

            saved = itemService.getItemById(structure.getId(), saved.getString("id")).get();

            if (!saved.getString("mac").equals("aaaaddddrrrr")) {
                throw new IllegalStateException("Data provided to Item apon saving and getting");
            }


        } catch (Exception e) {
            throw e;
        } finally {
            itemService.delete(structure.getId(), saved.getString("id"));
            Thread.sleep(1000);
            structureService.delete(structure.getId());
        }


    }

    @Test
    public void createItemThenPerformPartialUpdate() throws Exception {
        Structure structure = new Structure();
        structure.setId("Item5-" + String.valueOf(System.currentTimeMillis()));
        structure.setDescription("Defines an Item1");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        structure.getTraits().put("mac", macOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        // now we can create an item with the above fields
        TypeCheckMap obj = new TypeCheckMap();
        obj.put("ip", "192.0.2.101");
        obj.put("mac", "111111111111");

        structureService.save(structure);
        structureService.publish(structure.getId());
        TypeCheckMap saved = itemService.createItem(structure.getId(), obj);

        try {
            Thread.sleep(1000);// give time for ES to flush the new item

            saved = itemService.getItemById(structure.getId(), saved.getString("id")).get();

            if (!saved.getString("mac").equals("111111111111")) {
                throw new IllegalStateException("mac provided to Item apon saving and getting are not what was expected.");
            }


            TypeCheckMap partial = new TypeCheckMap();
            partial.put("id", saved.getString("id"));// required to update
            partial.put("mac", "aaaaddddrrrr");

            itemService.updateItem(structure.getId(), partial);

            TypeCheckMap updated = itemService.getItemById(structure.getId(), saved.getString("id")).get();

            if (!updated.getString("mac").equals("aaaaddddrrrr")) {
                throw new IllegalStateException("mac provided to Item apon saving and getting are not what we expected from the updated.");
            }

        } catch (Exception e) {
            throw e;
        } finally {
            itemService.delete(structure.getId(), saved.getString("id"));
            Thread.sleep(1000);
            structureService.delete(structure.getId());
        }

    }
}
