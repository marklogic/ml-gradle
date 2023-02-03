/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for any object used for ferrying JSON around with Jackson.
 */
@JsonIgnoreProperties
public abstract class ApiObject {

    private ObjectMapper objectMapper;

    /**
     * Intended as a convenience method for a user to find out all the property names of an API object from within the
     * context of e.g. Groovy Shell.
     *
     * @return
     */
    @JsonIgnore
    public List<String> getPropertyNames() {
        BeanWrapper bw = new BeanWrapperImpl(this);
        List<String> list = new ArrayList<>();
        for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
            if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                list.add(pd.getName());
            }
        }
        return list;
    }

    @JsonIgnore
    public String getJson() {
        ObjectMapper mapper = getObjectMapper();
        if (mapper != null) {
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Unable to write object as JSON, cause: " + ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * Syntactic sugar - it's very quick to type e.g. "db.props" in Groovy shell vs "db.propertyNames".
     *
     * @return
     */
    @JsonIgnore
    public List<String> getProps() {
        return getPropertyNames();
    }

    @JsonIgnore
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
