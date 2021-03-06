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

package com.kinotic.structures.internal.trait.lifecycle;

import com.kinotic.structures.api.domain.Structure;
import com.kinotic.structures.api.domain.TypeCheckMap;
import com.kinotic.structures.api.domain.traitlifecycle.HasOnBeforeCreate;
import com.kinotic.structures.api.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VpnIp implements HasOnBeforeCreate {

    @Autowired
    private ItemService itemService;

    @Override
    public TypeCheckMap beforeCreate(TypeCheckMap obj, Structure structure, String fieldName) throws Exception {
        Random rnd = new Random();
        int first = rnd.nextInt(200);
        int second = rnd.nextInt(255);
        obj.amend("vpnIp", "10.50." + first + "." + second);
        return obj;
    }

}
