package com.marklogic.client.schemasloader.impl;

import com.marklogic.client.AbstractIntegrationTest;
import com.marklogic.client.file.DocumentFile;
import com.marklogic.client.helper.ClientHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LoadSchemasTest extends AbstractIntegrationTest {

	/**
	 * Wipes out documents matching the ones we intend to load - it's assumed you're not using the Schemas database for
	 * anything besides ad hoc testing like this.
	 */
	@Before
	public void setup() {
		client = newClient("Schemas");
		client.newServerEval().xquery("cts:uri-match('*.tde*') ! xdmp:document-delete(.)").eval();
	}

	@Test
	public void test() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client);
		List<DocumentFile> files = loader.loadSchemas("src/test/resources/schemas");
		assertEquals(3, files.size());

		ClientHelper helper = new ClientHelper(client);
		List<String> uris = helper.getUrisInCollection("http://marklogic.com/xdmp/tde");
		assertTrue(uris.contains("/child/child.tdej"));
		assertTrue(uris.contains("/child/grandchild/grandchild.tdex"));
		assertTrue(uris.contains("/parent.tdex"));
	}
}
