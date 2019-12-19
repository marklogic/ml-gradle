package com.marklogic.client.ext.file;

import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

public class CollectionsFileDocumentFileProcessorTest extends Assert {

	private CollectionsFileDocumentFileProcessor processor = new CollectionsFileDocumentFileProcessor();

	@Test
	public void wildcard() {
		File testDir = new File("src/test/resources/process-files/wildcard-test");

		DocumentFile file = new DocumentFile("/test.json", new File(testDir, "test.json"));
		processor.processDocumentFile(file);
		assertTrue(file.getDocumentMetadata().getCollections().contains("json-data"));
		assertFalse(file.getDocumentMetadata().getCollections().contains("xml-data"));
		assertTrue(file.getDocumentMetadata().getCollections().contains("global"));

		file = new DocumentFile("/test.xml", new File(testDir, "test.xml"));
		processor.processDocumentFile(file);
		assertFalse(file.getDocumentMetadata().getCollections().contains("json-data"));
		assertTrue(file.getDocumentMetadata().getCollections().contains("xml-data"));
		assertTrue(file.getDocumentMetadata().getCollections().contains("global"));
	}

	@Test
	public void replaceTokens() {
		File testDir = new File("src/test/resources/process-files/token-test");

		DefaultTokenReplacer tokenReplacer = new DefaultTokenReplacer();
		Properties props = new Properties();
		props.setProperty("%%someCollection%%", "this-was-replaced");
		tokenReplacer.setProperties(props);
		processor.setTokenReplacer(tokenReplacer);

		DocumentFile file = new DocumentFile("/test.json", new File(testDir, "test.json"));
		processor.processDocumentFile(file);
		assertTrue(file.getDocumentMetadata().getCollections().contains("this-was-replaced"));
		assertFalse(file.getDocumentMetadata().getCollections().contains("%%someCollection%%"));
	}
}
