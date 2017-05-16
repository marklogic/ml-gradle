package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.AbstractIntegrationTest;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.batch.SimpleDocumentWriteOperation;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.ext.datamovement.QueryBatcherTemplate;
import com.marklogic.client.helper.ClientHelper;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ManageCollectionsTest extends AbstractIntegrationTest {

	private static final String COLLECTION = "modify-query-collections-test";

	@Test
	public void setThenAddThenRemove() {
		QueryBatcherTemplate qbt = new QueryBatcherTemplate(newClient("Documents"));

		// Clear out the test documents
		qbt.applyOnDocuments(new DeleteListener(), "1.xml", "2.xml");

		// Insert documents
		RestBatchWriter writer = new RestBatchWriter(client, false);
		writer.write(Arrays.asList(
			new SimpleDocumentWriteOperation("1.xml", "<one/>", COLLECTION),
			new SimpleDocumentWriteOperation("2.xml", "<two/>", COLLECTION)
		));
		writer.waitForCompletion();

		// Set collections
		qbt.applyOnCollections(new SetCollectionsListener(COLLECTION, "red"), COLLECTION);
		assertUriInCollections("1.xml", COLLECTION, "red");
		assertUriInCollections("2.xml", COLLECTION, "red");

		// Add collections
		qbt.applyOnCollections(new AddCollectionsListener("blue", "green"), COLLECTION);
		assertUriInCollections("1.xml", COLLECTION, "red", "blue", "green");
		assertUriInCollections("2.xml", COLLECTION, "red", "blue", "green");

		// Remove queryCollections
		qbt.applyOnCollections(new RemoveCollectionsListener("red", "blue", "green"), COLLECTION);
		assertUriInCollections("1.xml", COLLECTION);
		assertUriInCollections("2.xml", COLLECTION);
	}

	private void assertUriInCollections(String uri, String... collections) {
		ClientHelper clientHelper = new ClientHelper(client);
		List<String> list = clientHelper.getCollections(uri);
		for (String coll : collections) {
			assertTrue(list.contains(coll));
		}
		assertEquals(collections.length, list.size());
	}
}
