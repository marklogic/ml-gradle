package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.helper.ClientHelper;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoadSchemasTest extends AbstractSchemasTest {

	@Test
	public void test() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client);
		RestBatchWriter writer = (RestBatchWriter) loader.getBatchWriter();
		assertEquals(1, writer.getThreadCount(), "Should default to 1 so that any error from loading a document " +
			"into a schemas database is immediately thrown to the client");

		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "schemas").toString());
		assertEquals(5, files.size());

		ClientHelper helper = new ClientHelper(client);
		List<String> uris = helper.getUrisInCollection(TdeUtil.TDE_COLLECTION);
		assertEquals(4, uris.size(), "The non-tde/ruleset.txt file should not be in the TDE collection");
		assertTrue(uris.contains("/child/child.tdej"));
		assertTrue(uris.contains("/child/grandchild/grandchild.tdex"));
		assertTrue(uris.contains("/parent.tdex"));
		assertTrue(uris.contains("/tde/ruleset.txt"));
	}

	@Test
	public void testTemplateBatchInsert() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client, "Documents");
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "good-schemas", "originals").toString());
		assertEquals(2, files.size());

		ClientHelper helper = new ClientHelper(client);
		List<String> uris = helper.getUrisInCollection(TdeUtil.TDE_COLLECTION);
		assertEquals(2, uris.size());
		assertTrue(uris.contains("/tde/good-schema.json"));
		assertTrue(uris.contains("/tde/good-schema.xml"));

		DocumentMetadataHandle handle = helper.getMetadata("/tde/good-schema.json");
		assertTrue(handle.getPermissions().get("rest-reader").contains(DocumentMetadataHandle.Capability.READ),
			"Permissions defined in permissions.properties should be applied on the document");
		assertTrue(handle.getPermissions().get("rest-writer").contains(DocumentMetadataHandle.Capability.UPDATE),
			"Permissions defined in permissions.properties should be applied on the document");

		handle = helper.getMetadata("/tde/good-schema.xml");
		assertTrue(handle.getPermissions().get("rest-reader").contains(DocumentMetadataHandle.Capability.READ),
			"Permissions defined in permissions.properties should be applied on the document");
		assertTrue(handle.getPermissions().get("rest-writer").contains(DocumentMetadataHandle.Capability.UPDATE),
			"Permissions defined in permissions.properties should be applied on the document");
	}

	@Test
	public void invalidClientAndNoFilesToLoad() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(newClient("invalid-database-doesnt-exist"));
		List<DocumentFile> files = loader.loadSchemas(Paths.get("src", "test", "resources", "no-schemas").toString());
		assertEquals(0, files.size(),
			"When there aren't any files to load, then no error should be thrown when the client is invalid (which in " +
				"this scenario is due to a missing database); instead, an empty list should be returned");
	}

	@Test
	public void invalidClientWithFilesToLoad() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(newClient("invalid-database-doesnt-exist"));
		FailedRequestException ex = assertThrows(FailedRequestException.class,
			() -> loader.loadSchemas(Paths.get("src", "test", "resources", "good-schemas").toString()));

		String message = ex.getServerMessage();
		assertTrue(message.startsWith("XDMP-NOSUCHDB: No such database invalid-database-doesnt-exist"),
			"Because there are files to load and the client points to a database that doesn't exist, an error " +
				"should be thrown with no files loaded; actual message: " + message);
	}
}
