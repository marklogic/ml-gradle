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
package com.marklogic.mgmt.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.marklogic.mgmt.api.LowerCaseWithHyphensStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Static factory class that is intended to be a single point of construction of a Jackson ObjectMapper that is used
 * throughout the ml-app-deployer library. The single ObjectMapper can be customized via an ObjectMapperInitializer
 * instance.
 */
public abstract class ObjectMapperFactory {

	private static ObjectMapper objectMapper;
	private static List<ObjectMapperInitializer> objectMapperInitializers;

	public static void addObjectMapperInitializer(ObjectMapperInitializer initializer) {
		if (objectMapperInitializers == null) {
			objectMapperInitializers = new ArrayList<>();
		}
		objectMapperInitializers.add(initializer);
	}

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			initializeObjectMapper();
		}
		return objectMapper;
	}

	/**
	 * All of the default settings here originated in the API.java class. Then #187 resulted in comments being allowed.
	 * Most of the settings only matter for when Resource objects are being written to JSON via Jackson annotations.
	 */
	private static void initializeObjectMapper() {
		objectMapper = new ObjectMapper();
		objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		objectMapper.setPropertyNamingStrategy(new LowerCaseWithHyphensStrategy());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// This is needed at least for localname on Element instances
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		if (objectMapperInitializers != null) {
			for (ObjectMapperInitializer initializer : objectMapperInitializers) {
				initializer.initializeObjectMapper(objectMapper);
			}
		}
	}
}

interface ObjectMapperInitializer {

	void initializeObjectMapper(ObjectMapper objectMapper);

}
