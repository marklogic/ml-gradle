package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.helper.ClientHelper;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateQbvTest extends AbstractSchemasTest {

	@Test
	public void test() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client, "Documents");
		Path path = Paths.get("src", "test", "resources", "qbv-schemas");
		List<DocumentFile> files = loader.loadSchemas(path.toString());
		assertEquals(3, files.size(),
			"Only the TDE templates should be in this list");

		ClientHelper helper = new ClientHelper(client);
		List<String> tdeUris = helper.getUrisInCollection(TdeUtil.TDE_COLLECTION);
		assertEquals(2, tdeUris.size());
		List<String> qbvUris = helper.getUrisInCollection(QbvDocumentFileProcessor.QBV_COLLECTION);
		assertEquals(2, qbvUris.size(), "Both QBVs, and only the QBVs, should be in the QBV collection");
		assertTrue(qbvUris.contains("/qbv/authors.sjs.xml"));
		assertTrue(qbvUris.contains("/qbv/publications.xqy.xml"));

		// TODO - Verify that a query works for the QBV view
		// TODO - Waiting for a real test-app for ml-javaclient-util
//		RowManager rowManager = client.newRowManager();
//		PlanBuilder.ModifyPlan plan = rowManager.newPlanBuilder().fromView("alternate", "authors");
//		rowManager.resultDoc(plan, new JacksonHandle()).get();

		verifyMetadata(qbvUris.get(0), metadata -> {
			DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
			assertPermissionExists(perms, "rest-reader", DocumentMetadataHandle.Capability.READ);
			assertPermissionExists(perms, "rest-writer", DocumentMetadataHandle.Capability.UPDATE);
			assertEquals(2, perms.size());
			DocumentMetadataHandle.DocumentCollections colls = metadata.getCollections();
			assertTrue(colls.contains("http://marklogic.com/xdmp/qbv"));
			assertTrue(colls.contains("col1"));
			assertTrue(colls.contains("col3"));
			assertEquals(3, colls.size());
		});

	}

	@Test
	public void loadBadOptic() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client, "Documents");
		Path path = Paths.get("src", "test", "resources", "qbv-bad-schemas");
		RuntimeException ex = assertThrows(RuntimeException.class, () -> loader.loadSchemas(path.toString()));
		assertTrue(ex.getMessage().contains("Query-Based View generation failed for file:"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("/qbv/bad-authors.sjs"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("ensure your Optic script includes a call to generate a view;"), "Unexpected message: " + ex.getMessage());
	}

	@Test
	public void schemaViewDoesNotExist() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client, "Documents");
		Path path = Paths.get("src", "test", "resources", "qbv-no-tde-schemas");
		RuntimeException ex = assertThrows(RuntimeException.class, () -> loader.loadSchemas(path.toString()));
		assertTrue(ex.getMessage().contains("Query-Based View generation failed for file:"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("/qbv/books.sjs"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("Server Message: SQL-TABLENOTFOUND: plan.generateView(plan.sparql(\"\"), \"alternate\", \"books\") -- Unknown table: Table 'Medical.Books' not found"), "Unexpected message: " + ex.getMessage());
	}

	@Test
	public void emptyDirectories() {
		DefaultSchemasLoader loader = new DefaultSchemasLoader(client, "Documents");
		Path path = Paths.get("src", "test", "resources", "qbv-empty-schemas");
		List<DocumentFile> files = loader.loadSchemas(path.toString());
		assertEquals(0, files.size());
		ClientHelper helper = new ClientHelper(client);
		List<String> qbvUris = helper.getUrisInCollection(QbvDocumentFileProcessor.QBV_COLLECTION);
		assertEquals(0, qbvUris.size());
	}

	private void verifyMetadata(String uri, Consumer<DocumentMetadataHandle> verifier) {
		verifier.accept(client.newJSONDocumentManager().readMetadata(uri, new DocumentMetadataHandle()));
	}

	private void assertPermissionExists(DocumentMetadataHandle.DocumentPermissions perms, String role,
										DocumentMetadataHandle.Capability capability) {
		assertTrue(perms.containsKey(role), "No permissions for role: " + role);
		assertTrue(perms.get(role).contains(capability),
			"Capability " + capability + " for role " + role + " not found");
	}
}
