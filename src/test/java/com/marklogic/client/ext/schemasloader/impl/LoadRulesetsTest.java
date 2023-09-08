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
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.helper.ClientHelper;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoadRulesetsTest extends AbstractSchemasTest {

	@Test
	public void test() {
		// Pass in a TDE validation database to ensure that TDE validation doesn't happen for these files
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client, newContentClient());
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "rulesets", "collection-test").toString());
		assertEquals(2, files.size());

		ClientHelper helper = new ClientHelper(client);

		List<String> collections = helper.getCollections("/ruleset1.xml");
		assertEquals(1, collections.size());
		assertTrue(collections.contains("ruleset-abc"));

		collections = helper.getCollections("/ruleset2.json");
		assertEquals(2, collections.size());
		assertTrue(collections.contains("ruleset-abc"));
		assertTrue(collections.contains("ruleset-xyz"));

		DocumentMetadataHandle.DocumentPermissions perms = helper.getMetadata("/ruleset1.xml").getPermissions();
		assertEquals(DocumentMetadataHandle.Capability.READ, perms.get("rest-admin").iterator().next());
		assertEquals(DocumentMetadataHandle.Capability.UPDATE, perms.get("manage-admin").iterator().next());
	}
}
