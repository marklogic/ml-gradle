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
package com.marklogic.client.ext.file;

import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionsFileDocumentFileProcessorTest {

	private CollectionsFileDocumentFileProcessor processor = new CollectionsFileDocumentFileProcessor();

	@Test
	public void wildcard() throws IOException {
		File testDir = new File("src/test/resources/process-files/wildcard-test");

		File collectionsPropertiesFile = new File(testDir, processor.getPropertiesFilename());
		processor.loadProperties(collectionsPropertiesFile);

		DocumentFile file = new DocumentFile("/test.json", new File(testDir, "test.json"));
		processor.processDocumentFile(file);
		DocumentMetadataHandle.DocumentCollections collections = file.getDocumentMetadata().getCollections();
		assertEquals(3, collections.size(), "test.json should have the *, test.json, and *.json rules applied for it.");
		assertTrue(collections.contains("json-data"));
		assertTrue(collections.contains("json-data-wildcard"));
		assertFalse(collections.contains("xml-data"));
		assertTrue(collections.contains("global"));

		file = new DocumentFile("/test-1.json", new File(testDir, "test-1.json"));
		processor.processDocumentFile(file);
		collections = file.getDocumentMetadata().getCollections();
		assertEquals(2, collections.size(), "test-1.json should have the * and *.json rules applied for it.");
		assertFalse(collections.contains("json-data"));
		assertTrue(collections.contains("json-data-wildcard"));
		assertFalse(collections.contains("xml-data"));
		assertTrue(collections.contains("global"));

		file = new DocumentFile("/test.xml", new File(testDir, "test.xml"));
		processor.processDocumentFile(file);
		collections = file.getDocumentMetadata().getCollections();
		assertEquals(2, collections.size(), "test.xml should have the * and test.xml rules applied for it.");
		assertFalse(collections.contains("json-data"));
		assertFalse(collections.contains("json-data-wildcard"));
		assertTrue(collections.contains("xml-data"));
		assertTrue(collections.contains("global"));
	}

	@Test
	public void replaceTokens() throws IOException {
		File testDir = new File("src/test/resources/process-files/token-test");

		File collectionsPropertiesFile = new File(testDir, processor.getPropertiesFilename());
		processor.loadProperties(collectionsPropertiesFile);

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
