package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.helper.ClientHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoadSchemasTest extends AbstractSchemasTest {

	@Test
	public void test() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client);
		RestBatchWriter writer = (RestBatchWriter)loader.getBatchWriter();
		assertEquals(1, writer.getThreadCount(), "Should default to 1 so that any error from loading a document " +
			"into a schemas database is immediately thrown to the client");

		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "schemas").toString());
		assertEquals(5, files.size());

		ClientHelper helper = new ClientHelper(client);
		List<String> uris = helper.getUrisInCollection("http://marklogic.com/xdmp/tde");
		assertEquals(4, uris.size(), "The non-tde/ruleset.txt file should not be in the TDE collection");
		assertTrue(uris.contains("/child/child.tdej"));
		assertTrue(uris.contains("/child/grandchild/grandchild.tdex"));
		assertTrue(uris.contains("/parent.tdex"));
		assertTrue(uris.contains("/tde/ruleset.txt"));
	}
}
