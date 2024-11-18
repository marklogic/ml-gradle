/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
		// This method is deprecated in Spring 6.0 and slated to be removed in Spring 7.0. But it can be safely used
		// by both Spring 5.x and 6.x. We'll need to revisit this when we want to support ml-gradle when Spring 7.0 is
		// on the classpath as well.
		return response.getStatusCodeValue();
	}

	public static HttpStatus getHttpStatus(ResponseEntity<?> response) {
		return HttpStatus.valueOf(getHttpStatusCode(response));
	}

	public static int getHttpStatusCode(RestClientResponseException ex) {
		// This method is deprecated in Spring 6.0 and slated to be removed in Spring 7.0. But it can be safely used
		// by both Spring 5.x and 6.x. We'll need to revisit this when we want to support ml-gradle when Spring 7.0 is
		// on the classpath as well.
		return ex.getRawStatusCode();
	}

	public static HttpStatus getHttpStatus(RestClientResponseException ex) {
		return HttpStatus.valueOf(getHttpStatusCode(ex));
	}

	private SpringWebUtil() {
	}
}
