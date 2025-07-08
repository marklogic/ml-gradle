/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;

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
	 * All the default settings here originated in the API.java class. Then #187 resulted in comments being allowed.
	 * Most of the settings only matter for when Resource objects are being written to JSON via Jackson annotations.
	 */
	private static void initializeObjectMapper() {
		objectMapper = new ObjectMapper();
		objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);

		// We want lower-case-hyphenated ("kebab") for all JSON field names, as that's what the Manage API
		// standardizes on.
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

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
