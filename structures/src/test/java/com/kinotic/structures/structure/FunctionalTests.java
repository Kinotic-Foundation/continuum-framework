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

import com.kinotic.structures.api.domain.Structure;
import com.kinotic.structures.api.domain.Trait;
import com.kinotic.structures.api.domain.TypeCheckMap;
import com.kinotic.structures.api.services.ItemService;
import com.kinotic.structures.api.services.StructureService;
import com.kinotic.structures.api.services.TraitService;
import org.elasticsearch.ElasticsearchStatusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FunctionalTests {

    @Autowired
    private TraitService traitService;
    @Autowired
    private StructureService structureService;
    @Autowired
    private ItemService itemService;

    @Test
    public void tryAddAdditionalFieldOutsideSchema() throws Exception {
        Assertions.assertThrows(ElasticsearchStatusException.class, () -> {
            Structure structure = new Structure();
            structure.setId("NUC7-" + System.currentTimeMillis());
            structure.setDescription("Defines the NUC Device properties");

            Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
            Optional<Trait> ipOptional = traitService.getTraitByName("Ip");

            structure.getTraits().put("vpnIp", vpnIpOptional.get());
            structure.getTraits().put("ip", ipOptional.get());
            // should also get createdTime, updateTime, and deleted by default

            structureService.save(structure);

            try {

                structureService.publish(structure.getId());

                // now we can create an item with the above fields
                TypeCheckMap obj = new TypeCheckMap();
                obj.put("ip", "192.0.2.11");
                obj.put("mac", "aaaaaaaaaaa1");
                itemService.createItem(structure.getId(), obj);

            } catch (ElasticsearchStatusException e) {
                throw e;
            } finally {
                structureService.delete(structure.getId());
            }
        });
    }

    @Test
    public void addTraitAfterPublishedAndNewItemAddedThenUpdateItem() throws Exception {
        Structure structure = new Structure();
        structure.setId("NUC8-" + System.currentTimeMillis());
        structure.setDescription("Defines the NUC Device properties");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        structureService.save(structure);
        structureService.publish(structure.getId());


        // now we can create an item with the above fields
        TypeCheckMap obj = new TypeCheckMap();
        obj.amend("ip", "192.0.2.11");
        obj.amend("vpnIp","10.99.99.9");
        TypeCheckMap savedJsonItem = itemService.createItem(structure.getId(), obj);
        TypeCheckMap item = null;

        try {
            structureService.addTraitToStructure(structure.getId(), "mac", macOptional.get());

            savedJsonItem.put("mac", "aaaaaaaaaaa1");

            itemService.updateItem(structure.getId(), savedJsonItem);

            item = itemService.getItemById(structure.getId(), savedJsonItem.getString("id")).get();

            if (!item.has("mac")) {
                throw new IllegalStateException("After save of an Item, new Trait field added, mac, does not exist after pulling from ES.");
            }


            if (!item.has("ip")) {
                throw new IllegalStateException("After save of an Item, field 'ip' does not exist after pulling from ES.");
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (item != null) {
                itemService.delete(structure.getId(), item.getString("id"));
            } else {
                itemService.delete(structure.getId(), savedJsonItem.getString("id"));
            }

            Thread.sleep(1000);
            structureService.delete(structure.getId());
        }

    }

    @Test
    public void addTraitAfterPublishedAndNewItems() throws Exception {
        Structure structure = new Structure();
        structure.setId("NUC9-"+System.currentTimeMillis());
        structure.setDescription("Defines the NUC Device properties");


        Optional<Trait> vpnIpOptional = traitService.getTraitByName("VpnIp");
        Optional<Trait> ipOptional = traitService.getTraitByName("Ip");
        Optional<Trait> macOptional = traitService.getTraitByName("Mac");

        structure.getTraits().put("vpnIp", vpnIpOptional.get());
        structure.getTraits().put("ip", ipOptional.get());
        // should also get createdTime, updateTime, and deleted by default

        structureService.save(structure);
        structureService.publish(structure.getId());


        // now we can create an item with the above fields
        TypeCheckMap firstItem = new TypeCheckMap();
        firstItem.amend("ip","192.0.2.11");
        firstItem.amend("vpnIp","10.99.99.9");
        TypeCheckMap savedJsonItem = itemService.createItem(structure.getId(), firstItem);
        TypeCheckMap item = null;

        try{
            structureService.addTraitToStructure(structure.getId(), "mac", macOptional.get());

            TypeCheckMap secondItem = new TypeCheckMap();
            secondItem.amend("ip","192.0.2.12");
            secondItem.amend("vpnIp","10.99.99.10");
            secondItem.amend("mac", "aaaaaaaaaaa2");

            item = itemService.createItem(structure.getId(), secondItem);

            TypeCheckMap freshFromDb = itemService.getItemById(structure.getId(), item.getString("id")).get();

            if(!freshFromDb.has("mac")){
                throw new IllegalStateException("After save of an Item, new Trait field added, mac, does not exist after pulling from ES.");
            }

            if(!freshFromDb.has("ip")){
                throw new IllegalStateException("After save of an Item, field 'ip' does not exist after pulling from ES.");
            }
        }catch(Exception e){
            throw e;
        }finally{
            if(item != null){
                itemService.delete(structure.getId(), item.getString("id"));
            }
            if(savedJsonItem != null){
                itemService.delete(structure.getId(), savedJsonItem.getString("id"));
            }
            Thread.sleep(1000);
            structureService.delete(structure.getId());
        }
    }
}
