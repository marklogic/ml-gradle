package com.marklogic.client.ext.datamovement;

import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.batch.SimpleDocumentWriteOperation;
import com.marklogic.client.ext.datamovement.job.DeleteCollectionsJob;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for making it easier to test DMSDK listeners and consumers.
 */
public abstract class AbstractDataMovementTest extends AbstractIntegrationTest {

	protected final static String COLLECTION = "data-movement-test";

	protected final static String FIRST_URI = "/test/dmsdk-test-1.xml";
	protected final static String SECOND_URI = "/test/dmsdk-test-2.xml";

	protected QueryBatcherTemplate queryBatcherTemplate;

	@Before
	public void setup() {
		queryBatcherTemplate = new QueryBatcherTemplate(newClient("Documents"));
		queryBatcherTemplate.setJobName("manage-collections-test");
		queryBatcherTemplate.setBatchSize(1);
		queryBatcherTemplate.setThreadCount(2);

		queryBatcherTemplate.applyOnDocumentUris(new DeleteListener(), FIRST_URI, SECOND_URI);
		new DeleteCollectionsJob(COLLECTION).run(client);

		writeDocuments(FIRST_URI, SECOND_URI);
	}

	protected void writeDocuments(String... uris) {
		RestBatchWriter writer = new RestBatchWriter(client, false);
		List<DocumentWriteOperation> list = new ArrayList<>();
		for (String uri : uris) {
			list.add(new SimpleDocumentWriteOperation(uri, "<test>" + uri + "</test>", COLLECTION));
		}
		writer.write(list);
		writer.waitForCompletion();
	}

}
