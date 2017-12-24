package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class ExportBatchesToZipsTest extends AbstractDataMovementTest {

	private File exportDir;
	private ExportBatchesToZipsJob job;

	@Before
	public void moreSetup() {
		writeDocuments("/test/three.xml", "/test/four.xml");

		exportDir = new File("build/export-test/" + System.currentTimeMillis());
		job = new ExportBatchesToZipsJob(exportDir);
		job.setWhereCollections(COLLECTION);
		job.setBatchSize(2);
	}

	@Test
	public void defaultSettings() {
		job.run(client);

		assertZipFileContainsEntryNames(new File(exportDir, "batch-1.zip"), FIRST_URI, SECOND_URI);
		assertZipFileContainsEntryNames(new File(exportDir, "batch-2.zip"), "/test/three.xml", "/test/four.xml");
	}

	@Test
	public void flattenUriAndAddPrefix() {
		job.getExportBatchesToZipsListener()
			.withFlattenUri(true)
			.withUriPrefix("/example/")
			.withFileExtension(".jar");

		job.run(client);

		assertZipFileContainsEntryNames(new File(exportDir, "batch-1.jar"),
			"/example/dmsdk-test-1.xml", "/example/dmsdk-test-2.xml");
		assertZipFileContainsEntryNames(new File(exportDir, "batch-2.jar"),
			"/example/three.xml", "/example/four.xml");
	}
}
