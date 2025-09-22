/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

/**
 * This class shields callers from the different return types in Spring 5 and Spring 6 when getting a status code object
 * from a Spring response or exception. This supports a use case where either ml-gradle or ml-app-deployer is on the
 * classpath along with spring-web 6.x as well. Examples of this include a Gradle buildscript that uses ml-gradle and
 * a plugin that depends on Spring 6, or a Spring Boot middle tier that needs to use ml-app-deployer.
 */
public abstract class SpringWebUtil {

	public static int getHttpStatusCode(ResponseEntity<?> response) {
		return response.getStatusCode().value();
	}

	public static HttpStatus getHttpStatus(ResponseEntity<?> response) {
		return HttpStatus.valueOf(getHttpStatusCode(response));
	}

	public static int getHttpStatusCode(RestClientResponseException ex) {
		return ex.getStatusCode().value();
	}

	public static HttpStatus getHttpStatus(RestClientResponseException ex) {
		return HttpStatus.valueOf(getHttpStatusCode(ex));
	}

	private SpringWebUtil() {
	}
}
