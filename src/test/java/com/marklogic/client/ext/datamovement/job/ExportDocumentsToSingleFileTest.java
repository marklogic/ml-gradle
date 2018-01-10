package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.Properties;

public class ExportDocumentsToSingleFileTest extends AbstractDataMovementTest {

	@Test
	public void test() throws Exception {
		File exportDir = new File("build/export-test/" + System.currentTimeMillis());
		File exportFile = new File(exportDir, "exportToFileTest.xml");
		new ExportToFileJob(exportFile).setWhereUris(FIRST_URI, SECOND_URI).run(client);

		String exportedXml = new String(FileCopyUtils.copyToByteArray(exportFile));
		logger.info("Exported XML: " + exportedXml);
		assertTrue(exportedXml.contains("<test>" + FIRST_URI + "</test>"));
		assertTrue(exportedXml.contains("<test>" + SECOND_URI + "</test>"));
	}

	@Test
	public void configureWithProperties() throws Exception {
		Properties props = new Properties();
		props.setProperty("exportPath", "build/export-test/" + System.currentTimeMillis() + "/file.xml");
		props.setProperty("fileHeader", "<results>");
		props.setProperty("fileFooter", "</results>");
		props.setProperty("recordPrefix", "<record>");
		props.setProperty("recordSuffix", "</record>");
		props.setProperty("batchSize", "52");
		props.setProperty("consistentSnapshot", "true");
		props.setProperty("jobName", "my-job");
		props.setProperty("logBatches", "true");
		props.setProperty("logBatchesWithLogger", "true");
		props.setProperty("threadCount", "7");
		props.setProperty("whereUris", FIRST_URI + "," + SECOND_URI);

		ExportToFileJob job = new ExportToFileJob();
		assertTrue(job.configureJob(props).isEmpty());
		job.run(client);

		String exportedXml = new String(FileCopyUtils.copyToByteArray(job.getExportFile()));
		logger.info("Exported XML: " + exportedXml);
		assertTrue(exportedXml.startsWith("<results>\n<record><test>"));
		assertTrue(exportedXml.endsWith("</test></record></results>"));
	}
}
