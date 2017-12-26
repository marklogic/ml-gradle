package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import com.marklogic.client.ext.datamovement.UrisQueryQueryBatcherBuilder;
import com.marklogic.client.ext.datamovement.job.AddCollectionsJob;
import com.marklogic.client.ext.datamovement.job.RemoveCollectionsJob;
import com.marklogic.client.ext.datamovement.job.SetCollectionsJob;
import com.marklogic.client.ext.datamovement.listener.RemoveCollectionsListener;
import com.marklogic.client.ext.datamovement.listener.SetCollectionsListener;
import com.marklogic.client.ext.helper.ClientHelper;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

public class ManageCollectionsTest extends AbstractDataMovementTest {

	@Test
	public void setThenAddThenRemove() {
		new SetCollectionsJob(COLLECTION, "red").setWhereCollections(COLLECTION).run(client);
		assertUriInCollections(FIRST_URI, COLLECTION, "red");
		assertUriInCollections(SECOND_URI, COLLECTION, "red");

		new AddCollectionsJob("blue", "green").setWhereCollections(COLLECTION).run(client);
		assertUriInCollections(FIRST_URI, COLLECTION, "red", "blue", "green");
		assertUriInCollections(SECOND_URI, COLLECTION, "red", "blue", "green");

		// Remove collections
		Properties props = new Properties();
		props.setProperty("collections", "red");
		RemoveCollectionsJob removeCollectionsJob = new RemoveCollectionsJob();
		List<String> messages = removeCollectionsJob.configureJob(props);
		assertTrue("Should not have any validation messages: " + messages, messages.isEmpty());
		removeCollectionsJob.run(client);

		props.setProperty("collections", "blue,green");
		props.setProperty("whereCollections", COLLECTION);
		removeCollectionsJob = new RemoveCollectionsJob();
		messages = removeCollectionsJob.configureJob(props);
		assertTrue("Should not have any validation messages: " + messages, messages.isEmpty());
		removeCollectionsJob.run(client);
		assertUriInCollections(FIRST_URI, COLLECTION);
		assertUriInCollections(SECOND_URI, COLLECTION);

		// Set via URI pattern
		new SetCollectionsJob(COLLECTION, "red").setWhereUriPattern("/test/dmsdk-test-*.xml").run(client);
		assertUriInCollections(FIRST_URI, COLLECTION, "red");
		assertUriInCollections(SECOND_URI, COLLECTION, "red");

		queryBatcherTemplate.applyOnUriPattern(new RemoveCollectionsListener("red"), "/test/dmsdk-test-2*");
		assertUriInCollections(FIRST_URI, COLLECTION, "red");
		assertUriNotInCollections(SECOND_URI, "red");

		// Set via XQuery URIs query
		String xquery = String.format("cts:document-query(('%s', '%s'))", FIRST_URI, SECOND_URI);
		new SetCollectionsJob(COLLECTION, "green").setWhereUrisQuery(xquery).run(client);
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
