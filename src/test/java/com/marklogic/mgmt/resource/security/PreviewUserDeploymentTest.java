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
package com.marklogic.mgmt.resource.security;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.rest.util.PreviewInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreviewUserDeploymentTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		UserManager mgr = new UserManager(manageClient);

		String json =
			"  {\"user-name\":\"sample-app-user1\"," +
				"  \"password\": \"password\"," +
				"  \"description\": \"Description\"," +
				"  \"role\": [\"rest-reader\"]}";

		try {
			// Preview the deployment before the user is created
			PreviewInterceptor interceptor = new PreviewInterceptor(manageClient);
			manageClient.getRestTemplate().getInterceptors().add(interceptor);
			mgr.save(json);
			assertFalse(mgr.exists("sample-app-user1"));

			ArrayNode results = interceptor.getResults();
			assertEquals(1, results.size());
			ObjectNode result = (ObjectNode) results.get(0);
			String message = result.get("message").asText();
			assertTrue(message.startsWith("Will create new resource at: "));
			assertTrue(message.endsWith("/manage/v2/users"));
			assertTrue(result.has("resource"), "The resource to be created should be in the result object");

			// Now create the user
			manageClient.getRestTemplate().getInterceptors().clear();
			mgr.save(json);
			assertTrue(mgr.exists("sample-app-user1"));

			interceptor = new PreviewInterceptor(manageClient);
			manageClient.getRestTemplate().getInterceptors().add(interceptor);
			String updateJson =
				"  {\"user-name\":\"sample-app-user1\"," +
					"  \"password\": \"password\"," +
					"  \"description\": \"Updated description\"," +
					"  \"role\": [\"rest-writer\", \"rest-reader\"]}";
			mgr.save(updateJson);

			results = interceptor.getResults();
			assertEquals(1, results.size());
			result = (ObjectNode) results.get(0);
			message = result.get("message").asText();
			assertTrue(message.startsWith("Will update resource at: "));
			assertTrue(message.endsWith("/manage/v2/users/sample-app-user1/properties"));
			assertNotNull(result.get("existingResource"));

			ArrayNode patch = (ArrayNode)result.get("patch");
			System.out.println(patch);
			assertEquals(2, patch.size());
			ObjectNode firstChange = (ObjectNode)patch.get(0);
			assertEquals("replace", firstChange.get("op").asText());
			assertEquals("/description", firstChange.get("path").asText());
			assertEquals("Updated description", firstChange.get("value").asText());

			ObjectNode secondChange = (ObjectNode)patch.get(1);
			assertEquals("add", secondChange.get("op").asText());
			assertEquals("/role/1", secondChange.get("path").asText());
			assertEquals("rest-writer", secondChange.get("value").asText());

		} finally {
			manageClient.getRestTemplate().getInterceptors().clear();
			mgr.delete(json);
		}
	}
}

