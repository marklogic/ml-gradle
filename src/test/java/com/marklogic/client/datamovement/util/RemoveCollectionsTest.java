package com.marklogic.client.datamovement.util;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.ext.datamovement.listener.AddCollectionsListener;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryDefinition;

public class RemoveCollectionsTest extends LoggingObject {

	public static void main(String[] args) {
		new RemoveCollectionsTest().run();
	}

	public void run() {
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "chs-content",
			new DatabaseClientFactory.DigestAuthContext("admin", "admin"));

		DataMovementManager dmm = client.newDataMovementManager();

		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryDefinition queryDef = queryMgr.newStructuredQueryBuilder().collection("list");

		QueryBatcher batcher = dmm.newQueryBatcher(queryDef);
		batcher.
			withConsistentSnapshot().
			withBatchSize(20).
			withThreadCount(10).
			onQueryFailure(new QueryFailureListener() {
				@Override
				public void processFailure(QueryBatchException e) {
					System.err.println(e.getMessage());
				}
			}).
			onUrisReady(new AddCollectionsListener("slappy", "slappy2"));
		dmm.startJob(batcher);
		batcher.awaitCompletion();

		client.release();
	}
}
