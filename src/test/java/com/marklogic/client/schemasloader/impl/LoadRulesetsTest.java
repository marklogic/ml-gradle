package com.marklogic.client.schemasloader.impl;

import com.marklogic.client.AbstractIntegrationTest;
import com.marklogic.client.file.DocumentFile;
import com.marklogic.client.helper.ClientHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by rrudin on 2/21/2017.
 */
public class LoadRulesetsTest extends AbstractIntegrationTest {

	/**
	 * Wipes out documents matching the ones we intend to load - it's assumed you're not using the Schemas database for
	 * anything besides ad hoc testing like this.
	 */
	@Before
	public void setup() {
		client = newClient("Schemas");
		client.newServerEval().xquery("cts:uri-match('/ruleset*.*') ! xdmp:document-delete(.)").eval();
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

	}
}
