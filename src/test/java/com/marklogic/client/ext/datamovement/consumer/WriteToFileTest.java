package com.marklogic.client.ext.datamovement.consumer;

import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import com.marklogic.client.ext.datamovement.listener.XmlOutputListener;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileWriter;

public class WriteToFileTest extends AbstractDataMovementTest {

	@Test
	public void test() throws Exception {
		// Do a quick test of exporting the data
		ExportListener exportListener = new ExportListener();
		File exportDir = new File("build/export-test");
		exportDir.mkdirs();
		WriteToFileConsumer l = new WriteToFileConsumer(exportDir);
		exportListener.onDocumentReady(l);
		queryBatcherTemplate.applyOnCollections(exportListener, COLLECTION);
		assertTrue(new File(exportDir, FIRST_URI).exists());
		assertTrue(new File(exportDir, SECOND_URI).exists());

		// Now try exporting all of the documents to one file
		File exportFile = new File(exportDir, "exportToFileTest.xml");
		FileWriter fileWriter = new FileWriter(exportFile);
		ExportToWriterListener exportToWriterListener = new ExportToWriterListener(fileWriter);
		exportToWriterListener.onGenerateOutput(new XmlOutputListener());
		queryBatcherTemplate.applyOnCollections(exportToWriterListener, COLLECTION);
		fileWriter.close();
		String exportedXml = new String(FileCopyUtils.copyToByteArray(exportFile));
		assertEquals("<one/><two/>", exportedXml);
	}
}
