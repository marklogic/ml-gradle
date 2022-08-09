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
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client, "Documents");
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
