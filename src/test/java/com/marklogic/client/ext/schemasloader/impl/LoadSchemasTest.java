package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.helper.ClientHelper;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

public class LoadSchemasTest extends AbstractIntegrationTest {

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
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "schemas").toString());
		assertEquals(5, files.size());

		ClientHelper helper = new ClientHelper(client);
		List<String> uris = helper.getUrisInCollection("http://marklogic.com/xdmp/tde");
		assertEquals("The non-tde/ruleset.txt file should not be in the TDE collection", 4, uris.size());
		assertTrue(uris.contains("/child/child.tdej"));
		assertTrue(uris.contains("/child/grandchild/grandchild.tdex"));
		assertTrue(uris.contains("/parent.tdex"));
		assertTrue(uris.contains("/tde/ruleset.txt"));
	}
}
