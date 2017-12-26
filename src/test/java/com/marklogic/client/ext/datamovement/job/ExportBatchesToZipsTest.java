package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

public class ExportBatchesToZipsTest extends AbstractDataMovementTest {

	private File exportDir;
	private ExportBatchesToZipsJob job;
	private Properties props;

	@Before
	public void moreSetup() {
		writeDocuments("/test/three.xml", "/test/four.xml");

		final String exportPath = "build/export-test/" + System.currentTimeMillis();
		props = new Properties();
		props.setProperty("exportPath", exportPath);
		props.setProperty("whereCollections", COLLECTION);
		props.setProperty("batchSize", "2");
		exportDir = new File(exportPath);
		job = new ExportBatchesToZipsJob();
	}

	@Test
	public void defaultSettings() {
		assertTrue(job.configureJob(props).isEmpty());
		job.run(client);

		assertZipFileContainsEntryNames(new File(exportDir, "batch-1.zip"), FIRST_URI, SECOND_URI);
		assertZipFileContainsEntryNames(new File(exportDir, "batch-2.zip"), "/test/three.xml", "/test/four.xml");
	}

	@Test
	public void flattenUriAndAddPrefix() {
		props.setProperty("flattenUri", "true");
		props.setProperty("uriPrefix", "/example/");
		props.setProperty("filenamePrefix", "my-batch-");
		props.setProperty("filenameExtension", ".jar");
		assertTrue(job.configureJob(props).isEmpty());
		job.run(client);

		assertZipFileContainsEntryNames(new File(exportDir, "my-batch-1.jar"),
			"/example/dmsdk-test-1.xml", "/example/dmsdk-test-2.xml");
		assertZipFileContainsEntryNames(new File(exportDir, "my-batch-2.jar"),
			"/example/three.xml", "/example/four.xml");
	}
}
