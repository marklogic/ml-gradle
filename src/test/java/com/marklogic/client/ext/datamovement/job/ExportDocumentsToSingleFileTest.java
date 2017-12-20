package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;

public class ExportDocumentsToSingleFileTest extends AbstractDataMovementTest {

	@Test
	public void test() throws Exception {
		File exportDir = new File("build/export-test/" + System.currentTimeMillis());
		File exportFile = new File(exportDir, "exportToFileTest.xml");
		new ExportToFileJob(exportFile).setWhereUris(FIRST_URI, SECOND_URI).run(client);

		String exportedXml = new String(FileCopyUtils.copyToByteArray(exportFile));
		logger.info("Exported XML: " + exportedXml);
		assertTrue(exportedXml.contains("<one/>"));
		assertTrue(exportedXml.contains("<two/>"));
	}
}
