package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import com.marklogic.client.ext.datamovement.UrisQueryQueryBatcherBuilder;
import com.marklogic.client.ext.helper.ClientHelper;
import org.junit.Test;

import java.util.List;

public class ManageCollectionsTest extends AbstractDataMovementTest {

	@Test
	public void setThenAddThenRemove() {
		// Set collections
		queryBatcherTemplate.applyOnCollections(new SetCollectionsListener(COLLECTION, "red"), COLLECTION);
		assertUriInCollections(FIRST_URI, COLLECTION, "red");
		assertUriInCollections(SECOND_URI, COLLECTION, "red");

		// Add collections
		queryBatcherTemplate.applyOnCollections(new AddCollectionsListener("blue", "green"), COLLECTION);
		assertUriInCollections(FIRST_URI, COLLECTION, "red", "blue", "green");
		assertUriInCollections(SECOND_URI, COLLECTION, "red", "blue", "green");

		// Remove collections
		queryBatcherTemplate.applyOnCollections(new RemoveCollectionsListener("red", "blue", "green"), COLLECTION);
		assertUriInCollections(FIRST_URI, COLLECTION);
		assertUriInCollections(SECOND_URI, COLLECTION);

		// Set via URI pattern
		queryBatcherTemplate.applyOnUriPattern(new SetCollectionsListener(COLLECTION, "red"), "/test/dmsdk-test-*.xml");
		assertUriInCollections(FIRST_URI, COLLECTION, "red");
		assertUriInCollections(SECOND_URI, COLLECTION, "red");

		queryBatcherTemplate.applyOnUriPattern(new RemoveCollectionsListener("red"), "/test/dmsdk-test-2*");
		assertUriInCollections(FIRST_URI, COLLECTION, "red");
		assertUriNotInCollections(SECOND_URI, "red");

		// Set via XQuery URIs query
		String xquery = String.format("cts:document-query(('%s', '%s'))", FIRST_URI, SECOND_URI);
		queryBatcherTemplate.applyOnUrisQuery(new SetCollectionsListener(COLLECTION, "green"), xquery);
		assertUriInCollections(FIRST_URI, COLLECTION, "green");
		assertUriInCollections(SECOND_URI, COLLECTION, "green");

		// Set via Javascript URIs query
		String javascript = String.format("cts.documentQuery(['%s', '%s'])", FIRST_URI, SECOND_URI);
		queryBatcherTemplate.applyOnUrisQuery(new SetCollectionsListener(COLLECTION, "blue"), javascript);
		assertUriInCollections(FIRST_URI, COLLECTION, "blue");
		assertUriInCollections(SECOND_URI, COLLECTION, "blue");

		// Set via full XQuery query
		xquery = String.format("cts:uris((), (), cts:document-query(('%s', '%s')))", FIRST_URI, SECOND_URI);
		queryBatcherTemplate.applyOnUrisQuery(new SetCollectionsListener(COLLECTION, "green"), xquery);
		assertUriInCollections(FIRST_URI, COLLECTION, "green");
		assertUriInCollections(SECOND_URI, COLLECTION, "green");

		// Set via full Javascript query
		javascript = String.format("cts.uris('', null, cts.documentQuery(['%s', '%s']))", FIRST_URI, SECOND_URI);
		queryBatcherTemplate.applyOnUrisQuery(new SetCollectionsListener(COLLECTION, "blue"), javascript);
		assertUriInCollections(FIRST_URI, COLLECTION, "blue");
		assertUriInCollections(SECOND_URI, COLLECTION, "blue");

		// Test out a failure listener
		xquery = String.format("cts:document-query(('%s', '%s'))", FIRST_URI, SECOND_URI);
		UrisQueryQueryBatcherBuilder builder = new UrisQueryQueryBatcherBuilder(xquery);
		builder.setWrapQueryIfAppropriate(false); // this will result in a bad query
		queryBatcherTemplate.apply(new SetCollectionsListener(COLLECTION, "green"), builder);
		assertUriInCollections(FIRST_URI, COLLECTION, "blue");
		assertUriInCollections(SECOND_URI, COLLECTION, "blue");

	}

	private void assertUriInCollections(String uri, String... collections) {
		ClientHelper clientHelper = new ClientHelper(client);
		List<String> list = clientHelper.getCollections(uri);
		for (String coll : collections) {
			assertTrue(list.contains(coll));
		}
		assertEquals(collections.length, list.size());
	}

	private void assertUriNotInCollections(String uri, String... collections) {
		ClientHelper clientHelper = new ClientHelper(client);
		List<String> list = clientHelper.getCollections(uri);
		for (String coll : collections) {
			assertFalse(list.contains(coll));
		}
	}
}
