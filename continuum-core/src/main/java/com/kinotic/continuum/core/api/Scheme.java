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

package com.kinotic.continuum.core.api;

import org.apache.commons.lang3.Validate;

/**
 *
 * Created by Navid Mitchell on 5/1/20
 */
public enum Scheme {
    SERVICE("srv"),
    STREAM("stream");


    private String raw;

    Scheme(String raw) {
        this.raw = raw;
    }

    public String raw() {
        return raw;
    }

    public static Scheme create(String raw){
        Validate.notBlank(raw, "raw must not be null or blank");
        Scheme ret;
        switch (raw){
            case "srv":
                ret = SERVICE;
                break;
            case "stream":
                ret = STREAM;
                break;
            default:
                ret = null;
                break;
        }
        if(ret == null){
            throw new IllegalArgumentException(raw + " is not a valid Scheme");
        }
        return ret;
    }
}
