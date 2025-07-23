/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.ext.batch.DataMovementBatchWriter;
import com.marklogic.client.ext.datamovement.job.DeleteCollectionsJob;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractDataMovementTest extends AbstractIntegrationTest {

	protected final static String COLLECTION = "data-movement-test";

	protected final static String FIRST_URI = "/test/dmsdk-test-1.xml";
	protected final static String SECOND_URI = "/test/dmsdk-test-2.xml";

	protected QueryBatcherTemplate queryBatcherTemplate;

	@AfterEach
	public void releaseClientOnTearDown() {
		if (client != null) {
			try {
				client.release();
			} catch (Exception ex) {
				// That's fine, the test probably released it already
			}
		}
	}

	@BeforeEach
	public void setupAbstractDataMovementTest() {
		queryBatcherTemplate = new QueryBatcherTemplate(newClient("Documents"));
		queryBatcherTemplate.setJobName("manage-collections-test");
		queryBatcherTemplate.setBatchSize(1);
		queryBatcherTemplate.setThreadCount(2);

		queryBatcherTemplate.applyOnDocumentUris(new DeleteListener(), FIRST_URI, SECOND_URI);
		new DeleteCollectionsJob(COLLECTION).run(client);

		writeDocuments(FIRST_URI, SECOND_URI);
	}

	protected DatabaseClient newClient(String database) {
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(database);
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);
		return client;
	}

	protected void writeDocuments(String... uris) {
		List<DocumentWriteOperation> list = new ArrayList<>();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add(COLLECTION);
		for (String uri : uris) {
			list.add(new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE, uri,
				metadata, new StringHandle("<test>" + uri + "</test>").withFormat(Format.XML)));
		}
		writeDocuments(list);
	}

	protected void writeDocuments(List<DocumentWriteOperation> writeOperations) {
		DataMovementBatchWriter writer = new DataMovementBatchWriter(client);
		writer.initialize();
		writer.write(writeOperations);
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

		logger.info("Entry names: {}", names);
		for (String name : names) {
			assertTrue(entryNames.contains(name));
		}
		assertEquals(names.length, entryNames.size());
	}

}
