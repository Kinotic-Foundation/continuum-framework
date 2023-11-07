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
package org.kinotic.continuum.internal.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.core.api.crud.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by navid on 2/4/20
 */
public class PageableDeserializer extends JsonDeserializer<Pageable> {

    @Override
    public Pageable deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Validate.isTrue(node.has("pageSize"), "pageSize missing from Pageable");
        int pageSize = node.get("pageSize").intValue();

        Sort sort = null;
        if(node.has("sort")){
            JsonNode ordersNode = node.get("sort").get("orders");
            if(ordersNode != null && ordersNode.isArray()){
                List<Order> orders = deserializeOrders(ordersNode);
                sort = Sort.by(orders);
            }
        }

        Integer pageNumber = null;
        if(node.has("pageNumber")){
            pageNumber = node.get("pageNumber").intValue();
        }

        // We do this check with a boolean, because a null cursor indicates the first page
        String cursor = null;
        boolean cursorPresent = false;
        if(node.has("cursor")){
            cursorPresent = true;
            cursor = node.get("cursor").textValue();
        }

        if(!cursorPresent && pageNumber == null){
            throw new IllegalArgumentException("Pageable must have either a cursor or pageNumber");
        } else if (cursorPresent && pageNumber != null) {
            throw new IllegalArgumentException("Pageable cannot have both a cursor and a pageNumber");
        }

        Pageable ret;
        if(pageNumber == null){
            ret = Pageable.create(cursor, pageSize, sort);
        }else{
            ret = Pageable.create(pageNumber, pageSize, sort);
        }
        return ret;
    }

    private List<Order> deserializeOrders(JsonNode ordersNode){
        List<Order> ret = new LinkedList<>();
        for(JsonNode node: ordersNode){

            Validate.isTrue(node.has("direction"), "direction missing from Order");
            Direction direction = Direction.fromString(node.get("direction").asText());

            Validate.isTrue(node.has("property"), "property missing from Order");
            String property = node.get("property").asText();

            if(node.has("nullHandling")){
                NullHandling nullHandling = NullHandling.valueOf(node.get("nullHandling").asText());
                ret.add(new Order(direction, property, nullHandling));
            }else {
                ret.add(new Order(direction, property));
            }
        }
        return ret;
    }


}
