package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExportToZipTest extends AbstractDataMovementTest {

	private File exportFile;
	private ExportToZipJob exportToZipJob;

	@Before
	public void setupConsumer() {
		exportFile = new File("build/export-test/" + System.currentTimeMillis() + "-write-to-zip-file-test.zip");
		exportToZipJob = new ExportToZipJob(exportFile);
		exportToZipJob.setWhereCollections(COLLECTION);
	}

	@Test
	public void test() {
		exportToZip();
		assertZipFileContainsEntryNames(exportFile, FIRST_URI, SECOND_URI);
	}

	@Test
	public void flattenUri() {
		exportToZipJob.getWriteToZipConsumer().setFlattenUri(true);
		exportToZip();
		assertZipFileContainsEntryNames(exportFile, "dmsdk-test-1.xml", "dmsdk-test-2.xml");
	}

	@Test
	public void uriPrefix() {
		final String prefix = "/example";
		exportToZipJob.getWriteToZipConsumer().setUriPrefix(prefix);
		exportToZip();
		assertZipFileContainsEntryNames(exportFile, prefix + FIRST_URI, prefix + SECOND_URI);
	}

	private void exportToZip() {
		exportToZipJob.run(client);
	}

}
