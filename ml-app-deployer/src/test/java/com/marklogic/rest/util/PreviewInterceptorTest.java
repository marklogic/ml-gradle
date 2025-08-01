/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PreviewInterceptorTest  {

	private PreviewInterceptor interceptor = new PreviewInterceptor(null);

	@Test
	public void hasError() throws IOException {
		final byte[] fakeData = "{}".getBytes(Charset.defaultCharset());

		assertTrue(interceptor.hasError(new MockClientHttpResponse(fakeData, HttpStatus.BAD_REQUEST)));
		assertFalse(interceptor.hasError(new MockClientHttpResponse(fakeData, HttpStatus.NOT_FOUND)),
			"A 404 isn't regarded as an error because while doing a preview, " +
				"it may occur because a new resource, like a database, needs to be created and a GET is " +
				"being made on a resource specific to that database, like a CPF pipeline");
	}

	@Test
	public void removePassword() {
		ObjectNode node = ObjectMapperFactory.getObjectMapper().createObjectNode();
		node.set("password", new TextNode("changeme"));

		assertTrue(node.has("password"));

		interceptor.modifyPayloadBeforePreview(node);

		assertFalse(node.has("password"));
	}

	@Test
	public void removeSchemaName() {
		ObjectNode node = ObjectMapperFactory.getObjectMapper().createObjectNode();
		node.set("view-name", new TextNode("abc"));
		node.set("schema-name", new TextNode("xyz"));

		assertTrue(node.has("view-name"));
		assertTrue(node.has("schema-name"));

		interceptor.modifyPayloadBeforePreview(node);

		assertTrue(node.has("view-name"));
		assertFalse(node.has("schema-name"));
	}
}
