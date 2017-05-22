package com.marklogic.client.schemasloader.impl;

import com.marklogic.client.AbstractIntegrationTest;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.helper.ClientHelper;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LoadRulesetsTest extends AbstractIntegrationTest {

	/**
	 * Wipes out the Schemas database - it's assumed you're not using the Schemas database for
	 * anything besides ad hoc testing like this.
	 */
	@Before
	public void setup() {
		client = newClient("Schemas");
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
	}

	@Test
	public void test() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client);
		List<DocumentFile> files = loader.loadSchemas("src/test/resources/rulesets/collection-test");
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
		assertEquals("Should have the two default perms plus the two custom ones", 4, perms.size());
		assertEquals(DocumentMetadataHandle.Capability.READ, perms.get("rest-reader").iterator().next());
		assertEquals(DocumentMetadataHandle.Capability.UPDATE, perms.get("rest-writer").iterator().next());
		assertEquals(DocumentMetadataHandle.Capability.READ, perms.get("rest-admin").iterator().next());
		assertEquals(DocumentMetadataHandle.Capability.UPDATE, perms.get("manage-admin").iterator().next());
	}
}
