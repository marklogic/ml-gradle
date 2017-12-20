package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import com.marklogic.client.ext.datamovement.consumer.WriteDocumentToFileConsumer;
import org.junit.Test;

import java.io.File;

public class ExportDocumentsToFilesTest extends AbstractDataMovementTest {

	@Test
	public void test() {
		File exportDir = new File("build/export-test/" + System.currentTimeMillis());
		new SimpleExportJob(new WriteDocumentToFileConsumer(exportDir))
			.setWhereCollections(COLLECTION)
			.run(client);
		assertTrue(new File(exportDir, FIRST_URI).exists());
		assertTrue(new File(exportDir, SECOND_URI).exists());
	}
}

