package com.marklogic.client.ext.datamovement.consumer;

import com.marklogic.client.datamovement.ExportListener;
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

public class WriteToZipFileTest extends AbstractDataMovementTest {

	private File exportFile = new File("build/write-to-zip-file-test.zip");
	private WriteToZipConsumer writeToZipConsumer;

	@Before
	public void setupConsumer() {
		exportFile.getParentFile().mkdirs();
		writeToZipConsumer = new WriteToZipConsumer(exportFile);
	}

	@Test
	public void test() {
		exportToZip();
		assertZipFileContainsEntryNames(FIRST_URI, SECOND_URI);
	}

	@Test
	public void flattenUri() {
		writeToZipConsumer.setFlattenUri(true);
		exportToZip();
		assertZipFileContainsEntryNames("dmsdk-test-1.xml", "dmsdk-test-2.xml");
	}

	@Test
	public void uriPrefix() {
		final String prefix = "/example";
		writeToZipConsumer.setUriPrefix(prefix);
		exportToZip();
		assertZipFileContainsEntryNames(prefix + FIRST_URI, prefix + SECOND_URI);
	}
	private void exportToZip() {
		ExportListener listener = new ExportListener();
		listener.onDocumentReady(writeToZipConsumer);
		queryBatcherTemplate.applyOnCollections(listener, COLLECTION);
		try {
			writeToZipConsumer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void assertZipFileContainsEntryNames(String... names) {
		Set<String> entryNames = new HashSet<>();
		try {
			ZipFile zipFile = new ZipFile(exportFile);
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
