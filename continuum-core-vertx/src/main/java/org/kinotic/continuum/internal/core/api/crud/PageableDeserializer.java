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

package org.kinotic.continuum.internal.core.api.crud;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by navid on 2/4/20
 */
public class PageableDeserializer extends JsonDeserializer<Pageable> {

    @Override
    public Pageable deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Validate.isTrue(node.has("pageNumber"), "pageNumber missing from Pageable");
        int pageNumber = node.get("pageNumber").intValue();
        Validate.isTrue(node.has("pageSize"), "pageSize missing from Pageable");
        int pageSize = node.get("pageSize").intValue();
        Sort sort = null;

        if(node.has("sort")){
            JsonNode ordersNode = node.get("sort").get("orders");
            if(ordersNode != null && ordersNode.isArray()){
                List<Sort.Order> orders = deserializeOrders(ordersNode);
                sort = Sort.by(orders);
            }
        }

        Pageable ret;
        if(sort == null){
            ret = PageRequest.of(pageNumber, pageSize);
        }else{
            ret = PageRequest.of(pageNumber, pageSize, sort);
        }

        return ret;
    }

    private List<Sort.Order> deserializeOrders(JsonNode ordersNode){
        List<Sort.Order> ret = new LinkedList<>();
        for(JsonNode node: ordersNode){
            Validate.isTrue(node.has("direction"), "direction missing from Order");
            Sort.Direction direction = Sort.Direction.fromString(node.get("direction").asText());
            Validate.isTrue(node.has("property"), "property missing from Order");
            String property = node.get("property").asText();
            Validate.isTrue(node.has("nullHandling"), "nullHandling missing from Order");
            Sort.NullHandling nullHandling = Sort.NullHandling.valueOf(node.get("nullHandling").asText());
            ret.add(new Sort.Order(direction, property, nullHandling));
        }
        return ret;
    }


}
