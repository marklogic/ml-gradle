package com.marklogic.client.ext.datamovement;

import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.ext.batch.DataMovementBatchWriter;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.batch.SimpleDocumentWriteOperation;
import com.marklogic.client.ext.datamovement.job.DeleteCollectionsJob;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
		DataMovementBatchWriter writer = new DataMovementBatchWriter(client);
		writer.initialize();
		List<DocumentWriteOperation> list = new ArrayList<>();
		for (String uri : uris) {
			list.add(new SimpleDocumentWriteOperation(uri, "<test>" + uri + "</test>", COLLECTION));
		}
		writer.write(list);
		writer.waitForCompletion();
	}

	protected void assertZipFileContainsEntryNames(File file, String... names) {
		Set<String> entryNames = new HashSet<>();
		try {
			ZipFile zipFile = new ZipFile(file);
			try {
				Enumeration<?> entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry) entries.nextElement();
					entryNames.add(zipEntry.getName());
				}
			} finally {
				zipFile.close();
			}
		} catch (IOException ie) {
			throw new RuntimeException(ie);
		}

		logger.info("Entry names: " + entryNames);
		for (String name : names) {
			assertTrue(entryNames.contains(name));
		}
		assertEquals(names.length, entryNames.size());
	}

}
