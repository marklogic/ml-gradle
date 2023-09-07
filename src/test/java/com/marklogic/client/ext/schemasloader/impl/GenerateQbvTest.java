package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.helper.ClientHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateQbvTest extends AbstractSchemasTest {

	private DefaultSchemasLoader loader;

	@BeforeEach
	void beforeEach() {
		// As a sanity check, verify that QBVs get generated when we tell DSL not to validate TDEs.
		loader = new DefaultSchemasLoader(client, newContentClient(), false);
	}

	@Test
	public void test() {
		Path path = Paths.get("src", "test", "resources", "qbv-schemas");
		List<DocumentFile> files = loader.loadSchemas(path.toString());
		assertEquals(2, files.size(), "Only the TDE templates should be in this list");

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

		String uri = qbvUris.get(0);
		verifyCollections(uri, "http://marklogic.com/xdmp/qbv", "col1", "col3");
		verifyPermissions(uri, "rest-reader", "read", "rest-writer", "update");
	}

	@Test
	public void loadBadOptic() {
		Path path = Paths.get("src", "test", "resources", "qbv-bad-schemas");
		RuntimeException ex = assertThrows(RuntimeException.class, () -> loader.loadSchemas(path.toString()));
		assertTrue(ex.getMessage().contains("Query-Based View generation failed for file:"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("/qbv/bad-authors.sjs"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("ensure your Optic script includes a call to generate a view;"), "Unexpected message: " + ex.getMessage());
	}

	@Test
	public void schemaViewDoesNotExist() {
		Path path = Paths.get("src", "test", "resources", "qbv-no-tde-schemas");
		RuntimeException ex = assertThrows(RuntimeException.class, () -> loader.loadSchemas(path.toString()));
		assertTrue(ex.getMessage().contains("Query-Based View generation failed for file:"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("/qbv/books.sjs"), "Unexpected message: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("Server Message: SQL-TABLENOTFOUND: plan.generateView(plan.sparql(\"\"), \"alternate\", \"books\") -- Unknown table: Table 'Medical.Books' not found"), "Unexpected message: " + ex.getMessage());
	}

	@Test
	public void emptyDirectories() {
		Path path = Paths.get("src", "test", "resources", "qbv-empty-schemas");
		List<DocumentFile> files = loader.loadSchemas(path.toString());
		assertEquals(0, files.size());
		ClientHelper helper = new ClientHelper(client);
		List<String> qbvUris = helper.getUrisInCollection(QbvDocumentFileProcessor.QBV_COLLECTION);
		assertEquals(0, qbvUris.size());
	}
}
