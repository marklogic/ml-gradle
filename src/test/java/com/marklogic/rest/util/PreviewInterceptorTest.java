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
package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class PreviewInterceptorTest  {

	private PreviewInterceptor interceptor = new PreviewInterceptor(null);

	@Test
	public void hasError() {
		assertTrue(interceptor.hasError(HttpStatus.BAD_REQUEST));
		assertFalse(interceptor.hasError(HttpStatus.NOT_FOUND),
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
